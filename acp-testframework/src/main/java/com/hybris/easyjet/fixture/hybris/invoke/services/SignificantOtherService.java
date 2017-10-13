package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.SignificantOtherAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.dependants.operationconfirmation.SignificantOtherResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by claudiodamico on 09/03/2017.
 */
@Getter
@Setter
public class SignificantOtherService extends HybrisService implements IService {

    private SignificantOtherResponse significantOtherResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected SignificantOtherService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public SignificantOtherResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return significantOtherResponse;
    }

    @Override
    public SignificantOtherAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new SignificantOtherAssertion(significantOtherResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(significantOtherResponse.getUpdateConfirmation());
    }

    @Override
    protected void mapResponse() {
        significantOtherResponse = restResponse.as(SignificantOtherResponse.class);
    }
}
