package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
@ToString
@Getter
@Setter
public class CurrencyConversionResponse extends Response implements IResponse {
    @JsonProperty("additionalInformation")
    private List<AdditionalInformation> additionalInformation;
    @JsonProperty("result")
    private Result result;

    @Getter
    @Setter
    public class Result {
        @JsonProperty("amount")
        private Double amount;
        @JsonProperty("fromCurrencyCode")
        private String fromCurrencyCode;
        @JsonProperty("toCurrencyCode")
        private String toCurrencyCode;
    }
}