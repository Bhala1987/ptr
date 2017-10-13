package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajakm on 11/10/2017.
 */
@Getter
@Setter
public class IdentifyPassengerResponse extends Response {

    private Authentication authentication;
    private List<MatchingPassengers> matchingPassengers = new ArrayList<>();

    @Getter
    @Setter
    public static class Authentication {
        private String accessToken;
        private String tokenType;
        private Integer expiresIn;
        private String refreshToken;
        private String scope;
    }

    @Getter
    @Setter
    public static class MatchingPassengers {
        private String passengerOnFlightId;
        private String title;
        private String firstName;
        private String lastName;
    }
}
