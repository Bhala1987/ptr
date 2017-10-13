package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g.dimartino on 13/05/17.
 */
@Getter
@Setter
public class CarHire {
    private String type;
    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;
    private String category;
    private String rateId;
    private CheckInOutStation checkInStation;
    private CheckInOutStation checkOutStation;
    private String checkInDateTime;
    private String checkOutDateTime;
    private String customerEmail;
    private String customerPhone;
    private Driver primaryDriver;
    private Driver otherDrivers;
    private List<AbstractProductItem> carExtras = new ArrayList<>();

    @Getter
    @Setter
    public static class CheckInOutStation {
        private Address address;
        private String emailAddress;
        private String phone;
        private String openingTime;
        private String closingTime;
        private String faxNumber;
        private String stationCode;
        private String stationName;
    }

    @Getter
    @Setter
    public static class Driver {
        private Name driverName;
        private String age;
        private String countryOfResidence;
        private String drivingLicenceNumber;
        private String drivingLicenceIssuingCountry;
        private String drivingLicenceExpiryDate;
    }

}