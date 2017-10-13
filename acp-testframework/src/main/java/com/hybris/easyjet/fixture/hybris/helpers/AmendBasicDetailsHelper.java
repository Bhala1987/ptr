package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.WaitHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.UpdatePassengerDetailsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendBasicDetailsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AmendBasicDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.AmendBasicDetailsService;
import org.fluttercode.datafactory.impl.DataFactory;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.config.constants.CommonConstants.*;
import static com.hybris.easyjet.fixture.WaitHelper.*;

/**
 * Created by tejaldudhale on 21/06/2017.
 */
@Component
public class AmendBasicDetailsHelper {
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    SerenityFacade testData;

    private DataFactory dataFactory = new DataFactory();
    private AmendBasicDetailsService amendBasicDetailsService;


    /**
     * invokeAmendBasicDetails, it invokes AmendBasicDetailsService
     *
     * @param passengerCode                The passenger to amend
     * @param amendBasicDetailsRequestBody The body of the request to send.
     */
    public void invokeAmendBasicDetails(String ordRefId, String passengerCode, AmendBasicDetailsRequestBody amendBasicDetailsRequestBody) {
        BasketPathParams basketPathParams = BasketPathParams.builder()
                .basketId(ordRefId)
                .passengerId(passengerCode)
                .path(BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS)
                .build();

        amendBasicDetailsService = serviceFactory.amendBasicDetails(
                new AmendBasicDetailsRequest(
                        HybrisHeaders.getValidWithToken(testData.getChannel(),testData.getAccessToken()).build(),
                        basketPathParams,
                        UpdatePassengerDetailsQueryParams.builder().allRelatedFlights("true").operationTypeUpdate("UPDATE").build(),
                        amendBasicDetailsRequestBody
                )
        );

        testData.setData(SERVICE, amendBasicDetailsService);

        int[] retryNoOfTimes = {10};
            pollingLoop().until(() -> {
                amendBasicDetailsService.invoke();
                retryNoOfTimes[0]--;
                return amendBasicDetailsService.getStatus() == 200 || retryNoOfTimes[0] == 0;
            });
    }

    public void invokeAmendBasicDetails(String firstname, String lastname) {
        BasketPathParams basketPathParams = BasketPathParams.builder()
                .basketId(testData.getAmendableBasket())
                .passengerId(testData.getPassengerId())
                .path(BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS)
                .build();
        Name.NameBuilder updatedName = Name.builder();
        if (!"nochange".equalsIgnoreCase(firstname)) {
            updatedName = updatedName.firstName(firstname);
        }
        if (!"nochange".equalsIgnoreCase(lastname)) {
            updatedName = updatedName.lastName(lastname);
        }
        AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder()
                .name(updatedName.build())
                .build();

        amendBasicDetailsService = serviceFactory.amendBasicDetails(
                new AmendBasicDetailsRequest(
                        HybrisHeaders.getValidWithToken(testData.getChannel(),testData.getAccessToken()).build(),
                        basketPathParams,
                        UpdatePassengerDetailsQueryParams.builder().allRelatedFlights("true").build(),
                        amendBasicDetailsRequestBody
                )
        );

        testData.setData("amendBasicDetailsService", amendBasicDetailsService);

        amendBasicDetailsService.invoke();
    }

    public com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name updateName(Map<String, String> fields, Basket.Passenger passenger, Name name) {
        String field;
        String updatedName;
        for (Map.Entry<String, String> entry : fields.entrySet())
            switch (entry.getKey()) {
                case FIRSTNAME:
                    field = passenger.getPassengerDetails().getName().getFirstName();
                    updatedName = updateName(field, entry.getValue());
                    name.setFirstName(updatedName);
                    break;
                case LASTNAME:
                    field = passenger.getPassengerDetails().getName().getLastName();
                    updatedName = updateName(field, entry.getValue());
                    name.setLastName(updatedName);
                    break;
                case TITLE:
                    name.setTitle(entry.getValue());
                    break;
                case MIDDLENAME:
                    field = passenger.getPassengerDetails().getName().getMiddleName();
                    updatedName = updateName(field, entry.getValue());
                    name.setMiddleName(updatedName);
                    break;
            }
        return name;
    }

    private String updateName(String field, String noOfChars) {
        switch (noOfChars) {
            case "NON_ALPHA":
                return "586$%t68965^**&%@34567897654";
            case "TOO_SHORT":
                return "A";
            default:
                // If the number of character that need replacing is less then to total then replace
                // them, otherwise create a whole new random string.
                Integer noOfCharsInt = Integer.parseInt(noOfChars);
                return noOfCharsInt <= field.length() ?
                        field.replace(field.substring(0, noOfCharsInt), dataFactory.getRandomChars(noOfCharsInt)) :
                        field.replace(field, dataFactory.getRandomChars(noOfCharsInt));
        }
    }

    public AmendBasicDetailsService getAmendBasicDetailsService() {
        return amendBasicDetailsService;
    }
}
