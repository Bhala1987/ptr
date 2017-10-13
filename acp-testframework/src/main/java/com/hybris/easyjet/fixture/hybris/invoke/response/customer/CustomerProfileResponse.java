package com.hybris.easyjet.fixture.hybris.invoke.response.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.common.RecentSearch;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.managepaymentdetails.PaymentMethodTypeResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CustomerProfileResponse extends Response {
    private Customer customer;


    @Getter
    @Setter
    public static class Customer {
        private List<String> allowedFunctions = new ArrayList<>();
        private BasicProfile basicProfile;
        private AdvancedProfile advancedProfile;
    }

    @Getter
    @Setter
    public static class BasicProfile {
        private PersonalDetails personalDetails;
        private List<Address> contactAddress = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class PersonalDetails {
        private String customerId;
        private String age;
        private String email;
        private String status;
        private String type;
        private String group;
        private String title;
        private String firstName;
        private String lastName;
        private String ejPlusCardNumber;
        private String nifNumber;
        private String phoneNumber;
        private String alternativePhoneNumber;
        private String flightClubId;
        private String flightClubExpiryDate;
        private String employeeId;
        private String employeeEmail;
        private List<KeyDate> keyDates = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class KeyDate {
        private String type;
        private String day;
        private String month;
    }

    @Getter
    @Setter
    public static class AdvancedProfile extends Response {
        private List<RelatedBooking> relatedBookings = new ArrayList<>();
        private SavedPayments savedPayments;
        private List<Profile> savedPassengers = new ArrayList<>();
        private List<ProfileRecentSearch<Flight>> recentSearches = new ArrayList<>();
        private List<FlightInterest> flightInterests = new ArrayList<>();
        private List<IdentityDocument> identityDocuments = new ArrayList<>();
        private SavedSSRs savedSSRs;
        private SignificantOthers significantOthers;
        private List<Profile> dependents = new ArrayList<>();
        private List<Comment> comments = new ArrayList<>();
        private List<AuditDatum> auditData = new ArrayList<>();
        private CommunicationPreferences communicationPreferences;
        private TravelPreferences travelPreferences;
        private AncillaryPreferences ancillaryPreferences;
    }

    @Getter
    @Setter
    public static class ProfileRecentSearch<F extends com.hybris.easyjet.fixture.hybris.invoke.response.customer.common.RecentSearch.Flight> {
        private F outbound;
        private F inbound;
        private List<PassengerMix> passengerMix = new ArrayList<>();

        @Getter
        @Setter
        public static class Flight {
            private String code;
            private String name;
            private String terminal;
        }

        @Getter
        @Setter
        public static class PassengerMix {
            private String type;
            private String quantity;
        }

    }

    @Getter
    @Setter
    public static class RelatedBooking {
        private String restURI;
        private String referenceNumber;
        private String date;
        private String status;
        private String outboundSectorName;
        private String outboundDepartureDate;
        private String currency;
        private String totalAmount;
        private Boolean isDisrupted;
    }

    @Getter
    @Setter
    public static class SavedPayments {
        private List<PaymentMethodTypeResponse.BankAccounts> bankAccounts = new ArrayList();
        private List<PaymentMethodTypeResponse.PaymentCard> creditCards = new ArrayList();
        private List<PaymentMethodTypeResponse.PaymentCard> debitCards = new ArrayList();
        private List<SavedCard> savedCards = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class SavedCard {
        private String code;
        private String type;
        @JsonProperty("default")
        private Boolean _default;
        private String validToMonth;
        private String validToYear;
        private String validFromMonth;
        private String validFromYear;
        private String lastFourDigits;
    }

    @Getter
    @Setter
    public static class Flight extends RecentSearch.Flight {
        private String marketGroup;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = false)
    public static class FlightInterest {
        private String flightKey;
        private String flightNumber;
        private String carrier;
        private String departureDateTime;
        private String arrivalDateTime;
        private Sector sector;
        private List<FareType> fareTypes = new ArrayList<>();
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = false)
    public static class Sector extends AbstractSector {
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = false)
    public static class FareType {
        private String code;
        private Integer numberAvailable;
    }

    @Getter
    @Setter
    public static class SignificantOthers {
        private String remainingChanges;
        private String changesEndDate;
        private List<Profile> passengers = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class AuditDatum {
        private String changeType;
        private String summary;
        private String agent;
    }

    @Getter
    @Setter
    public static class CommunicationPreferences {
        private List<String> optedOutMarketing = new ArrayList<>();
        private Period optedOutPeriod;
        private List<String> contactMethods = new ArrayList<>();
        private List<String> contactTypes = new ArrayList<>();
        private String frequency;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = false)
    public static class Period {
        private String fromDate;
        private String toDate;
    }

    @Getter
    @Setter
    public static class TravelPreferences {
        private List<String> preferredAirports = new ArrayList<>();
        private List<String> travellingTo = new ArrayList<>();
        private List<String> tripTypes = new ArrayList<>();
        private List<String> travellingWhen = new ArrayList<>();
        private List<String> travellingWith = new ArrayList<>();
        private List<String> travellingSeasons = new ArrayList<>();
        private Period travellingPeriod;
    }

    @Getter
    @Setter
    public static class AncillaryPreferences {
        private List<String> seatingPreferences = new ArrayList<>();
        private String seatNumber;
        private String holdBagQuantity;
        private String holdBagWeight;
    }

}