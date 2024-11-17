package org.nimdaved.toolrent.service;

import org.nimdaved.toolrent.domain.Rental;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentGeneratorService {

    /**
     * Creates legal document
     *
     * @param rental
     * @return
     */
    @Transactional
    public String createRentalAgreement(Rental rental) {
        StringBuilder result = new StringBuilder("Rental Agreement\n\n ")
            .append("Tool code: ")
            .append(rental.getTool().getCode())
            .append("\n")
            .append("Tool type: ")
            .append(rental.getTool().getToolType())
            .append("\n")
            .append("Tool brand: ")
            .append(rental.getTool().getBrand())
            .append("\n")
            .append("Rental days: ")
            .append(rental.getDayCount())
            .append("\n")
            .append("Checkout date: ")
            .append(rental.getCheckOutDate())
            .append("\n")
            .append("Due date: ")
            .append(rental.getCheckOutDate().plusDays(rental.getDayCount()))
            .append("\n")
            .append("Daily rental charge: ")
            .append(rental.getDailyCharges())
            .append("\n")
            .append("Charge days: ")
            .append(rental.getChargedDaysCount())
            .append("\n")
            .append("Pre-discount charge: ")
            .append(rental.getChargeAmount())
            .append("\n")
            .append("Discount percent: ")
            .append(rental.getDiscountPercent())
            .append("\n")
            .append("Discount amount: ")
            .append(rental.getDiscountAmount())
            .append("\n")
            .append("Final charge: ")
            .append(rental.getFinalCharge())
            .append("\n");

        return result.toString();
    }
}
