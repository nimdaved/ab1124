package org.nimdaved.toolrent.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.nimdaved.toolrent.domain.Charge;
import org.nimdaved.toolrent.domain.Tool;
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
    private final CalendarService calendarService;

    public ChargeService(ChargeRepository chargeRepository, CalendarService calendarService) {
        this.chargeRepository = chargeRepository;
        this.calendarService = calendarService;
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

    /**
     * Calculate rental charges adjusted for holidays and weekends
     *
     * @param tool
     * @param checkOutDate
     * @param rentalDayCount
     * @return
     */
    public Charges calculateCharges(Tool tool, LocalDate checkOutDate, Integer rentalDayCount) {
        var counts = calendarService.getDayCounts(checkOutDate, rentalDayCount);

        Charge charge = chargeRepository
            .findOneByToolType(tool.getToolType())
            .orElseThrow(() -> new IllegalArgumentException("Charge not found for tool type: " + tool.getToolType()));

        var dailyCharge = charge.getDailyCharge();

        boolean chargedOnWeekends = charge.getWeekendCharge();
        boolean chargedOnHolidays = charge.getHolidayCharge();
        boolean chargedOnWeekdays = charge.getWeekdayCharge();

        int chargedDays =
            getChargedDays(chargedOnWeekdays, counts::weekdays) +
            getChargedDays(chargedOnWeekends, counts::weekendsNonHolidays) +
            getChargedDays(chargedOnHolidays, counts::holidaysNonWeekends) +
            getChargedDays(chargedOnWeekends && chargedOnHolidays, counts::weekendsAndHolidays);

        var chargedAmount = dailyCharge.multiply(BigDecimal.valueOf(chargedDays)).setScale(2, RoundingMode.HALF_UP);

        return new Charges(dailyCharge, chargedDays, chargedAmount);
    }

    private int getChargedDays(boolean chargeable, Supplier<Integer> dayCountSupplier) {
        return chargeable ? dayCountSupplier.get() : 0;
    }

    public record Charges(BigDecimal dailyCharges, int chargedDays, BigDecimal chargedAmount) {}
}
