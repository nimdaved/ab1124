package org.nimdaved.toolrent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.nimdaved.toolrent.web.rest.TestUtil.sameNumber;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.IntegrationTest;
import org.nimdaved.toolrent.domain.Rental;
import org.nimdaved.toolrent.domain.Tool;
import org.nimdaved.toolrent.domain.enumeration.RentalStatus;
import org.nimdaved.toolrent.repository.RentalRepository;
import org.nimdaved.toolrent.repository.ToolRepository;
import org.nimdaved.toolrent.service.dto.RentalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RentalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RentalResourceIT {

    private static final LocalDate DEFAULT_CHECK_OUT_DATE = LocalDate.ofEpochDay(0L);

    private static final Integer DEFAULT_DAY_COUNT = 1;

    private static final Integer DEFAULT_DISCOUNT_PERCENT = 0;

    private static final RentalStatus DEFAULT_STATUS = RentalStatus.CREATED;

    private static final BigDecimal DEFAULT_CHARGE_AMOUNT = new BigDecimal(0);

    private static final String ENTITY_API_URL = "/api/rentals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Tool tool = ToolResourceIT.createEntity();

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRentalMockMvc;

    @Autowired
    private ToolRepository toolRepository;

    private Rental rental;

    private Rental insertedRental;

    private RentalRequest rentalRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rental createEntity() {
        return new Rental()
            .checkOutDate(DEFAULT_CHECK_OUT_DATE)
            .dayCount(DEFAULT_DAY_COUNT)
            .discountPercent(DEFAULT_DISCOUNT_PERCENT)
            .status(DEFAULT_STATUS)
            .chargeAmount(DEFAULT_CHARGE_AMOUNT);
    }

    public static RentalRequest createRentalRequest() {
        var rentalRequest = new RentalRequest();
        rentalRequest.setCheckOutDate(DEFAULT_CHECK_OUT_DATE);
        rentalRequest.setDiscountPercent(DEFAULT_DISCOUNT_PERCENT);
        rentalRequest.setCheckOutDate(DEFAULT_CHECK_OUT_DATE);
        rentalRequest.setDayCount(DEFAULT_DAY_COUNT);
        rentalRequest.setToolCode(tool.getId());
        return rentalRequest;
    }

    @BeforeEach
    public void initTest() {
        rental = createEntity();
        rentalRequest = createRentalRequest();
        toolRepository.save(tool);
    }

    @AfterEach
    public void cleanup() {
        if (insertedRental != null) {
            rentalRepository.delete(insertedRental);
            insertedRental = null;
        }
        toolRepository.delete(tool);
    }

    @Test
    @Transactional
    void checkCheckOutDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rental.setCheckOutDate(null);

        // Create the Rental, which fails.

        restRentalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rental)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDayCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rental.setDayCount(null);

        // Create the Rental, which fails.

        restRentalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rental)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDiscountPercentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rental.setDiscountPercent(null);

        // Create the Rental, which fails.

        restRentalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(rentalRequestBytes()))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRentals() throws Exception {
        // Initialize the database
        insertedRental = rentalRepository.saveAndFlush(rental);

        // Get all the rentalList
        restRentalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rental.getId().intValue())))
            .andExpect(jsonPath("$.[*].checkOutDate").value(hasItem(DEFAULT_CHECK_OUT_DATE.toString())))
            .andExpect(jsonPath("$.[*].dayCount").value(hasItem(DEFAULT_DAY_COUNT)))
            .andExpect(jsonPath("$.[*].discountPercent").value(hasItem(DEFAULT_DISCOUNT_PERCENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].chargeAmount").value(hasItem(sameNumber(DEFAULT_CHARGE_AMOUNT))));
    }

    @Test
    @Transactional
    void getRental() throws Exception {
        // Initialize the database
        insertedRental = rentalRepository.saveAndFlush(rental);

        // Get the rental
        restRentalMockMvc
            .perform(get(ENTITY_API_URL_ID, rental.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rental.getId().intValue()))
            .andExpect(jsonPath("$.checkOutDate").value(DEFAULT_CHECK_OUT_DATE.toString()))
            .andExpect(jsonPath("$.dayCount").value(DEFAULT_DAY_COUNT))
            .andExpect(jsonPath("$.discountPercent").value(DEFAULT_DISCOUNT_PERCENT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.chargeAmount").value(sameNumber(DEFAULT_CHARGE_AMOUNT)));
    }

    @Test
    @Transactional
    void getNonExistingRental() throws Exception {
        // Get the rental
        restRentalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteRental() throws Exception {
        // Initialize the database
        insertedRental = rentalRepository.saveAndFlush(rental);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rental
        restRentalMockMvc
            .perform(delete(ENTITY_API_URL_ID, rental.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return rentalRepository.count();
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    byte[] rentalRequestBytes() throws Exception {
        return om.writeValueAsBytes(rentalRequest);
    }
}
