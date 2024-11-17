package org.nimdaved.toolrent.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;
import org.nimdaved.toolrent.domain.Customer;
import org.nimdaved.toolrent.domain.Rental;
import org.nimdaved.toolrent.domain.Tool;
import org.nimdaved.toolrent.domain.enumeration.RentalStatus;
import org.nimdaved.toolrent.repository.CustomerRepository;
import org.nimdaved.toolrent.repository.RentalRepository;
import org.nimdaved.toolrent.service.dto.RentalRequest;
import org.nimdaved.toolrent.service.dto.ToolRentalEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Example;
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
    private final ToolService toolService;
    private final ChargeService chargeService;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RentalService(
        RentalRepository rentalRepository,
        ToolService toolService,
        ChargeService chargeService,
        CustomerRepository customerRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.rentalRepository = rentalRepository;
        this.toolService = toolService;
        this.chargeService = chargeService;
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    public Rental create(RentalRequest rentalRequest) {
        var rental = new Rental();
        rental.setDiscountPercent(rentalRequest.getDiscountPercent());
        rental.setCheckOutDate(rentalRequest.getCheckOutDate());
        rental.setDayCount(rentalRequest.getDayCount());

        rental.setCustomer(findCustomer(Optional.ofNullable(rentalRequest.getCustomer())));
        rental.setTool(getAvailableTool(rentalRequest.getToolCode()));
        var charges = calculateCharges(rental.getTool(), rental.getCheckOutDate(), rental.getDayCount());
        rental.setChargeAmount(charges.chargedAmount());

        var saved = rentalRepository.save(rental);
        saved.setChargedDaysCount(charges.chargedDays());
        saved.setDailyCharges(charges.dailyCharges());

        eventPublisher.publishEvent(new ToolRentalEvents.RentalCreated(saved));

        return saved;
    }

    public Rental checkout(Rental rental) {
        return changeStatus(rental, RentalStatus.CHECKED_OUT, ToolRentalEvents.RentalCheckedOut::new);
    }

    public Rental checkin(Rental rental) {
        return changeStatus(rental, RentalStatus.CHECKED_IN, ToolRentalEvents.RentalCheckedIn::new);
    }

    public Rental cancel(Rental rental) {
        return changeStatus(rental, RentalStatus.CANCELLED, ToolRentalEvents.RentalCanceled::new);
    }

    @EventListener
    @Transactional
    public void onEvent(ToolRentalEvents.RentalAgreementAccepted event) {
        checkout(event.rentalAgreement().getRental());
    }

    @EventListener
    @Transactional
    public void onEvent(ToolRentalEvents.RentalAgreementRejected event) {
        cancel(event.rentalAgreement().getRental());
    }

    private <T> Rental changeStatus(Rental rental, RentalStatus status, Function<Rental, T> function) {
        rental.setStatus(status);
        var saved = rentalRepository.save(rental);
        eventPublisher.publishEvent(function.apply(saved));

        return saved;
    }

    private Customer findCustomer(Optional<Customer> customer) {
        return customer.flatMap(c -> customerRepository.findOne(Example.of(c))).orElseGet(() -> customerRepository.findDefaultCustomer());
    }

    private ChargeService.Charges calculateCharges(Tool tool, LocalDate checkOutDate, Integer dayCount) {
        return chargeService.calculateCharges(tool, checkOutDate, dayCount);
    }

    private Tool getAvailableTool(String toolCode) {
        return toolService.getAvailableTool(toolCode);
    }

    /**
     * Save a rental.
     *
     * @param rental the entity to save.
     * @return the persisted entity.
     */
    private Rental save(Rental rental) {
        LOG.debug("Request to save Rental : {}", rental);
        var saved = rentalRepository.save(rental);
        return saved;
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
