package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.dao.DiscountReasonDao;
import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.DiscountReasonQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.DiscountReasonRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.DiscountReasonService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * Created by giuseppecioce on 31/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetDiscountReasonSteps {

    @Autowired
    private HybrisServiceFactory servicefactory;
    private DiscountReasonService discountReasonService;
    @Autowired
    private DiscountReasonDao discountReasonDao;
    private List<String> expectedResult;
    @Autowired
    private CurrenciesDao currenciesDao;
    private String channel;
    private String currency = "";

    @Given("^The channel has initiated a getDiscountReason request using channel \"([^\"]*)\"$")
    public void theChannelHasInitiatedAGetDiscountReasonRequestUsingChannel(String channel) throws Throwable {
        this.channel = channel;
        expectedResult = discountReasonDao.getValidDiscountIgnoreCurrency();
    }

    @Given("^The channel has initiated a getDiscountReason request using channel \"([^\"]*)\" and specific currency$")
    public void theChannelHasInitiatedAGetDiscountReasonRequestUsingChannelAndSpecificCurrency(String channel) throws Throwable {
        this.channel = channel;
        List<CurrencyModel> activeCurrencies = currenciesDao.getCurrencies(true);
        currency = activeCurrencies.get(0 + (int) (Math.random() * activeCurrencies.size())).getCode();
        expectedResult = discountReasonDao.getAllValidDiscount(currency);
    }

    @When("^I receive the request$")
    public void iReceiveTheRequest() throws Throwable {
        discountReasonService = servicefactory.getDiscountReasonService(new DiscountReasonRequest(HybrisHeaders.getValid(channel).build(), DiscountReasonQueryParams.builder().currency(currency).build()));
        discountReasonService.invoke();
    }

    @Then("^I will return a list of active Discount reason codes to the channel$")
    public void iWillReturnAListOfActiveDiscountReasonCodesToTheChannel() throws Throwable {
        discountReasonService.assertThat().theResultIsTheExpected(expectedResult);
    }

    @And("^the request is not in the format defined in the service contract$")
    public void theRequestIsNotInTheFormatDefinedInTheServiceContract() throws Throwable {
        currency = "INVALID_CURRENCY";
    }

    @Then("^I will return an error message \"([^\"]*)\"$")
    public void iWillReturnAnErrorMessage(String error) throws Throwable {
        discountReasonService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @And("^I have discount reason which offline date is in the past from the current date$")
    public void iHaveDiscountReasonWhichOfflineDateIsInThePastFromTheCurrentDate() throws Throwable {
        expectedResult = discountReasonDao.getInvalidDiscount(currency);
    }

    @Then("^the discount reason is not returned$")
    public void theDiscountReasonIsNotReturned() throws Throwable {
        discountReasonService.assertThat().theResultNotContain(expectedResult);
    }
}
