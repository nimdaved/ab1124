package org.nimdaved.toolrent.service.dto;

import org.nimdaved.toolrent.domain.Rental;
import org.nimdaved.toolrent.domain.RentalAgreement;

public final class ToolRentalEvents {

    private ToolRentalEvents() {}

    public record RentalCreated(Rental rental) {}

    public record RentalCheckedOut(Rental rental) {}

    public record RentalCheckedIn(Rental rental) {}

    public record RentalCanceled(Rental rental) {}

    public record RentalAgreementAccepted(RentalAgreement rentalAgreement) {}

    public record RentalAgreementRejected(RentalAgreement rentalAgreement) {}
}
