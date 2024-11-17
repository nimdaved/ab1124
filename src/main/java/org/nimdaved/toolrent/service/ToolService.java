package org.nimdaved.toolrent.service;

import java.util.List;
import java.util.Optional;
import org.nimdaved.toolrent.domain.Tool;
import org.nimdaved.toolrent.repository.ToolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.nimdaved.toolrent.domain.Tool}.
 */
@Service
@Transactional
public class ToolService {

    private static final Logger LOG = LoggerFactory.getLogger(ToolService.class);

    private final ToolRepository toolRepository;

    public ToolService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    /**
     * Save a tool.
     *
     * @param tool the entity to save.
     * @return the persisted entity.
     */
    public Tool save(Tool tool) {
        LOG.debug("Request to save Tool : {}", tool);
        return toolRepository.save(tool);
    }

    /**
     * Update a tool.
     *
     * @param tool the entity to save.
     * @return the persisted entity.
     */
    public Tool update(Tool tool) {
        LOG.debug("Request to update Tool : {}", tool);
        tool.setIsPersisted();
        return toolRepository.save(tool);
    }

    /**
     * Partially update a tool.
     *
     * @param tool the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Tool> partialUpdate(Tool tool) {
        LOG.debug("Request to partially update Tool : {}", tool);

        return toolRepository
            .findById(tool.getCode())
            .map(existingTool -> {
                if (tool.getToolType() != null) {
                    existingTool.setToolType(tool.getToolType());
                }
                if (tool.getBrand() != null) {
                    existingTool.setBrand(tool.getBrand());
                }

                return existingTool;
            })
            .map(toolRepository::save);
    }

    /**
     * Get all the tools.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Tool> findAll() {
        LOG.debug("Request to get all Tools");
        return toolRepository.findAll();
    }

    /**
     * Get one tool by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Tool> findOne(String id) {
        LOG.debug("Request to get Tool : {}", id);
        return toolRepository.findById(id);
    }

    /**
     * Delete the tool by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Tool : {}", id);
        toolRepository.deleteById(id);
    }

    public Tool getAvailableTool(String toolCode) {
        //TODO: implement me
        return null;
    }
}
