package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * Created by giuseppecioce on 20/09/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class MakePaymentAsCommitBooking {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private PaymentServiceResponseHelper paymentServiceResponseHelper;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;

    @Given("^I commit an amendable basket where flight (.*) to (.*) with (card|elv) in (GBP|EUR) and (.*)$")
    public void iAmUsingOneChannel(String origin, String destination, String paymentType, String currency, String cardDetails) throws Throwable {
        // create booking
        testData.setPaymentCode(paymentType);
        testData.setOrigin(origin);
        testData.setDestination(destination);
        testData.setCurrency(currency);
        bookingHelper.createBookingAndGetAmendable(CommonConstants.ONE_ADULT, CommonConstants.STANDARD, false);
        // add hold item in order to change flight price
        basketHoldItemsHelper.buildRequestToAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        basketHoldItemsHelper.invokeServiceAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        basketHoldItemsHelper.getAddHoldBagToBasketService().getResponse();
        // commit amendable basket
        paymentServiceResponseHelper.addValuesInToMap(cardDetails.split("-"), paymentType);
        paymentServiceResponseHelper.buildRequestToCommitBookingWithCardDetails(paymentType);
        paymentServiceResponseHelper.sendCommitBookingRequest();
    }

    @When("^I commit an amendable basket as (STANDARD_CUSTOMER|BUSINESS|IMMIGRATION) type with (.*) for (.*) different (debit|creditfile|combination) with (.*)$")
    public void iCommitAnAmendableBasketWithBookingOption(String bookingType, String typeOfManageBooking, int numberOfPayment, String typeOfCombinationPayment, String paymentDetails) throws Throwable {
        // create booking
        testData.setBookingType(bookingType);
        bookingHelper.createBookingAndGetAmendable(CommonConstants.ONE_ADULT, CommonConstants.STANDARD, false);
        // add hold item in order to change flight price
        basketHoldItemsHelper.buildRequestToAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        basketHoldItemsHelper.invokeServiceAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        basketHoldItemsHelper.getAddHoldBagToBasketService().getResponse();
        // commit amendable basket
        bookingHelper.setPaymentDetails(paymentDetails);
        bookingHelper.setPaymentType(typeOfCombinationPayment);
        testData.setData(WANT_DEAL, false);
        bookingHelper.createBooking(bookingHelper.manageBookingFromBasketResponse(null, typeOfManageBooking, numberOfPayment));
    }

    @Then("^an error (.*) should returned to the channel$")
    public void anErrorShouldReturnedToTheChannel(String errorCode) throws Throwable {
        bookingHelper.getCommitBookingService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Then("^the booking should created$")
    public void theBookingShouldCreated() throws Throwable {
        bookingHelper.getCommitBookingService().getResponse();
    }

    @And("^I have a booking for flight (LTN) to (ALC) with currency (GBP|EUR) fare (Standard|Flexi) and period (present|future)$")
    public void iHaveABookingForFlightToWithCurrencyCurrencyFareFareTypeAndPeriodPeriod(String origin, String destination, String currency, String fareType, String periodForFlight) throws Throwable {
        testData.setPeriod(periodForFlight);
        testData.setCurrency(currency);
        // add flight
        paymentServiceResponseHelper.addFlightToBasket(origin, destination, fareType);
        // commit booking and amendable basket
        bookingHelper.updatePassengerAndLinkCustomer(bookingHelper.getBasketHelper().getBasketResponse(testData.getBasketId(), testData.getChannel()));
        bookingHelper.getAmendableBasket(bookingHelper.createNewBooking(bookingHelper.manageBookingFromBasketResponse(null, "", 1)).getBookingConfirmation().getBookingReference());
    }

    @When("^I commit the amendable basket with (elv|card) and (.*) containing invalid (.*)$")
    public void iCommitTheAmendableBasketWithPaymentTypeAndPaymentDetailsContainingInvalidIncorrectInfo(String paymentType, String paymentDetails, String incorrectInfo) throws Throwable {
        // add hold item in order to change flight price
        basketHoldItemsHelper.buildRequestToAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        basketHoldItemsHelper.invokeServiceAddHoldBags(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        basketHoldItemsHelper.getAddHoldBagToBasketService().getResponse();
        // set details for booking and send request
        testData.setPaymentCode(paymentType);
        paymentServiceResponseHelper.addValuesInToMap(paymentDetails.split("-"), paymentType);
        paymentServiceResponseHelper.manageCommitBookingRequest(paymentType, incorrectInfo);
        paymentServiceResponseHelper.sendCommitBookingRequest();
    }
}
