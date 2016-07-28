package com.cs.messaging.email;

import javax.annotation.Nullable;

/**
 * @author Omid Alaepour
 */
public enum BrontoWorkflowName {
    SIGNUP,
    @SuppressWarnings("SpellCheckingInspection")EMAILVERIFICATION,
    @SuppressWarnings("SpellCheckingInspection")RESETPASSWORD;

    @Nullable
    public static BrontoWorkflowName getWorkflowName(final String name) {
        for (final BrontoWorkflowName brontoWorkflowName : BrontoWorkflowName.values()) {
            if (brontoWorkflowName.name().equalsIgnoreCase(name)) {
                return brontoWorkflowName;
            }
        }
        return null;
    }
}
