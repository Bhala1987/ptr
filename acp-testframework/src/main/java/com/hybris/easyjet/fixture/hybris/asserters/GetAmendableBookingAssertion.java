package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.createamendablebooking.GetAmendableBookingResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
public class GetAmendableBookingAssertion extends Assertion<GetAmendableBookingAssertion, GetAmendableBookingResponse> {

    public GetAmendableBookingAssertion(GetAmendableBookingResponse getAmendableBookingResponse) {
        this.response = getAmendableBookingResponse;
    }

    public GetAmendableBookingAssertion basketIsCreated() {
        assertThat(response.getOperationConfirmation().getBasketCode()).isNotEmpty();
        assertThat(response.getOperationConfirmation().getBasketCode()).isNotNull();
        return this;
    }

    public void basketHasLinkedFlights(Basket basket) {
        assertThat(basket.getOutbounds()).isNotEmpty();
        assertThat(basket.getInbounds()).isNotEmpty();

        basket.getInbounds().stream()
            .flatMap(inboundFlights -> inboundFlights.getFlights().stream())
            .forEach(inboundFlight -> assertThat(
                basket.getOutbounds().stream()
                    .flatMap(outboundFlights -> outboundFlights.getFlights().stream())
                    .anyMatch(outboundFlight -> outboundFlight.getLinkedFlights().contains(
                        inboundFlight.getFlightKey()
                    ))
                ).isTrue()
            );
    }

    public void assertEquals(String expected, String actual) {
        assertThat(actual).isEqualTo(expected);
    }
    public void assertNotEquals(String expected, String actual) {
        assertThat(actual).isNotEqualTo(expected);
    }
    public void assertTrue(boolean condition) {
        assertThat(condition).isTrue();
    }

    public void assertFalse(boolean condition) {
        assertThat(condition).isFalse();
    }
}
