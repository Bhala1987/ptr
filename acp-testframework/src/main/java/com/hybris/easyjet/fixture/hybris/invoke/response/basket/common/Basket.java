package com.hybris.easyjet.fixture.hybris.invoke.response.basket.common;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g.dimartino on 21/05/17.
 */
@Getter
@Setter
public class Basket {
    private String basketLanguage;
    private String basketType;
    private String bookingReason;
    private String code;
    private String defaultCardType;
    private AugmentedPriceItems.Discounts discounts;
    private AugmentedPriceItems fees;
    private List<Flights> outbounds = new ArrayList<>();
    private Currency currency;
    private List<Flights> inbounds = new ArrayList<>();
    private List<CarHire> carHires = new ArrayList<>();
    private List<TravelInsurance> travelInsurances = new ArrayList<>();
    private List<Hotel> hotels = new ArrayList<>();
    private AugmentedPriceItems taxes;
    private Double subtotalAmountWithCreditCard;
    private Double subtotalAmountWithDebitCard;
    private Double totalAmountWithCreditCard;
    private Double totalAmountWithDebitCard;
    private PriceDifference priceDifference;

    @Override
    public String toString() {
        return this.code;
    }

    @Getter
    @Setter
    public static class Flights extends AbstractFlights<Flight> {
        private Double journeyTotalWithCreditCard;
        private Double journeyTotalWithDebitCard;
    }

    @Getter
    @Setter
    public static class Flight extends AbstractFlights.AbstractFoundFlight<Sector, Passenger> {
        private Boolean active;
        private String entryStatus;
    }

    @Getter
    @Setter
    public static class Sector extends AbstractSector {
        private Boolean apisRequired;
        private Boolean nifNumberRequired;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = false)
    public static class Passenger extends AbstractPassenger {
        private SpecialRequest specialRequests;
        private String passengerType;
        private Double passengerTotalWithCreditCard;
        private Double passengerTotalWithDebitCard;
        private SavedSSRs savedSSRs;
        private List<String> passengerMap;
        private List<AdditionalSeat> additionalSeats = new ArrayList<>();
        private String passengerStatus;
        private Boolean active;
        private Boolean saveToCustomerProfile;
        private String entryStatus;
        private String savedPassengerCode;
        @Override
        public String toString() {
            return this.getCode();
        }
    }

    @Getter
    @Setter
    public static class PriceDifference {
        private Double amountWithCreditCard;
        private Double amountWithDebitCard;
    }

}
