package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Passenger {
    private String externalPassengerId;
    private List<String> infantsOnLap;
    private List<String> infantsOnSeat;
    private Integer age;
    private Boolean isLead;
    private FareProduct fareProduct;
    private String fareType;
    private String allocatedFareClass;
    private List<HoldItem> holdItems;
    private List<CabinItem> cabinItems;
    private List<AdditionalItem> additionalItems;
    private Seat seat;
    private List<AdditionalSeat> additionalSeats;
    private PassengerAPIS passengerAPIS;
    private List<SpecialRequest> specialRequests;
    private String passengerTotalWithCreditCard;
    private String passengerTotalWithDebitCard;
}