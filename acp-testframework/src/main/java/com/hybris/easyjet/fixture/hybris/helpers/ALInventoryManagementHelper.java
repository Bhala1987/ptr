package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.alei.invokers.ALHeaders;
import com.hybris.easyjet.fixture.alei.invokers.pathparams.InventoryPathParams;
import com.hybris.easyjet.fixture.alei.invokers.requestbodies.AllocateInventoryRequestBody;
import com.hybris.easyjet.fixture.alei.invokers.requests.InventoryRequest;
import com.hybris.easyjet.fixture.alei.invokers.responses.Errors;
import com.hybris.easyjet.fixture.alei.invokers.responses.inventory.AllocationResponse;
import com.hybris.easyjet.fixture.alei.invokers.services.factories.ALServiceFactory;
import com.hybris.easyjet.fixture.alei.invokers.services.impl.ALInventoryService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by PTR tejal.
 */
@Component
public class ALInventoryManagementHelper {

    protected static final Logger LOG = LogManager.getLogger(ALInventoryManagementHelper.class);
    private ALHeaders headers;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private ALServiceFactory serviceFactory;

    public void allocateAllInventory(String flightKey) {
        testData.setData(SerenityFacade.DataKeys.FLIGHT_FULLY_ALLOCATED, flightKey);
        testData.setActualFlightKey(flightKey);
        InventoryPathParams pathParams = InventoryPathParams.builder().operation("allocations").build();

        ALInventoryService allocateRequest;
        String actualPrice = "1.0";
        int quantity = 1000;
        Map<String, Integer> allocatedFares = new HashMap<>();

        do {
            quantity /= 10;
            boolean moreAvailability = true;
            do {
                headers = ALHeaders.getValid().build();
                allocateRequest = serviceFactory.allocateInventory(
                        new InventoryRequest(headers, pathParams,
                                getBody(Double.valueOf(actualPrice), quantity)));

                allocateRequest.invoke();
                try {
                    actualPrice = allocateRequest.getErrors().getErrors().stream()
                            .filter(error -> "SVC_100089_3005".equals(error.getCode()))
                            .findFirst()
                            .map(error -> error.getAffectedData().stream()
                                    .filter(affectedData -> "price".equals(affectedData.getDataName()))
                                    .findFirst()
                                    .map(Errors.Data::getDataValue)
                                    .orElse(null))
                            .orElse(actualPrice);

                    moreAvailability = allocateRequest.getErrors().getErrors().stream()
                            .noneMatch(error -> "SVC_100089_3001".equals(error.getCode()));
                } catch (Exception exception) {
                    LOG.error(exception);
                    exception.getStackTrace();
                    // It was added only one flightkey so result will contain only one element in the result list
                    AllocationResponse result = (AllocationResponse) allocateRequest.getResponse();
                    allocatedFares = Stream.of(allocatedFares
                            , result.getResults().get(0).getAllocatedFares().stream()
                                    .collect(Collectors.toMap(
                                            AllocationResponse.AllocationFare::getFareClass
                                            , AllocationResponse.AllocationFare::getNumberAllocated)))
                            .flatMap(m -> m.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (allocatedFare1, allocatedFare2) -> allocatedFare1 + allocatedFare2));
                }
            } while (moreAvailability);

        } while (quantity > 1);

        if (testData.keyNotExist(SerenityFacade.DataKeys.ALLOCATED_FLIGHTS)) {
            testData.setData(SerenityFacade.DataKeys.ALLOCATED_FLIGHTS, new HashMap<String, Map<String, Integer>>());
        }
        HashMap<String, Map<String, Integer>> allocatedFlights = testData.getData(SerenityFacade.DataKeys.ALLOCATED_FLIGHTS);

        if (allocatedFlights != null) {
            allocatedFlights.put(testData.getActualFlightKey() + "-" + testData.getActualFareType(), allocatedFares);
        }
    }

    private AllocateInventoryRequestBody getBody(double price, int quantity) {
        String uid = headers.getXClientTransactionId();
        return AllocateInventoryRequestBody.builder()
                .uniqueIdentifier(uid)
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
}