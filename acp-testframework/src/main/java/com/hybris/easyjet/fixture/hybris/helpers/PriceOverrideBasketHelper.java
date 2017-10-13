package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.PriceOverrideDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.PriceOverrideRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.BasketHoldItemsFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PriceOverrideRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.PriceOverrideBasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.OVERRIDE_PRICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.REMOVE_OVERRIDE_PRICE;

/**
 * Created by giuseppecioce on 23/03/2017.
 */
@Component
public class PriceOverrideBasketHelper {

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private PriceOverrideDao priceOverrideDao;

    private List<Basket.Passenger> passengers;
    private Map passengerProductCode = new HashMap<>();
    private Map<String, PricingHelper> passengerPriceProductCodeAfterApplyOverride = new HashMap<>();
    private Map<String, PricingHelper> passengerPriceProductCodeBeforeApplyOverride = new HashMap<>();
    private Map<String, PricingHelper> passengerPriceBeforeOverridePrice = new HashMap<>();
    private BasketHoldItemsHelper basketHoldItemsHelper;
    private PriceOverrideBasketService priceOverrideBasketService;
    private PriceOverrideRequestBody priceOverrideRequestBody;
    private BasketPathParams params;
    private String overrideDiscountCode;
    private String discountReason;
    private double amountDiscount = 0;
    private PricingHelper basketPriceBeforeApplyOverride = new PricingHelper();
    private PricingHelper basketPriceAfterApplyOverride = new PricingHelper();

    @Autowired
    public PriceOverrideBasketHelper(BasketHoldItemsHelper basketHoldItemsHelper) {
        this.basketHoldItemsHelper = basketHoldItemsHelper;
    }

    public Map<String, PricingHelper> getPassengerPriceProductCodeAfterApplyOverride() {
        return passengerPriceProductCodeAfterApplyOverride;
    }

    public Map<String, PricingHelper> getPassengerPriceProductCodeBeforeApplyOverride() {
        return passengerPriceProductCodeBeforeApplyOverride;
    }

    public List<Basket.Passenger> getPassenger() {
        return passengers;
    }

