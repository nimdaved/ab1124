package org.nimdaved.toolrent.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.nimdaved.toolrent.domain.ToolInventory;
import org.nimdaved.toolrent.repository.ToolInventoryRepository;
import org.nimdaved.toolrent.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.nimdaved.toolrent.domain.ToolInventory}.
 */
@RestController
@RequestMapping("/api/tool-inventories")
@Transactional
public class ToolInventoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(ToolInventoryResource.class);

    private static final String ENTITY_NAME = "toolInventory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ToolInventoryRepository toolInventoryRepository;

    public ToolInventoryResource(ToolInventoryRepository toolInventoryRepository) {
        this.toolInventoryRepository = toolInventoryRepository;
    }

    /**
     * {@code POST  /tool-inventories} : Create a new toolInventory.
     *
     * @param toolInventory the toolInventory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new toolInventory, or with status {@code 400 (Bad Request)} if the toolInventory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ToolInventory> createToolInventory(@Valid @RequestBody ToolInventory toolInventory) throws URISyntaxException {
        LOG.debug("REST request to save ToolInventory : {}", toolInventory);
        if (toolInventory.getId() != null) {
            throw new BadRequestAlertException("A new toolInventory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        toolInventory = toolInventoryRepository.save(toolInventory);
        return ResponseEntity.created(new URI("/api/tool-inventories/" + toolInventory.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, toolInventory.getId().toString()))
            .body(toolInventory);
    }

    /**
     * {@code PUT  /tool-inventories/:id} : Updates an existing toolInventory.
     *
     * @param id the id of the toolInventory to save.
     * @param toolInventory the toolInventory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated toolInventory,
     * or with status {@code 400 (Bad Request)} if the toolInventory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the toolInventory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ToolInventory> updateToolInventory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ToolInventory toolInventory
    ) throws URISyntaxException {
        LOG.debug("REST request to update ToolInventory : {}, {}", id, toolInventory);
        if (toolInventory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, toolInventory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!toolInventoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        toolInventory = toolInventoryRepository.save(toolInventory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, toolInventory.getId().toString()))
            .body(toolInventory);
    }

    /**
     * {@code PATCH  /tool-inventories/:id} : Partial updates given fields of an existing toolInventory, field will ignore if it is null
     *
     * @param id the id of the toolInventory to save.
     * @param toolInventory the toolInventory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated toolInventory,
     * or with status {@code 400 (Bad Request)} if the toolInventory is not valid,
     * or with status {@code 404 (Not Found)} if the toolInventory is not found,
     * or with status {@code 500 (Internal Server Error)} if the toolInventory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ToolInventory> partialUpdateToolInventory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ToolInventory toolInventory
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ToolInventory partially : {}, {}", id, toolInventory);
        if (toolInventory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, toolInventory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!toolInventoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ToolInventory> result = toolInventoryRepository
            .findById(toolInventory.getId())
            .map(existingToolInventory -> {
                if (toolInventory.getLocation() != null) {
                    existingToolInventory.setLocation(toolInventory.getLocation());
                }
                if (toolInventory.getStockCount() != null) {
                    existingToolInventory.setStockCount(toolInventory.getStockCount());
                }
                if (toolInventory.getCheckedOutCount() != null) {
                    existingToolInventory.setCheckedOutCount(toolInventory.getCheckedOutCount());
                }
                if (toolInventory.getOnHoldCount() != null) {
                    existingToolInventory.setOnHoldCount(toolInventory.getOnHoldCount());
                }

                return existingToolInventory;
            })
            .map(toolInventoryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, toolInventory.getId().toString())
        );
    }

    /**
     * {@code GET  /tool-inventories} : get all the toolInventories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of toolInventories in body.
     */
    @GetMapping("")
    public List<ToolInventory> getAllToolInventories() {
        LOG.debug("REST request to get all ToolInventories");
        return toolInventoryRepository.findAll();
    }

    /**
     * {@code GET  /tool-inventories/:id} : get the "id" toolInventory.
     *
     * @param id the id of the toolInventory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the toolInventory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ToolInventory> getToolInventory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ToolInventory : {}", id);
        Optional<ToolInventory> toolInventory = toolInventoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(toolInventory);
    }

    /**
     * {@code DELETE  /tool-inventories/:id} : delete the "id" toolInventory.
     *
     * @param id the id of the toolInventory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToolInventory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ToolInventory : {}", id);
        toolInventoryRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
