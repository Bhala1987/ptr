package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddCarHireProductRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddCarToBasketRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CarHireFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddCarToBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.CarHireRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddCarToBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.CarHireService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_SERVICE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CAR_HIRE_SERVICE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;


/**
 * Created by stalluri on 26/07/2017.
 */
@Component
public class CarHireHelper {

    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private CarHireFactory carHireFactory;
    @Autowired
    private AddCarToBasketRequestBodyFactory addCarToBasketRequestBodyFactory;

    private CarHireRequestBody carHireRequestBody;
    private AddCarHireProductRequestBody addCarHireProductRequestBody;

    public void getCarHireQuotes() {
        carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
        getCarHireQuotesResponse();
    }

    public void addCarHireToBasketWithMissingMandatoryFields(String errorType) {
        carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
        switch (errorType) {
            case "pickUpDate":
                carHireRequestBody.setPickUpDate("");
                break;
            case "pickUpTime":
                carHireRequestBody.setPickUpTime("");
                break;
            case "dropOffDate":
                carHireRequestBody.setDropOffDate("");
                break;
            case "dropOffTime":
                carHireRequestBody.setDropOffTime("");
                break;
            default:
                throw new RuntimeException("Invalid Field selected");
        }
        getCarHireQuotesResponse();
    }

    public void addCarHireToBasketWithMissingNonMandatoryFields(String fieldType) {
        switch (fieldType) {
            case "carCategory":
                carHireRequestBody = carHireFactory.aCarHireReqBody();
                carHireRequestBody.setCarCategory("");
                break;
            case "locale":
                carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
                carHireRequestBody.setLocale("");
                break;
            case "driverCountryResidence":
                carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
                carHireRequestBody.setDriverCountryResidence("");
                break;
            case "pickUpStation":
                carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
                carHireRequestBody.setPickUpStation("");
                break;
            case "pickUpAirport":
                carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
                carHireRequestBody.setPickUpAirport("");
                break;
            case "dropOffStation":
                carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
                carHireRequestBody.setDropOffStation("");
                break;
            case "dropOffAirport":
                carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
                carHireRequestBody.setDropOffAirport("");
                break;
            case "ageOfDriver":
                carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
                carHireRequestBody.setAgeOfDriver(null);
                break;
            case "target":
                carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
                carHireRequestBody.setTarget("");
                break;
            case "insuranceList":
                carHireRequestBody = carHireFactory.aCarHireReqBody();
                carHireRequestBody.setInsuranceList(null);
                break;
            case "equipmentList":
                carHireRequestBody = carHireFactory.aCarHireReqBody();
                carHireRequestBody.setEquipmentList(null);
                break;
            case "noCareHireProducts":
                carHireRequestBody = carHireFactory.aCarHireReqBody();
                carHireRequestBody.setCarCategory("TEST");
                break;

            default:
                throw new RuntimeException("Invalid Field selected " + fieldType);
        }
        getCarHireQuotesResponse();
    }

    public void addCarHireToBasketWithErrorFields(String errorField, String fieldValue) {
        Calendar date;
        carHireRequestBody = carHireFactory.aBasicCarHireReqBody();
        BasketService basketService = testData.getData(BASKET_SERVICE);
        switch (errorField) {
            case "driverAgeLessThan":
                carHireRequestBody.setAgeOfDriver(Integer.parseInt(fieldValue));
                break;
            case "carPickUpBeforeArrival":
                date = getInBoundDate(basketService.getResponse().getBasket().getOutbounds()
                        .stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null).getDepartureDateTime());
                date.add(Calendar.DATE, -1);
                carHireRequestBody.setPickUpDate(getDateFormatter().format(date.getTime()));
                break;
            case "carDropOffAfterDeparture":
                date = getInBoundDate(basketService.getResponse().getBasket().getInbounds()
                        .stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null).getDepartureDateTime());
                date.add(Calendar.DATE, +1);
                carHireRequestBody.setDropOffDate(getDateFormatter().format(date.getTime()));
                break;
            case "carDropOffInDifferentCountry":
                carHireRequestBody.setDropOffAirport(testData.getOrigin());
                break;
            default:
                throw new RuntimeException("Invalid Error Field Selected");
        }

        getCarHireQuotesResponse();
    }

    private static SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    private void getCarHireQuotesResponse() {
        BasketPathParams pathParams = BasketPathParams.builder().basketId(getBasketId()).path(BasketPathParams.BasketPaths.CAR_HIRE).build();
        CarHireService carHireService = serviceFactory.getCarHireService(new CarHireRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, carHireRequestBody));
        carHireService.invoke();
        testData.setData(SERVICE, carHireService);
        testData.setData(CAR_HIRE_SERVICE, carHireService);
    }

    public String getBasketId() {
        return basketHelper.getBasketService().getResponse().getBasket().getCode();
    }

    private Calendar getInBoundDate(String date) {

        Calendar calender = Calendar.getInstance();
        try {
            calender.setTime(getDateFormatterWithDay().parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calender;
    }

    private static SimpleDateFormat getDateFormatterWithDay() {
        return new SimpleDateFormat("EEE d-MMM-yyyy HH-mm-ss");
    }

    public void getCarHireQuotes(String pickUpStation, String dropOffStation) {
        carHireRequestBody = carHireFactory.aBasicCarHireReqBody(pickUpStation, dropOffStation);
        getCarHireQuotesResponse();
    }

}
