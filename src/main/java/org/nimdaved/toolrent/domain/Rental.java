package org.nimdaved.toolrent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.nimdaved.toolrent.domain.enumeration.RentalStatus;

/**
 * A Rental.
 */
@Entity
@Table(name = "rental")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Rental implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Use LocalDate (vs. ZonedDateTime) as timezone is not strictly required
     */
    @Schema(description = "Use LocalDate (vs. ZonedDateTime) as timezone is not strictly required", required = true)
    @NotNull
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @NotNull
    @Min(value = 1)
    @Max(value = 365)
    @Column(name = "day_count", nullable = false)
    private Integer dayCount;

    @NotNull
    @Min(value = 0)
    @Max(value = 100)
    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalStatus status;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "charge_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal chargeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "toolInventory" }, allowSetters = true)
    private Tool tool;

    @JsonIgnoreProperties(value = { "rental" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "rental")
    private RentalAgreement rentalAgreement;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Rental id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCheckOutDate() {
        return this.checkOutDate;
    }

    public Rental checkOutDate(LocalDate checkOutDate) {
        this.setCheckOutDate(checkOutDate);
        return this;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Integer getDayCount() {
        return this.dayCount;
    }

    public Rental dayCount(Integer dayCount) {
        this.setDayCount(dayCount);
        return this;
    }

    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }

    public Integer getDiscountPercent() {
        return this.discountPercent;
    }

    public Rental discountPercent(Integer discountPercent) {
        this.setDiscountPercent(discountPercent);
        return this;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public RentalStatus getStatus() {
        return this.status;
    }

    public Rental status(RentalStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public BigDecimal getChargeAmount() {
        return this.chargeAmount;
    }

    public Rental chargeAmount(BigDecimal chargeAmount) {
        this.setChargeAmount(chargeAmount);
        return this;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Rental customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Tool getTool() {
        return this.tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public Rental tool(Tool tool) {
        this.setTool(tool);
        return this;
    }

    public RentalAgreement getRentalAgreement() {
        return this.rentalAgreement;
    }

    public void setRentalAgreement(RentalAgreement rentalAgreement) {
        if (this.rentalAgreement != null) {
            this.rentalAgreement.setRental(null);
        }
        if (rentalAgreement != null) {
            rentalAgreement.setRental(this);
        }
        this.rentalAgreement = rentalAgreement;
    }

    public Rental rentalAgreement(RentalAgreement rentalAgreement) {
        this.setRentalAgreement(rentalAgreement);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rental)) {
            return false;
        }
        return getId() != null && getId().equals(((Rental) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Rental{" +
            "id=" + getId() +
            ", checkOutDate='" + getCheckOutDate() + "'" +
            ", dayCount=" + getDayCount() +
            ", discountPercent=" + getDiscountPercent() +
            ", status='" + getStatus() + "'" +
            ", chargeAmount=" + getChargeAmount() +
            "}";
    }
}
