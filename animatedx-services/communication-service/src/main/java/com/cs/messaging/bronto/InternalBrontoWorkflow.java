package com.cs.messaging.bronto;

import com.cs.messaging.email.BrontoWorkflow;
import com.cs.messaging.email.BrontoWorkflowName;
import com.cs.messaging.email.BrontoWorkflowStatus;

import java.util.Date;

/**
 * @author Omid Alaepour
 */
public class InternalBrontoWorkflow {
    private String id;
    private String siteId;
    private BrontoWorkflowName name;
    private String description;
    private BrontoWorkflowStatus status;
    private Date createdDate;
    private Date modifiedDate;
    private Date activatedDate;
    private Date deActivatedDate;

    public InternalBrontoWorkflow(final BrontoWorkflow brontoWorkflow) {
        id = brontoWorkflow.getWorkflowId();
//        name = brontoWorkflow.getName();
//        status = brontoWorkflow.getStatus();
        //Todo test and fix
    }

    public InternalBrontoWorkflow() {
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(final String siteId) {
        this.siteId = siteId;
    }

    public BrontoWorkflowName getName() {
        return name;
    }

    public void setName(final BrontoWorkflowName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public BrontoWorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(final BrontoWorkflowStatus status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(final Date activatedDate) {
        this.activatedDate = activatedDate;
    }

    public Date getDeActivatedDate() {
        return deActivatedDate;
    }

    public void setDeActivatedDate(final Date deActivatedDate) {
        this.deActivatedDate = deActivatedDate;
    }
}
