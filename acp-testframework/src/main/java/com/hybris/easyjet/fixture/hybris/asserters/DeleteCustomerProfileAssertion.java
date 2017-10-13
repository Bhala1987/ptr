package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.DeleteCustomerProfileResponse;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by robertadigiorgio on 28/02/2017.
 */
public class DeleteCustomerProfileAssertion extends Assertion<DeleteCustomerProfileAssertion, DeleteCustomerProfileResponse> {

    public DeleteCustomerProfileAssertion(DeleteCustomerProfileResponse deleteCustomerProfileResponse) {

        this.response = deleteCustomerProfileResponse;
    }

    public DeleteCustomerProfileAssertion fieldIsEmpty(Integer row) {

        assertThat(row).isEqualTo(0);
        return this;
    }

    public DeleteCustomerProfileAssertion fieldIsEmpty(String row) {

        if (row == null) {
            assertThat(row).isEqualTo(null);
        } else {
            assertThat(row).isEqualTo("0");
        }
        return this;
    }

    public DeleteCustomerProfileAssertion statusOfCustomerIsDeleted(String status) {

        assertThat(status).isEqualTo("DELETED");
        return this;
    }
}

