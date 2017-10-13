package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.HoldItemsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.HoldItemsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppedimartino on 15/03/17.
 */
public class HoldItemsService extends HybrisService implements IService {

    private HoldItemsResponse holdItemsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public HoldItemsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IResponse getResponse() {

        assertThatServiceCallWasSuccessful();
        return holdItemsResponse;
    }

    @Override
    public HoldItemsAssertion assertThat() {

        return new HoldItemsAssertion(holdItemsResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {

        checkThatResponseBodyIsPopulated(holdItemsResponse.getHoldItems());
    }

    @Override
    protected void mapResponse() {
        holdItemsResponse = restResponse.as(HoldItemsResponse.class);
    }
}
