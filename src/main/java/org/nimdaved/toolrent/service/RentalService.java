package org.nimdaved.toolrent.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.nimdaved.toolrent.domain.Rental;
import org.nimdaved.toolrent.repository.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.nimdaved.toolrent.domain.Rental}.
 */
@Service
@Transactional
public class RentalService {

    private static final Logger LOG = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    /**
     * Save a rental.
     *
     * @param rental the entity to save.
     * @return the persisted entity.
     */
    public Rental save(Rental rental) {
        LOG.debug("Request to save Rental : {}", rental);
        return rentalRepository.save(rental);
    }

    /**
     * Update a rental.
     *
     * @param rental the entity to save.
     * @return the persisted entity.
     */
    public Rental update(Rental rental) {
        LOG.debug("Request to update Rental : {}", rental);
        return rentalRepository.save(rental);
    }

    /**
     * Partially update a rental.
     *
     * @param rental the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Rental> partialUpdate(Rental rental) {
        LOG.debug("Request to partially update Rental : {}", rental);

        return rentalRepository
            .findById(rental.getId())
            .map(existingRental -> {
                if (rental.getCheckOutDate() != null) {
                    existingRental.setCheckOutDate(rental.getCheckOutDate());
                }
                if (rental.getDayCount() != null) {
                    existingRental.setDayCount(rental.getDayCount());
                }
                if (rental.getDiscountPercent() != null) {
                    existingRental.setDiscountPercent(rental.getDiscountPercent());
                }
                if (rental.getStatus() != null) {
                    existingRental.setStatus(rental.getStatus());
                }
                if (rental.getChargeAmount() != null) {
                    existingRental.setChargeAmount(rental.getChargeAmount());
                }

                return existingRental;
            })
            .map(rentalRepository::save);
    }

    /**
     * Get all the rentals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Rental> findAll(Pageable pageable) {
        LOG.debug("Request to get all Rentals");
        return rentalRepository.findAll(pageable);
    }

    /**
     * Get one rental by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Rental> findOne(Long id) {
        LOG.debug("Request to get Rental : {}", id);
        return rentalRepository.findById(id);
    }

    /**
     * Delete the rental by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Rental : {}", id);
        rentalRepository.deleteById(id);
    }
}
