package com.hybris.easyjet.fixture.hybris.invoke.response.customer;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IdentifyCustomerResponse extends Response {
    private List<Customer> customers = new ArrayList<>();
    private List<AdditionalInformation> additionalInformation = new ArrayList<>();;

    @Getter
    @Setter
    public static class Customer {
        private String customerId;
        private String title;
        private String firstName;
        private String lastName;
        private String easyjetPlusCardNumber;
        private String flightClubNumber;
        private String email;
        private String postcode;
        private String countryName;
    }

}