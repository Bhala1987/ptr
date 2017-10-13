package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.validationconfirmation.ValidateMembershipResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rajakm on 21/08/2017.
 */
public class ValidateMembershipAssertion extends Assertion<ValidateMembershipAssertion, ValidateMembershipResponse> {
    public ValidateMembershipAssertion(ValidateMembershipResponse validateMembershipResponse) {
        this.response = validateMembershipResponse;
    }

    public ValidateMembershipAssertion hasMembershipDetailsReturned(ValidateMembershipResponse actualDetails, Boolean expectedStatus, String expectedMembershipNumber, String expectedLastName){
        assertThat(actualDetails.getValidationConfirmation().getIsMembershipValid())
                .withFailMessage("The membershipvalid value is not correct")
                .isEqualTo(expectedStatus);

        assertThat(actualDetails.getValidationConfirmation().getMembershipNumber())
                .withFailMessage("The membership number is not as expected")
                .isEqualTo(expectedMembershipNumber);

        assertThat(actualDetails.getValidationConfirmation().getMembershipHolderName())
                .withFailMessage("The membership last name is not correct")
                .isEqualTo(expectedLastName);

        return this;
    }
}
