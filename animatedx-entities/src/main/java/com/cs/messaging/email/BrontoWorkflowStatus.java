package com.cs.messaging.email;

import javax.annotation.Nullable;

/**
 * @author Omid Alaepour
 */
public enum BrontoWorkflowStatus {
    ACTIVE, INACTIVE;

    @Nullable
    public static BrontoWorkflowStatus getWorkflowStatus(final String status) {
        for (final BrontoWorkflowStatus brontoWorkflowStatus : BrontoWorkflowStatus.values()) {
            if (brontoWorkflowStatus.name().equalsIgnoreCase(status)) {
                return brontoWorkflowStatus;
            }
        }
        return null;
    }
}
