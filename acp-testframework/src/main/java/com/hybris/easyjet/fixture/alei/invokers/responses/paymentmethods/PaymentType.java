
package com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "allowedCurrencies",
        "bankNameRequired",
        "cardHolderNameRequired",
        "allowedDaysTillDeparture",
        "allowedMarketCountryCode",
        "cardNumberRequired",
        "code",
        "description",
        "isCreditCard",
        "issueNumberRequired",
        "issuers",
        "paymentMethod",
        "paymentMethodId",
        "securityCodeLength",
        "securityNumberRequired",
        "startDateRequired"
})
@Getter
@Setter
public class PaymentType {

    @JsonProperty("allowedCurrencies")
    private AllowedCurrencies allowedCurrencies;
    @JsonProperty("bankNameRequired")
    private String bankNameRequired;
    @JsonProperty("cardHolderNameRequired")
    private String cardHolderNameRequired;
    @JsonProperty("allowedDaysTillDeparture")
    private String allowedDaysTillDeparture;
    @JsonProperty("allowedMarketCountryCode")
    private String allowedMarketCountryCode;
    @JsonProperty("cardNumberRequired")
    private String cardNumberRequired;
    @JsonProperty("code")
    private String code;
    @JsonProperty("description")
    private String description;
    @JsonProperty("isCreditCard")
    private String isCreditCard;
    @JsonProperty("issueNumberRequired")
    private String issueNumberRequired;
    @JsonProperty("issuers")
    private List<Object> issuers = null;
    @JsonProperty("paymentMethod")
    private String paymentMethod;
    @JsonProperty("paymentMethodId")
    private String paymentMethodId;
    @JsonProperty("securityCodeLength")
    private String securityCodeLength;
    @JsonProperty("securityNumberRequired")
    private String securityNumberRequired;
    @JsonProperty("startDateRequired")
    private String startDateRequired;

}