    public List<Basket.Passenger> getActualPassenger(String channel) throws Throwable {
        return getBasketHoldItemsHelper().getBasketResponse(channel).getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getPassengerDetails().getPassengerType().equals("adult")).collect(Collectors.toList());
    }

    public PriceOverrideBasketService getPriceOverrideBasketService() {
        return priceOverrideBasketService;
    }

    public double getAmountDiscount() {
        return amountDiscount;
    }

    public PricingHelper getBasketPriceBeforeApplyOverride() {
        return basketPriceBeforeApplyOverride;
    }

    public PricingHelper getBasketPriceAfterApplyOverride() {
        return basketPriceAfterApplyOverride;
    }

    public BasketHoldItemsHelper getBasketHoldItemsHelper() {
        return basketHoldItemsHelper;
    }

    public Map getPassengerProductCode() {
        return passengerProductCode;
    }

    public String getDiscountReason() {
        return discountReason;
    }

    public Map<String, PricingHelper> getPassengerPriceBeforeOverridePrice() {
        return passengerPriceBeforeOverridePrice;
    }

    public void addItemEachPassenger(String channel) throws Throwable {
        List<Basket.Passenger> passengers = basketHoldItemsHelper.getBasketResponse(channel).getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).
                flatMap(g -> g.getPassengers().stream()).
                filter(h -> !h.getPassengerDetails().getPassengerType().equalsIgnoreCase("infant")
                        && !h.getPassengerDetails().getPassengerType().equalsIgnoreCase("child")).collect(Collectors.toList());
        passengers.forEach(item -> {
            try {
                basketHoldItemsHelper.buildRequestToAddHoldBags(channel);
            } catch (EasyjetCompromisedException e) {
            }

            basketHoldItemsHelper.updateFieldWithValueForHoldBag("passengerCode", item.getCode());
            try {
                basketHoldItemsHelper.invokeServiceAddHoldBags(channel);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            passengerProductCode.put(item.getCode(), basketHoldItemsHelper.getProductCode());
        });
    }

    public void buildRequestToApplyOverridePrice() {
        params = BasketPathParams.builder().basketId(basketHoldItemsHelper.getBasketId()).path(OVERRIDE_PRICE).build();
        List<String> reasonCodeList = priceOverrideDao.getValidDiscountReason();
        discountReason = reasonCodeList.get(1);
        priceOverrideRequestBody = BasketHoldItemsFactory.aBasicOverridePriceBasketLevel(discountReason);

        try {
            storeBasketPricingBeforeApplyOverride();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void buildRequestToRemoveOverridePrice() {
        params = BasketPathParams.builder().basketId(basketHoldItemsHelper.getBasketId()).overrideDiscountCode(overrideDiscountCode).path(REMOVE_OVERRIDE_PRICE).build();
    }

    public void invokeApplyOverridePrice(String channel) throws Throwable {
        priceOverrideBasketService = serviceFactory.getPriceOverride(new PriceOverrideRequest(HybrisHeaders.getValid(channel).build(), params, priceOverrideRequestBody));
        priceOverrideBasketService.invoke();
        amountDiscount += priceOverrideRequestBody.getOverrideTotalAmount();

        // overrideDiscountCode can not be got in case of validation of mandatory field (not required!) Useful in order to not duplicate the method
        overrideDiscountCode = priceOverrideBasketService.getSuccessful() ? priceOverrideBasketService.getResponse().getOperationConfirmation().getDiscountCode() : "";

        storeBasketPricingAfterApplyOverride();
    }

    public void invokeApplyOverridePriceOnPassenger(String channel) throws Throwable {
        passengerPriceBeforeOverridePrice.clear();
        passengers = basketHoldItemsHelper.getBasketResponse(channel).getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> !h.getPassengerDetails().getPassengerType().equalsIgnoreCase("infant") && !h.getPassengerDetails().getPassengerType().equalsIgnoreCase("child")).collect(Collectors.toList());
        passengers.forEach(item -> {
            try {
                passengerPriceBeforeOverridePrice.put(item.getCode(), storePassengerPricingBeforeApplyOverride(item.getCode()));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            updateFieldWithValue("passengerCode", item.getCode());
            priceOverrideBasketService = serviceFactory.getPriceOverride(new PriceOverrideRequest(HybrisHeaders.getValid(channel).build(), params, priceOverrideRequestBody));
            try {
                priceOverrideBasketService.invoke();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            amountDiscount += priceOverrideRequestBody.getOverrideTotalAmount();
        });
        overrideDiscountCode = priceOverrideBasketService.getSuccessful() ? priceOverrideBasketService.getResponse().getOperationConfirmation().getDiscountCode() : "";
    }

    public void invokeApplyOverridePriceOnProduct(String channel) throws Throwable {
        passengerPriceProductCodeBeforeApplyOverride.clear();
        passengerPriceProductCodeAfterApplyOverride.clear();
        passengers = basketHoldItemsHelper.getBasketResponse(channel).getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> !h.getPassengerDetails().getPassengerType().equalsIgnoreCase("infant") && !h.getPassengerDetails().getPassengerType().equalsIgnoreCase("child")).collect(Collectors.toList());
        passengers.forEach(item -> {
            storeProductPricingBeforeRemoveOverride(item.getCode(), (String) passengerProductCode.get(item.getCode()));
            updateFieldWithValue("passengerCode", item.getCode());
            updateFieldWithValue("productCode", (String) passengerProductCode.get(item.getCode()));
            priceOverrideBasketService = serviceFactory.getPriceOverride(new PriceOverrideRequest(HybrisHeaders.getValid(channel).build(), params, priceOverrideRequestBody));
            try {
                priceOverrideBasketService.invoke();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            amountDiscount += priceOverrideRequestBody.getOverrideTotalAmount();

            storeProductPricingAfterRemoveOverride(item.getCode(), (String) passengerProductCode.get(item.getCode()));
        });
        overrideDiscountCode = priceOverrideBasketService.getSuccessful() ? priceOverrideBasketService.getResponse().getOperationConfirmation().getDiscountCode() : "";
        storeBasketPricingAfterApplyOverride();
    }

    public void invokeRemoveOverridePrice(String channel) throws Throwable {
        priceOverrideBasketService = serviceFactory.getPriceOverride(new PriceOverrideRequest(HybrisHeaders.getValid(channel).build(), params));
        priceOverrideBasketService.invoke();
    }

    private void storeBasketPricingBeforeApplyOverride() throws Throwable {
        BasketsResponse basketsResponse = basketHoldItemsHelper.getBasketResponse(testData.getChannel());
        basketPriceBeforeApplyOverride.setTotalAmountWithCreditCard(basketsResponse.getBasket().getTotalAmountWithCreditCard());
        basketPriceBeforeApplyOverride.setTotalAmountWithDebitCard(basketsResponse.getBasket().getTotalAmountWithDebitCard());
        basketPriceBeforeApplyOverride.setSubtotalAmountWithCreditCard(basketsResponse.getBasket().getSubtotalAmountWithCreditCard());
        basketPriceBeforeApplyOverride.setSubtotalAmountWithDebitCard(basketsResponse.getBasket().getSubtotalAmountWithDebitCard());
        amountDiscount = 0;
    }

    private PricingHelper storePassengerPricingBeforeApplyOverride(String passengerCode) throws Throwable {
        Basket.Passenger desiredPassenger = basketHoldItemsHelper.getBasketResponse(testData.getChannel()).getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equals(passengerCode)).findFirst().orElse(null);
        PricingHelper pricingHelper = new PricingHelper();
        pricingHelper.setTotalAmountWithCreditCard(desiredPassenger.getPassengerTotalWithCreditCard());
        pricingHelper.setTotalAmountWithDebitCard(desiredPassenger.getPassengerTotalWithDebitCard());
        return pricingHelper;
    }

    private void storeBasketPricingAfterApplyOverride() throws Throwable {
        BasketsResponse basketsResponse = basketHoldItemsHelper.getBasketResponse(testData.getChannel());
        basketPriceAfterApplyOverride.setTotalAmountWithCreditCard(basketsResponse.getBasket().getTotalAmountWithCreditCard());
        basketPriceAfterApplyOverride.setTotalAmountWithDebitCard(basketsResponse.getBasket().getTotalAmountWithDebitCard());
        basketPriceAfterApplyOverride.setSubtotalAmountWithCreditCard(basketsResponse.getBasket().getSubtotalAmountWithCreditCard());
        basketPriceAfterApplyOverride.setSubtotalAmountWithDebitCard(basketsResponse.getBasket().getSubtotalAmountWithDebitCard());
    }

    private void storeProductPricingAfterRemoveOverride(String passengerCode, String productCode) {
        BasketsResponse basketsResponse = null;
        try {
            basketsResponse = basketHoldItemsHelper.getBasketResponse(testData.getChannel());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        PricingHelper productPriceBeforeApplyOverride = new PricingHelper();
        productPriceBeforeApplyOverride.setTotalAmountWithCreditCard(basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(l -> l.getCode().equals(passengerCode)).flatMap(h -> h.getHoldItems().stream()).filter(i -> i.getCode().equals(productCode)).mapToDouble(m -> m.getPricing().getTotalAmountWithCreditCard()).findFirst().orElse(0));
        productPriceBeforeApplyOverride.setTotalAmountWithDebitCard(basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(l -> l.getCode().equals(passengerCode)).flatMap(h -> h.getHoldItems().stream()).filter(i -> i.getCode().equals(productCode)).mapToDouble(m -> m.getPricing().getTotalAmountWithDebitCard()).findFirst().orElse(0));
        passengerPriceProductCodeAfterApplyOverride.put(passengerCode, productPriceBeforeApplyOverride);
    }

    private void storeProductPricingBeforeRemoveOverride(String passengerCode, String productCode) {
        BasketsResponse basketsResponse = null;
        try {
            basketsResponse = basketHoldItemsHelper.getBasketResponse(testData.getChannel());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        PricingHelper productPriceBeforeApplyOverride = new PricingHelper();
        productPriceBeforeApplyOverride.setTotalAmountWithCreditCard(basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(l -> l.getCode().equals(passengerCode)).flatMap(h -> h.getHoldItems().stream()).filter(i -> i.getCode().equals(productCode)).mapToDouble(m -> m.getPricing().getTotalAmountWithCreditCard()).findFirst().orElse(0));
        productPriceBeforeApplyOverride.setTotalAmountWithDebitCard(basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(l -> l.getCode().equals(passengerCode)).flatMap(h -> h.getHoldItems().stream()).filter(i -> i.getCode().equals(productCode)).mapToDouble(m -> m.getPricing().getTotalAmountWithDebitCard()).findFirst().orElse(0));
        passengerPriceProductCodeBeforeApplyOverride.put(passengerCode, productPriceBeforeApplyOverride);
    }

    public void removeFieldFromRequestBodyToApply(String field) {
        if (field.equals("basketCode"))
            params = BasketPathParams.builder().basketId("0").path(OVERRIDE_PRICE).build();

        priceOverrideRequestBody = BasketHoldItemsFactory.missingFieldOverridePrice(field, priceOverrideRequestBody);
    }

    public void removeFieldFromRequestBodyToRemove(String field) {
        if (field.equals("basketCode"))
            params = BasketPathParams.builder().basketId("0").overrideDiscountCode(overrideDiscountCode).path(REMOVE_OVERRIDE_PRICE).build();
        else if (field.equals("discountCode"))
            params = BasketPathParams.builder().basketId(basketHoldItemsHelper.getBasketId()).overrideDiscountCode("0").path(REMOVE_OVERRIDE_PRICE).build();
    }

    private void updateFieldWithValue(String field, String value) {
        switch (field) {
            case "passengerCode":
                priceOverrideRequestBody.setPassengerCode(value);
                break;
            case "isApplicableToAllPassengers":
                priceOverrideRequestBody.setApplicableToAllPassengers(Boolean.parseBoolean(value));
                break;
            case "productCode":
                priceOverrideRequestBody.setProductCode(value);
                break;
            case "reasonCode":
                priceOverrideRequestBody.setReasonCode(value);
                break;
            case "comment":
                priceOverrideRequestBody.setComment(value);
                break;
            case "overrideTotalAmount":
                priceOverrideRequestBody.setOverrideTotalAmount(Double.parseDouble(value));
                break;
            case "feeChargeCode":
                priceOverrideRequestBody.setFeeChargeCode(value);

        }
    }
}
