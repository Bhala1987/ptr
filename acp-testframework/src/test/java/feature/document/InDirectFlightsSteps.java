package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.helpers.AirportsForIndirectRoutes;
import com.hybris.easyjet.database.hybris.models.ChannelPropertiesModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.ChannelPropertiesHelper;
import com.hybris.easyjet.fixture.hybris.helpers.IndirectFlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by ptr-kvijayapal on 2/7/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class InDirectFlightsSteps {

    private static final String DATE_PATTERN = "dd-MM-yyyy";
    protected static Logger LOG = LogManager.getLogger(FindFlightsSteps.class);
    @Autowired
    private IndirectFlightHelper indirectFlightHelper;
    @Autowired
    private FlightFinder flightFinder;
    private List<AirportsForIndirectRoutes> airportList;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private ChannelPropertiesHelper channelPropertiesHelper;
    private FlightsService flightService;
    private String origin;
    private String destination;
    private List<String> outboundConnectingAirports;
    private List<String> inboundConnectingAirports;
    private List<String> alternateOutboundDepartureAirports;
    private List<String> alternateInboundDepartureAirports;
    private FlightQueryParams params;
    private String basketCode;
    private List<FindFlightsResponse.Flight> outboundFlights;
    private CommitBookingService commitBookingService;
    private String channelUsed = "";
    private BookingConfirmationResponse bookingResponse;
    @Autowired
    private BookingHelper commitBookingHelper;
    private List<FindFlightsResponse.Flight> outboundJourney;
    private List<FindFlightsResponse.Flight> inboundJourney;
    private BasketsResponse basketResponse;
    @Autowired
    SerenityFacade testData;

    public InDirectFlightsSteps() {
    }

    private boolean channelHasConfiguredToSearchForIndirectFlights(String channel) throws Exception {
        List<ChannelPropertiesModel> properties = channelPropertiesHelper.getChannelProperties(channel);
        for (ChannelPropertiesModel model : properties) {
            if (model.getP_propertyname().equals("indirectRoutesAllowed"))
                return model.getP_propertyvalue().equals("true");
        }
        return false;
    }

    private boolean channelHasRestrictedToSearchForIndirectFlights(String channel) throws Exception {
        return !channelHasConfiguredToSearchForIndirectFlights(channel);
    }

    @Given("^that indirect flights are configured for \"([^\"]*)\" to \"([^\"]*)\"$")
    public void thatIndirectFlightsAreAvailableForTo(String origin, String destination) throws Throwable {
        this.origin = origin;
        this.destination = destination;
    }

    @When("^I request for indirect flights for that route$")
    public void iRequestForIndirectFlightsForThatRoute() throws Throwable {

        params = FlightQueryParams.builder().origin(origin).destination(destination).outboundDate(getFormattedOutboundDate())
                .inboundDate(getFormattedInboundDate()).adult("1").child("1").infant("1").indirectOutbound("true").indirectInbound("true").build();
        flightService = serviceFactory.findFlight((new FlightsRequest(HybrisHeaders.getValid("ADAirport").build(), params)));
        flightService.invoke();
    }

    @When("^I request for indirect flights for that route with no inbound flight$")
    public void iRequestForIndirectFlightsForThatRoutewithnoInbound() throws Throwable {

        params = FlightQueryParams.builder().origin(origin).destination(destination).outboundDate(getFormattedOutboundDate())
                .adult("1").child("1").infant("1").indirectOutbound("true").build();
        flightService = serviceFactory.findFlight((new FlightsRequest(HybrisHeaders.getValid("ADAirport").build(), params)));
        flightService.invoke();
    }

    @Then("^indirect flights are returned$")
    public void indirectFlightsAreReturned() throws Throwable {
        flightService
                .assertThat()
                .allFlightsReturnedAreInDirect()
                //.allOutboundJourneysAreValid(origin, destination, outboundConnectingAirports, alternateOutboundDepartureAirports)
                .allInboundJourneysAreValid(destination, origin, inboundConnectingAirports, alternateInboundDepartureAirports);
    }

    @Given("^\"([^\"]*)\" has configured to search for indirect flights$")
    public void hasConfiguredToSearchForIndirectFlights(String channel) throws Throwable {
        assertTrue(channelHasConfiguredToSearchForIndirectFlights(channel));
    }

    @When("^I request for indirect flights for that route from \"([^\"]*)\"$")
    public void iRequestForIndirectFlightsForThatRouteFrom(String channel) throws Throwable {
        channelUsed = channel;
        params = FlightQueryParams.builder().origin(origin).destination(destination).outboundDate(getFormattedOutboundDate())
                .inboundDate(getFormattedInboundDate()).adult("1").child("1").infant("1").indirectOutbound("true").indirectInbound("true").build();
        flightService = serviceFactory.findFlight((new FlightsRequest(HybrisHeaders.getValid(channel).build(), params)));
        flightService.invoke();
        testData.setData(GET_FLIGHT_SERVICE, flightService);
      //  testData.setOutboundFlights(flightService.getOutboundFlights());
    }

    @Given("^\"([^\"]*)\" has not configured to search for indirect flights$")
    public void hasNotConfiguredToSearchForIndirectFlights(String channel) throws Throwable {
        assertTrue(channelHasRestrictedToSearchForIndirectFlights(channel));
    }

    @Then("^I should see invalid channel error message$")
    public void iShouldSeeAnErrorMessage() throws Throwable {
        flightService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100148_3017");
    }

    @Given("^outbound indirect flights are available from alternate departure airports for \"([^\"]*)\" to \"([^\"]*)\"$")
    public void outboundIndirectFlightsAreAvailableFromAlternateAirportsForTo(String origin, String destination) throws Throwable {
        this.origin = origin;
        this.destination = destination;
        indirectFlightHelper.readAllIndirectRoutesFromHybris().indirectRoutesConfiguredInHybrisFor(this.origin, this.destination);
    }

    @Given("^inbound indirect flights are available from alternate departure airports for \"([^\"]*)\" to \"([^\"]*)\"$")
    public void inboundIndirectFlightsAreAvailableFromAlternateAirportsForTo(String origin, String destination) throws Throwable {
        this.origin = origin;
        this.destination = destination;
        indirectFlightHelper.readAllIndirectRoutesFromHybris().indirectRoutesConfiguredInHybrisFor(this.destination, this.origin);
    }

    @When("^I request indirect flights for outbound$")
    public void iRequestIndirectFlightsForOutbound() throws Throwable {
        params = FlightQueryParams.builder().origin(origin).destination(destination).outboundDate(getFormattedOutboundDate())
                .adult("1").indirectOutbound("true").build();
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("ADAirport").build(), params));
        flightService.invoke();
    }

    @When("^I request indirect flights for inbound$")
    public void iRequestIndirectFlightsForInbound() throws Throwable {
        params = FlightQueryParams.builder().origin(origin).destination(destination).outboundDate(getFormattedOutboundDate())
                .inboundDate(getFormattedInboundDate()).adult("1").indirectOutbound("true").indirectInbound("true").build();
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("ADAirport").build(), params));
        flightService.invoke();
    }

    @Then("^indirect flights are returned to include the alternate outbound departure airports$")
    public void indirectFlightsAreReturnedToIncludeTheAlternateOutboundDepartureAirports() throws Throwable {
        List<String> outboundConnectingAirports = indirectFlightHelper.getAllConnectingAirportsForRoute(origin, destination);
        List<String> alternateOutboundDepartureAirports = indirectFlightHelper.getListOfAlternateAirportFor(origin);
        flightService.assertThat().allFlightsReturnedAreInDirect().allOutboundJourneysAreValid(origin, destination, outboundConnectingAirports, alternateOutboundDepartureAirports);
    }

    @Then("^indirect flights are returned to include the alternate inbound departure airports$")
    public void indirectFlightsAreReturnedToIncludeTheAlternateInboundDepartureAirports() throws Throwable {
        List<String> inboundConnectingAirports = indirectFlightHelper.getAllConnectingAirportsForRoute(destination, origin);
        List<String> alternateInboundDepartureAirports = indirectFlightHelper.getListOfAlternateAirportFor(destination);
        flightService.assertThat().allFlightsReturnedAreInDirect().allInboundJourneysAreValid(destination, origin, inboundConnectingAirports, alternateInboundDepartureAirports);
    }

    @Given("^that indirect flights are available for \"([^\"]*)\" to \"([^\"]*)\" on \"([^\"]*)\" flexible days too$")
    public void thatIndirectFlightsAreAvailableForToOnDaysToo(String origin, String destination, String flexiDays) throws Throwable {
        this.origin = origin;
        this.destination = destination;
        indirectFlightHelper.readAllIndirectRoutesFromHybris().indirectRoutesConfiguredInHybrisFor(this.origin, this.destination).indirectRoutesConfiguredInHybrisFor(this.destination, this.origin);
        this.outboundConnectingAirports = indirectFlightHelper.getAllConnectingAirportsForRoute(this.origin, this.destination);
        this.inboundConnectingAirports = indirectFlightHelper.getAllConnectingAirportsForRoute(this.destination, this.origin);
        this.alternateOutboundDepartureAirports = indirectFlightHelper.getListOfAlternateAirportFor(this.origin);
        this.alternateInboundDepartureAirports = indirectFlightHelper.getListOfAlternateAirportFor(this.destination);
        indirectFlightHelper.readAllIndirectRoutesFromHybris().indirectRoutesConfiguredInHybrisFor(this.origin, this.destination);
    }

    @When("^I request for indirect flights for \"([^\"]*)\" flexible days$")
    public void iRequestForIndirectFlightsForFlexibleDays(String flexibleDays) throws Throwable {
        params = FlightQueryParams.builder().origin(origin).destination(destination).outboundDate(getFormattedOutboundDate())
                .inboundDate(getFormattedInboundDate()).adult("1").indirectOutbound("true").indirectInbound("true").flexibleDays(flexibleDays).build();
        flightService = serviceFactory.findFlight((new FlightsRequest(HybrisHeaders.getValid("ADAirport").build(), params)));
        flightService.invoke();
    }

    @Then("^return indirect flights only for the requested day$")
    public void returnIndirectFlightsOnlyForTheRequestedDay() throws Throwable {
        flightService.assertThat().allOutboundJourneysHasDepartureDateAs(params.getOutboundDate());
    }

    @Given("^indirect flights are available for \"([^\"]*)\" to \"([^\"]*)\" for more than the maximum allowed duration$")
    public void indirectFlightsAreAvailableForToForMoreThanTheMaximumAllowedDuration(String origin, String destination) throws Throwable {
        this.origin = origin;
        this.destination = destination;
        indirectFlightHelper.readAllIndirectRoutesFromHybris().indirectRoutesConfiguredInHybrisFor(this.origin, this.destination);
        this.outboundConnectingAirports = indirectFlightHelper.getAllConnectingAirportsForRoute(this.origin, this.destination);
        this.alternateOutboundDepartureAirports = indirectFlightHelper.getListOfAlternateAirportFor(this.origin);
        this.alternateInboundDepartureAirports = indirectFlightHelper.getListOfAlternateAirportFor(this.destination);
        indirectFlightHelper.readAllIndirectRoutesFromHybris().indirectRoutesConfiguredInHybrisFor(this.origin, this.destination);
    }

    @Given("^indirect flights are available for \"([^\"]*)\" to \"([^\"]*)\" with less connection time than configured$")
    public void indirectFlightsAreAvailableForToWithLessConnectionTimeThanConfigured(String origin, String destination) throws Throwable {
        this.origin = origin;
        this.destination = destination;
        indirectFlightHelper.readAllIndirectRoutesFromHybris().indirectRoutesConfiguredInHybrisFor(this.origin, this.destination).indirectRoutesConfiguredInHybrisFor(this.destination, this.origin);
        this.outboundConnectingAirports = indirectFlightHelper.getAllConnectingAirportsForRoute(this.origin, this.destination);
        this.inboundConnectingAirports = indirectFlightHelper.getAllConnectingAirportsForRoute(this.destination, this.origin);
        this.alternateOutboundDepartureAirports = indirectFlightHelper.getListOfAlternateAirportFor(this.origin);
        this.alternateInboundDepartureAirports = indirectFlightHelper.getListOfAlternateAirportFor(this.destination);
        indirectFlightHelper.readAllIndirectRoutesFromHybris().indirectRoutesConfiguredInHybrisFor(this.origin, this.destination);

    }

    @Then("^indirect flights are returned in journey time ascending$")
    public void indirectFlightsAreReturnedInJourneyTimeAscending() throws Throwable {
        flightService.assertThat().resultsAreInAscendingOrderOfTheirTotalDuration();
    }

    private String getFormattedOutboundDate() {
        Date currentDate = new Date();
        Date newDate = addDaysToDate(currentDate, Integer.valueOf(2));
        return getFormattedDate(newDate, DATE_PATTERN);
    }

    private String getFormattedFuterDateDaysBy(int numberOfDays) {
        Date currentDate = new Date();
        Date newDate = addDaysToDate(currentDate, Integer.valueOf(numberOfDays));
        return getFormattedDate(newDate, DATE_PATTERN);
    }

    public String getFormattedDate(Date myDate, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(myDate);
    }

    public Date addDaysToDate(Date dateToBeModified, Integer daysToAdd) {
        if (dateToBeModified != null && daysToAdd != null) {
            Calendar newDate = Calendar.getInstance(); // creates calendar
            newDate.setTime(dateToBeModified); // sets calendar time/date
            newDate.add(Calendar.DAY_OF_MONTH, daysToAdd); // adds two hours
            return newDate.getTime();
        } else {
            return null;
        }
    }

    private String getFormattedInboundDate() throws ParseException {
        Date currentDate = new Date();
        Date newDate = addDaysToDate(currentDate, Integer.valueOf(9));
        return getFormattedDate(newDate, DATE_PATTERN);

        /*Date newOutboundDate = getDateFromString(outboundDate, DATE_PATTERN);
        Date newInboundDate = addDaysToDate(newOutboundDate, Integer.valueOf(10));
        return getFormattedDate(newInboundDate, DATE_PATTERN);*/
    }

    public Date getDateFromString(String myDate, String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.parse(myDate);
    }


    @Then("^indirect flights with less than than (.*) are not returned$")
    public void indirectFlightsWithLessThanThanMinimumConnectionTimeAreNotReturned(long connectionTime) throws Throwable {
        flightService.assertThat().noFlightsAreReturnedWithLessThanMinimumConnectionTime(connectionTime);
    }


    @Then("^indirect flights with more than (.*) minutes duration are not returned$")
    public void indirectFlightsWithMoreThanMaximumDurationMinutesDurationAreNotReturned(long maximumDuration) throws Throwable {
        flightService.assertThat().noFlightsAreReturnedWithDurationGreaterThanMaximumAllowed(maximumDuration);
    }

    @When("^I add the flight to my basket for indirect routes$")
    public void iAddTheFlightToMyBasketForIndirectRoutes() throws Throwable {
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        basketResponse = basketHelper.addInDirectFlightToBasket(flightsService.getOutboundJourneys(), channelUsed, flightService.getResponse().getCurrency(), CommonConstants.OUTBOUND);
//        basketResponse = basketHelper.addInDirectFlightToBasket(flightsService.getInBoundJourneys(), channelUsed, flightService.getResponse().getCurrency(),CommonConstants.INBOUND);
        basketCode = basketHelper.getBasketService().getResponse().getBasket().getCode();
    }

    @Then("^all the indirect flights are added to my basket$")
    public void allTheIndirectFlightsAreAddedToMyBasket() throws Throwable {
        flightService.assertThat().allFlightsReturnedAreInDirect();
        basketHelper.getBasket(basketCode);
        flightService.assertThat().allOutboundJourneysAreAddedToBasket(testData.getOutboundFlights(), basketHelper.getBasketService().getResponse());
    }

    @Then("^the inventory is allocated to all the flights$")
    public void theInventoryIsAllocatedToAllTheFlights() throws Throwable {
        //make sure that basket is empty before adding stuff in
        basketCode = basketHelper.getBasketService().getResponse().getBasket().getCode();
        basketHelper.emptyBasket(basketCode, channelUsed);
    }

    @Then("^the base price and associated taxes are the same in the basket for indirect flight$")
    public void theBasePriceAndAssociatedTaxesAreTheSameInTheBasketForIndirectFlight() throws Throwable {
        basketHelper.getBasketService().assertThat().flightBasePriceAndFeesAreTheSameForASinglePassenger(
                flightService.getResponse().getInbound().getJourneys().get(0).getFlights().get(0));
    }

    @And("^I have requested the allocation for the requested sectors from \"([^\"]*)\"$")
    public void iHaveRequestedTheAllocationForTheRequestedSectors(String channel) throws Throwable {
        if (!channel.equals("ADAirport")) {
            CommitBookingRequest commitBookingRequest;

            commitBookingRequest = commitBookingHelper.createNewBookingRequestForChannelBasedOnBasket(basketHelper.getBasketService()
                    .getResponse(), channelUsed);

            commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
            commitBookingService.invoke();
            commitBookingService.assertThat().gotAValidResponse();
            bookingResponse = commitBookingService.getResponse();
        }
    }

    @When("^I add the flight to my basket for indirect routes from \"([^\"]*)\"$")
    public void iAddTheFlightToMyBasketForIndirectRoutesFrom(String channel) throws Throwable {
        outboundJourney = flightService.getOutboundFlights();
        basketHelper.addInDirectFlightToBasket(flightService.getOutboundJourneys(), channel, flightService.getResponse().getCurrency(),"outbound");
        basketCode = basketHelper.getBasketService().getResponse().getBasket().getCode();
    }

    @When("^I try adding the flight to my basket for indirect routes from \"([^\"]*)\"$")
    public void iTryAddingTheFlightToMyBasketForIndirectRoutesFrom(String channel) throws Throwable {
        outboundJourney = flightService.getOutboundFlights();
        basketHelper.attemptToAddInDirectFlightToBasketAsChannelUsingFlightCurrency(flightService.getOutboundJourneys(), channel, flightService.getResponse().getCurrency());
    }

    @Then("^error \"([^\"]*)\" should be returned$")
    public void errorShouldBeReturned(String errorCode) throws Throwable {
        basketHelper.getBasketService().assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Given("^I have a direct flight from \"([^\"]*)\" to \"([^\"]*)\" in the basket added already$")
    public void iHaveADirectFlightFromToInTheBaskedAddedAlready(String origin, String destination) throws Throwable {
        params = FlightQueryParams.builder().origin(origin).destination(destination).outboundDate(getFormattedOutboundDate())
                .adult("1").build();
        flightService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("ADAirport").build(), params));
        flightService.invoke();
        basketHelper.addAFlightToMyBasket(flightService.getOutboundFlight(),testData.getChannel(),false);
    }

    @Then("^all flights including bundle is added to the basket per passenger$")
    public void allFlightsIncludingBundleIsAddedToTheBasketPerPassenger() throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^Fees and Taxes are added to the basket for each sector$")
    public void feesAndTaxesAreAddedToTheBasketForEachSector() throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^I will see the indirect journey in the basket$")
    public void iWillSeeTheIndirectJourneyInTheBasket() throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("^successful response is returned to the channel$")
    public void successfulResponseIsReturnedToTheChannel() throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^I have added indirect flights from \"([^\"]*)\" to \"([^\"]*)\" to the basket via \"([^\"]*)\"$")
    public void iHaveAddedIndirectFlightsFromToToTheBasketVia(String origin, String destination, String channel) throws Throwable {
        thatIndirectFlightsAreAvailableForTo(origin, destination);
        iRequestForIndirectFlightsForThatRouteFrom(channel);
        iAddTheFlightToMyBasketForIndirectRoutes();
    }

    @When("^I add the flight to my basket from \"([^\"]*)\" for indirect routes with price change$")
    public void iAddTheFlightToMyBasketForIndirectRoutesWithPriceChange(String channel) throws Throwable {
        List<FindFlightsResponse.Flight> flights = flightService.getOutboundFlights();
        for (FindFlightsResponse.Flight flight : flights) {
            flight.getFareTypes().get(0).getPassengers().get(0).setBasePrice(0.99);
        }
        basketHelper.addInDirectFlightToBasket(flightService.getOutboundJourneys(), channel, flightService.getResponse().getCurrency(),"single");
        basketCode = basketHelper.getBasketService().getResponse().getBasket().getCode();
    }

    @And("^the flights are not added to the basket$")
    public void theFlightsAreNotAddedToTheBasket() throws Throwable {
        assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds()).isEmpty();
    }


    @Given("^I have added outbound indirect flights from \"([^\"]*)\" to \"([^\"]*)\" to the basket via \"([^\"]*)\"$")
    public void iHaveAddedOutboundIndirectFlightsFromToToTheBasketVia(String origin, String destination, String channel) throws Throwable {
        assertTrue(channelHasConfiguredToSearchForIndirectFlights(channel));
        this.origin = origin;
        this.destination = destination;
        indirectFlightHelper.readAllIndirectRoutesFromHybris().indirectRoutesConfiguredInHybrisFor(this.origin, this.destination);
        channelUsed = channel;
        params = FlightQueryParams.builder().origin(origin).destination(destination).outboundDate(getFormattedFuterDateDaysBy(5))
                .inboundDate(getFormattedFuterDateDaysBy(6)).adult("1").indirectOutbound("true").indirectInbound("true").build();
        flightService = serviceFactory.findFlight((new FlightsRequest(HybrisHeaders.getValid(channel).build(), params)));
        flightService.invoke();
        outboundJourney = flightService.getOutboundFlights();
        basketHelper.addOutboundInDirectFlightToBasketAsChannelUsingFlightCurrency(outboundJourney, channel, flightService.getResponse().getCurrency());
    }

    @When("^I try adding inbound indirect flights before the outbound already in the basket$")
    public void iTryAddingInboundIndirectFlightsBeforeTheOutboundAlreadyInTheBasket() throws Throwable {
        params = FlightQueryParams.builder().origin(origin).destination(destination).outboundDate(getFormattedFuterDateDaysBy(2))
                .inboundDate(getFormattedFuterDateDaysBy(3)).adult("1").indirectOutbound("true").indirectInbound("true").build();
        flightService = serviceFactory.findFlight((new FlightsRequest(HybrisHeaders.getValid(channelUsed).build(), params)));
        flightService.invoke();
        inboundJourney = flightService.getInboundFlights();
        basketHelper.attemptToAddInboundInDirectFlightToBasketAsChannelUsingFlightCurrency(inboundJourney, channelUsed, flightService.getResponse().getCurrency());
    }
}
