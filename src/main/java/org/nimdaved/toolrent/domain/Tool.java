package org.nimdaved.toolrent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.nimdaved.toolrent.domain.enumeration.ToolType;
import org.springframework.data.domain.Persistable;

/**
 * A Tool.
 */
@Entity
@Table(name = "tool")
@JsonIgnoreProperties(value = { "new", "id" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tool implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 4, max = 16)
    @Id
    @Column(name = "code", length = 16, nullable = false, unique = true)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tool_type", nullable = false)
    private ToolType toolType;

    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "brand", length = 128, nullable = false)
    private String brand;

    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_inventory_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = { "tools" }, allowSetters = true)
    private ToolInventory toolInventory;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getCode() {
        return this.code;
    }

    public Tool code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ToolType getToolType() {
        return this.toolType;
    }

    public Tool toolType(ToolType toolType) {
        this.setToolType(toolType);
        return this;
    }

    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
    }

    public String getBrand() {
        return this.brand;
    }

    public Tool brand(String brand) {
        this.setBrand(brand);
        return this;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @Override
    public String getId() {
        return this.code;
    }

    @org.springframework.data.annotation.Transient
    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Tool setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public ToolInventory getToolInventory() {
        return this.toolInventory;
    }

    public void setToolInventory(ToolInventory toolInventory) {
        this.toolInventory = toolInventory;
    }

    public Tool toolInventory(ToolInventory toolInventory) {
        this.setToolInventory(toolInventory);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tool)) {
            return false;
        }
        return getCode() != null && getCode().equals(((Tool) o).getCode());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tool{" +
            "code=" + getCode() +
            ", toolType='" + getToolType() + "'" +
            ", brand='" + getBrand() + "'" +
            "}";
    }
}
