package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "bundleCode",
        "code",
        "name",
        "description",
        "quantity",
        "pricing",
        "maxLength",
        "maxWidth",
        "maxHeight",
        "maxWeight"
})
public class CabinItem {

    @JsonProperty("bundleCode")
    private String bundleCode;
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("pricing")
    private Pricing pricing;
    @JsonProperty("maxLength")
    private Integer maxLength;
    @JsonProperty("maxWidth")
    private Integer maxWidth;
    @JsonProperty("maxHeight")
    private Integer maxHeight;
    @JsonProperty("maxWeight")
    private Integer maxWeight;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("bundleCode")
    public String getBundleCode() {
        return bundleCode;
    }

    @JsonProperty("bundleCode")
    public void setBundleCode(String bundleCode) {
        this.bundleCode = bundleCode;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("quantity")
    public Integer getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("pricing")
    public Pricing getPricing() {
        return pricing;
    }

    @JsonProperty("pricing")
    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    @JsonProperty("maxLength")
    public Integer getMaxLength() {
        return maxLength;
    }

    @JsonProperty("maxLength")
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @JsonProperty("maxWidth")
    public Integer getMaxWidth() {
        return maxWidth;
    }

    @JsonProperty("maxWidth")
    public void setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
    }

    @JsonProperty("maxHeight")
    public Integer getMaxHeight() {
        return maxHeight;
    }

    @JsonProperty("maxHeight")
    public void setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
    }

    @JsonProperty("maxWeight")
    public Integer getMaxWeight() {
        return maxWeight;
    }

    @JsonProperty("maxWeight")
    public void setMaxWeight(Integer maxWeight) {
        this.maxWeight = maxWeight;
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
