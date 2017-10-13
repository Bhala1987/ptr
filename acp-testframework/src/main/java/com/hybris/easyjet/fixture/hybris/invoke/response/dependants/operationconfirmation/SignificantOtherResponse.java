package com.hybris.easyjet.fixture.hybris.invoke.response.dependants.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by claudiodamico on 09/03/2017.
 */
@Getter
@Setter
public class SignificantOtherResponse extends AbstractConfirmation<SignificantOtherResponse.OperationConfirmation> {
    private SignificantOtherResponse significantOtherResponse;

    @Getter
    @Setter
    public static class OperationConfirmation extends UpdateDependantsResponse.OperationConfirmation {
        private Integer remainingChanges;
        private String changesEndDate;
    }

}