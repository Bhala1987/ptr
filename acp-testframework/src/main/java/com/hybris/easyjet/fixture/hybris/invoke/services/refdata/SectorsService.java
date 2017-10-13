package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.asserters.SectorAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.SectorResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
public class SectorsService extends HybrisService {

    private SectorResponse sectorResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public SectorsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public SectorResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return sectorResponse;
    }

    @Override
    public SectorAssertion assertThat() {
        return new SectorAssertion(sectorResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(sectorResponse.getSectors());
    }

    @Override
    protected void mapResponse() {
        sectorResponse = restResponse.as(SectorResponse.class);
    }
}
