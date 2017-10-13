package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.constants.FlightInterestConstants;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.models.FlightInterestModel;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.fixture.hybris.asserters.SaveFlightInterestServiceAssertion;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightInterestHelper;
import com.hybris.easyjet.fixture.hybris.helpers.StaffMembertoCustomerProfileAssociationHelper;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.SeatMapQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest.FlightInterestRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.SaveFlightInterestService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory.getRandomEmail;


@ContextConfiguration(classes = TestApplication.class)

public class SaveFlightInterestToAProfileSteps {


    @Autowired
    FlightFinder flightFinder;
    @Autowired
    FlightInterestHelper flightInterestHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffMembertoCustomerProfileAssociationHelper;
    private SaveFlightInterestService flightInterestService;
    private SeatMapQueryParams queryParams;
    private FlightInterestRequestBody saveFlightInterestRequestBody;
    private CustomerProfileService customerProfileService;
    private String flightInterestCustomerId;
    private String flightKey;
    private LocalDateTime newDepartureDateTime;
    private SaveFlightInterestServiceAssertion saveFlightInterestServiceAssertion;
    private List<FlightInterestModel> flightInterestsBefore;
    private List<FlightInterestModel> flightInterestsAfter;
    private List<HybrisFlightDbModel> flightsToAdd;
    private Map<String, List<String>> flightInterestsToAdd;
    private FlightsService flightService;


    @Given("^staff Customer is logged in$")
    public void iCreateAStaffCustomerAndLogIn() throws Throwable {
        customerHelper.createNewCustomerProfileWithEmail(getRandomEmail(10));
        this.flightInterestCustomerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromId(this.flightInterestCustomerId, false);
        customerHelper.loginWithValidCredentials();
    }

    @When("^I receive a request to add a flight interest to the profile$")
    public void iReceiveAReqToAddFlightInterestToProfile() throws Throwable {
        this.flightKey = flightInterestHelper.getValidFlightKey();
        flightInterestHelper.addFlightInterestToProfile(this.flightInterestCustomerId, this.flightKey, FlightInterestConstants.LEGAL_FARE_TYPE, FlightInterestConstants.DIGITAL_CHANNEL);
    }

    @Then("^I will validate that the request meets the service contract$")
    public void theRequestMeetsTheServiceContract() throws Throwable {
        flightInterestHelper.getFlightInterestService().wasSuccessful();
        saveFlightInterestServiceAssertion = new SaveFlightInterestServiceAssertion(flightInterestHelper.getFlightInterestService().getResponse());
        saveFlightInterestServiceAssertion.iHaveCorrectValidationStatusesForFlightKeys(Collections.singletonList(this.flightKey))
                .additionalInformationIsEmpty()
                .customerIdIsEqualTo(this.flightInterestCustomerId);
    }

    //2nd scenario

    @And("^flight standard departure time is less than the checkin time from now$")
    public void flightStandardDepartureTimeIsHoursFromNow() throws Throwable {
        flightInterestsBefore = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        this.flightKey = flightInterestHelper.getValidFlightKeyForAirport(FlightInterestConstants.TEST_AIRPORT_CODE);
        flightInterestHelper.setupDatabaseData(FlightInterestConstants.TEST_AIRPORT_CODE, FlightInterestConstants.CHECKIN_TIME, FlightInterestConstants.CHECKIN_TIME, this.flightKey);
    }

    @When("^I receive a request to add a flight interest on that flight to the profile$")
    public void iReceiveAReqToAddThatFlightInterestToProfile() throws Throwable {
        flightInterestHelper.addSingleFlightInterestToProfile(this.flightInterestCustomerId, this.flightKey, FlightInterestConstants.LEGAL_FARE_TYPE, FlightInterestConstants.DIGITAL_CHANNEL);
    }

