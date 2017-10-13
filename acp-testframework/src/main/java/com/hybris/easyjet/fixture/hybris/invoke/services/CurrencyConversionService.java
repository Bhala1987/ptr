package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.CurrencyConversionAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.CurrencyConversionResponse;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
public class CurrencyConversionService extends HybrisService implements IService {

    private CurrencyConversionResponse currencyConversionResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public CurrencyConversionService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public CurrencyConversionAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new CurrencyConversionAssertion(currencyConversionResponse);
    }

    @Override
    public CurrencyConversionResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return currencyConversionResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(currencyConversionResponse.getResult().getAmount());
    }

    @Override
    protected void mapResponse() {
        currencyConversionResponse = restResponse.as(CurrencyConversionResponse.class);
    }

    public int getStatusCode() {
        return super.restResponse.getStatusCode();
    }
}

