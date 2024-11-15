package org.nimdaved.toolrent.service.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.nimdaved.toolrent.domain.Customer;

public class RentalRequest {

    @NotNull
    private String toolCode;

    @NotNull
    private LocalDate checkOutDate;

    @NotNull
    @Min(value = 1)
    @Max(value = 365)
    private Integer dayCount;

    @NotNull
    @Min(value = 0)
    @Max(value = 100)
    private Integer discountPercent = 0;

    private String location;
    private Customer customer;

    public @NotNull String getToolCode() {
        return toolCode;
    }

    public void setToolCode(@NotNull String toolCode) {
        this.toolCode = toolCode;
    }

    public @NotNull LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(@NotNull LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public @NotNull @Min(value = 1) @Max(value = 365) Integer getDayCount() {
        return dayCount;
    }

    public void setDayCount(@NotNull @Min(value = 1) @Max(value = 365) Integer dayCount) {
        this.dayCount = dayCount;
    }

    public @NotNull @Min(value = 0) @Max(value = 100) Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(@NotNull @Min(value = 0) @Max(value = 100) Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return (
            "RentalRequest{" +
            "toolCode='" +
            toolCode +
            '\'' +
            ", checkOutDate=" +
            checkOutDate +
            ", dayCount=" +
            dayCount +
            ", discountPercent=" +
            discountPercent +
            ", location='" +
            location +
            '\'' +
            '}'
        );
    }
}
