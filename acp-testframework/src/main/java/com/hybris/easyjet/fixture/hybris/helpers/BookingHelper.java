package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.PaymentMethodsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.CancelBookingRefundRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.*;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.BasketContentFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CommitBookingFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PaymentMethodFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.managebooking.PaymentBalanceRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.CancelBookingRefundRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.PaymentMethodsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Profile;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.RegisterCustomerResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;
import com.hybris.easyjet.fixture.hybris.invoke.services.managebooking.PaymentBalanceService;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoopForSearchBooking;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.CALCULATE_PAYMENT_BALANCE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.CANCEL_BOOKING;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.GETBOOKING;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;
import static org.assertj.core.api.Assertions.assertThat;

@Component
public class BookingHelper {

    @Getter
    private final BasketHelper basketHelper;
    @Getter
    private CommitBookingService commitBookingService;
    @Getter
    private CancelBookingRefundService cancelBookingRefundService;

    @Setter
    private String paymentDetails;
    @Setter
    private String paymentType;

    private Map<String, String> paymentInfo = new HashMap<>();
    private String[] paymentData = null;

    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private PaymentMethodFactory paymentMethodFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private SavedPassengerHelper savedPassengerHelper;

    private final HybrisServiceFactory serviceFactory;
    private final TravellerHelper travellerHelper;
    private final CustomerHelper customerHelper;
    private final DealsInfoHelper dealsInfoHelper;
    private final ManageAdditionalFareToPassengerInBasketHelper manageAdditionalFareToPassengerInBasketHelper;
    private DealModel dealInfo;
    private CustomerProfileService customerProfileService;
    private GetBookingService getBookingService;
    private PaymentBalanceService paymentBalanceService;
    private PaymentMethod paymentMethod;
    private BasketsResponse basketsResponse;
    private Basket basket;
    private BasketContent basketContent;
    private CommitBookingRequestBody commitBooking;
    private Passengers passengers;
    private AddFlightRequestBody addFlight;
    private RegisterCustomerResponse.OperationConfirmation customer;
    private DealModel dealsInfo;
    private BookingPathParams bookingPathParams;
    private BasketPathParams basketPathParams;
    private CancelBookingRefundRequestBody cancelBookingRefundRequestBody;
    private PaymentBalanceRequestBody paymentBalanceRequestBody;
    private FlightsService flightsService;

    @Autowired
    public BookingHelper(
            HybrisServiceFactory serviceFactory,
            TravellerHelper travellerHelper,
            BasketHelper basketHelper,
            CustomerHelper customerHelper,
            DealsInfoHelper dealsInfoHelper,
            ManageAdditionalFareToPassengerInBasketHelper manageAdditionalFareToPassengerInBasketHelper
    ) {

        this.serviceFactory = serviceFactory;
        this.travellerHelper = travellerHelper;
        this.basketHelper = basketHelper;
        this.customerHelper = customerHelper;
        this.dealsInfoHelper = dealsInfoHelper;
        this.manageAdditionalFareToPassengerInBasketHelper = manageAdditionalFareToPassengerInBasketHelper;
    }

    private static MemberShipModel getEJPlusDetails() {
        return MemberShipModel.builder()
                .ejMemberShipNumber("S008888")
                .expiryDate("2058-12-31 00:00:00.000000")
                .firstname("Andrea")
                .lastname("Rossi")
                .status("8796133851227")
                .build();
    }

    public CommitBookingRequestBody getCommitBookingRequest() {

        return commitBooking;
    }

    public BasketsResponse getBasketUsed() {

        return basketsResponse;
    }

    public BasketContent getBasketContent() {
        return basketContent;
    }

    public GetBookingService getGetBookingService() {

        return getBookingService;
    }

    public CustomerProfileService getCustomerProfileService() {

        return customerProfileService;
    }

    public BookingConfirmationResponse createNewBooking(CommitBookingRequest request) throws EasyjetCompromisedException {
        commitTheBooking(request);
        testData.setData(SerenityFacade.DataKeys.GET_BOOKING_RESPONSE, commitBookingService.getResponse());
        return commitBookingService.getResponse();
    }

    public void createBooking(CommitBookingRequest request) throws EasyjetCompromisedException {
        commitTheBooking(request);
    }

