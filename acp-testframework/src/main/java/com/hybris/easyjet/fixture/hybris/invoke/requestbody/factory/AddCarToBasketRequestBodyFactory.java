package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.DateFormat;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddCarHireProductRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireEquipmentProduct;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireLocation;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireProduct;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.DriverContext;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.common.Car;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.common.Equipment;
import com.hybris.easyjet.fixture.hybris.invoke.services.CarHireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

@Component
public class AddCarToBasketRequestBodyFactory {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;

    public CarHireLocation buildCarHireLocationObject() {
        String arrivalDateTime = testData.getData(OUTBOUND_DATE);
        String departureDateTime = testData.getData(INBOUND_DATE);
        return CarHireLocation.builder()
                .pickUpAirport(testData.getDestination())
                .pickUpStation("ALCT01")
                .pickUpDate(getDateFormat(arrivalDateTime))
                .pickUpTime(getAddedTime(arrivalDateTime, 1))
                .dropOffAirport(testData.getDestination())
                .dropOffStation("ALCT01")
                .dropOffDate(getDateFormat(departureDateTime, 2))
                .dropOffTime(getAddedTime(departureDateTime, -1))
                .build();
    }

    public CarHireLocation buildCarHireLocationObject(int noOfCarHireDays, int pickUpTime, int dropOffTime) {
        String arrivalDateTime = testData.getData(OUTBOUND_DATE);
        String departureDateTime = testData.getData(INBOUND_DATE);
        return CarHireLocation.builder()
                .pickUpAirport(testData.getDestination())
                .pickUpStation("ALCT01")
                .pickUpDate(getDateFormat(arrivalDateTime))
                .pickUpTime(getAddedTime(arrivalDateTime, pickUpTime))
                .dropOffAirport(testData.getDestination())
                .dropOffStation("ALCT01")
                .dropOffDate(getDateFormat(departureDateTime, noOfCarHireDays))
                .dropOffTime(getAddedTime(departureDateTime, dropOffTime))
                .build();
    }

    public CarHireProduct buildCarHireProductObject() {
        CarHireService carHireService = testData.getData(CAR_HIRE_SERVICE);
        if (null != carHireService.getResponse() && null != carHireService.getResponse().getResult()) {
            Car car = carHireService.getResponse().getResult().getCars().stream().findFirst().get();
            return CarHireProduct.builder()
                    .rateID(car.getRateID())
                    .carCategoryCode(car.getCarCategoryCode())
                    .carCategoryName(car.getCarCategoryName())
                    .totalPrice(car.getTotalPrice())
                    .currency(car.getCurrency()).build();
        }
        return CarHireProduct.builder()
                .rateID("af7f7aae82fba70aea0ded593cd2d9994f6665c0335f5b22fa3e8d7f258baccf")
                .carCategoryCode("MCMR")
                .carCategoryName("MINI 2/4 DOORS,MANUAL,A/C")
                .totalPrice(99.96)
                .currency("GBP")
                .build();
    }

    public DriverContext buildDriverContextObject(String passengerType) {
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        Basket.Passenger carProductIsAssociatedToPassenger = getPassengerAssociatedToCar(passengerType, basket);

        return DriverContext.builder()
                .age(20)
                .countryResidence("GBR")
                .passengerCode(carProductIsAssociatedToPassenger.getCode())
                .build();
    }

    private Basket.Passenger getPassengerAssociatedToCar(String passengerType, Basket basket) {
        return basket.getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream().filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals(passengerType)))
                .findFirst().get();
    }

    public CarHireEquipmentProduct buildCarHireEquipmentProductObject() {
        CarHireService carHireService = testData.getData(CAR_HIRE_SERVICE);
        if (null != carHireService.getResponse() && null != carHireService.getResponse().getResult()) {
            Equipment equipment = carHireService.getResponse().getResult().getEquipmentList().stream().findFirst().get();
            return CarHireEquipmentProduct.builder()
                    .equipmentCode(equipment.getEquipmentCode())
                    .equipmentDescription(equipment.getEquipmentDescription())
                    .quantity(1)
                    .equipmentRentalPrice(equipment.getEquipmentRentalPrice()).build();
        }
        return null;


    }

    public AddCarHireProductRequestBody buildCarHireProductRequestBody() {
        List<CarHireEquipmentProduct> equipmentList = new ArrayList<>();
        equipmentList.add(buildCarHireEquipmentProductObject());
        return AddCarHireProductRequestBody.builder()
                .location(buildCarHireLocationObject())
                .driver(buildDriverContextObject(CommonConstants.ADULT))
                .carHireProduct(buildCarHireProductObject())
                .equipmentList(equipmentList)
                .build();
    }

    public AddCarHireProductRequestBody buildCarHireProductRequestBody(String passengerType, int noOfEquipments, int noOfCarHireDays, int pickUpTime, int dropOffTime) {
        List<CarHireEquipmentProduct> equipmentList = new ArrayList<>();
        getListOfCarHireEquipments(noOfEquipments, equipmentList);
        return AddCarHireProductRequestBody.builder()
                .location(buildCarHireLocationObject(noOfCarHireDays, pickUpTime, dropOffTime))
                .driver(buildDriverContextObject(passengerType))
                .carHireProduct(buildCarHireProductObject())
                .equipmentList(equipmentList)
                .build();
    }

    private void getListOfCarHireEquipments(int noOfEquipments, List<CarHireEquipmentProduct> equipmentList) {
        int counter = 1;
        while(counter <= noOfEquipments) {
            equipmentList.add(buildCarHireEquipmentProductObject());
            counter++;
        }
    }

    private String getAddedTime(String flightDate, int minutes) {
        Calendar date = getInBoundDate(flightDate);
        date.add(Calendar.MINUTE, +minutes);
        return getDateFormatter().format(date.getTime());
    }

    private static SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat("HH:mm:ss");
    }

    private static SimpleDateFormat getDateFormatterWithDay() {
        return new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
    }

    private Calendar getInBoundDate(String date) {
        Calendar calender = Calendar.getInstance();
        try {
            if (date == null) {
                date = testData.getData(OUTBOUND_DATE);
                calender.setTime(getDateFormatterWithDay().parse(date));
                calender.add(Calendar.DATE, 2);
            } else
                calender.setTime(getDateFormatterWithDay().parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calender;
    }

    private String getDateFormat(String date) {
        if (date != null) {
            return new SimpleDateFormat("yyyy-MM-dd")
                    .format(DateFormat.getDateCalender(date).getTime());
        }
        return null;
    }


    private String getDateFormat(String date, int noOfCarHireDays) {
        if (date != null) {
            Calendar calendar = DateFormat.getDateCalender(date);
            calendar.add(Calendar.DATE, noOfCarHireDays);
            return new SimpleDateFormat("yyyy-MM-dd")
                    .format(calendar.getTime());
        } else {
            return getDropOffDateFromArrivalDate(noOfCarHireDays);
        }
    }

    private String getDropOffDateFromArrivalDate(int noOfCarHireDays) {
        String arrivalDateTime = testData.getData(OUTBOUND_DATE);
        Calendar arrivalCalDate = getInBoundDate(arrivalDateTime);
        arrivalCalDate.add(Calendar.DATE, noOfCarHireDays);
        return new SimpleDateFormat("yyyy-MM-dd")
                .format(arrivalCalDate.getTime());
    }
}
