package com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RemoveFlightInterestRequestBodyFactory {


    public static RemoveFlightInterestRequestBody getRemoveFlightInterestRequestBody(Map<String, List<String>> interests) {
        List<FlightInterest> flightInterestsToRemove = new ArrayList<FlightInterest>();
        for (String flightKey : interests.keySet()) {
            for (String fare : interests.get(flightKey)) {
                flightInterestsToRemove.add(FlightInterest.builder()
                        .flightKey(flightKey)
                        .fareType(fare)
                        .build()
                );
            }
        }
        return new RemoveFlightInterestRequestBody(flightInterestsToRemove);
    }

}
