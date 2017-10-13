package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.models.ChannelPropertiesModel;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.asserters.CustomerRecentSearchAssertion;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.factory.FlightQueryParamsFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory.getRandomEmail;


@ContextConfiguration(classes = TestApplication.class)

public class SaveSearchToAProfileSteps {

    private static final String DATE_PATTERN = "dd-MM-yyyy";
    @Autowired
    FlightFinder flightFinder;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private SaveRecentSearchHelper saveRecentSearchHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerRecentSearchAssertion customerRecentSearchAssertion;
    @Autowired
    private ChannelPropertiesHelper channelPropertiesHelper;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private SerenityFacade testData;
    private FlightsService findFlightsService;
    private FlightQueryParams queryParams;
    private String customerId;
    private List<FlightQueryParams> paramList;
    private int maximumSavesAllowedPerCustomer;

    @When("^I made a new search for flights successfully$")
    public void i_made_a_new_search_for_flights() throws Throwable {
        testData.setPassengerMix("1 Adult, 1 Child, 0 Infant");
        findFlightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
        queryParams = flightHelper.getParam();
    }

    @And("^I made the same search again$")
    public void iMadeTheSameSearchAgain() throws Throwable {
        findFlightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("Digital").build(), queryParams));
        findFlightsService.invoke();
        findFlightsService.wasSuccessful();
    }

    @Then("^this search should be saved$")
    public void search_should_be_saved() throws Throwable {
        saveRecentSearchHelper.invokeRecentSearchServiceFor(customerId);
        customerRecentSearchAssertion.readRecentSearchesFor(customerId);
        customerRecentSearchAssertion.recentSearchShouldContains(queryParams);
        saveRecentSearchHelper.getRecentSearchService().assertThat().recentSearchShouldContains(queryParams);
    }

    @Then("^search should be saved only once$")
    public void search_should_be_saved_only_once() throws Throwable {
        saveRecentSearchHelper.invokeRecentSearchServiceFor(customerId);
        customerRecentSearchAssertion.readRecentSearchesFor(customerId);
        customerRecentSearchAssertion.totalRecentSearchesShouldBe(1).recentSearchShouldContains(queryParams);
        saveRecentSearchHelper.getRecentSearchService().assertThat().totalRecentSearchesShouldBe(1).recentSearchShouldContains(queryParams);
    }

    @And("^oldest search should be removed$")
    public void oldestSearchShouldBeRemoved() throws Throwable {
        customerRecentSearchAssertion.readRecentSearchesFor(customerId);
        customerRecentSearchAssertion.oldestSearchShouldBeRemoved(paramList.get(0));
    }

    @Then("^I should see two searches saved to the profile$")
    public void iShouldSeeTwoSearchesSavedToTheProfile() throws Throwable {
        saveRecentSearchHelper.invokeRecentSearchServiceFor(customerId);
        customerRecentSearchAssertion.readRecentSearchesFor(customerId);
        customerRecentSearchAssertion.totalRecentSearchesShouldBe(2);
        saveRecentSearchHelper.getRecentSearchService().assertThat().totalRecentSearchesShouldBe(2);
    }

    @When("^I retrieve the recent searches$")
    public void iRetrieveTheRecentSearches() throws Throwable {
        saveRecentSearchHelper.invokeRecentSearchServiceFor(customerId);
    }

    @Then("^I should see no search results$")
    public void iShouldSeeNoSearchResults() throws Throwable {
        saveRecentSearchHelper.invokeRecentSearchServiceFor(customerId);
        customerRecentSearchAssertion.readRecentSearchesFor(customerId);
        customerRecentSearchAssertion.totalRecentSearchesShouldBe(0);
        saveRecentSearchHelper.getRecentSearchService().assertThat().totalRecentSearchesShouldBe(0);
    }

    @And("^that the configurations is in place for maximum saves allowed$")
    public void configurationsIsInPlaceForMaximumSavesAllowed() throws Throwable {
        this.maximumSavesAllowedPerCustomer = readMaxAllowedSearchesFromConfig();
    }

    @And("^I made another new search for flights from a different session$")
    public void iMadeAnotherNewSearchForFlightsFromADifferentSession() throws Throwable {
        customerHelper.loginWithValidCredentials();
        queryParams.setOutboundDate(new DateFormat().addDay(2,testData.getOutboundDate()));
        findFlightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("Digital").build(), queryParams));
        findFlightsService.invoke();
        findFlightsService.wasSuccessful();
    }

    @And("^customer has \"([^\"]*)\" searches saved to the profile$")
    public void customerHasSavedToTheProfile(String searches) throws Throwable {
        customerHelper.createNewCustomerProfileWithEmail(getRandomEmail(10));
        customerHelper.loginWithValidCredentials();
        this.customerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
        int searchesToPerform = searches.equals("maximum allowed") ? maximumSavesAllowedPerCustomer : Integer.parseInt(searches);
        List<HybrisFlightDbModel> uniqueValidFlightsFound = flightFinder.findUniqueValidFlights();
        searchForFlights(searchesToPerform, uniqueValidFlightsFound);
        customerRecentSearchAssertion.readRecentSearchesFor(customerId);
        customerRecentSearchAssertion.totalRecentSearchesShouldBe(searchesToPerform);
    }

    private int readMaxAllowedSearchesFromConfig() throws Exception {
        List<ChannelPropertiesModel> properties = channelPropertiesHelper.getAllChannelProperties();
        for (ChannelPropertiesModel model : properties) {
            if (model.getP_propertyname().equals("maxRecentSearchRecords"))
                return Integer.parseInt(model.getP_propertyvalue());
        }
        throw new EasyjetCompromisedException("Configuration is not in place for maximum saves to allow.");
    }

    private void searchForFlights(int searchesToPerform, List<HybrisFlightDbModel> uniqueValidFlightsFound) throws Throwable {
        int totalNumberOfUniqueFlightsFound = uniqueValidFlightsFound.size();
        if (searchesToPerform > 0) {
            if (totalNumberOfUniqueFlightsFound >= searchesToPerform) {
                paramList = new ArrayList<>();
                for (int i = 0; i < searchesToPerform; i++) {
                    FlightQueryParams params = FlightQueryParamsFactory.generateFlightSearchCriteria(
                            uniqueValidFlightsFound.get(i)).adult("1").child("0").infant("0").build();
                    paramList.add(params);
                    serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("Digital").build(), params)).invoke();
                }
            } else throw new EasyjetCompromisedException("Not enough unique flights found");
        }
    }

    @And("^I made the same search again with different outbound date$")
    public void iMadeTheSameSearchAgainWithDifferentDepartureDate() throws Throwable {
        queryParams.setOutboundDate(new DateFormat().addDay(2,testData.getOutboundDate()));
        findFlightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("Digital").build(), queryParams));
        findFlightsService.invoke();
        findFlightsService.wasSuccessful();
    }

    @And("^I made the same search again with different inbound date$")
    public void iMadeTheSameSearchAgainWithDifferentInboundDate() throws Throwable {
        findFlightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getJourneyType(), testData.getInboundDate(), testData.getInboundDate());
    }

    @When("^I made a new return search for flights successfully$")
    public void iMadeANewReturnSearchForFlightsSuccessfully() throws Throwable {
        testData.setPassengerMix("1 Adult, 1 Child");
        testData.setChannel("Digital");
        testData.setJourneyType("outbound/inbound");
        findFlightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getJourneyType(), testData.getOutboundDate(), testData.getInboundDate());
    }

    private String getModifiedOutboundDate(String outboundDate) throws ParseException {
        Date modifiedOutboundDate = saveRecentSearchHelper.addDaysToDate(saveRecentSearchHelper.getDateFromString(outboundDate, SaveSearchToAProfileSteps.DATE_PATTERN), Integer.valueOf(2));
        return saveRecentSearchHelper.getFormattedDate(modifiedOutboundDate, SaveSearchToAProfileSteps.DATE_PATTERN);
    }

    private String getFormattedInboundDate(String outboundDate, int daysToStay) throws ParseException {
        Date newOutboundDate = saveRecentSearchHelper.getDateFromString(outboundDate, SaveSearchToAProfileSteps.DATE_PATTERN);
        Date newInboundDate = saveRecentSearchHelper.addDaysToDate(newOutboundDate, Integer.valueOf(daysToStay));
        return saveRecentSearchHelper.getFormattedDate(newInboundDate, SaveSearchToAProfileSteps.DATE_PATTERN);
    }

    private String getFormattedOutboundDate() {
        Date currentDate = new Date();
        Date newDate = saveRecentSearchHelper.addDaysToDate(currentDate, Integer.valueOf(2));
        return saveRecentSearchHelper.getFormattedDate(newDate, SaveSearchToAProfileSteps.DATE_PATTERN);
    }
}