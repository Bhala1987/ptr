package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.AmendCommitBookingDao;
import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.database.hybris.dao.PassengerTypeDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.database.hybris.models.PassengerTypeDbModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.BookingConfirmationAssertion;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Name;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.SpecialRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.CustomerProfileQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.UpdatePassengerDetailsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendBasicDetailsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendPassengerSSRRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.DeletePassengerSSRRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.UpdateBasicDetailsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.BasketContent;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.CommitBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateSavedPassengerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.BasketContentFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PaymentMethodFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AmendBasicDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AmendPassengerSSRRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateBasicDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.HoldItemsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.SavedSSRs;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketTravellerService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.UpdateBasicDetailsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.*;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.document.steps.services.checkinservices.CheckInForFlightSteps;
import feature.document.steps.services.createbookingservices.GetPaymentMethodsForChannelSteps;
import feature.document.steps.services.managebookingservices.CreateBasketSteps;
import feature.document.steps.services.managebookingservices.GetBookingSteps;
import feature.document.steps.services.managecustomerservices.RegisterCustomerSteps;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoopForSearchBooking;
import static com.hybris.easyjet.fixture.hybris.asserters.BookingConfirmationAssertion.BOOKING_STATUS.COMPLETED;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;
import static feature.document.steps.constants.StepsRegex.PASSENGER_TYPES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by prite,tejal,dan on 09/11/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class CommitBookingSteps {
    protected  static Logger LOG = LogManager.getLogger(CommitBookingSteps.class);

    @Autowired
    AmendBasicDetailsHelper amendBasicDetailsHelper;

    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper commitBookingHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private AddEJPlusSeatToBasketHelper addEJPlusSeatToBasketHelper;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private BookingCommentHelper bookingCommentHelper;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    @Autowired
    private CartDao cartDao;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private AmendCommitBookingDao amendCommitBookingDao;
    @Autowired
    private HoldItemsDao holdItemsDao;
    @Autowired
    private PassengerTypeDao passengerTypeDao;

    private com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name updateName;
    private com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name name;
    private Map<String, String> updatedFields;
    @Autowired
    private ManageBookingHelper manageBookingHelper;
    private BasketContent basketContentWithHoldItem;
    private CustomerProfileService customerProfileService;
    private FlightsService flightsService;
    private CommitBookingService commitBookingService;
    private BookingConfirmationResponse bookingConfirmationResponse;
    private GetBookingResponse bookingContext;
    private GetBookingService getBookingService;
    private FindFlightsResponse.Flight flight;
    private BasketsResponse basketUsed;
    private CommitBookingRequest commitBookingRequest;
    private String theStaffCustomerUID = "cus00000001";
    private String theChosenCustomerUID;
    private CustomerProfileResponse theStaffProfile;
    private AmendBasicDetailsService amendBasicDetailsService;
    private AmendPassengerSSRService amendPassengerSSRService;
    private GetAmendableBookingService amendableBookingService;
    private BasketTravellerService travellerService;
    private Basket basket;
    private String ordRefId;
    private String channelUsed;
    private Passengers updatePassengersRequestBody;
    private List<HoldItemsResponse.HoldItems> holdItems;
    private Basket.Passenger passenger;
    private String passengerType;
    private Integer beforeAllocation;
    private Integer afterAllocation;

    private ChangeFlightService changeFlightService;
    @Steps
    private CheckInForFlightSteps checkInForFlightSteps;
    @Autowired
    private SavedPassengerHelper passengerHelper;
    @Autowired
    private AccountPasswordHelper accountPasswordHelper;
    @Autowired
    private TravellerHelper travellerHelper;
    @Steps
    private feature.document.steps.services.createbookingservices.CommitBookingSteps commitBookingSteps;
    @Steps
    private GetBookingSteps getBookingSteps;
    @Steps
    private GetPaymentMethodsForChannelSteps getPaymentMethodsForChannelSteps;
    @Steps
    private CreateBasketSteps createBasketSteps;
    @Autowired
    private CancelBookingHelper cancelBookingHelper;

    @Steps
    private RegisterCustomerSteps registerCustomerSteps;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private String passengerId;
    private UpdateBasicDetailsRequestBody.UpdateBasicDetailsRequestBodyBuilder updateBasicDetailsRequestBody;
    private UpdatePassengerDetailsQueryParams.UpdatePassengerDetailsQueryParamsBuilder updatePassengerDetailsQueryParams;
    private UpdateBasicDetailsService updateBasicDetailsService;

    @Given("^I have a valid booking via (.*)$")
    public void iHaveAValidBooking(String channel) throws Throwable {
        testData.setChannel(channel);
        commitBookingHelper.createNewBookingRequest(CommonConstants.ONE_ADULT, channel);
    }

    @Given("^basket contains return flight for (.*) passengers (.*) fare via the (.*) channel$")
    public void myBasketContainsReturnFlightForPassengersAddedViaTheChannel(String passengers, String bundle, String channel) throws Throwable {
        testData.setChannel(channel);
        flightsService = flightHelper.getFlights(testData.getChannel(), passengers, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        basketHelper.addReturnFlightWithTaxToBasketAsChannelJourneyType(flightsService, passengers, "GBP", channel, bundle, "RETURN");
    }

    @When("^I do the commit booking with parameter ([^\"]*)$")
    public void iDoTheCommitBookingForBooking(String typeOfBooking) throws Throwable {
        commitBookingRequest = commitBookingHelper.createNewBookingRequestForError(typeOfBooking, CommonConstants.ONE_ADULT, STANDARD, 1, "");
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
    }

    @Then("^an error message is returned for each ([^\"]*)$")
    public void anErrorMessageIsReturnedForEach(String parameter) throws Throwable {
        commitBookingService.assertThatErrors().containedTheCorrectErrorMessage(parameter);
    }

    @When("^I do the commit booking$")
    public void iDoTheCommitBooking() throws Throwable {
        if (testData.getChannel().equals("")) {
            testData.setChannel("Digital");
        }
        commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(
                basketHelper.getBasketService().getResponse(),
                testData.getChannel());
        basketHelper.getBasket(
                basketHelper.getBasketService().getResponse().getBasket().getCode(),
                testData.getChannel());

        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);

        commitBookingService.invoke();

        pollingLoop().untilAsserted(() -> commitBookingService.assertThat().gotAValidResponse());
        bookingConfirmationResponse = commitBookingService.getResponse();
        getBookingContext();
    }

    @When("^I do the booking$")
    public void iDoTheCommitBookingForAllChannel() throws Throwable {

        commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService()
                .getResponse(), testData.getChannel());
        basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());

        try {
            pollingLoop().untilAsserted(() -> {
                List<Basket.Passenger> passengers = getOutBoundPassengers();
                assertThat(passengers.stream().filter(pg -> StringUtils.isNotBlank(pg.getPassengerDetails().getName().getTitle()) && pg.getAge() != null));
            });
        } catch (ConditionTimeoutException e) {
            fail("Missing mandatory passenger information(name/title/age)");
        }
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
//        pause();
        commitBookingService.assertThat().gotAValidResponse();
        bookingConfirmationResponse = commitBookingService.getResponse();
        getBookingContext();
    }


    @When("^I do the commit booking with (.*) passengers$")
    public void iDoTheCommitBooking(String passengerMix) throws Throwable {
        commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasketForGivenFlight(basketHelper.getBasketService()
                .getResponse(), testData.getChannel());
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
//        pause();
        commitBookingService.assertThat().gotAValidResponse();
        bookingConfirmationResponse = commitBookingService.getResponse();
        getBookingContext();
    }

    private GetBookingResponse getBookingContext() {
        pollingLoop().untilAsserted(() ->
                {
                    bookingContext = commitBookingHelper.getBookingDetails(
                            bookingConfirmationResponse.getConfirmation().getBookingReference(),
                            testData.getChannel());
                    testData.setBookingResponse(bookingContext);
                    assertThat(bookingContext.getBookingContext().getBooking().getBookingStatus().equalsIgnoreCase("COMPLETED"));
                }
        );
        return bookingContext;
    }

    @Then("^an error (.*) is returned for duplicate booking$")
    public void anErrorMessageIsReturnedForDuplicateBooking(String errorCode) throws Throwable {

        commitBookingHelper.getCommitBookingService()
                .assertThatErrors()
                .containedTheCorrectErrorMessage(errorCode);
    }

    @And("^a booking reference (.*) returned")
    public void aBookingReferenceIsReturned(String condition) throws Throwable {
        if (condition.equalsIgnoreCase("is")) {
            pollingLoop().untilAsserted(() -> {
                assertThat(bookingConfirmationResponse.getConfirmation().getBookingReference()).isNotEmpty();
            });
        } else {
            assertThat(bookingConfirmationResponse.getConfirmation().getBookingReference()).isEmpty();
        }
    }

    @And("^the booking reference is returned with a ([^\"]*) status")
    public void aBookingReferenceIsReturnedWithAStatus(String status) throws Throwable {
        aBookingReferenceIsReturned("is");

        assertThat(bookingConfirmationResponse.getConfirmation().getBookingStatus()).isEqualToIgnoringCase(status);
    }

    @Given("^I have committed a booking via ([^\"]*)$")
    public void iHaveCommittedABookingVia(String channel) throws Throwable {
        testData.setChannel(channel);
        bookingConfirmationResponse = commitBookingHelper.createNewBooking(commitBookingHelper.createNewBookingRequestForChannel(channel));
        getBookingContext();
    }

    @Then("^order is created from cart$")
    public void orderIsCreatedFromCart() throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .theBasketWasAddedToTheBooking(basketHelper.getBasketService().getResponse());
    }

    @Then("^passenger details are created with status as Booked$")
    public void passengerDetailsAreCreatedWithStatusAsBooked() throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .thePassengersDetailsAreStoredInTheBooking(basketHelper.getBasketService().getResponse());
    }

    @Then("^customer profile is linked with the booking$")
    public void customerProfileIsLinkedWithTheBooking() throws Throwable {
        commitBookingHelper.getBookingDetails(bookingConfirmationResponse.getConfirmation().getBookingReference(), testData.getChannel());
        try {
            pollingLoop().untilAsserted(() -> commitBookingHelper.getGetBookingService()
                .assertThat()
                .theCustomerDetailsAreAssociatedWithTheBooking(commitBookingHelper.getCustomerProfileService())
            );
        } catch (ConditionTimeoutException e) {
            throw new EasyjetCompromisedException("Customer details not associated with booking");
        }

    }

    @Then("^created date time is stored$")
    public void createdDateTimeIsStored() throws Throwable {
        commitBookingHelper.getGetBookingService().assertThat().theBookingTimeIsRecorded();
    }


    @When("^I do the commit booking for the same flight and passengers$")
    public void iDoTheCommitBookingForTheSameFlightAndPassengers() throws Throwable {
        commitBookingHelper.createDuplicateBooking(testData.getChannel());
    }

    @Then("^Booking is created from Cart and it has the flight details$")
    public void bookingIsCreatedFromCartAndItHasTheFlightDetails() throws Throwable {
        BookingPathParams params = BookingPathParams.builder()
                .bookingId(bookingConfirmationResponse.getConfirmation().getBookingReference())
                .build();

        getBookingService = serviceFactory.getBookings(
                new GetBookingRequest(
                        HybrisHeaders.getValid(testData.getChannel()).build(),
                        params
                )
        );

        pollingLoop().ignoreExceptions().untilAsserted(
                () -> {
                    getBookingService.invoke();
                    basketUsed = commitBookingHelper.getBasketUsed();
                    getBookingService.assertThat().theBasketWasAddedToTheBooking(basketUsed);
                }
        );
    }

    @When("^I do the booking with valid basket content for ([^\"]*)$")
    public void iDoTheBookingWithValidBasketContentFor(String channel) throws Throwable {
        testData.setChannel(channel);
        bookingConfirmationResponse = commitBookingHelper.createNewBookingForPublicChannel("", channel);
        getBookingContext();
    }

    @When("^I do the booking with valid basket content$")
    public void iDoTheBookingWithValidBasketContent() throws Throwable {
        basket = basketHelper.getBasketService().getResponse().getBasket();

        CommitBookingRequestBody commitBooking = CommitBookingRequestBody.builder()
                .bookingType(basket.getBasketType())
                .bookingReason(basket.getBookingReason())
                .basketContent(testData.getBasketContent())
                .paymentMethods(Collections.singletonList(PaymentMethodFactory.generateDebitCardPaymentMethod(basket)))
                .overrideWarning(true)
                .build();

        commitBookingService = serviceFactory.commitBooking(new CommitBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), commitBooking));
        commitBookingService.invoke();

        bookingConfirmationResponse = commitBookingService.getResponse();
    }

    @And("^Payment transaction are recorded in the booking$")
    public void paymentTransactionAreRecordedInTheBooking() throws Throwable {
        commitBookingHelper.getGetBookingService().assertThat()
                .arePaymentDetailsRecordedOnBooking(commitBookingHelper.getCommitBookingRequest());
    }

    @When("^I do the corporate commit booking with ([^\"]*) deal information for \"([^\"]*)\"$")
    public void iDoTheCorporateCommitBookingWithDealInformationFor(String option, String channel) throws Throwable {

        testData.setChannel(channel);
        commitBookingRequest = commitBookingHelper.createNewCorporateBookingRequestMockPayment(channel, option);
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
        commitBookingService.assertThat().gotAValidResponse();
        bookingConfirmationResponse = commitBookingService.getResponse();
        getBookingContext();
    }

    @When("^I do the commit booking with basket content which has \"([^\"]*)\" via \"([^\"]*)\"$")
    public void iDoTheCommitBookingWithBasketContentWhichHasVia(String criteria, String channel) throws Throwable {
        testData.setChannel(channel);
        bookingConfirmationResponse = commitBookingHelper.createNewBookingForPublicChannel(criteria, channel);
        getBookingContext();
    }

    @Then("^I should be able to create successful booking with reference number$")
    public void iShouldBeAbleToCreateSuccessfulBookingWithReferenceNumber() throws Throwable {
        assertThat(bookingConfirmationResponse.getConfirmation().getBookingReference()).isNotEmpty();
        //TODO investigating on what should be the right status
        commitBookingHelper.getCommitBookingService().assertThat().bookingStatusAs(COMPLETED);
    }

    @Then("^I created a successful booking with reference number$")
    public void iCreatedASuccessfulBookingWithReferenceNumber() throws Throwable {
        CommitBookingService commitBooking = testData.getData(SERVICE);
        commitBooking.assertThat().bookingStatusAs(COMPLETED);
    }

    @And("^booking should have details of newly created customer$")
    public void bookingShouldHaveDetailsOfNewlyCreatedCustomer() throws Throwable {
        commitBookingHelper.getBookingDetails(
                bookingConfirmationResponse.getBookingConfirmation().getBookingReference(),
                testData.getChannel()
        );

        if (customerProfileService == null) {
            customerProfileService = commitBookingHelper.associateCustomerProfile(testData.getChannel(), getBookingContext().getBookingContext().getBooking().getBookingContact().getCustomerId());
        }

        try {
            pollingLoop().untilAsserted(
                    () -> commitBookingHelper.getGetBookingService()
                            .assertThat()
                            .theCustomerDetailsAreAssociatedWithTheBooking(customerProfileService)
            );
        } catch (ConditionTimeoutException e) {
            throw new EasyjetCompromisedException("Customer details not associated with booking");
        }

    }

    @When("^I do the commit booking with ([^\"]*) via ([^\"]*)$")
    public void iDoTheCommitBookingWithInTheBasketContentVia(String criteria, String channel) throws Throwable {
        testData.setChannel(channel);
        bookingConfirmationResponse = commitBookingHelper.createNewBookingForPublicChannel(criteria, channel);

        assertThat(Optional.of(bookingConfirmationResponse.getBookingConfirmation().getBookingReference()).isPresent()).isTrue();
    }

    @When("^I do the corporate booking with deal for \"([^\"]*)\"$")
    public void iDoTheCorporateBookingWithValidBasketContentFor(String channel) throws Throwable {
        testData.setChannel(channel);
        bookingConfirmationResponse = commitBookingHelper.createCorporateBookingWithDealForPublicChannel("corporate", channel);
        getBookingContext();
    }

    @When("^I call the commit booking with missing parameter then we get respective error as below$")
    public void iCallTheCommitBookingWithMissing(Map<String, String> parameters) throws Throwable {
        BasketsResponse basketsResponse = commitBookingHelper.getBasket(CommonConstants.PUBLIC_API_B2B_CHANNEL);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            commitBookingRequest = commitBookingHelper.createNewBookingRequestForPublicChannelWithError(basketsResponse, parameter.getKey());
            commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
            commitBookingService.invoke();
            commitBookingService.assertThatErrors().containedTheCorrectErrorMessage(parameter.getValue());
        }
        commitBookingHelper.clearBasket(basketsResponse.getBasket().getCode());
    }


    @When("^I do the booking with valid basket content for same passenger and flight via \"([^\"]*)\"$")
    public void iDoTheBookingWithValidBasketContentForSamePassengerAndFlightVia(String channel) throws Throwable {
        testData.setChannel(channel);
        commitBookingHelper.createNewBookingForPublicChannel("", channel);
        commitBookingHelper.createDuplicateBookingForPublicChannel(commitBookingHelper.getCommitBookingRequest(), channel);
    }

    @And("^Booking is associated with newly created customer$")
    public void bookingIsAssociatedWithNewlyCreatedCustomer() throws Throwable {
        getBookingService
                .assertThat()
                .theBookingAssociatedToCustomer(customerProfileService.getResponse().getCustomer(), bookingConfirmationResponse.getConfirmation().getBookingReference());
    }

    @Then("^I do get customer profile$")
    public void iDoGetCustomerProfile() throws Throwable {
        customerProfileService = commitBookingHelper.associateCustomerProfile(testData.getChannel(), getBookingService.getResponse().getBookingContext().getBooking().getBookingContact().getCustomerId());
    }

    @And("^passenger has the respective bundle code$")
    public void passengerHasTheRespectiveBundleCode() throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .thePassengersDetailsHasExpectedBundleDetailsBooking(basketHelper.getBasketService().getResponse());
    }

    @And("^I have return flight for (.*)$")
    public void iHaveReturnFlightForPassengermix(String passengers) throws Throwable {
        RemoveFlightFromBasketSteps steps = new RemoveFlightFromBasketSteps();
        steps.iHaveFoundAValidOutboundFlightForPassengerMix(passengers);
    }

    @When("^I have basket content with passenger mix as (.*) and (.*) and criteria as (.*)$")
    public void iDoCommitBookingWithPassengerMixAsPassengerMixAndFaretypeAndCriteriaAs(String passengerMix, String fareType, String criteria) throws Throwable {
        commitBookingHelper.getUpdatedBasketContent(fareType, testData.getJourneyType(), criteria);
        testData.setBasketContent(commitBookingHelper.getBasketContent());
    }


    @And("^I have valid basket content with passenger mix as (.*) and (.*)$")
    public void iHaveValidBasketContentWithPassengerMixAsPassengerMixAndFareType(String passengerMix, String fareType) throws Throwable {
        commitBookingHelper.getValidBasketContent(fareType, testData.getJourneyType());
        testData.setBasketContent(commitBookingHelper.getBasketContent());
    }

    @And("^I have basket content with faretype as (.*) and journeyType as (.*) with criteria (.*)$")
    public void iHaveBasketContentWithFaretypeAsFareTypeAndJourneyTypeAsJourneyType(String fareType, String journeyType, String criteria) throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        commitBookingHelper.getUpdatedBasketContent(basketService.getResponse(), criteria);
        testData.setBasketContent(commitBookingHelper.getBasketContent());
    }

    @Given("^I have the basket content with invalid request (.*)$")
    public void iHaveTheBasketContentWithInvalidRequest(String parameter) throws Throwable {
        if (parameter.equals("Empty_BasketContent")) {
            testData.setBasketContent(null);
        } else
            commitBookingHelper.getBasketContentWithInvalidParam(parameter);
    }

    @And("^I have basket content$")
    public void iHaveBasketContentWithFirstFlightOnJourneyJourneyIsNotAvailable() throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        commitBookingHelper.getUpdatedBasketContent(basketService.getResponse(), "");
        testData.setBasketContent(commitBookingHelper.getBasketContent());
    }

    @Given("^I am a staff member and logged in as user (.*) and (.*)$")
    public void iAmAStaffAndLoggedInAsUser(String user, String pwd) throws Throwable {
        theChosenCustomerUID = theStaffCustomerUID;

        if (testData.getChannel().contains("AD"))
            bookingCommentHelper.agentLogin(user, pwd);
        else if (testData.getChannel().equalsIgnoreCase("Digital"))
            customerHelper.loginWithValidCredentials(testData.getChannel(), user, pwd, false);

        getStaffProfile();

        testData.setData(CUSTOMER_ID, theChosenCustomerUID);
        testData.setData(HEADERS, HybrisHeaders.getValid(testData.getChannel()));
    }

    @Given("^I have valid basket for a (.*) booking type and (.*) fare$")
    public void iHaveValidBasketForBookingTypeAndFareType(String bookingType, String fareType) throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), CommonConstants.ONE_ADULT, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        flight = flightsService.getOutboundFlight();
        AddFlightRequestBody aFlight = basketHelper.createAddFlightRequestWithBookingTypeAndFareType(flight, bookingType, fareType, CommonConstants.ONE_ADULT);
        aFlight.setFareType(fareType);
        basketHelper.addFlightToBasketAsChannel(aFlight, testData.getChannel());
    }

    @When("^I do the commit booking for a staff customer$")
    public void iDoTheCommitBookingforStaff() throws Throwable {
        commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasketWithSignificanOtherPassanger(basketHelper.getBasketService()
                .getResponse(), theStaffCustomerUID, testData.getChannel(), theStaffProfile.getCustomer().getAdvancedProfile().getSignificantOthers().getPassengers().get(0));

        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
        commitBookingService.assertThat().gotAValidResponse();
        bookingConfirmationResponse = commitBookingService.getResponse();

        getBookingContext();
    }

    @Then("^I will link the booking to the staff customer$")
    public void iLinkBookingToStaff() throws Throwable {
        try {
            pollingLoop().untilAsserted(() -> {
                commitBookingHelper.getGetBookingService()
                        .assertThat()
                        .theCustomerDetailsAreAssociatedWithTheBooking(commitBookingHelper.getCustomerProfileService());
            });
        } catch (ConditionTimeoutException e) {
            throw new EasyjetCompromisedException("Customer details not associated with booking");
        }
    }

    @And("^I will return Confirmation response is generated back to the Channel$")
    public void iReturnConfirmationResponseIsGeneratedBack() throws Throwable {

        try {
            pollingLoopForSearchBooking().untilAsserted(() -> {
                commitBookingHelper.getGetBookingService()
                        .assertThat()
                        .theBasketWasAddedToTheBooking(basketHelper.getBasketService().getResponse());
            });
        } catch (ConditionTimeoutException e) {
            throw new EasyjetCompromisedException("Basket was not added to the booking");
        }

    }

    private Name updateNameOfSignificantOtherForStaff(Name aChosenName) {
        aChosenName.setFirstName(
                theStaffProfile.getCustomer().getAdvancedProfile().getSignificantOthers().getPassengers().get(0).getFirstName()
        );

        aChosenName.setLastName(
                theStaffProfile.getCustomer().getAdvancedProfile().getSignificantOthers().getPassengers().get(0).getLastName()
        );

        return aChosenName;
    }

    private void getStaffProfile() {
        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(theStaffCustomerUID).path(PROFILE).build();
        customerProfileService = serviceFactory.getCustomerProfile(
                new ProfileRequest(HybrisHeaders.getValid(testData.getChannel()).prefer("FULL").authorization("Bearer ".concat(testData.getAccessToken())).build(), profilePathParams, CustomerProfileQueryParams.builder().sections("dependants").build())
        );
        customerProfileService.invoke();
        theStaffProfile = customerProfileService.getResponse();
    }

    @When("^do commit booking with (.*) via ([^\"]*)$")
    public void doCommitBookingWithPriceChangeVia(String criteria, String channel) throws Throwable {
        testData.setChannel(channel);
        commitBookingHelper.createNewBookingPublicApiB2BChannel(criteria);
        testData.setData(SERVICE, commitBookingHelper.getCommitBookingService());
    }

    @And("^I will delete the temp basket$")
    public void iWillDeleteTheTempBasket() throws Throwable {
        assertThat(cartDao.isBasketExists(testData.getBasketId())).isFalse();
    }

    @When("^I do commit booking for given basket$")
    public void iDoTheCommitBookingForGivenBasket() throws Throwable {
        basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());

        try {
            List<Basket.Passenger> outBoundPassengers = getOutBoundPassengers();
            if (CollectionUtils.isNotEmpty(outBoundPassengers)) {
                pollingLoop().untilAsserted(() -> {
                    assertThat(outBoundPassengers.stream().filter(pg -> StringUtils.isNotBlank(pg.getPassengerDetails().getName().getTitle()) && pg.getAge() != null));
                });
            }
            List<Basket.Passenger> inboundPassengers = getInboundPassengers();
            if (CollectionUtils.isNotEmpty(inboundPassengers)) {
                pollingLoop().untilAsserted(() -> {
                    assertThat(inboundPassengers.stream().filter(pg -> StringUtils.isNotBlank(pg.getPassengerDetails().getName().getTitle()) && pg.getAge() != null));
                });
            }
        } catch (ConditionTimeoutException e) {
            fail("Missing mandatory passenger information");
        }

        commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse());
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();

        commitBookingService.assertThat().gotAValidResponse();
        bookingConfirmationResponse = commitBookingService.getResponse();
        testData.setData(BOOKING_ID, bookingConfirmationResponse.getBookingConfirmation().getBookingReference());

        if (bookingConfirmationResponse.getBookingConfirmation().getAuthentication() != null) {
            testData.setAccessToken(bookingConfirmationResponse.getBookingConfirmation().getAuthentication().getAccessToken());
        }

        getBookingContext();
    }

    @When("^I do commit booking for given basket with unavailable inventory$")
    public void iDoTheCommitBookingForGivenBasketInventory() throws Throwable {
        basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        try {
            List<Basket.Passenger> outBoundPassengers = getOutBoundPassengers();
            if (CollectionUtils.isNotEmpty(outBoundPassengers)) {
                pollingLoop().untilAsserted(() -> {
                    assertThat(outBoundPassengers.stream().filter(pg -> StringUtils.isNotBlank(pg.getPassengerDetails().getName().getTitle()) && pg.getAge() != null));
                });
            }
            List<Basket.Passenger> inboundPassengers = getInboundPassengers();
            if (CollectionUtils.isNotEmpty(inboundPassengers)) {
                pollingLoop().untilAsserted(() -> {
                    assertThat(inboundPassengers.stream().filter(pg -> StringUtils.isNotBlank(pg.getPassengerDetails().getName().getTitle()) && pg.getAge() != null));
                });
            }

        } catch (ConditionTimeoutException e) {
            fail("Missing mandatory passenger information");
        }
        commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse());
        basket=basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
        testData.setData(SERVICE, commitBookingService);
    }

    @And("^booking has APIS details for each passenger$")
    public void bookingHasAPISDetailsForEachPassenger() throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .thePassengerHasAPISDetails(basketHelper.getBasketService().getResponse());
    }

    @And("^the booking has seat details for respective passengers$")
    public void theBookingHasSeatDetailsForRespectivePassengers() throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .thePassengerHasSeatDetails(basketHelper.getBasketService().getResponse());

    }

    @And("^the booking has product (.*) details$")
    public void theBookingHasProductHoldItemDetails(List<String> productType) throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .thePassengerHasProductDetails(basketHelper.getBasketService().getResponse());

    }

    @And("^the booking has details of respective (.*)$")
    public void theBookingHasDetailsOfRespectiveProducts(List<String> productType) throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .thePassengerHasProductDetails(basketHelper.getBasketService().getResponse());
    }

    @And("^the booking has the cabin bag for each passenger$")
    public void theBookingHasTheCabinBagForEachPassenger() throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .thePassengerHasCabinBagDetails(basketHelper.getBasketService().getResponse());
    }

    @When("^I do get booking details via (.*)$")
    public void iDoGetBookingDetails(String channel) throws Throwable {
        if (channel.equalsIgnoreCase(CommonConstants.PUBLIC_API_B2B_CHANNEL)) {
            testData.setChannel(channel);
        }
        getBookingContext();
    }

    @And("^the booking has details of allowed documents$")
    public void theBookingHasDetailsOfAllowedDocuments() throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat().theBookingHasDetailsOfAllowedDocuments();
    }

    @And("^the booking has details of additional seat$")
    public void theBookingHasDetailsOfAdditionalSeat() throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .thePassengerHasAdditionalSeatDetails(basketHelper.getBasketService().getResponse());
    }

    @When("^I do the commit booking for All \"([^\"]*)\"$")
    public void iDoTheCommitBookingForAllChannel(String channel) throws Throwable {
        channelUsed = null;
        basket = null;
        commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(
                basketHelper.getBasketService().getResponse(),
                channel
        );
        basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), channel);

        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
        commitBookingService.assertThat().gotAValidResponse();
        bookingConfirmationResponse = commitBookingService.getResponse();
        channelUsed = channel;
    }

    @And("^verify the booking status changed to \"([^\"]*)\"$")
    public void verifyTheBookingStatusChangedTo(String status) throws Throwable {
        try {
            pollingLoop().ignoreExceptions().untilAsserted(
                    () -> assertThat(
                            commitBookingHelper.getBookingDetails(
                                    bookingConfirmationResponse.getConfirmation().getBookingReference(),
                                    testData.getChannel()
                            ).getBookingContext().getBooking().getBookingStatus()
                    ).isEqualToIgnoringCase(status));
        } catch (ConditionTimeoutException e) {
            fail("BOOKING STATUS IS NOT COMPLETED !!!");
        }
    }

    private void verifyTheOrderIsInEditMode() throws Throwable {
        basket = null;
        ordRefId = null;

        String bookingId;
        if (bookingConfirmationResponse == null) {
            bookingId = testData.getData(SerenityFacade.DataKeys.BOOKING_ID);
        } else {
            bookingId = bookingConfirmationResponse.getBookingConfirmation().getBookingReference();
        }

        basketHelper.createAmendableBasket(bookingId);
        amendableBookingService = basketHelper.getGetAmendableBookingService();
        assertThat(
                amendableBookingService.getResponse().getOperationConfirmation().getBasketCode()
        ).isNotNull();

        basket = basketHelper.getBasket(amendableBookingService.getResponse().getOperationConfirmation().getBasketCode(), testData.getChannel());
        assertThat(basket).isNotNull();

        testData.setData("amendableBookingService", amendableBookingService);
        ordRefId = amendableBookingService.getResponse().getOperationConfirmation().getBasketCode();
        testData.setDocumentId(ordRefId);
        testData.setAmendableBasket(basket.getCode());
        testData.setBasketId(ordRefId);
    }

    @And("^I add NIF number with \"([^\"]*)\" and will receive an error code \"([^\"]*)\"$")
    public void iAddNIFNumberWithAndWillReceiveAnErrorCode(String condition, String errorCode) throws Throwable {
        List<Basket.Passenger> passengers = getOutBoundPassengers();

        passengers.forEach(passenger -> {
            try {
                String nif = "";
                switch (condition) {
                    case "incorrect_length":
                        nif = "765445";
                        break;
                    case "incorrect_format":
                        nif = "73538719*";
                        break;
                    default:
                        nif = "";
                }

                invokeAmendBasicDetails(
                        passenger.getCode(),
                        AmendBasicDetailsRequestBody.builder()
                                .nifNumber(nif)
                                .build()
                );

                amendBasicDetailsService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
            } catch (Throwable th) {
                LOG.error(th);
            }
        });
    }

    @And("^I add, update and verify NIF number \"([^\"]*)\" \"([^\"]*)\"$")
    public void iAddUpdateAndVerifyNIFNumber(String addNif, String updateNif) throws Throwable {
        List<Basket.Passenger> passengers = getOutBoundPassengers();
        passengers.forEach(passenger -> {
            basket = null;
            // Add the nif number.
            invokeAmendBasicDetails(
                    passenger.getCode(),
                    AmendBasicDetailsRequestBody.builder()
                            .nifNumber(addNif)
                            .build()
            );
            amendBasicDetailsService.assertThat().basketIsUpdated();
            verifyBasket(passenger.getCode(), addNif);

            // Update the nif number
            invokeAmendBasicDetails(
                    passenger.getCode(),
                    AmendBasicDetailsRequestBody.builder()
                            .nifNumber(updateNif)
                            .build()
            );

            amendBasicDetailsService.assertThat().basketIsUpdated();
            verifyBasket(passenger.getCode(), updateNif);
        });
    }

    /**
     * invokeAmendBasicDetails, it invokes AmendBasicDetailsService
     *
     * @param passengerCode                The passenger to amend
     * @param amendBasicDetailsRequestBody The body of the request to send.
     */
    private void invokeAmendBasicDetails(String passengerCode, AmendBasicDetailsRequestBody amendBasicDetailsRequestBody) {
        BasketPathParams basketPathParams = BasketPathParams.builder()
                .basketId(ordRefId)
                .passengerId(passengerCode)
                .path(BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS)
                .build();

        amendBasicDetailsService = serviceFactory.amendBasicDetails(
                new AmendBasicDetailsRequest(
                        HybrisHeaders.getValid(testData.getChannel()).build(),
                        basketPathParams,
                        UpdatePassengerDetailsQueryParams.builder().allRelatedFlights("false").build(),
                        amendBasicDetailsRequestBody
                )
        );

        amendBasicDetailsService.invoke();
    }

    /**
     * verifyBasket, it checks the NIF is updated by calling basket service
     *
     * @param passengerCode
     * @param nif
     */
    private void verifyBasket(String passengerCode, String nif) {
        basket = null;
//        pause(5000);
        basket = basketHelper.getBasket(ordRefId, channelUsed);
        assertThat(basket).isNotNull();
        List<Basket.Passenger> rPassengers = getOutBoundPassengers();
        Optional<Basket.Passenger> rPassenger = rPassengers.stream().filter(rpg -> rpg.getCode().equals(passengerCode)).findFirst();
        assertThat(rPassenger).isNotNull();
        assertThat(rPassenger.get().getPassengerDetails().getNifNumber()).contains(nif);
    }

    @And("^receive an error code \"([^\"]*)\" by adding new NIF \"([^\"]*)\" which is already used by other passenger$")
    public void receiveAnErrorCodeByAddingNewNIFWhichIsAlreadyUsedByOtherPassenger(String errorCode, String nif) throws Throwable {
        List<Basket.Passenger> passengers = getOutBoundPassengers();

        AtomicInteger atomicInteger = new AtomicInteger(0);
        passengers.forEach(passenger -> {
            basket = null;

            invokeAmendBasicDetails(
                    passenger.getCode(),
                    AmendBasicDetailsRequestBody.builder()
                            .nifNumber(nif)
                            .build()
            );

            int index = atomicInteger.getAndIncrement();
            switch (index) {
                case 0:
                    amendBasicDetailsService.assertThat().basketIsUpdated();
                    verifyBasket(passenger.getCode(), nif);
                    break;
                case 1:
                    amendBasicDetailsService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
                    break;
            }
        });
    }

    @And("^booking has EjPlusNumber details for each passenger$")
    public void bookingHasEjPlusNumberDetailsForEachPassenger() throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat()
                .thePassengerHasEjPlusNumberDetails(basketHelper.getBasketService().getResponse());
    }

    @And("^I add holdItem as (.*) for (.*) passenger to my basket content$")
    public void iAddHoldItemAsHoldBagToMyBasketContent(String productType, String passengerIndex) throws Throwable {
        holdItems = basketHoldItemsHelper.getHoldItemProduct(testData.getChannel(), productType);
        if (passengerIndex.equalsIgnoreCase("all)")) {
            testData.setBasketContent(BasketContentFactory.getBasketContentWithHoldItem(holdItems));
        } else {
            String[] index = passengerIndex.split("'");
            basketContentWithHoldItem = BasketContentFactory.getBasketContentWithHoldItem(holdItems, Integer.valueOf(index[0]));
            testData.setBasketContent(basketContentWithHoldItem);

        }
        updateBasketTotalsTotals(holdItems);
    }

    private void updateBasketTotalsTotals(List<HoldItemsResponse.HoldItems> holdItems) {

        BigDecimal totalDebitCard = BigDecimal.valueOf(0.0);
        BigDecimal totalCreditCard = BigDecimal.valueOf(0.0);
        for (HoldItemsResponse.HoldItems items : holdItems) {
            totalDebitCard = totalDebitCard.add(BigDecimal.valueOf(items.getPrices().get(0).getBasePrice())).setScale(2, RoundingMode.HALF_UP);
            totalCreditCard = totalCreditCard.add(BigDecimal.valueOf(items.getPrices().get(0).getBasePrice() * 1.05).setScale(2, RoundingMode.HALF_UP));
        }
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Double totalAmountWithCreditCard = basketService.getResponse().getBasket().getTotalAmountWithCreditCard();
        basketService.getResponse().getBasket().setTotalAmountWithCreditCard
                (totalCreditCard.add(BigDecimal.valueOf(totalAmountWithCreditCard)).doubleValue());
        System.out.println("totalAmountWithCreditCard +totalCreditCard :" + (totalCreditCard.add(BigDecimal.valueOf(totalAmountWithCreditCard)).doubleValue()));
        Double totalAmountWithDebitCard = basketService.getResponse().getBasket().getTotalAmountWithDebitCard();
        basketService.getResponse().getBasket().setTotalAmountWithDebitCard
                (totalDebitCard.add(BigDecimal.valueOf(totalAmountWithDebitCard)).doubleValue());
    }

    @And("^I have hold items based on (.*)$")
    public void iHaveHoldItemsBasedOnProductType(String productType) throws Throwable {
        holdItems = basketHoldItemsHelper.getHoldItemProduct(testData.getChannel(), productType);
    }

    @And("^I add hold bag along with excess weight to my basket content$")
    public void iAddHoldBagAlongWithExcessWeightToMyBasketContent() throws Throwable {

    }

    @And("^I have basket content with seats$")
    public void iHaveBasketContentWithSeats() throws Throwable {
        BasketContent content = BasketContentFactory.getBasketContent(commitBookingHelper.getBasketWithSeats(testData.getChannel()).getBasket());
        testData.setBasketContent(content);
    }

    @And("^I add holdItem as (.*) to my basket with missing (.*)$")
    public void iAddHoldItemAsProductTyeToMyBasketWithMissingParameter(String productType, List<String> params) throws Throwable {
        testData.setBasketContent(BasketContentFactory.getBasketContentWithHoldItem(holdItems, 0));
    }

    @And("^I update the basket content with invalid request (.*) for product (.*)$")
    public void iUpdateTheBasketContentWithInvalidRequestParameter(String param, String productType) throws Throwable {
        BasketContent basketContent = BasketContentFactory.getBasketContentWithInvalidOrMissingParameter(basketContentWithHoldItem, param, productType);
        testData.setBasketContent(basketContent);
    }

    @Then("^the booking has been created$")
    public void theBookingHasBeenCreated() throws Throwable {
        commitBookingService.getResponse();
    }

    @When("^I do the commit booking as (.*) with (.*) for (.*) with (.*) different payment (.*)$")
    public void iDoTheCommitBookingAsWithForWithDifferentPayment(String bookingType, String typeOfBooking, String paymentType, int numberOfPayment, String paymentDetails) throws Throwable {
        commitBookingHelper.setPaymentDetails(paymentDetails);
        commitBookingHelper.setPaymentType(paymentType);
        testData.setBookingType(bookingType);
        commitBookingRequest = commitBookingHelper.createNewBookingRequestForError(typeOfBooking, CommonConstants.ONE_ADULT, STANDARD, numberOfPayment, bookingType);
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
    }

    @And("^I do commit booking for passenger mix (.*), booking (.*) and payments with (.*), (.*), (.*) and (.*)$")
    public void iDoCommitBookingForPassengerMixBookingAndPaymentsWithDetails(String passengerMix, String bookingType, String typeOfBooking, int numberOfPayment, String paymentMethods, String paymentDetails) throws Throwable {
        commitBookingHelper.setPaymentDetails(paymentDetails);
        commitBookingRequest = commitBookingHelper.createNewBookingRequestWithMultiplePaymentMethods(null, typeOfBooking, passengerMix, STANDARD, numberOfPayment, bookingType, paymentMethods);
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
        testData.setData(SerenityFacade.DataKeys.SERVICE, commitBookingService);
        testData.setData(SerenityFacade.DataKeys.BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
    }

    @When("^I do commit booking for an (.*) amount with passenger mix (.*), booking (.*) and payments with (.*), (.*), (.*), (.*)$")
    public void iDoCommitBookingWithIncorrectAmountForPassengerMixBookingAndPaymentsWithDetails(String amount, String passengerMix, String bookingType, String typeOfBooking, int numberOfPayment, String paymentMethods, String paymentDetails) throws Throwable {
        commitBookingHelper.setPaymentDetails(paymentDetails);
        commitBookingRequest = commitBookingHelper.createNewBookingRequestWithMultiplePaymentMethods(amount, typeOfBooking, passengerMix, STANDARD, numberOfPayment, bookingType, paymentMethods);
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
        testData.setData(SerenityFacade.DataKeys.SERVICE, commitBookingService);
    }


    @When("^I attempt to commit the booking for (.*) with hold item price change$")
    public void iAttemptToCommitTheBookingForAdultAndPriceChangeForHoldItems(String passengerMix) throws Throwable {
        commitBookingHelper.commitTheBooking(commitBookingHelper.holdItemPriceChangeCommitBookingRequestForPublicChannel(passengerMix, 1));
        testData.setData(SERVICE, commitBookingHelper.getCommitBookingService());
    }

    @Then("^the commit booking should fail with error (.*) affected data (.*)$")
    public void theCommitBookingShouldFailWithErrorAffectedData(String errorCode, String affectedData) throws Throwable {
        CommitBookingService commitBookingService = testData.getData(SERVICE);
        commitBookingService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
        commitBookingService.assertThatErrors().containedTheAffectedData(affectedData);
    }

    @And("^commit the booking$")
    public void commitTheBooking() throws Throwable {
        bookingConfirmationResponse = commitBookingHelper.createNewBooking(commitBookingRequest);
        assertThat(Optional.of(bookingConfirmationResponse.getBookingConfirmation().getBookingReference()).isPresent()).isTrue();
        testData.setData(BOOKING_ID, bookingConfirmationResponse.getBookingConfirmation().getBookingReference());
    }

    @And("^attempt to commit the booking$")
    public void attemptToCommitTheBooking() throws Throwable {
        commitBookingHelper.commitTheBooking(commitBookingRequest);
    }

    @When("^I attempt to commit the booking for (.*) and (\\d+) hold bags$")
    public void iAttemptToCommitTheBookingForPassengersAndQuantityHoldBags(String passengerMix, int holdBags) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setHoldBagCount(holdBags);
        commitBookingHelper.commitTheBooking(commitBookingHelper.createNewBookingForPublicChannelWithProducts(holdBags, 0, 0, false));
        testData.setData(SERVICE, commitBookingHelper.getCommitBookingService());
    }

    @When("^I attempt to commit the booking for (.*) and (\\d+) sports equipment$")
    public void iAttemptToCommitTheBookingForAdultAndSportEquipment(String passengerMix, int sportEquipments) throws Throwable {
        testData.setPassengerMix(passengerMix);
        testData.setSportEquipCount(sportEquipments);
        commitBookingHelper.commitTheBooking(commitBookingHelper.createNewBookingForPublicChannelWithProducts(0, 0, sportEquipments, false));
        testData.setData(SERVICE, commitBookingHelper.getCommitBookingService());
    }

    @And("^creating booking for (.*)$")
    public void iCreatingBookingFor(String pax) throws Throwable {
        testData.setPassengerMix(pax);
    }

    @When("^I create a commit booking request with (.*) hold bag, (.*) excess weight, (.*) sport equipment (with|without) seats$")
    public void iCreateACommitBookingRequestWithHoldBagExcessWeightSportEquipment(String holdBags, String excessWeights, String sportEquipments, String withSeats) throws Throwable {
        testData.setSportEquipCount(Integer.parseInt(sportEquipments));
        testData.setHoldBagCount(Integer.parseInt(holdBags));

        commitBookingRequest = commitBookingHelper.createNewBookingForPublicChannelWithProducts(
                Integer.parseInt(holdBags),
                Integer.parseInt(excessWeights),
                Integer.parseInt(sportEquipments),
                (withSeats.equalsIgnoreCase("with")) // true if it is, false otherwise.
        );

        testData.setData(SERVICE, commitBookingHelper.getCommitBookingService());
    }

    @When("^passenger \"([^\"]*)\" requests to update their \"([^\"]*)\" to \"([^\"]*)\"$")
    public void aPassengerHasTheirXUpdatedToY(int passengerIndex, String field, String value) throws Throwable {
        List<Basket.Passenger> passengers = getOutBoundPassengers();
        // I construct a request body by attempting to call the builder methods from the fields provided in the examples
        // table.. better than a switch I think.
        try {
            AmendBasicDetailsRequestBody.AmendBasicDetailsRequestBodyBuilder builder =
                    (AmendBasicDetailsRequestBody.AmendBasicDetailsRequestBodyBuilder) MethodUtils.invokeExactMethod(
                            AmendBasicDetailsRequestBody.builder(),
                            field,
                            value
                    );

            invokeAmendBasicDetails(passengers.get(passengerIndex - 1).getCode(), builder.build());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("^the amend passenger request should fail with the error codes \"([^\"]*)\"$")
    public void theAmendPassengerRequestShouldFailWithTheErrorCodes(String codes) throws Throwable {
        amendBasicDetailsService.assertThatErrors().containedTheCorrectErrorMessage(codes.split(", "));
    }

    @Then("^the basket should be updated to include the following information for passenger \"([^\"]*)\":$")
    public void theBasketShouldBeUpdatedToIncludeTheFollowingPassengerInformation(int passengerIndex, DataTable expectedValuesTable) throws Throwable {
        Map<String, String> expectedValues = expectedValuesTable.asMap(String.class, String.class);

        pollingLoop().untilAsserted(() -> {
            final Basket basket = basketHelper.getBasket(ordRefId, testData.getChannel());

            expectedValues.forEach(
                    (key, value) -> amendBasicDetailsService.assertThat().passengerDetailsAreUpdatedWith(
                            basket.getOutbounds(),
                            passengerIndex - 1,
                            key,
                            value
                    )
            );
        });
    }

    @When("^a passenger attempts to delete all required contact information for passenger \"([^\"]*)\"$")
    public void aPassengerAttemptsToDeleteAllContactInformationForAllPassengers(int passengerIndex) throws Throwable {
        aPassengerHasTheirXUpdatedToY(passengerIndex, "email", "");
        aPassengerHasTheirXUpdatedToY(passengerIndex, "phoneNumber", "");
    }

    @When("^passenger \"([^\"]*)\" (adds|updates) SSRs with the following:$")
    public void passengerAddsSSRsWithTheFollowing(int passengerIndex, String addOrUpdate, List<AmendPassengerSSRRequestBody.SSRRequestBody> ssrs) {
        List<Basket.Passenger> passengers = basket.getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream())
                .collect(Collectors.toList());

        // Basket params builder.
        BasketPathParams.BasketPathParamsBuilder basketPathParamsBuilder = BasketPathParams.builder()
                .basketId(ordRefId)
                .passengerId(passengers.get(passengerIndex - 1).getCode());

        passenger = passengers.get(passengerIndex - 1);

        // Determine which operation we're performing.
        switch (addOrUpdate) {
            case "adds":
                basketPathParamsBuilder.path(BasketPathParams.BasketPaths.ADD_PASSENGER_SSR);
                break;
            case "updates":
                basketPathParamsBuilder.path(BasketPathParams.BasketPaths.UPDATE_PASSENGER_SSR);
                break;
            default:
                throw new RuntimeException("Unable to proceed without addOrUpdate specified.");
        }

        // Build a request.
        AmendPassengerSSRRequestBody amendPassengerSSRRequestBody = AmendPassengerSSRRequestBody.builder()
                .applyToAllFutureFlights(true)
                .ssrs(ssrs)
                .build();

        // Send the built request.
        amendPassengerSSRService = serviceFactory.amendPassengerSsr(
                new AmendPassengerSSRRequest(
                        HybrisHeaders.getValid(testData.getChannel()).build(),
                        basketPathParamsBuilder.build(),
                        amendPassengerSSRRequestBody
                )
        );

        testData.setData(SERVICE, amendPassengerSSRService);

        amendPassengerSSRService.invoke();
    }

    @When("^the selected passenger deletes their \"([^\"]*)\" SSR$")
    public void passengerDeletesTheirSSR(String code) {
        // Basket params builder.
        BasketPathParams.BasketPathParamsBuilder basketPathParamsBuilder = BasketPathParams.builder()
                .basketId(ordRefId)
                .passengerId(passenger.getCode())
                .path(BasketPathParams.BasketPaths.REMOVE_PASSENGER_SSR);

        // Build a request.
        DeletePassengerSSRRequestBody deletePassengerSSRRequestBody = DeletePassengerSSRRequestBody.builder()
                .removeFromAllFutureFlights(true)
                .ssrCodes(Arrays.asList(code))
                .build();

        // Send the built request.
        amendPassengerSSRService = serviceFactory.amendPassengerSsr(
                new AmendPassengerSSRRequest(
                        HybrisHeaders.getValid(testData.getChannel()).build(),
                        basketPathParamsBuilder.build(),
                        deletePassengerSSRRequestBody
                )
        );

        amendPassengerSSRService.invoke();
    }

    @Then("^an SSR with the code \"([^\"]*)\" should be associated to selected passenger on all flights$")
    public void anSSRWithTheCodeShouldBeAssociatedToPassengerOnAllFlights(String code) {
        pollingLoop().untilAsserted(() -> {
            final Basket basket = basketHelper.getBasket(ordRefId, testData.getChannel());

            amendPassengerSSRService.assertThat().passengerHasSSRWithCode(passenger, code, basket);
        });
    }

    @Then("^the selected passenger should have no SSRs in the basket$")
    public void passengerShouldHaveNoSSRsInTheBasket() {
        pollingLoop().untilAsserted(() -> {
            final Basket basket = basketHelper.getBasket(ordRefId, testData.getChannel());

            amendPassengerSSRService.assertThat().passengerDoesNotHaveSsrs(passenger, basket);
        });
    }

    @And("^I do a commit booking with (.*) for ([^\"]*) with (.*) APIS using ([^\"]*)$")
    public void iDoACommitBookingWithForWithAPISUsingWithAnd(int numberOfFlight, String passenger, boolean withAPIS, String fare) throws Throwable {
        testData.setUpdatePassengerWithApis(withAPIS);

        if (numberOfFlight > 1) {
            basketHelper.addNumberOfFlightsToBasket(numberOfFlight, testData.getChannel());
        } else {
            basketHelper.addFlightToBasket(passenger, testData.getOrigin(), testData.getDestination(), false, fare, null);
        }

        commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService()
                .getResponse(), testData.getChannel());
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);

        final int[] attempts = {3};
        pollingLoopForSearchBooking().until(() -> {
            commitBookingService.invoke();
            attempts[0]--;
            return commitBookingService.getStatusCode() == 200 || attempts[0] == 0;
        });
        commitBookingService.getResponse();

        bookingConfirmationResponse = commitBookingService.getResponse();
        testData.setData(BOOKING_ID, bookingConfirmationResponse.getBookingConfirmation().getBookingReference());
        getBookingContext();
    }

    @And("^commit the booking with hold items$")
    public void commitTheBookingWithHoldItems() throws Throwable {
        commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(testData.getBasketId(), testData.getChannel());
        commitTheBooking();
        try {
            pollingLoop().untilAsserted(() ->
                    commitBookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getBookingStatus().equals("COMPLETED")
            );
            testData.setBookingResponse(commitBookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getChannel()));
        } catch (org.awaitility.core.ConditionTimeoutException e) {
            fail("BOOKING STATUS IS NOT COMPLETED !!!");
        }
    }

    @When("^passenger (.*) on (.*) flight requests to update following details:$")
    public void passengerOnOutboundFlightRequestsToUpdateTheirName(int passengerIndex, String journey, Map<String, String> updatedValuesTable) throws Throwable {
        updatedFields = updatedValuesTable;
        List<Basket.Passenger> passengers;
        switch (journey) {
            case OUTBOUND:
                passengers = getOutBoundPassengers();
                break;
            case INBOUND:
                passengers = getInboundPassengers();
                break;
            default:
                passengers = getOutBoundPassengers();
                break;
        }
        passenger = passengers.get(passengerIndex - 1);
        name = com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name.builder().build();
        updateName = amendBasicDetailsHelper.updateName(updatedFields, passenger, name);
        AmendBasicDetailsRequestBody amendBasicDetailsRequest = AmendBasicDetailsRequestBody.builder()
                .name(updateName)
                .build();
        amendBasicDetailsHelper.invokeAmendBasicDetails(ordRefId, passengers.get(passengerIndex - 1).getCode(), amendBasicDetailsRequest);
    }

    @When("^(.*) on (.*) flight requests to update following details:$")
    public void passengerUpdateNameDetails(String passengerType, String journey, DataTable updatedValuesTable) throws Throwable {
        String[] passengerTypes = passengerType.split(" ");
        String[] paxIndex = passengerTypes[0].split("'");
        updatedFields = updatedValuesTable.asMap(String.class, String.class);
        List<Basket.Passenger> passengers;
        this.passengerType = passengerTypes[1];
        switch (journey.trim()) {
            case OUTBOUND:
                passengers = basketHelper.getPassengersBasedOnType(basket.getOutbounds(), this.passengerType);
                break;
            case INBOUND:
                passengers = basketHelper.getPassengersBasedOnType(basket.getInbounds(), this.passengerType);
                break;
            default:
                passengers = basketHelper.getPassengersBasedOnType(basket.getOutbounds(), this.passengerType);
                break;
        }
        passenger = passengers.get(Integer.valueOf(paxIndex[0]) - 1);
        name = com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name.builder().build();
        updateName = amendBasicDetailsHelper.updateName(
                updatedFields, passenger, name
        );
        AmendBasicDetailsRequestBody amendBasicDetailsRequest = AmendBasicDetailsRequestBody.builder()
                .name(updateName)
                .build();
        amendBasicDetailsHelper.invokeAmendBasicDetails(ordRefId, passenger.getCode(), amendBasicDetailsRequest);
    }

    private List<Basket.Passenger> getInboundPassengers() {
        return basket.getInbounds().stream()
                .flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream())
                .collect(Collectors.toList());
    }

    private List<Basket.Passenger> getOutBoundPassengers() {
        return basket.getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream())
                .collect(Collectors.toList());
    }

    @And("^the basket should be updated to include the respective information for all flights (with|without) infant$")
    public void theBasketShouldBeUpdatedToIncludeTheRespectiveInformationForAllFlights(String withInfant) {
        pollingLoop().untilAsserted(
                () -> amendBasicDetailsHelper.getAmendBasicDetailsService().assertThat().passengerNameAreUpdated(
                        basketHelper.getAllPassengerRecordsFromAllFlights(
                                basketHelper.getBasket(ordRefId, testData.getChannel()),
                                passenger.getPassengerMap()
                        ),
                        updateName,
                        updatedFields
                )
        );

        if (withInfant.equals("with")) {
            amendBasicDetailsHelper.getAmendBasicDetailsService()
                    .assertThat()
                    .passengerHasInfantOnLap(
                            basketHelper.getBasket(ordRefId, testData.getChannel())
                                    .getOutbounds());
        }
    }

    @And("^I amend the basket$")
    public void iAmendTheBasket() throws Throwable {
        verifyTheOrderIsInEditMode();
    }

    @Then("^I see all the benefits of the (.*)")
    public void iSeeAllTheBenefitsOfThe(String promotionType) throws Throwable {
        MemberShipModel expectedMemberShipDetails = (MemberShipModel) testData.getData("ejPlusMember");

        Integer noOfcabinItems = (Integer) testData.getData("cabinItems");

        try {
            pollingLoop().untilAsserted(() -> {
                basketHelper.getBasket(amendableBookingService.getResponse().getOperationConfirmation().getBasketCode());
                List<Basket.Passenger> passengers = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(fts -> fts.getFlights().stream()).flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
                amendableBookingService.assertThat().assertTrue(passengers.stream().findFirst().orElse(null).getCabinItems().size() == noOfcabinItems);
            });
        } catch (ConditionTimeoutException e) {
            fail("Basket is not recalculated after removing EJPlus number");
        }

        try {
            pollingLoop().untilAsserted(() -> {
                basketHelper.getBasket(amendableBookingService.getResponse().getOperationConfirmation().getBasketCode());
                List<Basket.Passenger> passengers = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(fts -> fts.getFlights().stream()).flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
                amendableBookingService.assertThat().assertEquals(passengers.stream().findFirst().orElse(null).getPassengerDetails().getName().getLastName(), expectedMemberShipDetails.getLastname());
            });
        } catch (ConditionTimeoutException e) {
            fail("Passenger lastname is not updated");
        }
    }


    @And("^I commit a booking with for (.*) and with using (.*)")
    public void iCommitABookingWithForAndWithUsing(String passenger, String fareType) throws Throwable {
        basketHelper.addFlightToBasket(passenger, testData.getOrigin(), testData.getDestination(), false, fareType, null);
        bookingConfirmationResponse = commitBookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, false);
        testData.setData(BOOKING_ID, bookingConfirmationResponse.getBookingConfirmation().getBookingReference());
        getBookingContext();
        List<AbstractPassenger.CabinItem> cabinItems = basketHelper.getBasketService().getResponse().getBasket().getOutbounds()
                .stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null)
                .getPassengers().stream().findFirst().orElse(null).getCabinItems();
        testData.setData("cabinItems", cabinItems.size());
    }

    @When("^I update with ejPlus (.*)")
    public void iUpdateWithNumber(String memberType) throws Throwable {
        List<Basket.Passenger> passengers = basket.getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream())
                .collect(Collectors.toList());
        manageBookingHelper.amendReqBodyWithEJPlusNumber(passengers.stream().findFirst().orElse(null), memberType);

    }

    @And("^I commit a booking with (.*) and has ejPlus (.*) member with (.*)")
    public void iCommitABookingWithAndHasMemberWith(String passenger, String customerType, String fare) throws Throwable {
        basketHelper.addFlightToBasket(passenger, testData.getOrigin(), testData.getDestination(), false, fare, null);
        addEJPlusSeatToBasketHelper.updateFirstPassengerWithEJPlus(testData.getChannel(), customerType);
        bookingConfirmationResponse = commitBookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, false);
        testData.setData(BOOKING_ID, bookingConfirmationResponse.getBookingConfirmation().getBookingReference());
        getBookingContext();
        List<AbstractPassenger.CabinItem> cabinItems = basketHelper.getBasketService().getResponse().getBasket().getOutbounds()
                .stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null)
                .getPassengers().stream().findFirst().orElse(null).getCabinItems();
        testData.setData("cabinItems", cabinItems.size());

    }

    @When("^I update with another ejPlus (.*)")
    public void iUpdateWithAnotherEjPlusType(String memberType) throws Throwable {
        List<Basket.Passenger> passengers = basket.getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream())
                .collect(Collectors.toList());
        manageBookingHelper.amendReqBodyWithEJPlusNumber(passengers.stream().findFirst().orElse(null), memberType);

    }

    @When("^I change last name$")
    public void iChangeLastName() throws Throwable {
        List<Basket.Passenger> passengers = basket.getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream())
                .collect(Collectors.toList());
        manageBookingHelper.amendReqBodyWithEJPlusMemberLastNameChange(passengers.stream().findFirst().orElse(null));
    }

    @Then("^I don't see any ejPlus benefits in my basked$")
    public void iDontSeeAnyEjplusBenifitsInMyBasked() throws Throwable {
        MemberShipModel expectedMemberShipDetails = (MemberShipModel) testData.getData("ejPlusMember");
        Integer noOfcabinItems = (Integer) testData.getData("cabinItems");
        try {
            pollingLoop().untilAsserted(() -> {
                basketHelper.getBasket(amendableBookingService.getResponse().getOperationConfirmation().getBasketCode());
                List<Basket.Passenger> passengers = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(fts -> fts.getFlights().stream()).flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
                amendableBookingService.assertThat().assertTrue(passengers.stream().findFirst().orElse(null).getCabinItems().size() < noOfcabinItems);
            });
        } catch (ConditionTimeoutException e) {
            fail("EJPlus number not matched");
        }
        try {
            pollingLoop().untilAsserted(() -> {
                basketHelper.getBasket(amendableBookingService.getResponse().getOperationConfirmation().getBasketCode());
                List<Basket.Passenger> passengers = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(fts -> fts.getFlights().stream()).flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
                amendableBookingService.assertThat().assertNotEquals(passengers.stream().findFirst().orElse(null).getPassengerDetails().getName().getLastName(), expectedMemberShipDetails.getLastname());
            });
        } catch (ConditionTimeoutException e) {
            fail("Last name not matched with EJPlus number");
        }
    }

    @And("^I commit a booking with my (.*), (.*) for (.*) using (.*) amended basket")
    public void iCommitABookingWithSpecificName(String firstName, String lastName, String passenger, String fare) throws Throwable {

        basketHelper.addFlightToBasket(passenger, testData.getOrigin(), testData.getDestination(), false, fare, null);

        commitBookingRequest = commitBookingHelper.createBookingRequestWithNameField(basketHelper.getBasketService().getResponse(),
                testData.getChannel(), firstName, lastName);
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);

        final int[] attempts = {3};
        pollingLoopForSearchBooking().until(() -> {
            commitBookingService.invoke();
            attempts[0]--;
            return commitBookingService.getStatusCode() == 200 || attempts[0] == 0;
        });
        commitBookingService.getResponse();

        bookingConfirmationResponse = commitBookingService.getResponse();
        String bookingReference = bookingConfirmationResponse.getBookingConfirmation().getBookingReference();
        testData.setData(BOOKING_ID, bookingReference);
        getBookingContext();
        testData.setAmendableBasket(basketHelper.createAmendableBasket(bookingReference));
        testData.setPassengerId(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
    }

    @When("^I update my (.*) to (.*)")
    public void iUpdateMyNameFieldToChangedNameFieldValue(String firstname, String lastname) throws Throwable {
        amendBasicDetailsHelper.invokeAmendBasicDetails(firstname, lastname);

    }

    @Then("^I see basket price (increased|nochange)$")
    public void iSeeBasketPriceIncreased(String priceStatus) throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Double beforeUpdateNameTotalAmountWithDebitCard = basketService.getResponse().getBasket().getTotalAmountWithDebitCard();

        pollingLoop().ignoreExceptions().untilAsserted(() -> {
            amendBasicDetailsHelper.getAmendBasicDetailsService().getResponse();
            basketHelper.getBasket(testData.getAmendableBasket());
            Double afterUpdateNameTotalAmountWithDebitCard = basketHelper.getBasketService().getResponse().getBasket().getTotalAmountWithDebitCard();
            AmendBasicDetailsService amendBasicDetailsService = (AmendBasicDetailsService) testData.getData("amendBasicDetailsService");
            if (priceStatus.equals("increased")) {
                amendBasicDetailsService.assertThat().assertTrue(beforeUpdateNameTotalAmountWithDebitCard < afterUpdateNameTotalAmountWithDebitCard);
            } else {
                amendBasicDetailsService.assertThat().assertTrue(beforeUpdateNameTotalAmountWithDebitCard.equals(afterUpdateNameTotalAmountWithDebitCard));
            }
        });
    }

    @When("^I commit booking with (.*) additional seat with (.*) and (.*) and (.*)$")
    public void icommitBookingWithAdditionalSeatWithPassengerMixAndFareTypeAndSeat(Integer additionalSeat, String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addSeatWithAdditionalSeat(passengerMix, fareType, aSeatProduct, additionalSeat);
        bookingConfirmationResponse = commitBookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, false);
        commitBookingService = testData.getData(SERVICE);
        commitBookingService.assertThat().bookingStatusAs(BookingConfirmationAssertion.BOOKING_STATUS.COMPLETED);
        verifyTheOrderIsInEditMode();
    }

    @When("^I send an updatePassenger with (.*), (.*) with (.*) and (.*), (.*) with (.*)$")
    public void iSendAnUpdatePassengerBookingWithPassengerAndFareType(String parameter, String booking, String passengerMix, String fareType, String login, String invalidParameter) throws Throwable {
        String savePassengerId;

        accountPasswordHelper.createNewAccountForCustomerAndLoginIt();

        if (login.equals("no Login")) {
            HybrisService.theJSessionCookie.set("");
        }
        passengerHelper.aValidRequestToCreateACompleteSavedPassenger();
        passengerHelper.addCompleteValidPassengerToExistingCustomer(testData.getData(CUSTOMER_ID));
        testData.setPassengerMix(passengerMix);
        FlightsService flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        testData.setFlightKey(flightsService.getOutboundFlight().getFlightKey());
        basketHelper.addFlightsToBasket(fareType, OUTBOUND);
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
        testData.setBasketId(basketHelper.getBasketService().getResponse().getBasket().getCode());

        savePassengerId = (String) testData.getData("SavedPassengerId");

        if (invalidParameter.equals("invalid SavedPassengerId")) {
            savePassengerId = "INVALID_SAVED_PASSENGER_ID";
        }

        createRequestBodyForUpdatePassenger(savePassengerId, parameter);
        testData.setData(SerenityFacade.DataKeys.BASKET_ID, testData.getBasketId());
        testData.setData(SerenityFacade.DataKeys.BOOKING_TYPE, testData.getBookingType());
        testData.setData(SerenityFacade.DataKeys.CUSTOMER_ID, testData.getData(CUSTOMER_ID));
        testData.setData(SerenityFacade.DataKeys.CHANNEL, testData.getChannel());

        if (booking.equals("with Booking")) {
            getPaymentMethodsForChannelSteps.getMockedCardPaymentMethod();
            // TODO check problem for commit booking "mismatch payment amount" with credit card
//        getPaymentMethodsForChannelSteps.getValidCardPaymentMethod();

            commitBookingSteps.sentCommitBookingRequest();

            getBookingSteps.sendGetBookingRequest();
            if (testData.keyExist(SerenityFacade.DataKeys.GET_BOOKING_RESPONSE)) {
                testData.setBookingResponse(testData.getData(SerenityFacade.DataKeys.GET_BOOKING_RESPONSE));
            }
        }
    }

    private void createRequestBodyForUpdatePassenger(String savedPassengerId, String parameter) {
        updatePassengersRequestBody = travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse());
        updatePassengersRequestBody.getPassengers().get(0).setPassengerAPIS(null);
        AddUpdateSavedPassengerRequestBody savedPassengerRequest = (AddUpdateSavedPassengerRequestBody) testData.getData("UpdateSavedPassengerRequest");
        updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getName().setFirstName(savedPassengerRequest.getFirstName());
        updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getName().setLastName(savedPassengerRequest.getLastName());
        updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getName().setTitle(savedPassengerRequest.getTitle());
        updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().setPhoneNumber(savedPassengerRequest.getPhoneNumber());
        updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().setEmail(savedPassengerRequest.getEmail());
        updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().setNifNumber(savedPassengerRequest.getNifNumber());
        updatePassengersRequestBody.getPassengers().get(0).setAge(savedPassengerRequest.getAge());
        updatePassengersRequestBody.getPassengers().get(0).setSaveToCustomerProfile(true);
        updatePassengersRequestBody.getPassengers().get(0).setUpdateSavedPassengerCode(savedPassengerId);

        switch (parameter) {
            case "title":
                updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getName().setTitle("mrs");
                break;
            case "firstName":
                updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getName().setFirstName("Mara");
                break;
            case "age":
                updatePassengersRequestBody.getPassengers().get(0).setAge(45);
                break;
            case "ejPlusCardNumber":
                String eJPlusNumber = (String) testData.getData("ejPlusCardNumber");
                updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().setEjPlusCardNumber(eJPlusNumber);
                break;
            case "email":
                updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().setEmail("Marafhisvi@reply.com");
                break;
            case "phoneNumber":
                updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().setPhoneNumber("44292298781");
                break;
            case "nifNumber":
                updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().setNifNumber("520575312");
                break;
            case "ssr":

                List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
                SavedSSRs.Ssr ssrToAdd = new SavedSSRs.Ssr();
                ssrToAdd.setCode("WCHC");
                ssrToAdd.setIsTandCsAccepted(true);
                mySsrList.add(ssrToAdd);
                SpecialRequest addSSr = SpecialRequest.builder().build();
                addSSr.setSsrs(mySsrList);

                updatePassengersRequestBody.getPassengers().get(0).setSpecialRequests(addSSr);
                break;
        }

        basketHelper.updatePassengersForChannel(updatePassengersRequestBody, testData.getChannel(), basketHelper.getBasketService().getResponse()
                .getBasket()
                .getCode());

    }

    @Then("^I will receive an (SVC_\\d+_\\d+) message$")
    public void iWillReceiveAnWarningMessage(String warning) throws Throwable {
        travellerService = basketHelper.getBasketPassengerService();
        travellerService.assertThat().verifyWarningMessage(warning, true);
    }

    @Then("^I update the old passenger in the customer profile with (.*)$")
    public void iUpdateTheOldPassengerInTheCustomerProfile(String parameter) throws Throwable {
        GetBookingResponse getBooking = testData.getBookingResponse();
        customerProfileService = commitBookingHelper.associateCustomerProfile(testData.getChannel(), getBooking.getBookingContext().getBooking().getBookingContact().getCustomerId());
        CustomerProfileResponse customerProfile = customerProfileService.getResponse();
        getBookingService = testData.getData(SerenityFacade.DataKeys.GET_BOOKING_SERVICE);
        getBookingService.assertThat().checkCustomerProfileNotIsUpdated(customerProfile, getBooking, parameter, updatePassengersRequestBody);
    }

    @Then("^I update the new passenger in the customer profile with (.*)$")
    public void iUpdateTheNewPassengerInTheCustomerProfileWithParameter(String parameter) throws Throwable {
        GetBookingResponse getBooking = testData.getBookingResponse();

        customerProfileService = commitBookingHelper.associateCustomerProfile(testData.getChannel(), getBooking.getBookingContext().getBooking().getBookingContact().getCustomerId());
        customerProfileService = commitBookingHelper.associateCustomerProfile(testData.getChannel(), getBooking.getBookingContext().getBooking().getBookingContact().getCustomerId());

        CustomerProfileResponse customerProfile = customerProfileService.getResponse();
        getBookingService = testData.getData(SerenityFacade.DataKeys.GET_BOOKING_SERVICE);
        getBookingService.assertThat().checkNewCustomerProfileIsUpdated(customerProfile, getBooking, parameter, updatePassengersRequestBody);
    }

    @Then("^the booking flights should be linked together using the linkedFlights attribute$")
    public void theBookingFlightsShouldBeLinkedTogetherUsingTheLinkedFlightsAttribute() {
        GetBookingResponse.Booking booking = getBookingContext().getBookingContext().getBooking();

        assertThat(booking.getOutbounds()).isNotEmpty();
        assertThat(booking.getInbounds()).isNotEmpty();

        booking.getInbounds().stream()
                .flatMap(inboundFlights -> inboundFlights.getFlights().stream())
                .forEach(inboundFlight -> assertThat(
                        booking.getOutbounds().stream()
                                .flatMap(outboundFlights -> outboundFlights.getFlights().stream())
                                .anyMatch(outboundFlight -> outboundFlight.getLinkedFlights().contains(
                                        inboundFlight.getFlightKey()
                                ))
                        ).isTrue()
                );
    }

    @When("^complete booking with \"([^\"]*)\" via \"([^\"]*)\"$")
    public void completeBookingWithVia(String criteria, String channel) throws Throwable {
        testData.setChannel(channel);
        commitBookingHelper.createNewBookingPublicApiB2BChannel(criteria);
        testData.setData(SerenityFacade.DataKeys.COMMIT_BOOKING_SERVICE,commitBookingHelper.getCommitBookingService() );
    }

    @And("^I have an amendable basket with (.*) fare for (.*) where first adult associated a emergency exit seat and the second adult associated the infant$")
    public void iCreatedAnAmendableBasketWithFareTypeFareForPassengerWithOneAdultEmergencySeat(String fraeType, String passengerMix) throws Throwable {
        testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), STANDARD, false);
        purchasedSeatHelper.addPurchasedSeatAndOneEmergencySeatForFirstAdultPassengers();
        BookingConfirmationResponse bookingConfirmationResponse = bookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, false);
        testData.setData(SerenityFacade.DataKeys.BOOKING_ID, bookingConfirmationResponse.getBookingConfirmation().getBookingReference());
        createBasketSteps.sendCreateAmendableBasketRequest();
        GetAmendableBookingService getAmendableBookingService = testData.getData(SERVICE);
        testData.setData(SerenityFacade.DataKeys.BASKET_ID, getAmendableBookingService.getResponse().getOperationConfirmation().getBasketCode());
    }

    @And("^the amendable basket with (.*) exist$")
    public void iHaveCreatedAnAmendableBasketWithPassengerMix(String passengers) throws Throwable {
        String amendable = bookingHelper.createBookingAndGetAmendable(passengers, STANDARD, true);
        testData.setData(SerenityFacade.DataKeys.BASKET_ID, amendable);
    }

    @And("^the credit card fee should be same as sum of payment balance response fee amount$")
    public void theCreditCardFeeShouldBeSameAsSumOfPaymentBalanceResponseFeeAmount() throws Throwable {
        BookingPathParams params = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).build();
        getBookingService = serviceFactory.getBookings(new GetBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params));
        getBookingService.invoke();

        Optional<AugmentedPriceItem> creditFee = getBookingService.getResponse().getBookingContext().getBooking().getPriceSummary().getFees().getItems().stream().filter((item -> item.getCode().equalsIgnoreCase("CRCardFee"))).findFirst();

        assertThat(creditFee.get().getAmount().equals(testData.getData(SerenityFacade.DataKeys.CREDIT_CARD_FEE))).withFailMessage("Credit card fee in booking " + creditFee.get().getAmount() + " is not same as that of calculate payment balance service " + testData.getData(SerenityFacade.DataKeys.CREDIT_CARD_FEE)).isTrue();
    }

    @When("^complete booking with \"([^\"]*)\" via \"([^\"]*)\" for passengers \"([^\"]*)\"$")
    public void completeBookingWithViaForPassengers(String criteria, String channel, String passengerMix) throws Throwable {
        testData.setChannel(channel);
        testData.setPassengerMix(passengerMix);
        commitBookingHelper.createNewBookingPublicApiB2BChannel(criteria);
        testData.setData(SerenityFacade.DataKeys.COMMIT_BOOKING_SERVICE,commitBookingHelper.getCommitBookingService() );
    }

    @And("^I have made a booking with passenger (.*) and fare (.*) with additional seat (\\d+)$")
    public void iHaveMadeABookingWithPassengerPassengerMixAndFareFareTypeWithAdditionalSeatAddlSeat(String passengerMix, String fareType, int addlSeat) throws Throwable {
        bookingHelper.createBookingAndGetAmendable(passengerMix, fareType, true);
    }

    @And("^I have made a booking with passenger (.*) and fare (.*) with purchased seat for (\\d+) flights$")
    public void iHaveMadeABookingWithPassengerPassengerMixAndFareFareTypeWithPurchasedSeatAndAdditionalSeatAddlSeat(String passengerMix, String fareType, int numFlights) throws Throwable {
        bookingHelper.createBookingWithMultipleFlightAndGetAmendable(passengerMix, fareType, numFlights);
    }

    @And("^I have made a booking with passenger (.*) and fare (.*) without seat$")
    public void iHaveMadeABookingWithPassengerPassengerMixAndFareFareType(String passengerMix, String fareType) throws Throwable {
        bookingHelper.createBookingAndGetAmendable(passengerMix, fareType, true);
    }

    @And("^I have made a booking with passenger (.*), fare (.*) and purchased seat$")
    public void iHaveMadeABookingWithPassengerPassengerMixFareFareTypeAndPurchasedSeat(String passengerMix, String fareType) throws Throwable {
        bookingHelper.createBookingWithMultipleFlightAndGetAmendable(passengerMix, fareType, 1);
    }

    @And("^I recommit the booking$")
    public void iRecommitTheBooking() throws Throwable {
        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        Double amountWithDebitCard = basketService.getResponse().getBasket().getTotalAmountWithDebitCard();
        bookingHelper.createAnotherBookingRequestForAmendableBasket(basketService.getResponse(), amountWithDebitCard, false);
    }

    @Then("^I see commit is successful$")
    public void iSeeCommitIsSuccessful() throws Throwable {
        CommitBookingService commitBookingService = testData.getData(SERVICE);
        String bookingRef = commitBookingService.getResponse().getBookingConfirmation().getBookingReference();
        String bookingStatus = commitBookingService.getResponse().getBookingConfirmation().getBookingStatus();
        if (bookingRef == null) fail("Booking failed with credit file fund payment");
        testData.setData(BOOKING_ID, bookingRef);
        if (!(bookingStatus.equals("COMPLETED") || bookingStatus.equals("CONSIGNMENT_CREATED")|| bookingStatus.equalsIgnoreCase("BOOKING_COMMITTED")))
            fail("Booking failed with credit file fund payment");
    }

    @And("^I see history added for the booking$")
    public void iSeeHistoryAddedForTheBooking() throws Throwable {
        try {
            pollingLoop().untilAsserted(() -> {
                Integer orderHistoryCount = amendCommitBookingDao.getOrderHistoryCount(testData.getData(BOOKING_ID));
                assertThat(orderHistoryCount >= 5);
                assertThat(amendCommitBookingDao.getOrderCount(testData.getData(BOOKING_ID)) >= 2);
            });
        }catch (ConditionTimeoutException cte){
            fail("Booking history ");
        }
    }

    @And("^the booking is amendable$")
    public void theBookingIsAmendable() throws Throwable {
        verifyTheOrderIsInEditMode();
    }

    @When("^I do the commit booking after change$")
    public void iDoTheCommitBookingAfterChange() throws Throwable {
        commitBookingSteps.sendCommitBookingRequest();
    }


    @When("^I commit a booking with (.*) fare and (.*) passenger$")
    public void iCommitABookingWithFareTypeFareAndPassengerPassenger(String fareType, String passengerMix) throws Throwable {
        bookingHelper.createBookingWithPurchasedSeatAndGetAmendable(passengerMix, fareType, true, false, null, false);
    }

    @When("^I commit a booking with (.*) fare and (.*) passenger without purchased seat$")
    public void iCommitABookingWithFareTypeFareAndPassengerPassengerWithoutPurchasedSeat(String fareType, String passengerMix) throws Throwable {
        testData.setData(FARE_TYPE, fareType);
        bookingHelper.commitBooking(passengerMix, fareType, false, 1, false, null, false);
    }

    @When("^I commit a booking with (.*) fare and (.*) passenger without purchased seat for (\\d+) flights$")
    public void iCommitABookingWithFareTypeFareAndPassengerPassengerWithoutPurchasedSeatForNumberOfFlights(String fareType, String passengerMix, int numberOfFlights) throws Throwable {
        bookingHelper.commitBooking(passengerMix, fareType, false, numberOfFlights, false, null, false);
    }

    @When("^I commit a booking with (.*) fare and (.*) passenger with purchased seat (.*)$")
    public void iCommitABookingWithFareTypeFareAndPassengerPassengerWithoutPurchasedSeat(String fareType, String passengerMix, PurchasedSeatHelper.SEATPRODUCTS seatproducts) throws Throwable {
        bookingHelper.commitBooking(passengerMix, fareType, false, 1, true, seatproducts, false);
    }

    @And("^I recommit the booking with (.*)$")
    public void iRecommitTheBookingWithXposId(String xposId) throws Throwable {
        testData.setData(TRANSACTION_ID,xposId);
        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        Double amountWithDebitCard = basketService.getResponse().getBasket().getPriceDifference().getAmountWithDebitCard();
        bookingHelper.createAnotherBookingRequestForAmendableBasket(basketService.getResponse(), amountWithDebitCard, true);

    }

    @Then("^I see message (.*) with new flight price$")
    public void iSeeMessageWithNewFlightPrice(String messageCode) throws Throwable {
        CommitBookingService commitBookingService = testData.getData(SERVICE);
        commitBookingService.assertThatErrors().containedTheCorrectErrorMessage(messageCode);
    }

    @When("^I add a product (.*) whose price has not changed$")
    public void iAddAProductProductWhosePriceHasNotChanged(String product) throws Throwable {
        if ("hold bag".contains(product)) {
            basketHoldItemsHelper.buildRequestToAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
            beforeAllocation = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "20kgbag").get(0));
            basketHoldItemsHelper.invokeServiceAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        } else if ("sport equipment".contains(product)) {
            basketHoldItemsHelper.buildRequestToAddSportEquipment(testData.getData(SerenityFacade.DataKeys.CHANNEL));
            beforeAllocation = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "Snowboard").get(0));
            basketHoldItemsHelper.invokeServiceAddSportItems(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        } else if ("excess weight".contains(product)) {
            basketHoldItemsHelper.buildRequestToAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
            beforeAllocation = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "3kgextraweight").get(0));
            basketHoldItemsHelper.invokeServiceAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        }
    }

    @And("^I created an amendable basket for (.*) fare and (.*) passenger$")
    public void iCreatedAnAmendableBasketForFareTypeFareAndPassengerPassenger(String fare, String passengerMix) throws Throwable {
        String amendable = bookingHelper.createBookingAndGetAmendable(passengerMix, fare, true);
        testData.setData(SerenityFacade.DataKeys.BASKET_ID, amendable);
    }

    @Then("^the stock level should be changed for product (.*)$")
    public void theStockLevelShouldBeChangedForProduct(String product) throws Throwable {
        if ("hold bag".contains(product)) {
            afterAllocation = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "20kgbag").get(0));
        } else if ("sport equipment".contains(product)) {
            afterAllocation = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "Snowboard").get(0));
        } else if ("excess weight".contains(product)) {
            afterAllocation = Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "3kgextraweight").get(0));

        }
        assertThat(afterAllocation).isGreaterThan(beforeAllocation).withFailMessage("No change in stock level");
    }

    @When("^I commit booking$")
    public void iCommitBooking() throws Throwable {
        basketHelper.getBasket(testData.getData(SerenityFacade.DataKeys.BASKET_ID));
        bookingConfirmationResponse = bookingHelper.createNewBooking(bookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse(), testData.getData(CUSTOMER_ID), testData.getChannel(), false));
    }

    @And("^I should be able to see booking is successful$")
    public void iShouldBeAbleToSeeBookingIsSuccessful() throws Throwable {
        pollingLoop().untilAsserted(() -> {
            bookingContext = getBookingContext();
            assertThat(bookingContext.getBookingContext().getBooking().getBookingStatus().equalsIgnoreCase("COMPLETED")).withFailMessage("Booking status is not 'COMPLETED").isTrue();
            assertThat(bookingContext.getBookingContext().getBooking().getBookingReference()).withFailMessage("Booking reference is not generated or empty").isNotEmpty();
        });

    }

    //TODO find a reliable way to retrieve this information from the database
    private static MemberShipModel getEJPlusDetails() {
        return MemberShipModel.builder()
                .ejMemberShipNumber("S008888")
                .expiryDate("2058-12-31 00:00:00.000000")
                .firstname("Andrea")
                .lastname("Rossi")
                .status("8796133851227")
                .build();
    }

    //TODO generify this method to work with evry passenger mix; at the moment accept only 1 adult
    @When("^I do the commit booking with eJplus and SSR with (.*) and (.*)$")
    public void iDoTheCommitBookingWithEJplusAndSSR(String fareType,String passengerMix) throws Throwable {
        registerCustomerSteps.sendRegisterCustomerRequest(null);
        testData.setAccessToken(testData.getData(SerenityFacade.DataKeys.CUSTOMER_ACCESS_TOKEN));

        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fareType, false);

        Passengers updatePassengersRequestBody = travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse());

        MemberShipModel membershipDetail = getEJPlusDetails();
        updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getName().setLastName(membershipDetail.getLastname());
        updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().setEjPlusCardNumber(membershipDetail.getEjMemberShipNumber());

        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr ssrToAdd = new SavedSSRs.Ssr();
        ssrToAdd.setCode("WCHC");
        ssrToAdd.setIsTandCsAccepted(false);
        mySsrList.add(ssrToAdd);
        SpecialRequest addSSr = SpecialRequest.builder().build();
        addSSr.setSsrs(mySsrList);

        updatePassengersRequestBody.getPassengers().get(0).setSpecialRequests(addSSr);

        basketHelper.updatePassengersForChannel(updatePassengersRequestBody, testData.getChannel(), basketHelper.getBasketService().getResponse()
                .getBasket()
                .getCode());

        testData.setData(SerenityFacade.DataKeys.BASKET_ID, testData.getBasketId());
        testData.setData(SerenityFacade.DataKeys.BOOKING_TYPE, testData.getBookingType());

        testData.setData(BOOKING_TYPE, "STANDARD_CUSTOMER");
        getPaymentMethodsForChannelSteps.getMockedCardPaymentMethod();
        // TODO check problem for commit booking "mismatch payment amount" with credit card
