package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.RecalculatePricesRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.BasketContent;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.managebooking.PaymentBalanceRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RecalculatePricesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.RecalculatePricesService;
import com.hybris.easyjet.fixture.hybris.invoke.services.managebooking.PaymentBalanceService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.CALCULATE_PAYMENT_BALANCE;

/**
 * Created by tejaldudhale on 10/05/2017.
 */
@Component
public class RepriceBasketHelper {

    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;

    private RecalculatePricesRequestBody recalculatePricesRequestBody;
    private RecalculatePricesService recalculatePricesService;
    Basket basket;
    @Getter
    private PaymentBalanceService paymentBalanceService;
    private BasketPathParams basketPathParams;
    @Getter
    private PaymentBalanceRequestBody paymentBalanceRequestBody;

    public RecalculatePricesService getRecalculatePricesService() {
        return recalculatePricesService;
    }

    public Basket getBasket(String channel, String journeyType, String faretype, String bookingtype) throws Throwable {
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        bookingHelper.getBasketHelper().createBasketWithPassengerMix(flightsService, journeyType, faretype, bookingtype, testData.getChannel(), false);

        testData.setData(GET_FLIGHT_SERVICE, null);
        return bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket();
    }

    public RecalculatePricesService invokeRepriceBasket(String channel, String basketCode, BasketContent basketContent) {
        recalculatePricesRequestBody = RecalculatePricesRequestBody.builder().build();
        if (basketCode == null) {
            recalculatePricesRequestBody.setBasketContent(basketContent);
        } else
            recalculatePricesRequestBody = RecalculatePricesRequestBody.builder().basketCode(basketCode).build();

        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        recalculatePricesService = serviceFactory.recalculatePricesService(new RecalculatePricesRequest(headers.build(), recalculatePricesRequestBody));
        recalculatePricesService.invoke();
        return recalculatePricesService;
    }

    public void invokeRepriceBasketServiceWithInvalidParam(String channel, BasketContent basketContent) {
        recalculatePricesRequestBody = RecalculatePricesRequestBody.builder().build();
        recalculatePricesRequestBody.setBasketContent(basketContent);
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        recalculatePricesService = serviceFactory.recalculatePricesService(new RecalculatePricesRequest(headers.build(), recalculatePricesRequestBody));
        recalculatePricesService.invoke();
    }

    public void invokeRepriceBasketServiceWithInvalidBasketCode(RecalculatePricesRequestBody recalculatePricesRequestBody) {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        recalculatePricesService = serviceFactory.recalculatePricesService(new RecalculatePricesRequest(headers.build(), recalculatePricesRequestBody));
        recalculatePricesService.invoke();
    }

    /**
     * The method occupies to set the prereq for the test, have a basket ref
     * @return basket ref
     * @throws Throwable
     */
    public String produceBasketRef() throws Throwable {
        boolean isBooking = false;
        boolean isBasket = true;
        String basketCode = "";
        if(isBooking) {
            bookingHelper.createNewBookingRequestForChannel(testData.getChannel());
            String bookingRef = bookingHelper.getCommitBookingService().getResponse().getBookingConfirmation().getBookingReference();
            basketCode = bookingHelper.getBasketHelper().createAmendableBasket(bookingRef);
        } else if(isBasket) {
            bookingHelper.getBasketHelper().myBasketContainsAFlightWithPassengerMix(testData.getPassengerMix(), testData.getChannel(), STANDARD, false);
            basketCode = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getCode();
        }
        return basketCode;
    }

