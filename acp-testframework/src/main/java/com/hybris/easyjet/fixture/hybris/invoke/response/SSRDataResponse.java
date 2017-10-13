package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedValue;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppecioce on 09/02/2017.
 */
@Getter
@Setter
public class SSRDataResponse extends Response {
    private List<SSRData> ssrdata = new ArrayList<>();

    @Getter
    @Setter
    public static class SSRData {
        @JsonProperty("PALCALRequired")
        private Boolean PALCALRequired;
        private String code;
        private Boolean isActive;
        private Boolean isTsandCsMandatory;
        private List<LocalizedValue> localizedDescriptions = new ArrayList<>();
        private List<LocalizedValue> localizedNames = new ArrayList<>();
    }

}