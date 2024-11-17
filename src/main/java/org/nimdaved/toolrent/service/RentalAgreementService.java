package org.nimdaved.toolrent.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.function.Function;
import org.nimdaved.toolrent.domain.RentalAgreement;
import org.nimdaved.toolrent.domain.enumeration.RentalAgreementStatus;
import org.nimdaved.toolrent.repository.RentalAgreementRepository;
import org.nimdaved.toolrent.service.dto.ToolRentalEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Service Implementation for managing {@link org.nimdaved.toolrent.domain.RentalAgreement}.
 */
@Service
@Transactional
public class RentalAgreementService {

    private static final Logger LOG = LoggerFactory.getLogger(RentalAgreementService.class);

    private final RentalAgreementRepository rentalAgreementRepository;
    private final DocumentGeneratorService documentGeneratorService;

    private final ApplicationEventPublisher eventPublisher;

    public RentalAgreementService(
        RentalAgreementRepository rentalAgreementRepository,
        DocumentGeneratorService documentGeneratorService,
        ApplicationEventPublisher eventPublisher
    ) {
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.documentGeneratorService = documentGeneratorService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Save a rentalAgreement.
     *
     * @param rentalAgreement the entity to save.
     * @return the persisted entity.
     */
    public RentalAgreement save(RentalAgreement rentalAgreement) {
        LOG.debug("Request to save RentalAgreement : {}", rentalAgreement);
        return rentalAgreementRepository.save(rentalAgreement);
    }

    /**
     * Get all the rentalAgreements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RentalAgreement> findAll(Pageable pageable) {
        LOG.debug("Request to get all RentalAgreements");
        return rentalAgreementRepository.findAll(pageable);
    }

    /**
     * Get one rentalAgreement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RentalAgreement> findOne(Long id) {
        LOG.debug("Request to get RentalAgreement : {}", id);
        return rentalAgreementRepository.findById(id);
    }

    /**
     * Delete the rentalAgreement by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RentalAgreement : {}", id);
        rentalAgreementRepository.deleteById(id);
    }

    /**
     * Accept rental agreement and notify downstream services
     *
     * @param rentalAgreementId
     */
    public void accept(Long rentalAgreementId) {
        changeStatus(rentalAgreementId, RentalAgreementStatus.ACCEPTED, ToolRentalEvents.RentalAgreementAccepted::new);
    }

    /**
     * Reject rental agreement and notify downstream services
     *
     * @param rentalAgreementId
     */
    public void reject(Long rentalAgreementId) {
        changeStatus(rentalAgreementId, RentalAgreementStatus.REJECTED, ToolRentalEvents.RentalAgreementRejected::new);
    }

    private <R> void changeStatus(Long rentalAgreementId, RentalAgreementStatus status, Function<RentalAgreement, R> function) {
        findOne(rentalAgreementId).ifPresentOrElse(
            rentalAgreement -> {
                rentalAgreement.setStatus(status);
                var saved = save(rentalAgreement);
                rentalAgreement.getRental();
                eventPublisher.publishEvent(function.apply(saved));
            },
            () -> {
                throw new EntityNotFoundException("Could not find RentalAgreement with id " + rentalAgreementId);
            }
        );
    }

    /**
     * Processes ToolRentalEvents.RentalCreated event
     *
     * @param event
     */
    @TransactionalEventListener
    @Transactional
    public void onEvent(ToolRentalEvents.RentalCreated event) {
        LOG.debug("Received event : {}", event);

        RentalAgreement rentalAgreement = new RentalAgreement()
            .rental(event.rental())
            .agreement(documentGeneratorService.createRentalAgreement(event.rental()));

        save(rentalAgreement);
    }
}
