package com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SaveFlightInterestRequestBodyFactory {


    public static FlightInterestRequestBody getSaveFlightInterestRequestBody(Map<String, List<String>> interests) {
        List<FlightInterest> flightInterests = new ArrayList<FlightInterest>();
        for (String flightKey : interests.keySet()) {
            for (String fare : interests.get(flightKey)) {
                flightInterests.add(FlightInterest.builder()
                        .flightKey(flightKey)
                        .fareType(fare)
                        .build()
                );
            }
        }
        return new FlightInterestRequestBody(flightInterests);
    }

}
