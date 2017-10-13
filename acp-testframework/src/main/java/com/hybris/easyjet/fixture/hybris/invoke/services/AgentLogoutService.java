package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.hybris.asserters.LogoutAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.LogoutResponse;
import org.junit.Assert;

/**
 * Created by giuseppedimartino on 13/02/17.
 */
public class AgentLogoutService extends HybrisService {

    private LogoutResponse logoutResponse;

    /**
     * a service comprises a request and an endpoint
     *
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    protected AgentLogoutService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public IResponse getResponse() {
        Assert.assertNull("The message body was populated but the service reported a " + restResponse.getStatusCode() + " " + restResponse.getStatusLine(), restResponse);
        return logoutResponse;
    }

    @Override
    public LogoutAssertion assertThat() {
        return new LogoutAssertion(logoutResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(logoutResponse);
    }

    @Override
    protected void mapResponse() {
        logoutResponse = restResponse.as(LogoutResponse.class);
    }
}
