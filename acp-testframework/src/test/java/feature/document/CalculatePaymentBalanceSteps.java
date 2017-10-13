package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.RepriceBasketHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by giuseppecioce on 27/07/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class CalculatePaymentBalanceSteps {
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private RepriceBasketHelper repriceBasketHelper;

    @Given("^I request a calculate payment balance with (.*)$")
    public void iRequestACalculatePaymentBalanceWith(String field) throws Throwable {
        String basketCode = repriceBasketHelper.produceBasketRef();
        repriceBasketHelper.prepareStatementRequest(basketCode, "DL");
        repriceBasketHelper.invalidRequestWithField(field);
        repriceBasketHelper.invokePaymentBalance();
    }

    @Then("^I see the process fail with (.*)$")
    public void iSeeTheProcessFailWith(String error) throws Throwable {
        repriceBasketHelper.getPaymentBalanceService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I request a valid calculate payment balance with fee depending to the payment method (.*)$")
    public void iRequestAValidCalculatePaymentBalanceWithNoFeeAssociatedToThePaymentMethod(String paymentMethod) throws Throwable {
        String basketCode = repriceBasketHelper.produceBasketRef();
        repriceBasketHelper.prepareStatementRequest(basketCode, paymentMethod);
        repriceBasketHelper.invokePaymentBalance();
    }

    @Then("^I should see the outstanding balance for the basket giving the payment methods and amounts requested$")
    public void iShouldSeeTheOutstandingBalanceForTheBasketGivingThePaymentMethodsAndAmountsRequested() throws Throwable {
        repriceBasketHelper.getPaymentBalanceService().assertThat().verifyOutstandingBalanceBasedOnPaymentMethod((Basket) testData.getData("basket"));
    }

    @And("^I should see the payment method fee based on the payment amounts and payment methods requested$")
    public void iShouldSeeThePaymentMethodFeeBasedOnThePaymentAmountsAndPaymentMethodsRequested() throws Throwable {
        repriceBasketHelper.getPaymentBalanceService().assertThat().verifyAdminFeeBasedOnPaymentMethod(repriceBasketHelper.getPaymentBalanceRequestBody());
    }
}
