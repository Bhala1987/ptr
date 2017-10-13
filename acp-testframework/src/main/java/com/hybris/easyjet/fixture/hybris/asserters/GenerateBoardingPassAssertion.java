package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GenerateBoardingPassRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.GenerateBoardingPassResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_BOOKING_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by albertowork on 5/24/17.
 */
public class GenerateBoardingPassAssertion extends Assertion<GenerateBoardingPassAssertion, GenerateBoardingPassResponse> {

    public GenerateBoardingPassAssertion(GenerateBoardingPassResponse generateBoardingPassResponse) {

        this.response = generateBoardingPassResponse;
    }

    public void allPassengersHaveABoardingPass(String flightKey, List<String> passengerCodes) {

        List<GenerateBoardingPassResponse.Flight> flightList = response.getFlights();
        GenerateBoardingPassResponse.Flight flightSelect = null;
        for (GenerateBoardingPassResponse.Flight flight : flightList) {
            if (flight.getFlightKey().equals(flightKey)) {
                flightSelect = flight;
            }
        }

        if (Objects.nonNull(flightSelect)) {
            assertThat(flightSelect.getPassengers().containsAll(passengerCodes))
                    .withFailMessage("NOT ALL PASSENGERS HAVE BOARDING PASS").isTrue();
        }
    }

    public void selectedPassengersHaveBoardingPass(String flightKey, String passengerCode, String errorMessage) {
        if(response == null){
            response = testData.getData(GET_BOOKING_RESPONSE);
        }
        List<GenerateBoardingPassResponse.Flight> flightList = response.getFlights();
        GenerateBoardingPassResponse.Flight flightSelect = null;
        for (GenerateBoardingPassResponse.Flight flight : flightList) {
            if (flight.getFlightKey().equals(flightKey)) {
                flightSelect = flight;
            }
        }

        if (Objects.nonNull(flightSelect)) {
            assertThat(flightSelect.getPassengers().stream().anyMatch(p -> p.getPassengerCode().equals(passengerCode)))
                    .withFailMessage(errorMessage).isTrue();
        }
    }

    public void allPassengersHaveAPdfLink(String flightKey) {
        List<GenerateBoardingPassResponse.Flight> flightList = response.getFlights();

        GenerateBoardingPassResponse.Flight flightSelect = flightList
                .stream().filter(f -> f.getFlightKey().equals(flightKey)).findFirst().get();

        if (Objects.nonNull(flightSelect)) {
            boolean allPassengersHavePdfLink = true;
            for (GenerateBoardingPassResponse.Flight.Passenger passenger : flightSelect.getPassengers()) {
                if (Objects.isNull(passenger.getPassengerCode())) {
                    allPassengersHavePdfLink = false;
                }
            }
            assertThat(allPassengersHavePdfLink)
                    .withFailMessage("NOT ALL PASSENGERS HAVE PDF LINK").isTrue();
        }
    }

    public void passengersHasInfantOnLap(List<GenerateBoardingPassRequestBody.Flight.Passenger> passengersResponse,
                                         String flightKey, BasketsResponse basketsResponse){

        List<Basket.Flights> outbounds = basketsResponse.getBasket().getOutbounds();

        Basket.Flights outboundsFlights = outbounds.get(0);
        List<Basket.Flight> flightList = outboundsFlights.getFlights();

        Basket.Flight flight = flightList
                .stream().filter(f -> f.getFlightKey().equals(flightKey)).findFirst().get();

        for(GenerateBoardingPassRequestBody.Flight.Passenger passengerResponse : passengersResponse){
            Basket.Passenger passenger = flight.getPassengers()
                    .stream().filter(p -> p.getCode().equals(passengerResponse.getPassengerCode())).findFirst().get();
            assertThat(hasPassengerInfantOnLap(passenger))
                    .withFailMessage("NOT ALL PASSENGERS HAVE PDF LINK").isTrue();
        }
    }

    public boolean hasPassengerInfantOnLap(Basket.Passenger passenger){
        return CollectionUtils.isNotEmpty(passenger.getInfantsOnLap());
    }

    public void allPassengerHaveAPdfLink(String flightKey, List<String> passengerCodes){
        List<GenerateBoardingPassResponse.Flight> flightList = response.getFlights();
        GenerateBoardingPassResponse.Flight flightSelect = flightList
                .stream().filter(f -> f.getFlightKey().equals(flightKey)).findFirst().get();

        boolean allPassengersHavePdfLink = false;
        if (Objects.nonNull(flightSelect)) {
            allPassengersHavePdfLink = true;

            for(String passengerCode : passengerCodes){
                allPassengersHavePdfLink = flightSelect.getPassengers()
                        .stream().anyMatch(p -> (p.getPassengerCode().equals(passengerCode) && hasPdfLink(p)));
            }
        }
        assertThat(allPassengersHavePdfLink)
                .withFailMessage("NOT ALL PASSENGERS HAVE PDF LINK").isTrue();
    }

    public boolean hasPdfLink(GenerateBoardingPassResponse.Flight.Passenger passenger){
        return passenger.getDocuments().stream().anyMatch(d -> Objects.nonNull(d.getBoardingPassPdfLink()));
    }
}
