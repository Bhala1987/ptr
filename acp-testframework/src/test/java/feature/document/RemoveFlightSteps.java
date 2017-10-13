package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.RemoveFlightQueryParam;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveFlightFromBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Currency;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.RemoveFlightFromBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import javafx.util.Pair;
import org.awaitility.core.ConditionTimeoutException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_SERVICE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.constants.CommonConstants.ONE_ADULT;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

/**
 * Created by giuseppedimartino on 11/04/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class RemoveFlightSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private BookingHelper bookingHelper;
    private RemoveFlightFromBasketService removeFlightService;
    private BasketPathParams basketPathParams;
    private RemoveFlightQueryParam removeFlightQueryParam;
    private String basketId;
    private String flightKey;

    @And("^I want to remove the '(outbound|inbound)' flight$")
    public void iWantToRemoveTheInboundFlight(String trip) throws Throwable {
        basketId = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getCode();
        switch (trip) {
            case "inbound":
                flightKey = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getInbounds().get(0).getFlights().get(0).getFlightKey();
                break;
            case "outbound":
                flightKey = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getInbounds().get(0).getFlights().get(0).getFlightKey();
                break;
        }

        basketPathParams = BasketPathParams.builder().basketId(basketId).path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey(flightKey).build();
    }

    @And("^from-search-results is (true|false)$")
    public void fromSearchResultsFalse(String fromSearchResult) throws Throwable {
        removeFlightQueryParam = RemoveFlightQueryParam.builder().fromSearchResults(fromSearchResult).build();
    }

    @When("^I send the request to removeFlight\\(\\)$")
    public void iSendTheRequestToRemoveFlight() throws Throwable {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        if (removeFlightQueryParam != null)
            removeFlightService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(headers.build(), basketPathParams, removeFlightQueryParam));
        else
            removeFlightService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(headers.build(), basketPathParams));
        removeFlightService.invoke();
    }
    @When("^I send the request to remove flight$")
    public void iSendRequestToRemoveFlight() throws Throwable {
        String currentCookie = HybrisService.theJSessionCookie.get();
        HybrisService.theJSessionCookie.set(currentCookie);
        basketPathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey(testData.getData(SerenityFacade.DataKeys.FLIGHT_KEY)).build();
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        HybrisService.theJSessionCookie.set(currentCookie);
        if (removeFlightQueryParam != null)
            removeFlightService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(headers.build(), basketPathParams, removeFlightQueryParam));
        else
            removeFlightService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(headers.build(), basketPathParams));
        removeFlightService.invoke();
    }

    @When("^I send the request to removeFlight from basket$")
    public void iSendTheRequestToRemoveFlightFromBasket() throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basketId = basketService.getResponse().getBasket().getCode();
        basketPathParams = BasketPathParams.builder()
                .basketId(basketId)
                .path(BasketPathParams.BasketPaths.REMOVE_FLIGHT)
                .flightKey(testData.getActualFlightKey())
                .build();

        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        if (removeFlightQueryParam != null)
            removeFlightService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(headers.build(), basketPathParams, removeFlightQueryParam));
        else
            removeFlightService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(headers.build(), basketPathParams));

        removeFlightService.invoke();

    }

    @Then("^I receive a confirmation response for the removeFlight\\(\\)$")
    public void iReceiveAConfirmationResponseForTheRemoveFlight() throws Throwable {
        removeFlightService.assertThat().basketOperationConfirmation(basketId);
    }

    @And("^the '(?:outbound|inbound)' flight has been removed from the basket$")
    public void theInboundFlightHasBeenRemovedFromTheBasket() throws Throwable {

        try {
            pollingLoop().untilAsserted(() -> {
                bookingHelper.getBasketHelper().getBasket(basketId, testData.getChannel());
                bookingHelper.getBasketHelper().getBasketService().assertThat().theBasketDoesNotContainsTheFlight(flightKey);
            });
        } catch (ConditionTimeoutException ignored) {
            fail("The basket still contains the removed flight " + flightKey);
        }
    }

    @Then("^the admin fee will (not )?be apportioned among the passenger of the '(outbound|inbound|next pair of|next)' flight$")
    public void theAdminFeeWillBeApportionedAmongThePassengerOfTheOutboundFlight(String apportioning, String journey) throws Throwable {
        basketId = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getCode();

        Currency currency = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getCurrency();
        BigDecimal adminFee = BigDecimal.ZERO;
        List<FeesAndTaxesModel> adminFees = feesAndTaxesDao.getFees("AdminFee", null, currency.getCode(), "adult");
        if (adminFees.size() > 0) {
            adminFee = new BigDecimal(adminFees.get(0)
                    .getFeeValue()
                    .toString());
        }
        if (!adminFee.equals(BigDecimal.ZERO) && (apportioning != null || journey.equals("next pair of")))
            adminFee = adminFee.multiply(new BigDecimal("0.5"));

        BigDecimal expectedAdminFee = adminFee;

        pollingLoop().untilAsserted(() -> {
            bookingHelper.getBasketHelper().getBasket(basketId, testData.getChannel());
            bookingHelper.getBasketHelper().getBasketService().assertThat().theAdminFeeForEachPassengerIsCorrect(Integer.valueOf(currency.getDecimalPlaces()), expectedAdminFee);
                }
        );
    }

    @When("^I commit booking request for amendable basket after delete additional flight$")
    public void commitBookingForAmendableBasketWithARemovedFlight() throws Throwable {
        testData.setPassengerMix(ONE_ADULT);
        testData.setFareType(STANDARD);
        basketId = bookingHelper.getAmendableBasketWithSavedPassenger(testData.getPassengerMix(), testData.getFareType(), new Pair<>(false, false));
        flightKey = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();
        bookingHelper.getBasketHelper().removeFlightFromBasket(basketId,flightKey);
        bookingHelper.getBasketHelper().getBasket(testData.getBasketId(), testData.getChannel());
        Double amountWithDebitCard = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getPriceDifference().getAmountWithDebitCard();
        String originalPaymentMethodContext = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getPayments().stream().findFirst().orElse(null).getTransactionId();
        bookingHelper.getBasketHelper().getBasket(testData.getBasketId());
        BasketService basketService = bookingHelper.getBasketHelper().getBasketService();
        bookingHelper.commitBookingWithPartialRefund(basketService.getResponse(), Math.abs(amountWithDebitCard), "24_HOUR_CANCELLATION", originalPaymentMethodContext, "card",false);
    }

    @Then("^I want validate successful response after commit booking$")
    public void validatingResponseAfterCommittedBooking() throws Throwable{
        bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
    }

    @And("^I want to check status for flight in the booking is INACTIVE$")
    public void isBookingStatusInactive() throws Throwable{
        bookingHelper.getGetBookingService().assertThat().getFlightActiveStatus();
    }

    @And("^I want to check amend entry status for flight in the booking is CHANGED$")
    public void isBookingStatusChanged() throws Throwable{
        bookingHelper.getGetBookingService().assertThat().getFlightEntryStatus();
    }
}
