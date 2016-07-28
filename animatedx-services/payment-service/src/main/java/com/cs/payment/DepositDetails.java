package com.cs.payment;

/**
 * @author Joakim Gottzén
 */
public class DepositDetails {
    private final String url;
    private final boolean popOut;

    public DepositDetails(final String url, final boolean popOut) {
        this.url = url;
        this.popOut = popOut;
    }

    public String getUrl() {
        return url;
    }

    public boolean isPopOut() {
        return popOut;
    }
}
