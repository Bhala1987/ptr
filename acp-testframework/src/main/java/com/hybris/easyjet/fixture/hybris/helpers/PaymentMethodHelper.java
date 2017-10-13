package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.PaymentModeDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.CustomerProfileQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.PaymentMethodsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails.RemoveSavedPaymentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails.SavedPaymentMethodRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PaymentMethodFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RemovePaymentFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.DeleteCustomerProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveSavedPaymentRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails.PaymentMethodTypeService;
import com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails.RemoveSavedPaymentService;
import com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails.SavedPaymentMethodService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.*;

/**
 * Created by tejaldudhale on 25/04/2017.
 */
@Component
public class PaymentMethodHelper {
    @Getter
    private SavedPaymentMethodService savedPaymentMethodService;
    @Getter
    private SavedPaymentMethodRequestBody savedPaymentMethodRequestBody;
    @Getter
    private PaymentMethodsService paymentMethodsService;
    @Getter
    private PaymentMethodTypeService paymentMethodTypeService;
    @Getter
    private RemoveSavedPaymentRequestBody removeSavedPaymentRequestBody;
    @Getter
    private RemoveSavedPaymentService removeSavedPaymentService;

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory hybrisServiceFactory;
    @Autowired
    private CustomerProfileHelper customerProfileHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffMembertoCustomerProfileAssociationHelper;
    @Autowired
    private DealsInfoHelper dealsInfoHelper;
    @Autowired
    private FlightHelper flightHelper;

    private CustomerPathParams customerPathParams;
    private CustomerProfileQueryParams customerProfileQueryParams;


    @Autowired
    private PaymentModeDao paymentModeDao;

    public enum PAYMENT_METHOD {CREDIT_CARD, DEBIT_CARD, BANK_ACCOUNT,EXPIRED_CREDIT_CARD}

    private Enum ADD_PAYMENT_METHOD;
    private static final String invalid = "INVALID_VALUE";

    public void createBasketBasedOnBookingType(String basketType, String channel, String passengerMix) throws Throwable {
        switch (basketType) {
            case "STANDARD_CUSTOMER":
                getBasket(channel, passengerMix);
                break;
            case "BUSINESS":
                getBasketWithBookingTypeAsBusiness(channel, passengerMix);
                break;
            case "STAFF":
                getStaffBasket(channel, passengerMix);
                break;
            default:
                break;
        }
    }

    private void getStaffBasket(String channel, String passengerMix) throws Throwable {
        testData.setChannel(channel);
        customerHelper.createRandomCustomer(testData.getChannel());
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromRequest(testData.getChannel(), false);
        basketHelper.myBasketContainsAFlightWithPassengerMixForStaff(passengerMix, channel, "Staff");
    }

    public void deleteCustomer(String customerId) {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).build();
        DeleteCustomerProfileService deleteCustomerProfileService = hybrisServiceFactory.deleteCustomerDetails(new DeleteCustomerProfileRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
        deleteCustomerProfileService.invoke();
    }

