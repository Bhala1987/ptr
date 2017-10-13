package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.SignificantOtherIdDocumentAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.dependants.operationconfirmation.IdentityDocumentResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by adevanna on 13/03/17.
 */
@Getter
@Setter
public class SignificantOtherIdDocumentService extends HybrisService implements IService {

    private IdentityDocumentResponse identityDocumentResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected SignificantOtherIdDocumentService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IdentityDocumentResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return identityDocumentResponse;
    }

    @Override
    public SignificantOtherIdDocumentAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new SignificantOtherIdDocumentAssertion(identityDocumentResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(identityDocumentResponse.getUpdateConfirmation());

    }

    @Override
    protected void mapResponse() {
        identityDocumentResponse = restResponse.as(IdentityDocumentResponse.class);
    }
}
