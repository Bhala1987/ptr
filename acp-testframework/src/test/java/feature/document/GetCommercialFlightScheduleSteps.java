package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.fixture.hybris.helpers.ChannelPropertiesHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.CommercialFlightScheduleParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.CommercialFlightScheduleRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CommercialFlightScheduleService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.DAY_OF_YEAR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by jamie on 13/02/2017.
 */

//TODO: Need to fix findFLights to filter on dept arrv before uncommenting

@ContextConfiguration(classes = TestApplication.class)

public class GetCommercialFlightScheduleSteps {

    protected static Logger LOG = LogManager.getLogger(GetCommercialFlightScheduleSteps.class);
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private ChannelPropertiesHelper channelPropertiesHelper;
    private CommercialFlightScheduleService theCFSService;
    private CommercialFlightScheduleParams theCFSParams;
    private String theChannel;
    private String theOrigin = "LTN";
    private String theDestination = "CDG";
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private Calendar theFromDate = Calendar.getInstance();
    private Calendar theToDate = Calendar.getInstance();
    private List<HybrisFlightDbModel> expectedFlightResults;
    private String theDayRange;
    private String theMaxRangeOfResults;

    private void executeFlightScheduleRequestUsingStoredParameters() {
        theCFSService = serviceFactory.getCommercialFlightSchedule(new CommercialFlightScheduleRequest(HybrisHeaders.getValid(theChannel).build(), theCFSParams));
        theCFSService.invoke();
    }

    @Given("^that I am using channel:(.*)$")
    public void thatIAmUsingChannelChannel(String aChannel) throws Throwable {
        theChannel = aChannel.trim();
        testData.setChannel(theChannel);
    }


    @When("^I make a request for the flight schedules using all parameters$")
    public void iMakeARequestForTheFlightScheduleUsingAllParameters() throws Throwable {

        theToDate.add(DAY_OF_YEAR, 3);

        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .origin(theOrigin)
                .destination(theDestination)
                .fromDate(getDateParamFromCalendar(theFromDate))
                .toDate(getDateParamFromCalendar(theToDate))
                .build()
        ;

        //expectedFlightResults = flightFinder.findValidFlights(theFromDate, theToDate,0,theOrigin,theDestination,null,false);

        executeFlightScheduleRequestUsingStoredParameters();
    }

    @When("^I make a request for the flight schedules without the From-Date$")
    public void iMakeARequestForTheFlightScheduleWithoutTheFromDate() throws Throwable {
        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .destination(theDestination)
                .origin(theOrigin)
                .toDate(getDateParamFromCalendar(theToDate))
                .build()
        ;

        executeFlightScheduleRequestUsingStoredParameters();
    }

    @When("^I make a request for the flight schedules using Origin, Destination and a day range of (\\d+) days$")
    public void iMakeARequestForTheFlightScheduleUsingOriginDestinationAndADayRangeOfDays(int aDayRange) throws Throwable {
        Calendar myDaysLater = Calendar.getInstance();
        myDaysLater.setTime(theFromDate.getTime());
        myDaysLater.add(DAY_OF_YEAR, aDayRange);

        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .destination(theDestination)
                .origin(theOrigin)
                .fromDate(getDateParamFromCalendar(theFromDate))
                .toDate(getDateParamFromCalendar(myDaysLater))
                .build()
        ;

        executeFlightScheduleRequestUsingStoredParameters();
    }

    @When("^I make a request for the flight schedules using only From-date$")
    public void iMakeARequestForTheFlightScheduleUsingOnlyFromDate() throws Throwable {
        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .fromDate(getDateParamFromCalendar(theFromDate))
                .build()
        ;

        executeFlightScheduleRequestUsingStoredParameters();
    }

    @When("^I make a request for the flight schedules using only From-date and Origin$")
    public void iMakeARequestForTheFlightScheduleUsingOnlyFromDateAndOrigin() throws Throwable {
        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .origin(theOrigin)
                .fromDate(getDateParamFromCalendar(theFromDate))
                .build()
        ;

        executeFlightScheduleRequestUsingStoredParameters();
    }

