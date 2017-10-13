package com.hybris.easyjet.fixture.hybris.invoke.services.eventmessagecreation;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.asserters.GenerateMessageAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.EventMessage.EventMessageResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

public class EventMessageService extends HybrisService {


    private EventMessageResponse eventMessageResponse;

    public EventMessageService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(eventMessageResponse);
    }

    @Override
    protected void mapResponse() {
        eventMessageResponse = restResponse.as(EventMessageResponse.class);
    }

    @Override
    public void invoke() {
        super.invoke();
    }

    @Override
    public EventMessageResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return eventMessageResponse;
    }

    @Override
    public GenerateMessageAssertion assertThat() {
        return new GenerateMessageAssertion(eventMessageResponse);
    }

}
