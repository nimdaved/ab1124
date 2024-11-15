package org.nimdaved.toolrent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.nimdaved.toolrent.domain.ToolAsserts.*;
import static org.nimdaved.toolrent.web.rest.TestUtil.createUpdateProxyForBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nimdaved.toolrent.IntegrationTest;
import org.nimdaved.toolrent.domain.Tool;
import org.nimdaved.toolrent.domain.enumeration.ToolType;
import org.nimdaved.toolrent.repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ToolResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ToolResourceIT {

    private static final ToolType DEFAULT_TOOL_TYPE = ToolType.LADDER;
    private static final ToolType UPDATED_TOOL_TYPE = ToolType.CHINSAW;

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tools";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{code}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restToolMockMvc;

    private Tool tool;

    private Tool insertedTool;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tool createEntity() {
        return new Tool().code(UUID.randomUUID().toString()).toolType(DEFAULT_TOOL_TYPE).brand(DEFAULT_BRAND);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tool createUpdatedEntity() {
        return new Tool().code(UUID.randomUUID().toString()).toolType(UPDATED_TOOL_TYPE).brand(UPDATED_BRAND);
    }

    @BeforeEach
    public void initTest() {
        tool = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTool != null) {
            toolRepository.delete(insertedTool);
            insertedTool = null;
        }
    }

    @Test
    @Transactional
    void createTool() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Tool
        var returnedTool = om.readValue(
            restToolMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tool)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Tool.class
        );

        // Validate the Tool in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertToolUpdatableFieldsEquals(returnedTool, getPersistedTool(returnedTool));

        insertedTool = returnedTool;
    }

    @Test
    @Transactional
    void createToolWithExistingId() throws Exception {
        // Create the Tool with an existing ID
        insertedTool = toolRepository.saveAndFlush(tool);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restToolMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tool)))
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkToolTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tool.setToolType(null);

        // Create the Tool, which fails.

        restToolMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tool)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBrandIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tool.setBrand(null);

        // Create the Tool, which fails.

        restToolMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tool)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTools() throws Exception {
        // Initialize the database
        tool.setCode(UUID.randomUUID().toString());
        insertedTool = toolRepository.saveAndFlush(tool);

        // Get all the toolList
        restToolMockMvc
            .perform(get(ENTITY_API_URL + "?sort=code,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].code").value(hasItem(tool.getCode())))
            .andExpect(jsonPath("$.[*].toolType").value(hasItem(DEFAULT_TOOL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND)));
    }

    @Test
    @Transactional
    void getTool() throws Exception {
        // Initialize the database
        tool.setCode(UUID.randomUUID().toString());
        insertedTool = toolRepository.saveAndFlush(tool);

        // Get the tool
        restToolMockMvc
            .perform(get(ENTITY_API_URL_ID, tool.getCode()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.code").value(tool.getCode()))
            .andExpect(jsonPath("$.toolType").value(DEFAULT_TOOL_TYPE.toString()))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND));
    }

    @Test
    @Transactional
    void getNonExistingTool() throws Exception {
        // Get the tool
        restToolMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTool() throws Exception {
        // Initialize the database
        tool.setCode(UUID.randomUUID().toString());
        insertedTool = toolRepository.saveAndFlush(tool);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tool
        Tool updatedTool = toolRepository.findById(tool.getCode()).orElseThrow();
        // Disconnect from session so that the updates on updatedTool are not directly saved in db
        em.detach(updatedTool);
        updatedTool.toolType(UPDATED_TOOL_TYPE).brand(UPDATED_BRAND);

        restToolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTool.getCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTool))
            )
            .andExpect(status().isOk());

        // Validate the Tool in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedToolToMatchAllProperties(updatedTool);
    }

    @Test
    @Transactional
    void putNonExistingTool() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tool.setCode(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(put(ENTITY_API_URL_ID, tool.getCode()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tool)))
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTool() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tool.setCode(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tool))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTool() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tool.setCode(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tool)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tool in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateToolWithPatch() throws Exception {
        // Initialize the database
        tool.setCode(UUID.randomUUID().toString());
        insertedTool = toolRepository.saveAndFlush(tool);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tool using partial update
        Tool partialUpdatedTool = new Tool();
        partialUpdatedTool.setCode(tool.getCode());

        partialUpdatedTool.toolType(UPDATED_TOOL_TYPE);

        restToolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTool.getCode())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTool))
            )
            .andExpect(status().isOk());

        // Validate the Tool in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertToolUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTool, tool), getPersistedTool(tool));
    }

    @Test
    @Transactional
    void fullUpdateToolWithPatch() throws Exception {
        // Initialize the database
        tool.setCode(UUID.randomUUID().toString());
        insertedTool = toolRepository.saveAndFlush(tool);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tool using partial update
        Tool partialUpdatedTool = new Tool();
        partialUpdatedTool.setCode(tool.getCode());

        partialUpdatedTool.toolType(UPDATED_TOOL_TYPE).brand(UPDATED_BRAND);

        restToolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTool.getCode())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTool))
            )
            .andExpect(status().isOk());

        // Validate the Tool in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertToolUpdatableFieldsEquals(partialUpdatedTool, getPersistedTool(partialUpdatedTool));
    }

    @Test
    @Transactional
    void patchNonExistingTool() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tool.setCode(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tool.getCode()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tool))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTool() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tool.setCode(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tool))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tool in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTool() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tool.setCode(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restToolMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tool)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tool in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTool() throws Exception {
        // Initialize the database
        tool.setCode(UUID.randomUUID().toString());
        insertedTool = toolRepository.saveAndFlush(tool);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tool
        restToolMockMvc
            .perform(delete(ENTITY_API_URL_ID, tool.getCode()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return toolRepository.count();
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

    protected Tool getPersistedTool(Tool tool) {
        return toolRepository.findById(tool.getCode()).orElseThrow();
    }

    protected void assertPersistedToolToMatchAllProperties(Tool expectedTool) {
        assertToolAllPropertiesEquals(expectedTool, getPersistedTool(expectedTool));
    }

    protected void assertPersistedToolToMatchUpdatableProperties(Tool expectedTool) {
        assertToolAllUpdatablePropertiesEquals(expectedTool, getPersistedTool(expectedTool));
    }
}
