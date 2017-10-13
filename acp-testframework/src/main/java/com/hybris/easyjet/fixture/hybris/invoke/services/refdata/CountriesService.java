package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.CountriesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.CountriesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class CountriesService extends HybrisService implements IService {

    private CountriesResponse countriesResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public CountriesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public CountriesAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new CountriesAssertion(countriesResponse);
    }

    @Override
    public CountriesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return countriesResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(countriesResponse.getCountries());
    }

    @Override
    protected void mapResponse() {
        countriesResponse = restResponse.as(CountriesResponse.class);
    }
}
