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
