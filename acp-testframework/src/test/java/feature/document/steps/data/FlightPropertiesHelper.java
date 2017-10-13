package feature.document.steps.data;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.database.hybris.dao.FlightsDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.FlightsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.hybris.easyjet.models.PassengerMix;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.document.steps.CommonSteps;
import feature.document.steps.constants.StepsRegex;
import feature.document.steps.services.createbasketservices.AddFlightSteps;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * FlightPropertiesHelper handle the retrieval of flight properties from the DB.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author rajakm
 */
@ContextConfiguration(classes = TestApplication.class)
public class FlightPropertiesHelper {

    private int infantsConsumed = 0;
    private int infantsOnSeatConsumed = 0;
    private int infantsLimit = 0;
    private int infantsOnSeatLimit = 0;

    @Steps
    private SerenityReporter reporter;

    @Autowired
    private SerenityFacade testData;

    @Autowired
    private FlightsDao flightsDao;

    @Steps
    private CommonSteps commonSteps;
    @Steps
    private AddFlightSteps addFlightSteps;
    @Steps
    private FlightsAssertion flightsAssertion;

    /**
     * Get the infants consumed and infants limit property from the db
     *
     * @throws EasyjetCompromisedException if the property is not defined for the channel used (specified through testData)
     */
    private void getInfantsCountProperties() throws EasyjetCompromisedException {
        HashMap<String, Integer> infantsLimitAndConsumed = new HashMap<>(flightsDao.getInfantsLimitAndConsumed(testData.getData(FLIGHT_KEY)));
        if (!infantsLimitAndConsumed.isEmpty()) {
            infantsLimit = infantsLimitAndConsumed.get("InfantsLimit");
            infantsConsumed = infantsLimitAndConsumed.get("InfantsConsumed");
            infantsOnSeatLimit = infantsLimitAndConsumed.get("InfantsOnSeatLimit");
            infantsOnSeatConsumed = infantsLimitAndConsumed.get("InfantsOnSeatConsumed");
            reporter.info("Infants limit: " + infantsLimit + "; Infants consumed: " + infantsConsumed + "; InfantsOnSeat limit: " + infantsOnSeatLimit + "; InfantsOnSeat consumed: " + infantsOnSeatConsumed);
        } else {
            throw new EasyjetCompromisedException("No infants limit and consumed details available for this flight");
        }
    }

    private void setPassengerMix(String type, int toAdd) throws EasyjetCompromisedException {
        FindFlightsResponse.Flight flight = testData.getData(OUTBOUND_FLIGHT);
        testData.setData(FLIGHT_KEY, flight.getFlightKey());
        getInfantsCountProperties();
        int infantOnSeatAvailability = infantsOnSeatLimit - infantsOnSeatConsumed;
        int infantAvailability = infantsLimit - infantsConsumed;

        int adultCount;
        switch (type) {
            case "infants":
                adultCount = infantAvailability + toAdd;
                int infantsOnLapCount = infantAvailability + toAdd;
                testData.setData(PASSENGER_MIX, (adultCount + " adult;" + infantsOnLapCount + ",0 infant"));
                break;
            case "infantsOnSeat":
                adultCount = infantOnSeatAvailability / 2 + 1;
                int infantsOnSeatCount = infantOnSeatAvailability + toAdd;
                testData.setData(PASSENGER_MIX, (adultCount + " adult;" + infantsOnSeatCount + " infant"));
                break;
        }
        testData.setData(PASSENGERS, new PassengerMix(testData.getData(PASSENGER_MIX)));
    }

    @And("^infants limits and consumed values are stored for the flight$")
    public void getInfantsLimitsAndCosumedForTheFlight() throws EasyjetCompromisedException {
        FindFlightsResponse.Flight flight = testData.getData(OUTBOUND_FLIGHT);
        testData.setData(FLIGHT_KEY, flight.getFlightKey());
        getInfantsCountProperties();
    }

    @When("^I added a flight to the basket" + StepsRegex.JOURNEY + " with more (infants|infantsOnSeat) than allowed(?: with override (true|false))?$")
    public void sendAddFlightRequestForMoreThanAlloweInfants(String journeyType, String type, String overrideWarning) throws EasyjetCompromisedException {
        setPassengerMix(type, 1);
        addFlightSteps.sendAddFlightRequest(journeyType, overrideWarning);
    }

    @But("^the (infants|infantsOnSeat) limit is consumed$")
    public void theInfantTypeLimitIsConsumed(String type) throws EasyjetCompromisedException {
        String currentCookie = HybrisService.theJSessionCookie.get();
        testData.storeTestData();
        HybrisService.theJSessionCookie.set("");
        commonSteps.channelSelection("ADAirport");
        setPassengerMix(type, 0);
        addFlightSteps.sendAddFlightRequest("single", "true");
        testData.restoreTestData();
        HybrisService.theJSessionCookie.set(currentCookie);
    }

    @Then("^the number of (infants|infantsOnSeat) for the flight will be (reserved|released)$")
    public void updateInfantsReservationForTheFlight(String type, String check) throws EasyjetCompromisedException {
        PassengerMix passengers = testData.getData(PASSENGERS);
        int expectedInfantsConsumed = 0;
        int expectedInfantsOwnSeatConsumed = 0;
        if (check.equals("reserved")) {
            expectedInfantsConsumed = infantsConsumed + passengers.getInfantOnLap() + passengers.getInfantOnSeat();
            expectedInfantsOwnSeatConsumed = infantsOnSeatConsumed + passengers.getInfantOnSeat();
        } else if (check.equals("released")) {
            expectedInfantsConsumed = infantsConsumed - passengers.getInfantOnLap() - passengers.getInfantOnSeat();
            expectedInfantsOwnSeatConsumed = infantsOnSeatConsumed - passengers.getInfantOnSeat();
        }
        getInfantsCountProperties();

        if (type.equals("infants"))
            flightsAssertion.verifyInfantsConsumedOnAFlight(infantsConsumed, expectedInfantsConsumed);
        else if (type.equals("infantsOnSeat"))
            flightsAssertion.verifyInfantsConsumedOnAFlight(infantsConsumed, expectedInfantsConsumed);
            flightsAssertion.verifyInfantsConsumedOnAFlight(infantsOnSeatConsumed, expectedInfantsOwnSeatConsumed);
    }

}