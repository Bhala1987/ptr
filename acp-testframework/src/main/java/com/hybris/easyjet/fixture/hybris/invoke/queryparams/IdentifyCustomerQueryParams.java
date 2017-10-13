package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dwebb on 12/5/2016.
 */
@Builder
public class IdentifyCustomerQueryParams extends QueryParameters {

    private String title;
    private String firstname;
    private String lastname;
    private String email;
    private String ejcard;
    private String flightclubnumber;
    private String postcode;
    private String countryisocode;
    private String country;
    private String sortfield;
    private String order;

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(title)) {
            queryParams.put("title", title);
        }
        if (isPopulated(firstname)) {
            if (firstname.equals("{EMPTY}")) {
                firstname = "";
            }
            queryParams.put("first-name", firstname);
        }
        if (isPopulated(lastname)) {
            queryParams.put("last-name", lastname);
        }
        if (isPopulated(email)) {
            queryParams.put("email", email);
        }
        if (isPopulated(ejcard)) {
            queryParams.put("ejcard-number", ejcard);
        }
        if (isPopulated(flightclubnumber)) {
            queryParams.put("flightclub-number", flightclubnumber);
        }
        if (isPopulated(postcode)) {
            queryParams.put("postcode", postcode);
        }
        if (isPopulated(countryisocode)) {
            queryParams.put("country-iso-type", countryisocode);
        }
        if (isPopulated(sortfield)) {
            queryParams.put("sort-field", sortfield);
        }
        if (isPopulated(order)) {
            queryParams.put("sort-order", order);
        }
        if (isPopulated(country)) {
            queryParams.put("country", country);
        }

        return queryParams;
    }

}
