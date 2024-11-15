package org.nimdaved.toolrent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.nimdaved.toolrent.domain.ChargeAsserts.*;
import static org.nimdaved.toolrent.web.rest.TestUtil.createUpdateProxyForBean;
import static org.nimdaved.toolrent.web.rest.TestUtil.sameNumber;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.IntegrationTest;
import org.nimdaved.toolrent.domain.Charge;
import org.nimdaved.toolrent.domain.enumeration.ToolType;
import org.nimdaved.toolrent.repository.ChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ChargeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChargeResourceIT {

    private static final ToolType DEFAULT_TOOL_TYPE = ToolType.LADDER;
    private static final ToolType UPDATED_TOOL_TYPE = ToolType.CHINSAW;

    private static final BigDecimal DEFAULT_DAILY_CHARGE = new BigDecimal(0);
    private static final BigDecimal UPDATED_DAILY_CHARGE = new BigDecimal(1);

    private static final Boolean DEFAULT_WEEKDAY_CHARGE = false;
    private static final Boolean UPDATED_WEEKDAY_CHARGE = true;

    private static final Boolean DEFAULT_WEEKEND_CHARGE = false;
    private static final Boolean UPDATED_WEEKEND_CHARGE = true;

    private static final Boolean DEFAULT_HOLIDAY_CHARGE = false;
    private static final Boolean UPDATED_HOLIDAY_CHARGE = true;

    private static final String ENTITY_API_URL = "/api/charges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChargeMockMvc;

    private Charge charge;

    private Charge insertedCharge;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Charge createEntity() {
        return new Charge()
            .toolType(DEFAULT_TOOL_TYPE)
            .dailyCharge(DEFAULT_DAILY_CHARGE)
            .weekdayCharge(DEFAULT_WEEKDAY_CHARGE)
            .weekendCharge(DEFAULT_WEEKEND_CHARGE)
            .holidayCharge(DEFAULT_HOLIDAY_CHARGE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Charge createUpdatedEntity() {
        return new Charge()
            .toolType(UPDATED_TOOL_TYPE)
            .dailyCharge(UPDATED_DAILY_CHARGE)
            .weekdayCharge(UPDATED_WEEKDAY_CHARGE)
            .weekendCharge(UPDATED_WEEKEND_CHARGE)
            .holidayCharge(UPDATED_HOLIDAY_CHARGE);
    }

    @BeforeEach
    public void initTest() {
        charge = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCharge != null) {
            chargeRepository.delete(insertedCharge);
            insertedCharge = null;
        }
    }

    @Test
    @Transactional
    void createCharge() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Charge
        var returnedCharge = om.readValue(
            restChargeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(charge)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Charge.class
        );

        // Validate the Charge in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertChargeUpdatableFieldsEquals(returnedCharge, getPersistedCharge(returnedCharge));

        insertedCharge = returnedCharge;
    }

    @Test
    @Transactional
    void createChargeWithExistingId() throws Exception {
        // Create the Charge with an existing ID
        charge.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(charge)))
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDailyChargeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        charge.setDailyCharge(null);

        // Create the Charge, which fails.

        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(charge)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWeekdayChargeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        charge.setWeekdayCharge(null);

        // Create the Charge, which fails.

        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(charge)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWeekendChargeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        charge.setWeekendCharge(null);

        // Create the Charge, which fails.

        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(charge)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHolidayChargeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        charge.setHolidayCharge(null);

        // Create the Charge, which fails.

        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(charge)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCharges() throws Exception {
        // Initialize the database
        insertedCharge = chargeRepository.saveAndFlush(charge);

        // Get all the chargeList
        restChargeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(charge.getId().intValue())))
            .andExpect(jsonPath("$.[*].toolType").value(hasItem(DEFAULT_TOOL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].dailyCharge").value(hasItem(sameNumber(DEFAULT_DAILY_CHARGE))))
            .andExpect(jsonPath("$.[*].weekdayCharge").value(hasItem(DEFAULT_WEEKDAY_CHARGE.booleanValue())))
            .andExpect(jsonPath("$.[*].weekendCharge").value(hasItem(DEFAULT_WEEKEND_CHARGE.booleanValue())))
            .andExpect(jsonPath("$.[*].holidayCharge").value(hasItem(DEFAULT_HOLIDAY_CHARGE.booleanValue())));
    }

    @Test
    @Transactional
    void getCharge() throws Exception {
        // Initialize the database
        insertedCharge = chargeRepository.saveAndFlush(charge);

        // Get the charge
        restChargeMockMvc
            .perform(get(ENTITY_API_URL_ID, charge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(charge.getId().intValue()))
            .andExpect(jsonPath("$.toolType").value(DEFAULT_TOOL_TYPE.toString()))
            .andExpect(jsonPath("$.dailyCharge").value(sameNumber(DEFAULT_DAILY_CHARGE)))
            .andExpect(jsonPath("$.weekdayCharge").value(DEFAULT_WEEKDAY_CHARGE.booleanValue()))
            .andExpect(jsonPath("$.weekendCharge").value(DEFAULT_WEEKEND_CHARGE.booleanValue()))
            .andExpect(jsonPath("$.holidayCharge").value(DEFAULT_HOLIDAY_CHARGE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingCharge() throws Exception {
        // Get the charge
        restChargeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCharge() throws Exception {
        // Initialize the database
        insertedCharge = chargeRepository.saveAndFlush(charge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the charge
        Charge updatedCharge = chargeRepository.findById(charge.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCharge are not directly saved in db
        em.detach(updatedCharge);
        updatedCharge
            .toolType(UPDATED_TOOL_TYPE)
            .dailyCharge(UPDATED_DAILY_CHARGE)
            .weekdayCharge(UPDATED_WEEKDAY_CHARGE)
            .weekendCharge(UPDATED_WEEKEND_CHARGE)
            .holidayCharge(UPDATED_HOLIDAY_CHARGE);

        restChargeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCharge.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCharge))
            )
            .andExpect(status().isOk());

        // Validate the Charge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChargeToMatchAllProperties(updatedCharge);
    }

    @Test
    @Transactional
    void putNonExistingCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        charge.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(put(ENTITY_API_URL_ID, charge.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(charge)))
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        charge.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(charge))
            )
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        charge.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(charge)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Charge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChargeWithPatch() throws Exception {
        // Initialize the database
        insertedCharge = chargeRepository.saveAndFlush(charge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the charge using partial update
        Charge partialUpdatedCharge = new Charge();
        partialUpdatedCharge.setId(charge.getId());

        partialUpdatedCharge
            .dailyCharge(UPDATED_DAILY_CHARGE)
            .weekdayCharge(UPDATED_WEEKDAY_CHARGE)
            .weekendCharge(UPDATED_WEEKEND_CHARGE)
            .holidayCharge(UPDATED_HOLIDAY_CHARGE);

        restChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCharge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCharge))
            )
            .andExpect(status().isOk());

        // Validate the Charge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChargeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCharge, charge), getPersistedCharge(charge));
    }

    @Test
    @Transactional
    void fullUpdateChargeWithPatch() throws Exception {
        // Initialize the database
        insertedCharge = chargeRepository.saveAndFlush(charge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the charge using partial update
        Charge partialUpdatedCharge = new Charge();
        partialUpdatedCharge.setId(charge.getId());

        partialUpdatedCharge
            .toolType(UPDATED_TOOL_TYPE)
            .dailyCharge(UPDATED_DAILY_CHARGE)
            .weekdayCharge(UPDATED_WEEKDAY_CHARGE)
            .weekendCharge(UPDATED_WEEKEND_CHARGE)
            .holidayCharge(UPDATED_HOLIDAY_CHARGE);

        restChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCharge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCharge))
            )
            .andExpect(status().isOk());

        // Validate the Charge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChargeUpdatableFieldsEquals(partialUpdatedCharge, getPersistedCharge(partialUpdatedCharge));
    }

    @Test
    @Transactional
    void patchNonExistingCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        charge.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, charge.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(charge))
            )
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        charge.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(charge))
            )
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        charge.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(charge)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Charge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCharge() throws Exception {
        // Initialize the database
        insertedCharge = chargeRepository.saveAndFlush(charge);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the charge
        restChargeMockMvc
            .perform(delete(ENTITY_API_URL_ID, charge.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chargeRepository.count();
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

    protected Charge getPersistedCharge(Charge charge) {
        return chargeRepository.findById(charge.getId()).orElseThrow();
    }

    protected void assertPersistedChargeToMatchAllProperties(Charge expectedCharge) {
        assertChargeAllPropertiesEquals(expectedCharge, getPersistedCharge(expectedCharge));
    }

    protected void assertPersistedChargeToMatchUpdatableProperties(Charge expectedCharge) {
        assertChargeAllUpdatablePropertiesEquals(expectedCharge, getPersistedCharge(expectedCharge));
    }
}
