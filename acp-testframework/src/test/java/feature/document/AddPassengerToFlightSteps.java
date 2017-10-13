package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.AmendableBasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.ManageBookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AddPassengerToFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddPassengerToFlightRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation.AffectedData;
import com.hybris.easyjet.fixture.hybris.invoke.services.AddPassengerToFlightService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketTravellerService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BUNDLE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CHANNEL;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.ADD_PASSENGER_TO_FLIGHT;
import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jamie on 06/07/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddPassengerToFlightSteps {
    protected static Logger LOG = LogManager.getLogger(AddPassengerToFlightSteps.class);

    @Autowired
    private AmendableBasketHelper amendableBasketHelper;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private ManageBookingHelper manageBookingHelper;
    @Autowired
    private AddpassengerToFlightHelper addpassengerToFlightHelper;
    @Autowired
    private TravellerHelper travellerHelper;
    AddPassengerToFlightService addPassengerService;
    private BasketTravellerService basketTravellerService;
    private String INCORRECT_FLIGHT_KEY = "ABC123";
    private String INCORRECT_BASKET_CODE = "000";
    private String SINGLE = "single";
    private String NEW_PASSENGER_CODE = "newPassengerCode";

    private void addPassengerToFlight(List<String> aFlightKey, String aBasketCode, String aBundleCode) {
        addPassengerToFlight(aFlightKey, aBasketCode, aBundleCode, null, null, null);
    }

    private void adddInfantToFlight(List<String> aFlightKey, String aBasketCode, String aBundleCode, String aPassengerType, String overideLimits, String responsibleAdultPassengerCode) {
        addPassengerToFlight(aFlightKey, aBasketCode, aBundleCode, aPassengerType, overideLimits, responsibleAdultPassengerCode);
    }


    private void addPassengerToFlight(List<String> aFlightKey, String aBasketCode, String aBundleCode, String aPassengerType, String aOverrideLimits, String responsibleAdultPassengerCode) {
        AddPassengerToFlightRequestBody requestBody = AddPassengerToFlightRequestBody.builder().flightKeys(aFlightKey).bundleCode(aBundleCode).overrideLimits(aOverrideLimits).passengerType(aPassengerType).responsibleAdultPassengerCode(responsibleAdultPassengerCode).build();
        BasketPathParams pathParams = BasketPathParams.builder().basketId(aBasketCode).path(ADD_PASSENGER_TO_FLIGHT).build();
        addPassengerService = serviceFactory.getAddPassengerToFlight(new AddPassengerToFlightRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, requestBody));
        addPassengerService.invoke();

        testData.setData(SERVICE, addPassengerService);

        if (!ofNullable(addPassengerService.getErrors()).isPresent()) {
            List<String> newPaxCodes = addPassengerService.getResponse().getAdditionalInformations().stream()
                    .flatMap(ad -> ad.getAffectedData().stream())
                    .filter(d -> d.getDataName().equalsIgnoreCase(NEW_PASSENGER_CODE))
                    .map(AffectedData::getDataValue)
                    .collect(Collectors.toList());

            testData.setData(SerenityFacade.DataKeys.PASSENGER_CODES, newPaxCodes);
        }
    }

    @When("^I attempt to add a passenger to the flight with an invalid flight id$")
    public void iAttemptToAddAPassengerToTheFlightWithAnInvalidFlightId() throws Throwable {
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));
        testData.setData(BUNDLE, testData.getData(SerenityFacade.DataKeys.FARE_TYPE));
        List<String> flightKeys = new ArrayList<>(Arrays.asList(INCORRECT_FLIGHT_KEY));
        addPassengerToFlight(flightKeys, testData.getBasketId(), testData.getData(BUNDLE));
    }

    @When("^I attempt to add a passenger to the flight with an invalid basket id$")
    public void iAttemptToAddAPassengerToTheFlightWithAnInvalidBasketId() throws Throwable {
        testData.setFlightKey(testData.getData(SerenityFacade.DataKeys.FLIGHT_KEY));
        testData.setData(BUNDLE, testData.getData(SerenityFacade.DataKeys.FARE_TYPE));
        List<String> flightKeys = new ArrayList<>(Arrays.asList(testData.getFlightKey()));
        addPassengerToFlight(flightKeys, INCORRECT_BASKET_CODE, testData.getData(BUNDLE));
    }

    @When("^I attempt to add a passenger to the flight with a bundle not in the basket$")
    public void iAttemptToAddAPassengerToTheFlightWithABundleNotInTheBasket() throws Throwable {
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));
        testData.setFlightKey(testData.getData(SerenityFacade.DataKeys.FLIGHT_KEY));
        testData.setData(BUNDLE, testData.getData(SerenityFacade.DataKeys.FARE_TYPE));

        String bundleNotInBasket = Arrays.stream(new String[]{"Standard", "Flexi"}).filter(bundle -> !bundle.equals(testData.getData(BUNDLE))).findFirst().get();
        List<String> flightKeys = new ArrayList<>(Arrays.asList(testData.getFlightKey()));
        addPassengerToFlight(flightKeys, testData.getBasketId(), bundleNotInBasket);
    }

    @Then("^the add passenger service returns a warning with code '(.*)'$")
    public void theAddPassengerServiceReturnsAWarningWithCode(String aWarningCode) throws Throwable {
        addPassengerService.assertThat().additionalInformationContains(aWarningCode);
    }

    @When("^I send a request to Add a passenger for ([^\"]*) flight$")
    public void iSendARequestToAddAPassenger(String criteria) throws EasyjetCompromisedException {
        List<String> flightKeys;
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));
        testData.setFlightKey(testData.getData(SerenityFacade.DataKeys.FLIGHT_KEY));
        testData.setData(BUNDLE, testData.getData(SerenityFacade.DataKeys.FARE_TYPE));
        if (criteria.equalsIgnoreCase(SINGLE)) {
            flightKeys = new ArrayList<>(Arrays.asList(testData.getFlightKey()));
        } else {
            flightKeys = manageBookingHelper.getAllFlightKeysFromTheBasket();
        }
        addPassengerToFlight(flightKeys, testData.getBasketId(), testData.getData(BUNDLE));
    }

    @And("^the passenger is added to ([^\"]*) flight in the basket$")
    public void thePassengerIsAddedToTheFlightInTheBasket(String criteria) {
        List<String> expectedPassengerCodes = testData.getData(SerenityFacade.DataKeys.PASSENGER_CODES);
        String flightKey = testData.getData(SerenityFacade.DataKeys.FLIGHT_KEY);
        if (criteria.equalsIgnoreCase(SINGLE)) {
            manageBookingHelper.verifyTheNewPassengersAreAdded(flightKey, expectedPassengerCodes);
        } else {
            manageBookingHelper.verifyTheNewPassengersAreAdded(expectedPassengerCodes);
        }
    }

    @And("^add a ([^\"]*) bundle for the new passenger in the basket$")
    public void addAFaretypeBundleToTheBasket(String fareType) throws Throwable {
        List<String> expectedPassengerCodes = testData.getData(SerenityFacade.DataKeys.PASSENGER_CODES);
        manageBookingHelper.verifyTheBundleForNewPassengersAdded(expectedPassengerCodes, fareType);
    }

    @And("^all the calculation in the basket are right after adding passenger$")
    public void allTheCalculationInTheBasketAreRightAfterAddPassenger() throws Throwable {
        manageBookingHelper.verifyBasketTotalAfterAmendBooking();
    }

    @When("^I attempt to add an infant passenger to the flight in the basket$")
    public void iAttemptToAddAnInfantPassengerToTheFlightInTheBasket() throws Throwable {
        String basketId = testData.getData(SerenityFacade.DataKeys.BASKET_ID);
        FindFlightsResponse.Flight outBoundFlight = testData.getData(SerenityFacade.DataKeys.OUTBOUND_FLIGHT);
        testData.setBasketId(basketId);
        testData.setData(BUNDLE, testData.getData(SerenityFacade.DataKeys.FARE_TYPE));
        addPassengerToFlight(Arrays.asList(outBoundFlight.getFlightKey()), testData.getBasketId(), testData.getData(BUNDLE), "infant", null, null);
    }

    @And("^I attempt to add an infant passenger to the first flight in the basket$")
    public void iAttemptToAddAnInfantPassengerToTheFirstFlightInTheBasket() throws Throwable {
        amendableBasketHelper
                .generateParamsToAddInfantOnLapBasedOnPassenger("valid")
                .generateValidRequestBodyToAddInfantOnLap()
                .invokeAddInfantOnLap();

        basketHelper.getBasketResponse(testData.getBasketId(), "Digital");

    }

    @Then("^I see recalculated basket totals$")
    public void iSeeRecalculatedBasketTotals() throws Throwable {
        addPassengerService.getResponse();
        manageBookingHelper.verifyBasketTotalsMoreAfterAddingInfant();
    }

    @And("^Add (\\d+) infant on seat to flight and associate to the select passenger$")
    public void addAnInfantOnSeatToFlightAndAssociateToTheFirstPassenger(int noOfInfant) throws Throwable {
        for (int x = 0; x < noOfInfant; x++) {
            addPassengerToFlight(Arrays.asList(testData.getData(SerenityFacade.DataKeys.FLIGHT_KEY).toString()), testData.getData(SerenityFacade.DataKeys.BASKET_ID), "Standard", "infant", "true", testData.getPassengerId());
        }
        basketHelper.getBasket(testData.getData(SerenityFacade.DataKeys.BASKET_ID));
    }

    @Then("^the add passenger to flight service returns (.*)$")
    public void theAddPassengerToFlightServiceReturnsSVC(String error) throws Throwable {
        addPassengerService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I add a passenger (.*) and (.*) fare to all flights$")
    public void iAddAPassengerPassengerMixAndFareTypeFareToAllFlights(String passengerMix, String fareType) throws Throwable {

        FlightPassengers passengers = new FlightPassengers(passengerMix);

        int n = passengers.getTotalNumberOfPassengers();

        List<String> flightKeys = manageBookingHelper.getAllFlightKeysFromTheBasket();

        for (int i = 0; i < n; i ++) {
            addpassengerToFlightHelper.addPassengerToFLight(testData.getBasketId(), fareType, flightKeys);
            addPassengerService = testData.getData(SERVICE);

            assertThat(addPassengerService.getResponse().getAdditionalInformations().stream().allMatch(code -> code.getCode().equalsIgnoreCase("SVC_100524_1001"))).withFailMessage("Passenger is not successfully added to flight.").isEqualTo(TRUE);
            assertThat(addPassengerService.getResponse().getOperationConfirmation().getBasketCode().equalsIgnoreCase(testData.getBasketId())).withFailMessage("Passenger is not successfully added to flight.").isEqualTo(TRUE);
        }

        basketHelper.getBasket(testData.getBasketId());
        BasketService basketService = basketHelper.getBasketService();

        basketHelper.updatePassengersForChannel(travellerHelper.createValidRequestToAddAllPassengersForBasket(basketService.getResponse()), testData.getData(CHANNEL), basketService.getResponse()
                .getBasket()
                .getCode());

        if (passengerMix.contains("infant")) {
            for (int j = 0; j < basketService.getResponse().getBasket().getOutbounds().size(); j ++) {
                basketHelper.updatePassengersForChannel(travellerHelper.createRequestToChangePassengerAgeAndType("infant", basketService.getResponse().getBasket().getOutbounds().get(j).getFlights().get(0).getPassengers().get(0).getCode(), false), testData.getData(CHANNEL), basketService.getResponse().getBasket().getCode());
                basketTravellerService = testData.getData(SERVICE);
                assertThat(basketTravellerService.getResponse().getAdditionalInformations().stream().anyMatch(code -> code.getCode().equalsIgnoreCase("SVC_100273_1000"))).withFailMessage("Passenger details are not updated successfully.").isEqualTo(TRUE);
            }
        }
        if (passengerMix.contains("child")) {
            for (int j = 0; j < basketService.getResponse().getBasket().getOutbounds().size(); j ++) {
                basketHelper.updatePassengersForChannel(travellerHelper.createRequestToChangePassengerAgeAndType("child", basketService.getResponse().getBasket().getOutbounds().get(j).getFlights().get(0).getPassengers().get(0).getCode(), false), testData.getData(CHANNEL), basketService.getResponse().getBasket().getCode());
                basketTravellerService = testData.getData(SERVICE);
                assertThat(basketTravellerService.getResponse().getAdditionalInformations().stream().anyMatch(code -> code.getCode().equalsIgnoreCase("SVC_100273_1000"))).withFailMessage("Passenger details are not updated successfully.").isEqualTo(TRUE);
            }
        }

    }
}
