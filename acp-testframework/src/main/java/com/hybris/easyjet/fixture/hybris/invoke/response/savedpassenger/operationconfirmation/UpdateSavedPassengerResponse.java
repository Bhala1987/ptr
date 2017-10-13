package com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 20/02/2017.
 */
public class UpdateSavedPassengerResponse extends AbstractConfirmation<UpdateSavedPassengerResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private String customerId;
        private String passengerId;
        private String documentId;
    }

}