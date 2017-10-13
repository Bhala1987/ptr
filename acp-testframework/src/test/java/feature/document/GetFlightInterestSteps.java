package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.FlightInterestConstants;
import com.hybris.easyjet.database.hybris.models.FlightInterestModel;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightInterestHelper;
import com.hybris.easyjet.fixture.hybris.helpers.StaffMembertoCustomerProfileAssociationHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest.FlightInterestRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ManageFlightInterestRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetFlightInterestService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.GET_FLIGHT_INTEREST;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory.getRandomEmail;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by robertadigiorgio on 01/08/2017.
 */

@ContextConfiguration(classes = TestApplication.class)
public class GetFlightInterestSteps {


    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    FlightInterestHelper flightInterestHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffMembertoCustomerProfileAssociationHelper;
    private GetFlightInterestService getFlightInterestService;

    @When("^I send an getFlightInterest with (.*), (.*)$")
    public void iSendAnGetFlightInterestWithPassengerAndFareTypeFareTypeAndParameterLogin(String parameter, String login) throws Throwable {

        customerHelper.createNewCustomerProfileWithEmail(getRandomEmail(10));
        String customerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromId(customerId, false);
        if (login.equals("with Login")) {
            customerHelper.loginWithValidCredentials();
        }
        List<HybrisFlightDbModel> flightsToAdd = flightInterestHelper.getNValidFlights(1);
        List<FlightInterestModel> flightInterestsBefore = flightInterestHelper.getSavedFlightInterestsFor(customerId);
        Map<String, List<String>> flightInterestsToAdd = flightInterestHelper.creatMapOfFlighInterestsToAdd(flightsToAdd, Collections.singletonList(FlightInterestConstants.STAFF_FARE));
        flightInterestHelper.addFlightWithMultipleInterests(flightInterestsToAdd, customerId, FlightInterestConstants.DIGITAL_CHANNEL);

        flightInterestHelper.getFlightInterestService().wasSuccessful();
        List<FlightInterestModel> flightInterestsAfter = flightInterestHelper.getSavedFlightInterestsFor(customerId);

        if (parameter.equalsIgnoreCase("invalid parameter")) {
            customerId = "INVALID_CUSTOMER";
        }

        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(GET_FLIGHT_INTEREST).build();
        getFlightInterestService = serviceFactory.getFlightInterest(new ManageFlightInterestRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), params));
        testData.setData(SERVICE, getFlightInterestService);
        getFlightInterestService.invoke();
    }

    @Then("^I receive the interest Flight$")
    public void iReceiveTheInterestFlight() throws Throwable {
         getFlightInterestService.assertThat().checkThatFlightWasAdded((FlightInterestRequestBody) testData.getData("AddFlightInterestRequestBody"));
    }

    @Then("^I receive an error$")
    public void iReceiveAnError() throws Throwable {
        assertThat(getFlightInterestService.getStatusCode()).isEqualTo(403);
    }
}
