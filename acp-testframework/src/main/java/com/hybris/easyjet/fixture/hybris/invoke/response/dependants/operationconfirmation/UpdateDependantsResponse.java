package com.hybris.easyjet.fixture.hybris.invoke.response.dependants.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by markphipps on 23/03/2017.
 */
public class UpdateDependantsResponse extends AbstractConfirmation<UpdateDependantsResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private String customerId;
        private String passengerId;
    }

}