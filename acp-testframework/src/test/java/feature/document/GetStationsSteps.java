package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.CarHireHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.StationsForCarHireQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.StationsForCarHireRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.StationsForCarHireService;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by bhala.
 */

@ContextConfiguration(classes = TestApplication.class)
public class GetStationsSteps {

    @Autowired
    private HybrisServiceFactory serviceFactory;
    private StationsForCarHireService stationsForCarHireService;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private CarHireHelper carHireHelper;

    @When("^I call the getStations service for that particular country (.*)$")
    public void iCallTheGetStationsServiceForThatParticularCountryCode(String code) throws Throwable {
        stationsForCarHireService = serviceFactory.getStations(new StationsForCarHireRequest(HybrisHeaders.getValid(testData.getData(SerenityFacade.DataKeys.CHANNEL)).build(), StationsForCarHireQueryParams.builder().countryCode(code).build()));
        stationsForCarHireService.invoke();
        testData.setData(SerenityFacade.DataKeys.SERVICE, stationsForCarHireService);
    }

    @Then("^the stations for car hire purpose are returned$")
    public void theStationsForCarHirePurposeAreReturned() throws Throwable {
        stationsForCarHireService.assertThat().stationsForCarHireReturned();
    }

    @When("^I call the findCars service with drop off location is not same country as pick up location$")
    public void iCallTheFindCarsServiceWithDropOffLocationIsNotSameCountryAsPickUpLocation() throws Throwable {
        stationsForCarHireService = serviceFactory.getStations(new StationsForCarHireRequest(HybrisHeaders.getValid(testData.getData(SerenityFacade.DataKeys.CHANNEL)).build(), StationsForCarHireQueryParams.builder().countryCode("GBR").build()));
        stationsForCarHireService.invoke();
        String pickUpStation = stationsForCarHireService.getResponse().getStations().get(0).getStationCode();

        stationsForCarHireService = serviceFactory.getStations(new StationsForCarHireRequest(HybrisHeaders.getValid(testData.getData(SerenityFacade.DataKeys.CHANNEL)).build(), StationsForCarHireQueryParams.builder().countryCode("FRA").build()));
        stationsForCarHireService.invoke();
        String dropOffStation = stationsForCarHireService.getResponse().getStations().get(0).getStationCode();

        carHireHelper.getCarHireQuotes(pickUpStation, dropOffStation);
    }
}