    public void createBasketBasedOoMaxDaysTillDeparture(int maxDays, String channel) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMixBasedOnDates("1 Adult", channel);
    }

    public void createBasketBasedOnMaxDaysTillDeparture(String fareType) throws Throwable {
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        basketHelper.createBasketWithPassengerMix(flightsService, testData.getJourneyType(), fareType, "STANDARD_CUSTOMER", testData.getChannel(), false);
        testData.setData(GET_FLIGHT_SERVICE, null);
    }

    private void getBasketWithBookingTypeAsBusiness(String channel, String passengerMix) throws Throwable {
        DealModel dealInfo = dealsInfoHelper.findAValidDeals();
        basketHelper.myBasketContainsAFlightWithPassengerMixWithDeal(passengerMix, channel, dealInfo);
    }

    public void getBasket(String channel, String passengerMix) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, channel, STANDARD, false);
    }

    public void involePaymentService(List<String> criterias, String channel) {
        customerHelper.createRandomCustomer(channel);
        PaymentMethodsQueryParams paymentMethodsQueryParams = PaymentMethodsQueryParams.builder().build();
        HybrisHeaders hybrisHeaders = HybrisHeaders.builder().build();
        for (String criteria : criterias) {
            switch (criteria) {
                case "country":
                    hybrisHeaders = HybrisHeaders.getValid(channel)
                            .xCountry("GBR")
                            .build();
                    break;
                case "bookingType":
                    paymentMethodsQueryParams
                            .setBookingType(basketHelper.getBasketService().getResponse().getBasket().getBasketType());
                    break;
                default:
                    break;
            }
        }
        paymentMethodsService = hybrisServiceFactory
                .getPaymentMethods(new PaymentMethodsRequest(hybrisHeaders, paymentMethodsQueryParams));
        paymentMethodsService.invoke();
    }

    public void searchFlights(String channel, String passengerMix, String origin, String destination, String journey, int maxDays) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setJourneyType(journey);
        FlightsService flightsService = flightHelper.getFlights(channel, passengerMix, origin, destination, journey, new DateFormat().today().addDay(maxDays - 10), new DateFormat().today().addDay(maxDays - 5));
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        testData.setChannel(channel);
    }

    public void createCustomerAndLoginIt() {
        customerHelper.createCustomerAndLoginIt();
    }

    private void buildValidRequestSavedPaymentMethod(PAYMENT_METHOD paymentMethod) {
        switch (paymentMethod) {
            case CREDIT_CARD:
                savedPaymentMethodRequestBody = PaymentMethodFactory.aBasicAddDebitCardPaymentDetails(testData.isDefaultPaymentMethod());
                ADD_PAYMENT_METHOD = ADD_CREDIT_CARD_PAYMENT_METHOD;
                break;
            case DEBIT_CARD:
                savedPaymentMethodRequestBody = PaymentMethodFactory.aBasicAddCreditCardPaymentDetails(testData.isDefaultPaymentMethod());
                ADD_PAYMENT_METHOD = ADD_DEBIT_CARD_PAYMENT_METHOD;
                break;
            case BANK_ACCOUNT:
                savedPaymentMethodRequestBody = PaymentMethodFactory.aBasicAddBankPaymentDetails(testData.isDefaultPaymentMethod());
                ADD_PAYMENT_METHOD = ADD_BANK_ACCOUNT_PAYMENT_METHOD;
                break;
            case EXPIRED_CREDIT_CARD:
                savedPaymentMethodRequestBody = PaymentMethodFactory.aBasicAddExpiredCreditCardPaymentDetails(testData.isDefaultPaymentMethod());
                ADD_PAYMENT_METHOD = ADD_CREDIT_CARD_PAYMENT_METHOD;
                break;
            default:
                throw new IllegalArgumentException("You have to specify a valid payment method (you can choose into credit, debit or bank)");
        }
    }

    private void invokeAddPaymentWithParameter(String parameter) throws Throwable {
        CustomerPathParams profilePathParams = null;
        switch (parameter) {
            case "invalidID":
                profilePathParams = CustomerPathParams.builder().customerId(invalid).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
            case "invalidSession":
                HybrisService.theJSessionCookie.set("");
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
            case "mismatchIDSession":
                CustomerModel dbCustomer = customerProfileHelper.findAValidCustomerProfile();
                profilePathParams = CustomerPathParams.builder().customerId(dbCustomer.getUid()).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
            case "invalidPaymentID":
                savedPaymentMethodRequestBody.setPaymentCode(invalid);
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
            default:
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
        }
        savedPaymentMethodService = hybrisServiceFactory.addSavedPaymentMethod(new PaymentMethodsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), profilePathParams, savedPaymentMethodRequestBody));
        savedPaymentMethodService.invoke();
    }

    public void submitPaymentMethod(PAYMENT_METHOD paymentMethod, String typeOfSubmitted) throws Throwable {
        buildValidRequestSavedPaymentMethod(paymentMethod);
        invokeAddPaymentWithParameter(typeOfSubmitted);
    }

    public void shouldIReceiveAnError(boolean should, String error) {
        if (should) {
            savedPaymentMethodService.assertThatErrors().containedTheCorrectErrorMessage(error);
        } else {
            savedPaymentMethodService.getResponse();
        }
    }

    public List<String> getPaymentReferenceForCustomer() {
        return paymentModeDao.getPaymentEntityForCustomer(testData.getData(CUSTOMER_ID));
    }

    public void updatePaymentMethod(String typeOfSubmitted) throws Throwable {
        savedPaymentMethodRequestBody = null;
        if (ADD_PAYMENT_METHOD.equals(CustomerPathParams.CustomerPaths.ADD_CREDIT_CARD_PAYMENT_METHOD)) {
            ADD_PAYMENT_METHOD = UPDATE_CREDIT_CARD_PAYMENT_METHOD;
        } else if (ADD_PAYMENT_METHOD.equals(CustomerPathParams.CustomerPaths.ADD_DEBIT_CARD_PAYMENT_METHOD)) {
            ADD_PAYMENT_METHOD = UPDATE_DEBIT_CARD_PAYMENT_METHOD;
        } else if (ADD_PAYMENT_METHOD.equals(CustomerPathParams.CustomerPaths.ADD_BANK_ACCOUNT_PAYMENT_METHOD)) {
            ADD_PAYMENT_METHOD = UPDATE_BANK_ACCOUNT_PAYMENT_METHOD;
        } else {
            throw new IllegalArgumentException("You have to specify a valid payment method (you can choose into credit, debit or bank)");
        }
        String paymentRef = savedPaymentMethodService.getResponse().getOperationConfirmation().getSavedPaymentMethodReference();
        invokeUpdatePaymentWithParameter(paymentRef, typeOfSubmitted);
    }

    private void invokeUpdatePaymentWithParameter(String paymentReference, String parameter) throws Throwable {
        CustomerPathParams profilePathParams = null;
        switch (parameter) {
            case "invalidID":
                profilePathParams = CustomerPathParams.builder().customerId(invalid).savedPaymentMethodReference(paymentReference).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
            case "invalidSession":
                HybrisService.theJSessionCookie.set("");
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).savedPaymentMethodReference(paymentReference).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
            case "mismatchIDSession":
                CustomerModel dbCustomer = customerProfileHelper.findAValidCustomerProfile();
                profilePathParams = CustomerPathParams.builder().customerId(dbCustomer.getUid()).savedPaymentMethodReference(paymentReference).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
            case "invalidPaymentReferenceId":
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).savedPaymentMethodReference(invalid).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
            default:
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).savedPaymentMethodReference(paymentReference).path((CustomerPathParams.CustomerPaths) ADD_PAYMENT_METHOD).build();
                break;
        }
        savedPaymentMethodService = hybrisServiceFactory.addSavedPaymentMethod(new PaymentMethodsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), profilePathParams, savedPaymentMethodRequestBody));
        savedPaymentMethodService.invoke();
    }

    public String getDefaultPaymentMethod() {
        return paymentModeDao.getDefaultPaymentorCustomer(testData.getData(CUSTOMER_ID));
    }

    public void removePayment(String typeOfSubmitted) throws Throwable {

        String paymentRef = savedPaymentMethodService.getResponse().getOperationConfirmation().getSavedPaymentMethodReference();
        removeSavedPaymentRequestBody = RemovePaymentFactory.aBasicAddBankPaymentDetails(paymentRef);
        invokeRemovePaymentWithParameter(typeOfSubmitted, paymentRef);
    }

    private void invokeRemovePaymentWithParameter(String invalidType, String paymentReference) {
        CustomerPathParams profilePathParams = null;

        CustomerPathParams.CustomerPaths removeSavedPayment = CustomerPathParams.CustomerPaths.REMOVE_SAVED_PAYMENT;
        HybrisHeaders hybrisHeaders = HybrisHeaders.getValid(testData.getChannel()).build();
        switch (invalidType) {
            case "customerID":
                profilePathParams = CustomerPathParams.builder().customerId("InvalidPaymmentId").path(removeSavedPayment).build();
                break;
            case "session":
                HybrisService.theJSessionCookie.set("");
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(removeSavedPayment).build();
                break;

            case "paymentRef":
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(removeSavedPayment).build();
                removeSavedPaymentRequestBody.setSavedPaymentMethodRef(paymentReference+"InvalidRefId");
                break;
            case "channel":
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(removeSavedPayment).build();
                hybrisHeaders = HybrisHeaders.getValid(testData.getChannel()).xPosId("blabla").build();
                break;
            default:
                profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(removeSavedPayment).build();
                break;
        }
        removeSavedPaymentService = hybrisServiceFactory.removeSavedPayment(new RemoveSavedPaymentRequest(hybrisHeaders, profilePathParams, removeSavedPaymentRequestBody));
        removeSavedPaymentService.invoke();
        testData.setData("removeSavedPaymentService",removeSavedPaymentService);

    }

    public void getSavedPaymentToChannel(String customerLogon){
        customerPathParams = CustomerPathParams.builder().customerId(customerLogon).path(GET_PAYMENT_METHOD).build();
        paymentMethodTypeService = hybrisServiceFactory.addPaymentType(new PaymentMethodsRequest(HybrisHeaders.getValid(testData.getChannel()).build(),customerPathParams, customerProfileQueryParams ));
        paymentMethodTypeService.invoke();
    }

    public void getSavedPaymentsForExpiredCards(String customerLogon){
        customerProfileQueryParams = CustomerProfileQueryParams.builder().excludeExpired("true").build();
        customerPathParams = CustomerPathParams.builder().customerId(customerLogon).path(GET_PAYMENT_METHOD).build();
        paymentMethodTypeService = hybrisServiceFactory.addPaymentType(new PaymentMethodsRequest(HybrisHeaders.getValid(testData.getChannel()).build(),customerPathParams, customerProfileQueryParams ));
        paymentMethodTypeService.invoke();
    }

    public void removePaymentFromReference(String savedPaymentMethodReference) throws Throwable {
        removeSavedPaymentRequestBody = RemovePaymentFactory.aBasicAddBankPaymentDetails(savedPaymentMethodReference);
        invokeRemovePaymentWithParameter("", savedPaymentMethodReference);
    }
}
