package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.PassengerInformationDao;
import com.hybris.easyjet.database.hybris.dao.PaymentModeDao;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.CompensationHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import java.util.HashMap;
import java.util.List;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Niyi Falade on 18/09/17.
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddCompensationSteps {
    protected static Logger LOG = LogManager.getLogger(AddCompensationSteps.class);

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private CompensationHelper compensationHelper;
    @Autowired
    private PaymentModeDao paymentModeDao;
    @Autowired
    private PassengerInformationDao passengerInformationDao;
    @Autowired
    BookingHelper bookingHelper;

    @And("^create compensation for a basket using paymentType (.*) and (.*)$")
    public void createCompensationForBasket(String paymentType, boolean isLead) throws Throwable {
        compensationHelper.addCompensationToBasket(paymentType, isLead, "GBP", "100");
    }

    @Then("^compensation payment status (.*)$")
    public void compensationPaymentStatus(String status) throws Throwable {
        List<String> refundPaymentTransactionStatus = paymentModeDao.getRefundPaymentTransactionStatus(testData.getData(BASKET_ID));
        assertThat(refundPaymentTransactionStatus.get(0).equalsIgnoreCase(status))
                .isTrue()
                .withFailMessage("incorrect status set");
    }

    @And("^create compensation process using invalid (.*)$")
    public void createCompensationProcessUsingInvalid(String entry) throws Throwable {
        compensationHelper.addCompensationUsingInvalidItem(entry);
    }

    @Then("^create compensation service returns \"([^\"]*)\"$")
    public void createCompensationServiceReturns(String errorCode) throws Throwable {
        compensationHelper.getCreateCompensationService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @When("^create compensation omitting the mandatory \"([^\"]*)\"$")
    public void createCompensationOmittingTheMandatoryAndAndAnd(String entry ) throws Throwable {
        compensationHelper.addCompensationOmitMandatoryFields(entry);
    }

    @And("^create compensation with (.*), (.*) and (.*)$")
    public void createCompensationWithPaymentTypeCurrencyAndCompensationAmount(String paymentType, String currency, String compensationAmount) throws Throwable {
        compensationHelper.addCompensationToBasket(paymentType, true, currency, compensationAmount);
    }

    @Then("^I see voucher's unique code, email, amount, (.*), balance same as original amount and active,end dates$")
    public void iSeeVoucherSUniqueCodeAmountCurrencyBalanceSameAsOriginalAmountAndActiveEndDates(String currency) throws Throwable {
        bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel());
        HashMap<String, String> voucherDetails = passengerInformationDao.getVoucherInformation(testData.getData(BOOKING_ID)).stream().findFirst().orElse(null);
        String paymentType = "VOUCHER";
        GetBookingResponse.Payment paymentObj = bookingHelper.getGetBookingService().getResponse().getBookingContext().getBooking().getPayments().stream()
                .filter(f -> f.getType().toUpperCase().equals(paymentType)).findFirst().orElse(null);
        bookingHelper.getGetBookingService().assertThat().checkCorrectPaymentDetailsDisplayed(paymentObj,paymentType, currency);
        bookingHelper.getGetBookingService().assertThat().checkCorrectVoucherDetailsDisplayed(voucherDetails);

    }
}
