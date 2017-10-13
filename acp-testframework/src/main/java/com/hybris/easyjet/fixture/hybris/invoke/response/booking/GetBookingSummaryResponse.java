package com.hybris.easyjet.fixture.hybris.invoke.response.booking;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajakm on 02/08/2017.
 */
@Getter
@Setter
public class GetBookingSummaryResponse extends Response{

    private List<Flights> bookingSummaries = new ArrayList<>();

    @Getter
    @Setter
    public static class Flights {
        private String restURI;
        private String referenceNumber;
        private String creationDate;
        private String bookingStatus;
        private String bookingCurrency;
        private String bookingTotalAmount;
        private String customerFullName;
        private String departureAirportName;
        private String arrivalAirportName;
        private String flightNumber;
        private String departureDateTime;
        private String disruptionLevel;
        private List<Passengers> passengers = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Passengers {
        private String passengerFullName;
        private String fareType;
    }

}