//        getPaymentMethodsForChannelSteps.getValidCardPaymentMethod();

        commitBookingSteps.sentCommitBookingRequest();
        getBookingSteps.sendGetBookingRequest();
        checkInForFlightSteps.checkin();

    }

    @Then("^The information are stored$")
    public void theInformationAreStored() throws Throwable {
        TimeUnit.MINUTES.sleep(1);
    }


    private void setRequestPathParameter() {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .passengerId(passengerId)
                .path(UPDATE_BASIC_DETAILS);
    }

    private void setRequestQueryParameter(){
        updatePassengerDetailsQueryParams = UpdatePassengerDetailsQueryParams.builder()
                .operationTypeUpdate("UPDATE");
    }

    private void setRequestBody(Map<String, Object> details) {
        updateBasicDetailsRequestBody = UpdateBasicDetailsRequestBody.builder();

        for (Map.Entry<String, Object> entry : details.entrySet()) {
            try {
                updateBasicDetailsRequestBody.getClass().getMethod(entry.getKey(), entry.getValue().getClass()).invoke(updateBasicDetailsRequestBody, entry.getValue());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            }
        }
    }

    private void invokeUpdateBasicDetailsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updateBasicDetailsService = serviceFactory.updateBasicDetails(new UpdateBasicDetailsRequest(headers.build(), basketPathParams.build(), updatePassengerDetailsQueryParams.build(), updateBasicDetailsRequestBody.build()));
        testData.setData(SERVICE, updateBasicDetailsService);
        updateBasicDetailsService.invoke();
    }

    private void sendUpdateBasicDetailsRequest(Map<String, Object> details) {
        setRequestPathParameter();
        setRequestQueryParameter();
        setRequestBody(details);
        invokeUpdateBasicDetailsService();
    }

    @When("^I change the age of an? " + PASSENGER_TYPES + " with " + PASSENGER_TYPES + " age (?:having emergency exit seat (true|false))?$")
    public void iChangeTheAgeOfOriginalPassengerTypeWithPassengerTypeAge(String originalPassengerType, String newPassengerType, boolean emergencyExit) throws EasyjetCompromisedException {
        basketHelper.getBasket(testData.getData(BASKET_ID));
        basket = basketHelper.getBasketService().getResponse().getBasket();

        String passengerType;
        if (originalPassengerType.startsWith("infant")) {
            passengerType = "infant";
        } else {
            passengerType = originalPassengerType;
        }

        Stream<Basket.Passenger> passengersCodes = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals(passengerType));

        if (emergencyExit) {
            passengerId = passengersCodes.filter(pass -> !pass.getCode().equalsIgnoreCase(testData.getData(PASSENGER_ID))).findFirst().orElseThrow(() -> new EasyjetCompromisedException("The adult passenger with emergency exit is incorrect or not present in the basket")).getCode();
        } else {
            if (originalPassengerType.equals("infantOnSeat")) {
                passengersCodes = passengersCodes.filter(passenger -> !passenger.getFareProduct().getType().equalsIgnoreCase("InfantOnLapProduct"));
            } else if (originalPassengerType.equals("infantOnLap")) {
                passengersCodes = passengersCodes.filter(passenger -> passenger.getFareProduct().getType().equalsIgnoreCase("InfantOnLapProduct"));
            }
            passengerId = passengersCodes.findFirst()
                    .orElseThrow(() -> new EasyjetCompromisedException("No " + originalPassengerType + " passenger is present in the basket"))
                    .getCode();
        }

        testData.setData(SerenityFacade.DataKeys.PASSENGER_ID, passengerId);

        if (newPassengerType.equalsIgnoreCase("infantonlap")) {
            newPassengerType = "infant";
        }

        PassengerTypeDbModel hybrisPassenger = passengerTypeDao.getPassengersOfType(newPassengerType);
        int age = new Random().nextInt(Math.min(99, hybrisPassenger.getMaxAge()) - hybrisPassenger.getMinAge()) + hybrisPassenger.getMinAge();

        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("age", age);

        testData.setData(SerenityFacade.DataKeys.HEADERS, HybrisHeaders.getValid(testData.getData(CHANNEL)));

        if (!emergencyExit) {
            pollingLoop().untilAsserted(() -> {
                sendUpdateBasicDetailsRequest(requestBody);
                updateBasicDetailsService.getResponse();
                updateBasicDetailsService.assertThat().additionalInformationContains("SVC_100009_2002");
            });
        } else {
            sendUpdateBasicDetailsRequest(requestBody);
        }
    }

    @Then("^the " + PASSENGER_TYPES + " should be set in the basket$")
    public void theNewPassengerTypeShouldBeSetInTheBasket(String newPassengerType) throws Throwable {
        updateBasicDetailsService.getResponse();
        assertThat(updateBasicDetailsService.getResponse().getAdditionalInformations().stream().anyMatch(info -> info.getCode().equalsIgnoreCase("SVC_100009_2002"))).withFailMessage("Change Age has resulted in a change in Passenger Type has not happened").isEqualTo(true);

        basketHelper.getBasket(testData.getData(BASKET_ID));
        basket = basketHelper.getBasketService().getResponse().getBasket();

        if (newPassengerType.equalsIgnoreCase("infantonlap")) {
            Basket.Passenger infantOnLapPassenger = basket.getOutbounds().stream().flatMap(flights -> flights.getFlights().stream()).flatMap(passengers -> passengers.getPassengers().stream()).filter(passenger1 -> passenger1.getFareProduct().getCode().equalsIgnoreCase(newPassengerType)).findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant on lap doesn't exist"));
            assertThat(infantOnLapPassenger.getFareProduct().getCode().equalsIgnoreCase(newPassengerType)).withFailMessage("The new passenger type " + newPassengerType + " has not been set in the amendable basket").isTrue();
        } else {
            Basket.Passenger passenger = basket.getOutbounds().stream().flatMap(flights -> flights.getFlights().stream()).flatMap(passengers -> passengers.getPassengers().stream()).filter(passengerCode -> passengerCode.getCode().equalsIgnoreCase(testData.getData(SerenityFacade.DataKeys.PASSENGER_ID))).findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger code is either incorrect or doesn't exist"));
            assertThat(passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(newPassengerType)).withFailMessage("The new passenger type " + newPassengerType + " has not been set in the amendable basket").isTrue();
        }
    }

    @And("^the purchased seat for " + PASSENGER_TYPES + " should be removed in the basket$")
    public void thePurchasedSeatForOldPassengerTypeShouldBeRemovedInTheBasket(String newPassengerType) throws Throwable {
        updateBasicDetailsService.getResponse();
        if (newPassengerType.equalsIgnoreCase("infantonlap")) {
            Basket.Passenger infantOnLapPassenger = basket.getOutbounds().stream().flatMap(flights -> flights.getFlights().stream()).flatMap(passengers -> passengers.getPassengers().stream()).filter(passenger -> passenger.getFareProduct().getCode().equalsIgnoreCase(newPassengerType)).findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant on lap doesn't exist"));
            assertThat(Objects.isNull(infantOnLapPassenger.getSeat())).withFailMessage("The new passenger type " + newPassengerType + " seat has not been removed in the amendable basket").isTrue();
        } else {
            passenger = basket.getOutbounds().stream().flatMap(flights -> flights.getFlights().stream()).flatMap(passengers -> passengers.getPassengers().stream()).filter(passenger -> passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(newPassengerType)).findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger doesn't exist"));
            assertThat(Objects.nonNull(passenger.getSeat())).withFailMessage("The new passenger type " + newPassengerType + " seat has been removed in the amendable basket").isTrue();
        }
    }
    @And("^I see updated details in booking$")
    public void iSeeUpdatedDetailsInBooking() throws Throwable {
        String passengerCode = testData.getData(SAVED_PASSENGER_CODE);
        bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
        List<GetBookingResponse.Flights> outbounds = bookingHelper.getGetBookingService().getResponse().getBookingContext().getBooking().getOutbounds();
        Map<String, Object> passengerDetailsMap = testData.getData(SerenityFacade.DataKeys.SESSION);
        bookingHelper.getGetBookingService().assertThat().checkPassengerDetailsUpdated(outbounds,passengerCode, passengerDetailsMap);

    }

    @And("^I see additional information codes in commit booking (.*)")
    public void iSeeAdditionalInformationCodes(List<String> additionalInfoCodes) throws Throwable {
        Map<String,Object> fieldsMap = testData.getData(SerenityFacade.DataKeys.SESSION);
        if(Objects.nonNull(fieldsMap.get("name"))){
            bookingHelper.getCommitBookingService().assertThat().additionalInformationContains(additionalInfoCodes.toArray(new String[additionalInfoCodes.size()]));
        }
    }

    @And("^I see get booking additional information codes (.*)")
    public void iSeeGetBookingAdditionalInformationCodes(List<String> additionalInfoCodes) throws Throwable {
        Map<String,Object> fieldsMap = testData.getData(SerenityFacade.DataKeys.SESSION);
        if(Objects.nonNull(fieldsMap.get("name"))){
            bookingHelper.getGetBookingService().assertThat().additionalInformationContains(additionalInfoCodes.toArray(new String[additionalInfoCodes.size()]));
        }
    }

    @When("^I have receive a valid cancelBooking request containing the (.*)$")
    public void iReceiveCancelBookingRequestWithHoldItems(String product) throws Throwable {
        bookingHelper.createBookingWithHoldItemAndGetAmendable(ONE_ADULT, STANDARD,  1, product);

        if ((product.equals("hold bag"))) {
            testData.setData(STOCK_BEFORE_CHANGE_FLIGHT, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "20kgbag").get(0)));
        } else if (product.equals("sport equipment")) {
            testData.setData(STOCK_BEFORE_CHANGE_FLIGHT, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "Snowboard").get(0)));
        }
        cancelBookingHelper.initiateCancelBooking();
        cancelBookingHelper.cancelBooking();
        cancelBookingHelper.cancelBookingStatusCheck("CANCELLED");
    }

    @Then("^I check the stock level for the flight for the number of requested (.*) has been released.$")
    public void theStockLevelShouldBeDecreasedForProduct(String product) throws Throwable {
        cancelBookingHelper.verifyStockLevelForHoldItems(product, testData.getData(STOCK_BEFORE_CHANGE_FLIGHT));
    }

    @And("^I have an amendable basket with a flight having (.*)$")
    public void iHaveAmendableBasketWithOutHoldItems(String product) throws Throwable {
        String amendableBaksetCode = bookingHelper.createBookingWithHoldItemAndGetAmendable(ONE_ADULT, STANDARD,  1, product);
        testData.setBasketId(amendableBaksetCode);
        testData.setData(BOOKING_ID, amendableBaksetCode);
    }

    @And("^I select the payment method as (.*)$")
    public void iSelectThePaymentMethodAs(String paymentType) throws Throwable {
        commitBookingHelper.setPaymentMethod(paymentType);
    }

    @And("^I see booking version, history and field changed$")
    public void iSeeBookingVersionAndHistory() throws Throwable {
        List<Basket.Passenger> updatePassengers = testData.getData(SerenityFacade.DataKeys.PASSENGER_LIST);
        try {
            pollingLoop().untilAsserted(() -> {

                updatePassengers.forEach(p -> {
                    assertThat(amendCommitBookingDao.getPassengerHistoryCount(testData.getData(BOOKING_ID),p.getCode()) == 1);
                });
            });
        } catch (ConditionTimeoutException cte) {
            fail("Booking history ");
        }
    }

    @And("^history description, field changed for the passenger$")
    public void historyDescriptionFieldChangedForThePassenger() throws Throwable {
        List<Basket.Passenger> updatePassengers = testData.getData(SerenityFacade.DataKeys.PASSENGER_LIST);
        updatePassengers.forEach(p -> {

        });
    }

    @And("^I recommit booking with (.*) with partial refund$")
    public void iRecommitBookingWithPaymentTypeWithPartialRefund(String paymentMethod) throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        Double amountWithDebitCard = basketHelper.getBasketService().getResponse().getBasket().getPriceDifference().getAmountWithDebitCard();
        String originalPaymentMethodContext = commitBookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getPayments().stream().findFirst().orElse(null).getTransactionId();
        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        bookingHelper.commitBookingWithPartialRefund(basketService.getResponse(), Math.abs(amountWithDebitCard), "24_HOUR_CANCELLATION", originalPaymentMethodContext, paymentMethod,false);
    }

    @And("^I recommit booking with  (.*) with incorrect partial refund$")
    public void iRecommitBookingWithPaymentTypeWithIncorrectPartialRefund(String paymentMethod) throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        Double amountWithDebitCard = basketHelper.getBasketService().getResponse().getBasket().getPriceDifference().getAmountWithDebitCard()-100;
        String originalPaymentMethodContext = commitBookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getPayments().stream().findFirst().orElse(null).getTransactionId();

        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        bookingHelper.commitBookingWithPartialRefund(basketService.getResponse(), Math.abs(amountWithDebitCard), "24_HOUR_CANCELLATION", originalPaymentMethodContext, paymentMethod,true);
    }
    @Then("^I see booking error (.*) message is displayed$")
    public void iSeeBookingErrorMessageIsDisplayed(String bookingError) throws Throwable {
        commitBookingService = testData.getData(SERVICE);
        commitBookingService.assertThatErrors().containedTheCorrectErrorMessage(bookingError);
    }
    @Then("^the commit booking should fail with error (.*)")
    public void theCommitBookingShouldFailWithError(String errorCode) throws Throwable {
        CommitBookingService commitBookingService = testData.getData(SERVICE);
        commitBookingService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @When("^I commit a booking with purchased seat and transaction id (.*-.*-.*-.*-.*)$")
    public void iCommitABookingWithPurchasedSeatAndTransactionId(String transactionId) throws Throwable {
        testData.setData(TRANSACTION_ID, transactionId);
        purchasedSeatHelper.addSeatWithoutAdditionalSeat(ONE_ADULT, STANDARD, null);
        // store price for seat
        Basket basket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        testData.setData(SEAT_PRICE, basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).filter(s -> Objects.nonNull(s.getSeat())).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with seat")).getSeat().getPricing().getBasePrice());
        commitBookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, true);
    }

    @And("^the basket should be update with the new price for the seat$")
    public void theBasketShouldBeUpdateWithTheNewPriceForTheSeat() throws Throwable {
        Basket basket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        double actualBasePrice = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).filter(s -> Objects.nonNull(s.getSeat())).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with seat")).getSeat().getPricing().getBasePrice();
        assertThat(actualBasePrice)
                .withFailMessage("Price for seat in the basket has not been updated")
                .isNotEqualTo(testData.getData(SEAT_PRICE));
    }

    @And("^I recommit booking when payment service down with (.*) with (.*)$")
    public void iRecommitBookingWhenPaymentServiceDownWithPaymentTypeWithXposId(String paymentMethod, String xposId) throws Throwable {
        testData.setData(TRANSACTION_ID,xposId);
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        Double amountWithDebitCard = basketHelper.getBasketService().getResponse().getBasket().getPriceDifference().getAmountWithDebitCard();
        String originalPaymentMethodContext = commitBookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getPayments().stream().findFirst().orElse(null).getTransactionId();
        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        bookingHelper.commitBookingWithPartialRefund(basketService.getResponse(), Math.abs(amountWithDebitCard), "24_HOUR_CANCELLATION", originalPaymentMethodContext, paymentMethod,true);
    }

    @And("^I recommit booking when KANA is down (.*) with (.*)$")
    public void iRecommitBookingWhenKANAIsDownXposId(String paymentMethod, String xposId) throws Throwable {
        testData.setData(TRANSACTION_ID,xposId);
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        Double amountWithDebitCard = basketHelper.getBasketService().getResponse().getBasket().getPriceDifference().getAmountWithDebitCard();
        String originalPaymentMethodContext = commitBookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getPayments().stream().findFirst().orElse(null).getTransactionId();
        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        bookingHelper.commitBookingWithPartialRefund(basketService.getResponse(), Math.abs(amountWithDebitCard), "24_HOUR_CANCELLATION", originalPaymentMethodContext, paymentMethod,true);
    }

    @And("^I recommit with incorrect refund (.*)  fund (.*) with partial refund$")
    public void iRecommitWithIncorrectRefundRefundTypeFundFundNameWithPartialRefund(String refundAmount, String paymentMethod) throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        Double amountWithDebitCard;
        if (refundAmount.equalsIgnoreCase("MoreThanOriginalAmt")) {
            amountWithDebitCard = basketHelper.getBasketService().getResponse().getBasket().getTotalAmountWithDebitCard()+200;
        } else if (refundAmount.equalsIgnoreCase("MoreThanRefundAmt")){
            amountWithDebitCard = Math.abs(basketHelper.getBasketService().getResponse().getBasket().getPriceDifference().getAmountWithDebitCard()) + 5;
        }else {
            amountWithDebitCard = Math.abs(basketHelper.getBasketService().getResponse().getBasket().getPriceDifference().getAmountWithDebitCard());
            paymentMethod="card";

        }
        String originalPaymentMethodContext = commitBookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getPayments().stream().findFirst().orElse(null).getTransactionId();
        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        bookingHelper.commitBookingWithPartialRefund(basketService.getResponse(), amountWithDebitCard, "24_HOUR_CANCELLATION", originalPaymentMethodContext, paymentMethod, true);

    }

    @And("^I commit the booking again$")
    public void iCommitTheBookingAgain() throws Throwable {
        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        bookingHelper.commitBookingFromBasket(basketService.getResponse());
    }

    @When("^I commit booking with (.*) fare and (.*) passenger$")
    public void iCommitBookingWithFareTypeFareAndPassengerPassenger(String fareType, String passengerMix ) throws Throwable {
        bookingHelper.createBookingWithPurchasedSeatAndGetAmendable(passengerMix, fareType, false, false, null, false);
    }

    @When("^I add a new flight for (.*) passenger and (.*) fare to the booking$")
    public void iAddANewFlightForPassengerMixPassengerAndFareToTheBooking(String passengerMix, String fareType) throws Throwable {
        testData.setOutboundDate(LocalDate.now().plusDays(10).format(DateTimeFormatter.ofPattern("dd-MM-yyy")));
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fareType, false);
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        bookingConfirmationResponse = bookingHelper.createNewBooking(bookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse(), testData.getData(CUSTOMER_ID), testData.getChannel(), true));
        String bookingRef = bookingConfirmationResponse.getBookingConfirmation().getBookingReference();
        testData.setData(BOOKING_ID, bookingRef);
    }
}
