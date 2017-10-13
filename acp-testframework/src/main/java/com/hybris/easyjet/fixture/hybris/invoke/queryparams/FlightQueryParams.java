package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by webbd on 10/31/2016.
 */
@Data
@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
public class FlightQueryParams extends QueryParameters {

    private String origin;
    private String destination;
    private String outboundDate;
    private String inboundDate;
    private String adult;
    private String infant;
    private String child;
    private String flexibleDays;
    private String fareTypes;
    private String currency;
    private String minPrice;
    private String maxPrice;
    private String isAlternativeFlightsRequired;
    private String indirectOutbound;
    private String indirectInbound;
    private String groupBooking;
    private String staffBooking;
    private String bookingRef;
    private String entryRef;
    private String clearBasket;
    private String excludeAdminFee;
    private String flightKey;
    private List<String> passenderIds;

    @Override
    public Map<String, String> getParameters() {

        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(origin)) {
            queryParams.put("origin", origin);
        }
        if (isPopulated(destination)) {
            queryParams.put("destination", destination);
        }
        if (isPopulated(outboundDate)) {
            queryParams.put("outbound-date", outboundDate);
        }
        if (isPopulated(inboundDate)) {
            queryParams.put("inbound-date", inboundDate);
        }
        if (isPopulated(flexibleDays)) {
            queryParams.put("flexible-days", flexibleDays);
        }
        if (isPopulated(adult)) {
            queryParams.put("adult", adult);
        }
        if (isPopulated(infant)) {
            queryParams.put("infant", infant);
        }
        if (isPopulated(child)) {
            queryParams.put("child", child);
        }
        if (isPopulated(minPrice)) {
            queryParams.put("min-price", minPrice);
        }
        if (isPopulated(maxPrice)) {
            queryParams.put("max-price", maxPrice);
        }
        if (isPopulated(fareTypes)) {
            queryParams.put("fare-types", fareTypes);
        }
        if (isPopulated(isAlternativeFlightsRequired)) {
            queryParams.put("is-alternative-flights-required", isAlternativeFlightsRequired);
        }
        if (isPopulated(currency)) {
            queryParams.put("currency", currency);
        }
        if (isPopulated(indirectOutbound)) {
            queryParams.put("indirect-outbound", indirectOutbound);
        }
        if (isPopulated(indirectInbound)) {
            queryParams.put("indirect-inbound", indirectInbound);
        }

        if (isPopulated(groupBooking)) {
            queryParams.put("group-booking", groupBooking);
        }

        if (isPopulated(staffBooking)) {
            queryParams.put("staff-booking", staffBooking);
        }

        if (isPopulated(bookingRef)) {
            queryParams.put("booking-ref", bookingRef);
        }

        if (isPopulated(entryRef)) {
            queryParams.put("entry-ref", entryRef);
        }

        if (isPopulated(clearBasket)) {
            queryParams.put("clear-basket", clearBasket);
        }

        if (isPopulated(excludeAdminFee)) {
            queryParams.put("exclude-admin-fee", excludeAdminFee);
        }

        if (isPopulated(flightKey)) {
            queryParams.put("flight-key", flightKey);
        }

        if (isPopulated(passenderIds)) {
            queryParams.put("passenger-id-list", String.join(",", passenderIds));
        }

        return queryParams;
    }

}


