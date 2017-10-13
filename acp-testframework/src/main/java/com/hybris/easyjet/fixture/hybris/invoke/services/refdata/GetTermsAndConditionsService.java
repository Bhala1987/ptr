package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.hybris.asserters.TermsAndConditionsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.GetTermsAndConditionsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetTermsAndConditionsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by rajakm on 12/09/2017.
 */
public class GetTermsAndConditionsService extends HybrisService {
    private GetTermsAndConditionsResponse getTermsAndConditionsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public GetTermsAndConditionsService(GetTermsAndConditionsRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public TermsAndConditionsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new TermsAndConditionsAssertion(getTermsAndConditionsResponse);
    }

    @Override
    public GetTermsAndConditionsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getTermsAndConditionsResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getTermsAndConditionsResponse.getTermsAndConditions());
    }

    @Override
    protected void mapResponse() {
        getTermsAndConditionsResponse = restResponse.as(GetTermsAndConditionsResponse.class);
    }

}
