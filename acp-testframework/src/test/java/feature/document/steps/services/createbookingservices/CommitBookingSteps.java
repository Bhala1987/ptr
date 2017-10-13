package feature.document.steps.services.createbookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.CommitBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.CustomerDeviceContext;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.document.steps.constants.StepsRegex;
import feature.document.steps.helpers.BasketContentHelper;
import feature.document.steps.services.GetFlightsSteps;
import feature.document.steps.services.createbasketservices.AddFlightSteps;
import feature.document.steps.services.createbasketservices.UpdatePassengersSteps;
import feature.document.steps.services.managebookingservices.GetBookingSteps;
import feature.document.steps.services.managecustomerservices.RegisterCustomerSteps;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * CommitBookingSteps handle the communication with the commitBooking service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class CommitBookingSteps {

    private static final String SVC_100022_2057 = "SVC_100022_2057"; // Missing mandatory passenger information(name/title/age)
    private static final String STAFF = "Staff";
    private static final String STANDBY = "Standby";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private RegisterCustomerSteps registerCustomerSteps;
    @Steps
    private GetFlightsSteps getFlightsSteps;
    @Steps
    private AddFlightSteps addFlightSteps;
    @Steps
    private UpdatePassengersSteps updatePassengersSteps;
    @Steps
    private GetPaymentMethodsForChannelSteps getPaymentMethodsForChannelSteps;
    @Steps
    private GetBookingSteps getBookingSteps;

    private CommitBookingService commitBookingService;
    private CommitBookingRequestBody.CommitBookingRequestBodyBuilder commitBookingRequestBody;
    private boolean overrideWarning = true;

    private void setRequestBody() {
        String ipAddress = StringUtils.join(
                Arrays.asList(
                        testData.dataFactory.getNumberBetween(1, 254),
                        testData.dataFactory.getNumberBetween(1, 254),
                        testData.dataFactory.getNumberBetween(1, 254),
                        testData.dataFactory.getNumberBetween(1, 254)
                ), ".");
        commitBookingRequestBody = CommitBookingRequestBody.builder()
                .overrideWarning(overrideWarning)
                .basketCode(testData.getData(BASKET_ID))
                .basketContent(testData.getData(BASKET_CONTENT))
                //TODO check how we should set this
                .bookingReason("LEISURE")
                .bookingType(testData.getData(BOOKING_TYPE))
                .customerDeviceContext(
                        CustomerDeviceContext.builder()
                                .device("iMAC")
                                .ipAddress(ipAddress)
                                .operationalSystem("MAC OS X")
                                .build())
                .paymentMethods(Collections.singletonList(testData.getData(PAYMENT_METHOD)));
    }

    private void invokeCommitBookingService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        commitBookingService = serviceFactory.commitBooking(new CommitBookingRequest(headers.build(), commitBookingRequestBody.build()));
        testData.setData(SERVICE, commitBookingService);
        commitBookingService.invoke();
    }

    public void sendCommitBookingRequest() {
        setRequestBody();
        invokeCommitBookingService();
    }

    @Step("Commit booking")
    public void sentCommitBookingRequest() {
        setRequestBody();

        pollingLoop().untilAsserted(() -> {
                    invokeCommitBookingService();
                    if (Objects.isNull(commitBookingService.getErrors())) {
                        assertThat(commitBookingService.getResponse().getBookingConfirmation())
                                .isNotNull();
                        testData.setData(BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
                    } else {
                        assertThat(commitBookingService.getErrors().getErrors().stream()
                                .map(Errors.Error::getCode)
                                .collect(Collectors.toList()))
                                .doesNotContain(SVC_100022_2057);
                    }
                }
        );
    }

    @Step("Create booking")
    @Given("^I have committed a booking" + StepsRegex.RETURN + StepsRegex.HOLD_ITEMS + StepsRegex.SPORTS_ITEMS + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX_APIS + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + "$")
    public void createBooking(String withReturn, String addHoldBag, Integer holdBag, Integer excessWeightQuantity, String excessWeightType, String passengerWithHolddBag, String addsportsItem, Integer sportsItemQty, String passengerWithSportsItem, String origin, String destination, String fareType, String passengerMix, String apisData, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency) throws EasyjetCompromisedException {
        if (testData.getData(CHANNEL).equals("PublicApiB2B")) {
            getFlightsSteps.searchFlights(withReturn, origin, destination, fareType, passengerMix, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);
            testData.setData(BASKET_CONTENT, new BasketContentHelper().getBasket());
        } else {
            registerCustomerSteps.sendRegisterCustomerRequest(fareType);
            String journeyType = "";
            if (StringUtils.isNotBlank(withReturn)) {
                journeyType = "outbound/inbound";
            }
            addFlightSteps.sendAddFlightRequest(journeyType, addHoldBag, holdBag, excessWeightQuantity, excessWeightType, passengerWithHolddBag, addsportsItem, sportsItemQty, passengerWithSportsItem, origin, destination, fareType, passengerMix, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);

            if (StringUtils.isNotBlank(fareType) && (fareType.equals(STAFF) || fareType.equals(STANDBY))) {
                updatePassengersSteps.sendUpdatePassengerRequestForCustomer();
            } else {
                updatePassengersSteps.sendUpdatePassengerRequest(apisData);
            }
        }
        getPaymentMethodsForChannelSteps.getMockedCardPaymentMethod();
        // TODO check problem for commit booking "mismatch payment amount" with credit card
//        getPaymentMethodsForChannelSteps.getValidCardPaymentMethod();
        sentCommitBookingRequest();
        getBookingSteps.sendGetBookingRequest();
    }

    @When("^I send the request to commitBooking service(?: with override (true|false))?$")
    public void commitBooking(String overrideWarning) throws EasyjetCompromisedException {
        if (testData.getData(CHANNEL).equals("PublicApiB2B")) {
            testData.setData(BASKET_CONTENT, new BasketContentHelper().getBasket());
            getPaymentMethodsForChannelSteps.getMockedCardPaymentMethod();
            // TODO check problem for commit booking "mismatch payment amount" with credit card
//        getPaymentMethodsForChannelSteps.getValidCardPaymentMethod();
        }
        else
        {
            registerCustomerSteps.sendRegisterCustomerRequest(testData.getData(FARE_TYPE));
            getPaymentMethodsForChannelSteps.getMockedCardPaymentMethod();
        }
        if (StringUtils.isNotBlank(overrideWarning)) {
            this.overrideWarning = Boolean.valueOf(overrideWarning);
        }
        sendCommitBookingRequest();
    }

    @When("^I have got payment method and proceed(ed)? to commit the booking?$")
    public void getPaymentMethodAndCommitBooking(String check) throws EasyjetCompromisedException {
        getPaymentMethodsForChannelSteps.getMockedCardPaymentMethod();
        // TODO check problem for commit booking "mismatch payment amount" with credit card
//        getPaymentMethodsForChannelSteps.getValidCardPaymentMethod();
        if (StringUtils.isBlank(check)) {
            sendCommitBookingRequest();
        } else {
            sentCommitBookingRequest();
        }
    }

    @When("^I proceed to commit the booking( without apis data)?$")
    public void iProceedToCommitTheBooking(String apisData) throws EasyjetCompromisedException {
        registerCustomerSteps.sendRegisterCustomerRequest(testData.getData(FARE_TYPE));
        if (testData.getData(FARE_TYPE).equals(STAFF)) {
            updatePassengersSteps.sendUpdatePassengerRequestForCustomer();
        } else {
            updatePassengersSteps.sendUpdatePassengerRequest(apisData);
        }
        getPaymentMethodsForChannelSteps.getMockedCardPaymentMethod();
        // TODO check problem for commit booking "mismatch payment amount" with credit card
//        getPaymentMethodsForChannelSteps.getValidCardPaymentMethod();
        sendCommitBookingRequest();
    }

    @Then("^the booking is completed$")
    public void theBookingIsCompleted() {
        if (Objects.isNull(commitBookingService.getResponse())) {
            pollingLoop().untilAsserted(() -> {
                        invokeCommitBookingService();
                        if (Objects.isNull(commitBookingService.getErrors())) {
                            assertThat(commitBookingService.getResponse().getBookingConfirmation())
                                    .isNotNull();
                            testData.setData(BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
                        } else {
                            assertThat(commitBookingService.getErrors().getErrors().stream()
                                    .map(Errors.Error::getCode)
                                    .collect(Collectors.toList()))
                                    .doesNotContain(SVC_100022_2057);
                        }
                    }
            );
        } else {
            assertThat(commitBookingService.getResponse().getBookingConfirmation())
                    .isNotNull();
            testData.setData(BOOKING_ID, commitBookingService.getResponse().getBookingConfirmation().getBookingReference());
        }
        getBookingSteps.sendGetBookingRequest();
    }

}