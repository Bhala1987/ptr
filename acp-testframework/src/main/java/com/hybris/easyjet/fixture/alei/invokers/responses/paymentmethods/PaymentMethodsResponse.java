
package com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "paymentMethods"
})
public class PaymentMethodsResponse {

    @JsonProperty("paymentMethods")
    private List<PaymentMethod> paymentMethods = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("paymentMethods")
    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    @JsonProperty("paymentMethods")
    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
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
