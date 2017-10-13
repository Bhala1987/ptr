package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.GetAmendableBookingAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.createamendablebooking.GetAmendableBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import org.springframework.stereotype.Component;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
@Component
public class GetAmendableBookingService extends HybrisService implements IService {

    private GetAmendableBookingResponse getAmendableBookingResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public GetAmendableBookingService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public GetAmendableBookingAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new GetAmendableBookingAssertion(getAmendableBookingResponse);
    }

    @Override
    public GetAmendableBookingResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return getAmendableBookingResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(getAmendableBookingResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        getAmendableBookingResponse = restResponse.as(GetAmendableBookingResponse.class);
    }

    public int getStatusCode() {
        return super.restResponse.getStatusCode();
    }
}

