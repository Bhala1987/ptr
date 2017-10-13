package com.hybris.easyjet.fixture.hybris.asserters.models;

import lombok.Data;

/**
 * Created by daniel on 11/11/2016.
 */
@Data
public class ExpectedAirport {
    private String code;
    private String country;
    private String defaultCurrency;
    private boolean isOnlineCheckInAvailable;
    private boolean isMobileCheckInAvailable;
}