package org.nimdaved.toolrent.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.nimdaved.toolrent.domain.Charge;
import org.nimdaved.toolrent.repository.ChargeRepository;
import org.nimdaved.toolrent.service.ChargeService;
import org.nimdaved.toolrent.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.nimdaved.toolrent.domain.Charge}.
 */
@RestController
@RequestMapping("/api/charges")
public class ChargeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChargeResource.class);

    private static final String ENTITY_NAME = "charge";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChargeService chargeService;

    private final ChargeRepository chargeRepository;

    public ChargeResource(ChargeService chargeService, ChargeRepository chargeRepository) {
        this.chargeService = chargeService;
        this.chargeRepository = chargeRepository;
    }

    /**
     * {@code POST  /charges} : Create a new charge.
     *
     * @param charge the charge to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new charge, or with status {@code 400 (Bad Request)} if the charge has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Charge> createCharge(@Valid @RequestBody Charge charge) throws URISyntaxException {
        LOG.debug("REST request to save Charge : {}", charge);
        if (charge.getId() != null) {
            throw new BadRequestAlertException("A new charge cannot already have an ID", ENTITY_NAME, "idexists");
        }
        charge = chargeService.save(charge);
        return ResponseEntity.created(new URI("/api/charges/" + charge.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, charge.getId().toString()))
            .body(charge);
    }

    /**
     * {@code PUT  /charges/:id} : Updates an existing charge.
     *
     * @param id the id of the charge to save.
     * @param charge the charge to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated charge,
     * or with status {@code 400 (Bad Request)} if the charge is not valid,
     * or with status {@code 500 (Internal Server Error)} if the charge couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Charge> updateCharge(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Charge charge
    ) throws URISyntaxException {
        LOG.debug("REST request to update Charge : {}, {}", id, charge);
        if (charge.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, charge.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chargeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        charge = chargeService.update(charge);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, charge.getId().toString()))
            .body(charge);
    }

    /**
     * {@code PATCH  /charges/:id} : Partial updates given fields of an existing charge, field will ignore if it is null
     *
     * @param id the id of the charge to save.
     * @param charge the charge to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated charge,
     * or with status {@code 400 (Bad Request)} if the charge is not valid,
     * or with status {@code 404 (Not Found)} if the charge is not found,
     * or with status {@code 500 (Internal Server Error)} if the charge couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Charge> partialUpdateCharge(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Charge charge
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Charge partially : {}, {}", id, charge);
        if (charge.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, charge.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chargeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Charge> result = chargeService.partialUpdate(charge);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, charge.getId().toString())
        );
    }

    /**
     * {@code GET  /charges} : get all the charges.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of charges in body.
     */
    @GetMapping("")
    public List<Charge> getAllCharges() {
        LOG.debug("REST request to get all Charges");
        return chargeService.findAll();
    }

    /**
     * {@code GET  /charges/:id} : get the "id" charge.
     *
     * @param id the id of the charge to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the charge, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Charge> getCharge(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Charge : {}", id);
        Optional<Charge> charge = chargeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(charge);
    }

    /**
     * {@code DELETE  /charges/:id} : delete the "id" charge.
     *
     * @param id the id of the charge to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharge(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Charge : {}", id);
        chargeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
