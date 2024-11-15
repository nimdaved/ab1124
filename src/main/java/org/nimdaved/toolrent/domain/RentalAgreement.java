package org.nimdaved.toolrent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.nimdaved.toolrent.domain.enumeration.RentalAgreementStatus;

/**
 * A RentalAgreement.
 */
@Entity
@Table(name = "rental_agreement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RentalAgreement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "agreement", nullable = false)
    private String agreement;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalAgreementStatus status;

    @JsonIgnoreProperties(value = { "customer", "tool", "rentalAgreement" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Rental rental;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RentalAgreement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgreement() {
        return this.agreement;
    }

    public RentalAgreement agreement(String agreement) {
        this.setAgreement(agreement);
        return this;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public RentalAgreementStatus getStatus() {
        return this.status;
    }

    public RentalAgreement status(RentalAgreementStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RentalAgreementStatus status) {
        this.status = status;
    }

    public Rental getRental() {
        return this.rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public RentalAgreement rental(Rental rental) {
        this.setRental(rental);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RentalAgreement)) {
            return false;
        }
        return getId() != null && getId().equals(((RentalAgreement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RentalAgreement{" +
            "id=" + getId() +
            ", agreement='" + getAgreement() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
