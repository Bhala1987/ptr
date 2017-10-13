package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.IdentifyCustomerResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by dwebb on 12/5/2016.
 * assertion wrapper for identify customer response object, provides reusable assertions to all tests
 */
public class IdentifyCustomerAssertion extends Assertion<IdentifyCustomerAssertion, IdentifyCustomerResponse> {

    public IdentifyCustomerAssertion(IdentifyCustomerResponse identifyCustomerResponse) {

        this.response = identifyCustomerResponse;
    }

    public IdentifyCustomerAssertion thatCorrectCustomerDetailsWereReturned(CustomerModel customer) {

        assertThat(response.getCustomers())
                .extracting("customerId")
                .contains(customer.getUid())
                .size().isGreaterThan(0);
        return this;
    }

    public IdentifyCustomerAssertion theListIsSortedByLastNameByDefault() {

        return thenDataReturnedIsSortedBy("lastName");
    }

    public IdentifyCustomerAssertion thenDataReturnedIsSortedBy(String sortField) {

        assertThat(response.getCustomers()).extracting(sortField).isSorted();
        return this;
    }

    public IdentifyCustomerAssertion thatVerifyTheReponseMessage(String strMessage) {

        //TODO check the real position of this
//        assertThat(response.getAdditionalProperties().toString().contains(strMessage));
        return this;
    }

    public IdentifyCustomerAssertion noResultIsFound(String errorCode) {

        assertThat(response.getAdditionalInformation().get(0).getCode().equals(errorCode))
                .isTrue();
        return this;
    }

}
