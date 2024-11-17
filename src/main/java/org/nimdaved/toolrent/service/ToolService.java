package org.nimdaved.toolrent.service;

import java.util.List;
import java.util.Optional;
import org.nimdaved.toolrent.domain.Tool;
import org.nimdaved.toolrent.repository.ToolInventoryRepository;
import org.nimdaved.toolrent.repository.ToolRepository;
import org.nimdaved.toolrent.service.dto.ToolRentalEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link org.nimdaved.toolrent.domain.Tool}.
 */
@Service
@Transactional
public class ToolService {

    private static final Logger LOG = LoggerFactory.getLogger(ToolService.class);

    private final ToolRepository toolRepository;
    private final ToolInventoryRepository toolInventoryRepository;

    public ToolService(ToolRepository toolRepository, ToolInventoryRepository toolInventoryRepository) {
        this.toolRepository = toolRepository;
        this.toolInventoryRepository = toolInventoryRepository;
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
        return toolRepository
            .findByToolCodeWithStockCountInInventory(toolCode)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find available tool with code: " + toolCode));
    }

    @EventListener
    @Transactional
    public void onRentalCreated(ToolRentalEvents.RentalCreated event) {
        var inventory = event.rental().getTool().getToolInventory();
        var onHold = inventory.getOnHoldCount() + 1;
        inventory.setOnHoldCount(onHold);
        toolInventoryRepository.save(inventory);
    }

    @EventListener
    @Transactional
    public void onRentalCanceled(ToolRentalEvents.RentalCanceled event) {
        var inventory = event.rental().getTool().getToolInventory();
        var onHold = inventory.getOnHoldCount() - 1;
        inventory.setOnHoldCount(onHold);
        toolInventoryRepository.save(inventory);
    }

    @EventListener
    @Transactional
    public void onRentalCheckedOut(ToolRentalEvents.RentalCheckedOut event) {
        var inventory = event.rental().getTool().getToolInventory();
        var onHold = inventory.getOnHoldCount() - 1;
        var checkedOut = inventory.getCheckedOutCount() + 1;
        inventory.setOnHoldCount(onHold);
        inventory.setCheckedOutCount(checkedOut);
        toolInventoryRepository.save(inventory);
    }

    @EventListener
    @Transactional
    public void onRentalCheckedIn(ToolRentalEvents.RentalCheckedIn event) {
        var inventory = event.rental().getTool().getToolInventory();
        var checkedOut = inventory.getCheckedOutCount() - 1;
        inventory.setCheckedOutCount(checkedOut);
        toolInventoryRepository.save(inventory);
    }
}
