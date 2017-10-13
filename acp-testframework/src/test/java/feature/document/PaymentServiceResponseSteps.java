package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PaymentMethodHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PaymentServiceResponseHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails.RemoveSavedPaymentService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;

/**
 * Created by rajakm on 15/05/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class PaymentServiceResponseSteps {

    protected static Logger LOG = LogManager.getLogger(AddEJPlusSeatToBasketSteps.class);

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private PaymentServiceResponseHelper paymentServiceResponseHelper;
    @Autowired
    private PaymentMethodHelper paymentMethodHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper bookingHelper;
    private RemoveSavedPaymentService removeSavedPaymentService;

    private String[] paymentDetails = null;
    private String paymentType;

    @Given("^that I have added a flight \"([^\"]*)\" to \"([^\"]*)\" to the basket with currency ([^\"]*) from ([^\"]*)$")
    public void that_i_have_added_a_flight_something_to_something_to_the_basket_with_currency_something_from_something(String src, String dest, String currency, String channel) throws Throwable {
        testData.setChannel(channel);
        testData.setCurrency(currency);
        testData.setOrigin(src);
        testData.setDestination(dest);
        paymentServiceResponseHelper.addFlightToBasket(src, dest, STANDARD);
    }

    @When("^I do commit booking with ([^\"]*) and ([^\"]*)$")
    public void i_do_commit_booking_with_something_and_something(String paymentType, String values) throws Throwable {
        paymentDetails = values.split("-");
        this.paymentType = paymentType;
        testData.setCurrency("basketCurrency");
        paymentServiceResponseHelper.addValuesInToMap(paymentDetails, paymentType);

    }

    @Then("^I will generate a make payment request to the payment service$")
    public void i_will_generate_a_make_payment_request_to_the_payment_service() throws Throwable {
        paymentServiceResponseHelper.createBookingWithValidPaymentDetails(testData.getCurrency(), "false", paymentType);
        paymentServiceResponseHelper.sendCommitBookingRequest();

        testData.setData(BOOKING_ID,
                paymentServiceResponseHelper.getCommitBookingService()
                        .getResponse()
                        .getBookingConfirmation()
                        .getBookingReference()
        );
    }

    @And("^I receive a successful response from PSP and continue with commit booking process$")
    public void i_receive_a_successful_response_from_psp_and_continue_with_commit_booking_process() throws Throwable {
        paymentServiceResponseHelper.verifyCommitBookingIsSuccessful();
    }

    @Then("^commit booking should be successful$")
    public void commit_booking_should_be_successful() throws Throwable {
        paymentServiceResponseHelper.createBookingWithValidPaymentDetails(testData.getCurrency(), "false", paymentType);
        paymentServiceResponseHelper.sendCommitBookingRequest();
        paymentServiceResponseHelper.verifyCommitBookingIsSuccessful();
    }

    @When("^I perform commit booking with ([^\"]*) and ([^\"]*) with ([^\"]*)$")
    public void i_perform_commit_booking_with_something_and_something_with_something(String paymentType, String values, String incorrectcurrency) throws Throwable {
        paymentDetails = values.split("-");
        this.paymentType = paymentType;
        testData.setCurrency(incorrectcurrency);
        paymentServiceResponseHelper.addValuesInToMap(paymentDetails, paymentType);

        paymentServiceResponseHelper.createBookingWithValidPaymentDetails(testData.getCurrency(), "false", paymentType);
        paymentServiceResponseHelper.sendCommitBookingRequest();
    }

    @And("^I will fail the commit Booking Process$")
    public void i_will_fail_the_commit_booking_process() throws Throwable {
        paymentServiceResponseHelper.verifyCommitBookingIsFailed();
    }

    @And("^I will generate a make payment request when payment service is offline$")
    public void i_will_generate_a_make_payment_request_when_payment_service_is_offline() throws Throwable {
        //This can be achieved only on Mocks
        paymentServiceResponseHelper.createBookingWithValidPaymentDetails(testData.getCurrency(), "true", paymentType);
        paymentServiceResponseHelper.sendCommitBookingRequest();
    }

    @When("^I receive an offline payment accepted response back from the payment service$")
    public void i_receive_an_offline_payment_accepted_response_back_from_the_payment_service() throws Throwable {
        //Verify in the ACP logs only
    }

    @Then("^I will record the payment transaction on the basket$")
    public void i_will_record_the_payment_transaction_on_the_basket() throws Throwable {
        //Verify in the backoffice only
    }

    @And("^I will continue with the commit booking process$")
    public void i_will_continue_with_the_commit_booking_process() throws Throwable {
        paymentServiceResponseHelper.verifyCommitBookingIsSuccessful();
    }

    @And("^I do commit booking request with ([^\"]*) and ([^\"]*) containing invalid ([^\"]*)$")
    public void iDoCommitBookingRequestWithAndContainingInvalid(String paymentType, String values, String invalidDetails) throws Throwable {
        paymentDetails = values.split("-");
        this.paymentType = paymentType;
        paymentServiceResponseHelper.addValuesInToMap(paymentDetails, paymentType);

        paymentServiceResponseHelper.createBookingWithValidPaymentDetails(testData.getCurrency(), "false", paymentType);
        paymentServiceResponseHelper.modifyPaymentWithInvalidDetails(invalidDetails);
    }

    @And("^I have a flight \"([^\"]*)\" to \"([^\"]*)\" with currency ([^\"]*) fare ([^\"]*) and period ([^\"]*)$")
    public void iHaveAFlightToWithCurrencyFareAndPeriod(String src, String dest, String currency, String fareType, String period) throws Throwable {
        testData.setPeriod(period);
        testData.setCurrency(currency);
        paymentServiceResponseHelper.addFlightToBasket(src, dest, fareType);
    }

    @When("^I receive payment rejected response$")
    public void iReceivePaymentRejectedResponse() throws Throwable {
        paymentServiceResponseHelper.sendCommitBookingRequest();
    }

    @Then("^I got ([^\"]*)$")
    public void iGotError(String error) throws Throwable {
        paymentServiceResponseHelper.getCommitBookingService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Given("^I am ([^\"]*) a staff ([^\"]*) and logged in as ([^\"]*) and ([^\"]*)$")
    public void iAmAStaffAndLoggedInAsAnd(boolean staff, String theStaffCustomerUID, String user, String pwd) throws Throwable {
        if (staff) {
            paymentServiceResponseHelper.prepareStatementForCommitBookingStaffCustomer(staff, theStaffCustomerUID, user, pwd);
        } else {
            paymentServiceResponseHelper.setStaff(false);
        }
    }

    @And("^I remove saved payment with invalid (.*)")
    public void iRemoveSavedPaymentWithInvalid(String invalidType) throws Throwable {
        paymentMethodHelper.removePayment(invalidType);
    }

    @And("^I see remove payment error code (.*)")
    public void iSeeRemovePaymentErrorCodeErrorCode(String errorCode) throws Throwable {
        removeSavedPaymentService = (RemoveSavedPaymentService) testData.getData("removeSavedPaymentService");
        removeSavedPaymentService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^I receive a successful response and the booking reference$")
    public void iReceiveASuccessfulResponseAndTheBookingReference() throws Throwable {
        paymentServiceResponseHelper.verifyCommitBookingIsSuccessful();
        testData.setData(BOOKING_ID, paymentServiceResponseHelper.getCommitBookingService().getResponse().getBookingConfirmation().getBookingReference());
    }

    @When("^I send a success remove payment details request$")
    public void iSendASuccessRemovePaymentDetailsRequest() throws Throwable {
        paymentMethodHelper.createCustomerAndLoginIt();
        basketHelper.myBasketContainsAFlightWithPassengerMix(testData.getPassengerMix(), testData.getChannel(), STANDARD, false);
        BasketsResponse basketsResponse = basketHelper.getBasketService().getResponse();
        bookingHelper.createNewBooking(bookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketsResponse, testData.getData(CUSTOMER_ID), testData.getChannel(), false));
        bookingHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
        String getSavedPaymentMethodReference = bookingHelper.getCustomerProfileService().getResponse().getCustomer().getAdvancedProfile().getSavedPayments().getDebitCards().stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No paymentsMethod stored aginst customer profile")).getSavedPaymentMethodReference();
        testData.setData("savedPaymentMethodReference", getSavedPaymentMethodReference);
        paymentMethodHelper.removePaymentFromReference(getSavedPaymentMethodReference);
        RemoveSavedPaymentService removeSavedPaymentService = (RemoveSavedPaymentService) testData.getData("removeSavedPaymentService");
        removeSavedPaymentService.getResponse();
    }

    @Then("^the payment method should be removed against the customer profile$")
    public void thePaymentMethodShouldBeRemovedAgainstTheCustomerProfile() throws Throwable {
        bookingHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
        CustomerProfileResponse.Customer customer = bookingHelper.getCustomerProfileService().getResponse().getCustomer();
        RemoveSavedPaymentService removeSavedPaymentService = (RemoveSavedPaymentService) testData.getData("removeSavedPaymentService");
        String getSavedPaymentMethodReference = (String) testData.getData("savedPaymentMethodReference");
        removeSavedPaymentService.assertThat().verifyPaymentMethodHasBeenDeleted(customer, getSavedPaymentMethodReference);
    }

    @Then("^I will generate a make payment request to the payment service with refund fee$")
    public void i_will_generate_a_make_payment_request_to_the_payment_service_with_refund() throws Throwable {
        paymentServiceResponseHelper.createBookingWithValidPaymentDetails(testData.getCurrency(), "false", paymentType);
        testData.setData(SerenityFacade.DataKeys.REFUND, "refund");
        paymentServiceResponseHelper.prepareRefundAndFeesData();
        paymentServiceResponseHelper.sendCommitBookingRequest();

        testData.setData(BOOKING_ID,
                paymentServiceResponseHelper.getCommitBookingService()
                        .getResponse()
                        .getBookingConfirmation()
                        .getBookingReference()
        );
    }
}
