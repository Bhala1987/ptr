package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.DateFormat;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.Equipment;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.Insurance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.INBOUND_DATE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.OUTBOUND_DATE;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireRequestBody.*;

/**
 * Created by stalluri on 26/07/2017.
 */
@Component
public class CarHireFactory {
    @Autowired
    private SerenityFacade testData;

    public CarHireRequestBody aBasicCarHireReqBody() {
        String arrivalDateTime = testData.getData(OUTBOUND_DATE);;
        String departureDateTime = testData.getData(INBOUND_DATE);
        return builder()
                .locale("en_GB")
                .pickUpAirport(testData.getDestination())
                .pickUpStation("ALCT01")
                .pickUpDate(getDateFormat(arrivalDateTime))
                .pickUpTime(getAddedTime(arrivalDateTime,1))
                .dropOffAirport(testData.getDestination())
                .dropOffStation("ALCT01")
                .dropOffDate(getDateFormat(departureDateTime))
                .dropOffTime(getAddedTime(departureDateTime,-1))
                .driverCountryResidence("GBR")
                .ageOfDriver(25)
                .target("DESKTOP")
                .build();
    }

    public CarHireRequestBody aCarHireReqBody() {
        String arrivalDateTime = testData.getData(OUTBOUND_DATE);;
        String departureDateTime = testData.getData(INBOUND_DATE);
        return builder()
                .carCategory(getRandomCarCategory().toString())
                .locale("en_GB")
                .pickUpStation("ALCT01")
                .pickUpAirport(testData.getDestination())
                .pickUpDate(getDateFormat(arrivalDateTime))
                .pickUpTime(getAddedTime(arrivalDateTime, 1))
                .dropOffStation("ALCT01")
                .dropOffAirport(testData.getDestination())
                .dropOffDate(getDateFormat(departureDateTime))
                .dropOffTime(getAddedTime(departureDateTime, -1))
                .driverCountryResidence("GBR")
                .ageOfDriver(25)
                .target("DESKTOP")
                .insuranceList(Arrays.asList(Insurance.builder().
                        insuranceCode(getRandomInsuranceCode().toString())
                        .build()))
                .equipmentList(
                        Arrays.asList(Equipment.builder()
                                .equipmentCode(getRandomEquipment().toString())
                                .quantity(1)
                                .build()))
                .build();
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
        } else {
            return getDropOffDateFromArrivalDate();
        }
    }

    private String getDropOffDateFromArrivalDate() {
        String arrivalDateTime = testData.getData(OUTBOUND_DATE);
        Calendar arrivalCalDate = getInBoundDate(arrivalDateTime);
        arrivalCalDate.add(Calendar.DATE, 2);
        return new SimpleDateFormat("yyyy-MM-dd")
                .format(arrivalCalDate.getTime());
    }

    private InsuranceCode getRandomInsuranceCode() {
        List<InsuranceCode> insuranceCodes =
                Collections.unmodifiableList(Arrays.asList(InsuranceCode.values()));
        Random random = new Random();
        return insuranceCodes.get(random.nextInt(insuranceCodes.size()));
    }

    private EquipmentEnum getRandomEquipment() {
        List<EquipmentEnum> equipmentList = Collections.unmodifiableList(Arrays.asList(EquipmentEnum.values()));
        Random random = new Random();
        return equipmentList.get(random.nextInt(equipmentList.size()));
    }

    private CarCategory getRandomCarCategory() {
        List<CarCategory> carCategoryList = Collections.unmodifiableList(Arrays.asList(CarCategory.values()));
        Random random = new Random();
        return carCategoryList.get(random.nextInt(carCategoryList.size()));
    }


    private enum InsuranceCode {

        WWI,
        RSA,
        PREMIUM,
        MEDIUM
    }

    private enum EquipmentEnum {
        ADD,
        CSB,
        CBS,
        CSI,
        CST,
        NVS,
        YOU
    }

    private enum CarCategory {
        MCMR,
        CDMR,
        IDMR,
        IVMR,
        FVMR,

    }

    public CarHireRequestBody aBasicCarHireReqBody(String pickUpStation, String dropOffStation) {
        String arrivalDateTime = testData.getOutboundDate();
        String departureDateTime = testData.getInboundDate();
        return builder()
                .locale("en_GB")
                .pickUpAirport(testData.getDestination())
                .pickUpStation(pickUpStation)
                .pickUpDate(getDateFormat(arrivalDateTime))
                .pickUpTime(getAddedTime(arrivalDateTime, 1))
                .dropOffAirport(testData.getDestination())
                .dropOffStation(dropOffStation)
                .dropOffDate(getDateFormat(departureDateTime))
                .dropOffTime(getAddedTime(departureDateTime, -1))
                .driverCountryResidence("GBR")
                .ageOfDriver(25)
                .target("DESKTOP")
                .build();
    }

}
