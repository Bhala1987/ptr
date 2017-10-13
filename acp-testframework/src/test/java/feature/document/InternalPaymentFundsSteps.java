package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.InternalPaymentsDao;
import com.hybris.easyjet.database.hybris.models.InternalPaymentModel;
import com.hybris.easyjet.fixture.hybris.helpers.AccountPasswordHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.InternalPaymentFundsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.InternalPaymentFundsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.InternalPaymentFundsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * Created by markphipps on 12/04/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class InternalPaymentFundsSteps {
    @Autowired
    private AccountPasswordHelper accountPasswordHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private InternalPaymentsDao internalPaymentsDao;
    @Autowired
    private SerenityFacade testData;
    private InternalPaymentFundsService internalPaymentFundsService;
    private List<InternalPaymentModel> funds;

    @Given("^I am logged in via channel \"([^\"]*)\"$")
    public void staffCustomerLoggedInViaChannel(String channel) throws Throwable {
        testData.setChannel(channel);
        accountPasswordHelper.createNewAccountForCustomerAndLoginIt();
    }

    @And("^that the \"([^\"]*)\" has initiated an invalid getInternalPaymentAvailableFunds request$")
    public void iAttemptToInitiateAnInvalidGetInternalPaymentFundsRequest(String channel) throws Throwable {
        internalPaymentFundsService = serviceFactory.getPaymentFundsService(new InternalPaymentFundsRequest(HybrisHeaders.getValidWithToken(channel, testData.getAccessToken()).build(), null));
    }

    @And("^the \"([^\"]*)\" send getInternalPaymentAvailableFunds request using filter \"([^\"]*)\" and fund type \"([^\"]*)\"$")
    public void thatTheHasInitiatedAValidGetInternalPaymentAvailableFundsRequestUsingFilterAndFundType(String channel, String filter, String fund) throws Throwable {
        InternalPaymentFundsQueryParams internalFundParams = InternalPaymentFundsQueryParams.builder().fundtype(fund).filterby(filter).build();
        internalPaymentFundsService = serviceFactory.getPaymentFundsService(new InternalPaymentFundsRequest(HybrisHeaders.getValidWithToken(channel, testData.getAccessToken()).build(), internalFundParams));
        internalPaymentFundsService.invoke();
    }

    @And("^the \"([^\"]*)\" send getInternalPaymentAvailableFunds request using fund type \"([^\"]*)\"$")
    public void thatTheHasInitiatedAValidGetInternalPaymentAvailableFundsRequestUsingFilterAndFundType(String channel, String fund) throws Throwable {
        InternalPaymentFundsQueryParams internalFundParams = InternalPaymentFundsQueryParams.builder().fundtype(fund).build();
        internalPaymentFundsService = serviceFactory.getPaymentFundsService(new InternalPaymentFundsRequest(HybrisHeaders.getValidWithToken(channel, testData.getAccessToken()).build(), internalFundParams));
        internalPaymentFundsService.invoke();
    }

    @Then("^I should add error message \"([^\"]*)\" to the getInternalPaymentFunds return message$")
    public void iShouldAddErrorMessageToTheSignificantOthersReturnMessage(String error) throws Throwable {
        internalPaymentFundsService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I receive the credit file request$")
    public void iReceiveTheCreditFileRequest() throws Throwable {
        internalPaymentFundsService.invoke();
    }

    @Then("^I will return a error message \"([^\"]*)\"$")
    public void iWillReturnAErrorMessage(String error) throws Throwable {
        internalPaymentFundsService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Then("^I will return a list of active credit files$")
    public void iWillReturnAListOfActiveCreditFilesAndVouchersBasedOnTheBookingTypeUserGroupChannelActiveToAndFromDates() throws Throwable {
        internalPaymentFundsService.assertThat().internalPaymentFundsWereReturned();
        funds = internalPaymentsDao.getActiveCreditFiles();

        Assert.assertTrue("List of credit files returned is not the same size as expected", funds.size() == getInternalPaymentOptionsCount(internalPaymentFundsService, false));
    }

    @And("^the \"([^\"]*)\" send getInternalPaymentAvailableFunds request using an invalid parameter$")
    public void thatTheHasInitiatedAValidGetInternalPaymentAvailableFundsRequestUsingAnInvalidParameter(String channel) throws Throwable {
        InternalPaymentFundsQueryParams internalFundParams = InternalPaymentFundsQueryParams.builder().invalid("bar").build();
        internalPaymentFundsService = serviceFactory.getPaymentFundsService(new InternalPaymentFundsRequest(HybrisHeaders.getValidWithToken(channel, testData.getAccessToken()).build(), internalFundParams));
        internalPaymentFundsService.invoke();
    }

    /*
    @param vouchers - used to determine whether to look for the list of vouchers ot the list of credit files
     */
    private int getInternalPaymentOptionsCount(InternalPaymentFundsService options, boolean vouchers) {
        int count = 0;
        try{
            if(vouchers) {
                count = options.getResponse().getVouchers().size();
            }
            else {
                count = options.getResponse().getCreditFiles().size();
            }
        }
        catch(Exception e) { /* in the case of no 'vouchers' a null pointer will be thrown on 'getX'*/ }
        return count;
    }
}
