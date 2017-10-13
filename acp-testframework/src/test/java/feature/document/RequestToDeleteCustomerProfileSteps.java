package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.DeleteCustomerProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.DeleteCustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by robertadigiorgio on 28/02/2017.
 */

@ContextConfiguration(classes = TestApplication.class)

public class RequestToDeleteCustomerProfileSteps {

    @Autowired
    public BookingHelper bookingHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper commitBookingHelper;
    @Autowired
    private SerenityFacade testData;
    private DeleteCustomerProfileService deleteCustomerProfileService;
    private FlightsService flightsService;
    private CommitBookingService commitBookingService;
    private GetBookingService getBookingService;
    private String customerId;

    @Given("^The customer ID not related to a registered customer$")
    public void theCustomerIDNotRelatedToARegisteredCustomer() throws Throwable {
        customerId = "000000";
    }

    @When("^I send a request to the delete customer profile with \"([^\"]*)\"$")
    public void iSendARequestToTheDeleteCustomerProfileWith(String channel) throws Throwable {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).build();
        deleteCustomerProfileService = serviceFactory.deleteCustomerDetails(new DeleteCustomerProfileRequest(HybrisHeaders.getValid(channel).build(), pathParams));
        deleteCustomerProfileService.invoke();
    }

    @Given("^I create a customer$")
    public void iCreateACustomer() throws Throwable {
        customerHelper.customerAccountExistsWithAKnownPassword();
        customerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
    }


    @Then("^I will return a error message in the channel \"([^\"]*)\"$")
    public void iWillReturnAErrorMessageInTheChannel(String error) throws Throwable {
        deleteCustomerProfileService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @And("^I sent a request to SearchFlights \"([^\"]*)\"$")
    public void iSentARequestToSearchFlights(String channel) throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), "1 adult", testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
    }

    @And("^I sent a request to AddFlights \"([^\"]*)\"$")
    public void iSentARequestToAddFlights(String channel) throws Throwable {
        basketHelper.addFlightToBasketAsChannelUsingFlightCurrency(
                flightsService.getOutboundFlight(), channel, flightsService.getResponse().getCurrency());
    }

    @And("^I sent a request to Commit Booking \"([^\"]*)\"$")
    public void iSentARequestToCommitBooking(String channel) throws Throwable {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), channel);
        CommitBookingRequest commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse(), customerId, channel, true);
        commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
    }

    @Then("^I will delete the Customer Profile APIS details, SSR details, Saved Payments, Saved Passengers, associated with the Profile$")
    public void iWillDeleteTheCustomerProfileAPISDetailsSSRDetailsSavedPaymentsSavedPassengersAssociatedWithTheProfile() throws Throwable {

        Integer paimentInfos = customerDao.getCustomersPaymentInfoWithId(customerId);
        deleteCustomerProfileService.assertThat().fieldIsEmpty(paimentInfos);

        Integer traveller = customerDao.getCustomersTravellerWithId(customerId);
        deleteCustomerProfileService.assertThat().fieldIsEmpty(traveller);

        Integer apisDetatil = customerDao.getCustomerAPISDetailWithId(customerId);
        deleteCustomerProfileService.assertThat().fieldIsEmpty(apisDetatil);

        String ssrDetail = customerDao.getCustomerSSRWithId(customerId).get(0);
        deleteCustomerProfileService.assertThat().fieldIsEmpty(ssrDetail);
    }

    @And("^change the Customer profile status to Deleted$")
    public void changeTheCustomerProfileStatusToDeleted() throws Throwable {
        String statusCustomer = customerDao.getStatusOfcustomer(customerId);
        deleteCustomerProfileService.assertThat().statusOfCustomerIsDeleted(statusCustomer);
    }

    @And("^The booking is still available \"([^\"]*)\"$")
    public void theBookingIsStillAvailable(String channel) throws Throwable {
        BookingPathParams params = BookingPathParams.builder().bookingId(commitBookingService.getResponse().getConfirmation().getBookingReference()).build();
        getBookingService = serviceFactory.getBookings(new GetBookingRequest(HybrisHeaders.getValid(channel).build(), params));
        getBookingService.invoke();
    }
}
