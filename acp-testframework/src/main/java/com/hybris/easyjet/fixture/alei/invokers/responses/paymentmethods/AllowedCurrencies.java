package com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by tejaldudhale on 24/04/2017.
 */
public class AllowedCurrencies {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "currency"
    })

        @JsonProperty("currency")
        private List<Currency> currency = null;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("currency")
        public List<Currency> getCurrency() {
            return currency;
        }

        @JsonProperty("currency")
        public void setCurrency(List<Currency> currency) {
            this.currency = currency;
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
