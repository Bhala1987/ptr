package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.AddInfantOnLapResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by vijayapalkayyam on 29/06/2017.
 */
public class AddInfantOnLapAssertion extends Assertion<AddInfantOnLapAssertion, AddInfantOnLapResponse> {

    private AddInfantOnLapResponse addInfantOnLapResponse;

    public AddInfantOnLapAssertion(AddInfantOnLapResponse addInfantOnLapResponse) {
        this.addInfantOnLapResponse = addInfantOnLapResponse;
    }

    public AddInfantOnLapAssertion containsPassengerCode(String code) {
        assertThat(addInfantOnLapResponse.getOperationConfirmation().getInfantOnLapPassengerCode()).contains(code);
        return this;
    }


}
