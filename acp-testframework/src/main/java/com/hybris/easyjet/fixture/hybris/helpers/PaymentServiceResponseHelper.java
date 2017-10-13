package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.CustomerProfileQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.BasketContent;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.CommitBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.PaymentMethod;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.RefundOrFee;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.BasketContentFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CommitBookingFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PaymentMethodFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.RecalculatePricesService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoopForSearchBooking;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by rajakm on 15/05/2017.
 */

@Component
public class PaymentServiceResponseHelper {

    @Getter
    @Setter
    private boolean staff = false;

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private RepriceBasketHelper repriceBasketHelper;

    private CommitBookingService commitBookingService;
    private CustomerProfileService customerProfileService;
    private CommitBookingRequest commitBookingRequest;
    private PaymentMethod paymentMethod;
    private Map<String, String> paymentDetails = new HashMap<>();
    private String theStaffCustomerUID;
    private Passengers passengers;
    private final TravellerHelper travellerHelper;
    private CustomerProfileResponse theStaffProfile;


    @Autowired
    public PaymentServiceResponseHelper(HybrisServiceFactory serviceFactory, TravellerHelper travellerHelper, BasketHelper basketHelper, CustomerProfileHelper customerProfileHelper, BookingHelper bookingHelper) {

        this.serviceFactory = serviceFactory;
        this.basketHelper = basketHelper;
        this.bookingHelper = bookingHelper;
        this.travellerHelper = travellerHelper;
    }

    public CommitBookingService getCommitBookingService() {
        return commitBookingService;
    }

    public void addFlightToBasket(String src, String dest, String fareType) throws Throwable {
        basketHelper.addFlightToBasket("1 Adult", src, dest, staff, fareType, null);
    }

    public void addFlightToBasketWithBookingType(String src, String dest, String bookingType, int adultCount) throws Throwable {
        String passengerMix = adultCount + " Adult";
        testData.setCurrency("GBP");
        basketHelper.addFlightToBasket(passengerMix, src, dest, staff, STANDARD, bookingType);

    }

    public void manageCommitBookingRequest(String paymentType, String incorrectInfo) throws EasyjetCompromisedException, InvocationTargetException, IllegalAccessException {
        createPaymentMethodFromCardType(paymentType, testData.getCurrency());
        modifyPaymentWithInvalidDetails(incorrectInfo);
        createRequestForCommitBooking(paymentType);
    }

