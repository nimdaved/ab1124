package org.nimdaved.toolrent.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.nimdaved.toolrent.domain.enumeration.ToolType;

/**
 * A Charge.
 */
@Entity
@Table(name = "charge")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Charge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tool_type")
    private ToolType toolType;

    /**
     * Use BigDecimal for the monetary amount as \"middle ground\"
     * between precision issues of \"double\" and complexities of \"Money\"
     */
    @Schema(
        description = "Use BigDecimal for the monetary amount as \"middle ground\"\nbetween precision issues of \"double\" and complexities of \"Money\"",
        required = true
    )
    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "1000")
    @Column(name = "daily_charge", precision = 21, scale = 2, nullable = false)
    private BigDecimal dailyCharge;

    @NotNull
    @Column(name = "weekday_charge", nullable = false)
    private Boolean weekdayCharge;

    @NotNull
    @Column(name = "weekend_charge", nullable = false)
    private Boolean weekendCharge;

    @NotNull
    @Column(name = "holiday_charge", nullable = false)
    private Boolean holidayCharge;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Charge id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ToolType getToolType() {
        return this.toolType;
    }

    public Charge toolType(ToolType toolType) {
        this.setToolType(toolType);
        return this;
    }

    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
    }

    public BigDecimal getDailyCharge() {
        return this.dailyCharge;
    }

    public Charge dailyCharge(BigDecimal dailyCharge) {
        this.setDailyCharge(dailyCharge);
        return this;
    }

    public void setDailyCharge(BigDecimal dailyCharge) {
        this.dailyCharge = dailyCharge;
    }

    public Boolean getWeekdayCharge() {
        return this.weekdayCharge;
    }

    public Charge weekdayCharge(Boolean weekdayCharge) {
        this.setWeekdayCharge(weekdayCharge);
        return this;
    }

    public void setWeekdayCharge(Boolean weekdayCharge) {
        this.weekdayCharge = weekdayCharge;
    }

    public Boolean getWeekendCharge() {
        return this.weekendCharge;
    }

    public Charge weekendCharge(Boolean weekendCharge) {
        this.setWeekendCharge(weekendCharge);
        return this;
    }

    public void setWeekendCharge(Boolean weekendCharge) {
        this.weekendCharge = weekendCharge;
    }

    public Boolean getHolidayCharge() {
        return this.holidayCharge;
    }

    public Charge holidayCharge(Boolean holidayCharge) {
        this.setHolidayCharge(holidayCharge);
        return this;
    }

    public void setHolidayCharge(Boolean holidayCharge) {
        this.holidayCharge = holidayCharge;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Charge)) {
            return false;
        }
        return getId() != null && getId().equals(((Charge) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Charge{" +
            "id=" + getId() +
            ", toolType='" + getToolType() + "'" +
            ", dailyCharge=" + getDailyCharge() +
            ", weekdayCharge='" + getWeekdayCharge() + "'" +
            ", weekendCharge='" + getWeekendCharge() + "'" +
            ", holidayCharge='" + getHolidayCharge() + "'" +
            "}";
    }
}
