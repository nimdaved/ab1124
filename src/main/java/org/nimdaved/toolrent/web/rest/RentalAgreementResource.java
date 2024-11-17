package org.nimdaved.toolrent.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.nimdaved.toolrent.domain.RentalAgreement;
import org.nimdaved.toolrent.repository.RentalAgreementRepository;
import org.nimdaved.toolrent.service.RentalAgreementService;
import org.nimdaved.toolrent.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.nimdaved.toolrent.domain.RentalAgreement}.
 */
@RestController
@RequestMapping("/api/rental-agreements")
public class RentalAgreementResource {

    private static final Logger LOG = LoggerFactory.getLogger(RentalAgreementResource.class);

    private static final String ENTITY_NAME = "rentalAgreement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RentalAgreementService rentalAgreementService;

    private final RentalAgreementRepository rentalAgreementRepository;

    public RentalAgreementResource(RentalAgreementService rentalAgreementService, RentalAgreementRepository rentalAgreementRepository) {
        this.rentalAgreementService = rentalAgreementService;
        this.rentalAgreementRepository = rentalAgreementRepository;
    }

    /**
     * {@code POST  /rental-agreements} : Create a new rentalAgreement.
     *
     * @param rentalAgreement the rentalAgreement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rentalAgreement, or with status {@code 400 (Bad Request)} if the rentalAgreement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RentalAgreement> createRentalAgreement(@Valid @RequestBody RentalAgreement rentalAgreement)
        throws URISyntaxException {
        LOG.debug("REST request to save RentalAgreement : {}", rentalAgreement);
        if (rentalAgreement.getId() != null) {
            throw new BadRequestAlertException("A new rentalAgreement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        rentalAgreement = rentalAgreementService.save(rentalAgreement);
        return ResponseEntity.created(new URI("/api/rental-agreements/" + rentalAgreement.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, rentalAgreement.getId().toString()))
            .body(rentalAgreement);
    }

    /**
     * Accept rental agreement by customer
     * @param id
     * @return the {@link ResponseEntity} with status {@code 202 (Accepted)},
     * or with status {@code 400 (Bad Request)} if the rentalAgreement is already accepted,
     * or with status {@code 500 (Internal Server Error)} if the rentalAgreement couldn't be updated.
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable(value = "id") final Long id) {
        LOG.debug("REST request to accept RentalAgreement : {}", id);
        rentalAgreementService.accept(id);
        return ResponseEntity.accepted().body(Map.of(id, "RentalAgreement accepted"));
    }

    /**
     * Reject rental agreement by customer
     * @param id
     * @return the {@link ResponseEntity} with status {@code 202 (Accepted)},
     * or with status {@code 400 (Bad Request)} if the rentalAgreement is already rejected,
     * or with status {@code 500 (Internal Server Error)} if the rentalAgreement couldn't be updated.
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable(value = "id") final Long id) {
        LOG.debug("REST request to reject RentalAgreement : {}", id);
        rentalAgreementService.reject(id);
        return ResponseEntity.accepted().body(Map.of(id, "RentalAgreement rejected"));
    }

    /**
     * {@code GET  /rental-agreements} : get all the rentalAgreements.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rentalAgreements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RentalAgreement>> getAllRentalAgreements(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of RentalAgreements");
        Page<RentalAgreement> page = rentalAgreementService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rental-agreements/:id} : get the "id" rentalAgreement.
     *
     * @param id the id of the rentalAgreement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rentalAgreement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RentalAgreement> getRentalAgreement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RentalAgreement : {}", id);
        Optional<RentalAgreement> rentalAgreement = rentalAgreementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rentalAgreement);
    }

    /**
     * {@code GET  /rental-agreements/rental/:id} : get the "id" rentalAgreement.
     *
     * @param id the id of the rentalAgreement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rentalAgreement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rental/{id}")
    public ResponseEntity<RentalAgreement> getRentalAgreementByRentalId(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RentalAgreement by rentalId : {}", id);
        Optional<RentalAgreement> rentalAgreement = rentalAgreementRepository.findByRentalId(id);
        return ResponseUtil.wrapOrNotFound(rentalAgreement);
    }

    /**
     * {@code DELETE  /rental-agreements/:id} : delete the "id" rentalAgreement.
     *
     * @param id the id of the rentalAgreement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRentalAgreement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RentalAgreement : {}", id);
        rentalAgreementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
