package org.nimdaved.toolrent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.nimdaved.toolrent.domain.RentalAgreementAsserts.*;
import static org.nimdaved.toolrent.web.rest.TestUtil.createUpdateProxyForBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.IntegrationTest;
import org.nimdaved.toolrent.domain.RentalAgreement;
import org.nimdaved.toolrent.domain.enumeration.RentalAgreementStatus;
import org.nimdaved.toolrent.repository.RentalAgreementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RentalAgreementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RentalAgreementResourceIT {

    private static final String DEFAULT_AGREEMENT = "AAAAAAAAAA";
    private static final String UPDATED_AGREEMENT = "BBBBBBBBBB";

    private static final RentalAgreementStatus DEFAULT_STATUS = RentalAgreementStatus.PENDING;
    private static final RentalAgreementStatus UPDATED_STATUS = RentalAgreementStatus.ACCEPTED;

    private static final String ENTITY_API_URL = "/api/rental-agreements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RentalAgreementRepository rentalAgreementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRentalAgreementMockMvc;

    private RentalAgreement rentalAgreement;

    private RentalAgreement insertedRentalAgreement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RentalAgreement createEntity() {
        return new RentalAgreement().agreement(DEFAULT_AGREEMENT).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RentalAgreement createUpdatedEntity() {
        return new RentalAgreement().agreement(UPDATED_AGREEMENT).status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        rentalAgreement = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRentalAgreement != null) {
            rentalAgreementRepository.delete(insertedRentalAgreement);
            insertedRentalAgreement = null;
        }
    }

    @Test
    @Transactional
    void createRentalAgreement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RentalAgreement
        var returnedRentalAgreement = om.readValue(
            restRentalAgreementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rentalAgreement)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RentalAgreement.class
        );

        // Validate the RentalAgreement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRentalAgreementUpdatableFieldsEquals(returnedRentalAgreement, getPersistedRentalAgreement(returnedRentalAgreement));

        insertedRentalAgreement = returnedRentalAgreement;
    }

    @Test
    @Transactional
    void createRentalAgreementWithExistingId() throws Exception {
        // Create the RentalAgreement with an existing ID
        rentalAgreement.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRentalAgreementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rentalAgreement)))
            .andExpect(status().isBadRequest());

        // Validate the RentalAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rentalAgreement.setStatus(null);

        // Create the RentalAgreement, which fails.

        restRentalAgreementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rentalAgreement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRentalAgreements() throws Exception {
        // Initialize the database
        insertedRentalAgreement = rentalAgreementRepository.saveAndFlush(rentalAgreement);

        // Get all the rentalAgreementList
        restRentalAgreementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rentalAgreement.getId().intValue())))
            .andExpect(jsonPath("$.[*].agreement").value(hasItem(DEFAULT_AGREEMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getRentalAgreement() throws Exception {
        // Initialize the database
        insertedRentalAgreement = rentalAgreementRepository.saveAndFlush(rentalAgreement);

        // Get the rentalAgreement
        restRentalAgreementMockMvc
            .perform(get(ENTITY_API_URL_ID, rentalAgreement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rentalAgreement.getId().intValue()))
            .andExpect(jsonPath("$.agreement").value(DEFAULT_AGREEMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRentalAgreement() throws Exception {
        // Get the rentalAgreement
        restRentalAgreementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRentalAgreement() throws Exception {
        // Initialize the database
        insertedRentalAgreement = rentalAgreementRepository.saveAndFlush(rentalAgreement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rentalAgreement
        RentalAgreement updatedRentalAgreement = rentalAgreementRepository.findById(rentalAgreement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRentalAgreement are not directly saved in db
        em.detach(updatedRentalAgreement);
        updatedRentalAgreement.agreement(UPDATED_AGREEMENT).status(UPDATED_STATUS);

        restRentalAgreementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRentalAgreement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRentalAgreement))
            )
            .andExpect(status().isOk());

        // Validate the RentalAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRentalAgreementToMatchAllProperties(updatedRentalAgreement);
    }

    @Test
    @Transactional
    void putNonExistingRentalAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalAgreement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRentalAgreementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rentalAgreement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rentalAgreement))
            )
            .andExpect(status().isBadRequest());

        // Validate the RentalAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRentalAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalAgreement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRentalAgreementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rentalAgreement))
            )
            .andExpect(status().isBadRequest());

        // Validate the RentalAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRentalAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalAgreement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRentalAgreementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rentalAgreement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RentalAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRentalAgreementWithPatch() throws Exception {
        // Initialize the database
        insertedRentalAgreement = rentalAgreementRepository.saveAndFlush(rentalAgreement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rentalAgreement using partial update
        RentalAgreement partialUpdatedRentalAgreement = new RentalAgreement();
        partialUpdatedRentalAgreement.setId(rentalAgreement.getId());

        partialUpdatedRentalAgreement.agreement(UPDATED_AGREEMENT);

        restRentalAgreementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRentalAgreement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRentalAgreement))
            )
            .andExpect(status().isOk());

        // Validate the RentalAgreement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRentalAgreementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRentalAgreement, rentalAgreement),
            getPersistedRentalAgreement(rentalAgreement)
        );
    }

    @Test
    @Transactional
    void fullUpdateRentalAgreementWithPatch() throws Exception {
        // Initialize the database
        insertedRentalAgreement = rentalAgreementRepository.saveAndFlush(rentalAgreement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rentalAgreement using partial update
        RentalAgreement partialUpdatedRentalAgreement = new RentalAgreement();
        partialUpdatedRentalAgreement.setId(rentalAgreement.getId());

        partialUpdatedRentalAgreement.agreement(UPDATED_AGREEMENT).status(UPDATED_STATUS);

        restRentalAgreementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRentalAgreement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRentalAgreement))
            )
            .andExpect(status().isOk());

        // Validate the RentalAgreement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRentalAgreementUpdatableFieldsEquals(
            partialUpdatedRentalAgreement,
            getPersistedRentalAgreement(partialUpdatedRentalAgreement)
        );
    }

    @Test
    @Transactional
    void patchNonExistingRentalAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalAgreement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRentalAgreementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rentalAgreement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rentalAgreement))
            )
            .andExpect(status().isBadRequest());

        // Validate the RentalAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRentalAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalAgreement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRentalAgreementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rentalAgreement))
            )
            .andExpect(status().isBadRequest());

        // Validate the RentalAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRentalAgreement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalAgreement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRentalAgreementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(rentalAgreement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RentalAgreement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRentalAgreement() throws Exception {
        // Initialize the database
        insertedRentalAgreement = rentalAgreementRepository.saveAndFlush(rentalAgreement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rentalAgreement
        restRentalAgreementMockMvc
            .perform(delete(ENTITY_API_URL_ID, rentalAgreement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return rentalAgreementRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected RentalAgreement getPersistedRentalAgreement(RentalAgreement rentalAgreement) {
        return rentalAgreementRepository.findById(rentalAgreement.getId()).orElseThrow();
    }

    protected void assertPersistedRentalAgreementToMatchAllProperties(RentalAgreement expectedRentalAgreement) {
        assertRentalAgreementAllPropertiesEquals(expectedRentalAgreement, getPersistedRentalAgreement(expectedRentalAgreement));
    }

    protected void assertPersistedRentalAgreementToMatchUpdatableProperties(RentalAgreement expectedRentalAgreement) {
        assertRentalAgreementAllUpdatablePropertiesEquals(expectedRentalAgreement, getPersistedRentalAgreement(expectedRentalAgreement));
    }
}
