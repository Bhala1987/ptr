package com.hybris.easyjet.fixture.hybris.invoke.pathparams;

import lombok.Builder;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.EventMessagePathParams.EventPaths.DEFAULT;

@Builder
public class EventMessagePathParams extends PathParameters {

    private static final String ADD_CUSTOMER_COMMENT_EVENT = "/addCustomerComment";
    private static final String UPDATE_CUSTOMER_COMMENT_EVENT = "/updateCustomerComment";
    private static final String REMOVE_CUSTOMER_COMMENT_EVENT = "/removeCustomerComment";

    private static final String ADD_BOOKING_COMMENT_EVENT = "/addBookingComment";
    private static final String UPDATE_BOOKING_COMMENT_EVENT = "/updateBookingComment";
    private static final String REMOVE_BOOKING_COMMENT_EVENT = "/removeBookingComment";

    private static final String CREATE_BOOKING_EVENT = "/bookingCreated";
    private static final String UPDATE_BOOKING_EVENT = "/updateBookingCreated";
    private static final String DELETE_BOOKING_EVENT = "/bookingCancelled";

    private static final String CREATE_CUSTOMER_EVENT = "/customerCreated";
    private static final String UPDATE_CUSTOMER_EVENT = "/customerUpdated";

    @Builder.Default
    private EventPaths path = DEFAULT;

    @Override
    public String get() {

        switch (path) {
            case ADD_CUSTOMER_COMMENT_EVENT:
                return ADD_CUSTOMER_COMMENT_EVENT;

            case UPDATE_CUSTOMER_COMMENT_EVENT:
                return UPDATE_CUSTOMER_COMMENT_EVENT;

            case REMOVE_CUSTOMER_COMMENT_EVENT:
                return REMOVE_CUSTOMER_COMMENT_EVENT;

            case ADD_BOOKING_COMMENT_EVENT:
                return ADD_BOOKING_COMMENT_EVENT;

            case UPDATE_BOOKING_COMMENT_EVENT:
                return UPDATE_BOOKING_COMMENT_EVENT;

            case REMOVE_BOOKING_COMMENT_EVENT:
                return REMOVE_BOOKING_COMMENT_EVENT;

            case CREATE_BOOKING_EVENT:
                return CREATE_BOOKING_EVENT;

            case UPDATE_BOOKING_EVENT:
                return UPDATE_BOOKING_EVENT;

            case DELETE_BOOKING_EVENT:
                return DELETE_BOOKING_EVENT;

            case CREATE_CUSTOMER_EVENT:
                return CREATE_CUSTOMER_EVENT;

            case UPDATE_CUSTOMER_EVENT:
                return UPDATE_CUSTOMER_EVENT;


            default:
                throw new IllegalArgumentException("You must specify a path for this service.");
        }

    }

    public enum EventPaths {
        DEFAULT,
        ADD_CUSTOMER_COMMENT_EVENT,
        UPDATE_CUSTOMER_COMMENT_EVENT,
        REMOVE_CUSTOMER_COMMENT_EVENT,
        ADD_BOOKING_COMMENT_EVENT,
        UPDATE_BOOKING_COMMENT_EVENT,
        REMOVE_BOOKING_COMMENT_EVENT,
        CREATE_BOOKING_EVENT,
        UPDATE_BOOKING_EVENT,
        DELETE_BOOKING_EVENT,
        CREATE_CUSTOMER_EVENT,
        UPDATE_CUSTOMER_EVENT
    }
}
