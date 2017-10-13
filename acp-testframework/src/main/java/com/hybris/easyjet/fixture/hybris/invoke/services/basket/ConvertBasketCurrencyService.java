package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.ConvertBasketCurrencyAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppedimartino on 27/03/17.
 */
public class ConvertBasketCurrencyService extends HybrisService implements IService {

    private BasketConfirmationResponse convertBasketCurrencyResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public ConvertBasketCurrencyService (IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return convertBasketCurrencyResponse;
    }

    @Override
    public ConvertBasketCurrencyAssertion assertThat() {
        return new ConvertBasketCurrencyAssertion(convertBasketCurrencyResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(convertBasketCurrencyResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        convertBasketCurrencyResponse = restResponse.as(BasketConfirmationResponse.class);
    }
}
