package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.PaymentMethodHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 21/06/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class ManagePaymentMethodSteps {

    @Autowired
    private PaymentMethodHelper paymentMethodHelper;
    @Autowired
    private SerenityFacade testData;

    private static final String invalidId = "invalidID";
    private static final String invalidSession = "invalidSession";
    private static final String mismatchIdSession = "mismatchIDSession";
    private static final String invalidPaymentId = "invalidPaymentID";
    private static final String invalidPaymentReferenceId = "invalidPaymentReferenceId";

    @Then("^I (.*) get an error (.*)")
    public void iShouldOrNotGetAnError(boolean should, String error) throws Throwable {
        paymentMethodHelper.shouldIReceiveAnError(should, error);
    }

    @When("^I submit add payment details (.*) request as default (.*) for an identified customer$")
    public void iSubmitAddPaymentDetailsRequestForAnIdentifiedCustomers(PaymentMethodHelper.PAYMENT_METHOD paymentMethod, boolean paymentIsDefault) throws Throwable {
        testData.setAsDefaultPaymentMethod(paymentIsDefault);

        paymentMethodHelper.createCustomerAndLoginIt();
        paymentMethodHelper.submitPaymentMethod(paymentMethod, invalidId);
    }

    @When("^I submit add payment details (.*) request as default (.*) for a user not logged in$")
    public void iSubmitAddPaymentDetailsRequestForAUserNotLoggedIn(PaymentMethodHelper.PAYMENT_METHOD paymentMethod, boolean paymentIsDefault) throws Throwable {
        testData.setAsDefaultPaymentMethod(paymentIsDefault);

        paymentMethodHelper.createCustomerAndLoginIt();
        paymentMethodHelper.submitPaymentMethod(paymentMethod, invalidSession);
    }

    @When("^I submit add payment details (.*) request as default (.*) for a not matching logged in user and customer requested$")
    public void iSubmitAddPaymentDetailsRequestForANotMatchingLoggedInUserAndCustomerRequested(PaymentMethodHelper.PAYMENT_METHOD paymentMethod, boolean paymentIsDefault) throws Throwable {
        testData.setAsDefaultPaymentMethod(paymentIsDefault);

        paymentMethodHelper.createCustomerAndLoginIt();
        paymentMethodHelper.submitPaymentMethod(paymentMethod, mismatchIdSession);
    }

    @When("^I submit add payment details (.*) request as default (.*) with invalid payment method ID$")
    public void iSubmitAddPaymentDetailsRequestWithInvalidPaymentMethodID(PaymentMethodHelper.PAYMENT_METHOD paymentMethod, boolean paymentIsDefault) throws Throwable {
        testData.setAsDefaultPaymentMethod(paymentIsDefault);

        paymentMethodHelper.createCustomerAndLoginIt();
        paymentMethodHelper.submitPaymentMethod(paymentMethod, invalidPaymentId);
    }

    @When("^I submit add payment details (.*) valid request as default (.*)$")
    public void iSubmitAddPaymentDetails(PaymentMethodHelper.PAYMENT_METHOD paymentMethod, boolean paymentIsDefault) throws Throwable {
        testData.setAsDefaultPaymentMethod(paymentIsDefault);
        paymentMethodHelper.createCustomerAndLoginIt();
        paymentMethodHelper.submitPaymentMethod(paymentMethod, "");
    }

    @Then("^I will receive a successful response$")
    public void iWillReceiveASuccessfulResponse() throws Throwable {
        paymentMethodHelper.shouldIReceiveAnError(false, "");
    }

    @Then("^the PaymentInfo entity has been added to the customer$")
    public void thePaymentInfoEntityHasBeenAddedToTheCustomer() throws Throwable {
        List<String> availablePaymentForCustomer = paymentMethodHelper.getPaymentReferenceForCustomer();
        assertThat(availablePaymentForCustomer)
                .withFailMessage("No payment entity has been stored against the customer profile")
                .isNotNull();

        assertThat(availablePaymentForCustomer)
                .withFailMessage("No payment entity has been stored against the customer profile")
                .isNotEmpty();
    }

    @Then("^the PaymentInfo entity should be stored against the customer profile for each payment method$")
    public void thePaymentInfoEntityShouldBeStoredAgainstTheCustomerProfileForEachPaymentMethod() throws Throwable {
        List<String> availablePaymentForCustomer = paymentMethodHelper.getPaymentReferenceForCustomer();
        paymentMethodHelper.getSavedPaymentMethodService().assertThat().verifyPaymentMethodEntityHasBeenStored(availablePaymentForCustomer);
    }

    @When("^I update payment details for an identified customer$")
    public void iSubmitSetPaymentDetailsForAnIdentifiedCustomer() throws Throwable {
        paymentMethodHelper.updatePaymentMethod(invalidId);
    }

    @When("^I update payment details for a user not logged in$")
    public void iSubmitSetPaymentDetailsForAUserNotLoggedIn() throws Throwable {
        paymentMethodHelper.updatePaymentMethod(invalidSession);
    }

    @When("^I update payment details for a not matching logged in user and customer requested$")
    public void iSubmitSetPaymentDetailsForANotMatchingLoggedInUserAndCustomerRequested() throws Throwable {
        paymentMethodHelper.updatePaymentMethod(mismatchIdSession);
    }

    @When("^I update payment details with invalid payment reference ID$")
    public void iSubmitSetPaymentDetailsWithInvalidPaymentReferenceID() throws Throwable {
        paymentMethodHelper.updatePaymentMethod(invalidPaymentReferenceId);
    }

    @When("^I update payment details$")
    public void iSubmitSetPaymentDetails() throws Throwable {
        paymentMethodHelper.updatePaymentMethod("");
    }

    @And("^payment method is now as default one$")
    public void paymentMethodIsNowAsDefaultOne() throws Throwable {
        String actualPaymentRef = paymentMethodHelper.getDefaultPaymentMethod();
        paymentMethodHelper.getSavedPaymentMethodService().assertThat().verifyPaymentMethodHasBeenSettedAsDefault(actualPaymentRef);
    }
}
