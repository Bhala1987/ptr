package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response for getAvailableFareTypes
 */
@Getter
@Setter
public class GetAvailableFareTypesResponse extends Response {
    private List<AvailableFareTypeData> availableFareTypes;

    @Getter
    @Setter
    public static class AvailableFareTypeData {
        private String code;
        private String gdsFareClass;
        private List<FareTypeDetailsData> localisedFareTypeDetails;
        private List<LocalizedFareTypeOptionData> optionsIncluded;
    }

    @Getter
    @Setter
    public static class FareTypeDetailsData {
        private String locale;
        private String name;
        private String description;
        private String fareConditions;
    }

    @Getter
    @Setter
    public static class LocalizedFareTypeOptionData {
        private List<LocalizedName> localizedNames;
    }
}