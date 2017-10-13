package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.dao.InternalPaymentsDao;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.database.hybris.models.InternalPaymentModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PaymentServiceResponseHelper;
import com.hybris.easyjet.fixture.hybris.helpers.TravellerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.InternalPaymentFundsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.CommitBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.PaymentMethod;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.RefundOrFee;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PaymentMethodFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.InternalPaymentFundsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.InternalPaymentFundsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.apache.commons.math3.util.Precision.round;
import static org.junit.Assert.fail;

/**
 * All steps for the committing a booking using different payment methods.
 *
 * @author Mark Phipps
 * @author Joshua Curtis
 */
@DirtiesContext
@ContextConfiguration(classes = TestApplication.class)
public class MakePaymentWithMethodsSteps
{

    @Autowired
    private SerenityFacade testData;

    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private BasketHelper basketHelper;

    @Autowired
    private TravellerHelper travellerHelper;

    @Autowired
    private BookingHelper bookingHelper;

    @Autowired
    private PaymentServiceResponseHelper paymentServiceResponseHelper;

    @Autowired
    private CurrenciesDao currenciesDao;

    @Autowired
    private InternalPaymentsDao internalPaymentsDao;

    @Autowired
    private SerenityFacade serenityFacade;

    private BasketsResponse basketsResponse;

    private CommitBookingService commitBookingService;

    private double flightPrice;

    private String basketCurrency;

    // Credit fund variables.
    private double fundBalance;

    private double basketTotalConverted;

    private String creditFileName;

    // Default values.
    private String defaultSrc = "LTN";

    private String defaultDest = "ALC";

    private int defaultPassenger = 1;

    @And("^I have added a flight with bookingType \"([^\"]*)\" to the basket$")
    public void thatIHaveAddedAFlightWithBookingTypeToTheBasket(String bookingType) throws Throwable {
        paymentServiceResponseHelper.addFlightToBasketWithBookingType(defaultSrc, defaultDest, bookingType, defaultPassenger);
    }

    @When("^I send a valid commit booking request with credit fund \"([^\"]*)\" as payment type$")
    public void iReceiveAValidCommitBookingRequestWithCreditFundAsPaymentType(String fundName) throws Throwable {
        setLocalFundBalanceAndName(fundName, testData.getChannel());

        addPassengersToBasket();

        PaymentMethod paymentMethod = PaymentMethodFactory.generateCreditFilePaymentMethod(
            fundName,
            basketsResponse.getBasket().getTotalAmountWithDebitCard(),
            basketsResponse.getBasket().getCurrency().getCode(),
                "FCPH-261 test"
        );

        commitTheBooking(basketsResponse, paymentMethod);
    }

    @When("^I send a valid commit booking request with credit fund \"([^\"]*)\" as payment type using currency \"([^\"]*)\"$")
    public void iSendAValidCommitBookingRequestWithCreditFundAsPaymentTypeWithCurrency(String fundName, String currency) throws Throwable {
        this.basketCurrency = currency;

        setLocalFundBalanceAndName(fundName, testData.getChannel());

        addPassengersToBasket();

        this.flightPrice = basketsResponse.getBasket().getTotalAmountWithDebitCard();
        PaymentMethod paymentMethod = PaymentMethodFactory.generateCreditFilePaymentMethod(
            fundName,
            flightPrice,
            currency,
                "FCPH-261 test"
        );

        commitTheBooking(basketsResponse, paymentMethod);
    }

    @When("^I send a valid commit booking request with credit fund \"([^\"]*)\" as payment type with payment value of (\\d+)$")
    public void iSendAValidCommitBookingRequestWithCreditFundAsPaymentTypeWithPaymentValueOf(String fundName, int amount) throws Throwable {
        setLocalFundBalanceAndName(fundName, testData.getChannel());

        addPassengersToBasket();

        PaymentMethod paymentMethod = PaymentMethodFactory.generateCreditFilePaymentMethod(
            fundName,
            (double) amount,
            basketsResponse
                .getBasket()
                .getCurrency()
                .getCode(),
                "FCPH-261 test"
        );

        commitTheBooking(basketsResponse, paymentMethod);
    }

