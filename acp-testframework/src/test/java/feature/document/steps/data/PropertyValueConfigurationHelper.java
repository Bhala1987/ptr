package feature.document.steps.data;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.database.hybris.dao.PropertyValueConfigurationDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import net.thucydides.core.annotations.Steps;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static feature.document.steps.helpers.DateHelper.getDate;
import static org.junit.Assert.fail;

/**
 * PropertyValueConfigurationHelper handle the retrieval of property values from the DB.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class PropertyValueConfigurationHelper {

    private static final Logger LOG = LogManager.getLogger(PropertyValueConfigurationHelper.class);

    private static final String DEPARTURE_THRESHOLD_NAME_CHANGE = "thresholdForNameChangeBasedOnDeparture";
    private static final String DEPARTURE_THRESHOLD_FLIGHT_CHANGE = "thresholdForFlightChangeBasedOnDeparture";
    private static final String FLEXI_THRESHOLD_FOR_FLIGHT_CHANGE_BEFORE_DEPARTURE = "flexiThresholdForFlightChangeBeforeDeparture";
    private static final String FLEXI_THRESHOLD_FOR_FLIGHT_CHANGE_AFTER_DEPARTURE = "flexiThresholdForFlightChangeAfterDeparture";

    @Steps
    private SerenityReporter reporter;

    @Autowired
    private SerenityFacade testData;

    @Autowired
    private PropertyValueConfigurationDao propertyValueConfigurationDao;

    /**
     * Get the threshold for the change name fee and set the outbound and inbound date in testData outside the threshold range
     *
     * @param when it can be before or after; define which threshold need to retrieve from db
     * @throws EasyjetCompromisedException if the date param is not valid (i.e. doesn't include a number of day to add from today)
     */
    @And("^I want to book a flight (before|after) the threshold for name change based on departure$")
    public void iWantToBookAFlightBeforeTheThresholdForNameChangeBasedOnDeparture(String when) throws EasyjetCompromisedException {
        int threshold = Integer.parseInt(propertyValueConfigurationDao.getPropertyValueBasedOnName(DEPARTURE_THRESHOLD_NAME_CHANGE));
        reporter.info("The threshold is " + threshold + " day");


        if (when.equals("before")) {
            testData.setData(OUTBOUND_DATE, getDate("1"));
            testData.setData(INBOUND_DATE, getDate(String.valueOf(threshold - 1)));
        } else {
            testData.setData(OUTBOUND_DATE, getDate(String.valueOf(threshold + 1)));
            testData.setData(INBOUND_DATE, getDate(String.valueOf(threshold + 4)));
        }
    }

    /**
     * Get the threshold for the change flight fee and set the outbound and inbound date in testData within the threshold range
     *
     * @param when it can be before or after; define which threshold need to retrieve from db
     * @throws EasyjetCompromisedException if the date param is not valid (i.e. doesn't include a number of day to add from today)
     */
    @And("^the date is within (before|after) threshold for flight change based on departure$")
    public void theDateIsWithinBeforeThreshold(String when) throws EasyjetCompromisedException {
        int daysBeforeDeparture = getDaysBeforeDeparture();

        int days;
        int threshold;
        if (when.equals("before")) {
            threshold = Integer.parseInt(propertyValueConfigurationDao.getPropertyValueBasedOnName(testData.getData(CHANNEL), FLEXI_THRESHOLD_FOR_FLIGHT_CHANGE_BEFORE_DEPARTURE));
            days = new Random().nextInt(Math.min(threshold, daysBeforeDeparture)) + 1;
        } else {
            threshold = Integer.parseInt(propertyValueConfigurationDao.getPropertyValueBasedOnName(testData.getData(CHANNEL), FLEXI_THRESHOLD_FOR_FLIGHT_CHANGE_AFTER_DEPARTURE));
            days = daysBeforeDeparture + new Random().nextInt(threshold);
        }
        reporter.info("The threshold is " + threshold + " day");

        testData.setData(OUTBOUND_DATE, getDate(String.valueOf(days)));
    }

    /**
     * Get the threshold for the change flight fee and set the outbound and inbound date in testData outside the threshold range
     *
     * @param when it can be before or after; define which threshold need to retrieve from db
     * @throws EasyjetCompromisedException if the date param is not valid (i.e. doesn't include a number of day to add from today)
     */
    @But("^the date is more than threshold for flight change based on departure days (before|after)$")
    public void theDateIsMoreThanThresholdDaysBefore(String when) throws EasyjetCompromisedException, ParseException {

        int daysBeforeDeparture = getDaysBeforeDeparture();

        int days;
        int threshold;
        if (when.equals("before")) {
            threshold = Integer.parseInt(propertyValueConfigurationDao.getPropertyValueBasedOnName(testData.getData(CHANNEL), FLEXI_THRESHOLD_FOR_FLIGHT_CHANGE_BEFORE_DEPARTURE));
            days = daysBeforeDeparture - threshold - 1;
        } else {
            threshold = Integer.parseInt(propertyValueConfigurationDao.getPropertyValueBasedOnName(testData.getData(CHANNEL), FLEXI_THRESHOLD_FOR_FLIGHT_CHANGE_AFTER_DEPARTURE));
            days = daysBeforeDeparture + threshold + 1;
        }
        reporter.info("The threshold is " + threshold + " day");

        testData.setData(OUTBOUND_DATE, getDate(String.valueOf(days)));
    }

    /**
     * Calculate the number of days between today and the outbound date stored in testData
     * The try/catch should never fail as the original outbound date has already been used in previous step hence should always be in the right format
     *
     * @return the number of days between today and the outbound date stored in testData
     */
    private int getDaysBeforeDeparture() {
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        Date today = date.getTime();

        Date currentOutboundDate;
        int daysBeforeDeparture = 0;
        try {
            currentOutboundDate = new SimpleDateFormat("dd-MM-yyyy").parse(testData.getData(OUTBOUND_DATE, "original"));
            daysBeforeDeparture = currentOutboundDate.compareTo(today);
        } catch (ParseException e) {
            LOG.error(e.getMessage());
            fail("Original outbound date is in the wrong format");
        }

        return daysBeforeDeparture;
    }
}