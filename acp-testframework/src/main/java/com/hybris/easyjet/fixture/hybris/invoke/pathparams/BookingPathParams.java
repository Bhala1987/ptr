package com.hybris.easyjet.fixture.hybris.invoke.pathparams;

import com.hybris.easyjet.fixture.IPathParameters;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.DEFAULT;


/**
 * Created by daniel on 28/11/2016.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class BookingPathParams extends PathParameters implements IPathParameters {

    private BookingPaths path;
    private String bookingId;
    private String passengerId;
    private String commentId;
    private String bookingReference;

    @Override
    public String get() {

        if (!isPopulated(bookingId)) {
            throw new IllegalArgumentException("You must specify a bookingId for this service.");
        }

        if (path == null) {
            path = DEFAULT;
        }

        switch (path) {
            case ADD_COMMENT:
                return bookingId + "/comments";
            case DELETE_COMMENT:
                return bookingId + "/comments/" + commentId;
            case UPDATE_COMMENT:
                return bookingId + "/comments/" + commentId;
            case AMENDABLE_BOOKING_REQUEST:
                return bookingId + "/create-basket-request";
            case SET_APIS_BOOKING:
                return bookingId + "/passengers/" + passengerId + "/identity-documents";
            case CHECKIN:
                return "/"+ bookingId + "/checkin-request";
            case CANCEL:
                return bookingId + "/initiate-cancel-booking-request";
            case GETBOOKING:
                return "/" + bookingReference;
            case REFUNDABLE_PAYMENT_METHODS:
                return  bookingId+ "/refund-payment-methods";
            case CANCEL_BOOKING:
                return "/" + bookingId + "/cancel-booking-request";
            case BOOKING_DOCUMENTS:
                return "/" + bookingId + "/request-documents";
            default:
                return bookingId;
        }
    }

    public enum BookingPaths {
        DEFAULT, ADD_COMMENT,CANCEL_BOOKING, DELETE_COMMENT, UPDATE_COMMENT, AMENDABLE_BOOKING_REQUEST, SET_APIS_BOOKING, CHECKIN, GETBOOKING, CANCEL, BOOKING_DOCUMENTS, REFUNDABLE_PAYMENT_METHODS
    }
}
