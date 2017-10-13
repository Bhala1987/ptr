package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Customer;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetAPIRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PassengerApisFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.DependantsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.SetAPIRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateCustomerDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ACCESS_TOKEN;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_CUSTOMER_PROFILE_SERVICE;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by dwebb on 12/2/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetCustomerProfileSteps {
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper commitBookingHelper;
    @Autowired
    private CustomerProfileHelper customerProfileHelper;
    @Autowired
    private SavedPassengerHelper savedPassengerHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private HybrisServiceFactory servicefactory;
    @Autowired
    private SerenityFacade testData;
    @Steps
    private SerenityReporter reporter;
    private FlightsService flightsService;
    private CommitBookingService commitBookingService;
    private GetBookingService getBookingService;
    private SetAPIHelper setAPIHelper = new SetAPIHelper();
    private CustomerProfileService customerProfileService;
    private GetDependantsService dependantsService;
    private UpdateCustomerDetailsService updateCustomerDetailsService;
    private SetAPIService setAPIService;
    private CustomerModel dbCustomer;
    private Customer body;
    private String dateOfBirth = "1990-12-12", documentExpiryDate = "2050-12-12", documentType = "PASSPORT", gender = "MALE", nationality = "GBR", countryOfIssue = "GBR", fullName = "Tony Henry", documentNumber = "AK123456";
    private String customerId;
    private String passengerId;


    @Given("^a customer profile exists$")
    public void aCustomerProfileExists() throws EasyjetCompromisedException {
        customerHelper.createRandomCustomer("Digital");
        dbCustomer = customerProfileHelper.getCustomerById(testData.getData(CUSTOMER_ID));
    }

    @When("^I search for the profile$")
    public void iSearchForTheProfile() throws Throwable {
        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(dbCustomer.getUid()).path(PROFILE).build();
        customerProfileService = servicefactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValidWithToken("Digital", testData.getAccessToken()).build(), profilePathParams, null));
        pollingLoop().untilAsserted(() -> {
            customerProfileService.invoke();
            assertThat(customerProfileService.getResponse().getCustomer().getBasicProfile()).isNotNull();
        });
    }

    @Then("^a profile is returned$")
    public void aProfileIsReturned() throws Throwable {
        customerProfileService.assertThat().theProfileContainsBasicData(dbCustomer);
    }

    @Given("^a customer profile does not exist$")
    public void aCustomerProfileDoesNotExist() throws Throwable {
        dbCustomer = new CustomerModel("YUT2367776", "YUT2367776", "", "", "", "", "", "", "", "", "");
    }

    @Then("^a profile error is returned$")
    public void aProfileErrorIsReturned() throws Throwable {
        customerProfileService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100041_1001");
    }

    @Given("^I create a Customer$")
    public void iCreateACustomer() throws Throwable {
        customerHelper.customerAccountExistsWithAKnownPassword();
        customerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
        testData.setData(CUSTOMER_ID, customerId);
    }

    @And("^I sent a request to UpdateCustomerDetail$")
    public void iSentARequestToUpdateCustomerDetail() throws Throwable {
        Customer.ContactAddress.ContactAddressBuilder contactAddressBuilder = Customer.ContactAddress.builder()
                .addressLine1("35, Main Street")
                .addressLine2("Flat 2B")
                .addressLine3("")
                .city("Oxford")
                .country("GBR")
                .postalCode("OX11 2ES");
        Customer.PersonalDetails.KeyDates.KeyDatesBuilder keyDatesBuilder = Customer.PersonalDetails.KeyDates.builder()
                .type("graduation")
                .month("12")
                .day("31");
        Customer.PersonalDetails.PersonalDetailsBuilder personalDetailsBuilder = Customer.PersonalDetails.builder()
                .email(UUID.randomUUID().toString().replace("-", "").substring(0, 9) + "@simulator.amazonses.com")
                .type("adult")
                .age(25)
                .title("mr")
                .firstName("Tony")
                .lastName("Henry")
                .ejPlusCardNumber("00453560")
                .nifNumber("876765512")
                .phoneNumber("774012854")
                .alternativePhoneNumber("0200123821")
                .flightClubId("543443")
                .flightClubExpiryDate("2017-02-09")
                .keyDates(new ArrayList<Customer.PersonalDetails.KeyDates>() {{
                              add(keyDatesBuilder.build());
                          }}
                );
        body = Customer.builder()
                .personalDetails(personalDetailsBuilder.build())
                .contactAddress(new ArrayList<Customer.ContactAddress>() {{
                    add(contactAddressBuilder.build());
                }})
                .build();

        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(PROFILE).build();
        updateCustomerDetailsService = servicefactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(HybrisHeaders.getValid("Digital").build(), pathParams, body));
        updateCustomerDetailsService.invoke();
    }

    @And("^I sent a request to SavedPassenger$")
    public void iSentARequestToSavedPassenger() throws Throwable {
        savedPassengerHelper.addValidPassengerToExistingCustomer(testData.getData(CUSTOMER_ID));

    }

    @And("^I sent a request to SetAPI$")
    public void iSentARequestToSetAPI() throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        SetAPIRequestBody requestBody = PassengerApisFactory.aCustomerPassengerApis(dateOfBirth, documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, fullName);
        setAPIService = servicefactory.setApi(new SetAPIRequest(HybrisHeaders.getValid("Digital").build(), params, requestBody));
        setAPIService.invoke();

        String documentId = setAPIService.getResponse().getDocumentId();
        testData.setDocumentId(documentId);
    }

    @And("^I sent a request to UpdateSpecialRequest$")
    public void iSentARequestToUpdateSpecialRequest() throws Throwable {
        savedPassengerHelper.aValidRequestToCreateASSR();
        savedPassengerHelper.addSSRsFromRequest();
    }

    @And("^I sent a request to CreateIdentityDocument$")
    public void iSentARequestToCreateIdentityDocument() throws Throwable {
        savedPassengerHelper.addValidIdentityDocumentToToExistingPassenger();
    }

    @And("^I sent a request to SearchFlight \"([^\"]*)\"$")
    public void iSentARequestToSearchFlight(String channel) throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), "1 adult", testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
    }

    @And("^I sent a request to AddFlight \"([^\"]*)\"$")
    public void iSentARequestToAddFlight(String channel) throws Throwable {
        basketHelper.addFlightToBasketAsChannelUsingFlightCurrency(
                flightsService.getOutboundFlight(), channel, flightsService.getResponse().getCurrency());
    }

    @And("^I sent a request to CommitBooking \"([^\"]*)\"$")
    public void iSentARequestToCommitBooking(String channel) throws Throwable {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), channel);
        CommitBookingRequest commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService().getResponse(), testData.getData(CUSTOMER_ID), channel, true);
        commitBookingService = servicefactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
    }

    @And("^I request the customer profile from Digital, PublicApiMobile or PublicApiB2B$")
    public void iRequestTheCustomerProfileFromDigital() {
        String[] channels = {"Digital", "PublicApiMobile", "PublicApiB2B"};
        int rnd = new Random().nextInt(channels.length);
        reporter.info(channels[rnd] + " channel selected");
        testData.setChannel(channels[rnd]);
        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(PROFILE).build();
        customerProfileService = servicefactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValid(testData.getChannel()).build(), profilePathParams, null));
        customerProfileService.invoke();
        testData.setData(GET_CUSTOMER_PROFILE_SERVICE, customerProfileService);
    }

    @Then("^I will receive the updated values to the channel$")
    public void iWillReceiveTheUpdatedValuesToTheChannel() throws Throwable {
        customerProfileService.assertThat().theProfileContainsData();
    }

    @Then("^I will receive the saved passenger for the customer to the channel$")
    public void iWillReceiveTheSavedPassengerForTheCustomerToTheChannel() throws Throwable {
        customerProfileService.assertThat().theProfileContainsPassenger();
    }

    @When("^I receive a request to getSavedPassenger from the channel$")
    public void iReceiveARequestToGetSavedPassengerFromTheChannel() throws Throwable {
        customerProfileService.invoke();
    }

    @Given("^a customer has saved passenger information$")
    public void aCustomerHasSavedPassengerInformation() throws Throwable {
        savedPassengerHelper.addValidPassengerToExistingCustomer();
        customerId = savedPassengerHelper.getCustomerId();
        passengerId = savedPassengerHelper.getPassengerId();
    }

    @Then("^I I will return a list of Saved Passengers associated to the customer$")
    public void iIWillReturnAListOfSavedPassengersAssociatedToTheCustomer() throws Throwable {
        customerProfileService.assertThat().theProfileContainsPassenger();
    }

    @Given("^that the \"([^\"]*)\" has initiated a request to getDependents$")
    public void thatTheHasInitiatedARequestToGetDependents(String channel) throws Throwable {
        aCustomerHasSavedPassengerInformation();
        CustomerPathParams dependantParams = CustomerPathParams.builder().customerId(customerId).path(DEPENDANTS_SERVICE_PATH).build();
        dependantsService = servicefactory.getDependantsService(new DependantsRequest(HybrisHeaders.getValid(channel).build(), dependantParams));
    }

    @When("^I receive a request to getDependents from the channel$")
    public void iReceiveARequestToGetDependentsFromTheChannel() throws Throwable {
        dependantsService.invoke();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    @Then("^I should add error message \"([^\"]*)\" to the Dependants return message$")
    public void iShouldAddErrorMessageToTheDependantsReturnMessage(String error) throws Throwable {
        dependantsService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Given("^a customer profile exists with status (.*)$")
    public void aCustomerProfileExistsWithStatus(String status) throws EasyjetCompromisedException {
        if (status.equals("ACTIVE")) {
            customerHelper.createRandomCustomer("Digital");
            dbCustomer = customerProfileHelper.getCustomerById(testData.getData(CUSTOMER_ID));
        } else {
            dbCustomer = customerProfileHelper.findAValidCustomerProfileByStatus(status);
        }
    }

    @When("^I search a the profile using the channel (.*)$")
    public void iSearchProfileUsingChanel(String channel) throws Throwable {
        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(dbCustomer.getUid()).path(PROFILE).build();
        customerProfileService = servicefactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValid(channel).build(), profilePathParams, null));
        customerProfileService.invoke();
    }

    @Then("^a profile is returned with the result as (.*)$")
    public void aProfileIsReturnedWithResultAs(String result) throws Throwable {
        if (result.equalsIgnoreCase("error")) {
            customerProfileService.assertThatErrors().containedTheCorrectErrorMessage(customerProfileService.getErrors().getErrors().get(0).getCode());
        } else if (result.equalsIgnoreCase("success")) {
            customerProfileService.assertThat().theProfileContainsBasicData(dbCustomer);
        }
    }

    @Then("^a get profile \"([^\"]*)\" is returned$")
    public void aGetProfileIsReturned(String errorCode) throws Throwable {
        CustomerProfileService customerProfileService = testData.getData(GET_CUSTOMER_PROFILE_SERVICE);
        customerProfileService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Then("^the temporary profile is returned for the channel$")
    public void theTemporaryProfileIsReturnedForTheChannel() throws Throwable {
        customerProfileService = testData.getData(SerenityFacade.DataKeys.SERVICE);
        customerProfileService.assertThat().theProfileIsValid(testData.getData(CUSTOMER_ID));
    }

    @Then("^get temporary profile return (.*)$")
    public void getTemporaryProfileReturnSVC__(String errorCode) throws Throwable {
        customerProfileService = testData.getData(SerenityFacade.DataKeys.SERVICE);
        customerProfileService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Then("^update service return (.*) for update request attempt$")
    public void updateServiceReturnSVC__ForUpdateRequestAttempt(String errorCode) throws Throwable {
      updateCustomerDetailsService =  testData.getData(SerenityFacade.DataKeys.SERVICE);
      updateCustomerDetailsService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }
}


