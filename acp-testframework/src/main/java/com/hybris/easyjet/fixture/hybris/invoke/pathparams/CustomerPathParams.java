package com.hybris.easyjet.fixture.hybris.invoke.pathparams;

import com.hybris.easyjet.fixture.IPathParameters;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.DEFAULT;

/**
 * Created by giuseppedimartino on 13/02/17.
 */
@Builder
public class CustomerPathParams extends PathParameters implements IPathParameters {

    private static final String SIGNIFICANT_OTHERS_SERVICE_PATH = "/significant-others";
    private static final String PROFILE_SERVICE = "/profile";
    private static final String PASSWORD_SERVICE = "/password"; //NOSONAR sonar think this is an actual password
    private static final String LOGOUT_REQUEST_SERVICE = "/logout-request";
    private static final String IDENTITY_DOCUMENTS_SERVICE = "/identity-documents";
    private static final String RECENT_SEARCHES_SERVICE = "/recent-searches";
    private static final String STAFF_FARE_ELIGIBILITY_REQUEST_SERVICE = "/staff-fare-eligibility-request";
    private static final String STAFF_PRIVILEGES_SERVICE = "/staff-privileges";
    private static final String PASSWORD_GENERATION_REQUEST_SERVICE = "/password-generation-request"; //NOSONAR sonar think this is an actual password
    private static final String SAVED_PASSENGERS_SERVICE = "/saved-passengers";
    private static final String DEPENDANTS_SERVICE_PATH = "/dependents";
    private static final String UPDATE_DEPENDANT_EJ_PLUS = "/ej-plus-card-number";
    private static final String MISSING_PASSENGER_ID = "The passenger id must be specified";
    private static final String FULL_PREFERENCES_PATH = "/preferences";
    private static final String COMMUNICATION_PREFERENCES_PATH = "/communication-preferences";
    private static final String TRAVEL_PREFERENCES_PATH = "/travel-preferences";
    private static final String ANCILLARY_PREFERENCES_PATH = "/ancillary-preferences";
    private static final String ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE = "/saved-ssrs";
    private static final String ADD_PAYMENT_METHOD_PATH = "/payment-methods";
    private static final String REMOVE_PAYMENT_METHOD_PATH = "/remove-payment-method-request";
    private static final String CREDIT_PATH = "credit-cards";
    private static final String DEBIT_PATH = "debit-cards";
    private static final String BANK_PATH = "bank-accounts";
    private static final String ADD_COMMENT_TO_CUSTOMER_PATH = "comments";
    private static final String MAKE_DEFAULT_REQUEST = "/make-default-request";
    private static final String FLIGHT_INTEREST = "/flight-interests";


    private String customerId;
    private String passengerId;
    private String documentId;
    private String savedPaymentMethodReference;
    @Builder.Default
    private CustomerPaths path = DEFAULT;
    private String commentCode;

