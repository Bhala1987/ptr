package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Created by albertowork on 5/24/17.
 */
@Getter
@Setter
public class GenerateBoardingPassResponse extends Response implements IResponse {

    @JsonProperty("flights")
    private List<GenerateBoardingPassResponse.Flight> flights;

    @Getter
    @Setter
    public static class Flight {

        @JsonProperty("flightKey")
        private String flightKey;

        @JsonProperty("passengers")
        private List<GenerateBoardingPassResponse.Flight.Passenger> passengers;

        @Getter
        @Setter
        public static class Passenger {

            @JsonProperty("passengerCode")
            private String passengerCode;
            @JsonProperty("documents")
            private List<GenerateBoardingPassResponse.Flight.Passenger.Document> documents;

            @Getter
            @Setter
            public static class Document {

                @JsonProperty("code")
                private String code;
                @JsonProperty("boardingPassPdfLink")
                private String boardingPassPdfLink;
                @JsonProperty("boardingPassBarcode")
                private String boardingPassBarcode;
            }

        }

    }

}
