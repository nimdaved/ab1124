package org.nimdaved.toolrent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.nimdaved.toolrent.domain.HolidayAsserts.*;
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
import org.nimdaved.toolrent.domain.Holiday;
import org.nimdaved.toolrent.domain.enumeration.HolidayType;
import org.nimdaved.toolrent.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link HolidayResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HolidayResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final HolidayType DEFAULT_HOLIDAY_TYPE = HolidayType.EXACT_DAY_OF_MONTH;
    private static final HolidayType UPDATED_HOLIDAY_TYPE = HolidayType.FIRST_DAY_OF_WEEK_IN_MONTH;

    private static final Integer DEFAULT_MONTH_NUMBER = 1;
    private static final Integer UPDATED_MONTH_NUMBER = 2;

    private static final Integer DEFAULT_DAY_NUMBER = 1;
    private static final Integer UPDATED_DAY_NUMBER = 2;

    private static final Boolean DEFAULT_OBSERVED_ON_CLOSEST_WEEKDAY = false;
    private static final Boolean UPDATED_OBSERVED_ON_CLOSEST_WEEKDAY = true;

    private static final String ENTITY_API_URL = "/api/holidays";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHolidayMockMvc;

    private Holiday holiday;

    private Holiday insertedHoliday;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Holiday createEntity() {
        return new Holiday()
            .name(DEFAULT_NAME)
            .holidayType(DEFAULT_HOLIDAY_TYPE)
            .monthNumber(DEFAULT_MONTH_NUMBER)
            .dayNumber(DEFAULT_DAY_NUMBER)
            .observedOnClosestWeekday(DEFAULT_OBSERVED_ON_CLOSEST_WEEKDAY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Holiday createUpdatedEntity() {
        return new Holiday()
            .name(UPDATED_NAME)
            .holidayType(UPDATED_HOLIDAY_TYPE)
            .monthNumber(UPDATED_MONTH_NUMBER)
            .dayNumber(UPDATED_DAY_NUMBER)
            .observedOnClosestWeekday(UPDATED_OBSERVED_ON_CLOSEST_WEEKDAY);
    }

    @BeforeEach
    public void initTest() {
        holiday = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedHoliday != null) {
            holidayRepository.delete(insertedHoliday);
            insertedHoliday = null;
        }
    }

    @Test
    @Transactional
    void createHoliday() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Holiday
        var returnedHoliday = om.readValue(
            restHolidayMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holiday)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Holiday.class
        );

        // Validate the Holiday in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertHolidayUpdatableFieldsEquals(returnedHoliday, getPersistedHoliday(returnedHoliday));

        insertedHoliday = returnedHoliday;
    }

    @Test
    @Transactional
    void createHolidayWithExistingId() throws Exception {
        // Create the Holiday with an existing ID
        holiday.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHolidayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holiday)))
            .andExpect(status().isBadRequest());

        // Validate the Holiday in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        holiday.setName(null);

        // Create the Holiday, which fails.

        restHolidayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holiday)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHolidayTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        holiday.setHolidayType(null);

        // Create the Holiday, which fails.

        restHolidayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holiday)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMonthNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        holiday.setMonthNumber(null);

        // Create the Holiday, which fails.

        restHolidayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holiday)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDayNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        holiday.setDayNumber(null);

        // Create the Holiday, which fails.

        restHolidayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holiday)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkObservedOnClosestWeekdayIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        holiday.setObservedOnClosestWeekday(null);

        // Create the Holiday, which fails.

        restHolidayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holiday)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHolidays() throws Exception {
        // Initialize the database
        insertedHoliday = holidayRepository.saveAndFlush(holiday);

        // Get all the holidayList
        restHolidayMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(holiday.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].holidayType").value(hasItem(DEFAULT_HOLIDAY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].monthNumber").value(hasItem(DEFAULT_MONTH_NUMBER)))
            .andExpect(jsonPath("$.[*].dayNumber").value(hasItem(DEFAULT_DAY_NUMBER)))
            .andExpect(jsonPath("$.[*].observedOnClosestWeekday").value(hasItem(DEFAULT_OBSERVED_ON_CLOSEST_WEEKDAY.booleanValue())));
    }

    @Test
    @Transactional
    void getHoliday() throws Exception {
        // Initialize the database
        insertedHoliday = holidayRepository.saveAndFlush(holiday);

        // Get the holiday
        restHolidayMockMvc
            .perform(get(ENTITY_API_URL_ID, holiday.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(holiday.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.holidayType").value(DEFAULT_HOLIDAY_TYPE.toString()))
            .andExpect(jsonPath("$.monthNumber").value(DEFAULT_MONTH_NUMBER))
            .andExpect(jsonPath("$.dayNumber").value(DEFAULT_DAY_NUMBER))
            .andExpect(jsonPath("$.observedOnClosestWeekday").value(DEFAULT_OBSERVED_ON_CLOSEST_WEEKDAY.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingHoliday() throws Exception {
        // Get the holiday
        restHolidayMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHoliday() throws Exception {
        // Initialize the database
        insertedHoliday = holidayRepository.saveAndFlush(holiday);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the holiday
        Holiday updatedHoliday = holidayRepository.findById(holiday.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHoliday are not directly saved in db
        em.detach(updatedHoliday);
        updatedHoliday
            .name(UPDATED_NAME)
            .holidayType(UPDATED_HOLIDAY_TYPE)
            .monthNumber(UPDATED_MONTH_NUMBER)
            .dayNumber(UPDATED_DAY_NUMBER)
            .observedOnClosestWeekday(UPDATED_OBSERVED_ON_CLOSEST_WEEKDAY);

        restHolidayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHoliday.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedHoliday))
            )
            .andExpect(status().isOk());

        // Validate the Holiday in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHolidayToMatchAllProperties(updatedHoliday);
    }

    @Test
    @Transactional
    void putNonExistingHoliday() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holiday.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHolidayMockMvc
            .perform(put(ENTITY_API_URL_ID, holiday.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holiday)))
            .andExpect(status().isBadRequest());

        // Validate the Holiday in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHoliday() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holiday.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHolidayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(holiday))
            )
            .andExpect(status().isBadRequest());

        // Validate the Holiday in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHoliday() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holiday.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHolidayMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holiday)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Holiday in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHolidayWithPatch() throws Exception {
        // Initialize the database
        insertedHoliday = holidayRepository.saveAndFlush(holiday);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the holiday using partial update
        Holiday partialUpdatedHoliday = new Holiday();
        partialUpdatedHoliday.setId(holiday.getId());

        partialUpdatedHoliday.name(UPDATED_NAME).monthNumber(UPDATED_MONTH_NUMBER).dayNumber(UPDATED_DAY_NUMBER);

        restHolidayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHoliday.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHoliday))
            )
            .andExpect(status().isOk());

        // Validate the Holiday in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHolidayUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedHoliday, holiday), getPersistedHoliday(holiday));
    }

    @Test
    @Transactional
    void fullUpdateHolidayWithPatch() throws Exception {
        // Initialize the database
        insertedHoliday = holidayRepository.saveAndFlush(holiday);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the holiday using partial update
        Holiday partialUpdatedHoliday = new Holiday();
        partialUpdatedHoliday.setId(holiday.getId());

        partialUpdatedHoliday
            .name(UPDATED_NAME)
            .holidayType(UPDATED_HOLIDAY_TYPE)
            .monthNumber(UPDATED_MONTH_NUMBER)
            .dayNumber(UPDATED_DAY_NUMBER)
            .observedOnClosestWeekday(UPDATED_OBSERVED_ON_CLOSEST_WEEKDAY);

        restHolidayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHoliday.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHoliday))
            )
            .andExpect(status().isOk());

        // Validate the Holiday in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHolidayUpdatableFieldsEquals(partialUpdatedHoliday, getPersistedHoliday(partialUpdatedHoliday));
    }

    @Test
    @Transactional
    void patchNonExistingHoliday() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holiday.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHolidayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, holiday.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(holiday))
            )
            .andExpect(status().isBadRequest());

        // Validate the Holiday in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHoliday() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holiday.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHolidayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(holiday))
            )
            .andExpect(status().isBadRequest());

        // Validate the Holiday in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHoliday() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        holiday.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHolidayMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(holiday)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Holiday in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHoliday() throws Exception {
        // Initialize the database
        insertedHoliday = holidayRepository.saveAndFlush(holiday);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the holiday
        restHolidayMockMvc
            .perform(delete(ENTITY_API_URL_ID, holiday.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return holidayRepository.count();
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

    protected Holiday getPersistedHoliday(Holiday holiday) {
        return holidayRepository.findById(holiday.getId()).orElseThrow();
    }

    protected void assertPersistedHolidayToMatchAllProperties(Holiday expectedHoliday) {
        assertHolidayAllPropertiesEquals(expectedHoliday, getPersistedHoliday(expectedHoliday));
    }

    protected void assertPersistedHolidayToMatchUpdatableProperties(Holiday expectedHoliday) {
        assertHolidayAllUpdatablePropertiesEquals(expectedHoliday, getPersistedHoliday(expectedHoliday));
    }
}