    @Override
    public String get() {

        if (!isPopulated(customerId)) {
            throw new IllegalArgumentException("You must specify a customerId and a path for this service.");
        }

        switch (this.path) {
            case PROFILE:
                return customerId + PROFILE_SERVICE;

            case UPDATE_FULL_PREFERENCES:
                return customerId + FULL_PREFERENCES_PATH;
            case UPDATE_ANCILLARY_PREFERENCES:
                return customerId + FULL_PREFERENCES_PATH + ANCILLARY_PREFERENCES_PATH;
            case UPDATE_COMMUNICATION_PREFERENCES:
                return customerId + FULL_PREFERENCES_PATH + COMMUNICATION_PREFERENCES_PATH;
            case UPDATE_TRAVEL_PREFERENCES:
                return customerId + FULL_PREFERENCES_PATH + TRAVEL_PREFERENCES_PATH;

            case PASSWORD:
                return customerId + PASSWORD_SERVICE;
            case RESET_PASSWORD:
                return customerId + "/password-reset-request";
            case GENERATE_PASSWORD:
                return customerId + PASSWORD_GENERATION_REQUEST_SERVICE;

            case LOGOUT:
                return customerId + LOGOUT_REQUEST_SERVICE;

            case APIS:
                return customerId + IDENTITY_DOCUMENTS_SERVICE;
            case REMOVE_APIS:
                return customerId + IDENTITY_DOCUMENTS_SERVICE + "/" + documentId;

            case SEARCHES:
                return customerId + RECENT_SEARCHES_SERVICE;

            case GET_FLIGHT_INTEREST:
                return customerId + FLIGHT_INTEREST;

            case GET_BOOKING_SUMMARIES:
                return customerId + "/booking-summaries";

            case STAFF_ELIGIBILITY:
                return customerId + STAFF_FARE_ELIGIBILITY_REQUEST_SERVICE;
            case STAFF_PRIVILEGE:
                return customerId + STAFF_PRIVILEGES_SERVICE;

            case ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE:
                return customerId + ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE;
            case DELETE_SSR_CUSTOMER:
                return customerId + ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE + "/" + documentId;

            case GET_SAVED_PASSENGER:
                return customerId + SAVED_PASSENGERS_SERVICE;
            case MANAGE_SAVED_PASSENGER:
                return customerId + SAVED_PASSENGERS_SERVICE + "/" + passengerId;
            case ADD_IDENTITY_DOCUMENT:
                return customerId + SAVED_PASSENGERS_SERVICE + "/" + passengerId + IDENTITY_DOCUMENTS_SERVICE;
            case DELETE_ALL_IDENTITY_DOCUMENTS:
                return customerId + SAVED_PASSENGERS_SERVICE + "/" + passengerId + IDENTITY_DOCUMENTS_SERVICE;
            case UPDATE_IDENTITY_DOCUMENT:
                return customerId + SAVED_PASSENGERS_SERVICE + "/" + passengerId + IDENTITY_DOCUMENTS_SERVICE + "/" + documentId;
            case ADD_SSR:
                return customerId + SAVED_PASSENGERS_SERVICE + "/" + passengerId + ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE;

            case GET_SIGNIFICANT_OTHER:
                return customerId + SIGNIFICANT_OTHERS_SERVICE_PATH;
            case ADD_SIGNIFICANT_OTHER:
                return customerId + SIGNIFICANT_OTHERS_SERVICE_PATH;
            case UPDATE_SIGNIFICANT_OTHER:
                if (StringUtils.isEmpty(passengerId)) {
                    throw new IllegalArgumentException(MISSING_PASSENGER_ID);
                }
                return customerId + SIGNIFICANT_OTHERS_SERVICE_PATH + "/" + passengerId;
            case ADD_SIGNIFICANT_OTHER_ID_DOCUMENT:
                if (StringUtils.isEmpty(passengerId)) {
                    throw new IllegalArgumentException(MISSING_PASSENGER_ID);
                }
                return customerId + SIGNIFICANT_OTHERS_SERVICE_PATH + "/" + passengerId + IDENTITY_DOCUMENTS_SERVICE;
            case UPDATE_SIGNIFICANT_OTHER_ID_DOCUMENT:
                if (StringUtils.isEmpty(passengerId) && StringUtils.isEmpty(documentId)) {
                    throw new IllegalArgumentException(MISSING_PASSENGER_ID);
                }
                return customerId + SIGNIFICANT_OTHERS_SERVICE_PATH + "/" + passengerId + IDENTITY_DOCUMENTS_SERVICE + "/" + documentId;
            case UPDATE_SIGNIFICANT_OTHER_ADD_SSR:
                if (StringUtils.isEmpty(passengerId) && StringUtils.isEmpty(documentId)) {
                    throw new IllegalArgumentException(MISSING_PASSENGER_ID);
                }
                return customerId + SIGNIFICANT_OTHERS_SERVICE_PATH + "/" + passengerId + ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE;

            case UPDATE_DEPENDANTS:
                if (StringUtils.isEmpty(passengerId)) {
                    throw new IllegalArgumentException(MISSING_PASSENGER_ID);
                }
                return customerId + DEPENDANTS_SERVICE_PATH + "/" + passengerId + "/personal-details";
            case UPDATE_DEPENDANTS_ID_DOCS:
                if (StringUtils.isEmpty(passengerId)) {
                    throw new IllegalArgumentException(MISSING_PASSENGER_ID);
                }
                return customerId + DEPENDANTS_SERVICE_PATH + "/" + passengerId + IDENTITY_DOCUMENTS_SERVICE + "/" + documentId;
            case ADD_DEPENDANTS_ID_DOCS:
                if (StringUtils.isEmpty(passengerId)) {
                    throw new IllegalArgumentException(MISSING_PASSENGER_ID);
                }
                return customerId + DEPENDANTS_SERVICE_PATH + "/" + passengerId + IDENTITY_DOCUMENTS_SERVICE;
            case UPDATE_DEPENDANTS_SSR:
                if (StringUtils.isEmpty(passengerId)) {
                    throw new IllegalArgumentException(MISSING_PASSENGER_ID);
                }
                return customerId + DEPENDANTS_SERVICE_PATH + "/" + passengerId + ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE;
            case UPDATE_DEPENDANTS_EJ_PLUS:
                if (StringUtils.isEmpty(passengerId)) {
                    throw new IllegalArgumentException(MISSING_PASSENGER_ID);
                }
                return customerId + DEPENDANTS_SERVICE_PATH + "/" + passengerId + "/personal-details" + UPDATE_DEPENDANT_EJ_PLUS;
            case DEPENDANTS_SERVICE_PATH:
                return customerId + DEPENDANTS_SERVICE_PATH + "/";

            case ADD_CREDIT_CARD_PAYMENT_METHOD:
                return customerId + ADD_PAYMENT_METHOD_PATH + "/" + CREDIT_PATH;
            case ADD_DEBIT_CARD_PAYMENT_METHOD:
                return customerId + ADD_PAYMENT_METHOD_PATH + "/" + DEBIT_PATH;
            case ADD_BANK_ACCOUNT_PAYMENT_METHOD:
                return customerId + ADD_PAYMENT_METHOD_PATH + "/" + BANK_PATH;
            case UPDATE_CREDIT_CARD_PAYMENT_METHOD:
                return customerId + ADD_PAYMENT_METHOD_PATH + "/" + CREDIT_PATH + "/" + savedPaymentMethodReference + MAKE_DEFAULT_REQUEST;
            case UPDATE_DEBIT_CARD_PAYMENT_METHOD:
                return customerId + ADD_PAYMENT_METHOD_PATH + "/" + DEBIT_PATH + "/" + savedPaymentMethodReference + MAKE_DEFAULT_REQUEST;
            case UPDATE_BANK_ACCOUNT_PAYMENT_METHOD:
                return customerId + ADD_PAYMENT_METHOD_PATH + "/" + BANK_PATH + "/" + savedPaymentMethodReference + MAKE_DEFAULT_REQUEST;
            case REMOVE_SAVED_PAYMENT:
                return customerId +ADD_PAYMENT_METHOD_PATH+REMOVE_PAYMENT_METHOD_PATH;
            case GET_PAYMENT_METHOD:
                return customerId + ADD_PAYMENT_METHOD_PATH;

            case ADD_COMMENT_TO_CUSTOMER:
                return customerId + "/" + ADD_COMMENT_TO_CUSTOMER_PATH;
            case MANAGE_CUSTOMER_COMMENTS:
                return customerId + "/" + ADD_COMMENT_TO_CUSTOMER_PATH + "/" + commentCode;

            default:
                return customerId;
        }

    }

