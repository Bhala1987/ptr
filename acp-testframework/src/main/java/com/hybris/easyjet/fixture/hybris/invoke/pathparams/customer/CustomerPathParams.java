package com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.PathParameters;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams.CustomerPaths.DEFAULT;

/**
 * Created by giuseppedimartino on 13/02/17.
 */
@Builder
public class CustomerPathParams extends PathParameters implements IPathParameters {

    private static final String PROFILE = "profile";
    private static final String PASSWORD = "password"; // NOSONAR sonar think this is an actual password
    private static final String PASSWORD_RESET_REQUEST = "password-reset-request"; // NOSONAR sonar think this is an actual password
    private static final String PASSWORD_GENERATION_REQUEST = "password-generation-request"; //NOSONAR sonar think this is an actual password
    private static final String LOGOUT_REQUEST = "logout-request";
    private static final String IDENTITY_DOCUMENTS = "identity-documents";
    private static final String RECENT_SEARCHES = "recent-searches";
    private static final String FLIGHT_INTERESTS = "flight-interests";
    private static final String BOOKING_SUMMARIES = "booking-summaries";
    private static final String STAFF_FARE_ELIGIBILITY_REQUEST = "staff-fare-eligibility-request";
    private static final String STAFF_PRIVILEGES = "staff-privileges";
    private static final String SAVED_SSRS = "saved-ssrs";
    private static final String COMMENTS = "comments";

    private String customerId;
    private String documentId;
    @Builder.Default
    private CustomerPaths path = DEFAULT;
    private String commentCode;

    @Override
    public String get() {

        if (!isPopulated(customerId)) {
            throw new IllegalArgumentException("You must specify a customerId and a path for this service.");
        }

        List<String> uri = new ArrayList<>();
        uri.add(customerId);
        switch (this.path) {
            case DEFAULT:
                break;

            case PROFILE:
                uri.add(PROFILE);
                break;

            case PASSWORD:
                uri.add(PASSWORD);
                break;
            case RESET_PASSWORD:
                uri.add(PASSWORD_RESET_REQUEST);
                break;
            case GENERATE_PASSWORD:
                uri.add(PASSWORD_GENERATION_REQUEST);
                break;

            case LOGOUT:
                uri.add(LOGOUT_REQUEST);
                break;

            case APIS:
                uri.add(IDENTITY_DOCUMENTS);
                if (StringUtils.isNotBlank(documentId)) {
                    uri.add(documentId);
                }
                break;

            case RECENT_SEARCHES:
                uri.add(RECENT_SEARCHES);
                break;

            case FLIGHT_INTERESTS:
                uri.add(FLIGHT_INTERESTS);
                break;

            case BOOKING_SUMMARIES:
                uri.add(BOOKING_SUMMARIES);
                break;

            case STAFF_ELIGIBILITY:
                uri.add(STAFF_FARE_ELIGIBILITY_REQUEST);
                break;
            case STAFF_PRIVILEGE:
                uri.add(STAFF_PRIVILEGES);
                break;

            case SSR:
                uri.add(SAVED_SSRS);
                if (StringUtils.isNotBlank(documentId)) {
                    uri.add(documentId);
                }
                break;

            case COMMENT:
                uri.add(COMMENTS);
                if (StringUtils.isNotBlank(commentCode)) {
                    uri.add(commentCode);
                }
                break;
        }

        return StringUtils.join(uri, '/');

    }

    public enum CustomerPaths {
        DEFAULT,
        PROFILE,
        PASSWORD,
        RESET_PASSWORD,
        GENERATE_PASSWORD,
        LOGOUT,
        APIS,
        RECENT_SEARCHES,
        FLIGHT_INTERESTS,
        BOOKING_SUMMARIES,
        STAFF_ELIGIBILITY,
        STAFF_PRIVILEGE,
        SSR,
        COMMENT
    }

}