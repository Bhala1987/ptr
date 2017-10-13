package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest.FlightInterest;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest.FlightInterestRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetFlightInterestResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by robertadigiorgio on 01/08/2017.
 */
public class GetFlightInterestAssertion extends Assertion<GetFlightInterestAssertion, GetFlightInterestResponse> {

    public GetFlightInterestAssertion(GetFlightInterestResponse getFlightInterestResponse) {

        this.response = getFlightInterestResponse;

    }

    public GetFlightInterestAssertion checkThatFlightWasAdded(FlightInterestRequestBody addFlightInterestRequestBody) {

        List<String> flightAdded = addFlightInterestRequestBody.getFlightInterest().stream().map(FlightInterest::getFlightKey).collect(Collectors.toList());

        List<String> getFlightInterest = this.response.getFlightInterests().stream().map(GetFlightInterestResponse.FlightInterests::getFlightKey).collect(Collectors.toList());
        Collections.sort(flightAdded);
        Collections.sort(getFlightInterest);

        assertThat(flightAdded).containsExactlyElementsOf(getFlightInterest);
        return this;
    }


    public void noFlightInterestsInGetFlightInterests() {

        List<String> getFlightInterest = this.response.getFlightInterests().stream().map(GetFlightInterestResponse.FlightInterests::getFlightKey).collect(Collectors.toList());

        assertThat(getFlightInterest.isEmpty())
                .withFailMessage("Flight Interest(s) were not removed").isTrue();
    }
}
