package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.PaymentModeDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.PaymentMethodsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.PaymentMethodsService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.WaitHelper.pause;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;

/**
 * Created by prite on 11/11/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetPaymentMethodsToChannelSteps {
    protected static Logger LOG = LogManager.getLogger(GetPaymentMethodsToChannelSteps.class);

    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private CustomerProfileHelper customerProfileHelper;

    @Autowired
    private BasketHelper basketHelper;

    @Autowired
    private TravellerHelper travellerHelper;

    @Autowired
    private PaymentMethodHelper paymentMethodHelper;

    @Autowired
    private CustomerHelper customerHelper;

    @Autowired
    private PaymentModeDao paymentModeDao;

    @Autowired
    private SerenityFacade testData;

    private CustomerProfileService customerProfileService;

    private CustomerModel dbCustomer;

    private PaymentMethodsService paymentMethodsService;

    @When("^I call the getPaymentMethods service with missing \"([^\"]*)\" for \"([^\"]*)\"$")
    public void iCallTheGetPaymentMethodsServiceWithMissingParameter(String parameter, String channel) throws Throwable {
        PaymentMethodsQueryParams paymentMethodsQueryParams = PaymentMethodsQueryParams.builder()
                .basketId(basketHelper.getBasketService().getResponse().getBasket().getCode())
                .bookingTypeCode("STANDARD_CUSTOMER")
                .customerId(dbCustomer.getCustomerid())
                .build();

        if (parameter.equals("CustomerId")) {
            paymentMethodsQueryParams.setCustomerId(null);
        }

        if (parameter.equals("BasketId")) {
            paymentMethodsQueryParams.setBasketId(null);
        }

        paymentMethodsService = serviceFactory.getPaymentMethods(new PaymentMethodsRequest(HybrisHeaders.getValid(channel).build(), paymentMethodsQueryParams));
        paymentMethodsService.invoke();
    }

    @When("^I call the service to retrieve payment methods for \"([^\"]*)\"$")
    public void iCallTheServiceToRetrievePaymentMethodsFor(String channel) throws Throwable {
        pause();

        PaymentMethodsQueryParams paymentMethodsQueryParams = PaymentMethodsQueryParams.builder()
                .basketId(basketHelper.getBasketService().getResponse().getBasket().getCode())
                .bookingTypeCode("STANDARD_CUSTOMER")
                .customerId(dbCustomer.getUid())
                .build();

        paymentMethodsService = serviceFactory.getPaymentMethods(
                new PaymentMethodsRequest(
                        HybrisHeaders.getValid(channel).build(), paymentMethodsQueryParams
                )
        );

        paymentMethodsService.invoke();
    }

    @Then("^the applicable payment methods are returned for \"([^\"]*)\"$")
    public void theApplicablePaymentMethodsAreReturnedFor(String channel) throws Throwable {
        if (paymentModeDao.getChannelsForPaymentMethod("voucher").contains(channel)) {
            paymentMethodsService.assertThat().paymentMethodsReturnedContainVoucher();
        }

        paymentMethodsService.assertThat().paymentMethodsWereReturned();
    }

    @Given("^I have a valid basket with passengers and associated customer created via \"([^\"]*)\"$")
    public void iHaveAValidBasketWithPassengersAndAssociatedCustomerCreatedVia(String channel) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix("1 Adult", channel, "Standard", false);

        Passengers request = travellerHelper.createValidRequestToAddAllPassengersForBasket(
                basketHelper.getBasketService().getResponse()
        );

        basketHelper.updatePassengersForChannel(
                request,
                channel,
                basketHelper.getBasketService().getResponse().getBasket().getCode()
        );

        customerHelper.createRandomCustomer(channel);
        dbCustomer = customerProfileHelper.getCustomerById(testData.getData(CUSTOMER_ID));
        CustomerPathParams profilePathParams = CustomerPathParams.builder()
                .customerId(dbCustomer.getUid())
                .path(PROFILE)
                .build();

        customerProfileService = serviceFactory.getCustomerProfile(
                new ProfileRequest(HybrisHeaders.getValid(channel).build(), profilePathParams)
        );

        customerProfileService.invoke();
    }

    @When("^retrieve saved payment methods customer not logged In \"([^\"]*)\"$")
    public void retrieveSavedPaymentMethodsCustomerNotLoggedIn(String customerLogon) throws Throwable {
        paymentMethodHelper.getSavedPaymentToChannel(customerLogon);
    }

    @Then("^saved payment response return errorcode \"([^\"]*)\"$")
    public void savedPaymentResponseReturnErrorcode(String errorcode) throws Throwable {
        paymentMethodHelper.getPaymentMethodTypeService().assertThatErrors().containedTheCorrectErrorMessage(errorcode);
    }

    @When("^retrieve saved payment methods with different customer details \"([^\"]*)\"$")
    public void retrieveSavedPaymentMethodsWithDifferentCustomerDetails(String customerLogon) throws Throwable {
        paymentMethodHelper.getSavedPaymentToChannel(customerLogon);
    }

    @When("^retrieve saved payment methods with valid customer logon \"([^\"]*)\"$")
    public void retrieveSavedPaymentMethodsWithValidCustomerLogon(String customerLogin) throws Throwable {
        paymentMethodHelper.getSavedPaymentToChannel(customerLogin);
    }

    @Then("^I retrieve saved payment for the (.*)$")
    public void iRetrieveSavedPaymentForTheChannel(String type) throws Throwable {
        paymentMethodHelper.getSavedPaymentToChannel(testData.getData(CUSTOMER_ID));
    }

    @When("^retrieve saved payment methods from my customer profile$")
    public void retrieveSavedPaymentMethodsFromMyCustomerProfile() throws Throwable {
        customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
    }

    @Then("^response contains the (.*) details$")
    public void responseContainsTheDebitcardDetails(String typeOfCard) throws Throwable {
        switch (typeOfCard) {
            case "debitcard":
                customerProfileService = testData.getData(SERVICE);
                customerProfileService.assertThat().paymentMethodIsPopulated();
                break;
            case "nocard":
                paymentMethodHelper.getPaymentMethodTypeService().assertThat().verifyExpirediSNotPopulated();
                break;
            default:
                paymentMethodHelper.getPaymentMethodTypeService().assertThat().verifyDebitCardDetailsiSPopulated();
        }
    }

    @When("^I retrieve expired card payment for the channel$")
    public void iRetrieveExpiredCardPaymentForTheChannel() throws Throwable {
        paymentMethodHelper.getSavedPaymentsForExpiredCards(testData.getData(CUSTOMER_ID));
    }

    @Given("^I am using the '(.*)'$")
    public void iAmUsingThePaymentType(String paymentType) {
        testData.setPaymentCode(paymentType);
    }
}
