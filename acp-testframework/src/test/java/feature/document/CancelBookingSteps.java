package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CreditFileFundDao;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.dao.PaymentModeDao;
import com.hybris.easyjet.database.hybris.models.CreditFileFundModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHoldItemsHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.CancelBookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.InitiateCancelBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.CancelBookingRefundService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.InitiateCancelBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.CANCEL;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = TestApplication.class)
@DirtiesContext
public class CancelBookingSteps {
    @Autowired
    private SerenityFacade testData;

    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    BasketHelper basketHelper;

    @Autowired
    private CancelBookingHelper cancelBookingHelper;

    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;

    @Getter
    private InitiateCancelBookingService cancelBookingService;
    @Autowired
    private PaymentModeDao paymentModeDao;

    @Autowired
    private CreditFileFundDao creditFileFundDao;

    @Autowired
    private CurrenciesDao currenciesDao;

    String originalPaymentMethodContext;
    double amount;
    String primaryReasonCode;
    String entry;
    private Double originalBalance;
    private String originalCurrency;
    private String originalPaymentMethodContextForCreditFile;
    private List <Double>refundFee = new ArrayList<Double>();

    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @When("^I request to cancel my booking using an invalid booking reference.$")
    public void iRequestToCancelMyBookingUsingAnInvalidBookingReference() {
        testData.setData(BOOKING_ID, "INVALID_BOOKING_ID");

        iRequestToCancelMyBooking();
    }

    @And("^I request to cancel my booking$")
    public void iRequestToCancelMyBooking() {
        InitiateCancelBookingRequest cancelBookingRequest = new InitiateCancelBookingRequest(
                HybrisHeaders.getValid(
                        testData.getChannel()
                ).build(),
                BookingPathParams.builder()
                        .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                        .path(CANCEL)
                        .build()
        );

        cancelBookingService = serviceFactory.initiateCancelBooking(cancelBookingRequest);

        // Needed to use the generic steps (for errors).
        testData.setData(SERVICE, cancelBookingService);

        cancelBookingService.invoke();
    }

    @And("^my booking is cancelled$")
    public void myBookingIsCancelled() {
        cancelBookingService.assertThat().bookingWasCancelled();
    }

    @Then("^the refund amount should be calculated appropriately for the given payment details and booking age$")
    public void theRefundAmountShouldBeCalculatedAppropriatelyForTheGivenPaymentTypeAndBookingAge() {
        GetBookingResponse.BookingContext bookingContext = bookingHelper.getBookingDetails(
                testData.getData(SerenityFacade.DataKeys.BOOKING_ID),
                testData.getChannel()
        ).getBookingContext();

        cancelBookingService.assertThat().refundAmountHasBeenCalculatedAppropriately(
                bookingContext.getBooking()
        );
    }

    @And("^the response should contain Primary Reason Code \"([^\"]*)\" and Primary Reason Name \"([^\"]*)\"$")
    public void theResponseShouldContainPrimaryReasonCodeAndPrimaryReasonName(String reasonCode, String reasonName) {
        cancelBookingService.assertThat().primaryReasonCodeAndNameIsSet(reasonCode, reasonName);
    }

    @When("^cancel my booking with an ([^\"]*) booking ref number$")
    public void cancelMyBookingWithAnInvalidBookingRefNumber(String entry) throws Throwable {
        bookingHelper.cancelBookingwithInvalidRef(entry);

    }

    @Then("^booking response returns ([^\"]*)$")
    public void bookingResponseReturns(String errorCode) throws Throwable {
        bookingHelper.getCancelBookingRefundService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @When("^cancel my booking with an ([^\"]*) refund amount$")
    public void cancelMyBookingWithAnIncorrectRefundAmount(String entry) throws Throwable {
        String originalPaymentMethodContext = bookingHelper.getGetBookingService().getResponse().getBookingContext().getBooking().getPayments().stream().findFirst().orElse(null).getTransactionId();
        bookingHelper.cancelBooking(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), entry, originalPaymentMethodContext);
    }

