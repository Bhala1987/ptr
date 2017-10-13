package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "amount",
        "fromCurrencyCode",
        "toCurrencyCode"
})
@Builder
@Getter
public class CurrencyConversionRequestBody implements IRequestBody {
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("fromCurrencyCode")
    private String fromCurrencyCode;
    @JsonProperty("toCurrencyCode")
    private String toCurrencyCode;

}
