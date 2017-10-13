package com.hybris.easyjet.fixture.hybris.invoke.services.refdata;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.SSRDataAssertions;
import com.hybris.easyjet.fixture.hybris.invoke.response.SSRDataResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppecioce on 09/02/2017.
 */
public class SSRDataService extends HybrisService implements IService {
    private SSRDataResponse ssrDataResponse;

    public SSRDataService(IRequest iRequest, String endpoint) {
        super(iRequest, endpoint);
    }

    @Override
    public SSRDataResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return ssrDataResponse;
    }

    @Override
    public SSRDataAssertions assertThat() {
        assertThatServiceCallWasSuccessful();
        return new SSRDataAssertions(ssrDataResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(ssrDataResponse.getSsrdata());
    }

    @Override
    protected void mapResponse() {
        ssrDataResponse = restResponse.as(SSRDataResponse.class);
    }
}
