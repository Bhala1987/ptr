package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.AlternateAirportQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetAlternateAirportsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetAlternateAirportsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by robertadigiorgio on 10/03/2017.
 */

@ContextConfiguration(classes = TestApplication.class)

public class AlternativeAirportsSteps {
    private GetAlternateAirportsService getAlternateAirportsService;

    @Autowired
    private HybrisServiceFactory serviceFactory;

    @When("^I send a request to alternate airports with the parameter \"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\"$")
    public void iSendARequestToAlternateAirportsWithTheParameter(String departure, String destination, String distance) throws Throwable {
        AlternateAirportQueryParams alternateAirportParams = AlternateAirportQueryParams.builder().departureCode(departure).destinationCode(destination).maxDistance(distance).build();
        getAlternateAirportsService = serviceFactory.getAlternateAirports(new GetAlternateAirportsRequest(HybrisHeaders.getValid("Digital").build(), alternateAirportParams));
        getAlternateAirportsService.invoke();
    }

    @Then("^I will return a error message on the channel \"([^\"]*)\"$")
    public void iWillReturnAErrorMessageOnTheChannel(String code) throws Throwable {
        getAlternateAirportsService.assertThatErrors().containedTheCorrectErrorMessage(code);
    }

    @Then("^distance between new departure airport and chosen departure is less than or equal 100 km$")
    public void distanceBetweenAirports() throws Throwable {
        getAlternateAirportsService.assertThat().theDistanceIsLessOfMaxDistance();
    }

    @And("^the list of departure airports sorted by distance ascending$")
    public void iWillReturnTheListOfDepartureAirportSortedByDistanceAscending() throws Throwable {
        getAlternateAirportsService.assertThat().theDistanceIsSorted();
    }

    @Then("^I will return an empty list of departure airports$")
    public void iWillReturnAnEmptyListOfDepartureAirports() throws Throwable {
        getAlternateAirportsService.assertThat().theAiportMustBeEmpty();
    }

    @Then("^the list of all alternative departure airports sorted alphabetically$")
    public void iWillAReturnAListOfAllAlternateDepartureAirportSortedAlphabetically() throws Throwable {
        getAlternateAirportsService.assertThat().theAirportAreSortedAlphabetically();
    }
}