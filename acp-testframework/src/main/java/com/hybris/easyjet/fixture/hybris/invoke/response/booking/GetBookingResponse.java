package com.hybris.easyjet.fixture.hybris.invoke.response.booking;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetBookingResponse extends Response {

    private BookingContext bookingContext;


    @Getter
    @Setter
    public static class BookingContext {
        private List<String> allowedFunctions = new ArrayList<>();
        private List<String> allowedDocuments = new ArrayList<>();
        private Booking booking;
    }


    @Getter
    @Setter
    public static class Booking {
        private String bookingReference;
        private String bookingDateTime;
        private String bookingLanguage;
        private String defaultCardType;
        private String bookingStatus;
        private String bookingType;
        private String bookingReason;
        private Currency bookingCurrency;
        private String channel;
        private String employeeNumber;
        private String agentId;
        private List<Flights> outbounds = new ArrayList<>();
        private List<Flights> inbounds = new ArrayList<>();
        private CorporateDetails corporateDetails;
        private List<CarHire> carHires = new ArrayList<>();
        private List<TravelInsurance> travelInsurances = new ArrayList<>();
        private List<Hotel> hotels = new ArrayList<>();
        private List<Payment> payments = new ArrayList<>();
        private BookingContact bookingContact;
        private List<OtherBookingInfo> otherBookingInfo = new ArrayList<>();
        private List<Comment> comments = new ArrayList<>();
        private PriceSummary priceSummary;

    }

    @Getter
    @Setter
    public static class Flights extends AbstractFlights<Flight> {
        private String totalAmount;
    }

    @Getter
    @Setter
    public static class Flight extends AbstractFlights.AbstractFoundFlight<Sector, Passenger> {
        private String commercialStatus;
        private String operationalStatus;
        private String disruptionLevel;
        private Boolean closed;
        private Boolean locked;
        private String entryStatus;
        private Boolean active;
    }

    @Getter
    @Setter
    public static class Sector extends AbstractSector {
        private Boolean apisRequired;
        private Boolean nifNumberRequired;
        private CheckInWindow checkInWindow;
    }

    @Getter
    @Setter
    public static class CheckInWindow {
        private String opening;
        private Boolean isOpen;
        private String closing;
    }

    @Getter
    @Setter
    public static class Passenger extends AbstractPassenger {
        private String travellerType;
        private String passengerStatus;
        private List<String> passengerMap = new ArrayList<>();
        private String totalAmount;
        private List<GetBookingResponse.SpecialRequest> specialRequests = new ArrayList<>();
        private List<AdditionalSeat> additionalSeats = new ArrayList<>();
        private String entryStatus;
        private Boolean active;
    }

    @Getter
    @Setter
    public static class SpecialRequest {
        private List<Ssr> ssrs = new ArrayList<>();
        private List<Remark> remarks = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Ssr {
        private String ssrCode;
        private String ssrDescription;
        private Boolean isTandCsAccepted;
    }

    @Getter
    @Setter
    public static class Remark {
        private String code;
        private String name;
    }


    @Getter
    @Setter
    public static class CorporateDetails {
        private String corporateId;
        private String officeId;
        private String dealId;
        private String discountTier;
        private String iataCode;
        private String ejContact;
    }

    @Getter
    @Setter
    public static class Payment {
        private String type;
        private String transactionId;
        private String externalReference;
        private String transactionType;
        private String creditFileReference;
        private String paymentTypeReference;
        private String transactionDateTime;
        private String comment;
        private Amount amount;
        private Integer exchangeRate;
        private ExchangeValue exchangeValue;
    }

    @Getter
    @Setter
    public static class Amount {
        private Double amount;
        private String currencyCode;
    }

    @Getter
    @Setter
    public static class ExchangeValue {
        private Integer amount;
        private String currencyCode;
    }

    @Getter
    @Setter
    public static class BookingContact {
        private String customerId;
        private Name name;
        private Address address;
        private String emailAddress;
        private String phone;
    }

    @Getter
    @Setter
    public static class OtherBookingInfo {
        private String ipAddress;
        private String device;
        private String operatingSystem;
    }

    @Getter
    @Setter
    public static class PriceSummary {
        private AugmentedPriceItems taxes;
        private AugmentedPriceItems fees;
        private AugmentedPriceItems discounts;
        private Double subtotalAmount;
        private Double totalAmount;
    }

}
