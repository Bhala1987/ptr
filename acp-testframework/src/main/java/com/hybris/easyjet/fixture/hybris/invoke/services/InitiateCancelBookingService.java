package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.fixture.hybris.asserters.InitiateCancelBookingAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.InitiateCancelBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.InitiateCancelBookingResponse;

/**
 * Service for managing the request response from the initiate cancel booking endpoint.
 *
 * @author Joshua Curtis <j.curtis@reply.com>
 */
public class InitiateCancelBookingService extends HybrisService {
    private InitiateCancelBookingResponse initiateCancelBookingResponse;

    private FeesAndTaxesDao feesAndTaxesDao;

    InitiateCancelBookingService(
        InitiateCancelBookingRequest request,
        String endpoint,
        FeesAndTaxesDao feesAndTaxesDao
    ) {
        super(request, endpoint);

        this.feesAndTaxesDao = feesAndTaxesDao;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(initiateCancelBookingResponse);
    }

    @Override
    public InitiateCancelBookingAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new InitiateCancelBookingAssertion(initiateCancelBookingResponse, feesAndTaxesDao);
    }

    @Override
    protected void mapResponse() {
        initiateCancelBookingResponse = restResponse.as(InitiateCancelBookingResponse.class);
    }

    @Override
    public InitiateCancelBookingResponse getResponse() {
        return initiateCancelBookingResponse;
    }
}
