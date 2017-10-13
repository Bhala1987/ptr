package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.CurrenciesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.CurrenciesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class CurrenciesService extends HybrisService implements IService {

    private CurrenciesResponse currenciesResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public CurrenciesService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public CurrenciesAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new CurrenciesAssertion(currenciesResponse);
    }

    @Override
    public CurrenciesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return currenciesResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(currenciesResponse.getCurrencies());
    }

    @Override
    protected void mapResponse() {
        currenciesResponse = restResponse.as(CurrenciesResponse.class);
    }
}