    public enum CustomerPaths {
        DEFAULT,
        PROFILE,
        PASSWORD,
        LOGOUT,
        APIS,
        REMOVE_APIS,
        SEARCHES,
        STAFF_ELIGIBILITY,
        STAFF_PRIVILEGE,
        GENERATE_PASSWORD,
        ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE,
        DELETE_SSR_CUSTOMER,
        GET_SAVED_PASSENGER,
        MANAGE_SAVED_PASSENGER,
        ADD_IDENTITY_DOCUMENT,
        UPDATE_IDENTITY_DOCUMENT,
        DELETE_ALL_IDENTITY_DOCUMENTS,
        ADD_SSR,
        ADD_SIGNIFICANT_OTHER,
        UPDATE_SIGNIFICANT_OTHER,
        ADD_SIGNIFICANT_OTHER_ID_DOCUMENT,
        UPDATE_SIGNIFICANT_OTHER_ID_DOCUMENT,
        RESET_PASSWORD,
        UPDATE_DEPENDANTS,
        UPDATE_DEPENDANTS_SSR,
        UPDATE_DEPENDANTS_ID_DOCS,
        ADD_DEPENDANTS_ID_DOCS,
        UPDATE_DEPENDANTS_EJ_PLUS,
        DEPENDANTS_SERVICE_PATH,
        GET_SIGNIFICANT_OTHER,
        UPDATE_SIGNIFICANT_OTHER_ADD_SSR,
        UPDATE_FULL_PREFERENCES,
        UPDATE_COMMUNICATION_PREFERENCES,
        UPDATE_TRAVEL_PREFERENCES,
        UPDATE_ANCILLARY_PREFERENCES,
        ADD_CREDIT_CARD_PAYMENT_METHOD,
        ADD_DEBIT_CARD_PAYMENT_METHOD,
        REMOVE_SAVED_PAYMENT,
        ADD_BANK_ACCOUNT_PAYMENT_METHOD,
        UPDATE_CREDIT_CARD_PAYMENT_METHOD,
        UPDATE_DEBIT_CARD_PAYMENT_METHOD,
        UPDATE_BANK_ACCOUNT_PAYMENT_METHOD,
        ADD_COMMENT_TO_CUSTOMER,
        GET_PAYMENT_METHOD,
        MANAGE_CUSTOMER_COMMENTS,
        GET_BOOKING_SUMMARIES,
        GET_FLIGHT_INTEREST
    }

}