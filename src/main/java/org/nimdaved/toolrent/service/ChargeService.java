package org.nimdaved.toolrent.service;

import java.util.List;
import java.util.Optional;
import org.nimdaved.toolrent.domain.Charge;
import org.nimdaved.toolrent.repository.ChargeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.nimdaved.toolrent.domain.Charge}.
 */
@Service
@Transactional
public class ChargeService {

    private static final Logger LOG = LoggerFactory.getLogger(ChargeService.class);

    private final ChargeRepository chargeRepository;

    public ChargeService(ChargeRepository chargeRepository) {
        this.chargeRepository = chargeRepository;
    }

    /**
     * Save a charge.
     *
     * @param charge the entity to save.
     * @return the persisted entity.
     */
    public Charge save(Charge charge) {
        LOG.debug("Request to save Charge : {}", charge);
        return chargeRepository.save(charge);
    }

    /**
     * Update a charge.
     *
     * @param charge the entity to save.
     * @return the persisted entity.
     */
    public Charge update(Charge charge) {
        LOG.debug("Request to update Charge : {}", charge);
        return chargeRepository.save(charge);
    }

    /**
     * Partially update a charge.
     *
     * @param charge the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Charge> partialUpdate(Charge charge) {
        LOG.debug("Request to partially update Charge : {}", charge);

        return chargeRepository
            .findById(charge.getId())
            .map(existingCharge -> {
                if (charge.getToolType() != null) {
                    existingCharge.setToolType(charge.getToolType());
                }
                if (charge.getDailyCharge() != null) {
                    existingCharge.setDailyCharge(charge.getDailyCharge());
                }
                if (charge.getWeekdayCharge() != null) {
                    existingCharge.setWeekdayCharge(charge.getWeekdayCharge());
                }
                if (charge.getWeekendCharge() != null) {
                    existingCharge.setWeekendCharge(charge.getWeekendCharge());
                }
                if (charge.getHolidayCharge() != null) {
                    existingCharge.setHolidayCharge(charge.getHolidayCharge());
                }

                return existingCharge;
            })
            .map(chargeRepository::save);
    }

    /**
     * Get all the charges.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Charge> findAll() {
        LOG.debug("Request to get all Charges");
        return chargeRepository.findAll();
    }

    /**
     * Get one charge by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Charge> findOne(Long id) {
        LOG.debug("Request to get Charge : {}", id);
        return chargeRepository.findById(id);
    }

    /**
     * Delete the charge by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Charge : {}", id);
        chargeRepository.deleteById(id);
    }
}
