package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AddPassengerToFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddPassengerToFlightRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.AddPassengerToFlightService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.ADD_PASSENGER_TO_FLIGHT;

/**
 * Created by Bhala on 09/10/2017.
 */
@Component
public class AddpassengerToFlightHelper {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private AddPassengerToFlightService addPassengerToFlightService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private AddPassengerToFlightRequestBody.AddPassengerToFlightRequestBodyBuilder addPassengerToFlightRequestBody;
    private String passengerType;

    public void addPassengerToFLight(String basketId, String fareType, List<String> flightKeys) {

        basketPathParams = BasketPathParams.builder()
                .basketId(basketId)
                .path(ADD_PASSENGER_TO_FLIGHT);

        flightKeys = new ArrayList<>(flightKeys);

        addPassengerToFlightRequestBody = AddPassengerToFlightRequestBody.builder()
                .bundleCode(fareType)
                .flightKeys(flightKeys)
                .passengerType(passengerType);

        addPassengerToFlightService = serviceFactory.getAddPassengerToFlight(new AddPassengerToFlightRequest(HybrisHeaders.getValid(testData.getData(CHANNEL)).build(), basketPathParams.build(), addPassengerToFlightRequestBody.build()));
        testData.setData(SERVICE, addPassengerToFlightService);
        addPassengerToFlightService.invoke();
        testData.setData(SERVICE, addPassengerToFlightService);
    }

}
