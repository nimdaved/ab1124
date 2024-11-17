package org.nimdaved.toolrent.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.nimdaved.toolrent.domain.Rental;
import org.nimdaved.toolrent.repository.RentalRepository;
import org.nimdaved.toolrent.service.RentalService;
import org.nimdaved.toolrent.service.dto.RentalRequest;
import org.nimdaved.toolrent.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.nimdaved.toolrent.domain.Rental}.
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalResource {

    private static final Logger LOG = LoggerFactory.getLogger(RentalResource.class);

    private static final String ENTITY_NAME = "rental";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RentalService rentalService;

    private final RentalRepository rentalRepository;

    public RentalResource(RentalService rentalService, RentalRepository rentalRepository) {
        this.rentalService = rentalService;
        this.rentalRepository = rentalRepository;
    }

    /**
     * {@code POST  /rentals} : Create a new rental.
     *
     * @param rental the rental to create.
     * @return the {@link ResponseEntity} redirecting , or with status {@code 400 (Bad Request)} if the rental has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<?> createRental(@Valid @RequestBody RentalRequest rental) throws URISyntaxException {
        LOG.debug("REST request to save Rental : {}", rental);

        var created = rentalService.create(rental);
        //Redirect to the rental agreement page to allow for acceptance
        URI location = URI.create("/api/rental-agreements/rental/" + created.getId());
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(location).build();
    }

    /**
     * {@code GET  /rentals} : get all the rentals.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rentals in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Rental>> getAllRentals(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        LOG.debug("REST request to get a page of Rentals");
        Page<Rental> page = rentalService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rentals/:id} : get the "id" rental.
     *
     * @param id the id of the rental to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rental, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRental(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Rental : {}", id);
        Optional<Rental> rental = rentalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rental);
    }

    /**
     * {@code DELETE  /rentals/:id} : delete the "id" rental.
     *
     * @param id the id of the rental to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Rental : {}", id);
        rentalService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
