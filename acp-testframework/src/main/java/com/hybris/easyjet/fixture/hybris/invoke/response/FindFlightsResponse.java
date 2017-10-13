package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.OfferPrice;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FindFlightsResponse extends Response {
    private String defaultCardType;
    private String currency;
    private Flights outbound;
    private Flights inbound;

    @Getter
    @Setter
    public static class Flights {
        private List<AlternativeSector> alternativeSectors = new ArrayList<>();
        private List<Journey> journeys = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Journey extends AbstractFlights<Flight> {
    }

    @Getter
    @Setter
    public static class Flight extends AbstractFlights.AbstractFlight {
        private String duration;
        private String availableStatus;
        private String operationalStatus;
        private Boolean isApisRequired;
        private Boolean isWrappedFare;
        private Boolean isSpeedyBoardingAllowed;
        private Airport departure;
        private Airport arrival;
        private Inventory inventory;
        private List<FareType> fareTypes = new ArrayList<>();
        private OfferPrice farePriceDifference;
    }

    @Getter
    @Setter
    public static class Airport {
        private String airportCode;
        private String airportName;
        private String date;
        private String marketGroup;
        private String terminal;
    }

    @Getter
    @Setter
    public static class Inventory {
        private Integer capacity;
        private Integer available;
        private Integer seats;
        private Integer lid;
    }

    @Getter
    @Setter
    public static class FareType {
        private String fareTypeCode;
        private Boolean isLowestFare;
        private String gdsFareClass;
        private TotalFare totalFare;
        private List<AugmentedPriceItem> discounts = new ArrayList<>();
        private FareClass fareClass;
        private List<Passenger> passengers = new ArrayList<>();
        private OfferPrice farePriceDifference;
    }

    @Getter
    @Setter
    public static class TotalFare {
        private Double withCreditCardFee;
        private Double withDebitCardFee;
    }

    @Getter
    @Setter
    public static class AugmentedPriceItem {
        private String code;
        private Double value;
        private Integer percentageValue;
    }

    @Getter
    @Setter
    public static class FareClass {
        private String code;
        private Integer availableUnits;
    }

    @Getter
    @Setter
    public static class Passenger {
        private String type;
        private Double basePrice;
        private Integer quantity;
        private Double totalTaxes;
        private Double totalFees;
        private Double totalDiscounts;
        private Integer additionalSeats;
        private Boolean infantOnSeat;
        private List<AugmentedPriceItem> discounts = new ArrayList<>();
        private List<AugmentedPriceItem> taxes = new ArrayList<>();
        private List<AugmentedPriceItem> fees = new ArrayList<>();
        private TotalFare totalPassengerFare;
        private OfferPrice farePriceDifference;
    }

    @Getter
    @Setter
    public static class AlternativeSector {
        private String origin;
        private String destination;
        private String fareType;
    }

}