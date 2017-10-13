package com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by vijayapalkayyam on 29/06/2017.
 */
@Getter
@Setter
public class AddInfantOnLapResponse extends AbstractConfirmation<AddInfantOnLapResponse.OperationConfirmation> {

    @Getter
    @Setter
    public class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private String infantOnLapPassengerCode;
        private String href;
    }

}
