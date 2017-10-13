package feature.document.steps.services.managebookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GetAmendableBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetAmendableBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetAmendableBookingService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import feature.document.steps.constants.StepsRegex;
import feature.document.steps.services.checkinservices.CheckInForFlightSteps;
import feature.document.steps.services.createbookingservices.CommitBookingSteps;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.AMENDABLE_BOOKING_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * CreateBasketSteps handle the communication with the createBasket service (aka amendableBasket).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class CreateBasketSteps {

    private static final String SVC_100245_3002 = "SVC_100245_3002"; // "Booking cannot be edited in this status"

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private CommitBookingSteps commitBookingSteps;
    @Steps
    private CheckInForFlightSteps checkInForFlightSteps;

    private GetAmendableBookingService getAmendableBookingService;
    private BookingPathParams.BookingPathParamsBuilder bookingPathParams;
    private GetAmendableBookingRequestBody.GetAmendableBookingRequestBodyBuilder getAmendableBookingRequestBody;

    private void setPathParameter() {
        bookingPathParams = BookingPathParams.builder()
                .bookingId(testData.getData(BOOKING_ID))
                .path(AMENDABLE_BOOKING_REQUEST);
    }

    private void setRequestBody() {
        getAmendableBookingRequestBody = GetAmendableBookingRequestBody.builder()
                .overrideLocking(Boolean.TRUE)
                .lockingLevel("BOOKING");
    }

    private void invokeCreateBasketService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getAmendableBookingService = serviceFactory.getAmendableBooking(new GetAmendableBookingRequest(headers.build(), bookingPathParams.build(), getAmendableBookingRequestBody.build()));
        testData.setData(SERVICE, getAmendableBookingService);
        getAmendableBookingService.invoke();
    }

    @Step("Create amendable basket")
    public void sendCreateAmendableBasketRequest() {
        setPathParameter();
        setRequestBody();
        invokeCreateBasketService();
    }

    @Given("^I created an amendable basket" + StepsRegex.RETURN + StepsRegex.HOLD_ITEMS + StepsRegex.SPORTS_ITEMS + StepsRegex.SECTOR + StepsRegex.FARE_TYPE + StepsRegex.PASSENGER_MIX_APIS + StepsRegex.DATES + StepsRegex.FLEXIBLE_DAYS + StepsRegex.DEAL + StepsRegex.CURRENCY + StepsRegex.CHECK_IN + "$")
    public void createAmendableBasket(String withReturn, String addHoldBag, Integer holdBag, Integer excessWeightQuantity, String excessWeightType, String passengerWithHolddBag, String addsportsItem, Integer sportsItemQty, String passengerWithSportsItem, String origin, String destination, String fareType, String passengerMix, String apisData, String outboundDate, String inboundDate, String flexibleDays, String applicationId, String officeId, String corporateId, String currency, String checkIn) throws EasyjetCompromisedException {
        if (StringUtils.isNotBlank(checkIn)) {
            checkInForFlightSteps.createCheckIn(withReturn, addHoldBag, holdBag, excessWeightQuantity, excessWeightType, passengerWithHolddBag, addsportsItem, sportsItemQty, passengerWithSportsItem, origin, destination, fareType, passengerMix, apisData, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);
        } else {
            commitBookingSteps.createBooking(withReturn, addHoldBag, holdBag, excessWeightQuantity, excessWeightType, passengerWithHolddBag, addsportsItem, sportsItemQty, passengerWithSportsItem, origin, destination, fareType, passengerMix, apisData, outboundDate, inboundDate, flexibleDays, applicationId, officeId, corporateId, currency);
        }
        createdBasket();
    }

    @Given("^I sent the request to createBasket service$")
    public void createdBasket() {
        pollingLoop().untilAsserted(() -> {
                    sendCreateAmendableBasketRequest();
                    if (Objects.isNull(getAmendableBookingService.getErrors())) {
                        assertThat(getAmendableBookingService.getResponse().getOperationConfirmation())
                                .isNotNull();
                        testData.setData(BASKET_ID, getAmendableBookingService.getResponse().getOperationConfirmation().getBasketCode());
                    } else {
                        assertThat(getAmendableBookingService.getErrors().getErrors().stream()
                                .map(Errors.Error::getCode)
                                .collect(Collectors.toList()))
                                .doesNotContain(SVC_100245_3002);
                    }
                }
        );
    }

    @When("^I send the request to createBasket service$")
    public void createBasket() {
        sendCreateAmendableBasketRequest();
    }

}