package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.PreferencesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.PreferencesResponse;

/**
 * Created by PTR Scaffolder.
 */
public class PreferencesService extends HybrisService implements IService {

    private PreferencesResponse preferencesResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected PreferencesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public PreferencesAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new PreferencesAssertion(preferencesResponse);
    }

    @Override
    public PreferencesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return preferencesResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(preferencesResponse.getPreferencesReferenceData());
    }

    @Override
    protected void mapResponse() {
        preferencesResponse = restResponse.as(PreferencesResponse.class);
    }
}