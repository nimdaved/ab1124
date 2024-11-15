package org.nimdaved.toolrent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The concept of ToolInventory is not specified in the requirents.
 * The entity created as an example of handling \"unspoken\" implicit
 * domain requirements
 */
@Schema(
    description = "The concept of ToolInventory is not specified in the requirents.\nThe entity created as an example of handling \"unspoken\" implicit\ndomain requirements"
)
@Entity
@Table(name = "tool_inventory")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ToolInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 64)
    @Column(name = "location", length = 64, nullable = false)
    private String location;

    @NotNull
    @Min(value = 0)
    @Max(value = 1000)
    @Column(name = "stock_count", nullable = false)
    private Integer stockCount;

    @NotNull
    @Min(value = 0)
    @Max(value = 1000)
    @Column(name = "checked_out_count", nullable = false)
    private Integer checkedOutCount;

    @NotNull
    @Min(value = 0)
    @Max(value = 1000)
    @Column(name = "on_hold_count", nullable = false)
    private Integer onHoldCount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "toolInventory")
    @JsonIgnoreProperties(value = { "toolInventory" }, allowSetters = true)
    private Set<Tool> tools = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ToolInventory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return this.location;
    }

    public ToolInventory location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getStockCount() {
        return this.stockCount;
    }

    public ToolInventory stockCount(Integer stockCount) {
        this.setStockCount(stockCount);
        return this;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Integer getCheckedOutCount() {
        return this.checkedOutCount;
    }

    public ToolInventory checkedOutCount(Integer checkedOutCount) {
        this.setCheckedOutCount(checkedOutCount);
        return this;
    }

    public void setCheckedOutCount(Integer checkedOutCount) {
        this.checkedOutCount = checkedOutCount;
    }

    public Integer getOnHoldCount() {
        return this.onHoldCount;
    }

    public ToolInventory onHoldCount(Integer onHoldCount) {
        this.setOnHoldCount(onHoldCount);
        return this;
    }

    public void setOnHoldCount(Integer onHoldCount) {
        this.onHoldCount = onHoldCount;
    }

    public Set<Tool> getTools() {
        return this.tools;
    }

    public void setTools(Set<Tool> tools) {
        if (this.tools != null) {
            this.tools.forEach(i -> i.setToolInventory(null));
        }
        if (tools != null) {
            tools.forEach(i -> i.setToolInventory(this));
        }
        this.tools = tools;
    }

    public ToolInventory tools(Set<Tool> tools) {
        this.setTools(tools);
        return this;
    }

    public ToolInventory addTool(Tool tool) {
        this.tools.add(tool);
        tool.setToolInventory(this);
        return this;
    }

    public ToolInventory removeTool(Tool tool) {
        this.tools.remove(tool);
        tool.setToolInventory(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ToolInventory)) {
            return false;
        }
        return getId() != null && getId().equals(((ToolInventory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ToolInventory{" +
            "id=" + getId() +
            ", location='" + getLocation() + "'" +
            ", stockCount=" + getStockCount() +
            ", checkedOutCount=" + getCheckedOutCount() +
            ", onHoldCount=" + getOnHoldCount() +
            "}";
    }
}