    @Then("^I will convert the requested amount to the currency of the credit file using the spot rate and reduce the fund balance$")
    public void iWillConvertTheRequestedAmountToTheCurrencyOfTheCreditFileUsingTheSpotRateAndReduceTheFundBalance() throws Throwable {
        double oldBalance = fundBalance;

        setLocalFundBalanceAndName(creditFileName, testData.getChannel());

        double currentBalanceAfterCommitBooking = fundBalance;

        String creditFileCurrency = currenciesDao.getCurrencyCodeForCreditFile(creditFileName);

        double creditFilesCurrencyExchangeRate = getExchangeRate(creditFileCurrency);
        double basketCurrencyExchangeRate = getExchangeRate(basketCurrency);

        double basketTotalInTargetCurrency = round((flightPrice * creditFilesCurrencyExchangeRate / basketCurrencyExchangeRate), 2);

        double expectedBalance = round(oldBalance - basketTotalInTargetCurrency, 2);

        Assert.assertTrue("Expected balance is : " + expectedBalance + ", but actual balance is: " + currentBalanceAfterCommitBooking, (expectedBalance == currentBalanceAfterCommitBooking));
    }

    @When("^I send a commit booking request with cash as the payment type$")
    public void iSendACommitBookingRequestWithCashAsThePaymentType() throws Throwable {
        String customerID = paymentServiceResponseHelper.updatePassengerAndGetCustomerId();
        paymentServiceResponseHelper.getCustomerProfile(customerID);
        addPassengersToBasket();

        PaymentMethod paymentMethod = PaymentMethodFactory.generateCashPaymentMethodsFromBasket(
            basketsResponse.getBasket(),
            basketsResponse.getBasket().getCurrency().getCode(),
            false
        );

        commitTheBooking(basketsResponse,paymentMethod);
    }

    @When("^I send a commit booking request with cash as the payment type on multiple payment methods in different currencies$")
    public void iSendACommitBookingRequestWithCashAsThePaymentTypeOnMultiplePaymentMethods() throws Throwable {
        addPassengersToBasket();

        PaymentMethod firstPaymentMethod = PaymentMethodFactory.generateCashPaymentMethodsFromBasket(
            basketsResponse.getBasket(),
            basketsResponse.getBasket().getCurrency().getCode(),
            false
        );

        PaymentMethod secondPaymentMethod = PaymentMethodFactory.generateCashPaymentMethodsFromBasket(
            basketsResponse.getBasket(),
            (basketsResponse.getBasket().getCurrency().getCode().equals("GBP") ? "EUR" : "GBP"), // Erm, that'll do? Right?
            false
        );

        // Redistribute payment amounts.
        double paymentDistributionAmount = 10.00;
        firstPaymentMethod.setPaymentAmount(firstPaymentMethod.getPaymentAmount() - paymentDistributionAmount);
        secondPaymentMethod.setPaymentAmount(paymentDistributionAmount);

        commitTheBooking(basketsResponse, firstPaymentMethod, secondPaymentMethod);
    }

    @Then("^I receive a booking confirmation and booking$")
    public void iReceiveABookingConfirmation() throws Throwable {
        String bookingRef = commitBookingService.getResponse().getBookingConfirmation().getBookingReference();
        String bookingStatus = commitBookingService.getResponse().getBookingConfirmation().getBookingStatus();
        if(bookingRef == null) fail("Booking failed with credit file fund payment");
        if(!(bookingStatus.equals("COMPLETED") || bookingStatus.equals("CONSIGNMENT_CREATED") || bookingStatus.equals("BOOKING_COMMITTED")))
            fail("Booking failed with credit file fund payment");

        serenityFacade.setData(SerenityFacade.DataKeys.BOOKING_ID, bookingRef);
    }

    @Then("^I receive a payment error \"([^\"]*)\"$")
    public void receiveAnInvalidPaymentMethodUsedError(String error) throws Throwable {
        commitBookingService.assertThatErrors().containedTheCorrectErrorMessage(error.split(", "));
    }

