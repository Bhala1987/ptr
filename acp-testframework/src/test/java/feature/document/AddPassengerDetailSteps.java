package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.PropertyValueConfigurationDao;
import com.hybris.easyjet.database.hybris.dao.SSRDataDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.WaitHelper;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Name;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.CustomerProfileQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.SavedSSRs;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import joptsimple.internal.Strings;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by dwebb on 11/22/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class AddPassengerDetailSteps {

    protected static Logger LOG = LogManager.getLogger(AddPassengerDetailSteps.class);

    @Autowired
    private SSRDataDao ssrDataDao;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private TravellerHelper passengerHelper;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;

    private CustomerProfileService customerProfileService;
    private String theStaffCustomerUID = "cus00000001";
    private Name theChosenName;
    private FlightsService flightsService;
    private String thePassengerMix = "2 Adult";

    private CustomerProfileResponse theStaffProfile;
    private Passengers sentUpdate;
    private String expectedPaxType;
    private Basket updatedBasket;
    private String flightKey;
    private Passengers updatePassengerRequestBody;
    @Autowired
    PropertyValueConfigurationDao propertyValueConfigurationDao;
    private static final String ADULTINFANTRATIOPROPERTYVALUE = "adultInfantOwnSeatRatio";
    private int adultCount = 0;
    private int infantOnOwnSeatCount = 0;
    private List<Basket.Passenger> sortedAdultPassengers;
    private List<Basket.Passenger> sortedInfantOnSeatPassengers;

    @Steps
    private CommonSteps commonSteps;

    @Given("^my basket contains \"([^\"]*)\"$")
    public void myBasketContains(String passengerMix) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix);
        this.thePassengerMix = passengerMix;
    }

    @Given("^my basket contains \"([^\"]*)\" added via \"([^\"]*)\"$")
    public void myBasketContainsAddedVia(String passengerMix, String channel) throws Throwable {
        flightKey = basketHelper.getFlightKeyForTheFlightWhichIsInBasketFor(passengerMix, channel);
        testData.setChannel(channel);
        this.thePassengerMix = passengerMix;
    }

    @Given("^(my basket contains \"([^\"]*)\" added via \"([^\"]*)\") for a staff member$")
    public void myBasketContainsAddedViaChannelForStaff(String passengerMix, String channel) throws Throwable {
        basketHelper.myBasketContainsAFlightWithPassengerMixForStaff(passengerMix, channel, "Staff");
        testData.setChannel(channel);
        this.thePassengerMix = passengerMix;
    }

    @Given("^I am updating my passenger details$")
    public void uptaingPassengerDetails() throws Throwable {
        this.myBasketContainsAddedVia(thePassengerMix, testData.getChannel());
    }

    @Given("^I am updating my passenger document details$")
    public void uptaingPassengerDocumentDetails() throws Throwable {
        this.myBasketContainsAddedVia(thePassengerMix, testData.getChannel());
    }

    @Given("^I am updating my passenger details as a staff member$")
    public void uptaingPassengerDetailsAsStaff() throws Throwable {
        this.myBasketContainsAddedViaChannelForStaff(thePassengerMix, testData.getChannel());
    }

    @When("^I change the passenger age of \"([^\"]*)\" to \"([^\"]*)\" via \"([^\"]*)\"$")
    public void iChangeThePassengerAgeOfToVia(String original, String changeTo, String channel) throws Throwable {
        testData.setChannel(channel);
        expectedPaxType = changeTo;
        testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
        Passengers request = passengerHelper.createRequestToChangePassengerAge(original, changeTo, basketHelper.getBasketService().getResponse());
        sentUpdate = request;
        basketHelper.updatePassengersForChannel(request, channel, basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @Then("^the basket is updated and fees calculated$")
    public void theBasketIsUpdatedAndFeesCalculated() throws Throwable {
        //get the basket and assert it has been updated
        Basket originalBasket = testData.getData(ORIGINAL_BASKET);
        basketHelper.getBasket(originalBasket.getCode(), testData.getChannel());
        updatedBasket = basketHelper.getBasketService().getResponse().getBasket();
        //compare
        basketHelper.getBasketService().assertThat().theBasketContainsTheUpdatedPassengerDetails(updatedBasket, sentUpdate, expectedPaxType);
    }

    @When("^I provide basic passenger details$")
    public void iProvideBasicPassengerDetails() throws Throwable {
        testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
        Passengers request = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse(), true);
        sentUpdate = request;
        basketHelper.updatePassengersForChannel(request, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    @Then("^the basket is updated with the details$")
    public void theBasketIsUpdatedWithTheDetails() throws Throwable {
        Basket originalBasket = testData.getData(ORIGINAL_BASKET);
        basketHelper.getBasket(originalBasket.getCode());
        Basket updatedBasket = basketHelper.getBasketService().getResponse().getBasket();
        basketHelper.getBasketService().assertThat().theBasketContainsTheUpdatedPassengerDetails(updatedBasket, sentUpdate);
    }

    @When("^I fail to provide the passenger field \"([^\"]*)\"$")
    public void iFailToProvideThePassengerField(String field) throws Throwable {
        List<FieldAndValue> myFields = new ArrayList<>();
        myFields.add(new FieldAndValue(field, null));
        Passengers request = passengerHelper.createRequestWithFieldSetAs(myFields, basketHelper.getBasketService().getResponse(), thePassengerMix);
        basketHelper.updatePassengersForChannel(request, "Digital", basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I set the passenger type as \"([^\"]*)\" and the title as \"([^\"]*)\"$")
    public void iSetThePassengerTypeAsAndTheTitleAs(String passengerType, String title) throws Throwable {
        ArrayList<FieldAndValue> list = new ArrayList<FieldAndValue>() {{
            add(FieldAndValue.builder().field("passengertype").value(passengerType).build());
            add(FieldAndValue.builder().field("title").value(title).build());
        }};
        Passengers request = passengerHelper.createRequestWithFieldSetAs(list, basketHelper.getBasketService().getResponse(), thePassengerMix, false);
        basketHelper.updatePassengersForChannel(request, "Digital", basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I set the passenger type as \"([^\"]*)\" and the age as \"([^\"]*)\"$")
    public void iSetThePassengerTypeAsAndTheAgeAs(String passengerType, String age) throws Throwable {
        ArrayList<FieldAndValue> list = new ArrayList<FieldAndValue>() {{
            add(FieldAndValue.builder().field("passengertype").value(passengerType).build());
            add(FieldAndValue.builder().field("age").value(Integer.toString(passengerHelper.getValidAgeForPassengerType(age))).build());
        }};
        Passengers request = passengerHelper.createRequestWithFieldSetAs(list, basketHelper.getBasketService().getResponse(), thePassengerMix, false);
        basketHelper.updatePassengersForChannel(request, "Digital", basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @Then("^I should (?:see|receive) the \"([^\"]*)\" error message(?:.*)")
    public void iShouldSeeTheErrorMessage(String err) throws Throwable {
        basketHelper.getBasketPassengerService().assertThatErrors().containedTheCorrectErrorMessage(err);
    }

    @Then("^I should see an \"([^\"]*)\" (.*) message$")
    public void iShouldSeeTheErrorMessage(String err, String type) throws Throwable {
        switch (type) {
            case "error":
                basketHelper.getBasketPassengerService().assertThatErrors().containedTheCorrectErrorMessage(err);
                break;
            case "warning":
                basketHelper.getBasketPassengerService().assertThat().additionalInformationReturned(err);
        }

    }

    @When("^I update passenger \"([^\"]*)\" details as \"([^\"]*)\"$")
    public void iUpdatePassengerDetailsWithAs(String field, String value) throws Throwable {
        List<FieldAndValue> myFields = new ArrayList<>();
        myFields.add(new FieldAndValue(field, value));
        Passengers request = passengerHelper.createRequestWithFieldSetAs(myFields, basketHelper.getBasketService().getResponse(), thePassengerMix);
        basketHelper.updatePassengersForChannel(request, "Digital", basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @And("^the infant is now on lap of the first adult$")
    public void theInfantIsNowOnLapOfTheFirstAdult() throws Throwable {
        basketHelper.getBasketService().assertThat().infantIsNowOnLapOfFirstAdult();
    }

    @When("^I change the passenger age of \"([^\"]*)\" to \"([^\"]*)\"$")
    public void iChangeThePassengerAgeOfTo(String original, String changeTo) throws Throwable {
        expectedPaxType = changeTo;
        testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
        Passengers request = passengerHelper.createRequestToChangePassengerAge(original, changeTo, basketHelper.getBasketService().getResponse());
        sentUpdate = request;
        basketHelper.updatePassengersForChannel(request, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @Then("^the Child seat is removed$")
    public void theChildSeatIsRemoved() throws Throwable {
        Basket originalBasket = testData.getData(ORIGINAL_BASKET);
        basketHelper.getBasket(originalBasket.getCode());
        basketHelper.getBasketService().assertThat().thereIsNoSeatOfType("child");
    }

    @And("^the infant is assigned to the second adult onlap$")
    public void theInfantIsAssignedToTheSecondAdultOnTheirLap() throws Throwable {
        Basket originalBasket = testData.getData(ORIGINAL_BASKET);
        basketHelper.getBasketService().assertThat().theInfantIsOnTheLapOfTheSecondAdult(originalBasket);
    }

    @And("^the \"([^\"]*)\" is now in their own seat$")
    public void theIsNowInTheirOwnSeat(String paxType) throws Throwable {
        Basket originalBasket = testData.getData(ORIGINAL_BASKET);
        basketHelper.getBasketService().assertThat().infantIsNowOnTheirOwnSeat(originalBasket, updatedBasket);
    }

    @When("^I set the passenger age as \"([^\"]*)\" and the title as \"([^\"]*)\"$")
    public void iSetThePassengerAgeAsAndTheTitleAs(String age, String title) throws Throwable {
        ArrayList<FieldAndValue> list = new ArrayList<FieldAndValue>() {{
            add(FieldAndValue.builder().field("age").value(Integer.toString(passengerHelper.getValidAgeForPassengerType(age))).build());
            add(FieldAndValue.builder().field("title").value(title).build());
        }};
        Passengers request = passengerHelper.createRequestWithFieldSetAs(list, basketHelper.getBasketService().getResponse(), thePassengerMix, false);
        basketHelper.updatePassengersForChannel(request, "Digital", basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I provide a NIF number less than (\\d+)$")
    public void iProvideANIFNumberLessThan(int aNumber) throws Throwable {
        String myNumber = buildAStringOfLengthLessThan(aNumber);
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse());
        passengers.getPassengers().get(0).getPassengerDetails().setNifNumber(myNumber);
        basketHelper.updatePassengersForChannel(passengers, "Digital", basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @Given("^I am updating my passenger details for at least two travellers$")
    public void iAmUpdatingMyPassengerDetailsForAtLeastTwoTravellers() throws Throwable {
        this.myBasketContainsAddedVia("2 adults", testData.getChannel());
    }

    @When("^I provide the same NIF number to both travellers$")
    public void iProvideTheSameNIFNumberToBothTravellers() throws Throwable {
        String myNumber = "123456789";

        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse());

        passengers.getPassengers().get(0).getPassengerDetails().setNifNumber(myNumber);
        passengers.getPassengers().get(1).getPassengerDetails().setNifNumber(myNumber);

        basketHelper.updatePassengersForChannel(passengers, "Digital", basketHelper.getBasketService().getResponse().getBasket().getCode());

    }

    @Given("^I (?:update|am updating) my passenger details as (.*)$")
    public void iUpdateMyPassengerDetailsAsChannel(String theChannel) throws Throwable {
        this.myBasketContainsAddedVia(thePassengerMix, theChannel);
    }

    @When("^the number of SSRs exceed the maximum of (.*)$")
    public void theNumberOfSSRsExceedTheMaximumOfX(int aMaxSSR) throws Throwable {
        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr mySsr = new SavedSSRs.Ssr();
        List<String> SSRCode = ssrDataDao.getSSRDataActive(true, aMaxSSR + 1);

        for (String code : SSRCode) {
            mySsr.setCode(code);
            mySsrList.add(mySsr);
        }

        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(
                basketHelper.getBasketService().getResponse()
        );

        passengers.getPassengers().get(0).getSpecialRequests().setSsrs(mySsrList);

        basketHelper.updatePassengersForChannel(
                passengers,
                testData.getChannel(),
                basketHelper.getBasketService().getResponse().getBasket().getCode()
        );
    }

    @When("^I add an SSR for (an invalid|a valid) sector$")
    public void whenIAddAndSSRForAnInvalidSector(String ssrType) throws Throwable {
        //TODO: add agree to TC for ssr
        if (testData.getDestination() == null || testData.getOrigin() == null) {
            testData.setOrigin("LTN");
            testData.setDestination("ALC");
        }

        List<SavedSSRs.Ssr> mySSR;
        if (ssrType.equals("an invalid")) {
            mySSR = getInvalidSSRForSector(testData.getOrigin(), testData.getDestination(), testData.getChannel());
        } else {
            mySSR = getValidSSRForSector(testData.getOrigin(), testData.getDestination(), testData.getChannel());
        }

        LocalDate myChosenDate = LocalDate.now();
        myChosenDate = myChosenDate.plusDays(5);
        String chosenDateString = myChosenDate.format(ofPattern("dd-MM-YYYY"));

        emptyBasket();

        FlightQueryParams flightParams = FlightQueryParams.builder().adult("1").origin(testData.getOrigin()).destination(testData.getDestination()).outboundDate(chosenDateString).build();
        flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), flightParams));
        flightsService.invoke();


        basketHelper.addFlightToBasketAsChannel(flightsService.getOutboundFlight());

        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(
                basketHelper.getBasketService().getResponse()
        );

        passengers.getPassengers().get(0).getSpecialRequests().setSsrs(mySSR);

        basketHelper.updatePassengersForChannel(
                passengers,
                testData.getChannel(),
                basketHelper.getBasketService().getResponse().getBasket().getCode()
        );
    }

    private List<SavedSSRs.Ssr> getInvalidSSRForSector(String theOrigin, String theDestination, String theChannelUsed) {
        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr mySsr = new SavedSSRs.Ssr();

        String SSRCode = ssrDataDao.getInvalidSSRForChannelAndSector(theChannelUsed, theOrigin + theDestination).get(0).getCode();
        mySsr.setCode(SSRCode);
        mySsrList.add(mySsr);

        assertThat(mySsrList).isNotEmpty().withFailMessage("Could not find invalid SSR for Channel:" + theChannelUsed + " Sector:" + theOrigin + theDestination);

        return mySsrList;

    }

    private List<SavedSSRs.Ssr> getValidSSRForSector(String theOrigin, String theDestination, String theChannelUsed) {
        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr mySsr = new SavedSSRs.Ssr();

        String SSRCode = ssrDataDao.getValidSSRForChannelAndSector(true, theChannelUsed, theOrigin + theDestination).get(0).getCode();
        mySsr.setCode(SSRCode);
        mySsrList.add(mySsr);

        return mySsrList;

    }

    @Given("^I am a \"([^\"]*)\" and logged in as user \"([^\"]*)\" and \"([^\"]*)\"$")
    public void iAmAAndLoggedInAsUserAnd(String arg0, String user, String pwd) throws Throwable {
        if (testData.getChannel() == null) {
            testData.setChannel(testData.getChannel());
        }

        customerHelper.loginWithValidCredentials(Strings.EMPTY, user, pwd, false);

        basketHelper.associateCustomerProfileToBasket(testData.getChannel(), theStaffCustomerUID);
        getStaffProfile();
        uptaingPassengerDetailsAsStaff();

        testData.setData(CUSTOMER_ID, theStaffCustomerUID);

    }

    private void getStaffProfile() {
        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(theStaffCustomerUID).path(PROFILE).build();
        customerProfileService = serviceFactory.getCustomerProfile(
                new ProfileRequest(HybrisHeaders.getValid(testData.getChannel()).prefer("FULL").build(), profilePathParams, CustomerProfileQueryParams.builder().sections("dependants").build())
        );
        customerProfileService.invoke();
        theStaffProfile = customerProfileService.getResponse();
    }

    @When("^I update passenger details for someone who is not a dependant or significant other of the staff member$")
    public void iUpdatePassengerDetailsForSomeoneWhoIsNotADependantOrSignificantOtherOfTheStaffMember() throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(
                basketHelper.getBasketService().getResponse()
        );

        passengers.getPassengers().get(0).getPassengerDetails().getName().setFirstName("myfirstname");

        basketHelper.updatePassengersForChannel(
                passengers,
                testData.getChannel(),
                basketHelper.getBasketService().getResponse().getBasket().getCode()
        );


    }

    @Then("^I receive an error stating the names do not match$")
    public void iReceiveAnErrorStatingTheNamesDoNotMatch() throws Throwable {
        basketHelper.getBasketPassengerService().assertThatErrors().containedTheCorrectErrorMessage("SVC_100045_3014");
    }

    @When("^I update passenger details for someone who is a (.*) of the staff customer$")
    public void iUpdatePassengerDetailsForSomeoneWhoIsADependant(String aPassengerType) throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(
                basketHelper.getBasketService().getResponse()
        );

        theChosenName = passengers.getPassengers().get(0).getPassengerDetails().getName();

        if (aPassengerType.equals("dependant")) {
            theChosenName = updateNameOfDependantForStaff(theChosenName);
        } else {
            theChosenName = updateNameOfSignificantOtherForStaff(theChosenName);
        }

        passengers.getPassengers().get(0).getPassengerDetails().setName(theChosenName);

        basketHelper.updatePassengersForChannel(
                passengers,
                testData.getChannel(),
                basketHelper.getBasketService().getResponse().getBasket().getCode()
        );
    }

    private Name updateNameOfDependantForStaff(Name aChosenName) {

        aChosenName.setFirstName(
                theStaffProfile.getCustomer().getAdvancedProfile().getDependents().get(0).getFirstName()
        );

        aChosenName.setLastName(
                theStaffProfile.getCustomer().getAdvancedProfile().getDependents().get(0).getLastName()
        );


        return aChosenName;

    }

    private Name updateNameOfSignificantOtherForStaff(Name aChosenName) {

        aChosenName.setFirstName(
                theStaffProfile.getCustomer().getAdvancedProfile().getSignificantOthers().getPassengers().get(0).getFirstName()
        );

        aChosenName.setLastName(
                theStaffProfile.getCustomer().getAdvancedProfile().getSignificantOthers().getPassengers().get(0).getLastName()
        );

        return aChosenName;
    }

    @Then("^the passenger details should be updated$")
    public void thePassengerDetailsShouldBeUpdated() throws Throwable {
        basketHelper.getBasketPassengerService().getResponse();
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
        basketHelper.getBasketService().assertThat().theBasketContainsTheUpdatedPassengerName(theChosenName);
    }

    @Then("^the passenger details should be updated with the SSR$")
    public void thePassengerDetailsShouldBeUpdatedWithTheSSR() throws Throwable {
        //we need to find the passenger type and query the db for ssr?


        basketHelper.getBasketPassengerService().assertThat().additionalInformationReturned("SVC_100273_1000");

    }

    private void emptyBasket() {
        basketHelper.emptyBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
    }

    @When("^I provide document number length as \"([^\"]*)\"characters$")
    public void iProvideDocumentNumberLengthAsCharacters(int length) throws Throwable {
        String documentNumber = passengerHelper.buildAStringOfLength(length);
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse(), true);
        passengers.getPassengers().get(0).getPassengerAPIS().setDocumentNumber(documentNumber);
        basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I set the document number as \"([^\"]*)\"$")
    public void iSetTheDocumentNumberAs(String docNumber) throws Throwable {
        String documentNumber = docNumber;
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse(), true);
        passengers.getPassengers().get(0).getPassengerAPIS().setDocumentNumber(documentNumber);
        basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    private String buildAStringOfLengthLessThan(int aNumber) {
        return passengerHelper.buildAStringOfLength(aNumber - 1);
    }

    @Given("^I am updating my passenger document details for \"([^\"]*)\"$")
    public void iAmUpdatingThePassengerDetailsForTheBasketThatContains(String passengerMix) throws Throwable {
        this.myBasketContainsAddedVia(passengerMix, testData.getChannel());
    }

    @When("^I set the date of birth of \"([^\"]*)\" as \"([^\"]*)\"$")
    public void iSetTheDateOfBirthOfAs(String passengerTypeToUpdateDOB, String dobToUse) throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse());
        passengerHelper.setDob(passengers.getPassengers(), passengerTypeToUpdateDOB, dobToUse);
        basketHelper.updatePassengersForChannel(passengers, "Digital", basketHelper.getBasketService().getResponse().getBasket().getCode());

    }

    @Given("^I am updating SSRs for a passenger in basket$")
    public void iAmUpdatingSSRsForAPassengerInBasket() throws Throwable {
        this.myBasketContainsAddedVia(thePassengerMix, testData.getChannel());
    }

    @When("^I include the mandatory Ts and Cs parameter for SSRs I am updating$")
    public void iIncludeTheMandatoryTsAndCsParameterForSSRsIAmUpdating() throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(
                basketHelper.getBasketService().getResponse()
        );
        List<SavedSSRs.Ssr> mySsrList = getSsrsWithTsAndCsParameterInIt(true);
        passengers.getPassengers().get(0).getSpecialRequests().setSsrs(mySsrList);
        basketHelper.updatePassengersForChannel(
                passengers,
                testData.getChannel(),
                basketHelper.getBasketService().getResponse().getBasket().getCode()
        );
    }

    @When("^I do not include the mandatory Ts and Cs parameter for SSRs I am updating$")
    public void iDoNotIncludeTheMandatoryTsAndCsParameterForSSRsIAmUpdating() throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(
                basketHelper.getBasketService().getResponse()
        );
        List<SavedSSRs.Ssr> mySsrList = getSsrsWithTsAndCsParameterInIt(false);
        passengers.getPassengers().get(0).getSpecialRequests().setSsrs(mySsrList);
        basketHelper.updatePassengersForChannel(
                passengers,
                testData.getChannel(),
                basketHelper.getBasketService().getResponse().getBasket().getCode()
        );
    }

    private List<SavedSSRs.Ssr> getSsrsWithTsAndCsParameterInIt(boolean ssrsTharAreMandatoryToTsAndCsToBeAccepted) {
        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr mySsr = new SavedSSRs.Ssr();
        List<String> SSRCodes = ssrDataDao.getSSRsForTermsAndConditionsMandatoryToBeAccepted(true, 2);

        for (String code : SSRCodes) {
            mySsr.setCode(code);
            if (ssrsTharAreMandatoryToTsAndCsToBeAccepted) {
                mySsr.setIsTandCsAccepted(true);
            }
            mySsrList.add(mySsr);
        }
        return mySsrList;
    }

    @When("^I provide basic passenger details with ejPlusCardNumber:(.*)$")
    public void iProvideBasicPassengerDetailsWithEjPlusCardNumber(String aNumber) throws Throwable {
        testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
        Passengers request = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse());
        request.getPassengers().get(0).getPassengerDetails().setEjPlusCardNumber(aNumber);
        sentUpdate = request;
        basketHelper.updatePassengersForChannel(request, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I set the document date of birth for \"([^\"]*)\" as \"([^\"]*)\" years old at the flight departure$")
    public void iSetTheDocumentDateOfBirthForAsYearsOldAtTheFlightDeparture(String passengerToUpdate, int age) throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse(), true);
        passengerHelper.setDob(passengers.getPassengers(), passengerToUpdate, getAValidDateForAgeFromFlightDepartureDate(flightKey, age));
        basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I set the document date of birth for \"([^\"]*)\" as \"([^\"]*)\" days old at the flight departure$")
    public void iSetTheDocumentDateOfBirthAnAsDaysOldAtTheFlightDeparture(String passengerToUpdate, int days) throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse(), true);
        passengerHelper.setDob(passengers.getPassengers(), passengerToUpdate, getAValidDateForInfantFromFlightDepartureDate(flightKey, days));
        basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I attempt to set the \"([^\"]*)\" apis without the \"([^\"]*)\" apis$")
    public void iAttemptToSetTheInfantApisWithoutTheApis(String passengerToKeepAPIs, String passengerToRemoveAPIs) throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse(), true);
        passengerHelper.removeAPIsFromRequestFor(passengers.getPassengers(), passengerToRemoveAPIs);
        basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I process the request for updatePassengers with documents$")
    public void iProcessTheRequestForUpdatePassengersWithDocuments() throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse());
        sentUpdate = passengers;
        basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @Then("^I should be able to store the documents successfully$")
    public void iShouldBeAbleToStoreTheDocumentsSuccessfully() throws Throwable {
        basketHelper.getBasketPassengerService().assertThat();
    }

    @Then("^I should receive both \"([^\"]*)\" and  \"([^\"]*)\" error messages$")
    public void iShouldReceiveAndErrorMessageStatingThatDateOfBirthDoesNotMatchWithPassengerType(String err1, String err2) throws Throwable {
        basketHelper.getBasketPassengerService().assertThatErrors().containedTheCorrectErrorMessage(err1, err2);
    }

    private String getAValidDateForAgeFromFlightDepartureDate(String flightKey, int age) {
        Calendar calender = setCalendar(flightKey);
        calender.add(Calendar.YEAR, (age * -1));
        return getSimpleDateFormat(calender);
    }

    @Given("^I am using ([^\"]*) channel$")
    public void iAmUsingChannel(String channel) throws Throwable {
        testData.setChannel(channel);
        testData.setData(CHANNEL, channel);

        if (channel.trim().equals(CommonConstants.AD_CHANNEL) || channel.trim().equals(CommonConstants.AD_CUSTOMER_SERVICE)) {
            commonSteps.iLoginAsAgentWithUsernameAndPassword("rachel", "12341234");
            customerHelper.createRandomCustomer(channel);
            customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
        }
    }

    @When("^I failed to provide \"([^\"]*)\" in apis$")
    public boolean iFailedToProvideInApis(String missingField) throws Throwable {
        List<FieldAndValue> fieldAndValues = new ArrayList<>();
        fieldAndValues.add(new FieldAndValue(missingField, null));
        Passengers passengers = passengerHelper.createARequestWithAPIsWithFieldSetAs(fieldAndValues, basketHelper.getBasketService().getResponse(), thePassengerMix);
        basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
        return true;
    }

    private Calendar setCalendar(String flightKey) {
        int year = Integer.parseInt(flightKey.substring(0, 4));
        int month = Integer.parseInt(flightKey.substring(4, 6));
        int day = Integer.parseInt(flightKey.substring(6, 8));
        Calendar calender = Calendar.getInstance(); // creates calendar
        calender.set(year, month - 1, day);
        return calender;
    }

    private String getSimpleDateFormat(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }

    private String getAValidDateForInfantFromFlightDepartureDate(String flightKey, int days) {
        Calendar calender = setCalendar(flightKey);
        calender.add(Calendar.DATE, days * -1);
        return getSimpleDateFormat(calender);
    }

    @Then("^I should see error \"([^\"]*)\" with \"([^\"]*)\" missing$")
    public void iShouldSeeTheErrorWithMissing(String errorcode, String field) throws Throwable {
        basketHelper.getBasketPassengerService().assertThatErrors().containedTheCorrectErrorMessage(errorcode);
    }

    @When("^I set age (\\d+) for \"([^\"]*)\" and no \"([^\"]*)\" apis but for infant$")
    public void iSetAgeForAndNoAdultAssociationForInfant(int age, String passengerTypeToUpdate, String passengerToRemoveAPIs) throws Throwable {
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse(), true);
        passengerHelper.setDob(passengers.getPassengers(), passengerTypeToUpdate, getAValidDateForAgeFromFlightDepartureDate(flightKey, age));
        passengerHelper.removeAPIsFromRequestFor(passengers.getPassengers(), passengerToRemoveAPIs);
        basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @Given("^my basket contains \"([^\"]*)\" using channel \"([^\"]*)\"$")
    public void myBasketContainsUsingChannel(String passengerMix, String channel) throws Throwable {
        testData.setChannel(channel);
        thePassengerMix = passengerMix;

        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), STANDARD, false);
    }

    @When("^I validate the request to updatePassenger$")
    public void iValidateTheRequestToUpdatePassenger() throws Throwable {
        basketHelper.updatePassengersForChannel(updatePassengerRequestBody, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @And("^I should see the \"([^\"]*)\" warning message$")
    public void iShouldSeeTheWarningMessage(String warning) throws Throwable {
        basketHelper.getBasketPassengerService().assertThat().additionalInformationReturned(warning);
    }

    @And("^I will not update the passenger details$")
    public void iWillNotUpdateThePassengerDetails() throws Throwable {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        basketHelper.getBasketService().assertThat().theBasketHasNotBeenUpdatedPassengerDetails(basketHelper.getBasketService().getResponse().getBasket(), updatePassengerRequestBody);
    }

    @Then("^I will update the passenger details$")
    public void iWillUpdateThePassengerDetails() throws Throwable {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        WaitHelper.pollingLoop().untilAsserted(() -> {
            basketHelper.getBasketService().assertThat().theBasketContainsTheUpdatedPassengerDetails(basketHelper.getBasketService().getResponse().getBasket(), updatePassengerRequestBody);
        });
    }

    @And("^I receive an update passenger request that changes all passenger to a \"([^\"]*)\"$")
    public void iReceiveAnUpdatePassengerRequestThatChangesAllPassengerToA(String passengerType) throws Throwable {
        updatePassengerRequestBody = passengerHelper.createRequestUpdatePassengerMixWithType(basketHelper.getBasketService().getResponse(), passengerType);
    }

    @And("^my basket contains \"([^\"]*)\" with fare type \"([^\"]*)\" using channel \"([^\"]*)\"$")
    public void myBasketContainsWithFareTypeUsingChannel(String passengerMix, String fareType, String channel) throws Throwable {
        testData.setChannel(channel);
        thePassengerMix = passengerMix;

        basketHelper.associateCustomerProfileToBasket(testData.getChannel(), theStaffCustomerUID);
        getStaffProfile();


        basketHelper.myBasketContainsAFlightWithPassengerMixForStaff(passengerMix, channel, fareType);
    }

    @And("^I receive an update passenger request that changes an Adult passenger to an infant with ratio that exceeds the threshold$")
    public void iReceiveAnUpdatePassengerRequestThatChangesAnAdultPassengerToAnInfantWithRatioThatExceedsTheThreshold() throws Throwable {
        updatePassengerRequestBody = passengerHelper.createRequestUpdatePassengerMixWithExceeds(basketHelper.getBasketService().getResponse());
    }

    @And("^I receive an update passenger request that changes all passenger to a \"([^\"]*)\" for the required fare$")
    public void iReceiveAnUpdatePassengerRequestThatChangesAllPassengerToAForTheRequiredFare(String passengerType) throws Throwable {
        updatePassengerRequestBody = passengerHelper.createRequestUpdatePassengerMixWithType(basketHelper.getBasketService().getResponse(), passengerType);

        theChosenName = updatePassengerRequestBody.getPassengers().get(0).getPassengerDetails().getName();

        theChosenName = updateNameOfSignificantOtherForStaff(theChosenName);

        updatePassengerRequestBody.getPassengers().get(0).getPassengerDetails().setName(theChosenName);
    }

    @And("^I am a staff customer logged in with credential \"([^\"]*)\" and \"([^\"]*)\"$")
    public void iAmAStaffCustomerLoggedInWithCredentialAnd(String user, String pwd) throws Throwable {
        customerHelper.loginWithValidCredentials(StringUtils.EMPTY, user, pwd, false);
    }

    @And("^I receive a request to change passenger from \"([^\"]*)\" to a \"([^\"]*)\"$")
    public void iReceiveARequestToChangePassengerFromToA(String passengerFromChange, String passengerChanged) throws Throwable {
        updatePassengerRequestBody = passengerHelper.createRequestUpdatePassengerAge(basketHelper.getBasketService().getResponse().getBasket(), passengerFromChange, passengerChanged);
    }

    @When("^I provide ejplus details as (.*) and (.*) and age (\\d+)$")
    public void iProvideEjplusDetailsAsAlexanderAnd(String lastName, String ejNumber, int age) throws Throwable {
        testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
        Passengers passengers = passengerHelper.createRequestWithMultipleFieldsSetAs(lastName, ejNumber, age, basketHelper.getBasketService().getResponse(), thePassengerMix);
        sentUpdate = passengers;
        basketHelper.updatePassengersForChannel(passengers, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I send a request to addSeat and change Age with (.*) with (.*) and (.*) fare type and (.*) to (.*) with (.*) additional Seat$")
    public void iSendARequestToAddSeatAndChangeAgePassengerWithSeat(String passengerMix, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct, String fareType, String passengerFromChange, String passengerChanged, Integer additionalSeat) throws Throwable {
        purchasedSeatHelper.addSeat(passengerMix, aSeatProduct, fareType, additionalSeat);
        updatePassengerRequestBody = passengerHelper.createRequestUpdatePassengerAge(basketHelper.getBasketService().getResponse().getBasket(), passengerFromChange, passengerChanged);
        basketHelper.updatePassengersForChannel(updatePassengerRequestBody, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        basketHelper.getBasketService().assertThat().theBasketContainsTheUpdatedPassengerDetails(basketHelper.getBasketService().getResponse().getBasket(), updatePassengerRequestBody);
    }

    @Then("^infant on own seat associated to the first adult passenger until the ratio of adult to infant on own seat is reached$")
    public void infantOnOwnSeatAssociatedToTheFirstAdultPassengerUntilTheRatioOfAdultToInfantOnOwnSeatIsReached() throws EasyjetCompromisedException {

        readPassengerCountBasedOnMyRequest(thePassengerMix);

        int thresholdInfantOOSPerAdult = getThresholdInfantOOSPerAdult();

        assertThat(infantOnOwnSeatCount).isLessThanOrEqualTo(thresholdInfantOOSPerAdult * adultCount);

        readPassengersBasenOnOrderEntryNumber(testData.getData(BASKET_SERVICE));

        for (Basket.Passenger adultPassenger : sortedAdultPassengers) {
            assertThat(adultPassenger.getInfantsOnSeat().size() <= thresholdInfantOOSPerAdult).isTrue();
        }

        assertThat(
                sortedAdultPassengers
                        .stream()
                        .mapToInt(passenger -> passenger.getInfantsOnSeat().size())
                        .sum()
        )
                .isEqualTo(infantOnOwnSeatCount);

        for (int i = infantOnOwnSeatCount; i > 0; i--) {
            if (i % thresholdInfantOOSPerAdult == 0) {
                assertThat(sortedAdultPassengers.get((i / thresholdInfantOOSPerAdult) - 1).getInfantsOnSeat().contains(sortedInfantOnSeatPassengers.get(i - 1).getCode())).isTrue();
            } else {
                assertThat(sortedAdultPassengers.get((i - (i % thresholdInfantOOSPerAdult)) / thresholdInfantOOSPerAdult).getInfantsOnSeat().contains(sortedInfantOnSeatPassengers.get(i - 1).getCode())).isTrue();
            }
        }
    }

    private void readPassengersBasenOnOrderEntryNumber(BasketService basketService) {
        sortedAdultPassengers = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(bound -> bound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase("adult"))
                .sorted((p1, p2) -> new Integer(p1.getFareProduct().getOrderEntryNumber()).compareTo(new Integer(p2.getFareProduct().getOrderEntryNumber())))
                .collect(toList());

        sortedInfantOnSeatPassengers = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(bound -> bound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase("infant"))
                .filter(passenger -> !passenger.getFareProduct().getBundleCode().equalsIgnoreCase("infantonlap"))
                .sorted((p1, p2) -> new Integer(p1.getFareProduct().getOrderEntryNumber()).compareTo(new Integer(p2.getFareProduct().getOrderEntryNumber())))
                .collect(toList());
    }

    private int getThresholdInfantOOSPerAdult() throws EasyjetCompromisedException {
        List<String> propertyValues = propertyValueConfigurationDao.getPropertyValuesBasedOnName(testData.getChannel(), ADULTINFANTRATIOPROPERTYVALUE);

        String ratio = propertyValues.stream().filter(value -> value.contains(":")).findAny().orElseThrow(() -> new EasyjetCompromisedException("ratio not configured"));

        return Integer.parseInt(Arrays.asList(ratio.split(":")).get(1));
    }

    private void readPassengerCountBasedOnMyRequest(String thePassengerMix) {
        List<String> pax = Arrays.asList(thePassengerMix.split(","));
        for (String passenger : pax) {
            if (containsIgnoreCase(passenger, "adult")) {
                adultCount = Integer.parseInt(Arrays.asList(passenger.toLowerCase().split("adult")).get(0).trim());
            } else if (containsIgnoreCase(passenger, "infant oos")) {
                infantOnOwnSeatCount = Integer.parseInt(Arrays.asList(passenger.toLowerCase().split("infant oos")).get(0).trim());
            }
        }
    }
}
