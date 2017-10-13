package com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

public class StaffMemberResponse extends AbstractConfirmation<StaffMemberResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends UpdateConfirmationResponse.OperationConfirmation {
        private String employeeEmail;
        private String employeeId;
        private Boolean isEligible;
    }

}