    @When("^I make a request for the flight schedules using only From-date and Destination$")
    public void iMakeARequestForTheFlightScheduleUsingOnlyFromDateAndDestination() throws Throwable {
        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .destination(theDestination)
                .fromDate(getDateParamFromCalendar(theFromDate))
                .build()
        ;

        executeFlightScheduleRequestUsingStoredParameters();
    }


    @When("^I make a request for the flight schedules using only From-Date, To-Date and Origin$")
    public void iMakeARequestForTheFlightScheduleUsingOnlyFromDateToDateAndOrigin() throws Throwable {

        theToDate.add(DAY_OF_YEAR, 2);

        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .origin(theOrigin)
                .fromDate(getDateParamFromCalendar(theFromDate))
                .toDate(getDateParamFromCalendar(theToDate))
                .build()
        ;

        executeFlightScheduleRequestUsingStoredParameters();
    }

    @When("^I make a request for the flight schedules using only From-Date, To-Date and Destination$")
    public void iMakeARequestForTheFlightScheduleUsingOnlyFromDateToDateAndDestination() throws Throwable {
        theToDate.add(DAY_OF_YEAR, 2);

        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .destination(theDestination)
                .fromDate(getDateParamFromCalendar(theFromDate))
                .toDate(getDateParamFromCalendar(theToDate))
                .build()
        ;

        executeFlightScheduleRequestUsingStoredParameters();
    }

    @When("^I make a request for the flight schedules using a day range of 1 day above the configuration value$")
    public void iMakeARequestForTheFlightScheduleUsingADayRangeOfOneAboveDays() throws Throwable {

        iMakeARequestForTheFlightSchedulesUsingADayRangeOfDays(Integer.valueOf(theDayRange) + 1);
    }

    @When("^I make a request for the flight schedules using a day range of 1 day below the configuration value$")
    public void iMakeARequestForTheFlightScheduleUsingADayRangeOfOneDaysBelow() throws Throwable {

        iMakeARequestForTheFlightSchedulesUsingADayRangeOfDays(Integer.valueOf(theDayRange) - 1);
    }

    @When("^I make a request for the flight schedules using a day range of (\\d+) days$")
    public void iMakeARequestForTheFlightSchedulesUsingADayRangeOfDays(int aDays) throws Throwable {
        Calendar myDaysLater = Calendar.getInstance();
        myDaysLater.setTime(theFromDate.getTime());
        myDaysLater.add(DAY_OF_YEAR, aDays);

        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .fromDate(getDateParamFromCalendar(theFromDate))
                .toDate(getDateParamFromCalendar(myDaysLater))
                .build()
        ;

        executeFlightScheduleRequestUsingStoredParameters();
    }


    @Then("^I receive the flight schedules for the route and date range provided$")
    public void iReceiveTheFlightScheduleForTheRouteAndDateRangeProvided() throws Throwable {
        theCFSService
                .assertThat()
                .allSchedulesFallWithinDateRange(theCFSParams.getFromDate(), theCFSParams.getToDate())
                .allSchedulesHaveCorrectDestination(theCFSParams.getDestination())
                .allSchedulesHaveCorrectOrigin(theCFSParams.getOrigin())
                .correctNumberOfSchedulesReturned(this.expectedFlightResults);
    }

    @Then("^a missing From-Date error is returned in the response$")
    public void anErrorIsReturnedInTheResponse() throws Throwable {
        theCFSService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100100_3002");
    }

    @Then("^I receive the flight schedules for the chosen day$")
    public void iReceiveTheFlightSchedulesForTheChosenDay() throws Throwable {
        theCFSService
                .assertThat()
                .allSchedulesHaveCorrectDepartureDay(theCFSParams.getFromDate())
        ;

    }

    @Then("^I receive the flight schedules for the origin and chosen day$")
    public void iReceiveTheFlightSchedulesForTheOriginAndChosenDay() throws Throwable {
        theCFSService
                .assertThat()
                .allSchedulesHaveCorrectDepartureDay(theCFSParams.getFromDate())
                .allSchedulesHaveCorrectOrigin(theCFSParams.getOrigin())
        ;
    }

