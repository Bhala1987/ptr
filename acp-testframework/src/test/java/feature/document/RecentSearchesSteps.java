package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.DeleteRecentSearchesQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.DeleteRecentSearchesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.SaveRecentSearchRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.DeleteRecentSearchesService;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetRecentSearchesService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.SEARCHES;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory.getRandomEmail;


/**
 * Created by albertowork on 7/6/17.
 */
@DirtiesContext
@ContextConfiguration(classes = TestApplication.class)
public class RecentSearchesSteps {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private FlightHelper flightHelper;
    private FlightsService flightsService;
    private DeleteRecentSearchesService deleteRecentSearchesService;
    private GetRecentSearchesService getRecentSearchesService;
    private String customerId;
    private String wrongCustomerId;
    private CustomerPathParams pathParams;
    private DeleteRecentSearchesQueryParams params;

    @Given("^the channel has initiated a request to clear recent search$")
    public void theChannelHasInitiatedARequestToClearRecentSearch() throws Throwable {
        testData.setChannel("ADAirport");
        wrongCustomerId = "customer55555";
    }

    @When("^the customer cannot be identify in the request$")
    public void theCustomerCannotBeIdentifyInTheRequest() throws Throwable {
        pathParams = CustomerPathParams.builder()
                .customerId(wrongCustomerId).path(CustomerPathParams.CustomerPaths.SEARCHES).build();
        deleteRecentSearchesService = serviceFactory.getRecentSearchesService(
                new DeleteRecentSearchesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, null));
        deleteRecentSearchesService.invoke();
    }

    @Then("^I will receive a error login customer message (SVC_\\d+_\\d+)$")
    public void iWillReceiveAErrorLoginCustomerMessageSVC__(String errorCode) throws Throwable {
        deleteRecentSearchesService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }


    @Given("^customer has logged in$")
    public void customerHasLoggedIn() throws Throwable {
        testData.setChannel("Digital");
        customerHelper.createNewCustomerProfileWithEmail(getRandomEmail(10));
        customerHelper.loginWithValidCredentials();
        this.customerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
    }

    @And("^the channel has initiated a request to clear specific recent search$")
    public void theChannelHasInitiatedARequestToClearSpecificRecentSearch() throws Throwable {
        pathParams = CustomerPathParams.builder()
                .customerId(customerId).path(CustomerPathParams.CustomerPaths.SEARCHES).build();
        params = DeleteRecentSearchesQueryParams.builder().searchIndexList("5").build();
    }

    @When("^the recent search not been identified$")
    public void TheRecentSearchNotBeenIdentified() throws Throwable {
        deleteRecentSearchesService = serviceFactory.getRecentSearchesService(
                new DeleteRecentSearchesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, params));
        deleteRecentSearchesService.invoke();
    }

    @Then("^I will receive a error customer message (SVC_\\d+_\\d+)$")
    public void iWillReceiveAErrorCustomerMessageSVC__(String errorCode) throws Throwable {
       deleteRecentSearchesService.assertThat().verifyInformationInAffectedData(errorCode);
    }

    @And("^the customer search more than one flight$")
    public void theCustomerSearchMoreThanOneFlight() throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), "1 Adult",
                testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        flightsService = flightHelper.getFlights(testData.getChannel(), "2 Adult",
                testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());

        pathParams = CustomerPathParams.builder().customerId(customerId).path(SEARCHES).build();
        getRecentSearchesService = serviceFactory.getRecentSearch(new SaveRecentSearchRequest(HybrisHeaders.getValid(testData.getChannel())
                .build(), pathParams));
        getRecentSearchesService.invoke();
        getRecentSearchesService.getResponse();
    }

    @When("^the channel send a request to clear specific recent search$")
    public void theChannelSendARequestToClearSpecificRecentSearch() throws Throwable {
        pathParams = CustomerPathParams.builder()
                .customerId(customerId).path(CustomerPathParams.CustomerPaths.SEARCHES).build();
        params = DeleteRecentSearchesQueryParams.builder().searchIndexList("1").build();
        deleteRecentSearchesService = serviceFactory.getRecentSearchesService(
                new DeleteRecentSearchesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, params));
        deleteRecentSearchesService.invoke();
    }

    @Then("^The recent search will be removed from the customer profile$")
    public void theRecentSearchWillBeRemovedFromTheCustomerProfile() throws Throwable {
        pathParams = CustomerPathParams.builder().customerId(customerId).path(SEARCHES).build();
        getRecentSearchesService = serviceFactory.getRecentSearch(new SaveRecentSearchRequest(HybrisHeaders.getValid(testData.getChannel())
                .build(), pathParams));
        getRecentSearchesService.invoke();
        getRecentSearchesService.getResponse();
        getRecentSearchesService.assertThat().totalRecentSearchesShouldBe(1);
    }

    @And("^the customer has more than recent search$")
    public void theCustomerHasMoreThanRecentSearch() throws Throwable {
        theCustomerSearchMoreThanOneFlight();
    }

    @When("^the channel has initiated a request to clear all recent searches$")
    public void theChannelHasInitiatedARequestToClearAllRecentSearches() throws Throwable {
        pathParams = CustomerPathParams.builder()
                .customerId(customerId).path(CustomerPathParams.CustomerPaths.SEARCHES).build();
        deleteRecentSearchesService = serviceFactory.getRecentSearchesService(
                new DeleteRecentSearchesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, null));
        deleteRecentSearchesService.invoke();
    }


    @Then("^The recent searches will be removed from the customer profile$")
    public void theRecentSearchesWillBeRemovedFromTheCustomerProfile() throws Throwable {
        pathParams = CustomerPathParams.builder().customerId(customerId).path(SEARCHES).build();
        getRecentSearchesService = serviceFactory.getRecentSearch(new SaveRecentSearchRequest(HybrisHeaders.getValid(testData.getChannel())
                .build(), pathParams));
        getRecentSearchesService.invoke();
        getRecentSearchesService.getResponse();
        getRecentSearchesService.assertThat().totalRecentSearchesShouldBe(0);
    }
}
