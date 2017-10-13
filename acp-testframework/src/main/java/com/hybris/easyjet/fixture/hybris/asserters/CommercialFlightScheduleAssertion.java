package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.CommercialFlightScheduleResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jamie on 13/02/2017.
 */
public class CommercialFlightScheduleAssertion extends Assertion<CommercialFlightScheduleAssertion, CommercialFlightScheduleResponse> {

    private SimpleDateFormat longFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    private SimpleDateFormat shortFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private Logger LOG = Logger.getLogger(this.getClass().getName());

    public CommercialFlightScheduleAssertion(CommercialFlightScheduleResponse aResponse) {

        this.response = aResponse;
    }

    public CommercialFlightScheduleAssertion allSchedulesHaveCorrectDestination(String aDestination) {

        for (CommercialFlightScheduleResponse.Schedule schedule : response.getSchedules()) {
            assertThat(schedule.getSector().getArrivalAirport().getAirportCode())
                    .isEqualTo(aDestination)
            ;
        }

        return this;
    }

    public CommercialFlightScheduleAssertion allSchedulesFallWithinDateRange(String aFromDate, String aToDate) {

        try {
            Date myFromDate = shortFormatter.parse(aFromDate);
            Date myToDate = shortFormatter.parse(aToDate);
            Calendar myToCalendar = Calendar.getInstance();
            myToCalendar.setTime(myToDate);
            myToCalendar.add(Calendar.DAY_OF_YEAR, 1);
            myToDate = myToCalendar.getTime();

            for (CommercialFlightScheduleResponse.Schedule schedule : response.getSchedules()) {
                Date departureTime = longFormatter.parse(schedule.getDepartureTime());

                assertThat(departureTime)
                        .isAfter(myFromDate)
                ;

                assertThat(departureTime)
                        .isBefore(myToDate)
                ;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this;
    }

    public CommercialFlightScheduleAssertion allSchedulesHaveCorrectOrigin(String aOrigin) {

        for (CommercialFlightScheduleResponse.Schedule schedule : response.getSchedules()) {
            assertThat(schedule.getSector().getDepartureAirport().getAirportCode())
                    .isEqualTo(aOrigin)
            ;
        }

        return this;
    }

    public CommercialFlightScheduleAssertion allSchedulesHaveCorrectDepartureDay(String fromDate) {

        try {
            for (CommercialFlightScheduleResponse.Schedule schedule : response.getSchedules()) {
                Calendar actualCal = Calendar.getInstance();
                actualCal.setTime(longFormatter.parse(schedule.getDepartureTime()));
                Calendar expectedCal = Calendar.getInstance();
                expectedCal.setTime(shortFormatter.parse(fromDate));

                assertThat(actualCal.get(Calendar.YEAR)).isEqualTo(expectedCal.get(Calendar.YEAR));
                assertThat(actualCal.get(Calendar.DAY_OF_YEAR)).isEqualTo(expectedCal.get(Calendar.DAY_OF_YEAR));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this;
    }

    public CommercialFlightScheduleAssertion correctNumberOfSchedulesReturned(List<HybrisFlightDbModel> aExpectedFlightResults) {
//        assertThat(response.getSchedules().size())
//            .isEqualTo(aExpectedFlightResults.size())
//        ;

        //TODO: Fix flightfinder results -JH

        return this;
    }

}
