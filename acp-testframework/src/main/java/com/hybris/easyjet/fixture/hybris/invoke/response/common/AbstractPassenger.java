package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class AbstractPassenger {
    private String code;
    private String toeiCode;
    private Boolean isLead;
    private PassengerDetails passengerDetails;
    @JsonProperty("ICTSStatus")
    private String ictsStatus;
    private List<String> infantsOnLap = new ArrayList<>();
    private List<String> infantsOnSeat = new ArrayList<>();
    private Integer age;
    private FareProduct fareProduct;
    private String fareType;
    private String allocatedFareClass;
    private List<HoldItem> holdItems = new ArrayList<>();
    private List<CabinItem> cabinItems = new ArrayList<>();
    private List<AdditionalItem> additionalItems = new ArrayList<>();
    private Seat seat;
    private PassengerAPIS passengerAPIS;
    private String apisStatus;
    private String boardingPassStatus;
    private List<AdditionalSeat> additionalSeats = new ArrayList<>();
    private List<String> passengerMap = new ArrayList<>();


    @Getter
    @Setter
    public static class PassengerDetails {
        private Name name;
        private String phoneNumber;
        private String email;
        private String passengerType;
        private String nifNumber;
        private String ejPlusCardNumber;
    }

    @Getter
    @Setter
    public static class FareProduct extends AbstractProductItem {
    }

    @Getter
    @Setter
    public static class HoldItem extends CabinItem {
    }

    @Getter
    @Setter
    public static class ExtraWeight extends AbstractProductItem {//NOSONAR
        //Sonar insist subclass should overide equals - this is handled by lombok
        private Integer maxLength;
        private Integer maxWidth;
        private Integer maxHeight;
        private Integer maxWeight;
    }

    @Getter
    @Setter
    public static class CabinItem extends ExtraWeight { //NOSONAR
        private List<AbstractPassenger.ExtraWeight> extraWeight = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class AdditionalItem extends AbstractProductItem {
    }

    @Getter
    @Setter
    public static class Seat extends AbstractProductItem { //NOSONAR
        private String seatNumber;
        private String seatBand;
        private List<String> seatCharacteristics = new ArrayList<>();
        private Boolean isAutoAllocated;
    }

    @Getter
    @Setter
    public static class AdditionalSeat {
        private FareProduct fareProduct;
        private AbstractPassenger.Seat seat;
        private String seatReasonCode;

    }

    @Getter
    @Setter
    public static class PassengerAPIS {
        private Name name;
        private String dateOfBirth;
        private String documentExpiryDate;
        private String documentNumber;
        private String documentType;
        private String gender;
        private String nationality;
        private String countryOfIssue;
    }

}