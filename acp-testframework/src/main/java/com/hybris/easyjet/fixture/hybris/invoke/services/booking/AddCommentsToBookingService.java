package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AddCommentToBookingAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.AddCommentToBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by rajakm on 05/05/2017.
 */
public class AddCommentsToBookingService extends HybrisService implements IService{
    private AddCommentToBookingResponse addCommentToBookingResponse;
    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */

    public AddCommentsToBookingService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public AddCommentToBookingAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AddCommentToBookingAssertion(addCommentToBookingResponse);
    }

    @Override
    public AddCommentToBookingResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return addCommentToBookingResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(addCommentToBookingResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        addCommentToBookingResponse = restResponse.as(AddCommentToBookingResponse.class);
    }
}