    @Then("^I will return an error to the channel$")
    public void anErrorIsReturned() throws Throwable {
        flightInterestHelper.getFlightInterestService().assertThatErrors().containedTheCorrectErrorMessage(FlightInterestConstants.TIME_LIMIT_ERROR_CODE);
    }


    @Then("^register flight interest is not added to the profile$")
    public void theFlightInterestIsNotRegisteredWithSuccessCode() throws Throwable {
        flightInterestsAfter = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        flightInterestHelper.getFlightInterestService().assertThatErrors().assertNoNewFlightInterestIsSaved(flightInterestsBefore, flightInterestsAfter);
    }


    @Then("^the register flight interest is not added to the profile$")
    public void theFlightInterestIsNotRegistered() throws Throwable {
        flightInterestsAfter = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        flightInterestHelper.getFlightInterestService().assertThatErrors().assertNoNewFlightInterestIsSaved(flightInterestsBefore, flightInterestsAfter);
    }

    //3 scenario

    @When("^I receive a request to save a flight interest to the profile$")
    public void iReceiveAReqToAddFlightInterestToProfileWithIllegalFareType() throws Throwable {
        this.flightKey = flightInterestHelper.getValidFlightKey();
    }


    @And("^the fare type \"([^\"]*)\" is not x= staff, staff standard or stand-by$")
    public void iReceiveAReqToAddFlightInterestToProfileWithIllegalFare(String fareType) throws Throwable {
        flightInterestsBefore = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        flightInterestHelper.addSingleFlightInterestToProfile(this.flightInterestCustomerId, this.flightKey, fareType, FlightInterestConstants.DIGITAL_CHANNEL);
    }

    @Then("^I will return a wrong fare type error to the channel$")
    public void aFareTypeErrorIsReturned() throws Throwable {
        flightInterestHelper.getFlightInterestService().assertThatErrors().containedTheCorrectErrorMessage(FlightInterestConstants.FARE_TYPE_ERROR_CODE);
    }

    //4th scenario
    @And("^the customer already has \"([^\"]*)\" registered interest stored$")
    public void iReceiveAReqToAddFlightInterestToProfile(String interestNumber) throws Throwable {
        List<HybrisFlightDbModel> flightsToAdd = flightInterestHelper.getNValidFlights(Integer.parseInt(interestNumber) + 1);
        this.flightKey = flightInterestHelper.getLastFlightToAddFromFlightsList(flightsToAdd);
        flightsToAdd = flightInterestHelper.getFlightListToAddBefore(flightsToAdd);
        flightInterestHelper.addFlightInterests(flightsToAdd, this.flightInterestCustomerId, FlightInterestConstants.DIGITAL_CHANNEL);
        flightInterestHelper.getFlightInterestService().wasSuccessful().additionalInformationIsEmpty();
        flightInterestsBefore = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
    }

    @When("^I receive a request to add a new flight interest to the profile$")
    public void iReceiveAReqToAddNewFlightInterestToProfile() throws Throwable {
        flightInterestHelper.addFlightInterestToProfile(this.flightInterestCustomerId, this.flightKey, FlightInterestConstants.LEGAL_FARE_TYPE, FlightInterestConstants.DIGITAL_CHANNEL);
    }

    @Then("^I will return a max number of registerd interests error to the channel$")
    public void aMaxRegisteredErrorIsReturned() throws Throwable {
        flightInterestHelper.getFlightInterestService().assertThatErrors().containedTheCorrectErrorMessage(FlightInterestConstants.MAX_REGISTERED_ERROR_CODE);
    }

    //5th scenario
    @And("^a registered flight interest is stored$")
    public void iAddAFlightInterest() throws Throwable {
        this.flightKey = flightInterestHelper.getValidFlightKey();
        flightInterestHelper.addSingleFlightInterestToProfile(this.flightInterestCustomerId, this.flightKey, FlightInterestConstants.LEGAL_FARE_TYPE, FlightInterestConstants.DIGITAL_CHANNEL);
        flightInterestHelper.getFlightInterestService().wasSuccessful();
        flightInterestsBefore = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
    }


