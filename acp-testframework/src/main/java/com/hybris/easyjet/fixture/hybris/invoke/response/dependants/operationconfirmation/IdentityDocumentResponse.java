package com.hybris.easyjet.fixture.hybris.invoke.response.dependants.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by adevanna on 13/03/17.
 */
public class IdentityDocumentResponse extends AbstractConfirmation<IdentityDocumentResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends UpdateDependantsResponse.OperationConfirmation {
        private String documentId;
    }

}