    /**
     * Add passengers to the current basket.
     */
    private void addPassengersToBasket() {
        // Get the basket.
        basketsResponse = basketHelper.getBasketService().getResponse();

        // Add valid passengers to the basket.
        basketHelper.updatePassengersForChannel(
            travellerHelper.createValidRequestToAddAllPassengersForBasket(basketsResponse),
            testData.getChannel(),
            basketsResponse
                .getBasket()
                .getCode()
        );
    }

    /**
     * Create the booking request and commit it.
     */
    private void commitTheBooking(BasketsResponse basketResponse, PaymentMethod... paymentMethods) {
        CommitBookingRequest commitBookingRequest = new CommitBookingRequest(
            HybrisHeaders.getValid(
                testData.getChannel()
            ).xClientTransactionId(
                "00000000-0000-0000-0000-000000000000"
            ).build(),
            bookingHelper.aBooking(
                basketsResponse,
                paymentMethods
            )
        );

        basketHelper.getBasket(
            basketHelper.getBasketService().getResponse().getBasket().getCode(),
            testData.getChannel()
        );

        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        testData.setData(SERVICE,commitBookingService);
        retryCommit();
        testData.setData(BOOKING_ID,
                commitBookingService
                        .getResponse()
                        .getBookingConfirmation()
                        .getBookingReference()
        );
    }

    private void retryCommit(){
        int[] noOfRetry ={6};
        pollingLoop().until(()->{
            commitBookingService.invoke();
            noOfRetry[0]--;
            return commitBookingService.getRestResponse().getStatusCode()==200 || noOfRetry[0]==0;
        });


    }

    /**
     * Get a list of all available payment funds.
     *
     * @param channel The channel to use
     * @return The payment funds.
     */
    private List<InternalPaymentModel> getPaymentFunds(String channel) {
        InternalPaymentFundsQueryParams internalFundParams = InternalPaymentFundsQueryParams.builder()
            .fundtype("credit-file")
            .build();

        InternalPaymentFundsService internalPaymentFundsService = serviceFactory.getPaymentFundsService(
            new InternalPaymentFundsRequest(HybrisHeaders.getValidWithToken(
                channel,
                testData.getAccessToken()
            ).build(), internalFundParams)
        );

        internalPaymentFundsService.invoke();

        internalPaymentFundsService.assertThat().internalPaymentFundsWereReturned();

        return internalPaymentsDao.getActiveCreditFiles();
    }

    /**
     * Get the predefined exchange rate for a given currency.
     *
     * @param curCode The currency to get the exchange rate for.
     * @return The exchange rate.
     */
    private double getExchangeRate(String curCode) {
        List<CurrencyModel> currencies = currenciesDao.getCurrencies(true);
        for (CurrencyModel currency : currencies) {
            if(currency.getCode().equalsIgnoreCase(curCode)) {
                return currency.getConversion();
            }
        }

        return -0.0;
    }

    /**
     * Set the balance and name of the fund.
     *
     * @param fundName The fund to get the balance of.
     * @param channel The channel to search for the fund in.
     */
    private void setLocalFundBalanceAndName(String fundName, String channel) {
        this.creditFileName = fundName;

        List<InternalPaymentModel> funds = getPaymentFunds(channel);
        for(InternalPaymentModel fund : funds) {
            if(fund.getCode().equalsIgnoreCase(fundName)) {
                fundBalance = fund.getCurrentBalance();
            }
        }
    }


