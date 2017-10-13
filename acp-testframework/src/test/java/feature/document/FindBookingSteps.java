package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerProfileHelper;
import com.hybris.easyjet.fixture.hybris.helpers.DateFormat;
import com.hybris.easyjet.fixture.hybris.helpers.TravellerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FindBookingQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.factory.FindBookingQueryParamsFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.FindBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.FindBookingsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.FindBookingService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.fixture.WaitHelper.*;


/**
 * Created by dwebb on 11/9/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class FindBookingSteps {

    protected static Logger LOG = LogManager.getLogger(FindBookingSteps.class);

    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private FindBookingService findBookingService;
    private GetBookingService getBookingService;
    private TravellerHelper travellerHelper;
    private CustomerProfileHelper customerProfileHelper;
    @Autowired
    private BookingHelper commitBookingHelper;
    @Autowired
    private SerenityFacade testData;
    private String bookingRef;
    private GetBookingResponse getBooking;
    private FindBookingQueryParams criteria;
    private String channel;
    private String ADULT = "adult";
    private List<HashMap<String, String>> bookingAvailable;

    private FindBookingQueryParams findBookingQueryParams = null;
    private List<String> listDBBooking = new ArrayList<>();
    private FindBookingQueryParams.FindBookingQueryParamsBuilder builder;

    private String dbBooking;

    @Then("^the booking is returned$")
    public void theBookingIsReturned() throws Throwable {
        findBookingService.assertThat().theBookingHasABookingReference(bookingRef);
    }

    @When("^I search for the booking using \"([^\"]*)\" and \"([^\"]*)\" characters of the firstname using channel \"([^\"]*)\"$")
    public void iSearchForTheBookingUsingAndCharactersOfTheFirstnameUsingChannel(String field, int charcount, String channel) throws Throwable {
        FindBookingQueryParams criteria = FindBookingQueryParamsFactory.SetBookingParams(getBooking, field, charcount);
        criteria.setSearchInBooker("true");
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();
    }

    @Then("^the booking \"([^\"]*)\" is returned$")
    public void theBookingIsReturned(String returns) throws Throwable {
        switch (returns) {
            case "error":
                findBookingService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100144_2001");
                break;
            case "result":
                findBookingService.assertThat().theBookingHasABookingReference(getBooking.getBookingContext().getBooking().getBookingReference());
        }
    }

    @Then("^an (.*) is returned informing me that I cannot search by only \"([^\"]*)\"$")
    public void anErrorIsReturnedInformingMeThatICannotSearchByOnly(String error, String field) throws Throwable {
        findBookingService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I search for the booking using \"([^\"]*)\" in \"([^\"]*)\" and channel \"([^\"]*)\"$")
    public void iSearchForTheBookingUsingInAndChannel(String fields, String caseFormat, String channel) throws Throwable {
        FindBookingQueryParams criteria = FindBookingQueryParamsFactory.SetBookingParams(getBookingService.getResponse(), fields, caseFormat);
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();
    }

    @When("^I search for a booking with unmatchable criteria using \"([^\"]*)\" and channel \"([^\"]*)\"$")
    public void iSearchForABookingWithUnmatchableCriteriaUsing(String params, String channel) throws Throwable {
        FindBookingQueryParams criteria = FindBookingQueryParamsFactory.BookingWithUnmatchableSearchCriteria(params);
        criteria.setSearchInBooker("true");
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();
    }

    @Then("^an error is returned saying that no search results match the criteria$")
    public void anErrorIsReturnedSayingThatNoSearchResultsMatchTheCriteria() throws Throwable {
        findBookingService.assertThat().additionalInformationReturned("SVC_100144_2003");
    }

    @When("^I search for the booking with a missing header \"([^\"]*)\"$")
    public void iSearchForTheBookingWithAMissingHeader(String header) throws Throwable {
        FindBookingQueryParams criteria = FindBookingQueryParams.builder().firstName("Ted").lastName("Tester").build();
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid("").build(), criteria));
        findBookingService.invoke();
    }

    @When("^I search for the booking but with empty \"([^\"]*)\" using channel \"([^\"]*)\"$")
    public void iSearchForTheBookingButWithEmptyUsingChannel(String emptyQueryParam, String channel) throws Throwable {
        FindBookingQueryParams criteria = FindBookingQueryParamsFactory.EmptyTheField(emptyQueryParam, FindBookingQueryParamsFactory.Basic_FindBookingParams(getBooking, true));
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();
    }

    @Step()
    @Given("^there are valid bookings using channel \"([^\"]*)\" and passenger mix \"([^\"]*)\"$")
    public void thereAreValidBookingsUsingChannel(String channel, String mix) throws Throwable {
        dbBooking = bookingDao.getRandomBooking();
        this.channel = channel;
        if (dbBooking == null) {
            bookingRef = commitBookingHelper.createNewBooking(channel, mix).getConfirmation().getBookingReference();
            getBooking = commitBookingHelper.getBookingDetails(bookingRef, channel);
        } else {
            bookingRef = dbBooking;
            getBooking = commitBookingHelper.getBookingDetails(bookingRef, channel);
        }
    }

    @Given("^there are valid bookings with passenger APIS using channel \"([^\"]*)\" and passenger mix \"([^\"]*)\"$")
    public void thereAreValidBookingsWithPaxAPISUsingChannel(String channel, String mix) throws Throwable {
        testData.setChannel(channel);
        bookingRef = commitBookingHelper.createNewBooking(channel, mix).getConfirmation().getBookingReference();
        getBooking = commitBookingHelper.getBookingDetails(bookingRef, channel);
    }

    @Given("^I have a booking with customer is travelling using channel \"([^\"]*)\" and passenger mix \"([^\"]*)\"$")
    public void iHaveABookingWithCustomerIsTravellingUsingChannel(String channel, String mix) throws Throwable {
        testData.setChannel(channel);
        bookingRef = commitBookingHelper.createBookingCustomerIsTravelling(channel, mix).getConfirmation().getBookingReference();
        getBooking = commitBookingHelper.getBookingDetails(bookingRef, channel);
    }

    @Given("^I have a booking with customer is not travelling using channel \"([^\"]*)\" and passenger mix \"([^\"]*)\"$")
    public void iHaveABookingWithCustomerIsNotTravellingUsingChannel(String channel, String mix) throws Throwable {
        testData.setChannel(channel);
        commitBookingHelper.createNewBooking(channel, mix);
    }


    @When("^I search for a booking for channel \"([^\"]*)\"$")
    public void iSearchForABookingForChannel(String channel) throws Throwable {
        this.channel = channel;
        FindBookingQueryParams criteria = FindBookingQueryParamsFactory.Basic_FindBookingParams(getBooking, false);
        criteria.setEmail(null);
        criteria.setTravelFromDate(null);
        pause(5000);
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();

        pollingLoop().untilAsserted(() -> {
            findBookingService.assertThat().bookingsAreReturned();
        });
    }

    @Then("^the bookings matching the search criteria are returned$")
    public void theBookingsMatchingTheSearchCriteriaAreReturned() throws Throwable {
        findBookingService.assertThat().theBookingHasABookingReference(bookingRef);
        findBookingService.assertThat().theBookingHasCustomerDetails(bookingRef, getBooking.getBookingContext().getBooking().getBookingContact());
        findBookingService.assertThat().theBookingHasDateAndStatus(bookingRef, getBooking.getBookingContext().getBooking().getBookingDateTime(), getBooking.getBookingContext().getBooking().getBookingStatus());
    }

    @Then("^the bookings by travelfrom are returned$")
    public void theBookingsMatchingTheSearchCriteria() throws Throwable {
        findBookingService.assertThat().bookingsAreReturned();
        findBookingService.assertThat().theBookingHasABookingReference(bookingRef);
        findBookingService.assertThat().theBookingHasDateAndStatus(bookingRef, getBooking.getBookingContext().getBooking().getBookingDateTime(), getBooking.getBookingContext().getBooking().getBookingStatus());
    }


    @Then("^the bookings are returned in date time order$")
    public void theBookingsAreReturnedInDateTimeOrder() throws Throwable {

        pollingLoopForSearchBooking().untilAsserted(() -> {
            findBookingService.assertThat().bookingsAreReturned();
        });

        List<FindBookingsResponse.Booking> bookings = findBookingService.getResponse().getBookings();
        findBookingService.assertThat().theBookingsAreInDateTimeOrder(bookings);

    }

    @When("^I search for the booking using invalid  \"([^\"]*)\" of customer$")
    public void iSearchForTheBookingUsingInvalidOfCustomer(String arg0) throws Throwable {
        FindBookingQueryParams criteria;
        criteria = FindBookingQueryParamsFactory.BookingWithInvalidEmailIdAsSearchCriteria();
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();
    }

    @When("^I search for the booking using \"([^\"]*)\" of customer via \"([^\"]*)\"$")
    public void iSearchForTheBookingUsingOfCustomerViaChannel(String params, String channel) throws Throwable {
        criteria = FindBookingQueryParamsFactory.SetBookingParams(getBooking, params, false);
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();
    }

    @Then("^the error message is returned informing me that the header is required$")
    public void theErrorMessageIsReturnedInformingMeThatTheHeaderIsRequired() throws Throwable {
        findBookingService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100000_2027");
    }

    @When("^I search for the booking using \"([^\"]*)\" of customer using channel \"([^\"]*)\"$")
    public void iSearchForTheBookingUsingOfCustomer(String params, String channel) throws Throwable {
        criteria = FindBookingQueryParamsFactory.SetBookingParams(getBooking, params, true);
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();
    }

    @When("^I search for the booking using \"([^\"]*)\" of traveller \"([^\"]*)\"$")
    public void iSearchForTheBookingUsingOfTraveller(String params, String searchInPax) throws Throwable {
        criteria = FindBookingQueryParamsFactory.SetBookingParams(getBooking, params, Boolean.valueOf(searchInPax));
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), criteria));
        findBookingService.invoke();
    }

    @When("^I search for the booking using \"([^\"]*)\" of traveller \"([^\"]*)\" and booker \"([^\"]*)\"$")
    public void iSearchForTheBookingUsingOfTravellerOrBooker(String params, String searchInPax, String searchInBooker) throws Throwable {
        criteria = FindBookingQueryParamsFactory.SetBookingParams(getBooking, params, Boolean.valueOf(searchInPax), Boolean.valueOf(searchInBooker));
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), criteria));
        findBookingService.invoke();
    }

    @When("^I search for a booking using the travelfrom dates using channel \"([^\"]*)\"$")
    public void iSearchForABookingUsingTheTravelfromDatesUsingChannel(String channel) throws Throwable {
        this.channel = channel;
        criteria = FindBookingQueryParamsFactory.Basic_FindBookingParamsWithDates(getBooking, true);
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();
    }

    @Then("^all the bookings which are scheduled to travel on date are returned$")
    public void allTheBookingsWhichAreScheduledToTravelOnDateAreReturned() throws Throwable {
        findBookingService.assertThat().theBookingHasABookingReference(bookingRef);
        String departureDateTime = getBooking.getBookingContext().getBooking().getOutbounds().get(0).getFlights().get(0).getDepartureDateTime();
        findBookingService.assertThat().theBookingBasedOnTravelToDate(departureDateTime);
    }

    @Then("^all the bookings which are scheduled to travel in between given range should returned$")
    public void allTheBookingsWhichAreScheduledToTravelInBetweenGivenRangeShouldReturned() throws Throwable {
        findBookingService.assertThat().theBookingHasABookingReference(bookingRef);
        findBookingService.assertThat().theBookingsAreInRangeOfTravelDate(DateFormat.getDateInSpecificFormat(criteria.getTravelToDate()), DateFormat.getDateInSpecificFormat(criteria.getTravelFromDate()));
    }

    @When("^I search for the booking using range as \"([^\"]*)\" of customer using channel \"([^\"]*)\"$")
    public void iSearchForTheBookingUsingRangeAsOfCustomer(String params, String channel) throws Throwable {
        criteria = FindBookingQueryParamsFactory.BookingWithTravelDateAsSearchCriteria(params);
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
        findBookingService.invoke();
    }

    @Then("^the bookings matching the search criteria based on search parameters \"([^\"]*)\" are returned$")
    public void theBookingsMatchingTheSearchCriteriaBasedOnSearchParametersAreReturned(String params) throws Throwable {
        findBookingService.assertThat().theBookingWithSearchCriteriaForTravellers(commitBookingHelper, params, criteria);
    }

    @Then("^the bookings matching the search criteria based on search parameters \"([^\"]*)\" are returned for customer$")
    public void theBookingsMatchingTheSearchCriteriaBasedOnSearchParametersForCustomer(String params) throws Throwable {
        findBookingService.assertThat().theBookingWithSearchCriteria(params, criteria);
    }

    @And("^the booking has booking date$")
    public void theBookingHasBookingDate() throws Throwable {
        findBookingService.assertThat().theBookingHasBookingDate(getBooking.getBookingContext().getBooking().getBookingDateTime(), bookingRef);
    }

    @And("^the booking status is \"([^\"]*)\"$")
    public void theBookingStatusIs(String status) throws Throwable {
        findBookingService.assertThat().theBookingHasStatus(status, bookingRef);
    }

    @And("^the booking has outbound date$")
    public void theBookingHasOutboundDate() throws Throwable {
        findBookingService.assertThat().theBookingHasOutboundDate(getBooking.getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).map(g -> g.getDepartureDateTime()).collect(Collectors.toList()), bookingRef);
    }

    @And("^the booking has customer details$")
    public void theBookingHasCustomerDetails() throws Throwable {
        findBookingService.assertThat().theBookingHasPersonalDetails(getBooking.getBookingContext().getBooking().getBookingContact(), bookingRef);
    }

    @And("^the booking has sector details$")
    public void theBookingHasSectorDeatils() throws Throwable {
        findBookingService.assertThat().theBookingHasSectorDetails(getBooking, bookingRef);
    }

    @When("^I search for the booking for channel \"([^\"]*)\" using invalid parameter$")
    public void iSearchForTheBookingForChannelUsingInvalidParameter(String channel) throws Throwable {
        List<String> paramNotAvailable = bookingDao.getParamNotAvailableForChannel(channel);
        if (!paramNotAvailable.isEmpty()) {
            for (String param : paramNotAvailable) {
                FindBookingQueryParams criteria = FindBookingQueryParamsFactory.searchWithOnlyThisParameter(param, "invalidvalue");
                findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(channel).build(), criteria));
                findBookingService.invoke();

                findBookingService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100144_2007");
            }
        }
    }

    @Then("^an error is returned for the wrong parameter$")
    public void anErrorIsReturnedForTheWrongParameter() throws Throwable {
        /* implicit check in when condition */
    }

    @When("^I search for the booking of traveller \"([^\"]*)\" with an invalid \"([^\"]*)\" for \"([^\"]*)\"$")
    public void i_search_for_the_booking_of_traveller_something_with_an_invalid_something_for_something(String searchInPax, String value, String field) throws Throwable {
        criteria = FindBookingQueryParamsFactory.SetBookingParams(getBooking, field, value, Boolean.valueOf(searchInPax));
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), criteria));
        findBookingService.invoke();
    }

    @Then("^an \"([^\"]*)\" is returned for booking search$")
    public void an_something_is_returned_for_booking_search(String error) {
        findBookingService.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I search for the booking of traveller \"([^\"]*)\" with an invalid travel dates \"([^\"]*)\"$")
    public void i_search_for_the_booking_of_traveller_with_an_invalid_travel_dates(String searchInPax, String params) throws Throwable {
        criteria = FindBookingQueryParamsFactory.BookingWithInvalidTravelDateAsSearchCriteria(params, Boolean.valueOf(searchInPax));
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), criteria));
        findBookingService.invoke();
    }

    @When("^I search one booking with (.*) for (.*)$")
    public void iSearchOneBookingWithParameterForUser(String parameter, String user) throws EasyjetCompromisedException {
        bookingAvailable = bookingDao.getSearchBookingDetail();

        builder = FindBookingQueryParams.builder();
        List<HashMap<String, String>> validBooking;
        HashMap<String, String> selectedBooking;

        String firstName;

        switch (parameter) {
            case "ejPlusNumber wrong":
                builder.ejPlusNumber("000000");
                break;
            case "bookingToDate < bookingFromDate":
                validBooking = bookingAvailable.stream()
                        .filter(booking -> Objects.nonNull(booking)
                                && StringUtils.isNotBlank(booking.get("bookingDate"))
                                && StringUtils.isNotBlank(booking.get("passengerLastName"))
                                && StringUtils.isNotBlank(booking.get("channel")))
                        .collect(Collectors.toList());
                if (validBooking.size() == 0) {
                    throw new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA);
                }
                selectedBooking = validBooking
                        .get(new Random().nextInt(validBooking.size()));

                builder
                        .bookingFromDate(selectedBooking.get("bookingDate"))
                        .bookingToDate("2017-09-01")
                        .lastName(selectedBooking.get("passengerLastName"))
                        .channel(selectedBooking.get("channel"));
                break;
            case "bookingFromDate,bookingToDate is blank":
                validBooking = bookingAvailable.stream()
                        .filter(booking -> Objects.nonNull(booking)
                                && StringUtils.isNotBlank(booking.get("bookingDate"))
                                && StringUtils.isNotBlank(booking.get("passengerLastName"))
                                && StringUtils.isNotBlank(booking.get("channel")))
                        .collect(Collectors.toList());
                if (validBooking.size() == 0) {
                    throw new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA);
                }
                selectedBooking = validBooking
                        .get(new Random().nextInt(validBooking.size()));

                listDBBooking.addAll(validBooking.stream()
                        .filter(booking -> Objects.nonNull(booking)
                                && booking.get("bookingDate").equalsIgnoreCase(selectedBooking.get("bookingDate"))
                                && booking.get("channel").equalsIgnoreCase(selectedBooking.get("channel"))
                                && (booking.get("passengerLastName").equalsIgnoreCase(selectedBooking.get("passengerLastName"))
                                || booking.get("customerLastName").equalsIgnoreCase(selectedBooking.get("customerLastName"))))
                        .map(booking -> booking.get("bookingReference"))
                        .collect(Collectors.toList()));

                builder
                        .bookingFromDate(selectedBooking.get("bookingDate"))
                        .channel(selectedBooking.get("channel"))
                        .lastName(selectedBooking.get("passengerLastName"));
                break;
            case "passengerTitle,name":
                validBooking = bookingAvailable.stream()
                        .filter(booking -> Objects.nonNull(booking)
                                && StringUtils.isNotBlank(booking.get("passengerTitle"))
                                && StringUtils.isNotBlank(booking.get("passengerFirstName")))
                        .collect(Collectors.toList());
                if (validBooking.size() == 0) {
                    throw new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA);
                }
                selectedBooking = validBooking
                        .get(new Random().nextInt(validBooking.size()));

                firstName = selectedBooking.get("passengerFirstName").substring(0, 1);
                listDBBooking.addAll(validBooking.stream()
                        .filter(booking -> Objects.nonNull(booking)
                                && ((booking.get("passengerTitle").equalsIgnoreCase(selectedBooking.get("passengerTitle"))
                                && (booking.get("passengerFirstName").startsWith(firstName)))
                                || (booking.get("customerTitle").equalsIgnoreCase(selectedBooking.get("passengerTitle"))
                                && booking.get("customerFirstName").startsWith(firstName))))
                        .map(booking -> booking.get("bookingReference"))
                        .collect(Collectors.toList()));

                builder
                        .title(selectedBooking.get("passengerTitle"))
                        .firstName(firstName);
                break;
            case "dob,firstName,passengerLastName":
                validBooking = bookingAvailable.stream()
                        .filter(booking -> Objects.nonNull(booking)
                                && StringUtils.isNotBlank(booking.get("dob"))
                                && StringUtils.isNotBlank(booking.get("passengerFirstName"))
                                && StringUtils.isNotBlank(booking.get("passengerLastName")))
                        .collect(Collectors.toList());
                if (validBooking.size() == 0) {
                    throw new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA);
                }
                selectedBooking = validBooking
                        .get(new Random().nextInt(validBooking.size()));

                firstName = selectedBooking.get("passengerFirstName").substring(0, 1);
                listDBBooking.addAll(validBooking.stream()
                        .filter(booking -> Objects.nonNull(booking)
                                && booking.get("dob").equalsIgnoreCase(selectedBooking.get("dob"))
                                && booking.get("passengerFirstName").startsWith(firstName)
                                && booking.get("passengerLastName").equals(selectedBooking.get("passengerLastName")))
                        .map(booking -> booking.get("bookingReference"))
                        .collect(Collectors.toList()));

                builder
                        .dob(selectedBooking.get("dob"))
                        .firstName(firstName)
                        .lastName(selectedBooking.get("passengerLastName"));
                break;
            default:
                String[] param = parameter.split(",");

                Stream<HashMap<String, String>> bookingStream = bookingAvailable.stream();
                for (String par : param) {
                    bookingStream = bookingStream.filter(booking -> Objects.nonNull(booking)
                            && StringUtils.isNotBlank(booking.get(par)));
                }
                validBooking = bookingStream.collect(Collectors.toList());

                if (validBooking.size() == 0) {
                    throw new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA);
                }
                selectedBooking = validBooking
                        .get(new Random().nextInt(validBooking.size()));
                try {
                    for (String par : param) {
                        String bookingPar = selectedBooking.get(par);
                        if (par.contains("passenger")) par = StringUtils.uncapitalize(par.substring(9));
                        else if (par.contains("customer")) par = StringUtils.uncapitalize(par.substring(8));
                        builder.getClass().getMethod(par, String.class).invoke(builder, bookingPar);
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    LOG.error("Invalid parameter", e);
                }

                bookingStream = validBooking.stream();
                for (String par : param) {
                    bookingStream = bookingStream.filter(booking -> Objects.nonNull(booking)
                            && booking.get(par).equalsIgnoreCase(selectedBooking.get(par)));
                }
                listDBBooking.addAll(bookingStream.map(booking -> booking.get("bookingReference")).collect(Collectors.toList()));

                if (user.equalsIgnoreCase("passenger/customer")) {
                    bookingStream = validBooking.stream();
                    for (String par : param) {
                        String finalPar;
                        if (par.contains("passenger")) finalPar = "customer" + par.substring(9);
                        else if (par.contains("customer")) finalPar = "passenger" + par.substring(8);
                        else finalPar = par;
                        bookingStream = bookingStream.filter(booking -> Objects.nonNull(booking)
                                && booking.get(finalPar).equalsIgnoreCase(selectedBooking.get(par)));
                    }
                    listDBBooking.addAll(bookingStream.map(booking -> booking.get("bookingReference")).collect(Collectors.toList()));
                }
                break;
        }

        switch (user) {
            case "customer":
                findBookingQueryParams = builder.searchInBooker("true").build();
                break;
            case "passenger":
                findBookingQueryParams = builder.searchInPax("true").build();
                break;
            default:
                findBookingQueryParams = builder.searchInBooker("true").searchInPax("true").build();
                break;
        }
        findBookingService = serviceFactory.findBooking(new FindBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), findBookingQueryParams));
        testData.setData(SerenityFacade.DataKeys.SERVICE, findBookingService);
        findBookingService.invoke();
    }

    @Then("^I return the summary bookings that match the criteria entered$")
    public void iReturnTheSummaryBookingsThatMatchTheCriteriaEntered() throws Throwable {
        listDBBooking = listDBBooking.stream().distinct().collect(Collectors.toList());
        findBookingService.assertThat().validateSearcForBooking(listDBBooking);
    }

}
