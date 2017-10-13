package feature.document.steps.helpers;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DateHelper generate date in the format used by ACP from meaningful string.
 * See src/test/java/feature/document/steps/constants/StepsRegex.DATES
 *
 * @author gd <g.dimartino@reply.it>
 */
public class DateHelper {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static final Calendar date = Calendar.getInstance();

    /**
     * Get a random date between today and next 10 days
     *
     * @return a string representing the generate date in dateFormat
     */
    public static String getDate() {
        date.setTime(new Date());
        date.add(Calendar.DAY_OF_MONTH, new Random().nextInt(10));
        return dateFormat.format(date.getTime());
    }

    /**
     * Get the date specified as argument
     *
     * @param date it can be today or a meaningful sentence that include the number of days to add to today
     * @return a string representing the generate date in dateFormat
     * @throws EasyjetCompromisedException if the date argument doesn't include a number of day to add from today and is not today
     */
    public static String getDate(String date) throws EasyjetCompromisedException {
        DateHelper.date.setTime(new Date());

        if (date.equals("today")) {
            return dateFormat.format(DateHelper.date.getTime());
        } else {
            try {
                new SimpleDateFormat("dd-MM-yyyy").parse(date);
                return date;
            } catch (ParseException ignored) {
                Matcher p = Pattern.compile("\\d+").matcher(date);
                if (p.find()) {
                    DateHelper.date.add(Calendar.DAY_OF_MONTH, Integer.valueOf(p.group()));
                    return dateFormat.format(DateHelper.date.getTime());
                } else {
                    throw new EasyjetCompromisedException("Date specified is not valid");
                }
            }
        }
    }
}