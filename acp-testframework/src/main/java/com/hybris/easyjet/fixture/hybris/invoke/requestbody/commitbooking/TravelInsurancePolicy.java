package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "insuredAddress",
        "insuredDOB",
        "policyType",
        "coverageArea",
        "quotePackId",
        "premiumAmountPaid"
})
public class TravelInsurancePolicy {

    @JsonProperty("insuredAddress")
    private InsuredAddress insuredAddress;
    @JsonProperty("insuredDOB")
    private String insuredDOB;
    @JsonProperty("policyType")
    private String policyType;
    @JsonProperty("coverageArea")
    private String coverageArea;
    @JsonProperty("quotePackId")
    private String quotePackId;
    @JsonProperty("premiumAmountPaid")
    private PremiumAmountPaid premiumAmountPaid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("insuredAddress")
    public InsuredAddress getInsuredAddress() {
        return insuredAddress;
    }

    @JsonProperty("insuredAddress")
    public void setInsuredAddress(InsuredAddress insuredAddress) {
        this.insuredAddress = insuredAddress;
    }

    @JsonProperty("insuredDOB")
    public String getInsuredDOB() {
        return insuredDOB;
    }

    @JsonProperty("insuredDOB")
    public void setInsuredDOB(String insuredDOB) {
        this.insuredDOB = insuredDOB;
    }

    @JsonProperty("policyType")
    public String getPolicyType() {
        return policyType;
    }

    @JsonProperty("policyType")
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    @JsonProperty("coverageArea")
    public String getCoverageArea() {
        return coverageArea;
    }

    @JsonProperty("coverageArea")
    public void setCoverageArea(String coverageArea) {
        this.coverageArea = coverageArea;
    }

    @JsonProperty("quotePackId")
    public String getQuotePackId() {
        return quotePackId;
    }

    @JsonProperty("quotePackId")
    public void setQuotePackId(String quotePackId) {
        this.quotePackId = quotePackId;
    }

    @JsonProperty("premiumAmountPaid")
    public PremiumAmountPaid getPremiumAmountPaid() {
        return premiumAmountPaid;
    }

    @JsonProperty("premiumAmountPaid")
    public void setPremiumAmountPaid(PremiumAmountPaid premiumAmountPaid) {
        this.premiumAmountPaid = premiumAmountPaid;
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
