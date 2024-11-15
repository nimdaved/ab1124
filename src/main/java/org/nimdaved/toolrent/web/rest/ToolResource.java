package org.nimdaved.toolrent.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.nimdaved.toolrent.domain.Tool;
import org.nimdaved.toolrent.repository.ToolRepository;
import org.nimdaved.toolrent.service.ToolService;
import org.nimdaved.toolrent.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.nimdaved.toolrent.domain.Tool}.
 */
@RestController
@RequestMapping("/api/tools")
public class ToolResource {

    private static final Logger LOG = LoggerFactory.getLogger(ToolResource.class);

    private static final String ENTITY_NAME = "tool";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ToolService toolService;

    private final ToolRepository toolRepository;

    public ToolResource(ToolService toolService, ToolRepository toolRepository) {
        this.toolService = toolService;
        this.toolRepository = toolRepository;
    }

    /**
     * {@code POST  /tools} : Create a new tool.
     *
     * @param tool the tool to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tool, or with status {@code 400 (Bad Request)} if the tool has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Tool> createTool(@Valid @RequestBody Tool tool) throws URISyntaxException {
        LOG.debug("REST request to save Tool : {}", tool);
        if (toolRepository.existsById(tool.getCode())) {
            throw new BadRequestAlertException("tool already exists", ENTITY_NAME, "idexists");
        }
        tool = toolService.save(tool);
        return ResponseEntity.created(new URI("/api/tools/" + tool.getCode()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tool.getCode()))
            .body(tool);
    }

    /**
     * {@code PUT  /tools/:code} : Updates an existing tool.
     *
     * @param code the id of the tool to save.
     * @param tool the tool to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tool,
     * or with status {@code 400 (Bad Request)} if the tool is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tool couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{code}")
    public ResponseEntity<Tool> updateTool(
        @PathVariable(value = "code", required = false) final String code,
        @Valid @RequestBody Tool tool
    ) throws URISyntaxException {
        LOG.debug("REST request to update Tool : {}, {}", code, tool);
        if (tool.getCode() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(code, tool.getCode())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!toolRepository.existsById(code)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tool = toolService.update(tool);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tool.getCode()))
            .body(tool);
    }

    /**
     * {@code PATCH  /tools/:code} : Partial updates given fields of an existing tool, field will ignore if it is null
     *
     * @param code the id of the tool to save.
     * @param tool the tool to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tool,
     * or with status {@code 400 (Bad Request)} if the tool is not valid,
     * or with status {@code 404 (Not Found)} if the tool is not found,
     * or with status {@code 500 (Internal Server Error)} if the tool couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{code}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tool> partialUpdateTool(
        @PathVariable(value = "code", required = false) final String code,
        @NotNull @RequestBody Tool tool
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Tool partially : {}, {}", code, tool);
        if (tool.getCode() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(code, tool.getCode())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!toolRepository.existsById(code)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tool> result = toolService.partialUpdate(tool);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tool.getCode()));
    }

    /**
     * {@code GET  /tools} : get all the tools.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tools in body.
     */
    @GetMapping("")
    public List<Tool> getAllTools() {
        LOG.debug("REST request to get all Tools");
        return toolService.findAll();
    }

    /**
     * {@code GET  /tools/:id} : get the "id" tool.
     *
     * @param id the id of the tool to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tool, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tool> getTool(@PathVariable("id") String id) {
        LOG.debug("REST request to get Tool : {}", id);
        Optional<Tool> tool = toolService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tool);
    }

    /**
     * {@code DELETE  /tools/:id} : delete the "id" tool.
     *
     * @param id the id of the tool to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTool(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Tool : {}", id);
        toolService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
