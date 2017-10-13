package com.hybris.easyjet.fixture.hybris.invoke.response.booking;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FindBookingsResponse extends Response {

    private List<Booking> bookings = new ArrayList<>();
    private List<AvailableSortField> availableSortFields = new ArrayList<>();

    @Getter
    @Setter
    public static class Booking {
        private String referenceNumber;
        private String bookingDate;
        private String bookingStatus;
        private String outboundSectorCode;
        private String outboundSectorName;
        private String outboundDepartureDate;
        private String customerTitle;
        private String customerFirstName;
        private String customerLastName;
        private String customerEmail;
        private String customerContactNumber;
        private String customerPostalCode;
        private String currency;
        private String totalAmount;
    }

    @Getter
    @Setter
    public static class AvailableSortField {
        private String code;
        private String name;
        private Boolean isDefault;
    }

}