    @Then("^I receive the flight schedules for the destination and chosen day$")
    public void iReceiveTheFlightSchedulesForTheDestinationAndChosenDay() throws Throwable {
        theCFSService
                .assertThat()
                .allSchedulesHaveCorrectDestination(theCFSParams.getDestination())
                .allSchedulesHaveCorrectDepartureDay(theCFSParams.getFromDate())
        ;
    }

    @Then("^I receive the flight schedules for the date range provided$")
    public void iReceiveTheFlightSchedulesForTheDateRangeProvided() throws Throwable {
        theCFSService
                .assertThat()
                .allSchedulesFallWithinDateRange(theCFSParams.getFromDate(), theCFSParams.getToDate())
        ;
    }


    @Then("^I receive the maximum configured days of flight schedule results$")
    public void iReceiveDaysOfFlightScheduleResults() throws Throwable {

        //also count results?
        Calendar myToDate = Calendar.getInstance();
        myToDate.setTime(formatter.parse(theCFSParams.getFromDate()));
        myToDate.add(DAY_OF_YEAR, Integer.valueOf(theMaxRangeOfResults));

        String myToDateAsString = formatter.format(myToDate.getTime());

        theCFSService
                .assertThat()
                .allSchedulesFallWithinDateRange(theCFSParams.getFromDate(), myToDateAsString)
        ;

    }

    @Then("^I receive the flight schedules for the origin and date range provided$")
    public void iReceiveTheFlightSchedulesForTheOriginAndDateRangeProvided() throws Throwable {
        //expectedFlightResults = flightFinder.findValidFlights(theFromDate, theToDate,0,theOrigin,null,null,false);

        theCFSService
                .assertThat()
                .correctNumberOfSchedulesReturned(expectedFlightResults)
                .allSchedulesHaveCorrectOrigin(theCFSParams.getOrigin())
                .allSchedulesFallWithinDateRange(theCFSParams.getFromDate(), theCFSParams.getToDate())
        ;
    }

    @Then("^I receive the flight schedules for the destination and date range provided$")
    public void iReceiveTheFlightSchedulesForTheDestinationAndDateRangeProvided() throws Throwable {
        //expectedFlightResults = flightFinder.findValidFlights(theFromDate, theToDate,0,null,theDestination,null,false);

        theCFSService
                .assertThat()
                .correctNumberOfSchedulesReturned(expectedFlightResults)
                .allSchedulesHaveCorrectDestination(theCFSParams.getDestination())
                .allSchedulesFallWithinDateRange(theCFSParams.getFromDate(), theCFSParams.getToDate())
        ;
    }

    @Then("^an arrival and destination should be provided error is returned in the response$")
    public void aDateRangeTooLargeErrorIsReceivedInTheResponse() throws Throwable {
        theCFSService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100100_3001");
    }

    @When("^I make a request for the flight schedules using a past date$")
    public void iMakeARequestForTheFlightSchedulesUsingAPastDate() throws Throwable {
        theToDate.add(DAY_OF_YEAR, -3);
        theFromDate.add(DAY_OF_YEAR, -5);

        theCFSParams = CommercialFlightScheduleParams
                .builder()
                .origin(theOrigin)
                .destination(theDestination)
                .fromDate(getDateParamFromCalendar(theFromDate))
                .toDate(getDateParamFromCalendar(theToDate))
                .build()
        ;

        //expectedFlightResults = flightFinder.findValidFlights(theFromDate, theToDate,0,theOrigin,theDestination,null,false);

        executeFlightScheduleRequestUsingStoredParameters();
    }

    private String getDateParamFromCalendar(Calendar aCalendar) {
        return formatter.format(aCalendar.getTime());
    }

    @And("^the day range is configured for flight schedules$")
    public void theDayRangeIsConfiguredToDays() throws Throwable {
        theDayRange = channelPropertiesHelper.getPropertyValueByChannelAndKey("All", "flightScheduleMaxDateRange");
        assertThat(theDayRange).isNotNull();
    }

    @And("^the maximum number of days is configured for flight schedules$")
    public void theDMaximumNumberOfDaysIsConfigured() throws Throwable {
        theMaxRangeOfResults = channelPropertiesHelper.getPropertyValueByChannelAndKey("All", "flightScheduleMaxAllowedDays");
        assertThat(theMaxRangeOfResults).isNotNull();
    }


}

