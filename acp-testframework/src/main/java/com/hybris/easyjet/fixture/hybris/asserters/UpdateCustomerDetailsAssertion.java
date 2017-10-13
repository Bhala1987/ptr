package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.UpdateConfirmationResponse;

import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.UpdateCustomerPreferencesFactory.*;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by giuseppedimartino on 17/02/17.
 */
public class UpdateCustomerDetailsAssertion extends Assertion<UpdateCustomerDetailsAssertion, UpdateConfirmationResponse> {

    public UpdateCustomerDetailsAssertion(UpdateConfirmationResponse updateConfirmationResponse) {

        this.response = updateConfirmationResponse;

    }

    public UpdateCustomerDetailsAssertion customerUpdated(String customerId) {
        assertThat(response.getConfirmation().getCustomerId()).isEqualTo(customerId);
        return this;
    }

    public UpdateCustomerDetailsAssertion fullPreferencesAreUpdated(CustomerProfileResponse aProfile) {
        ancillaryPreferencesAreUpdated(aProfile)
                .travelPreferencesAreUpdated(aProfile)
                .communicationPreferencesAreUpdated(aProfile);
        return this;
    }

    public UpdateCustomerDetailsAssertion ancillaryPreferencesAreUpdated(CustomerProfileResponse aProfile) {
        CustomerProfileResponse.AncillaryPreferences actualAncillaryPrefs = aProfile.getCustomer().getAdvancedProfile().getAncillaryPreferences();

        assertThat(actualAncillaryPrefs)
                .isEqualToIgnoringGivenFields(getAncillaryRequestBody().getAncillaryPreferences(), "additionalProperties", "additionalInformations");
        return this;
    }

    public UpdateCustomerDetailsAssertion communicationPreferencesAreUpdated(CustomerProfileResponse aProfile) {
        CustomerProfileResponse.CommunicationPreferences actualCommunicationPrefs = aProfile.getCustomer().getAdvancedProfile().getCommunicationPreferences();

        assertThat(actualCommunicationPrefs)
                .isEqualToIgnoringGivenFields(getCommunicationRequestBody().getCommunicationPreferences(), "additionalProperties", "additionalInformations");
        return this;
    }

    public UpdateCustomerDetailsAssertion travelPreferencesAreUpdated(CustomerProfileResponse aProfile) {
        CustomerProfileResponse.TravelPreferences actualTravelPrefs = aProfile.getCustomer().getAdvancedProfile().getTravelPreferences();

        assertThat(actualTravelPrefs)
                .isEqualToIgnoringGivenFields(getTravelRequestBody().getTravelPreferences(), "additionalProperties", "additionalInformations");
        return this;
    }


    public UpdateCustomerDetailsAssertion customerProfileIsNotUpdated(CustomerProfileResponse response) {
        assertThat(response.getCustomer().getAdvancedProfile().getAncillaryPreferences().getSeatNumber()).isNull();
        assertThat(response.getCustomer().getAdvancedProfile().getTravelPreferences().getPreferredAirports()).isEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getCommunicationPreferences().getContactMethods()).isEmpty();
        return this;
    }
}
