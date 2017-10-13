package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.OfferPrice;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class GetSeatMapResponse extends Response {
    private Flight flight;
    private String currencyCode;
    private List<Product> products = new ArrayList<>();
    private SeatLayout seatLayout;
    private Availability availability;
    private Metadata metadata;

    @Getter
    @Setter
    public static class Flight {
        private String flightKey;
        private String aircraftType;
    }

    @Getter
    @Setter
    public static class Product {
        private String id;
        private String name;
        private Double basePrice;
        private OfferPrice offerPrices;
        private Boolean isEligibleForPriorityBoarding;
        private List<String> seats = new ArrayList<>();
        private String entryStatus;
        private Boolean active;

    }

    @Getter
    @Setter
    public static class SeatLayout {
        private List<Bay> bays = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Bay {
        private String name;
        private List<Row> rows = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Row {
        private Integer rowNumber;
        private List<Block> blocks = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Block {
        private Integer sequence;
        private List<String> seats = new ArrayList<>();
        private List<Pattern> invalidPatterns = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Pattern {
        private List<String> patterns = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Availability {
        private Available available;
        private List<PlacementRule> placementRules = new ArrayList<>();
        private List<AdditionalSeatRule> additionalSeatRules = new ArrayList<>();
        private List<Restriction> restrictions = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Available {
        private List<String> seats = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class PlacementRule {
        private List<String> passengerType = new ArrayList<>();
        private String layoutElement;
        private Integer limit;
    }

    @Getter
    @Setter
    public static class AdditionalSeatRule {
        private List<Block> blocks = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Restriction {
        private List<String> passengerType = new ArrayList<>();
        private List<String> specialAssistanceType = new ArrayList<>();
        private List<String> seats = new ArrayList<>();
        private List<String> itemType = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Metadata {
        private Available isFrontRow;
        private Available isAisle;
        private Available isWindow;
        private Available isMiddle;
        private Available isBlank;
        private Available isEmergencyExit;
        private Available isBulkhead;
        private Available isFrontExitDoor;
        private Available isRearExitDoor;
        private List<Attribute> attributes = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Attribute {
        private String attribute;
        private List<String> seats = new ArrayList<>();
    }

}