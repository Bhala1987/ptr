package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.DeleteCommentOnBookingAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.DeleteCommentOnBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * @author Joshua Curtis <j.curtis@reply.com>
 */
public class DeleteCommentOnBookingService extends HybrisService implements IService{
    private DeleteCommentOnBookingResponse deleteCommentOnBookingResponse;

    public DeleteCommentOnBookingService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public DeleteCommentOnBookingAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new DeleteCommentOnBookingAssertion(deleteCommentOnBookingResponse);
    }

    @Override
    public DeleteCommentOnBookingResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return deleteCommentOnBookingResponse;
    }

    /**
     * We need to test for a 403 response too.
     */
    @Override
    protected void assertThatServiceCallWasSuccessful() {
        if (restResponse.getStatusCode() == 200 || restResponse.getStatusCode() == 403) {
            successful = true;
        }

        super.assertThatServiceCallWasSuccessful();
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(deleteCommentOnBookingResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        deleteCommentOnBookingResponse = restResponse.as(DeleteCommentOnBookingResponse.class);
    }
}