    public void commitTheBooking(CommitBookingRequest request) throws EasyjetCompromisedException {
        commitBookingService = serviceFactory.commitBooking(request);
        commitBookingService.invoke();
        verifyRestrictedRule();

        testData.setData(SERVICE, commitBookingService);

        // We need to check the status of the response. We can not call getResponse or checking if it is null, otherwise if it is a negative, will throw exception
        if (commitBookingService.getStatusCode() == 200) {
            testData.setData(BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
            testData.setData(SerenityFacade.DataKeys.BOOKING_STATUS, commitBookingService.getResponse().getBookingConfirmation().getBookingStatus());
        }
    }

    public BookingConfirmationResponse createNewBooking(String channel, String mix) throws Throwable {
        CommitBookingRequest request = createNewBookingRequestMockPayment(channel, mix);
        return createNewBooking(request);
    }

    public BookingConfirmationResponse createNewBookingWithReturnFlight(String channel, String mix) throws Throwable {
        CommitBookingRequest request = createNewBookingRequestWithReturnFlight(channel, mix);
        return createNewBooking(request);
    }

    public BookingConfirmationResponse createBookingCustomerIsTravelling(String channel, String mix) throws Throwable {
        CommitBookingRequest request = createBookingForCustomerIsTravelling(channel, mix);
        return createNewBooking(request);
    }

    public BookingConfirmationResponse createBookingWithReturnFlightAndAllProducts(String channel, String mix) throws Throwable {
        CommitBookingRequest request = createBookingRequestWithReturnFlightAndAllProducts(channel, mix);

        return createNewBooking(request);
    }

    public BookingConfirmationResponse createBooking(String mix, String paymentType, String paymentDetails) throws Throwable {
        CommitBookingRequest request = createNewBookingRequestWithSelectedPaymentType(mix, paymentType, paymentDetails, "Standard");
        return createNewBooking(request);
    }

    public BookingConfirmationResponse createNewBookingForPublicChannel(String criteria, String channel) throws Throwable {
        testData.setChannel(channel);
        CommitBookingRequest request = createNewBookingRequestForPublicChannel(criteria);
        HybrisService.theJSessionCookie.set(null);
        return createNewBooking(request);
    }

    public void createNewBookingPublicApiB2BChannel(String criteria) throws Throwable {
        CommitBookingRequest request = createNewBookingRequestForPublicChannel(criteria);
        HybrisService.theJSessionCookie.set(null);
        commitTheBooking(request);
    }

    public void createDuplicateBookingForPublicChannel(CommitBookingRequestBody bookingPayload, String channel) throws Throwable {
        CommitBookingRequest request = createDuplicateBookingRequestForPublicChannel(bookingPayload, channel);
        commitTheBooking(request);
    }

    public CommitBookingRequestBody aBooking(BasketsResponse basket, PaymentMethod... paymentMethodBody) {

        return CommitBookingFactory.aBooking(basket.getBasket(), Arrays.asList(paymentMethodBody), true,testData.getChannel());
    }

    public CommitBookingRequestBody aBookingWithOnlyRefundAndFee(BasketsResponse basket, Double paymentAmount, String currency, String reasonCode, String paymentContext, String pmtMethod) {

        return CommitBookingFactory.aBookingWithRefundOrFee(basket.getBasket(), true, paymentAmount, currency, reasonCode, paymentContext, pmtMethod);
    }

    private CommitBookingRequestBody aBookingWithRefundAndFee(BasketsResponse basket, List<PaymentMethod> paymentMethods) {

        return CommitBookingFactory.aBooking(basket.getBasket(), paymentMethods, true);
    }

    public BookingConfirmationResponse createCorporateBookingWithDealForPublicChannel(String criteria, String channel) throws Throwable {
        CommitBookingRequest request = createCorporateBookingWithDealRequestForPublicChannel(criteria, channel);
        HybrisService.theJSessionCookie.set(null);
        return createNewBooking(request);
    }

    public void createNewBookingRequest(String passenger, String channel) throws Throwable {
        flightHelper.setSectors();
        basketHelper.myBasketContainsAFlightWithPassengerMix(passenger, channel, STANDARD, false);
        addFlight = basketHelper.getAddedFlight();
        basketsResponse = basketHelper.getBasketService().getResponse();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), channel, basketsResponse
                .getBasket()
                .getCode());
        paymentMethod = getPaymentMethods(channel);
    }

    private CommitBookingRequest createNewBookingRequestMockPayment(String channel, String mix) throws Throwable {
        flightHelper.setSectors();
        basketHelper.myBasketContainsAFlightWithPassengerMix(mix);
        basketsResponse = basketHelper.getBasketService().getResponse();
        testData.setData("PassengersOnFlight", basketsResponse.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).collect(Collectors.toList()));
        basket = basketHelper.getBasket(basketsResponse.getBasket().getCode(), testData.getChannel());
        testData.setData(SerenityFacade.DataKeys.BASKET_ID, basket.getCode());
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), channel, basketsResponse
                .getBasket()
                .getCode());

        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket);

        associateCustomerProfile(channel);


        return new CommitBookingRequest(HybrisHeaders.getValid(channel).build(), aBooking(basketsResponse, paymentMethod));
    }

    private CommitBookingRequest createNewBookingRequestWithReturnFlight(String channel, String mix) throws Throwable {
        flightHelper.setSectors();
        basketHelper.myBasketContainsAReturnFlightWithPassengerMix(mix, channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        basket = basketHelper.getBasket(basketsResponse.getBasket().getCode(), testData.getChannel());
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse, true), channel, basketsResponse
                .getBasket()
                .getCode());
        //get payment methods
        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket);
        associateCustomerProfile(channel);
        //commit booking
        return new CommitBookingRequest(HybrisHeaders.getValid(channel).build(), aBooking(basketsResponse, paymentMethod));
    }

    private CommitBookingRequest createBookingForCustomerIsTravelling(String channel, String mix) throws Throwable {
        flightHelper.setSectors();
        basketHelper.myBasketContainsAFlightWithPassengerMix(mix);
        basketsResponse = basketHelper.getBasketService().getResponse();
        basket = basketHelper.getBasket(basketsResponse.getBasket().getCode(), testData.getChannel());
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse, true), channel, basketsResponse
                .getBasket()
                .getCode());
        //get payment methods
        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket);
        associateCustomerProfile(channel);
        String lastName = customerProfileService.getResponse().getCustomer().getBasicProfile().getPersonalDetails().getLastName();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToUpdateFirstPassengersForBasket(basketsResponse, lastName, true), channel, basketsResponse
                .getBasket()
                .getCode());
        //commit booking
        return new CommitBookingRequest(HybrisHeaders.getValid(channel).build(), aBooking(basketsResponse, paymentMethod));
    }

    private CommitBookingRequest createBookingRequestWithReturnFlightAndAllProducts(String channel, String mix) throws Throwable {
        flightHelper.setSectors();
        basketHelper.myBasketContainsAReturnFlightWithPassengerMix(mix, channel);
        basketHoldItemsHelper.addHoldItemWithExcessWeightToAllFlightsAllPassenger(testData.getChannel(), "Hold Bag", 1);
        basketHoldItemsHelper.buildRequestToAddSportEquipment(testData.getChannel());
        basketHoldItemsHelper.invokeServiceAddSportItems(testData.getChannel());

        basketsResponse = basketHelper.getBasketService().getResponse();
        basket = basketHelper.getBasket(basketsResponse.getBasket().getCode(), testData.getChannel());

        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse, true), channel, basketsResponse
                .getBasket()
                .getCode());
        //get payment methods
        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket);
        //get customer profile
        associateCustomerProfile(channel);
        //commit booking
        return new CommitBookingRequest(HybrisHeaders.getValid(channel).build(), aBooking(basketsResponse, paymentMethod));
    }

    private CommitBookingRequest createNewBookingRequestWithSelectedPaymentType(String passengerMix, String paymentMethods, String value, String requiredFare) throws Throwable {

        if (paymentMethods.contains("elv")) {
            testData.setCurrency("EUR");
            testData.setOutboundDate(new DateFormat().today().addDay(21));
            testData.setInboundDate(new DateFormat().today().addDay(21));
        }

        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        basketHelper.addReturnFlightWithTaxToBasketAsChannelJourneyType(flightsService, passengerMix, testData.getCurrency(), testData.getChannel(), "Standard", "SINGLE");

        basketsResponse = basketHelper.getBasketService().getResponse();
        basket = basketHelper.getBasketService().getResponse().getBasket();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), testData.getChannel(), basketsResponse
                .getBasket()
                .getCode());

        basketsResponse = basketHelper.getBasketResponse(basket.getCode(), testData.getChannel());

        customerHelper.createRandomCustomer(testData.getChannel());
        getCustomer(testData.getData(CUSTOMER_ID), testData.getChannel());

        paymentData = value.split("-");
        addValuesInToMap(paymentData, paymentMethods);

        //get payment methods
        if ("card".equals(paymentMethods)) {
            paymentMethod = PaymentMethodFactory.generatePaymentRequestForCard(basketsResponse, paymentMethods, paymentInfo, "GBP");
        } else if ("elv".equals(paymentMethods)) {
            paymentMethod = PaymentMethodFactory.generatePaymentRequestForElv(basketsResponse, CommonConstants.ELV, paymentInfo, testData.getCurrency());

        }

        //get customer profile
        associateCustomerProfile(testData.getChannel());
        //commit booking
        return new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), aBooking(basketsResponse, paymentMethod));
    }

    public CommitBookingRequest createNewCorporateBookingRequestMockPayment(String channel, String option) throws Throwable {
        flightHelper.setSectors();
        //get the deal information
        DealModel dealInfo = null;

        if ("valid".equalsIgnoreCase(option)) {
            dealInfo = dealsInfoHelper.findAValidDeals();
        }
        //basket with seatmap information
        basketHelper.myBasketContainsAFlightWithPassengerMixWithDeal(CommonConstants.ONE_ADULT, channel, dealInfo);
        basketsResponse = basketHelper.getBasketService().getResponse();
        //update passenger details to basket
        passengers = travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse);
        customerHelper.createRandomCustomer(channel);
        basketHelper.updatePassengersForChannel(passengers, channel, basketsResponse.getBasket().getCode());
        basket = basketsResponse.getBasket();
        //get customer profile
        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket);
        //get customer profile
        associateCustomerProfile(channel);
        //commit booking
        if (dealInfo != null) {
            return new CommitBookingRequest(HybrisHeaders.getValidWithDealInfo(channel, dealInfo.getSystemName(), dealInfo
                    .getOfficeId(), dealInfo
                    .getCorporateId()).build(), aBooking(basketsResponse, paymentMethod));
        } else {
            return new CommitBookingRequest(HybrisHeaders.getValidWithDealInfo(channel, "test", "test", "test")
                    .build(), aBooking(basketsResponse, paymentMethod));
        }
    }

    private PaymentMethod getPaymentMethods(String channel) throws Throwable {

        customerHelper.createRandomCustomer(channel);
        PaymentMethodsQueryParams paymentMethodsQueryParams = PaymentMethodsQueryParams.builder()
                .basketId(basketHelper.getBasketService().getResponse().getBasket().getCode())
                .bookingTypeCode("standard_customer")
                .customerId(testData.getData(CUSTOMER_ID))
                .build();
        PaymentMethodsService paymentMethodsService = serviceFactory.getPaymentMethods(new PaymentMethodsRequest(HybrisHeaders.getValid(channel)
                .build(), paymentMethodsQueryParams));
        paymentMethodsService.invoke();
        paymentMethodsService.assertThat().paymentMethodsWereReturned();
        PaymentMethodsResponse paymentMethodsAvailable = paymentMethodsService.getResponse();


        return PaymentMethodFactory.generateValidPaymentMethodBody(paymentMethodsAvailable.getPaymentMethods(), basketsResponse.getBasket());
    }

    private PaymentMethod getMissParameterPaymentMethods(String parameter) throws EasyjetCompromisedException {
        return PaymentMethodFactory.generatePaymentMethodBodyDebitCardWithMissingParameter(basketsResponse, parameter);
    }

    private PaymentMethod getValidPaymentMethods() throws EasyjetCompromisedException {
        return PaymentMethodFactory.generateDebitCardPaymentMethod(basketsResponse.getBasket());
    }

    private List<PaymentMethod> getMultiplePaymentMethods(int numberOfPayment, String paymentDetails, String paymentType) throws EasyjetCompromisedException {
        return paymentMethodFactory.generateMultiplePaymentMethod(basketsResponse.getBasket(), numberOfPayment, paymentDetails, paymentType);
    }

    private List<PaymentMethod> getMultiplePaymentMethodsBalance(int numberOfPayment, String paymentDetails) throws EasyjetCompromisedException {
        return paymentMethodFactory.generateMultiplePaymentMethodCalculatePaymentBalance(basketsResponse.getBasket(), paymentBalanceService.getResponse(), numberOfPayment, paymentDetails);
    }

    private List<PaymentMethod> getMultiplePaymentMethodsCreditDebit(int numberOfPayment, String paymentDetails) throws EasyjetCompromisedException {
        return paymentMethodFactory.generateMultiplePaymentMethodCreditDebit(basketsResponse.getBasket(), numberOfPayment, paymentDetails);
    }

    public GetBookingResponse getBookingDetails(String bookingRef, String channel) {
        BookingPathParams params = BookingPathParams.builder().bookingId(bookingRef).build();
        getBookingService = serviceFactory.getBookings(
                new GetBookingRequest(
                        HybrisHeaders.getValid(channel).build(),
                        params));

        getBookingInvokeWithPolling();

        testData.setBookingResponse(getBookingService.getResponse());
        testData.setData(SerenityFacade.DataKeys.BOOKING_STATUS, getBookingService.getResponse().getBookingContext().getBooking().getBookingStatus());
        testData.setData(SerenityFacade.DataKeys.BOOKING_ID, getBookingService.getResponse().getBookingContext().getBooking().getBookingReference());
        return getBookingService.getResponse();
    }

    private void getBookingInvokeWithPolling() {
        int[] attempts = {5};
        pollingLoop().until(() -> {
            getBookingService.invoke();
            attempts[0]--;
            return getBookingService.getStatusCode() == 200 || attempts[0] == 0;
        });
    }

    public CommitBookingRequest manageBookingFromBasketResponse(String amount, String typeOfManageBooking, int numberOfPayment) throws Throwable {
        basketsResponse = basketHelper.getBasketResponse(testData.getBasketId(), testData.getChannel());
        return manageBooking(amount, typeOfManageBooking, numberOfPayment, testData.getBookingType());
    }

    private CommitBookingRequest manageBooking(String amount, String typeOfBooking, int numberOfPayment, String bookingType) throws Throwable {
        if (typeOfBooking.startsWith("Invalid") || typeOfBooking.startsWith("Payment")) {
            //get valid payment methods to manage
            paymentMethod = getValidPaymentMethods();
            commitBooking = aBooking(basketsResponse, paymentMethod);
            modifyRequestWitInvalidFieldValue(typeOfBooking);

        } else if (typeOfBooking.startsWith("Missing")) {
            //get payment methods missing parameter
            paymentMethod = getMissParameterPaymentMethods(typeOfBooking);
            commitBooking = aBooking(basketsResponse, paymentMethod);

        } else if (typeOfBooking.startsWith("Multiple")) {
            List<PaymentMethod> paymentMethodList = getMultiplePaymentMethods(numberOfPayment, paymentDetails, paymentType);
            commitBooking = aBookingWithRefundAndFee(basketsResponse, paymentMethodList);
            if (typeOfBooking.contains("Invalid")) {
                paymentMethodList.forEach(payment ->
                        payment.setPaymentAmount(1.1)
                );
            }
        } else if (typeOfBooking.startsWith("Balance")) {
            //for multiple payment methods inclusive of Credit Card and Calculate Payment Balance
            List<PaymentMethod> paymentMethodList = new ArrayList<>();
            if (!testData.getChannel().equalsIgnoreCase(PUBLIC_API_B2B_CHANNEL)) {
                paymentMethodList = getMultiplePaymentMethodsBalance(numberOfPayment, paymentDetails);
                if (CommonConstants.INCORRECT.equalsIgnoreCase(amount)) {
                    paymentMethodList.get(0).setPaymentAmount(paymentMethodList.get(0).getPaymentAmount() + 1.0);
                }
            } else if (testData.getChannel().equalsIgnoreCase(PUBLIC_API_B2B_CHANNEL)) {
                paymentMethodList = getMultiplePaymentMethodsCreditDebit(numberOfPayment, paymentDetails);
                if (CommonConstants.INCORRECT.equalsIgnoreCase(amount)) {
                    paymentMethodList.get(0).setPaymentAmount(paymentMethodList.get(0).getPaymentAmount() - 1.0);
                }
            }
            commitBooking = aBookingWithRefundAndFee(basketsResponse, paymentMethodList);
            if (testData.getChannel().equalsIgnoreCase(PUBLIC_API_B2B_CHANNEL)) {
                commitBooking.setBasketContent(BasketContentFactory.getBasketContent(basketsResponse.getBasket()));
                commitBooking.setBasketCode(null);
                clearBasket(basketsResponse.getBasket().getCode());
            }
        } else { // valid booking
            paymentMethod = getValidPaymentMethods();
            commitBooking = aBooking(basketsResponse, paymentMethod);
        }
        if (bookingType.equalsIgnoreCase(CommonConstants.BUSINESS)) {
            if(testData.keyExist(WANT_DEAL) && !(Boolean) testData.getData(WANT_DEAL)) { // not necessary when we set BUSINESS as booking type we want have deal info
                return new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID).build(), commitBooking);
            } else {
                if(Objects.isNull(dealInfo)) {
                    dealInfo = dealsInfoHelper.findAValidDeals();
                }
                return new CommitBookingRequest(HybrisHeaders.getValidWithDealInfo(testData.getChannel(), dealInfo.getSystemName(), dealInfo
                        .getOfficeId(), dealInfo
                        .getCorporateId()).build(), commitBooking);
            }
        } else {
            return new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID).build(), commitBooking);
        }
    }

    public void updatePassengerAndLinkCustomer(BasketsResponse basketsResponse) {
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), testData.getChannel(), basketsResponse
                .getBasket()
                .getCode());

        customerHelper.createRandomCustomer(testData.getChannel());
        getCustomer(testData.getData(CUSTOMER_ID), testData.getChannel());
    }

    public CommitBookingRequest createNewBookingRequestForError(String typeOfError, String passengerMix, String requiredFare, int numberOfPayment, String bookingType) throws Throwable {
        if (bookingType.equalsIgnoreCase(CommonConstants.BUSINESS)) {
            dealInfo = dealsInfoHelper.findAValidDeals();
            basketHelper.myBasketContainsAFlightWithPassengerMixWithDeal(CommonConstants.ONE_ADULT, testData.getChannel(), dealInfo);
        } else {
            basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), requiredFare, false);
        }
        basketsResponse = basketHelper.getBasketService().getResponse();
        updatePassengerAndLinkCustomer(basketsResponse);
        return manageBooking(null, typeOfError, numberOfPayment, bookingType);
    }

    public CommitBookingRequest createNewBookingRequestWithMultiplePaymentMethods(String amount, String typeOfBooking, String passengerMix, String requiredFare, int numberOfPayment, String bookingType, String paymentMethods) throws Throwable {

        if (paymentMethods.contains("elv")) {
            testData.setCurrency("EUR");
            testData.setOutboundDate(new DateFormat().today().addDay(15));
            testData.setInboundDate(new DateFormat().today().addDay(20));
        }

        if (bookingType.equalsIgnoreCase(CommonConstants.BUSINESS)) {
            dealInfo = dealsInfoHelper.findAValidDeals();
            basketHelper.myBasketContainsAFlightWithPassengerMixWithDeal(passengerMix, testData.getChannel(), dealInfo);
        } else {
            basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), requiredFare, false);
        }

        basketsResponse = basketHelper.getBasketService().getResponse();
        basket = basketHelper.getBasketService().getResponse().getBasket();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), testData.getChannel(), basketsResponse
                .getBasket()
                .getCode());

        basketsResponse = basketHelper.getBasketResponse(basket.getCode(), testData.getChannel());

        customerHelper.createRandomCustomer(testData.getChannel());
        getCustomer(testData.getData(CUSTOMER_ID), testData.getChannel());

        paymentBalanceCalculate(bookingType, paymentMethods);

        return manageBooking(amount, typeOfBooking, numberOfPayment, bookingType);
    }

    private void paymentBalanceCalculate(String bookingType, String paymentMethods) {
        if (!testData.getChannel().equalsIgnoreCase(CommonConstants.PUBLIC_API_B2B_CHANNEL)) {
            pollingLoop().untilAsserted(() -> {

                basketPathParams = BasketPathParams.builder().basketId(basketsResponse.getBasket().getCode()).path(CALCULATE_PAYMENT_BALANCE).build();
                paymentBalanceRequestBody = PaymentBalanceRequestBody.builder().paymentMethods(buildRequestWithPaymentMethods(basket, paymentMethods)).build();
                paymentBalanceCalculateService(bookingType);

                if (paymentBalanceService.getResponse().getRemainingBalance().getWithDebitCard() != 0) {
                    paymentBalanceRequestBody.getPaymentMethods().get(0).setPaymentAmount(paymentBalanceRequestBody.getPaymentMethods().get(0).getPaymentAmount() + paymentBalanceService.getResponse().getRemainingBalance().getWithDebitCard());
                    paymentBalanceCalculateService(bookingType);
                }

                assertThat(paymentBalanceService.getResponse().getRemainingBalance().getWithCreditCard()).isZero();
                assertThat(paymentBalanceService.getResponse().getRemainingBalance().getWithDebitCard()).isZero();

                Double feeAmount = 0.0;
                int i = 0;
                do {
                    if (Objects.nonNull(paymentBalanceService.getResponse().getProposedPayments().getPaymentMethods().get(i).getFeeAmount())) {
                        feeAmount = feeAmount + paymentBalanceService.getResponse().getProposedPayments().getPaymentMethods().get(i).getFeeAmount();
                    }
                    i++;
                } while (i < paymentBalanceService.getResponse().getProposedPayments().getPaymentMethods().size());

                testData.setData(SerenityFacade.DataKeys.CREDIT_CARD_FEE, feeAmount);
            });
        }
    }

    private void paymentBalanceCalculateService(String bookingType) {
        if (bookingType.equalsIgnoreCase(CommonConstants.BUSINESS)) {
            paymentBalanceService = serviceFactory.paymentBalanceService(new PaymentMethodsRequest(HybrisHeaders.getValidWithDealInfo(testData.getChannel(), dealInfo.getSystemName(), dealInfo.getOfficeId(), dealInfo.getCorporateId()).build(),
                    basketPathParams,
                    paymentBalanceRequestBody));
        } else {
            paymentBalanceService = serviceFactory.paymentBalanceService(new PaymentMethodsRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                    basketPathParams,
                    paymentBalanceRequestBody));
        }
        paymentBalanceService.invoke();
    }

    /**
     * The method build an array list of PaymentMethod starting from a string containing paymentMethods
     *
     * @param paymentMethods different method, max of 2, if you need more methods you need to modify the following method, considering the split of the basket amount
     * @return
     */
    private List<PaymentBalanceRequestBody.PaymentMethod> buildRequestWithPaymentMethods(Basket basket, String paymentMethods) {
        List<String> paymentMethodsCode = new ArrayList<>();
        if (paymentMethods.contains(";")) {
            paymentMethodsCode = Arrays.asList(paymentMethods.split(";"));
        } else {
            paymentMethodsCode.add(paymentMethods);
        }

        List<PaymentBalanceRequestBody.PaymentMethod> paymentMethodList = new ArrayList<>();
        for (String code : paymentMethodsCode) {
            Random r = new Random();
            BigDecimal paymentAmount = BigDecimal.valueOf(0 + (basket.getTotalAmountWithDebitCard() - 0) * r.nextDouble()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal size = BigDecimal.valueOf(paymentMethodsCode.size());
            paymentAmount = paymentAmount.divide(size, 2, BigDecimal.ROUND_HALF_UP);
            String balancePaymentType = "card";
            String paymentCode = code;
            if (code.equalsIgnoreCase("creditfilefund")) {
                balancePaymentType = "creditfilefund";
                paymentCode = "CF";
            }
            if (code.equalsIgnoreCase("elv")) {
                balancePaymentType = "elv";
                paymentCode = "EV";
            }
            paymentMethodList.add(PaymentBalanceRequestBody.PaymentMethod.builder().paymentMethod(balancePaymentType).paymentAmount(paymentAmount.doubleValue()).paymentCode(paymentCode).build());
        }
        return paymentMethodList;
    }

    public CommitBookingRequest createNewBookingRequestForPublicChannelWithError(BasketsResponse basketsResponse, String parameter) throws EasyjetCompromisedException, InvocationTargetException, IllegalAccessException {
        if (parameter.contains("BasketContent")) {
            basketContent = BasketContentFactory.getBasketContentWithInvalidOrMissingParameter(basketsResponse.getBasket(), parameter);
            commitBooking = CommitBookingFactory.aBookingPayloadWithBasketContent(basketContent);
        } else {
            List<PaymentMethod> paymentsList = new ArrayList<>();
            paymentsList.add(PaymentMethodFactory.generateDebitCardPaymentMethod(basketsResponse.getBasket()));
            basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
            commitBooking = CommitBookingFactory.aBookingPayloadWithBasketContent(basketContent);
            commitBooking.setPaymentMethods(paymentsList);
            modifyRequestWitInvalidFieldValue(parameter);
        }
        return new CommitBookingRequest(HybrisHeaders.getValid(CommonConstants.PUBLIC_API_B2B_CHANNEL).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID).build(), commitBooking);
    }

    private void modifyRequestWitInvalidFieldValue(String parameter) {
        switch (parameter) {
            case "InvalidBookingType":
                commitBooking.setBookingType(CommitBookingFactory.SAMPLE);
                break;
            case "InvalidBookingReason":
                commitBooking.setBookingReason(CommitBookingFactory.SAMPLE);
                break;
            case "PaymentMismatched":
                commitBooking.getPaymentMethods().get(0).setPaymentAmount(1.36);
                break;
            case "InvalidPaymentAmount":
                commitBooking.getPaymentMethods().get(0).setPaymentAmount(-1.00);
                break;
            case "InvalidPaymentMethod":
                commitBooking.getPaymentMethods().get(0).setPaymentMethod("InvalidPaymentMethod");
                break;
            case "InvalidPaymentCode":
                commitBooking.getPaymentMethods().get(0).setPaymentCode("InvalidPaymentCode");
                break;
            case "MissingPaymentMethod":
                commitBooking.setPaymentMethods(null);
                break;
            case "MissingPaymentCode":
                commitBooking.getPaymentMethods().get(0).setPaymentCode(null);
                break;
            case "MissingPaymentAmount":
                commitBooking.getPaymentMethods().get(0).setPaymentAmount(null);
                break;
            case "MissingPaymentCurrency":
                commitBooking.getPaymentMethods().get(0).setPaymentCurrency(null);
                break;
            case "InvalidBasketReference":
                commitBooking.setBasketCode(CommitBookingFactory.SAMPLE);
                break;
            default:
                break;
        }
    }

    /**
     * This method create a booking from scratch
     *
     * @param theChannel channel to use
     * @return CommitBookingRequest
     * @throws Throwable
     */
    public CommitBookingRequest createNewBookingRequestForChannel(String theChannel) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(CommonConstants.ONE_ADULT, theChannel, STANDARD, false);
        basketsResponse = basketHelper.getBasketService().getResponse();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), theChannel, basketsResponse
                .getBasket()
                .getCode());

        customerHelper.createRandomCustomer(theChannel);
        getCustomer(testData.getData(CUSTOMER_ID), theChannel);

        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket);
        return new CommitBookingRequest(HybrisHeaders.getValid(theChannel).build(), aBooking(basketsResponse, paymentMethod));
    }

    public CommitBookingRequest createNewBookingRequestForChannelBasedOnBasket(BasketsResponse basket, String channel) throws Throwable {
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basket), channel, basket
                .getBasket()
                .getCode());

        customerHelper.createRandomCustomer(channel);
        return getCommitBookingRequest(basket, channel);
    }

    private CommitBookingRequest getCommitBookingRequest(BasketsResponse basket, String channel) throws EasyjetCompromisedException {
        getCustomer(testData.getData(CUSTOMER_ID), channel);
        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket.getBasket());
        pollingLoop().untilAsserted(() ->
                assertThat(
                        basketHelper.getBasket(testData.getBasketId(), channel)
                                .getOutbounds()
                                .stream()
                                .flatMap(flights -> flights.getFlights().stream())
                                .flatMap(flight -> flight.getPassengers().stream())
                                .allMatch(passenger -> passenger.getPassengerDetails().getName().getFirstName() != null))
                        .isTrue());

        return new CommitBookingRequest(HybrisHeaders.getValid(channel).build(), aBooking(basket, paymentMethod));
    }

    public CommitBookingRequest createBookingRequestWithNameField(BasketsResponse basket, String channel, String firstName, String lastName) throws Throwable {
        Passengers validRequestToAddAllPassengersForBasket = travellerHelper.createValidRequestToAddAllPassengersForBasket(basket);

        customerHelper.createRandomCustomer(channel);

        validRequestToAddAllPassengersForBasket.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getName().setFirstName(firstName);

        validRequestToAddAllPassengersForBasket.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getName().setLastName(lastName);

        basketHelper.updatePassengersForChannel(validRequestToAddAllPassengersForBasket, channel, basket.getBasket().getCode());

        return getCommitBookingRequest(basket, channel);
    }

    public CommitBookingRequest createNewBookingRequestForChannelBasedOnBasket(String basketId, String channel) throws Throwable {
        return createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketResponse(basketId, channel), channel);
    }

    public CommitBookingRequest createNewBookingRequestForChannelBasedOnBasket(BasketsResponse basket) throws EasyjetCompromisedException {
        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket.getBasket());
        return new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), aBooking(basket, paymentMethod));
    }

    public CommitBookingRequest createNewBookingRequestForChannelBasedOnBasketForGivenFlight(BasketsResponse basket, String channel) throws Throwable {
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddPassengersForBasket(basket), channel, basket
                .getBasket()
                .getCode());
        //get a customer profile id
        customerHelper.createRandomCustomer(channel);
        getCustomer(testData.getData(CUSTOMER_ID), channel);

        PaymentMethod paymentMethods = PaymentMethodFactory.generateDebitCardPaymentMethod(basket.getBasket());
        return new CommitBookingRequest(HybrisHeaders.getValid(channel).build(), aBooking(basket, paymentMethods));
    }

    private void getCustomer(String customerId, String channel) {
        CustomerPathParams profilePathParams = CustomerPathParams.builder()
                .customerId(customerId)
                .path(PROFILE)
                .build();
        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValidWithToken(channel, testData.getAccessToken())
                .build(), profilePathParams));
        customerProfileService.invoke();
    }

    public CommitBookingRequest createNewBookingRequestForChannelBasedOnBasket(BasketsResponse basket, String customerId, String channel, boolean Apis) throws EasyjetCompromisedException {
        if (Apis) {
            basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddPassengersForBasket(basket), channel, basket
                    .getBasket()
                    .getCode());
        } else {
            basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basket, false), channel, basket
                    .getBasket()
                    .getCode());
        }
        // if the channel is AD, the customer will be already in session as agent
        if(!testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CHANNEL) && !testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CUSTOMER_SERVICE)) {
            getCustomer(customerId, channel);
        }

        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket.getBasket());
        return new CommitBookingRequest(HybrisHeaders.getValid(channel).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID).build(), aBooking(basket, paymentMethod));
    }

    public void createAnotherBookingRequestForAmendableBasket(BasketsResponse basket, double paymentAmount, boolean ignoreErrors) throws EasyjetCompromisedException {
        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket.getBasket());
        paymentMethod.setPaymentAmount(paymentAmount);
        CommitBookingRequest request = new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID).build(), aBooking(basket, paymentMethod));
        commitBookingService = serviceFactory.commitBooking(request);
        if (ignoreErrors == false) {
            invokeWithRetry();
            verifyRestrictedRule();
            // If it's a negative test booking status wont be set.
            if (commitBookingService.getResponse() != null) {
                testData.setData(BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
                testData.setData(SerenityFacade.DataKeys.BOOKING_STATUS, commitBookingService.getResponse().getBookingConfirmation().getBookingStatus());
            }
        } else {
            commitBookingService.invoke();
        }
        testData.setData(SERVICE, commitBookingService);
    }

    public void commitBookingWithPartialRefund(BasketsResponse basket, Double paymentAmount, String reasonCode, String paymentContext, String pmtMethod, boolean withError) throws EasyjetCompromisedException {
        CommitBookingRequestBody requestBody = aBookingWithOnlyRefundAndFee(basket, paymentAmount, testData.getCurrency(), reasonCode, paymentContext, pmtMethod);
        CommitBookingRequest request = new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID).build(), requestBody);
        commitBookingService = serviceFactory.commitBooking(request);
        if(!withError) {
            invokeWithRetry();
            verifyRestrictedRule();
            // If it's a negative test booking status wont be set.
            if (commitBookingService.getResponse() != null) {
                testData.setData(BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
                testData.setData(SerenityFacade.DataKeys.BOOKING_STATUS, commitBookingService.getResponse().getBookingConfirmation().getBookingStatus());
            }
        }else{
            int[] noOfRetry = {5};
            try {
                pollingLoop().until(() -> {
                    commitBookingService.invoke();
                    Thread.sleep(500);
                    noOfRetry[0]--;
                    return commitBookingService.getStatusCode() != 500 || noOfRetry[0] == 0;
                });
            } catch (ConditionTimeoutException ct) {
//                do nothing
            }
        }
        testData.setData(SERVICE, commitBookingService);
    }

    private void invokeWithRetry() {
        int[] noOfRetry = {5};
        try {
            pollingLoop().until(() -> {
                commitBookingService.invoke();
                noOfRetry[0]--;
                return commitBookingService.getStatusCode() == 200 || noOfRetry[0] == 0;
            });
        } catch (ConditionTimeoutException ct) {
            commitBookingService.getResponse();
        }

    }

    public CommitBookingRequest createNewBookingRequestForChannelBasedOnBasketWithSignificanOtherPassanger(BasketsResponse basket, String customerId, String channel, Profile passanger) throws EasyjetCompromisedException {
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasketWithSignificantOther(basket, passanger), channel, basket
                .getBasket()
                .getCode());

        getCustomer(customerId, channel);

        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basket.getBasket());

        return new CommitBookingRequest(HybrisHeaders.getValid(channel).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID).build(), aBooking(basket, paymentMethod));
    }

    private CommitBookingRequest createCorporateBookingWithDealRequestForPublicChannel(String criteria, String channel) throws Throwable {
        dealsInfo = dealsInfoHelper.findAValidDeals();
        return new CommitBookingRequest(HybrisHeaders.getValidWithDealInfo(channel, dealsInfo.getSystemName(), dealsInfo.getOfficeId(), dealsInfo.getCorporateId()).build(), aBookingPayloadWithBasketContent(criteria, channel));
    }

    private CommitBookingRequest createNewBookingRequestForPublicChannel(String criteria) throws Throwable {
        commitBooking = aBookingPayloadWithBasketContent(criteria, testData.getChannel());
        return new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), commitBooking);
    }

    private CommitBookingRequest createDuplicateBookingRequestForPublicChannel(CommitBookingRequestBody bookingPayload, String channel) throws Throwable { //NOSONAR
        bookingPayload.setOverrideWarning(false);
        return new CommitBookingRequest(HybrisHeaders.getValid(channel).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID)
                .build(), bookingPayload);
    }

    private CommitBookingRequestBody aBookingPayloadWithBasketContent(String criteria, String channel) throws Throwable {
        basketContent = buildBasketContent(criteria, channel);
        List<PaymentMethod> paymentsList = new ArrayList<>();
        paymentsList.add(PaymentMethodFactory.generateDebitCardPaymentMethod(basketsResponse.getBasket()));

        commitBooking = CommitBookingFactory.aBooking(basketsResponse.getBasket(), paymentsList, true);
        commitBooking.setBasketContent(basketContent);
        commitBooking.setBasketCode(null);

        if ("corporate".equalsIgnoreCase(criteria)) {
            commitBooking.setBookingType(BUSINESS);
            commitBooking.setBookingReason(BUSINESS);
        }
        if ("price change".equalsIgnoreCase(criteria)) {
            commitBooking.getPaymentMethods().get(0).setPaymentAmount(basketContent.getTotalAmountWithDebitCard());
        }
        clearBasket(basketsResponse.getBasket().getCode());
        return commitBooking;
    }

    private BasketContent buildBasketContent(String criteria, String channel) throws Throwable {
        customerHelper.createRandomCustomer(channel);

        switch (criteria) {
            case "multiple flights":
                customerHelper.createRandomCustomer(channel);
                basketsResponse = getBasketWithMultipleFlights(channel);
                basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
                break;
            case "existing customer":
                basketsResponse = getBasketWithExistingCustomerContext(channel);
                basketContent = BasketContentFactory.getBasketContentForExistingCustomer(basketsResponse.getBasket(), customerProfileService.getResponse().getCustomer().getBasicProfile());
                break;
            case "non existing customer":
                customerHelper.createRandomCustomer(channel);
                basketsResponse = getBasketWithOutCustomerAssociation(channel);
                basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
                break;
            case "corporate":
                customerHelper.createRandomCustomer(channel);
                basketsResponse = getBasketWithDealApplied(channel, dealsInfo);
                basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
                break;
            case "price change":
                customerHelper.createRandomCustomer(channel);
                basketsResponse = getBasket(channel);
                testData.setBasketId(basketsResponse.getBasket().getCode());
                basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
                updatePrices(basketContent);
                break;
            case "price change with multiple flights":
                customerHelper.createRandomCustomer(channel);
                basketsResponse = getBasketWithMultipleFlights(channel);
                testData.setBasketId(basketsResponse.getBasket().getCode());
                basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
                updatePrices(basketContent);
                break;
            case "hold bag":
                customerHelper.createRandomCustomer(channel);
                basketsResponse = getBasketWithHoldItems(CommonConstants.ONE_ADULT, 1);
                testData.setBasketId(basketsResponse.getBasket().getCode());
                basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
                break;
            case "multiple passengers":
                basketsResponse = createABasketWithPassengerMix(CommonConstants.ONE_ADULT, testData.getChannel());
                basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
                break;
            case "Passenger with AddtionalFare":
                basketsResponse = createABasketWithPassengerMix(testData.getPassengerMix(), testData.getChannel());
                manageAdditionalFareToPassengerInBasketHelper.additionalFareToPassengerInBasketHelperMultiplePassengers();
                basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
                break;
            default:
                customerHelper.createRandomCustomer(channel);
                basketsResponse = getBasket(channel);
                basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
                break;
        }
        return basketContent;
    }

    private BasketsResponse getBasketWithExistingCustomerContext(String channel) throws Throwable {

        basketHelper.myBasketContainsAFlightWithPassengerMix("2 Adult", channel, STANDARD, false);
        associateCustomerProfile(channel);
        //make call to update travellers
        basketsResponse = basketHelper.getBasketService().getResponse();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), channel, basketsResponse
                .getBasket()
                .getCode());
        basketHelper.getBasket(basketsResponse.getBasket().getCode(), channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        return basketsResponse;
    }

    private BasketsResponse getBasketWithOutCustomerAssociation(String channel) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(CommonConstants.ONE_ADULT, channel, STANDARD, false);
        //make call to update travellers
        basketsResponse = basketHelper.getBasketService().getResponse();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), channel, basketsResponse.getBasket().getCode());
        basketHelper.getBasket(basketsResponse.getBasket().getCode(), channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        return basketsResponse;
    }

    public void createDuplicateBooking(String channel) throws Throwable {

        commitBookingResponse(channel);
        basketHelper.addFlightToMyBasket(addFlight, channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        passengers = travellerHelper.getPassengersUsedInBookingRequest(basketsResponse);
        basketHelper.updatePassengersForChannel(passengers, channel, basketHelper.getBasketService()
                .getResponse()
                .getBasket()
                .getCode());
        basketHelper.getBasket(basketsResponse.getBasket().getCode(), channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        commitBookingResponse(channel, customer, false);
    }

    private void commitBookingResponse(String channel) throws Throwable {

        commitBookingResponse(channel, true);
    }

    private void commitBookingResponse(String channel, boolean overrideWarning) throws Throwable {
        customerHelper.createRandomCustomer(channel);
        customer = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation();
        commitBookingResponse(channel, customer, overrideWarning);
    }

    private void commitBookingResponse(String channel, RegisterCustomerResponse.OperationConfirmation customer, boolean overrideWarning) throws Throwable {
        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basketsResponse.getBasket());
        associateCustomerProfile(channel, customer);
        commitBookingService = serviceFactory.commitBooking(new CommitBookingRequest(HybrisHeaders.getValid(channel).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID)
                .build(), CommitBookingFactory.aBooking(basketsResponse.getBasket(), Arrays.asList(paymentMethod), overrideWarning)));
        commitBookingService.invoke();
    }

    public BasketsResponse getBasket(String channel) throws Throwable {
        //find flights, add to basket and update traveller details
        basketHelper.myBasketContainsAFlightWithPassengerMix(CommonConstants.ONE_ADULT, channel, STANDARD, false);
        basketsResponse = basketHelper.getBasketService().getResponse();
        associateCustomerProfileWithShippingAddress(channel);
        //make call to update travellers
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), channel, basketsResponse.getBasket().getCode());
        basketHelper.getBasket(basketsResponse.getBasket().getCode(), channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        return basketsResponse;
    }

    public BasketsResponse createABasketWithPassengerMix(String passengerMix, String channel) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix);

        basketsResponse = basketHelper.getBasketService().getResponse();

        basketHelper.updatePassengersForChannel(
                travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse),
                channel,
                basketsResponse.getBasket().getCode()
        );

        basketHelper.getBasket(basketsResponse.getBasket().getCode(), channel);

        basketsResponse = basketHelper.getBasketService().getResponse();

        return basketsResponse;
    }

    public BasketsResponse getBasketWithSeats(String channel) throws Throwable {
        //find flights, add to basket and update traveller details
        flightHelper.setSectors();
        basketHelper.myBasketContainsAFlightWithPassengerMix(CommonConstants.ONE_ADULT, channel, STANDARD, false);

        basketsResponse = basketHelper.getBasketService().getResponse();
        //make call to update travellers
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), channel, basketsResponse.getBasket().getCode());
        testData.setSeatProductInBasket(PurchasedSeatHelper.SEATPRODUCTS.STANDARD);
        purchasedSeatHelper.addPurchasedSeatToBasketForEachPassengerAndFlight(PurchasedSeatHelper.SEATPRODUCTS.STANDARD, false);
        basketHelper.getBasket(basketsResponse.getBasket().getCode(), testData.getChannel());
        basketsResponse = basketHelper.getBasketService().getResponse();
        return basketsResponse;
    }

    private void associateCustomerProfile(String channel) throws Throwable {
        customerHelper.createRandomCustomer(channel);
        String customerId = testData.getData(CUSTOMER_ID);
        associateCustomerProfile(channel, customerId);
    }

    private void associateCustomerProfileWithShippingAddress(String channel) throws Throwable {
        customerHelper.createRandomCustomer(channel);
        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(PROFILE).build();
        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValid(channel).build(), profilePathParams));
        customerProfileService.invoke();
    }

    private void associateCustomerProfile(String channel, RegisterCustomerResponse.OperationConfirmation customer) throws Throwable { //NOSONAR
        String customerId = customer.getCustomerId();
        getCustomer(customerId, channel);
    }

    public CustomerProfileService associateCustomerProfile(String channel, String customerID) throws Throwable { //NOSONAR
        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(customerID).path(PROFILE).build();
        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValidWithToken(channel, testData.getAccessToken()).build(), profilePathParams));
        customerProfileService.invoke();
        testData.setData(GET_CUSTOMER_PROFILE_SERVICE, customerProfileService);
        return customerProfileService;
    }

    public GetBookingService getAllWithBookingReference() {
        testData.setData(BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
        BookingPathParams params = BookingPathParams.builder().bookingReference(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).path(GETBOOKING).build();
        getBookingService = serviceFactory.getBookings(new GetBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params));
        getBookingService.invoke();
        testData.setBookingResponse(getBookingService.getResponse());
        return getBookingService;
    }

    private BasketsResponse getBasketWithMultipleFlights(String channel) throws Throwable {
        //find flights, add to basketsResponse and update traveller details
        basketHelper.addNumberOfFlightsToBasket(2, channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), channel, basketsResponse.getBasket().getCode());
        basketHelper.getBasket(basketsResponse.getBasket().getCode(), channel);
        associateCustomerProfile(channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        return basketsResponse;
    }

    private BasketsResponse getBasketWithDealApplied(String channel, DealModel dealModel) throws Throwable {
        //find flights, add to basketsResponse and update traveller details
        basketHelper.myBasketContainsAFlightWithPassengerMixWithDeal(CommonConstants.ONE_ADULT, channel, dealModel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        //make call to update travellers
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), channel, basketsResponse.getBasket().getCode());
        basketHelper.getBasket(basketsResponse.getBasket().getCode(), channel);
        basketsResponse = basketHelper.getBasketService().getResponse();
        return basketsResponse;
    }

    public void clearBasket(String code) throws Throwable { //NOSONAR
        basketHelper.emptyBasket(code, CommonConstants.PUBLIC_API_B2B_CHANNEL);
    }

    public void getValidBasketContent(String fareType, String journey) throws Throwable {
        basket = getBasket(journey, fareType, null);
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
        basketContent = BasketContentFactory.getBasketContent(basket);
        clearBasket(basketsResponse.getBasket().getCode());
    }

    public void getUpdatedBasketContent(String fareType, String journey, String criteria) throws Throwable {
        basket = getBasket(journey, fareType, null);
        basketContent = BasketContentFactory.getBasketConetentWithUpdatedPrice(basketHelper.getBasketService().getResponse().getBasket(), criteria);
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
        clearBasket(basketsResponse.getBasket().getCode());
    }

    public void getUpdatedBasketContent(BasketsResponse response, String criteria) throws Throwable {
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(response), testData.getChannel(), response.getBasket().getCode());
        basketHelper.getBasket(response.getBasket().getCode(), testData.getChannel());
        testData.setData(GET_FLIGHT_SERVICE, null);
        BasketsResponse updatedResponse = basketHelper.getBasketService().getResponse();
        basketContent = BasketContentFactory.getBasketConetentWithUpdatedPrice(updatedResponse.getBasket(), criteria);
        clearBasket(updatedResponse.getBasket().getCode());
    }

    public Basket getBasket(String journeyType, String faretype, String bookingtype) throws Throwable {
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        basketHelper.createBasketWithPassengerMix(flightsService, journeyType, faretype, bookingtype, testData.getChannel(), false);
        basketsResponse = basketHelper.getBasketService().getResponse();
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse, true), testData.getChannel(), basketsResponse.getBasket().getCode());

        basketHelper.getBasket(basketsResponse.getBasket().getCode(), testData.getChannel(), true);
        testData.setData(GET_FLIGHT_SERVICE, null);
        return basketHelper.getBasketService().getResponse().getBasket();
    }

    public void getBasketContentWithInvalidParam(String param) throws Throwable {
        basketsResponse = getBasket(testData.getChannel());
        basketContent = BasketContentFactory.getBasketContentWithInvalidOrMissingParameter(basketsResponse.getBasket(), param);
        testData.setBasketContent(basketContent);
    }

    /**
     * updatePrices, it will update the basket prices for 8419 ticket
     *
     * @param basketContent
     * @throws Exception
     */
    private void updatePrices(BasketContent basketContent) throws Exception { //NOSONAR

        List<Flight> flights = basketContent.getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream()).collect(Collectors.toList());
        flights.forEach(flight -> flight.getPassengers().forEach(
                passenger -> passenger.getFareProduct().getPricing().setBasePrice(passenger.getFareProduct().getPricing().getBasePrice() + 10)
        ));
    }

    public CommitBookingRequest holdItemPriceChangeCommitBookingRequestForPublicChannel(String passengerMix, int quantity) throws Throwable {
        commitBooking = getValidCommitBookingBodyWithHoldBag(passengerMix, quantity, true);
        return new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), commitBooking);
    }

    public void createNewBookingForPublicChannelWithProducts() throws Throwable {
        createNewBookingForPublicChannelWithProducts(1, 1, 1, true);
    }

    public CommitBookingRequest createNewBookingForPublicChannelWithProducts(int holdBags, int excessWeights, int sportEquipments, boolean seats) throws Throwable {
        customerHelper.createRandomCustomer(testData.getChannel());
        basketsResponse = getBasketWithHoldItems(holdBags, excessWeights, sportEquipments, seats);
        testData.setBasketId(basketsResponse.getBasket().getCode());
        basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
        basketContent = setQuantity();
        commitBooking = CommitBookingRequestBody.builder()
                .bookingType(basketsResponse.getBasket().getBasketType())
                .bookingReason(basketsResponse.getBasket().getBookingReason())
                .basketContent(basketContent)
                .paymentMethods(Arrays.asList(PaymentMethodFactory.generateDebitCardPaymentMethod(basketsResponse.getBasket())))
                .overrideWarning(true)
                .build();
        clearBasket(basketsResponse.getBasket().getCode());
        return new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), commitBooking);
    }

    private BasketContent setQuantity() {
        basketContent.getOutbounds().forEach(
                outbound -> outbound.getFlights().forEach(
                        flight -> flight.getPassengers().forEach(
                                passenger -> passenger.getHoldItems().forEach(
                                        holdItem -> {
                                            if (holdItem.getCode().contains("Snowboard")) {
                                                holdItem.setQuantity(testData.getSportEquipCount());
                                            } else if (holdItem.getCode().contains("bag")) {
                                                holdItem.setQuantity(testData.getHoldBagCount());
                                            }
                                        }
                                )
                        )
                )
        );

        return basketContent;
    }

    private BasketsResponse getBasketWithHoldItems(String pax, int quantity) throws Throwable {
        //find flights, add to basket and update traveller details
        basketHelper.myBasketContainsAFlightWithPassengerMix(pax, testData.getChannel(), "Standard", false);
        basketsResponse = basketHelper.getBasketService().getResponse();
        associateCustomerProfileWithShippingAddress(testData.getChannel());
        //Add hold items
        basketHelper.addHoldAndExcessWeightBagsForAllPassengers(quantity, 0);
        //make call to update travellers
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), testData.getChannel(), basketsResponse.getBasket().getCode());
        basketHelper.getBasket(basketsResponse.getBasket().getCode(), testData.getChannel(), true);
        basketsResponse = basketHelper.getBasketService().getResponse();
        return basketsResponse;
    }

    private BasketsResponse getBasketWithHoldItems(int holdBags, int excessWeights, int sportEquips, boolean seats) throws Throwable {
        //find flights, add to basket and update traveller details
        basketHelper.myBasketContainsAFlightWithPassengerMix(testData.getPassengerMix(), testData.getChannel(), "Standard", false);
        basketsResponse = basketHelper.getBasketService().getResponse();
        associateCustomerProfileWithShippingAddress(testData.getChannel());
        //Add hold items
        basketHelper.addHoldAndExcessWeightBagsForAllPassengers(holdBags, excessWeights);
        //Add sport equipment items
        basketHelper.addSportEquipmentForAllPassengers(sportEquips);
        //Add purchased seat product
        if (seats) {
            purchasedSeatHelper.addPurchasedSeatToBasket(PurchasedSeatHelper.SEATPRODUCTS.STANDARD);
        }
        //make call to update travellers
        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse), testData.getChannel(), basketsResponse.getBasket().getCode());

        basketHelper.getBasket(basketsResponse.getBasket().getCode(), testData.getChannel(), true);
        basketsResponse = basketHelper.getBasketService().getResponse();
        return basketsResponse;
    }

    private BasketContent getValidBasketContentWithHoldBag(String pax, int quantity) throws Throwable {
        customerHelper.createRandomCustomer(testData.getChannel());
        basketsResponse = getBasketWithHoldItems(pax, quantity);
        testData.setBasketId(basketsResponse.getBasket().getCode());
        basketContent = BasketContentFactory.getBasketContent(basketsResponse.getBasket());
        return basketContent;
    }

    private CommitBookingRequestBody getValidCommitBookingBodyWithHoldBag(String pax, int quantity, boolean changePrice) throws Throwable {
        basketContent = getValidBasketContentWithHoldBag(pax, quantity);
        setQuantityForAllHoldItems(quantity, changePrice);
        commitBooking = CommitBookingRequestBody.builder()
                .bookingType(basketsResponse.getBasket().getBasketType())
                .bookingReason(basketsResponse.getBasket().getBookingReason())
                .basketContent(basketContent)
                .paymentMethods(Arrays.asList(PaymentMethodFactory.generateDebitCardPaymentMethod(basketsResponse.getBasket())))
                .build();

        clearBasket(basketsResponse.getBasket().getCode());
        return commitBooking;
    }

    private void setQuantityForAllHoldItems(int quantity, boolean changePrice) {
        if (quantity > 0) {
            for (UniquePassenger uniquePassenger : basketContent.getUniquePassengerList()) {
                if (!"infant".equalsIgnoreCase(uniquePassenger.getPassengerDetails().getPassengerType())) {
                    basketContent.getOutbounds().forEach(
                            outbound ->
                                    outbound.getFlights().forEach(
                                            flight ->
                                                    flight.getPassengers().forEach(
                                                            passenger ->
                                                                    passenger.getHoldItems().forEach(
                                                                            holdItem -> {
                                                                                holdItem.setQuantity(quantity);
                                                                                if (changePrice) {
                                                                                    holdItem.getPricing().setBasePrice(holdItem.getPricing().getBasePrice() + 16);
                                                                                }
                                                                            }
                                                                    )
                                                    )
                                    )
                    );
                }
            }
        }
    }

    /**
     * The method commit a booking where you can specify if during the update passenger information, you want to update the EJPlus details
     *
     * @param withEjPlus boolean value if you want to add EJPlus details
     * @throws Throwable NB: the method is specific to add EJPlus details just on the first passenger of the first outbound flight, please generalize this method if you want to add on more than one passeger
     */
    public BookingConfirmationResponse createBasicBookingRequestWithEJPlusDetailsAndCommitIt(boolean withEjPlus, boolean validation) throws Throwable {

        Passengers updatePassengers = travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse());
        MemberShipModel membershipDetail = getEJPlusDetails();
        updatePassengers.getPassengers().get(0).getPassengerDetails().getName().setLastName(membershipDetail.getLastname());
        if (withEjPlus) {
            updatePassengers.getPassengers().get(0).getPassengerDetails().setEjPlusCardNumber(membershipDetail.getEjMemberShipNumber());
            updatePassengers.getPassengers().get(0).setPassengerAPIS(null);
        }
        basketHelper.updatePassengersForChannel(updatePassengers, testData.getChannel(), basketHelper.getBasketService().getResponse()
                .getBasket()
                .getCode());

        // if the channel is AD, the customer will be already in session as agent
        if(!testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CHANNEL) && !testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CUSTOMER_SERVICE)) {
            //get a customer profile id
            customerHelper.createRandomCustomer(testData.getChannel());
            getCustomerProfile(testData.getData(CUSTOMER_ID));
        }

        Basket updatedBasket = getBasketAfterUpdatePassengers(basketHelper.getBasketService().getResponse().getBasket().getCode(), updatePassengers);
        PaymentMethod paymentMethods = PaymentMethodFactory.generateDebitCardPaymentMethod(updatedBasket);
        CommitBookingRequestBody commitBookingRequestBody = CommitBookingFactory.aBooking(updatedBasket, Arrays.asList(paymentMethods), true);
        CommitBookingRequest commitBookingRequest;
        if (CommonConstants.PUBLIC_API_B2B_CHANNEL.equalsIgnoreCase(testData.getChannel())) {
            commitBookingRequestBody.setBasketContent(BasketContentFactory.getBasketContentForExistingCustomer(updatedBasket, customerProfileService.getResponse().getCustomer().getBasicProfile()));
        }
        if(testData.keyExist(TRANSACTION_ID)) {
            commitBookingRequest = new CommitBookingRequest(HybrisHeaders.getValidXClientTransactionId(testData.getChannel(), testData.getData(TRANSACTION_ID)).build(), commitBookingRequestBody);
        } else {
            commitBookingRequest = new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), commitBookingRequestBody);
        }

        if (!validation) {
            return sendSuccessCommitBookingRequest(commitBookingRequest);
        } else {
            commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
            commitBookingService.invoke();
            testData.setData(SERVICE, commitBookingService);
            return null;
        }
    }

    public BookingConfirmationResponse basicBookingwithCardDetailsAndCommitIt(String paymentType) throws Throwable {
        Passengers updatePassengers = travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse());

        basketHelper.updatePassengersForChannel(updatePassengers, testData.getChannel(), basketHelper.getBasketService().getResponse()
                .getBasket()
                .getCode());

        // if the channel is AD, the customer will be already in session as agent
        if(!testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CHANNEL) && !testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CUSTOMER_SERVICE)) {
            //get a customer profile id
            customerHelper.createRandomCustomer(testData.getChannel());
            getCustomerProfile(testData.getData(CUSTOMER_ID));
        }

        Basket updatedBasket = getBasketAfterUpdatePassengers(basketHelper.getBasketService().getResponse().getBasket().getCode(), updatePassengers);

        PaymentMethod paymentMethods;
        if (paymentType.equals("debitCard")) {

            paymentMethods = PaymentMethodFactory.generateDebitCardPaymentMethod(updatedBasket);
        } else {
            paymentMethods = PaymentMethodFactory.generateCreditCardPaymentMethod(updatedBasket);
        }

        CommitBookingRequestBody commitBookingRequestBody = CommitBookingFactory.aBooking(updatedBasket, Arrays.asList(paymentMethods), true);
        CommitBookingRequest commitBookingRequest;

        if(testData.keyExist(TRANSACTION_ID)) {
            commitBookingRequest = new CommitBookingRequest(HybrisHeaders.getValidXClientTransactionId(testData.getChannel(), testData.getData(TRANSACTION_ID)).build(), commitBookingRequestBody);
        } else {
            commitBookingRequest = new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), commitBookingRequestBody);
        }
        return sendSuccessCommitBookingRequest(commitBookingRequest);
    }

    public void getCustomerProfile(String customerID) {
        CustomerPathParams profilePathParams = CustomerPathParams.builder()
                .customerId(customerID)
                .path(PROFILE)
                .build();
        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(testData.keyExist(CUSTOMER_ACCESS_TOKEN) ? HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getData(CUSTOMER_ACCESS_TOKEN)).build() : HybrisHeaders.getValidWithToken(testData.getChannel(),testData.getData(CUSTOMER_ACCESS_TOKEN)).build(), profilePathParams));
        customerProfileService.invoke();
    }

    private BookingConfirmationResponse sendSuccessCommitBookingRequest(CommitBookingRequest commitBookingRequest) throws TimeoutException, EasyjetCompromisedException {
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        final int[] attempts = {3};
        pollingLoopForSearchBooking().until(() -> {
            commitBookingService.invoke();
            attempts[0]--;
            testData.setData(SERVICE, commitBookingService);
            verifyRestrictedRule();
            return commitBookingService.getStatusCode() == 200 || attempts[0] == 0;
        });
        testData.setData(BOOKING_ID, commitBookingService.getResponse().getOperationConfirmation().getBookingReference());
        return commitBookingService.getResponse();
    }

    private void verifyRestrictedRule() throws EasyjetCompromisedException {
        if (Objects.nonNull(commitBookingService.getErrors())) {
            List<String> errorCodeDynamicRule = commitBookingService.getErrors().getErrors().stream().map(e -> e.getCode()).collect(Collectors.toList());
            if (errorCodeDynamicRule.contains("SVC_100022_3012")) {
                throw new EasyjetCompromisedException("(type: SVC_100022_3012, message: Unable to commit booking because price has changed for one or more of the requested flights)");
            } else if (errorCodeDynamicRule.contains("SVC_100022_3012")) {
                throw new EasyjetCompromisedException("(type: SVC_100022_3012, message: Seat not available (restricted by dynamic rule))");
            } else if (errorCodeDynamicRule.contains("SVC_100500_5041")) {
                throw new EasyjetCompromisedException("(type: SVC_100500_5041, message: Seat not available (restricted by dynamic rule))");
            }
        }
    }

    private CancelBookingRefundRequestBody.RefundOrFee cancelBookingRefundResponse() {
        return CancelBookingRefundRequestBody.RefundOrFee.builder()
                .type(CommonConstants.REFUND)
                .amount(150.56)
                .currency("GBP")
                .primaryReasonCode("24_HOUR_CANCELLATION")
                .primaryReasonName("24 Hour Cancellation")
                .originalPaymentMethod("CARD")
                .originalPaymentMethodContext("1234")
                .build();
    }


    private void cancelBookingWithInvalidParameters(String bookRef, double amount, String paymentContext, String reasonCode, String currency, String paymentMethod) {
        cancelBookingRefundRequestBody = CancelBookingRefundRequestBody.builder()
                .refundsAndFees(new ArrayList<CancelBookingRefundRequestBody.RefundOrFee>() {{
                    add(CancelBookingRefundRequestBody.RefundOrFee.builder().amount(amount)
                            .type(CommonConstants.REFUND)
                            .currency(currency)
                            .primaryReasonCode(reasonCode)
                            .primaryReasonName("Customer Cancellation 24 hours")
                            .originalPaymentMethodContext(paymentContext)
                            .originalPaymentMethod(paymentMethod)
                            .build()
                    );
                }}).build();

        bookingPathParams = BookingPathParams.builder()
                .bookingId(bookRef).path(CANCEL_BOOKING).build();

        cancelBookingRefundService = serviceFactory.cancelBookingRefund(new CancelBookingRefundRequest
                (HybrisHeaders.getValid(testData.getChannel()).build(),
                        bookingPathParams, cancelBookingRefundRequestBody));
        cancelBookingRefundService.invoke();

    }

    private void cancelTravelBooking(String entry, String bookRef, double amount, String paymentContext, String reasonCode, String currency) {

        switch (entry) {
            case CommonConstants.INCORRECT:
                cancelBookingRefundRequestBody = CancelBookingRefundRequestBody.builder()
                        .refundsAndFees(
                                new ArrayList<CancelBookingRefundRequestBody.RefundOrFee>() {{
                                    add(cancelBookingRefundResponse());
                                }}
                        ).build();
                bookingPathParams = BookingPathParams.builder().bookingId("1111")
                        .bookingReference(bookRef).path(CANCEL_BOOKING)
                        .build();
                cancelBookingRefundService = serviceFactory.cancelBookingRefund(new CancelBookingRefundRequest
                        (HybrisHeaders.getValid(testData.getChannel()).build(),
                                bookingPathParams, cancelBookingRefundRequestBody));
                break;
            case "invalid":
                cancelBookingRefundRequestBody = CancelBookingRefundRequestBody.builder()
                        .refundsAndFees(
                                new ArrayList<CancelBookingRefundRequestBody.RefundOrFee>() {{
                                    add(cancelBookingRefundResponse());
                                }}
                        ).build();

                bookingPathParams = BookingPathParams.builder().bookingId("11122")
                        .bookingReference("EZJ1500565059756").path(CANCEL_BOOKING).build();

                cancelBookingRefundService = serviceFactory.cancelBookingRefund(new CancelBookingRefundRequest
                        (HybrisHeaders.getValid(testData.getChannel()).build(),
                                bookingPathParams, cancelBookingRefundRequestBody));
                break;
            case CommonConstants.REFUND:
                sendCancelBookingRefundRequest(bookRef, amount, paymentContext, reasonCode, CommonConstants.CARDREFUND);
                break;
            case CommonConstants.CREDITFILEFUND:
                sendCancelBookingRefundRequest(bookRef, amount, paymentContext, reasonCode, CommonConstants.CREDITFILEFUNDREFUND);
                break;
            default:
                cancelBookingRefundService = serviceFactory.cancelBookingRefund(new CancelBookingRefundRequest
                        (HybrisHeaders.getValid(testData.getChannel()).build(),
                                bookingPathParams, cancelBookingRefundRequestBody));

        }

        cancelBookingRefundService.invoke();
    }

    private void sendCancelBookingRefundRequest(String bookRef, double amount, String paymentContext, String reasonCode, String refundPaymentMethod) {
        cancelBookingRefundRequestBody = CancelBookingRefundRequestBody.builder()
                .refundsAndFees(new ArrayList<CancelBookingRefundRequestBody.RefundOrFee>() {{
                    add(CancelBookingRefundRequestBody.RefundOrFee.builder()
                            .amount(amount)
                            .type(CommonConstants.REFUND)
                            .currency("GBP")
                            .primaryReasonCode(reasonCode)
                            .primaryReasonName("Customer Cancellation 24 hours")
                            .originalPaymentMethodContext(paymentContext)
                            .originalPaymentMethod(refundPaymentMethod)
                            .build()
                    );
                }}).build();

        bookingPathParams = BookingPathParams.builder()
                .bookingId(bookRef).path(CANCEL_BOOKING).build();

        cancelBookingRefundService = serviceFactory.cancelBookingRefund(new CancelBookingRefundRequest
                (HybrisHeaders.getValid(testData.getChannel()).build(),
                        bookingPathParams, cancelBookingRefundRequestBody));
    }

    public void cancelBookingwithInvalidRef(String entry) {
        cancelTravelBooking(entry, null, 0.00, null, null, null);
    }

    public void cancelBooking(String bookRef, String entry, String originalPaymentMethodContext) {
        cancelTravelBooking(entry, bookRef, 0.00, originalPaymentMethodContext, null, null);
    }

    public void cancelBooking(String bookRef, String entry, double amount, String paymentContext, String reasonCode) {
        cancelTravelBooking(entry, bookRef, amount, paymentContext, reasonCode, null);
    }

    public void cancelBookingInvalidCurrency(String bookRef, double amount, String paymentContext, String reasonCode, String currency, String paymentMethod) {
        cancelBookingWithInvalidParameters(bookRef, amount, paymentContext, reasonCode, currency, paymentMethod);
    }

    /**
     * The method invoke the get basket after updated the passenger information in a normal flow for committed a booking.
     * The check considering a passenger mix in the basket of only one passenger. If you need to use more generic you need
     * to refactor this method in order to consider also di other passenger.
     *
     * @param basketCode basket code
     * @param passengers update passengers
     * @return updated basket
     * @throws TimeoutException
     */
    private Basket getBasketAfterUpdatePassengers(String basketCode, Passengers passengers) throws TimeoutException {
        final Basket[] tmpBasket = {new Basket()};
        final int[] attempts = {3};
        try {
            pollingLoop().until(() -> {
                tmpBasket[0] = basketHelper.getBasket(basketCode, testData.getChannel());
                attempts[0]--;
                Basket.Passenger basketPassenger = tmpBasket[0].getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).findFirst().orElse(null);
                com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger expectedPassenger = passengers.getPassengers().stream().findFirst().orElse(null);
                if (Objects.isNull(basketPassenger) || Objects.isNull(expectedPassenger)) {
                    return false;
                } else if (Objects.isNull(basketPassenger.getPassengerDetails().getName().getLastName()) || Objects.isNull(expectedPassenger.getPassengerDetails().getName().getLastName())) {
                    return false;
                } else {
                    return basketPassenger.getPassengerDetails().getName().getLastName().equalsIgnoreCase(expectedPassenger.getPassengerDetails().getName().getLastName())
                            || attempts[0] == 0;
                }
            });
        } catch (Exception e) {
            throw new TimeoutException("The basket has not been updated properly after updating passenger information");
        }
        basketHelper.getBasketService().getResponse();
        return tmpBasket[0];
    }

    public String createBookingWithMultipleFlightAndGetAmendable(String passengerMix, String fare, int numberOfFlights) throws Throwable {
        String bookingRef = commitBooking(passengerMix, fare, false, numberOfFlights, false, null, false);
        return getAmendableBasket(bookingRef);
    }

    public String createBookingWithPurchasedSeatAndGetAmendable(String passengerMix, String fare, boolean withPurchaseSeat, boolean isEmergencySeatExit, PurchasedSeatHelper.SEATPRODUCTS seatproducts, boolean continuous) throws Throwable {
        String bookingRef = commitBooking(passengerMix, fare, isEmergencySeatExit, 1, withPurchaseSeat, seatproducts, continuous);
        return getAmendableBasket(bookingRef);
    }

    /**
     * This method commits the Booking
     * @param passengerMix - passengers
     * @param fare - faretype to be used
     * @param isEmergencySeatExit - true|false - need Emergency Exit seat or not
     * @param numberOfFlights - number of flights required
     * @param withPurchaseSeat - true|false - purchased seat required or not
     * @param seatproducts - specific seat product to be added - EXTRA_LEGROOM | UPFRONT | STANDARD
     * @param continuous - true|false - continuous or consecutive or adjacent seat required or not
     */
    public String commitBooking(String passengerMix, String fare, boolean isEmergencySeatExit, int numberOfFlights, boolean withPurchaseSeat, PurchasedSeatHelper.SEATPRODUCTS seatproducts, boolean continuous) throws Throwable {
        customerHelper.createCustomerAndLoginIt();
        if (numberOfFlights > 1) {
            getBasketHelper().myBasketContainsManyFlightWithPassengerMix(numberOfFlights, passengerMix, testData.getChannel(), fare, OUTBOUND);
        } else {
            getBasketHelper().myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fare, false);
        }

        if(withPurchaseSeat){
            List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.Seat> availableSeats = purchasedSeatHelper.getAvailableSeats(seatproducts);
            if (isEmergencySeatExit) {
                availableSeats.removeIf(s -> {
                    return !purchasedSeatHelper.isEmergencyExit(s.getSeatNumber());
                });
            } else {
                availableSeats.removeIf(s -> {
                    return purchasedSeatHelper.isEmergencyExit(s.getSeatNumber());
                });
            }
            if (continuous) {
                purchasedSeatHelper.addContinuousPurchasedSeatForEachPassengerAndFlight(availableSeats);
            } else {
                purchasedSeatHelper.addPurchasedSeatForEachPassengerAndFlight(availableSeats);
            }
            purchasedSeatHelper.getPurchasedSeatService().getResponse();
        }
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        createNewBooking(createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse(), testData.getData(CUSTOMER_ID), testData.getChannel(), true));
        commitBookingService.getResponse();
        String bookingRef = getCommitBookingService().getResponse().getBookingConfirmation().getBookingReference();
        testData.setData(BOOKING_ID, bookingRef);
        return bookingRef;
    }

    public String createBookingAndGetAmendable(String passengerMix, String fare, boolean Apis) throws Throwable {
        customerHelper.createCustomerAndLoginIt();
        getBasketHelper().myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fare, Apis);
        testData.setBasketId(basketHelper.getBasketService().getResponse().getBasket().getCode());
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        testData.setData("PassengersOnFlight", basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).collect(Collectors.toList()));
        testData.setPassengerId(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());

        if (Apis) {
            createNewBooking(createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse(), testData.getData(CUSTOMER_ID), testData.getChannel(), false));
        } else {
            createNewBooking(createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse(), testData.getData(CUSTOMER_ID), testData.getChannel(), true));
        }
        commitBookingService.getResponse();
        String bookingRef = getCommitBookingService().getResponse().getBookingConfirmation().getBookingReference();
        testData.setData(BOOKING_ID, bookingRef);
        return getAmendableBasket(bookingRef);
    }

    public void addValuesInToMap(String[] paymentdetails, String paymentType) {
        if (paymentdetails.length == 6) {
            if ("card".equals(paymentType)) {
                paymentInfo.put("cardType", paymentdetails[0].trim());
                paymentInfo.put("cardNumber", paymentdetails[1].trim());
                paymentInfo.put("cardSecNumber", paymentdetails[2].trim());
                paymentInfo.put("cardExpiryMonth", paymentdetails[3].trim());
                paymentInfo.put("cardExpiryYear", paymentdetails[4].trim());
                paymentInfo.put("cardHolderName", paymentdetails[5].trim());
            } else if ("elv".equals(paymentType)) {
                paymentInfo.put("accountHolderName", paymentdetails[0].trim());
                paymentInfo.put("accountNumber", paymentdetails[1].trim());
                paymentInfo.put("bankCity", paymentdetails[2].trim());
                paymentInfo.put("bankCode", paymentdetails[3].trim());
                paymentInfo.put("bankCountryCode", paymentdetails[4].trim());
                paymentInfo.put("bankName", paymentdetails[5].trim());
            }
        }
    }

    /**
     * cancelBooking, it cancels booking
     * @param bookRef
     * @param amount
     * @param paymentContext
     * @param reasonCode
     * @param paymentMethod
     * @param currency
     */
    public void cancelBooking(String bookRef, double amount, String paymentContext, String reasonCode, String paymentMethod, String currency) {
        cancelBookingRefundRequestBody = CancelBookingRefundRequestBody.builder()
                .refundsAndFees(new ArrayList<CancelBookingRefundRequestBody.RefundOrFee>() {{
                    add(CancelBookingRefundRequestBody.RefundOrFee.builder().amount(amount)
                            .type(CommonConstants.REFUND)
                            .currency(currency)
                            .primaryReasonCode(reasonCode)
                            .primaryReasonName("Customer Cancellation 24 hours")
                            .originalPaymentMethodContext(paymentContext)
                            .originalPaymentMethod(paymentMethod)
                            .build()
                    );
                }}).build();

        bookingPathParams = BookingPathParams.builder()
                .bookingId(bookRef).path(CANCEL_BOOKING).build();

        cancelBookingRefundService = serviceFactory.cancelBookingRefund(new CancelBookingRefundRequest
                (HybrisHeaders.getValid(testData.getChannel()).build(),
                        bookingPathParams, cancelBookingRefundRequestBody));
        cancelBookingRefundService.invoke();
    }

    public String getAmendableBasket(String bookingRef) {
        Basket basketTmp = getBasketHelper().getBasket(getBasketHelper().createAmendableBasket(bookingRef), testData.getChannel());
        testData.setBasketId(basketTmp.getCode());
        return basketTmp.getCode();
    }

    public String getAmendableBasketWithSavedPassenger(String passengerMix, String fare, Pair withSavedPassenger) throws Throwable {
        String bookingRef = commitBooking(passengerMix, fare, withSavedPassenger);
        return getAmendableBasket(bookingRef);
    }

    /**
     * The method commit a normal flow for a booking
     * @param passengerMix
     * @param fare
     * @param withSavedPassenger pair of boolean, the first value mean if a saved passenger linked to the customer is required; the second value mean if the saved passenger code is required against the update passenger information
     * NB: set to true STORE_SAVED_PASSENGER_CODE (SerenityFacade) in order to save the saved passenger code in the request body to update the passenger
     * NB: set to true UPDATE_PASSENGER_BASED_SAVED_PASSENGER (SerenityFacade) in order to save the saved passenger information (firstname, lastname and title) of the saved passenger against the update the passenger payload
     * @return amendable basket ref
     * @throws Throwable
     */
    private String commitBooking(String passengerMix, String fare, Pair withSavedPassenger) throws Throwable {
        // create customer (with saved passenger based on parameter)
        if((Boolean) withSavedPassenger.getValue()) {
            savedPassengerHelper.addValidPassengerToExistingCustomer();
        } else {
            customerHelper.createCustomerAndLoginIt();
        }

        // find flight + add flight
        getBasketHelper().myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fare, false);
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        testData.setData(PASSENGER_CODES, basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).findFirst().orElseThrow(() -> new IllformedLocaleException("No passenger in the basket")).getCode());

        // update passenger + get customer
        Passengers savedTraveller = travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse());
        Passenger passenger = savedTraveller.getPassengers().stream().findFirst().orElseThrow(() -> new IllegalAccessException("No passenger in the basket"));
        passenger.setSaveToCustomerProfile((Boolean) withSavedPassenger.getKey());
        if((Boolean) withSavedPassenger.getValue() && testData.keyExist(UPDATE_PASSENGER_BASED_SAVED_PASSENGER) && (boolean) testData.getData(UPDATE_PASSENGER_BASED_SAVED_PASSENGER)){
            getCustomer(testData.getData(CUSTOMER_ID), testData.getChannel());
            passenger.getPassengerDetails().getName().setFirstName(customerProfileService.getResponse().getCustomer().getAdvancedProfile().getSavedPassengers().stream().findFirst().orElseThrow(() -> new IllformedLocaleException("No saved passenger found")).getFirstName());
            passenger.getPassengerDetails().getName().setLastName(customerProfileService.getResponse().getCustomer().getAdvancedProfile().getSavedPassengers().stream().findFirst().orElseThrow(() -> new IllformedLocaleException("No saved passenger found")).getLastName());
            passenger.getPassengerDetails().getName().setTitle(customerProfileService.getResponse().getCustomer().getAdvancedProfile().getSavedPassengers().stream().findFirst().orElseThrow(() -> new IllformedLocaleException("No saved passenger found")).getTitle());
        }
        if(testData.keyExist(STORE_SAVED_PASSENGER_CODE) && (boolean) testData.getData(STORE_SAVED_PASSENGER_CODE)) {
            savedTraveller.getPassengers().stream().findFirst().orElseThrow(() -> new IllegalAccessException("No passenger in the basket")).setUpdateSavedPassengerCode(testData.getData(SAVED_PASSENGER_CODE));
        }
        basketHelper.updatePassengersForChannel(savedTraveller,
                testData.getChannel(),
                basketHelper.getBasketService().getResponse().getBasket().getCode());
        // if the channel is AD the customer is already in session as ana agent
        if(!testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CHANNEL) && !testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CUSTOMER_SERVICE)) {
            getCustomer(testData.getData(CUSTOMER_ID), testData.getChannel());
            testData.setData(SAVED_PASSENGER_SIZE, customerProfileService.getResponse().getCustomer().getAdvancedProfile().getSavedPassengers().size());
        }

        // commit booking
        commitBookingFromBasket(basketHelper.getBasketService().getResponse());

        // create amendable basket and return ref
        String bookingRef = getCommitBookingService().getResponse().getBookingConfirmation().getBookingReference();
        testData.setBasketId(bookingRef);
        testData.setData(BOOKING_ID, bookingRef);
        return bookingRef;
    }

    public BookingConfirmationResponse commitBookingFromBasket(BasketsResponse basketsResponse) throws Throwable {
        paymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(basketsResponse.getBasket());
        return createNewBooking(new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).xClientTransactionId(CommitBookingFactory.X_CLIENT_TRANSACTION_ID).build(), aBooking(basketsResponse, paymentMethod)));
    }

    /**
     * Create booking with hold Items
     *
     * @param passengerMix
     * @param fare
     * @param numberOfFlights
     * @return
     * @throws Throwable
     */
    public String createBookingWithHoldItemAndGetAmendable(String passengerMix, String fare, int numberOfFlights, String product) throws Throwable {
        String bookingRef = commitBookingWithHoldItems(passengerMix, fare, numberOfFlights, product);
        Basket basketTmp = getBasketHelper().getBasket(getBasketHelper().createAmendableBasket(bookingRef), testData.getChannel());
        return basketTmp.getCode();
    }

    private String commitBookingWithHoldItems(String passengerMix, String fare, int numberOfFlights, String product) throws Throwable {
        customerHelper.createCustomerAndLoginIt();
        if (numberOfFlights > 1) {
            getBasketHelper().myBasketContainsManyFlightWithPassengerMix(numberOfFlights, passengerMix, testData.getChannel(), fare, OUTBOUND);
        } else {
            getBasketHelper().myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fare, false);
        }
        if (product.equals("hold bag")) {
            basketHoldItemsHelper.buildRequestToAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
            basketHoldItemsHelper.invokeServiceAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        } else if (product.equals("sport equipment")) {
            basketHoldItemsHelper.buildRequestToAddSportEquipment(testData.getData(SerenityFacade.DataKeys.CHANNEL));
            basketHoldItemsHelper.invokeServiceAddSportItems(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        }
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        createNewBooking(createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse(), testData.getData(CUSTOMER_ID), testData.getChannel(), true));
        commitBookingService.getResponse();
        String bookingRef = getCommitBookingService().getResponse().getBookingConfirmation().getBookingReference();
        testData.setBasketId(bookingRef);
        testData.setData(BOOKING_ID, bookingRef);
        return bookingRef;
    }

    public void setPaymentMethod(String paymentType) throws EasyjetCompromisedException {
        switch (paymentType)
        {
            case "credit card":
                paymentMethod = PaymentMethodFactory.generateCreditCardPaymentMethod(basketHelper.getBasketService().getResponse().getBasket());
            break;
        }
    }
}