    @And("^I send a commit booking request with cash and with card and \"([^\"]*)\" on multiple payment methods$")
    public void iSendACommitBookingRequestWithCashAndWithCardAndPaymentDetailsOnMultiplePaymentMethods(String paymentDetails) throws Throwable {
        addPassengersToBasket();

        PaymentMethod firstPaymentMethod = PaymentMethodFactory.generateCashPaymentMethodsFromBasket(
              basketsResponse.getBasket(),
              basketsResponse.getBasket().getCurrency().getCode(),
              false
        );

        // Redistribute payment amounts.
        double paymentDistributionAmount = 10.00;
        firstPaymentMethod.setPaymentAmount(firstPaymentMethod.getPaymentAmount() - paymentDistributionAmount);

        String[] cardDetails = paymentDetails.split("-");
        PaymentMethod secondPaymentMethod = PaymentMethodFactory.generateDebitCardPaymentMethod(cardDetails[0], cardDetails[1], cardDetails[2], cardDetails[3], cardDetails[4], cardDetails[5], paymentDistributionAmount, basketsResponse.getBasket().getCurrency().getCode());
        commitTheBooking(basketsResponse, firstPaymentMethod, secondPaymentMethod);
    }

    @When("^I send a commit booking request with cash as the payment type and invalid amount due ([^\"]*)$")
    public void iSendACommitBookingRequestWithCash(Double refundAmount) throws Throwable {
        addPassengersToBasket();

        PaymentMethod paymentMethod = PaymentMethodFactory.generateCashPaymentMethodsFromBasket(
                basketsResponse.getBasket(),
                basketsResponse.getBasket().getCurrency().getCode(),
                false
        );
        testData.setData(SerenityFacade.DataKeys.REFUND,"refund");
        commitTheBookingWithRefundAndFees(true,refundAmount, paymentMethod);
    }
    /**
     * Create the booking request with refund and fees and commit it.
     */
    private void commitTheBookingWithRefundAndFees(boolean isInvalidTest, Double refundAmount, PaymentMethod... paymentMethods) {

        Basket basket = basketHelper.getBasket(
                basketHelper.getBasketService().getResponse().getBasket().getCode(),
                testData.getChannel()
        );

        if(!isInvalidTest) {
            //Assumption is that cash payment would be populated in debit card amount
            refundAmount = basket.getPriceDifference().getAmountWithDebitCard();
        }
        GetBookingResponse getOriginalBookingResponse = bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID),testData.getData(SerenityFacade.DataKeys.CHANNEL));
        List<RefundOrFee>  refundsAndFees = new ArrayList<>();
        GetBookingResponse.Payment originalPayment = getOriginalBookingResponse.getBookingContext().getBooking().getPayments().stream().findFirst().get();
        RefundOrFee refundOrFee  = RefundOrFee.builder().amount(Math.abs(refundAmount)).currency(basket.getCurrency().getCode()).type(testData.getData(SerenityFacade.DataKeys.REFUND)).primaryReasonCode(testData.getData(SerenityFacade.DataKeys.REFUND)).originalPaymentMethod(Arrays.asList(paymentMethods).get(0).getPaymentMethod()).originalPaymentMethodContext(originalPayment.getTransactionId()).build();
        CommitBookingRequestBody commitBookingRequestBody  = bookingHelper.aBooking(
                basketsResponse,
                paymentMethods
        );

        refundsAndFees.add(refundOrFee);
        commitBookingRequestBody.setRefundsAndFees(refundsAndFees);


        CommitBookingRequest commitBookingRequest = new CommitBookingRequest(
                HybrisHeaders.getValid(
                        testData.getChannel()
                ).xClientTransactionId(
                        "00000000-0000-0000-0000-000000000000"
                ).build(),
                commitBookingRequestBody
        );

        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        testData.setData(SERVICE,commitBookingService);
        commitBookingService.invoke();
    }

    @When("^I send a commit booking request with cash as the payment type after remove passenger$")
    public void iSendACommitBookingRequestWithCashRemovePassgr() throws Throwable {
        addPassengersToBasket();

        PaymentMethod paymentMethod = PaymentMethodFactory.generateCashPaymentMethodsFromBasket(
                basketsResponse.getBasket(),
                basketsResponse.getBasket().getCurrency().getCode(),
                false
        );
        testData.setData(SerenityFacade.DataKeys.REFUND,"refund");
        commitTheBookingWithRefundAndFees(false,0.0, paymentMethod);
    }

    @When("^I commit again the booking for the amendable basket$")
    public void iCommitAgainTheBookingFromTheAmendableBasket() throws Throwable {
        String bookingRef = commitBookingService.getResponse().getBookingConfirmation().getBookingReference();
        String amendableBasketRef = basketHelper.getBasket(basketHelper.createAmendableBasket(bookingRef), testData.getChannel()).getCode();
        basketsResponse = basketHelper.getBasketService().getResponse();
        PaymentMethod paymentMethod = PaymentMethodFactory.generateCashPaymentMethodsFromBasket(
                basketsResponse.getBasket(),
                amendableBasketRef,
                false
        );

        commitTheBooking(basketsResponse, paymentMethod);
    }

    @When("^I've amendable basket with with credit fund (.*) as payment type$")
    public void iCommitBookingRequestWithCreditFundAsPaymentType(String fundName) throws Throwable {
        testData.setCurrency("GBP");
        paymentServiceResponseHelper.addFlightToBasketWithBookingType(defaultSrc, defaultDest, "BUSINESS", defaultPassenger);
        setLocalFundBalanceAndName(fundName, testData.getChannel());
        addPassengersToBasket();
        double creditFilesCurrencyExchangeRate = getExchangeRate("EUR");
        double basketCurrencyExchangeRate = getExchangeRate("GBP");

        Double totalAmountWithDebitCard = basketsResponse.getBasket().getTotalAmountWithDebitCard();
        basketTotalConverted = round((totalAmountWithDebitCard * creditFilesCurrencyExchangeRate / basketCurrencyExchangeRate), 2);

        PaymentMethod paymentMethod = PaymentMethodFactory.generateCreditFilePaymentMethod(
                fundName,
                totalAmountWithDebitCard,
                basketsResponse.getBasket().getCurrency().getCode(),
                "FCPH-261 test"
        );
        commitTheBooking(basketsResponse, paymentMethod);

        String amendableBasket = basketHelper.createAmendableBasket(testData.getData(BOOKING_ID));
        testData.setBasketId(amendableBasket);

    }

    @Then("^I will convert the requested refund amount to the currency of the credit file using the spot rate$")
    public void iWillConvertTheRequestedRefundAmountToTheCurrencyOfTheCreditFileUsingTheSpotRate() throws Throwable {
        double oldBalance = fundBalance;
        setLocalFundBalanceAndName(creditFileName, testData.getChannel());
        double uncancelledTicketPrice =oldBalance-fundBalance;
        double amountPriceDifference =testData.getData(SerenityFacade.DataKeys.PRICE_DIFFERENCE);
        setLocalFundBalanceAndName(creditFileName, testData.getChannel());
        double creditFilesCurrencyExchangeRate = getExchangeRate("EUR");
        double basketCurrencyExchangeRate = getExchangeRate("GBP");
        double basketTotalInTargetCurrency = round((Math.abs(amountPriceDifference) * creditFilesCurrencyExchangeRate / basketCurrencyExchangeRate), 2);
        Assert.assertTrue(fundBalance + uncancelledTicketPrice== oldBalance);
        testData.setData(SerenityFacade.DataKeys.PRICE_DIFFERENCE, basketTotalInTargetCurrency);
    }
    @And("^I recommit booking with (.*) with partial refund with different (.*) currency$")
    public void iRecommitBookingWithFundNameWithPartialRefundWithDifferentCurrency(String paymentMethod, String currencyType) throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        Double amountPriceDifference = basketHelper.getBasketService().getResponse().getBasket().getPriceDifference().getAmountWithDebitCard();
        testData.setData(SerenityFacade.DataKeys.PRICE_DIFFERENCE,amountPriceDifference);
        String originalPaymentMethodContext = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()).getBookingContext().getBooking().getPayments().stream().findFirst().orElse(null).getTransactionId();
        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();
        bookingHelper.commitBookingWithPartialRefund(basketService.getResponse(), Math.abs(amountPriceDifference), "24_HOUR_CANCELLATION", originalPaymentMethodContext, paymentMethod,false);
    }
}
