package com.cs.messaging.email;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Omid Alaepour
 */
@Entity
@Table(name = "bronto_workflows")
public class BrontoWorkflow {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    @Column(name = "workflow_id", nullable = false, length = 200)
    @Nonnull
    private String workflowId;

    @Column(name = "name", nullable = false, length = 100)
    @Nonnull
    @Enumerated(STRING)
    private BrontoWorkflowName name;

    @Column(name = "status", nullable = false, length = 100)
    @Nonnull
    @Enumerated(STRING)
    private BrontoWorkflowStatus status;

    @Nonnull
    public Long getId() {
        return id;
    }

    public void setId(@Nonnull final Long id) {
        this.id = id;
    }

    @Nonnull
    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(@Nonnull final String workflowId) {
        this.workflowId = workflowId;
    }

    @Nonnull
    public BrontoWorkflowName getName() {
        return name;
    }

    public void setName(@Nonnull final BrontoWorkflowName name) {
        this.name = name;
    }

    @Nonnull
    public BrontoWorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final BrontoWorkflowStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BrontoWorkflow that = (BrontoWorkflow) o;

        return Objects.equal(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(workflowId)
                .addValue(name)
                .addValue(status)
                .toString();
    }
}