    private void createRequestForCommitBooking(String paymentType) throws InvocationTargetException, IllegalAccessException, EasyjetCompromisedException {
        pollingLoop().untilAsserted(() ->
                assertThat(
                        basketHelper.getBasket(testData.getBasketId(), testData.getChannel())
                                .getOutbounds()
                                .stream()
                                .flatMap(flights -> flights.getFlights().stream())
                                .flatMap(flight -> flight.getPassengers().stream())
                                .allMatch(passenger -> passenger.getPassengerDetails().getName().getFirstName() != null))
                        .isTrue());
        CommitBookingRequestBody commitBookingRequestBody = bookingHelper.aBooking(basketHelper.getBasketService().getResponse(), paymentMethod);
        if (CommonConstants.PUBLIC_API_B2B_CHANNEL.equalsIgnoreCase(testData.getChannel())) {
            BasketContent basketContent = BasketContentFactory.getBasketContentForExistingCustomer(basketHelper.getBasketService().getResponse().getBasket(), passengers.getPassengers().get(0));
            RecalculatePricesService recalculatePricesService = repriceBasketHelper.invokeRepriceBasket(testData.getChannel(), null, basketContent);
            Basket basket = basketHelper.getBasket(recalculatePricesService.getResponse().getBasket().getCode(),testData.getChannel());
            createPaymentMethodFromCardType(paymentType, basket.getCurrency().getCode());
            commitBookingRequestBody = bookingHelper.aBooking(basketHelper.getBasketService().getResponse(), paymentMethod);
            commitBookingRequestBody.setBasketContent(BasketContentFactory.getBasketContentForExistingCustomer(recalculatePricesService.getResponse().getBasket(), passengers.getPassengers().get(0)));

        }
        if ("true".equals(testData.getData(XTEST)) || "true".equals(System.getProperty("mocked"))) {
            commitBookingRequest = new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).xTest(testData.getData(XTEST)).build(), commitBookingRequestBody);
        } else {
            commitBookingRequest = new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), commitBookingRequestBody);
        }
    }

    public void buildRequestToCommitBookingWithCardDetails(String paymentType) throws Throwable{
        createPaymentMethodFromCardType(paymentType, testData.getCurrency());
        createRequestForCommitBooking(paymentType);
    }

    public void createBookingWithValidPaymentDetails(String currency, String xTest, String paymentType) throws Throwable {
        String customerID = updatePassengerAndGetCustomerId();
        getCustomerProfile(customerID);
        testData.setCurrency(currency);
        testData.setData(XTEST, xTest);
        buildRequestToCommitBookingWithCardDetails(paymentType);
    }

    private void createPaymentMethodFromCardType(String type, String currency) throws EasyjetCompromisedException {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        if ("card".equals(type)) {
            paymentMethod = PaymentMethodFactory.generatePaymentRequestForCard(basketHelper.getBasketService().getResponse(), type, paymentDetails, currency);
        } else if ("elv".equals(type)) {
            paymentMethod = PaymentMethodFactory.generatePaymentRequestForElv(basketHelper.getBasketService().getResponse(), type, paymentDetails, currency);
        }
    }

    public String updatePassengerAndGetCustomerId() throws Throwable {
        String customerID = "";
        if (staff) {
            passengers = travellerHelper.createValidRequestToAddAllPassengersForBasketWithSignificantOther(basketHelper.getBasketService().getResponse(), theStaffProfile.getCustomer().getAdvancedProfile().getSignificantOthers().getPassengers().get(0));
            basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());

            customerID = theStaffCustomerUID;
        } else {
            passengers = travellerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse());
            basketHelper.updatePassengersForChannel(passengers, testData.getChannel(),
                    basketHelper.getBasketService().getResponse().getBasket().getCode());

            customerHelper.createRandomCustomer(testData.getChannel());
            customerID = testData.getData(CUSTOMER_ID);
        }
        return customerID;
    }

    public void getCustomerProfile(String customerID) {
        CustomerPathParams profilePathParams = CustomerPathParams.builder()
                .customerId(customerID)
                .path(PROFILE)
                .build();
        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken())
                .build(), profilePathParams));
        customerProfileService.invoke();

        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
    }

    public void sendCommitBookingRequest() throws EasyjetCompromisedException {
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
    }

    public void sendSuccessCommitBookingRequest() throws TimeoutException {
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        final int[] attempts = {3};
        pollingLoopForSearchBooking().until(() -> {
            commitBookingService.invoke();
            attempts[0]--;
            return commitBookingService.getStatusCode() == 200 || attempts[0] == 0;
        });
        commitBookingService.getResponse();
    }

    public void verifyCommitBookingIsSuccessful() throws InterruptedException {
        commitBookingService.assertThat().gotAValidResponse();
    }

    public void verifyCommitBookingIsFailed() {
        commitBookingService.assertThatErrorsOverride();
    }

    public void addValuesInToMap(String[] paymentdetails, String paymentType) {
        if (paymentdetails.length == 6) {
            if ("card".equals(paymentType)) {
                paymentDetails.put("cardType", paymentdetails[0].trim());
                paymentDetails.put("cardNumber", paymentdetails[1].trim());
                paymentDetails.put("cardSecNumber", paymentdetails[2].trim());
                paymentDetails.put("cardExpiryMonth", paymentdetails[3].trim());
                paymentDetails.put("cardExpiryYear", paymentdetails[4].trim());
                paymentDetails.put("cardHolderName", paymentdetails[5].trim());
            } else if ("elv".equals(paymentType)) {
                paymentDetails.put("accountHolderName", paymentdetails[0].trim());
                paymentDetails.put("accountNumber", paymentdetails[1].trim());
                paymentDetails.put("bankCity", paymentdetails[2].trim());
                paymentDetails.put("bankCode", paymentdetails[3].trim());
                paymentDetails.put("bankCountryCode", paymentdetails[4].trim());
                paymentDetails.put("bankName", paymentdetails[5].trim());
            }
        }
    }

    public void modifyPaymentWithInvalidDetails(String invalidDetails) {
        switch (invalidDetails) {
            case "InvalidTransactionID":
                commitBookingRequest.getHeaders().setXClientTransactionId("X");
                break;
            case "InvalidCountryCode":
                commitBookingRequest.getHeaders().setXCountry("INVALID");
                break;
            case "InvalidPaymentCode":
                paymentMethod.setPaymentCode("INVALID");
                break;
            case "InvalidPaymentMethod":
                paymentMethod.setPaymentMethod("INVALID");
                break;
            case "InvalidExpiredDate":
                paymentMethod.getCard().setCardExpiryMonth("0");
                paymentMethod.getCard().setCardExpiryYear("0000");
                break;
            case "InvalidSecurityCode":
                paymentMethod.getCard().setCardSecurityNumber("XXX");
                break;
            case "InvalidCardIssueNumber":
                paymentMethod.getCard().setCardIssueNumber("INVALID");
                break;
            case "InvalidCardType":
                paymentMethod.getCard().setCardType("INVALID");
                break;
            case "InvalidAccountHolderName":
                paymentMethod.getBankAccount().setAccountHolderName("INVALID_NAME");
                break;
            case "InvalidAccountNumber":
                paymentMethod.getBankAccount().setAccountNumber("XXX-XXX-XXX");
                break;
            case "InvalidBankCode":
                paymentMethod.getBankAccount().setBankCode("XXX-XXX-XXX");
                break;
            case "InvalidBankName":
                paymentMethod.getBankAccount().setBankName("");
                break;
            case "InvalidPlaceholderName":
                paymentMethod.getBankAccount().setAccountHolderName("");
                break;
            case "InvalidCurrency":
                paymentMethod.setPaymentCurrency("INVALID");
                break;
            case "InvalidAmount":
                paymentMethod.setPaymentAmount(0.0);
                break;
            default:
                break;
        }
    }

    public void prepareStatementForCommitBookingStaffCustomer(boolean staff, String theStaffCustomerUID, String user, String pwd) {
        this.staff = staff;
        this.theStaffCustomerUID = theStaffCustomerUID;
        customerHelper.loginWithValidCredentials(testData.getChannel(), user, pwd, false);

        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(theStaffCustomerUID).path(PROFILE).build();
        customerProfileService = serviceFactory.getCustomerProfile(
                new ProfileRequest(HybrisHeaders.getValid(testData.getChannel()).prefer("FULL").build(), profilePathParams, CustomerProfileQueryParams.builder().sections("dependants").build())
        );
        customerProfileService.invoke();
        theStaffProfile = customerProfileService.getResponse();
    }

    public void createBasicBookingRequestAndCommitIt() throws Throwable {
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse()), testData.getChannel(), basketHelper.getBasketService().getResponse()
                .getBasket()
                .getCode());
        //get a customer profile id
        customerHelper.createRandomCustomer(testData.getChannel());
        getCustomerProfile(testData.getData(CUSTOMER_ID));

        PaymentMethod paymentMethods = PaymentMethodFactory.generateDebitCardPaymentMethod(basketHelper.getBasketService().getResponse().getBasket());
        commitBookingRequest = new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                CommitBookingFactory.aBooking(basketHelper.getBasketService().getResponse().getBasket(), Arrays.asList(paymentMethods), true));
    }

    public void prepareRefundAndFeesData(){
        CommitBookingRequestBody commitBookingRequestBody = (CommitBookingRequestBody)commitBookingRequest.getRequestBody();

        Basket basket = basketHelper.getBasket(
                basketHelper.getBasketService().getResponse().getBasket().getCode(),
                testData.getChannel()
        );
            Double refundAmount = basket.getPriceDifference().getAmountWithDebitCard();

        GetBookingResponse getOriginalBookingResponse = bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID),testData.getData(SerenityFacade.DataKeys.CHANNEL));
        List<RefundOrFee> refundsAndFees = new ArrayList<>();
        GetBookingResponse.Payment originalPayment = getOriginalBookingResponse.getBookingContext().getBooking().getPayments().stream().findFirst().get();
        RefundOrFee refundOrFee  = RefundOrFee.builder().amount(Math.abs(refundAmount)).currency(basket.getCurrency().getCode()).type(testData.getData(SerenityFacade.DataKeys.REFUND)).primaryReasonCode(testData.getData(SerenityFacade.DataKeys.REFUND)).originalPaymentMethod(paymentMethod.getPaymentMethod()).originalPaymentMethodContext(originalPayment.getTransactionId()).build();
        refundsAndFees.add(refundOrFee);
        commitBookingRequestBody.setRefundsAndFees(refundsAndFees);
    }
}
