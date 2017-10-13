package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PassengerTypesResponse extends Response {
    private List<PassengerType> passengerTypes = new ArrayList<>();
    private PassengerRules passengerRules;

    @Getter
    @Setter
    public static class PassengerType {
        private String code;
        private List<LocalizedName> localizedNames = new ArrayList<>();
        private Integer minAge;
        private Integer maxPermitted;
        private Integer maxAge;
    }

    @Getter
    @Setter
    public static class PassengerRules {
        private Integer maxPassengersPerBooking;
        private String adultInfantOnLapRatio;
        private String adultInfantOnSeatRatio;
    }

}