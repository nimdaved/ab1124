package org.nimdaved.toolrent.service;

import java.util.Optional;
import org.nimdaved.toolrent.domain.RentalAgreement;
import org.nimdaved.toolrent.repository.RentalAgreementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.nimdaved.toolrent.domain.RentalAgreement}.
 */
@Service
@Transactional
public class RentalAgreementService {

    private static final Logger LOG = LoggerFactory.getLogger(RentalAgreementService.class);

    private final RentalAgreementRepository rentalAgreementRepository;

    public RentalAgreementService(RentalAgreementRepository rentalAgreementRepository) {
        this.rentalAgreementRepository = rentalAgreementRepository;
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
     * Update a rentalAgreement.
     *
     * @param rentalAgreement the entity to save.
     * @return the persisted entity.
     */
    public RentalAgreement update(RentalAgreement rentalAgreement) {
        LOG.debug("Request to update RentalAgreement : {}", rentalAgreement);
        return rentalAgreementRepository.save(rentalAgreement);
    }

    /**
     * Partially update a rentalAgreement.
     *
     * @param rentalAgreement the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RentalAgreement> partialUpdate(RentalAgreement rentalAgreement) {
        LOG.debug("Request to partially update RentalAgreement : {}", rentalAgreement);

        return rentalAgreementRepository
            .findById(rentalAgreement.getId())
            .map(existingRentalAgreement -> {
                if (rentalAgreement.getAgreement() != null) {
                    existingRentalAgreement.setAgreement(rentalAgreement.getAgreement());
                }
                if (rentalAgreement.getStatus() != null) {
                    existingRentalAgreement.setStatus(rentalAgreement.getStatus());
                }

                return existingRentalAgreement;
            })
            .map(rentalAgreementRepository::save);
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
}
