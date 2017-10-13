package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Created by albertowork on 5/24/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class GenerateBoardingPassRequestBody implements IRequestBody {

    @JsonProperty("language")
    private String language;

    @JsonProperty("flights")
    private List<GenerateBoardingPassRequestBody.Flight> flights;

    @Getter
    @Setter
    @Builder
    public static class Flight {

        @JsonProperty("flightKey")
        private String flightKey;

        @JsonProperty("passengers")
        private List<GenerateBoardingPassRequestBody.Flight.Passenger> passengers;

        @Getter
        @Setter
        @Builder
        public static class Passenger {

            @JsonProperty("passengerCode")
            private String passengerCode;
            @JsonProperty("isAdditionalSeatsOnly")
            private boolean isAdditionalSeatsOnly;


        }

    }


}