    @When("^I sent request to add registered flight interest to staff customer profile for same flight Same fare type$")
    public void iAddAFlightInterestAlreadyRegistered() throws Throwable {
        flightInterestHelper.addFlightInterestToProfile(this.flightInterestCustomerId, this.flightKey, FlightInterestConstants.LEGAL_FARE_TYPE, FlightInterestConstants.DIGITAL_CHANNEL);
    }

    @Then("^I will return a duplicated registration error to the channel$")
    public void aDuplicatedErrorIsReturned() throws Throwable {
        flightInterestHelper.getFlightInterestService().assertThatErrors().containedTheCorrectErrorMessage(FlightInterestConstants.DUPLICATED_ERROR_CODE);
    }

    //6th scenario
    @When("^I receive a valid request to add a flight interest to the profile$")
    public void iReceiveAValidReqToAddFlightInterestToProfile() throws Throwable {
        flightInterestsBefore = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        this.flightKey = flightInterestHelper.getValidFlightKey();
        flightInterestHelper.addFlightInterestToProfile(this.flightInterestCustomerId, this.flightKey, FlightInterestConstants.LEGAL_FARE_TYPE, FlightInterestConstants.DIGITAL_CHANNEL);
    }

    @Then("^I will store registered flight interest to the profile$")
    public void theFlightInterestIsStored() throws Throwable {
        flightInterestHelper.getFlightInterestService().wasSuccessful();
        saveFlightInterestServiceAssertion = new SaveFlightInterestServiceAssertion(flightInterestHelper.getFlightInterestService().getResponse());
        flightInterestsAfter = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        saveFlightInterestServiceAssertion.assertANewFlightInterestIsSaved(flightInterestsBefore, flightInterestsAfter, this.flightKey, FlightInterestConstants.LEGAL_FARE_TYPE);
    }

    @And("^return confirmation to the channel$")
    public void returnConfirmationToChannel() throws Throwable {
        saveFlightInterestServiceAssertion.iHaveCorrectValidationStatusesForFlightKeys(Collections.singletonList(flightKey));
    }

    //7th

    @When("^I receive a valid request to add multiple \"([^\"]*)\" registered flight interest to staff customer profile$")
    public void iReceiveAReqToAddMultipleValidFlightInterestToProfile(String flightKeyNumber) throws Throwable {
        List<HybrisFlightDbModel> flightsToAdd = flightInterestHelper.getNValidFlights(Integer.parseInt(flightKeyNumber));
        flightInterestsBefore = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        this.flightInterestsToAdd = flightInterestHelper.creatMapOfFlighInterestsToAdd(flightsToAdd, Collections.singletonList(FlightInterestConstants.STAFF_FARE));
        flightInterestHelper.addFlightWithMultipleInterests(this.flightInterestsToAdd, this.flightInterestCustomerId, FlightInterestConstants.DIGITAL_CHANNEL);
    }


    @Then("^I will store registered all flight interest to the profile$")
    public void theFlightInterestAreStored() throws Throwable {
        flightInterestHelper.getFlightInterestService().wasSuccessful();
        saveFlightInterestServiceAssertion = new SaveFlightInterestServiceAssertion(flightInterestHelper.getFlightInterestService().getResponse());
        flightInterestsAfter = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        saveFlightInterestServiceAssertion.assertANewFlightInterestIsSaved(flightInterestsBefore, flightInterestsAfter, this.flightInterestsToAdd);
    }


    @And("^return all confirmation to the channel$")
    public void returnAllConfirmationToChannel() throws Throwable {
        saveFlightInterestServiceAssertion.iHaveCorrectValidationStatusesForFlightKeys(new ArrayList<String>(this.flightInterestsToAdd.keySet()));
    }


