package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.MarketGroupsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.MarketGroupsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by daniel on 26/11/2016.
 */
public class MarketGroupsService extends HybrisService implements IService {

    private MarketGroupsResponse marketGroupsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public MarketGroupsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public MarketGroupsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new MarketGroupsAssertion(marketGroupsResponse);
    }

    @Override
    public MarketGroupsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return marketGroupsResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(marketGroupsResponse.getMarketGroups());
    }

    @Override
    protected void mapResponse() {
        marketGroupsResponse = restResponse.as(MarketGroupsResponse.class);
    }
}
