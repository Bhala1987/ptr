package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetReasonForTravelRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.SetReasonForTravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.SET_REASON_FOR_TRAVEL;

/**
 * Created by Niyi Falade on 04/08/17.
 */
@Component
public class SetAReasonHelper {

    @Autowired
    private SerenityFacade testData;

    private HybrisServiceFactory hybrisServiceFactory;
    private SetReasonForTravelService setReasonForTravelService;

    @Autowired
    public SetAReasonHelper(HybrisServiceFactory serviceFactory) {
        this.hybrisServiceFactory = serviceFactory;
    }

    public void setReasonForTravel(String basketID, String bookingReason) {

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketID).path(SET_REASON_FOR_TRAVEL).build();
        SetReasonForTravelRequestBody setReasonForTravelRequestBody = SetReasonForTravelRequestBody.builder().bookingReason(bookingReason).build();
        setReasonForTravelService = hybrisServiceFactory.setReasonForTravelService(new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, setReasonForTravelRequestBody));
        setReasonForTravelService.invoke();
    }

    public SetReasonForTravelService getSetReasonForTravelService() {
        return setReasonForTravelService;
    }
}