    @When("^cancel my booking with a ([^\"]*) issued$")
    public void cancelMyBookingWithARefundIssued(String entry) throws Throwable {
        iRequestToCancelMyBooking();
        this.entry = entry;
        amount = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getAmount();
        originalPaymentMethodContext = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getOriginalPaymentMethodContext();
        String primaryReasonCode = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getPrimaryReasonCode();
        bookingHelper.cancelBooking(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), entry, amount, originalPaymentMethodContext, primaryReasonCode);
    }

    @When("^attempt to cancel a cancelled booking and issue another refund$")
    public void attemptToCancelACancelledBookingAndIssueARefund() throws Throwable {
        bookingHelper.cancelBooking(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), entry, amount, originalPaymentMethodContext, primaryReasonCode);
    }

    @When("^cancel my booking using an \"([^\"]*)\" and \"([^\"]*)\"$")
    public void cancelMyBookingUsingAnAnd(String currency, String paymentMethod) throws Throwable {
        iRequestToCancelMyBooking();
        double amount = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getAmount();
        String originalPaymentMethodContext = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getOriginalPaymentMethodContext();
        String primaryReasonCode = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getPrimaryReasonCode();
        bookingHelper.cancelBookingInvalidCurrency(testData.getData(SerenityFacade.DataKeys.BOOKING_ID),amount, originalPaymentMethodContext, primaryReasonCode, currency,paymentMethod);
    }


    @Then("^updated booking status should return \"([^\"]*)\"$")
    public void updatedBookingStatusShouldReturn(String bookingStatus) throws Throwable {
        pollingLoop().ignoreExceptions().untilAsserted(
                () -> {
                    bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getChannel());
                    bookingHelper.getGetBookingService().assertThat().bookingStatus(bookingStatus);
                }
        );
    }

    @When("^I request to cancel my full booking$")
    public void iRequestToCancelMyFullBooking() throws Throwable {
        cancelBookingHelper.initiateCancelBooking();
        cancelBookingHelper.cancelBooking();
    }

    @Then("^the booking status should be (.*)$")
    public void theBookingStatusShouldBe(String bookingStatus) throws Throwable {
        cancelBookingHelper.cancelBookingStatusCheck(bookingStatus);
    }

    @When("^I request for cancel booking$")
    public void iRequestForCancelBooking() throws Throwable {
        List<String> holdItems = testData.getData(SerenityFacade.DataKeys.FLIGHT_HOLD_ITEMS);
        testData.setData(SerenityFacade.DataKeys.FLIGHT_INVENTORY, basketHoldItemsHelper.getActualReservedItem(holdItems));
        cancelBookingHelper.initiateCancelBooking();
        cancelBookingHelper.cancelBooking();
        CancelBookingRefundService cancelBookingRefundService = testData.getData(SERVICE);
        cancelBookingRefundService.getResponse();
        cancelBookingHelper.cancelBookingStatusCheck("CANCELLED");
    }

    @Then("^I will set the transaction \"([^\"]*)\"$")
    public void iWillSetTheTransaction(String status) throws Throwable {
        pollingLoop().untilAsserted(() -> {
            List<String> refundPaymentTransactionStatus = paymentModeDao.getRefundPaymentTransactionStatus(testData.getData(BOOKING_ID));
            assertThat(refundPaymentTransactionStatus.get(0)).isEqualTo(status);
        });


    }

    @And("^I will receive an error code ([^\"]*) with status ([^\"]*)$")
    public void iWillReceiveAnErrorCode(String errorCode, int responseCode) throws Throwable {
        bookingHelper.getCancelBookingRefundService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
        assertEquals(responseCode, bookingHelper.getCancelBookingRefundService().getStatusCode());
    }

    /**
     * getCurrentDate, it gets the current date
     * @return
     */
    public static String getCurrentDate() {
        return LocalDate.now() + " 00:00:00";
    }

    @And("^I cancel the booking with refund and will get (\\d+) response code$")
    public void iCancelTheBookingWithRefundAndWillGetResponseCode(int responseCode) throws Throwable {
        iRequestToCancelMyBooking();
        String originalPaymentMethodContext = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getOriginalPaymentMethodContext();
        originalCurrency = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getCurrency();
        String paymentMethod = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getOriginalPaymentMethod();
        originalPaymentMethodContextForCreditFile = originalPaymentMethodContext;
        List <Double>refundAmount = new ArrayList<Double>();
        cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().stream().
                filter(refundAmt ->refundAmt.getType().equals("refund")).forEach(refundAmt ->refundAmount.add(refundAmt.getAmount()));
        cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().stream().
                filter(refundFees ->refundFees.getType().equals("fee")).forEach(refundFees ->refundFee.add(refundFees.getAmount()));
        String primaryReasonCode = cancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees().get(0).getPrimaryReasonCode();
        bookingHelper.cancelBooking(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), refundAmount.get(0), originalPaymentMethodContext, primaryReasonCode, paymentMethod, originalCurrency);
        assertEquals(responseCode, bookingHelper.getCancelBookingRefundService().getStatusCode());
    }

    @And("^I will verify the credit file current balance using \"([^\"]*)\"$")
    public void iWillVerifyTheCreditFileCurrentBalanceUsing(String creditFile) throws Throwable {
        CreditFileFundModel result = creditFileFundDao.getCreditFileBalance(getCurrentDate(), creditFile );
        assertNotNull(result);
        originalBalance = result.getCurrentBalance();
    }

    @And("^I will verify the amount for \"([^\"]*)\" and status as (.*)$")
    public void iWillVerifyTheAmountForAndStatusAsREFUND_ACCEPTED(String code, String refundStatus) throws Throwable {
        CreditFileFundModel result = creditFileFundDao.getCreditFileBalance(getCurrentDate(), code );
        assertNotNull(result);
        assertEquals(decimalFormat.format(Double.valueOf(originalBalance.doubleValue() - refundFee.get(0).doubleValue())), decimalFormat.format(result.getCurrentBalance()));
        assertEquals(refundStatus, paymentModeDao.getPaymentTransactionStatusByCode(originalPaymentMethodContextForCreditFile));
    }

    @And("^I will use currency conversion for \"([^\"]*)\" and status as (.*)$")
    public void iWillUseCurrencyConversionForAndStatusAsREFUND_ACCEPTED(String code, String refundStatus) throws Throwable {
        Double conversionRate = currenciesDao.getConversionRateByCode(originalCurrency);
        assertNotNull(conversionRate);
        CreditFileFundModel result = creditFileFundDao.getCreditFileBalance(getCurrentDate(), code);
        assertNotNull(result);
        conversionRate = new Double(decimalFormat.format(refundFee.get(0).doubleValue()/conversionRate.doubleValue()));
        assertEquals(decimalFormat.format(Double.valueOf(originalBalance.doubleValue() - conversionRate)), decimalFormat.format(result.getCurrentBalance()));
        assertEquals(refundStatus, paymentModeDao.getPaymentTransactionStatusByCode(originalPaymentMethodContextForCreditFile));
    }
}
