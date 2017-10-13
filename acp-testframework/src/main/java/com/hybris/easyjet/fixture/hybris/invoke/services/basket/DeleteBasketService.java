package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.WaitHelper;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by tejal on 01/08/2017.
 */
public class DeleteBasketService extends HybrisService implements IService {

    private WaitHelper waithelper;
    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public DeleteBasketService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {

    }
    @Override
    protected void mapResponse() {

    }
    @Override
    public void invoke() {
        super.invoke();
    }

    @Override
    public IResponse getResponse() {
        return null;
    }
    @Override
    public BasketsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new BasketsAssertion();
    }

}
