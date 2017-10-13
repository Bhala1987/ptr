package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.PaymentModeDao;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetRefundablePaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetRefundablePaymentMethodsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.REFUNDABLE_PAYMENT_METHODS;


/**
 * Created by aspiggia on 19/07/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class GetRefundablePaymentMethodsSteps {

   @Autowired
   private SerenityFacade testData;
   @Autowired
   private HybrisServiceFactory serviceFactory;
   @Autowired
   private PaymentModeDao paymentModeDao;
   private GetRefundablePaymentMethodsService getRefundablePaymentMethodsService;


   @Given("^that the booking reference in the request can not be identified$")
   public void thatTheBookingReferenceInTheRequestCanNotBeIdentified() throws Throwable {
      testData.setData(BOOKING_ID, "INVALID_BOOKING_ID");
   }

   @When("^the ([^\"]*) channel initiates a getRefundPaymentMethods request$")
   public void theHasInitiatedAGetRefundPaymentMethodsRequest(String channel) throws Throwable {
      GetRefundablePaymentMethodsRequest getRefundablePaymentMethodsRequest = new GetRefundablePaymentMethodsRequest(
            HybrisHeaders.getValid(
                  channel
            ).build(),
            BookingPathParams.builder()
                    .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                  .path(REFUNDABLE_PAYMENT_METHODS)
                  .build()
      );

      getRefundablePaymentMethodsService = serviceFactory.getRefundablePaymentMethodsService(getRefundablePaymentMethodsRequest);

      testData.setData(SERVICE, getRefundablePaymentMethodsService);

      getRefundablePaymentMethodsService.invoke();
   }


   @Then("^I will return a list of payment methods only including ([^\"]*)$")
   public void iWillReturnAListOfPaymentMethodsIncludingOnly(String paymentMethod) throws Throwable {
      getRefundablePaymentMethodsService.assertThat().isIncludedInPaymentMethods(paymentMethod).areIncludedOnlyNMethods(1);
   }

   @And("^I will not return the ([^\"]*)$")
   public void iWillNotReturnTheOriginalPaymentMethodCode(String paymentMethod) throws Throwable {
      getRefundablePaymentMethodsService.assertThat().isNotIncludedInPaymentMethods(paymentMethod);
   }

   @Then("^I will return a list of payment methods including ([^\"]*)$")
   public void iWillReturnAListOfPaymentMethodsIncluding(String paymentMethod) throws Throwable {
      getRefundablePaymentMethodsService.assertThat().isIncludedInPaymentMethods(paymentMethod);
   }

   @And("^the list of payment methods includes all the configured methods$")
   public void iWillReturnAListOfPaymentMethodsIncludingTheConfigured() throws Throwable {
      List<String> paymentMethods = paymentModeDao.getRefundPaymentMethods();
      //removing special value opm
      paymentMethods.remove("opm");
      getRefundablePaymentMethodsService.assertThat().areIncludedConfiguredPaymentMethods(paymentMethods);
   }

   @And("^the list of payment methods includes all the configured methods including opm$")
   public void iWillReturnAListOfPaymentMethodsIncludingTheConfiguredIncludingOpm() throws Throwable {
      getRefundablePaymentMethodsService.assertThat().areIncludedConfiguredPaymentMethods(paymentModeDao.getRefundPaymentMethods());
   }

}
