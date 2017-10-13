package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "address",
        "emailAddress",
        "phone",
        "openingTime",
        "closingTime",
        "faxNumber",
        "stationCode",
        "stationName"
})
public class CheckInStation {

    @JsonProperty("address")
    private Address address;
    @JsonProperty("emailAddress")
    private String emailAddress;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("openingTime")
    private String openingTime;
    @JsonProperty("closingTime")
    private String closingTime;
    @JsonProperty("faxNumber")
    private String faxNumber;
    @JsonProperty("stationCode")
    private String stationCode;
    @JsonProperty("stationName")
    private String stationName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(Address address) {
        this.address = address;
    }

    @JsonProperty("emailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }

    @JsonProperty("emailAddress")
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    @JsonProperty("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("openingTime")
    public String getOpeningTime() {
        return openingTime;
    }

    @JsonProperty("openingTime")
    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    @JsonProperty("closingTime")
    public String getClosingTime() {
        return closingTime;
    }

    @JsonProperty("closingTime")
    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    @JsonProperty("faxNumber")
    public String getFaxNumber() {
        return faxNumber;
    }

    @JsonProperty("faxNumber")
    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    @JsonProperty("stationCode")
    public String getStationCode() {
        return stationCode;
    }

    @JsonProperty("stationCode")
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    @JsonProperty("stationName")
    public String getStationName() {
        return stationName;
    }

    @JsonProperty("stationName")
    public void setStationName(String stationName) {
        this.stationName = stationName;
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
