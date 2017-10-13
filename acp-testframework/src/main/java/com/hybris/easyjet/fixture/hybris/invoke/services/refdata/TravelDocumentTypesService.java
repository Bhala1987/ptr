package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.TravelDocumentTypesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.TravelDocumentTypesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppecioce on 08/02/2017.
 */
public class TravelDocumentTypesService extends HybrisService implements IService {

    private TravelDocumentTypesResponse travelDocuemntTypeResponse;

    public TravelDocumentTypesService(IRequest iRequest, String endpoint) {
        super(iRequest, endpoint);
    }

    @Override
    public TravelDocumentTypesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return travelDocuemntTypeResponse;
    }

    @Override
    public TravelDocumentTypesAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new TravelDocumentTypesAssertion(travelDocuemntTypeResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(travelDocuemntTypeResponse.getTravelDocumentTypes());
    }

    @Override
    protected void mapResponse() {
        travelDocuemntTypeResponse = restResponse.as(TravelDocumentTypesResponse.class);
    }
}
