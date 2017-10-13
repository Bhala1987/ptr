package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.AmendPassengerSSRResponse;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Assertions for the amend passenger SSR request.
 *
 * @author Joshua Curtis <j.curtis@reply.com>
 */
public class AmendPassengerSSRAssertion extends Assertion<AmendPassengerSSRAssertion, AmendPassengerSSRResponse> {
    public AmendPassengerSSRAssertion(AmendPassengerSSRResponse amendPassengerSSRResponse) {
        this.response = amendPassengerSSRResponse;
    }

    public void passengerHasSSRWithCode(Basket.Passenger passenger, String code, Basket basket) {
        Stream<Basket.Flights> outbounds = basket.getOutbounds().stream();
        Stream<Basket.Flights> inbounds = basket.getInbounds().stream();

        Stream.concat(outbounds, inbounds).flatMap(
            flights -> flights.getFlights().stream()
        ).forEach(flight -> {
                Basket.Passenger flightPassenger = flight.getPassengers()
                    .stream()
                    .filter(pax -> passenger.getPassengerMap().contains(pax.getCode()))
                    .findFirst()
                    .get();

                assertThat(
                    flightPassenger.getSpecialRequests()
                        .getSsrs()
                        .stream()
                        .anyMatch(ssr -> ssr.getCode().equals(code))
                ).isTrue();
            }
        );
    }

    public void passengerDoesNotHaveSsrs(Basket.Passenger passenger, Basket basket) {
        Stream<Basket.Flights> outbounds = basket.getOutbounds().stream();
        Stream<Basket.Flights> inbounds = basket.getInbounds().stream();

        Stream.concat(outbounds, inbounds).flatMap(
            flights -> flights.getFlights().stream()
        ).forEach(
            flight -> assertThat(
                flight.getPassengers()
                    .stream()
                    .filter(pax -> passenger.getPassengerMap().contains(pax.getCode()))
                    .findFirst()
                    .get()
                    .getSpecialRequests()
                    .getSsrs()
            ).isEmpty()
        );
    }
}