    //8th
    @When("^I receive a request to add multiple \"([^\"]*)\" registered flight interest to staff customer profile and one flight interest fails the validation$")
    public void iReceiveAReqToAddMultipleInvalidFlightInterestToProfile(String flightKeyNumber) throws Throwable {
        flightsToAdd = flightInterestHelper.getNValidFlights(Integer.parseInt(flightKeyNumber));
        flightsToAdd = flightInterestHelper.setAnInvalidFlightKey(flightsToAdd);
        this.flightInterestsToAdd = flightInterestHelper.creatMapOfFlighInterestsToAdd(flightsToAdd, Collections.singletonList(FlightInterestConstants.STAFF_FARE));
        flightInterestsBefore = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        flightInterestHelper.addFlightWithMultipleInterests(this.flightInterestsToAdd, this.flightInterestCustomerId, FlightInterestConstants.DIGITAL_CHANNEL);
    }


    @Then("^I return confirmation to the channel$")
    public void returnConfirmationCodeToChannel() throws Throwable {
        flightInterestHelper.getFlightInterestService().wasSuccessful();
    }


    @And("^I return error message in Additional Details for the failed flight interests$")
    public void checkErrorMessages() throws Throwable {
        saveFlightInterestServiceAssertion = new SaveFlightInterestServiceAssertion(flightInterestHelper.getFlightInterestService().getResponse());
        saveFlightInterestServiceAssertion.iHaveConsistentValidationStatusesForFlightKeys(new ArrayList<String>(this.flightInterestsToAdd.keySet()))
                .additionalInformationReturned("SVC_100050_2021");
    }

    @And("^I will store successful registered flight interests to the profile$")
    public void returnAnotherConfirmationToChannel() throws Throwable {
        flightInterestsAfter = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        saveFlightInterestServiceAssertion.assertOnlyValidNewFlightInterestAreSaved(flightInterestsBefore, flightInterestsAfter, this.flightInterestsToAdd);
    }

    //9
    @When("^When I have receive a request from a Channel that is not Digital and I perform the validation$")
    public void iReceiveAReqToAddFlightInterestToProfileNotFromDigital() throws Throwable {
        this.flightKey = flightInterestHelper.getValidFlightKey();
        flightInterestHelper.addFlightInterestToProfile(this.flightInterestCustomerId, this.flightKey, FlightInterestConstants.LEGAL_FARE_TYPE, FlightInterestConstants.AD_CHANNEL);
    }

    @Then("^I will return an error Incorrect Channel, not supported request$")
    public void aWrongChannelErrorIsReturned() throws Throwable {
        flightInterestHelper.getFlightInterestService().assertThatErrors().containedTheCorrectErrorMessage(FlightInterestConstants.UNSUPPORTED_CHANNEL_ERROR_CODE);
    }


    @When("^I remove the flight interest without login$")
    public void iRemoveTheFlightInterestWithoutLogin() throws Throwable {

        List<HybrisFlightDbModel> flightsToAdd = flightInterestHelper.getNValidFlights(Integer.parseInt("3"));
        flightInterestsBefore = flightInterestHelper.getSavedFlightInterestsFor(this.flightInterestCustomerId);
        this.flightInterestsToAdd = flightInterestHelper.creatMapOfFlighInterestsToAdd(flightsToAdd, Collections.singletonList(FlightInterestConstants.STAFF_FARE));
        flightInterestHelper.addFlightWithMultipleInterests(this.flightInterestsToAdd, this.flightInterestCustomerId, FlightInterestConstants.DIGITAL_CHANNEL);

    }

    @And("^the customer is not hard logged in$")
    public void theCustomerIsNotHardLoggedIn() throws Throwable {
        customerHelper.createNewCustomerProfileWithEmail(getRandomEmail(10));
        this.flightInterestCustomerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromId(this.flightInterestCustomerId, false);
        customerHelper.loginWithValidCredentials();
    }
}




