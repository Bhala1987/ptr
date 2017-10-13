package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.alei.invokers.ALHeaders;
import com.hybris.easyjet.fixture.alei.invokers.pathparams.InventoryPathParams;
import com.hybris.easyjet.fixture.alei.invokers.requestbodies.AllocateInventoryRequestBody;
import com.hybris.easyjet.fixture.alei.invokers.requests.InventoryRequest;
import com.hybris.easyjet.fixture.alei.invokers.services.factories.ALServiceFactory;
import com.hybris.easyjet.fixture.alei.invokers.services.impl.ALInventoryService;
import com.hybris.easyjet.fixture.hybris.helpers.ALInventoryManagementHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import cucumber.api.java.en.But;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;
import static com.hybris.easyjet.fixture.alei.invokers.responses.Errors.Data;

//import com.hybris.easyjet.fixture.hybris.helpers.ALInventoryManagementHelper;

/**
 * Created by giuseppedimartino on 24/04/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class ALInventoryManagement {

    @Autowired
    FlightHelper flightHelper;
    @Autowired
    ALInventoryManagementHelper alInventoryManagementHelper;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private ALServiceFactory serviceFactory;
    @Autowired
    private BasketHelper basketHelper;

    private AllocateInventoryRequestBody getBody(double price, int quantity) {
        return AllocateInventoryRequestBody.builder()
                .uniqueIdentifier(UUID.randomUUID().toString())
                .fares(
                        Lists.newArrayList(
                                AllocateInventoryRequestBody.Fare.builder()
                                        .flightKey(testData.getActualFlightKey())
                                        .fareType(testData.getActualFareType().toUpperCase())
                                        .baseCurrencyCode("GBP")
                                        .requestedPrices(
                                                Lists.newArrayList(
                                                        AllocateInventoryRequestBody.RequestedPrice.builder()
                                                                .passengerType("ADULT")
                                                                .price(price)
                                                                .build()))
                                        .passengerMix(
                                                Lists.newArrayList(
                                                        AllocateInventoryRequestBody.Passenger.builder()
                                                                .passengerType("ADULT")
                                                                .numberRequired(quantity)
                                                                .build()))
                                        .numberOfInfants(0)
                                        .build()
                        )
                ).build();
    }

    @But("^there's no availability for that flight$")
    public void thereSNoAvailabilityForThatFlight() throws Throwable {
        alInventoryManagementHelper.allocateAllInventory((testData.getActualFlightKey()));
    }

    @But("^there is a Flight price change$")
    public void thereIsAFlightPriceChange() throws Throwable {

        InventoryPathParams pathParams = InventoryPathParams.builder().operation("allocations").build();

        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        List<FindFlightsResponse.FareType> standard = flightsService.getOutboundFlight().getFareTypes().stream().filter(fareType -> fareType.getFareTypeCode().equals("Standard")).collect(Collectors.toList());
        int quantity = standard.get(0).getFareClass().getAvailableUnits();

        ALInventoryService alInventoryService;

        String actualPrice = "1.0";

        boolean noAllocation = true;
        alInventoryService = serviceFactory.allocateInventory(new InventoryRequest(ALHeaders.getValid().build(), pathParams, getBody(Double.valueOf(actualPrice), quantity)));
        alInventoryService.invoke();

        boolean noAvailable = true;
        noAvailable = alInventoryService.getErrors().getErrors().stream().filter(error -> error.getCode().equals("SVC_100089_3001")).findFirst().isPresent();

        if (noAvailable == true)
        {
            do {
                quantity = Math.round(quantity / 10);
                alInventoryService = serviceFactory.allocateInventory(new InventoryRequest(ALHeaders.getValid().build(), pathParams, getBody(Double.valueOf(actualPrice), quantity)));
                alInventoryService.invoke();
                noAvailable = alInventoryService.getErrors().getErrors().stream().anyMatch(error -> error.getCode().equals("SVC_100089_3001"));
            } while (noAvailable);
        }

        actualPrice = alInventoryService.getErrors().getErrors().stream()
                .filter(error -> error.getCode().equals("SVC_100089_3005"))
                .findFirst()
                .map(error -> error.getAffectedData().stream()
                        .filter(affectedData -> affectedData.getDataName().equals("price"))
                        .findFirst()
                        .map(Data::getDataValue)
                        .orElse(null))
                .orElse(actualPrice);

            alInventoryService = serviceFactory.allocateInventory(new InventoryRequest(ALHeaders.getValid().build(), pathParams, getBody(Double.valueOf(actualPrice), quantity)));
            alInventoryService.invoke();
            do {
                alInventoryService = serviceFactory.allocateInventory(new InventoryRequest(ALHeaders.getValid().build(), pathParams, getBody(Double.valueOf(actualPrice), quantity)));
                alInventoryService.invoke();
                if(alInventoryService.getErrors()!=null) {
                    noAllocation = alInventoryService.getErrors().getErrors().stream()
                            .anyMatch(error -> error.getMessage().equals("Allocation request cannot be met because price has changed for one or more of the requested flights at the fare type specified."));
                }
            } while (noAllocation);
        alInventoryService = serviceFactory.allocateInventory(new InventoryRequest(ALHeaders.getValid().build(), pathParams, getBody(Double.valueOf(actualPrice), quantity)));
        alInventoryService.invoke();
    }

    @But("^there's no availability for the outbound flight$")
    public void thereSNoAvailabilityForTheOutboundFlight() throws Throwable {
        testData.setActualFlightKey(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey());
        thereSNoAvailabilityForThatFlight();
    }
}