    /**
     * The method build a valid request considering the payment method
     * @param basketCode
     * @param paymentMethods
     */
    public void prepareStatementRequest(String basketCode, String paymentMethods) {
        Basket basket = bookingHelper.getBasketHelper().getBasket(basketCode, testData.getChannel());
        testData.setData("basket", basket);

        basketPathParams = BasketPathParams.builder().basketId(basketCode).path(CALCULATE_PAYMENT_BALANCE).build();
        paymentBalanceRequestBody = PaymentBalanceRequestBody.builder().paymentMethods(buildRequestWithPaymentMethods(basket, paymentMethods)).build();
        paymentBalanceService = serviceFactory.paymentBalanceService(new PaymentMethodsRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                basketPathParams,
                paymentBalanceRequestBody));
    }

    /**
     * The method build an array list of PaymentMethod starting from a string containing paymentMethods
     * @param paymentMethods different method, max of 2, if you need more methods you need to modify the following method, considering the split of the basket amount
     * @return
     */
    public List<PaymentBalanceRequestBody.PaymentMethod> buildRequestWithPaymentMethods(Basket basket, String paymentMethods) {
        List<String> paymentMethodsCode = new ArrayList<>();
        if(paymentMethods.contains(";")) {
            paymentMethodsCode = Arrays.asList(paymentMethods.split(";"));
        } else {
            paymentMethodsCode.add(paymentMethods);
        }

        List<PaymentBalanceRequestBody.PaymentMethod> paymentMethodList = new ArrayList<>();
        for(String code: paymentMethodsCode) {
            Random r = new Random();
            BigDecimal paymentAmount = BigDecimal.valueOf(0 + (basket.getTotalAmountWithDebitCard() - 0) * r.nextDouble()).setScale(2, RoundingMode.HALF_UP);
            paymentAmount = paymentAmount.divide(BigDecimal.valueOf(paymentMethodsCode.size())).setScale(2, RoundingMode.HALF_UP);
            String paymentMethod = "card";
            String paymentCode = code;
            paymentMethodList.add(PaymentBalanceRequestBody.PaymentMethod.builder().paymentMethod(paymentMethod).paymentAmount(paymentAmount.doubleValue()).paymentCode(paymentCode).build());
        }
        return paymentMethodList;
    }

    /**
     * The method validate the field mandatory as per service contract
     * @param field
     */
    public void invalidRequestWithField(String field) {
        switch (field) {
            case "INVALID_BASKET_ID":
                basketPathParams.setBasketId("0000000");
                break;
            case "GREATER_PAYMENT_AMOUNT":
                Double greaterAmount = paymentBalanceRequestBody.getPaymentMethods().get(0).getPaymentAmount();
                Basket greaterAmountBasket = (Basket) testData.getData("basket");
                paymentBalanceRequestBody.getPaymentMethods().get(0).setPaymentAmount(greaterAmount + greaterAmountBasket.getTotalAmountWithDebitCard());
                break;
            case "LESSER_PAYMENT_AMOUNT":
                Double lesserAmount = paymentBalanceRequestBody.getPaymentMethods().get(0).getPaymentAmount();
                Basket lesserAmountBasket = (Basket) testData.getData("basket");
                paymentBalanceRequestBody.getPaymentMethods().get(0).setPaymentAmount(lesserAmount - lesserAmountBasket.getTotalAmountWithDebitCard());
                break;
            case "INVALID_PAYMENT_ID":
                paymentBalanceRequestBody.getPaymentMethods().get(0).setPaymentCode("INVALID");
                break;
            case "INVALID_PAYMENT_METHOD":
                paymentBalanceRequestBody.getPaymentMethods().get(0).setPaymentMethod("INVALID");
                break;
            default:
                break;
        }
    }

    /**
     * The method invoke the service payment balance
     */
    public void invokePaymentBalance() {
        paymentBalanceService.invoke();
    }

    public void createBasicRequestToRecalculateBasket(String basketCode) {
        basketPathParams = BasketPathParams.builder().basketId(basketCode).path(CALCULATE_PAYMENT_BALANCE).build();
        paymentBalanceRequestBody = PaymentBalanceRequestBody.builder().paymentMethods(basicRequestWithPaymentMethods()).build();
        paymentBalanceService = serviceFactory.paymentBalanceService(new PaymentMethodsRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                basketPathParams,
                paymentBalanceRequestBody));

        paymentBalanceService.invoke();
    }

    private List<PaymentBalanceRequestBody.PaymentMethod> basicRequestWithPaymentMethods() {
        return Arrays.asList(PaymentBalanceRequestBody.PaymentMethod.builder().paymentMethod("card").paymentAmount(0.0).paymentCode("DM").build());
    }
}
