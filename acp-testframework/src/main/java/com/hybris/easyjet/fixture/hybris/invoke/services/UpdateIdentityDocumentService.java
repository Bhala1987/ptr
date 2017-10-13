package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.UpdateIdentityDocumentAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger.operationconfirmation.UpdateIdentityDocumentResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
@Getter
@Setter
public class UpdateIdentityDocumentService extends HybrisService implements IService {

    private UpdateIdentityDocumentResponse updateIdentityDocumentResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected UpdateIdentityDocumentService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public UpdateIdentityDocumentAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new UpdateIdentityDocumentAssertion(updateIdentityDocumentResponse);
    }


    @Override
    public UpdateIdentityDocumentResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return updateIdentityDocumentResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(updateIdentityDocumentResponse.getUpdateConfirmation());
    }

    @Override
    protected void mapResponse() {
        updateIdentityDocumentResponse = restResponse.as(UpdateIdentityDocumentResponse.class);
    }

}