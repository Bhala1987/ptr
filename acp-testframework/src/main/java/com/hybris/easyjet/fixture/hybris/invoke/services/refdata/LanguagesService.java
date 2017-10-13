package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.LanguagesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.LanguagesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class LanguagesService extends HybrisService implements IService {

    private LanguagesResponse languagesResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public LanguagesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public LanguagesAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new LanguagesAssertion(languagesResponse);
    }

    @Override
    public LanguagesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return languagesResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(languagesResponse.getLanguages());
    }

    @Override
    protected void mapResponse() {
        languagesResponse = restResponse.as(LanguagesResponse.class);
    }
}
