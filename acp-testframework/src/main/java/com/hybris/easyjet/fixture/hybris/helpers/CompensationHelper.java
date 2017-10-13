package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.CreateCompensationRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CompensationMethodFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.CreateCompensationRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.CreateCompensationService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Niyi Falade on 18/09/17.
 */
@Component
public class CompensationHelper {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private CreateCompensationRequestBody createCompensationRequestBody;
    @Autowired
    private BasketHelper basketHelper;
    private BasketPathParams basketPathParams;
    private CreateCompensationService createCompensationService;
    List<Basket.Passenger> passenger;

    public void addCompensationToBasket(String paymentType, boolean paxOrder, String currency, String compensationAmount) {

        createCompensationRequestBody = compensationPaymentMethod(basketHelper.getBasketService().getResponse().getBasket(), paymentType, paxOrder, currency, compensationAmount);
        basketPathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(BasketPathParams.BasketPaths.COMPENSATION).build();
        invokeServices();
    }

    private void invokeServices() {
        createCompensationService = serviceFactory.createCompensationService(new CreateCompensationRequest(HybrisHeaders.getValid(testData.getChannel()).build(), basketPathParams, createCompensationRequestBody));
        createCompensationService.invoke();
    }

    public void addCompensationUsingInvalidItem(String entry) {

        testData.setData(SerenityFacade.DataKeys.PASSENGER_ID, getLeadOrSecondPassenger(basketHelper.getBasketService().getResponse().getBasket(), true).get(0));

        if (entry.equalsIgnoreCase("invalidbasketID")) {
            createCompensationRequestBody =
                    CompensationMethodFactory.generateVoucherCompensation(testData.getPassengerId(), testData.getFlightKey(), "GBP", "VOUCHER", "Simon black");
            basketPathParams = BasketPathParams.builder().basketId("000000021505906798885").path(BasketPathParams.BasketPaths.COMPENSATION).build();
        } else if (entry.equalsIgnoreCase("invalidFlightKey")) {
            createCompensationRequestBody =
                    CompensationMethodFactory.generateVoucherCompensation(testData.getPassengerId(), "20170921XXXXC2223", "GBP", "VOUCHER", "Simon black");
            basketPathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(BasketPathParams.BasketPaths.COMPENSATION).build();
        } else if (entry.equalsIgnoreCase("invalidpassengerID")) {
            createCompensationRequestBody =
                    CompensationMethodFactory.generateVoucherCompensation("000000101505916279926_20170921LTNALC2223_8796126508370", testData.getFlightKey(), "GBP", "VOUCHER", "Simon black");
            basketPathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(BasketPathParams.BasketPaths.COMPENSATION).build();
        } else if (entry.equalsIgnoreCase("passengerNotOnFlight")) {
            createCompensationRequestBody =
                    CompensationMethodFactory.generateVoucherCompensation("000000101505916279926_20170921LTNALC2223_8796126508370", testData.getFlightKey(), "GBP", "VOUCHER", "Simon black");
            basketPathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(BasketPathParams.BasketPaths.COMPENSATION).build();
        } else if (entry.equalsIgnoreCase("incorrectPaymentMethod")) {
            createCompensationRequestBody =
                    CompensationMethodFactory.generateVoucherCompensation(testData.getPassengerId(), testData.getFlightKey(), "GBP", "CARD", "Simon black");
            basketPathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(BasketPathParams.BasketPaths.COMPENSATION).build();
        }
        invokeServices();
    }

    public void addCompensationOmitMandatoryFields(String entry) {

        testData.setData(SerenityFacade.DataKeys.PASSENGER_ID, getLeadOrSecondPassenger(basketHelper.getBasketService().getResponse().getBasket(), true).get(0));

        if (entry.equalsIgnoreCase("emailAndnameOnVoucher")) {
            createCompensationRequestBody =
                    CompensationMethodFactory.generateAllDataCompensation(testData.getPassengerId(), testData.getFlightKey(), "GBP",
                            "VOUCHER", null, null,
                            null, null, null, null, null,
                            null, null, null, null,
                            null, null, null, null);
        } else if (entry.equalsIgnoreCase("nameAndAddress")){
            createCompensationRequestBody =
                    CompensationMethodFactory.generateAllDataCompensation(testData.getPassengerId(), testData.getFlightKey(), "GBP",
                            "CHEQUE", null, null,
                            null, null, null, null, null,
                            null, null, null, null,
                            "Oxfordshire", "GBR", "OX11 2ES", "Simon Black");

        } else if (entry.equalsIgnoreCase("bankDetails")){
            createCompensationRequestBody =
                    CompensationMethodFactory.generateAllDataCompensation(testData.getPassengerId(), testData.getFlightKey(), "GBP",
                            "BANKACCOUNT", null, null,
                            null, null, null, null, null,
                            null, null, null, null,
                            null, null, null, "null");

        } else if(entry.equalsIgnoreCase("creditFile")){
            createCompensationRequestBody =
                    CompensationMethodFactory.generateAllDataCompensation(testData.getPassengerId(), testData.getFlightKey(), "GBP",
                            "CREDITFILEFUND", null, null,
                            null, null, null, null, null,
                            null, null, null, null,
                            null, null, null, "null");
        }else if(entry.equalsIgnoreCase("invalidEmail")){
            createCompensationRequestBody =
                    CompensationMethodFactory.generateAllDataCompensation(testData.getPassengerId(), testData.getFlightKey(), "GBP",
                            "VOUCHER", "Simon black", "abc",
                            null, null, null, null, null,
                            null, null, null, null,
                            null, null, null, null);
        }
        basketPathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(BasketPathParams.BasketPaths.COMPENSATION).build();
        invokeServices();
    }

    public CreateCompensationRequestBody compensationPaymentMethod(Basket basket, String paymentType, boolean isLead, String currency, String compensationAmount) {

        switch (paymentType) {
            case "voucher":
                createCompensationRequestBody =
                        CompensationMethodFactory.generateVoucherCompensation(getLeadOrSecondPassenger(basket, isLead).get(0),currency, compensationAmount);
                break;
            case "cheque":
                createCompensationRequestBody =
                        CompensationMethodFactory.generateChequeCompensation(getLeadOrSecondPassenger(basket, isLead).get(0),currency, compensationAmount);
                break;
            case "credit file":
                createCompensationRequestBody =
                        CompensationMethodFactory.generateCreditFileCompensation(getLeadOrSecondPassenger(basket, isLead).get(0),currency, compensationAmount);
                break;
            case "bank Account":
                createCompensationRequestBody =
                        CompensationMethodFactory.generateBankAccountCompensation(getLeadOrSecondPassenger(basket, isLead).get(0),currency, compensationAmount);
                break;
        }

        return createCompensationRequestBody;
    }

    private List<Basket.Passenger> getLeadOrSecondPassenger(Basket basket, boolean isLead) {

        if (isLead) {

            passenger = basket.getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .filter(t -> t.getFareProduct().getOrderEntryNumber().equals("0"))
                    .collect(Collectors.toList());
        } else {

            passenger = basket.getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .collect(Collectors.toList());
            passenger.removeIf(t -> t.getFareProduct().getOrderEntryNumber().equals("0"));
        }

        return passenger;
    }

    public CreateCompensationService getCreateCompensationService() {
        return createCompensationService;
    }
}
