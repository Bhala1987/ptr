package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.SaveRecentSearchRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetRecentSearchesService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.SEARCHES;

/**
 * Created by ptr-kvijayapal on 1/23/2017.
 */
@Component
public class SaveRecentSearchHelper {

	private GetRecentSearchesService getRecentSearchesService;
	private HybrisServiceFactory serviceFactory;

	@Autowired
	public SaveRecentSearchHelper (HybrisServiceFactory serviceFactory) {

		this.serviceFactory = serviceFactory;
	}

	/**
	 * @return the recent search service object
	 */
	public void invokeRecentSearchServiceFor (String customerId) {

		CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).path(SEARCHES).build();
		getRecentSearchesService = serviceFactory.getRecentSearch(new SaveRecentSearchRequest(HybrisHeaders.getValid("Digital")
			.build(), pathParams));
		getRecentSearchesService.invoke();
	}

	public GetRecentSearchesService getRecentSearchService () {

		return getRecentSearchesService;
	}

    public Date addDaysToDate(Date dateToBeModified, Integer daysToAdd){
        if (dateToBeModified != null && daysToAdd != null) {
            Calendar newDate = Calendar.getInstance(); // creates calendar
            newDate.setTime(dateToBeModified); // sets calendar time/date
            newDate.add(Calendar.DAY_OF_MONTH, daysToAdd); // adds two hours
            return newDate.getTime();
        }
        else{
            return null;
        }
    }

    public String getFormattedDate(Date myDate, String pattern){
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(myDate);
    }

	public Date getDateFromString (String myDate, String pattern) throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.parse(myDate);
	}

}
