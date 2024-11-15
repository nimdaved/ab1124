package org.nimdaved.toolrent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.nimdaved.toolrent.domain.ToolInventoryAsserts.*;
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
import org.nimdaved.toolrent.domain.ToolInventory;
import org.nimdaved.toolrent.repository.ToolInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ToolInventoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ToolInventoryResourceIT {

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final Integer DEFAULT_STOCK_COUNT = 0;
    private static final Integer UPDATED_STOCK_COUNT = 1;

    private static final Integer DEFAULT_CHECKED_OUT_COUNT = 0;
    private static final Integer UPDATED_CHECKED_OUT_COUNT = 1;

    private static final Integer DEFAULT_ON_HOLD_COUNT = 0;
    private static final Integer UPDATED_ON_HOLD_COUNT = 1;

    private static final String ENTITY_API_URL = "/api/tool-inventories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ToolInventoryRepository toolInventoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restToolInventoryMockMvc;

    private ToolInventory toolInventory;

    private ToolInventory insertedToolInventory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ToolInventory createEntity() {
        return new ToolInventory()
            .location(DEFAULT_LOCATION)
            .stockCount(DEFAULT_STOCK_COUNT)
            .checkedOutCount(DEFAULT_CHECKED_OUT_COUNT)
            .onHoldCount(DEFAULT_ON_HOLD_COUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ToolInventory createUpdatedEntity() {
        return new ToolInventory()
            .location(UPDATED_LOCATION)
            .stockCount(UPDATED_STOCK_COUNT)
            .checkedOutCount(UPDATED_CHECKED_OUT_COUNT)
            .onHoldCount(UPDATED_ON_HOLD_COUNT);
    }

    @BeforeEach
    public void initTest() {
        toolInventory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedToolInventory != null) {
            toolInventoryRepository.delete(insertedToolInventory);
            insertedToolInventory = null;
        }
    }

    @Test
    @Transactional
    void createToolInventory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ToolInventory
        var returnedToolInventory = om.readValue(
            restToolInventoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(toolInventory)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ToolInventory.class
        );

        // Validate the ToolInventory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertToolInventoryUpdatableFieldsEquals(returnedToolInventory, getPersistedToolInventory(returnedToolInventory));

        insertedToolInventory = returnedToolInventory;
    }

    @Test
    @Transactional
    void createToolInventoryWithExistingId() throws Exception {
        // Create the ToolInventory with an existing ID
        toolInventory.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restToolInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(toolInventory)))
            .andExpect(status().isBadRequest());

        // Validate the ToolInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLocationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        toolInventory.setLocation(null);

        // Create the ToolInventory, which fails.

        restToolInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(toolInventory)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStockCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        toolInventory.setStockCount(null);

        // Create the ToolInventory, which fails.

        restToolInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(toolInventory)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCheckedOutCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        toolInventory.setCheckedOutCount(null);

        // Create the ToolInventory, which fails.

        restToolInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(toolInventory)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOnHoldCountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        toolInventory.setOnHoldCount(null);

        // Create the ToolInventory, which fails.

        restToolInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(toolInventory)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllToolInventories() throws Exception {
        // Initialize the database
        insertedToolInventory = toolInventoryRepository.saveAndFlush(toolInventory);

        // Get all the toolInventoryList
        restToolInventoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toolInventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].stockCount").value(hasItem(DEFAULT_STOCK_COUNT)))
            .andExpect(jsonPath("$.[*].checkedOutCount").value(hasItem(DEFAULT_CHECKED_OUT_COUNT)))
            .andExpect(jsonPath("$.[*].onHoldCount").value(hasItem(DEFAULT_ON_HOLD_COUNT)));
    }

    @Test
    @Transactional
    void getToolInventory() throws Exception {
        // Initialize the database
        insertedToolInventory = toolInventoryRepository.saveAndFlush(toolInventory);

        // Get the toolInventory
        restToolInventoryMockMvc
            .perform(get(ENTITY_API_URL_ID, toolInventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(toolInventory.getId().intValue()))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.stockCount").value(DEFAULT_STOCK_COUNT))
            .andExpect(jsonPath("$.checkedOutCount").value(DEFAULT_CHECKED_OUT_COUNT))
            .andExpect(jsonPath("$.onHoldCount").value(DEFAULT_ON_HOLD_COUNT));
    }

    @Test
    @Transactional
    void getNonExistingToolInventory() throws Exception {
        // Get the toolInventory
        restToolInventoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingToolInventory() throws Exception {
        // Initialize the database
        insertedToolInventory = toolInventoryRepository.saveAndFlush(toolInventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the toolInventory
        ToolInventory updatedToolInventory = toolInventoryRepository.findById(toolInventory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedToolInventory are not directly saved in db
        em.detach(updatedToolInventory);
        updatedToolInventory
            .location(UPDATED_LOCATION)
            .stockCount(UPDATED_STOCK_COUNT)
            .checkedOutCount(UPDATED_CHECKED_OUT_COUNT)
            .onHoldCount(UPDATED_ON_HOLD_COUNT);

        restToolInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedToolInventory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedToolInventory))
            )
            .andExpect(status().isOk());

        // Validate the ToolInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedToolInventoryToMatchAllProperties(updatedToolInventory);
    }

    @Test
    @Transactional
    void putNonExistingToolInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        toolInventory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToolInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, toolInventory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(toolInventory))
            )
            .andExpect(status().isBadRequest());

        // Validate the ToolInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchToolInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        toolInventory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(toolInventory))
            )
            .andExpect(status().isBadRequest());

        // Validate the ToolInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamToolInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        toolInventory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolInventoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(toolInventory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ToolInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateToolInventoryWithPatch() throws Exception {
        // Initialize the database
        insertedToolInventory = toolInventoryRepository.saveAndFlush(toolInventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the toolInventory using partial update
        ToolInventory partialUpdatedToolInventory = new ToolInventory();
        partialUpdatedToolInventory.setId(toolInventory.getId());

        restToolInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedToolInventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedToolInventory))
            )
            .andExpect(status().isOk());

        // Validate the ToolInventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertToolInventoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedToolInventory, toolInventory),
            getPersistedToolInventory(toolInventory)
        );
    }

    @Test
    @Transactional
    void fullUpdateToolInventoryWithPatch() throws Exception {
        // Initialize the database
        insertedToolInventory = toolInventoryRepository.saveAndFlush(toolInventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the toolInventory using partial update
        ToolInventory partialUpdatedToolInventory = new ToolInventory();
        partialUpdatedToolInventory.setId(toolInventory.getId());

        partialUpdatedToolInventory
            .location(UPDATED_LOCATION)
            .stockCount(UPDATED_STOCK_COUNT)
            .checkedOutCount(UPDATED_CHECKED_OUT_COUNT)
            .onHoldCount(UPDATED_ON_HOLD_COUNT);

        restToolInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedToolInventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedToolInventory))
            )
            .andExpect(status().isOk());

        // Validate the ToolInventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertToolInventoryUpdatableFieldsEquals(partialUpdatedToolInventory, getPersistedToolInventory(partialUpdatedToolInventory));
    }

    @Test
    @Transactional
    void patchNonExistingToolInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        toolInventory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToolInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, toolInventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(toolInventory))
            )
            .andExpect(status().isBadRequest());

        // Validate the ToolInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchToolInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        toolInventory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(toolInventory))
            )
            .andExpect(status().isBadRequest());

        // Validate the ToolInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamToolInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        toolInventory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolInventoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(toolInventory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ToolInventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteToolInventory() throws Exception {
        // Initialize the database
        insertedToolInventory = toolInventoryRepository.saveAndFlush(toolInventory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the toolInventory
        restToolInventoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, toolInventory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return toolInventoryRepository.count();
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

    protected ToolInventory getPersistedToolInventory(ToolInventory toolInventory) {
        return toolInventoryRepository.findById(toolInventory.getId()).orElseThrow();
    }

    protected void assertPersistedToolInventoryToMatchAllProperties(ToolInventory expectedToolInventory) {
        assertToolInventoryAllPropertiesEquals(expectedToolInventory, getPersistedToolInventory(expectedToolInventory));
    }

    protected void assertPersistedToolInventoryToMatchUpdatableProperties(ToolInventory expectedToolInventory) {
        assertToolInventoryAllUpdatablePropertiesEquals(expectedToolInventory, getPersistedToolInventory(expectedToolInventory));
    }
}
