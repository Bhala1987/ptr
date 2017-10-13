package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "driverName",
        "age",
        "countryOfResidence",
        "drivingLicenceNumber",
        "drivingLicenceIssuingCountry",
        "drivingLicenceExpiryDate"
})
public class PrimaryDriver {

    @JsonProperty("driverName")
    private DriverName driverName;
    @JsonProperty("age")
    private String age;
    @JsonProperty("countryOfResidence")
    private String countryOfResidence;
    @JsonProperty("drivingLicenceNumber")
    private String drivingLicenceNumber;
    @JsonProperty("drivingLicenceIssuingCountry")
    private String drivingLicenceIssuingCountry;
    @JsonProperty("drivingLicenceExpiryDate")
    private String drivingLicenceExpiryDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("driverName")
    public DriverName getDriverName() {
        return driverName;
    }

    @JsonProperty("driverName")
    public void setDriverName(DriverName driverName) {
        this.driverName = driverName;
    }

    @JsonProperty("age")
    public String getAge() {
        return age;
    }

    @JsonProperty("age")
    public void setAge(String age) {
        this.age = age;
    }

    @JsonProperty("countryOfResidence")
    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    @JsonProperty("countryOfResidence")
    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    @JsonProperty("drivingLicenceNumber")
    public String getDrivingLicenceNumber() {
        return drivingLicenceNumber;
    }

    @JsonProperty("drivingLicenceNumber")
    public void setDrivingLicenceNumber(String drivingLicenceNumber) {
        this.drivingLicenceNumber = drivingLicenceNumber;
    }

    @JsonProperty("drivingLicenceIssuingCountry")
    public String getDrivingLicenceIssuingCountry() {
        return drivingLicenceIssuingCountry;
    }

    @JsonProperty("drivingLicenceIssuingCountry")
    public void setDrivingLicenceIssuingCountry(String drivingLicenceIssuingCountry) {
        this.drivingLicenceIssuingCountry = drivingLicenceIssuingCountry;
    }

    @JsonProperty("drivingLicenceExpiryDate")
    public String getDrivingLicenceExpiryDate() {
        return drivingLicenceExpiryDate;
    }

    @JsonProperty("drivingLicenceExpiryDate")
    public void setDrivingLicenceExpiryDate(String drivingLicenceExpiryDate) {
        this.drivingLicenceExpiryDate = drivingLicenceExpiryDate;
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
