package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.database.hybris.dao.ProductDao;
import com.hybris.easyjet.database.hybris.models.ProductModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.HoldItemsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddHoldItemsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.BasketHoldItemsFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddHoldItemsToBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.HoldItemsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.HoldItemsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddHoldBagToBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddSportToBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.HoldItemsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_SERVICE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_STOCK_LEVEL_FLIGHTS;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.*;

/**
 * Created by giuseppecioce on 06/03/2017.
 */
@Component
public class BasketHoldItemsHelper {

    private static final Logger LOG = LogManager.getLogger(BasketHoldItemsHelper.class);

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private HoldItemsDao holdItemsDao;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private ProductDao productDao;

    public static final String TEN_KG_BAG = "10kgbag";
    private static final String HOLD_ITEM = "Hold Bag";
    private static final String TWENTY_KG_BAG = "20kgbag";
    private static final String THREE_KG_EXTRA = "3kgextraweight";
    private static final String INFANT = "infant";
    public static final String ADULT = "adult";
    private static final String CHILD = "child";
    private BasketHelper basketHelper;

    private AddSportToBasketService addSportToBasketService;
    private AddHoldBagToBasketService addHoldBagToBasketService;
    private AddHoldItemsRequestBody requestBody;

    private PricingHelper basketPrice = new PricingHelper();
    private int stockLevel;
    private int reservedAllocation;
    private BasketPathParams params;

    private int retrieveRandomItem;
    private String product;
    private String passengerType;
    private String passengerCode;
    private HoldItemsService holdItemsService;
    private HoldItemsResponse.HoldItems holdItems;
    private String index;


    @Autowired
    public BasketHoldItemsHelper(BasketHelper basketHelper, FlightHelper flightHelper, TravellerHelper travellerHelper) {
        this.basketHelper = basketHelper;
    }

    public HoldItemsResponse.HoldItems getHoldItems() {
        return holdItems;
    }

    public int getExcessWeightQuantity() {
        return requestBody.getExcessWeightQuantity();
    }

    public AddHoldBagToBasketService getAddHoldBagToBasketService() {
        return addHoldBagToBasketService;
    }

    public AddSportToBasketService getAddSportToBasketService() {
        return addSportToBasketService;
    }

    public AddHoldItemsRequestBody getRequestBody() {
        return requestBody;
    }

    public void addValidFlightToTheBasket(String channel, String bundle, String journey, String passengerMix, String currency) throws Throwable {
        if (passengerMix.contains(INFANT) && !passengerMix.contains(ADULT)) {
            passengerMix = "1 adult, " + passengerMix;//NOSONAR
        }
        testData.setChannel(channel);
        basketHelper.addFlightToBasket(passengerMix, testData.getOrigin(), testData.getDestination(), false, bundle, null);
        storeBasketPricing();
    }

    public void addValidFlightsToTheBasket(String channel, String bundle, String journey, String passengerMix, int quantity) throws Throwable {
        if (passengerMix.contains(INFANT) && !passengerMix.contains(ADULT)) {
            passengerMix = "1 adult, " + passengerMix;
        }
        List<AddFlightRequestBody> addFlights = new ArrayList<>();
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        List<FindFlightsResponse.Journey> outboundJourneys = flightsService.getOutboundJourneys();
        if (outboundJourneys.size() == 1) {
            quantity = 1;//NOSONAR
        }
        for (int index = 0; index < quantity; index++) {
            FindFlightsResponse.Flight flight1 = outboundJourneys.stream()
                    .filter(journey1 -> journey1.getFlights().get(0).getFlightKey().equalsIgnoreCase(testData.getFlightKey()))
                    .collect(Collectors.toList()).get(0).getFlights().get(0);
            addFlights.add(BasketHoldItemsFactory.aBasicAddFlightToBasket(flight1.getFlightKey(), flightsService.getResponse()
                    .getCurrency(), bundle, journey, passengerMix));
            testData.setFlightKey(flight1.getFlightKey());
        }
        basketHelper.addNumberOfFlightsToBasket(addFlights, channel);
    }

    private void storeBasketPricing() {
        Basket basketContext = basketHelper.getBasketService().getResponse().getBasket();
        basketPrice.setTotalAmountWithCreditCard(basketContext.getTotalAmountWithCreditCard());
        basketPrice.setTotalAmountWithDebitCard(basketContext.getTotalAmountWithDebitCard());
        basketPrice.setSubtotalAmountWithCreditCard(basketContext.getSubtotalAmountWithCreditCard());
        basketPrice.setSubtotalAmountWithDebitCard(basketContext.getSubtotalAmountWithDebitCard());
    }

