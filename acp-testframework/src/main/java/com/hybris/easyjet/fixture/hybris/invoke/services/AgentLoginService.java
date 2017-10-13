package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AgentLoginAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.AgentLoginResponse;

/**
 * Created by rajakm on 08/05/2017.
 */
public class AgentLoginService extends HybrisService implements IService {

    private AgentLoginResponse agentLoginResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected AgentLoginService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public AgentLoginAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AgentLoginAssertion(agentLoginResponse);
    }


    @Override
    public AgentLoginResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        agentLoginResponse.getAgentAuthenticationConfirmation().getAuthentication().getAccessToken();
        return agentLoginResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(agentLoginResponse.getAgentAuthenticationConfirmation());
    }

    @Override
    protected void mapResponse() {
        agentLoginResponse = restResponse.as(AgentLoginResponse.class);
    }
}


