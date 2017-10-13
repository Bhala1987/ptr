package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TravelInsurance extends AbstractProductItem {
    private String passengerCode;
    private String travelType;
    private String originAirport;
    private String originAirportCountry;
    private String destinationAirport;
    private String destinationAirportCountry;
    private List<TravelInsurancePolicy> travelInsurancePolicies = new ArrayList<>();

    @Getter
    @Setter
    public static class TravelInsurancePolicy {
        private Address insuredAddress;
        private String insuredDOB;
        private String policyType;
        private String coverageArea;
        private String quotePackId;
        private Pricing premiumAmountPaid;
    }

}