    public PricingHelper getBasketPrice() {
        return basketPrice;
    }

    public void buildRequestToAddSportEquipment(String channel) throws EasyjetCompromisedException {
        params = BasketPathParams.builder().basketId(getBasketId()).path(SPORT_EQUIP).build();
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setFlightKey(getFlightKeyFromBasketResponse());
        requestBody.setProductCode(retrieveValidItemSport());
        requestBody.setPassengerCode(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
        requestBody.setQuantity(1);
        reservedAllocation = getActualReservedItem();
    }

    public void buildRequestToAddHoldBags(String channel) throws EasyjetCompromisedException {
        params = BasketPathParams.builder().basketId(getBasketId()).path(HOLD_BAG).build();
        requestBody = BasketHoldItemsFactory.aBasicHoldItems();
        requestBody.setFlightKey(getFlightKeyFromBasketResponse());
        requestBody.setProductCode(TWENTY_KG_BAG);
        //TODO Dev needs to fix this validation
        requestBody.setExcessWeightProductCode(THREE_KG_EXTRA);
        requestBody.setExcessWeightQuantity(1);
        product = TWENTY_KG_BAG;
        requestBody.setPassengerCode(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
        requestBody.setQuantity(1);
        if (testData.getData(SerenityFacade.DataKeys.QUANTITY) != null)
            requestBody.setQuantity(testData.getData(SerenityFacade.DataKeys.QUANTITY));
        reservedAllocation = getActualReservedItem();
    }

    public void buildRequestToAddExcessBags(String channel) {
        params = BasketPathParams.builder().basketId(getBasketId()).path(HOLD_BAG).build();
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setExcessWeightProductCode(THREE_KG_EXTRA);
        product = THREE_KG_EXTRA;
    }

    public void buildRequestToAddSportEquipment(String item, String channel) throws EasyjetCompromisedException {
        params = BasketPathParams.builder().basketId(getBasketId()).path(SPORT_EQUIP).build();
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setFlightKey(getFlightKeyFromBasketResponse());
        requestBody.setProductCode(retrieveValidItemSportBelongToGroup(item.contains("Large")));
        requestBody.setPassengerCode(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
        requestBody.setQuantity(1);
        reservedAllocation = getActualReservedItem();
    }

    public void buildRequestToAddHoldBags(String item, String channel) throws EasyjetCompromisedException {
        params = BasketPathParams.builder().basketId(getBasketId()).path(HOLD_BAG).build();
        requestBody = BasketHoldItemsFactory.aBasicHoldItems();
        requestBody.setFlightKey(getFlightKeyFromBasketResponse());
        requestBody.setProductCode(TWENTY_KG_BAG);
        //TODO Dev needs to fix this validation
        requestBody.setExcessWeightProductCode("");
        requestBody.setExcessWeightQuantity(0);
        if (item.contains("HoldBag")) {
            product = TWENTY_KG_BAG;
        }
        requestBody.setPassengerCode(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
        requestBody.setQuantity(1);

        reservedAllocation = getActualReservedItem();
    }

    public void buildRequestToAdd10kgHoldBags(String item) {
        params = BasketPathParams.builder().basketId(getBasketId()).path(HOLD_BAG).build();
        requestBody = BasketHoldItemsFactory.aBasicHoldItems();
        requestBody.setFlightKey(getFlightKeyFromBasketResponse());
        requestBody.setProductCode(TEN_KG_BAG);
        //TODO Dev needs to fix this validation
        requestBody.setExcessWeightProductCode("");
        requestBody.setExcessWeightQuantity(0);
        if (item.contains("HoldBag")) {
            product = TEN_KG_BAG;
        }
        requestBody.setPassengerCode(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
        requestBody.setQuantity(1);
    }

    public void removeFieldFromRequestBody(String field) {
        if ("basketCode".equalsIgnoreCase(field)) {
            params = BasketPathParams.builder().basketId("0").path(SPORT_EQUIP).build();
        }
        requestBody = BasketHoldItemsFactory.missingFieldHoldItemsSport(field, requestBody);
    }

    public void removeHoldBagServiceFieldFromRequestBody(String field) {
        if ("basketCode".equalsIgnoreCase(field)) {
            params = BasketPathParams.builder().basketId("0").path(HOLD_BAG).build();
        }

        requestBody = BasketHoldItemsFactory.missingFieldHoldItems(field, requestBody);
    }

    public void invokeServiceAddSportItems(String channel) {
        passengerCode = requestBody.getPassengerCode();
        addSportToBasketService = serviceFactory.getAddSportEquipmentToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(channel).build(), params, requestBody));
        addSportToBasketService.invoke();
    }

    public void invokeServiceAddHoldBags(String channel) {
        passengerCode = requestBody.getPassengerCode();
        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(channel).build(), params, requestBody));
        addHoldBagToBasketService.invoke();
    }

    private void addHoldItemProductToBasket(AddHoldItemsRequestBody holdItemsRequestBody, String productType, String channel) {
        addHoldItemProductToBasket(holdItemsRequestBody, productType, channel, getBasketId());
    }

    private void addHoldItemProductToBasket(AddHoldItemsRequestBody holdItemsRequestBody, String productType, String channel, String basketId) {
        storeBasketPricing();
        if (HOLD_ITEM.equalsIgnoreCase(productType)) {
            params = BasketPathParams.builder().basketId(basketId).path(HOLD_BAG).build();
        } else {
            params = BasketPathParams.builder().basketId(basketId).path(SPORT_EQUIP).build();
        }

        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, holdItemsRequestBody));
        addHoldBagToBasketService.invoke();
    }

    private void addExcessWeightToHoldItem(AddHoldItemsRequestBody holdItemsRequestBody, String productType, String index, String basketId) throws Throwable {
        params = BasketPathParams.builder().basketId(basketId).path(EXCESS_WEIGHT)
                .index(index)
                .build();
        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, holdItemsRequestBody, true));
        addHoldBagToBasketService.invoke();
        addHoldBagToBasketService.getResponse();
    }

    public void addProductToAllPassengersForSpecificFlight(String productType, String flightKey, int quantity) {
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setFlightKey(flightKey);
        requestBody.setProductCode(getProductCodeBasedOnType(productType, testData.getChannel()));
        requestBody.setQuantity(quantity);
        addHoldItemProductToBasket(requestBody, productType, testData.getChannel());
    }

    public void addProductToSpecificPassengersForSpecificFlight(String productType, String flightKey, String passengerCode) throws EasyjetCompromisedException {
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setFlightKey(flightKey);
        requestBody.setPassengerCode(passengerCode);
        requestBody.setQuantity(testData.getData(SerenityFacade.DataKeys.QUANTITY));
        requestBody.setProductCode(getProductCodeBasedOnType(productType, testData.getChannel()));
        reservedAllocation = getActualReservedItem();
        addHoldItemProductToBasket(requestBody, productType, testData.getChannel());
    }

    public void addExcessWeightToSpecificPassengersForSpecificFlight(String productType, String flightKey, String passengerCode, int excessWeightQuantity, int itemIndex, boolean isPriceChange) throws Throwable {
        addExcessWeightToSpecificPassengersForSpecificFlight(productType, flightKey, passengerCode, excessWeightQuantity, itemIndex, isPriceChange, getBasketId());
    }

    void addExcessWeightToSpecificPassengersForSpecificFlight(String productType, String flightKey, String passengerCode, int excessWeightQuantity, int itemIndex, boolean isPriceChange, String basketCode) throws Throwable {
        holdItems = getProductBasedOnType(productType, testData.getChannel());
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setFlightKey(getOuBoundFlightKeyBasedOnIndex(0));
        requestBody.setPassengerCode(passengerCode);
        requestBody.setExcessWeightQuantity(excessWeightQuantity);
        requestBody.setExcessWeightProductCode(holdItems.getProductCode());
        index = getIndex(itemIndex);
        if (isPriceChange) {
            requestBody.setExcessWeightPrice(holdItems.getPrices().get(0).getBasePrice() + 10);
        } else {
            requestBody.setExcessWeightPrice(holdItems.getPrices().get(0).getBasePrice());
        }
        addExcessWeightToHoldItem(requestBody, productType, index, basketCode);
    }

    private HoldItemsResponse.HoldItems getProductBasedOnType(String type, String channel) {
        switch (type) {
            case "Excess Weight":
                return getHoldItem(channel, "ExcessWeightProduct");
            case "1kg Excess Weight":
                return getHoldItem(channel, "ExcessWeightProduct", "1");
            default:
                return getHoldItem(channel, "HoldBagProduct");
        }
    }

    public void addExcessWeightToSpecificPassengersForSpecificFlightWithError(String productType, String flightKey, String passengerCode, int excessWeightQuantity, boolean isIndex) throws Throwable {
        addExcessWeightToSpecificPassengersForSpecificFlightWithError(productType, flightKey, passengerCode, excessWeightQuantity, isIndex, getBasketId());
    }

    void addExcessWeightToSpecificPassengersForSpecificFlightWithError(String productType, String flightKey, String passengerCode, int excessWeightQuantity, boolean isIndex, String basketCode) throws Throwable {
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setFlightKey(flightKey);
        requestBody.setPassengerCode(passengerCode);
        requestBody.setExcessWeightProductCode(getProductCodeBasedOnType(productType, testData.getChannel()));
        requestBody.setExcessWeightQuantity(excessWeightQuantity);
        requestBody.setExcessWeightPrice(23.0);
        if (isIndex) {
            params = BasketPathParams.builder().basketId(basketCode).path(EXCESS_WEIGHT)
                    .index(getIndex(1))
                    .build();
        } else {
            params = BasketPathParams.builder().basketId(basketCode).path(EXCESS_WEIGHT)
                    .index("1234")
                    .build();
        }

        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, requestBody, true));
        addHoldBagToBasketService.invoke();
    }

    public String getProductCodeBasedOnType(String type, String channel) {
        switch (type) {
            case HOLD_ITEM:
                return TWENTY_KG_BAG;
            //We can uncomment below once we have stock for 10kg  bag
            //getHoldItem(channel,"HoldBagProduct").getProductCode();
            case "Large Sporting Equipment":
                return "CanoeKayak";
            case "Small Sporting Equipment":
                return "GolfBag";
            case "Excess Weight":
                return THREE_KG_EXTRA;
            case "1kg Excess Weight":
                return "1kgextraweight";
            case "Sporting Fire Arm":
                return "SportingFirearm";
            default:
                return getHoldItem(channel, "HoldBagProduct").getProductCode();

        }
    }

    private HoldItemsResponse.HoldItems getHoldItem(String channel, String productType) {
        return getHoldItem(channel, productType, null);
    }

    private HoldItemsResponse.HoldItems getHoldItem(String channel, String productType, String weight) {
        HoldItemsQueryParams.HoldItemsQueryParamsBuilder queryParams = HoldItemsQueryParams.builder();
        queryParams.currency(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode());
        holdItemsService = serviceFactory.getHoldItems(new HoldItemsRequest(HybrisHeaders.getValid("Digital")
                .build(), queryParams.build()));
        holdItemsService.invoke();
        holdItems = ((HoldItemsResponse) holdItemsService.getResponse())
                .getHoldItems()
                .stream()
                .filter(f ->
                {
                    return f.getProductType().equals(productType) && (!channel.matches("PublicApi(.*)") || !TEN_KG_BAG.matches(f.getProductCode()));
                })
                .filter(item -> weight == null || item.getProductCode().contains(weight))
                .collect(Collectors.toList()).get(0);

        return holdItems;
    }

    public HoldItemsResponse.HoldItems getHoldItem(String productCode) {
        HoldItemsQueryParams.HoldItemsQueryParamsBuilder queryParams = HoldItemsQueryParams.builder();
        queryParams.currency(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode());
        holdItemsService = serviceFactory.getHoldItems(new HoldItemsRequest(HybrisHeaders.getValid(testData.getData(SerenityFacade.DataKeys.CHANNEL))
                .build(), queryParams.build()));
        holdItemsService.invoke();
        holdItems = ((HoldItemsResponse) holdItemsService.getResponse())
                .getHoldItems()
                .stream()
                .filter(f ->
                {
                    if (f.getProductCode().equals(requestBody.getProductCode())) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList()).get(0);

        return holdItems;
    }

    public void addProductWithExcessWeightAllPassengersForSpecificFlight(String productType, String flightKey, String channel, int excessWeightQuantity) throws EasyjetCompromisedException {
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setProductCode(getProductCodeBasedOnType(productType, channel));
        requestBody.setQuantity(testData.getData(SerenityFacade.DataKeys.QUANTITY));
        requestBody.setFlightKey(flightKey);
        requestBody.setExcessWeightProductCode(getHoldItemProductCode(channel, "ExcessWeightProduct"));
        requestBody.setExcessWeightQuantity(excessWeightQuantity);
        addHoldItemProductToBasket(requestBody, productType, channel);
    }

    public void addHoldItemToSpecificPassengerSpecificFlight(String productType, String flightKey, String passengerCode, String channel) {
        addHoldItemToSpecificPassengerSpecificFlight(productType, flightKey, passengerCode, channel, null);
    }

    void addHoldItemToSpecificPassengerSpecificFlight(String productType, String flightKey, String passengerCode, String channel, String basketId) {
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setProductCode(getProductCodeBasedOnType(productType, channel));
        requestBody.setFlightKey(flightKey);
        requestBody.setPassengerCode(passengerCode);
        if (basketId == null) {
            addHoldItemProductToBasket(requestBody, productType, channel);
        } else {
            addHoldItemProductToBasket(requestBody, productType, channel, basketId);
        }
    }

    public void addHoldItemWithError(String productType, String flightKey, String passengerCode, String channel, int quantity) throws Throwable {
        addHoldItemWithError(productType, flightKey, passengerCode, quantity,channel, getBasketId());
    }

    void addHoldItemWithError(String productType, String flightKey, String passengerCode, int quantity, String channel, String basketId) throws Throwable {
        if (productType.equalsIgnoreCase(HOLD_ITEM)) {
            params = BasketPathParams.builder().basketId(basketId).path(HOLD_BAG).build();
        } else {
            params = BasketPathParams.builder().basketId(basketId).path(SPORT_EQUIP).build();
        }
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setProductCode(getProductCodeBasedOnType(productType, channel));
        requestBody.setFlightKey(flightKey);
        requestBody.setQuantity(quantity);
        requestBody.setPassengerCode(passengerCode);
        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(channel).build(), params, requestBody));
        addHoldBagToBasketService.invoke();
    }

    public void addHoldItemWithError(String channel, String productType) throws Throwable {
        if (productType.equalsIgnoreCase(HOLD_ITEM)) {
            params = BasketPathParams.builder().basketId(getBasketId()).path(HOLD_BAG).build();
        } else {
            params = BasketPathParams.builder().basketId(getBasketId()).path(SPORT_EQUIP).build();
        }
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setProductCode(getProductCodeBasedOnType(productType, channel));
        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(channel).build(), params, requestBody));
        addHoldBagToBasketService.invoke();
    }

    public void addHoldItemWithErrorForAllPassengerSpecificFlight(String channel, String productType, String flightKey, boolean isOverride, int quantity) throws Throwable {
        if (productType.equalsIgnoreCase(HOLD_ITEM)){
            params = BasketPathParams.builder().basketId(getBasketId()).path(HOLD_BAG).build();
        } else {
            params = BasketPathParams.builder().basketId(getBasketId()).path(SPORT_EQUIP).build();
        }
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setProductCode(getProductCodeBasedOnType(productType, channel));
        requestBody.setFlightKey(flightKey);
        requestBody.setOverride(isOverride);
        requestBody.setQuantity(quantity);
        addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(channel).build(), params, requestBody));
        addHoldBagToBasketService.invoke();
    }

    public void addHoldItemWithExcessWeightToAllFlightsAllPassenger(String channel, String productType, int excessWeightQuantity) throws EasyjetCompromisedException {
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setProductCode(getHoldItemProductCode(channel, "HoldBagProduct"));
        requestBody.setExcessWeightProductCode(getHoldItemProductCode(channel, "ExcessWeightProduct"));
        requestBody.setExcessWeightQuantity(excessWeightQuantity);
        addHoldItemProductToBasket(requestBody, productType, channel);
    }

    public void addHoldItemToAllFlightsForSpecificPassenger(String productType, String passengerCode, String channel) {
        this.passengerCode = passengerCode;
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setPassengerCode(passengerCode);
        requestBody.setProductCode(getProductCodeBasedOnType(productType, channel));
        addHoldItemProductToBasket(requestBody, productType, channel);
    }

    public void addHoldItemWithExcessWeightToAllFlightsForSpecificPassenger(String passengerCode, String channel, String productType, int excessWeightQuantity) {
        this.passengerCode = passengerCode;
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setPassengerCode(passengerCode);
        requestBody.setQuantity(testData.getData(SerenityFacade.DataKeys.QUANTITY));
        requestBody.setProductCode(getProductCodeBasedOnType(productType, channel));
        requestBody.setExcessWeightProductCode(THREE_KG_EXTRA);
        requestBody.setExcessWeightQuantity(excessWeightQuantity);
        addHoldItemProductToBasket(requestBody, productType, channel);
    }

    public void addHoldItemToAllFlightsAllPassenger(String channel, String productType) throws EasyjetCompromisedException {
        requestBody = BasketHoldItemsFactory.aHoldItems();
        requestBody.setProductCode(getProductCodeBasedOnType(productType, channel));
        reservedAllocation = getActualReservedItem();
        addHoldItemProductToBasket(requestBody, productType, channel);
    }

    public void createCartWithMultipleFlighsAndTraveller(String channel) throws Throwable {
        String bundleType = "Standard";
        String journey = "SINGLE";
        String currency = "GBP";
        addValidFlightToTheBasket(channel, bundleType, journey, "2 adult, 1 infant OL", currency);
        addValidFlightToTheBasket(channel, bundleType, journey, "1 adult", currency);

    }

    public String getHoldItemProductCode(String channel, String productType) throws EasyjetCompromisedException {
        HoldItemsQueryParams.HoldItemsQueryParamsBuilder queryParams = HoldItemsQueryParams.builder();

        //Check the basketResponse
        if (Objects.nonNull(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode())) {
            queryParams.currency(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode());
        } else {
            throw new EasyjetCompromisedException("Could not get currency from basket, as it is empty. Check stock levels.");
        }

        queryParams.currency(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode());
        holdItemsService = serviceFactory.getHoldItems(new HoldItemsRequest(HybrisHeaders.getValid("Digital")
                .build(), queryParams.build()));
        holdItemsService.invoke();
        String holdBagProductCode = ((HoldItemsResponse) holdItemsService.getResponse()).getHoldItems().stream().filter(
                f -> {
                    if (f.getProductType().equals(productType)) {
                        return !TEN_KG_BAG.matches(f.getProductCode());
                    }
                    return false;
                }
        ).collect(Collectors.toList()).get(0).getProductCode();
        return holdBagProductCode;
    }

    public List<HoldItemsResponse.HoldItems> getHoldItemProduct(String channel, String productType) throws EasyjetCompromisedException {
        List<HoldItemsResponse.HoldItems> holdItemsArrayList = new ArrayList<>();
        HoldItemsQueryParams.HoldItemsQueryParamsBuilder queryParams = HoldItemsQueryParams.builder();
        //Check the basketResponse
        BasketService basketService = testData.getData(BASKET_SERVICE);
        if (basketService != null) {
            if (Objects.nonNull(basketService.getResponse().getBasket().getCurrency().getCode())) {
                queryParams.currency(basketService.getResponse().getBasket().getCurrency().getCode());
            } else {
                throw new EasyjetCompromisedException("Could not get currency from basket, as it is empty. Check stock levels.");
            }
        }

        if (basketService != null) {
            queryParams.currency(basketService.getResponse().getBasket().getCurrency().getCode());
        }
        holdItemsService = serviceFactory.getHoldItems(new HoldItemsRequest(HybrisHeaders.getValid("Digital")
                .build(), queryParams.build()));
        holdItemsService.invoke();
        String[] productTypes = productType.split(",");

        for (String type : productTypes
                ) {
            HoldItemsResponse.HoldItems item = ((HoldItemsResponse) holdItemsService.getResponse()).getHoldItems().stream().filter(
                    f -> {
                        return f.getProductType().equals(type) && !TEN_KG_BAG.matches(f.getProductCode());
                    }
            ).collect(Collectors.toList()).get(0);

            holdItemsArrayList.add(item);
        }
        return holdItemsArrayList;
    }

    private String getFlightKeyFromBasketResponse() {
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();
    }

    public String getOuBoundFlightKeyBasedOnIndex(int index) {
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(index).getFlights().get(index).getFlightKey();
    }

    public String getIndex(int index) {
        List<AbstractPassenger.HoldItem> holdItemList = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .filter(flight -> flight.getFlightKey().equals(requestBody.getFlightKey()))
                .flatMap(g -> g.getPassengers().stream())
                .filter(g -> g.getCode().equalsIgnoreCase(requestBody.getPassengerCode()))
                .flatMap(hold -> hold.getHoldItems().stream())
                .collect(Collectors.toList());
        return holdItemList.get(index - 1).getOrderEntryNumber();
    }

    public String getPassengerCode(int index) {
        return basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(index).getFlights().get(index).getPassengers().get(index).getCode();
    }

    public String getPassengerWithType(int index, String type) {
        String newType;
        if (type.contains(";")) {
            newType = type.replaceAll(";", "");
        } else {
            newType = type;
        }
        List<Basket.Passenger> flightPassenger = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .filter(Objects::nonNull)
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .filter(g -> g.getPassengerDetails().getPassengerType().equalsIgnoreCase(newType))
                .collect(Collectors.toList());
        return flightPassenger.get(index).getCode();
    }

    public String getBasketId() {
        return basketHelper.getBasketService().getResponse().getBasket().getCode();
    }

    private String retrieveValidItemSportBelongToGroup(boolean large) throws EasyjetCompromisedException {
        String largeItem = "LargeSportsProduct";
        String smallItem = "SmallSportsProduct";
        List<String> itemsSport;
        if (large) {
            itemsSport = holdItemsDao.getItemsSportEquipment(largeItem);
        } else {
            itemsSport = holdItemsDao.getItemsSportEquipment(smallItem);
        }

        retrieveRandomItem = ThreadLocalRandom.current().nextInt(0, itemsSport.size());
        product = itemsSport.get(retrieveRandomItem);
        // TODO remove follow line when all stocklevel will be properly configured
        product = "Snowboard";
        try {
            stockLevel = Integer.parseInt(holdItemsDao.getStockLevelForFlight(getFlightKey(), product).get(0));
        } catch (Exception e) {
            LOG.error(e);
            throw new EasyjetCompromisedException(INSUFFICIENT_STOCK_LEVEL_FLIGHTS);
        }
        return product;
    }

    private String retrieveValidItemSport() throws EasyjetCompromisedException {
        List<String> itemsSport;
        itemsSport = holdItemsDao.getAllItemsSportEquipment();

        retrieveRandomItem = ThreadLocalRandom.current().nextInt(0, itemsSport.size());
        product = itemsSport.get(retrieveRandomItem);
        // TODO remove follow line when all stocklevel will be properly configured
        product = "Snowboard";
        try {
            stockLevel = Integer.parseInt(holdItemsDao.getStockLevelForFlight(getFlightKey(), product).get(0));
        } catch (Exception e) {
            LOG.error(e);
            throw new EasyjetCompromisedException(INSUFFICIENT_STOCK_LEVEL_FLIGHTS);
        }

        return product;
    }

    private int getStockLevel() {
        return stockLevel;
    }

    public int getHoldBagQuantity() {
        return requestBody.getQuantity();
    }

    public String getFlightKey() {
        return requestBody.getFlightKey();
    }

    public String getFlightKeyForHoldBags() {
        return requestBody.getFlightKey();
    }

    public String getHoldItemProductCode() {
        return requestBody.getProductCode();
    }

    public String getOrderEntryNumber() {
        return index;
    }

    public BasketsResponse getBasketResponse(String channel) {
        basketHelper.getBasket(getBasketId(), channel);
        return basketHelper.getBasketService().getResponse();
    }

    public void enableOverrideWarning() {
        requestBody.setOverride(true);
    }

    public void updateQuantityAllowPerPassenger(int threshold) {
        requestBody.setPassengerCode(getPassengerWithType(0, passengerType));
        requestBody.setQuantity(threshold == 0 ? 1 : threshold);
    }

    public void updateHoldBagQuantityAllowPerPassenger(int threshold) {
        List<Basket.Passenger> passengers = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).collect(Collectors.toList());
        List<String> code = passengers.stream().filter(f -> Objects.nonNull(f) && this.passengerType.equals(f.getPassengerDetails().getPassengerType())).map(g -> g.getCode()).collect(Collectors.toList());
        requestBody.setPassengerCode(code.get(0));
        requestBody.setQuantity(threshold == 0 ? 1 : threshold);
    }

    public int getThresholdForPassengerMix(String channel, String passengerType, String productCategory) throws EasyjetCompromisedException {
        String prodCategory = productCategory;
        boolean isInfantOnLap = false;
        if (passengerType.toLowerCase().contains(ADULT)) {
            this.passengerType = ADULT;
        } else if (passengerType.toLowerCase().contains(INFANT)) {
            this.passengerType = INFANT;
        } else if (passengerType.toLowerCase().contains(CHILD)) {
            this.passengerType = CHILD;
        }

        switch (productCategory) {
            case HOLD_ITEM:
                prodCategory = "HoldBagProduct";
                break;
            case "Large Sporting Equipment":
                prodCategory = "LargeSportsProduct";
                break;
            case "Small Sporting Equipment":
                prodCategory = "SmallSportsProduct";
                break;
            default:
                break;

        }

        return Integer.parseInt(holdItemsDao.getThresholdForPassengerMix(channel, this.passengerType, prodCategory, isInfantOnLap).get(0));
    }

    public void updateQuantityOverThresholdPerFlight() {
        int threshold = getStockLevel();
        requestBody.setQuantity(threshold + 1);
    }

    public String getProductCode() {
        return product;
    }

    public String getPassengerCode() {
        return passengerCode;
    }

    public int getReservedAllocation() {
        return reservedAllocation;
    }

    public int getActualReservedItem() throws EasyjetCompromisedException {
        try {
            return getReservedStockLevelForFlight();
        } catch (Exception e) {
            LOG.error(e);
            throw new EasyjetCompromisedException(INSUFFICIENT_STOCK_LEVEL_FLIGHTS);
        }
    }

    public Map<String, Integer> getActualReservedItem(List<String> holdBagItemsList) throws EasyjetCompromisedException {
        Map<String, Integer> holdbagItemsMap = new HashMap<>();
        try {
            holdBagItemsList.stream().forEach(item -> {
                try {
                    holdbagItemsMap.put(item, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getFlightKey(), item).get(0)));
                } catch (Exception e) {
                    LOG.error(e);
                    holdbagItemsMap.put(item, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(getFlightKeyFromBasketResponse(), item).get(0)));
                }
            });
        } catch (Exception e) {
            LOG.error(e);
            throw new EasyjetCompromisedException(INSUFFICIENT_STOCK_LEVEL_FLIGHTS);
        }
        return holdbagItemsMap;

    }

    private Integer getReservedStockLevelForFlight() {
        try {
            return Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getFlightKey(), requestBody.getProductCode()).get(0));
        } catch (Exception e) {
            return Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(getFlightKeyFromBasketResponse(), requestBody.getProductCode()).get(0));
        }
    }

    void updateFieldWithValueForHoldBag(String field, String value) {

        switch (field) {
            case "basketCode":
                params = BasketPathParams.builder().basketId(value).path(HOLD_BAG).build();
                break;
            case "productCode":
                requestBody.setProductCode(value);
                break;
            case "quantity":
                requestBody.setQuantity(Integer.parseInt(value));
                break;
            case "passengerCode":
                requestBody.setPassengerCode(value);
                break;
            case "flightKey":
                requestBody.setFlightKey(value);
                break;
            case "override":
                requestBody.setOverride(Boolean.parseBoolean(value));
                break;
            default:
                break;
        }
    }

    public ProductModel getProductWithRestrictedChannel(String productCode) {
        List<ProductModel> productModels = productDao.getRestrictedChannelsForProduct(productCode);
        if (CollectionUtils.isNotEmpty(productModels)) {
            ProductModel aggregated = new ProductModel();
            aggregated.setCode(productModels.get(0).getCode());
            aggregated.setProductType(productModels.get(0).getProductType());
            // each of the returned product models will have a maximum of 1 channel (cause we are reading them directly from DB)
            aggregated.setChannels(productModels.stream()
                    .filter(pm -> CollectionUtils.isNotEmpty(pm.getChannels()))
                    .map(pm -> pm.getChannels().get(0))
                    .collect(Collectors.toList()));
            return aggregated;
        }
        return null;
    }

    public void buildRequestAddHoldBagsToChangedFlight(String channel, String  flightKey) throws EasyjetCompromisedException {
        params = BasketPathParams.builder().basketId(getBasketId()).path(HOLD_BAG).build();
        requestBody = BasketHoldItemsFactory.aBasicHoldItems();
        requestBody.setFlightKey(flightKey);
        requestBody.setProductCode(TWENTY_KG_BAG);
        requestBody.setExcessWeightProductCode(THREE_KG_EXTRA);
        requestBody.setExcessWeightQuantity(1);
        product = TWENTY_KG_BAG;
        requestBody.setPassengerCode(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
        requestBody.setQuantity(1);
        if (testData.getData(SerenityFacade.DataKeys.QUANTITY) != null)
            requestBody.setQuantity(testData.getData(SerenityFacade.DataKeys.QUANTITY));
        reservedAllocation = getActualReservedItem();
    }

    public void addSportEqOnDifferentPassengers(String produceCode, List<String> passengersCode) {
        BasketPathParams sportPathParams = BasketPathParams.builder().basketId(getBasketId()).path(SPORT_EQUIP).build();
        for(String paxCode: passengersCode) {
            AddHoldItemsRequestBody sportRequestBody = BasketHoldItemsFactory.addHoldItemsRequestBody(paxCode.split("_")[1], paxCode, produceCode, null, null);
            AddHoldBagToBasketService addHoldBagToBasketService = serviceFactory.addHoldBagToBasket(new AddHoldItemsToBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), sportPathParams, sportRequestBody));
            addHoldBagToBasketService.invoke();
            addHoldBagToBasketService.getResponse();
        }
    }
}
