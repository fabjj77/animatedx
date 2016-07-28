package com.cs.payment.adyen;

/**
 * @author Joakim Gottzén
 */
final class AdyenConstants {
    private AdyenConstants() {
    }

    public static final String MERCHANT_REFERENCE = "merchantReference";
    public static final String PAYMENT_AMOUNT = "paymentAmount";
    public static final String CURRENCY_CODE = "currencyCode";
    public static final String SHIP_BEFORE_DATE = "shipBeforeDate";
    public static final String SKIN_CODE = "skinCode";
    public static final String MERCHANT_ACCOUNT = "merchantAccount";
    public static final String SESSION_VALIDITY = "sessionValidity";
    public static final String SHOPPER_LOCALE = "shopperLocale";
    public static final String ORDER_DATA = "orderData";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String SHOPPER_EMAIL = "shopperEmail";
    public static final String SHOPPER_REFERENCE = "shopperReference";
    public static final String RECURRING_CONTRACT = "recurringContract";
    public static final String ALLOWED_METHODS = "allowedMethods";
    public static final String BLOCKED_METHODS = "blockedMethods";
    public static final String OFFSET = "offset";
    public static final String BRAND_CODE = "brandCode";
    public static final String ISSUER_ID = "issuerId";
    public static final String SHOPPER_FIRST_NAME = "shopper.firstName";
    public static final String SHOPPER_LAST_NAME = "shopper.lastName";
    public static final String SHOPPER_DATE_OF_BIRTH_DAY_OF_MONTH = "shopper.dateOfBirthDayOfMonth";
    public static final String SHOPPER_DATE_OF_BIRTH_MONTH = "shopper.dateOfBirthMonth";
    public static final String SHOPPER_DATE_OF_BIRTH_YEAR = "shopper.dateOfBirthYear";
    public static final String SHOPPER_TELEPHONE_NUMBER = "shopper.telephoneNumber";
    public static final String SHOPPER_TYPE = "shopperType";
    public static final String SHOPPER_SIG = "shopperSig";
    public static final String MERCHANT_SIG = "merchantSig";

    static final String DEFAULT_OFFSET = "0";
    static final String SHOPPER_TYPE_VALUE = "1";
}
