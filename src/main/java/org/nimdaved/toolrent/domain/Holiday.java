package org.nimdaved.toolrent.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.nimdaved.toolrent.domain.enumeration.HolidayType;

/**
 * A Holiday.
 */
@Entity
@Table(name = "holiday")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Holiday implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "holiday_type", nullable = false)
    private HolidayType holidayType;

    @NotNull
    @Min(value = 1)
    @Max(value = 12)
    @Column(name = "month_number", nullable = false)
    private Integer monthNumber;

    /**
     * Day of month (1..31) when holidayType is EXACT_DAY_OF_MONTH,
     * otherwise day of week (1..7). @see ISO-8601
     */
    @Schema(
        description = "Day of month (1..31) when holidayType is EXACT_DAY_OF_MONTH,\notherwise day of week (1..7). @see ISO-8601",
        required = true
    )
    @NotNull
    @Min(value = 1)
    @Max(value = 31)
    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @NotNull
    @Column(name = "observed_on_closest_weekday", nullable = false)
    private Boolean observedOnClosestWeekday;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Holiday id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Holiday name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HolidayType getHolidayType() {
        return this.holidayType;
    }

    public Holiday holidayType(HolidayType holidayType) {
        this.setHolidayType(holidayType);
        return this;
    }

    public void setHolidayType(HolidayType holidayType) {
        this.holidayType = holidayType;
    }

    public Integer getMonthNumber() {
        return this.monthNumber;
    }

    public Holiday monthNumber(Integer monthNumber) {
        this.setMonthNumber(monthNumber);
        return this;
    }

    public void setMonthNumber(Integer monthNumber) {
        this.monthNumber = monthNumber;
    }

    public Integer getDayNumber() {
        return this.dayNumber;
    }

    public Holiday dayNumber(Integer dayNumber) {
        this.setDayNumber(dayNumber);
        return this;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Boolean getObservedOnClosestWeekday() {
        return this.observedOnClosestWeekday;
    }

    public Holiday observedOnClosestWeekday(Boolean observedOnClosestWeekday) {
        this.setObservedOnClosestWeekday(observedOnClosestWeekday);
        return this;
    }

    public void setObservedOnClosestWeekday(Boolean observedOnClosestWeekday) {
        this.observedOnClosestWeekday = observedOnClosestWeekday;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Holiday)) {
            return false;
        }
        return getId() != null && getId().equals(((Holiday) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Holiday{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", holidayType='" + getHolidayType() + "'" +
            ", monthNumber=" + getMonthNumber() +
            ", dayNumber=" + getDayNumber() +
            ", observedOnClosestWeekday='" + getObservedOnClosestWeekday() + "'" +
            "}";
    }
}
