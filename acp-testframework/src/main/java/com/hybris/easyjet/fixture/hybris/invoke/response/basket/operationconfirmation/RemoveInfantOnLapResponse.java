package com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppedimartino on 20/06/17.
 */
public class RemoveInfantOnLapResponse extends AbstractConfirmation<RemoveInfantOnLapResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private String infantOnLapPassengerCode;
    }
}