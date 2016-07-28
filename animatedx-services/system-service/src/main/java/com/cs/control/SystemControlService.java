package com.cs.control;

/**
 * @author Omid Alaepour
 */
public interface SystemControlService {

    void validateRegistrationAllowed(String email);

    boolean validateLoginAllowed(String email);

    boolean isBrontoEnabled();
}
