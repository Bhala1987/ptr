package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.hybris.asserters.BulkTransferReasonsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.GetBulkTransferReasonsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.BulkTransferReasonResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;


public class BulkTransferReasonsService extends HybrisService {

    private BulkTransferReasonResponse bulkTransferReasonResponse;

    public BulkTransferReasonsService(GetBulkTransferReasonsRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override

    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(bulkTransferReasonResponse.getBulkTransferReasons());
    }

    @Override
    protected void mapResponse() {
        bulkTransferReasonResponse = restResponse.as(BulkTransferReasonResponse.class);
    }

    @Override
    public BulkTransferReasonResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return bulkTransferReasonResponse;
    }

    @Override
    public BulkTransferReasonsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new BulkTransferReasonsAssertion(bulkTransferReasonResponse);
    }

}