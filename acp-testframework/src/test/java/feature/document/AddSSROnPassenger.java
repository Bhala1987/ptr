package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendPassengerSSRRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AmendPassengerSSRRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.AmendPassengerSSRService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.ADD_PASSENGER_SSR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 03/08/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddSSROnPassenger {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BookingHelper bookingHelper;
    private AmendPassengerSSRService amendPassengerSSRService;

    @When("^I send a manage passenger (.*) SSRs request on edit booking with purchased seat (.*) rule?")
    public void iSendAddSSRsRequestOnEditBookingWithSeat(String ssrCode, boolean isEmergency) throws Throwable {
        String amendableBasketRef = bookingHelper.createBookingWithPurchasedSeatAndGetAmendable(testData.getPassengerMix(), STANDARD, true, isEmergency, null, false);
        testData.setAmendableBasket(amendableBasketRef);
        testData.setData("SSR_CODE", ssrCode);

        BasketPathParams basketPathParams = BasketPathParams.builder().basketId(amendableBasketRef).passengerId(testData.getPassengerId()).path(ADD_PASSENGER_SSR).build();
        AmendPassengerSSRRequestBody amendPassengerSSRRequestBody = AmendPassengerSSRRequestBody.builder()
                .applyToAllFutureFlights(false)
                .ssrs(Arrays.asList(AmendPassengerSSRRequestBody.SSRRequestBody.builder()
                        .code(ssrCode)
                        .isTandCsAccepted(false)
                        .overrideSectorRestriction(false).build()))
                .build();

        amendPassengerSSRService = serviceFactory.amendPassengerSsr(
                new AmendPassengerSSRRequest(
                        HybrisHeaders.getValid(testData.getChannel()).build(),
                        basketPathParams,
                        amendPassengerSSRRequestBody
                )
        );
        amendPassengerSSRService.invoke();
    }

    @And("^the passenger SSRs (.*) be update$")
    public void thePassengerSSRsShouldBeUpdate(boolean should) throws Throwable {
        Basket basket = bookingHelper.getBasketHelper().getBasket(testData.getAmendableBasket(), testData.getChannel());
        Basket.Passenger passenger = basket.getOutbounds().stream().flatMap(flights -> flights.getFlights().stream()).flatMap(pass -> pass.getPassengers().stream()).filter(p -> p.getCode().equalsIgnoreCase(testData.getPassengerId())).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger in basket with code " + testData.getPassengerId()));
        if(should) {
            if(Objects.nonNull(amendPassengerSSRService.getErrors()) && amendPassengerSSRService.getErrors().getErrors().stream().map(e -> e.getCode()).collect(Collectors.toList()).contains("SVC_100600_1013")) {
                throw new EasyjetCompromisedException("(type: SVC_100600_1013, message: Seat not available (restricted by dynamic rule))");
            } else {
                amendPassengerSSRService.getResponse();
                amendPassengerSSRService.assertThat().passengerHasSSRWithCode(passenger, (String) testData.getData("SSR_CODE"), basket);
            }
        } else {
            assertThat(passenger.getSpecialRequests().getSsrs())
                    .withFailMessage("The passenger contain unexpected SSR")
                    .isEmpty();
        }
    }

    @Then("^I (.*) receive error (.*)$")
    public void iShouldReceiveErrorError(boolean should, String error) throws Throwable {
        if(should) {
            amendPassengerSSRService.assertThatErrors().containedTheCorrectErrorMessage(error);
        }
    }
}
