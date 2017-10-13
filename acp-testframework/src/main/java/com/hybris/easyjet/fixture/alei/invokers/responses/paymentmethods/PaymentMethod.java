
package com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "channelID",
    "paymentType"
})
public class PaymentMethod {

    @JsonProperty("channelID")
    private String channelID;
    @JsonProperty("paymentType")
    private List<PaymentType> paymentType = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("channelID")
    public String getChannelID() {
        return channelID;
    }

    @JsonProperty("channelID")
    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    @JsonProperty("paymentType")
    public List<PaymentType> getPaymentType() {
        return paymentType;
    }

    @JsonProperty("paymentType")
    public void setPaymentType(List<PaymentType> paymentType) {
        this.paymentType = paymentType;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
