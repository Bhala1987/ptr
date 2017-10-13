package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by webbd on 10/20/2016.
 */
@Getter
@Setter
public class GetAirportsResponse extends Response {
    private List<Airport> airports = new ArrayList<>();

    @Getter
    @Setter
    public static class Airport {
        private String code;
        private List<GetAirportsResponse.LocalizedValue> localizedNames = new ArrayList<>();
        private List<GetAirportsResponse.LocalizedValue> localizedCityNames = new ArrayList<>();
        private String country;
        private GeoLocation geoLocation;
        private Address address;
        private String timeZone;
        private String defaultCurrency;
        //TODO check what is a Terminal
        private List<Object> terminals = new ArrayList<>();
        //TODO check what is an AvailableService
        private List<Object> availableServices = new ArrayList<>();
        private Boolean isAvoidStopOver;
        private Boolean isOnlineCheckInAvailable;
        private Boolean isMobileCheckInAvailable;
        private Integer onlineCheckInCloseTime;
        private Integer airportCheckInCloseTime;
        private String marketGroup;
    }

    @Getter
    @Setter
    public static class GeoLocation {
        private Double latitude;
        private Double longitude;
    }

    @Getter
    @Setter
    public static class LocalizedValue {
        private String locale;
        private String name;
    }

    @Getter
    @Setter
    public static class Address {
        private String line1;
        private String line2;
        private String line3;
        private String postalCode;
    }

}