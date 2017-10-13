package feature.document.steps.services.productmanagementservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.AirportsDao;
import com.hybris.easyjet.fixture.hybris.asserters.AirportsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.AirportsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.AirportsService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.HEADERS;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = TestApplication.class)
public class GetAirportsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private AirportsDao airportsDao;

    @Steps
    private AirportsAssertion airportsAssertion;

    private AirportsService airportsService;
    private List<String> activeAirports;
    private List<String> inactiveAirports;

    private void invokeGetFlightService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        airportsService = serviceFactory.getAirports(new AirportsRequest(headers.build()));
        testData.setData(SERVICE, airportsService);
        airportsService.invoke();
    }

    @Given("^at least on airport is defined$")
    public void getAirportsFromDb() {
        activeAirports = airportsDao.getAirportsThatAreActive(true);
        inactiveAirports = airportsDao.getAirportsThatAreActive(false);

        assertThat(activeAirports.size())
                .withFailMessage("No active airport in the database")
                .isGreaterThan(0);
    }

    @When("^I send the getAirports request$")
    public void sendGetAirportsRequest() {
        invokeGetFlightService();
        airportsAssertion.setResponse(airportsService.getResponse());
    }

    @Then("^all the airports have a timezone specified$")
    public void allTheAirportsHaveATimezoneSpecified() {
        airportsAssertion
                .allTheAirportsHaveATimeZone();
    }

    @Then("^a list of airports is returned$")
    public void aListOfAirportsIsReturned() {
        airportsAssertion
                .allTheActiveAirportsAreReturned(activeAirports)
                .noneInactiveAirportsAreReturned(inactiveAirports)
                .allTheAirportsHaveACountry()
                .allTheAirportsHaveACurrency()
                .allTheAirportsHaveAOnlineCheckInAvailability()
                .allTheAirportsHaveAMobileCheckInAvailability()
                .allTheAirportsHaveALocalizedName();
    }
}