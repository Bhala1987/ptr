package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by vijayapalkayyam on 31/07/2017.
 */
@NoArgsConstructor
public class AmendableBasketAssertion extends Assertion<AmendableBasketAssertion, BasketsResponse> {

    /**
     * @param basketsResponse
     */
    public AmendableBasketAssertion(BasketsResponse basketsResponse) {

        this.response = basketsResponse;
    }

    public AmendableBasketAssertion passengerDetailsExistsFor(List<String> passengersRequestingAmendableBasket, GetBookingResponse.Booking originalBooking) {
        List<Basket.Passenger> outboundPassengersInAmendableBasket = getAllPassengersInBasket(response.getBasket().getOutbounds());
        List<Basket.Passenger> inboundPassengersInAmendableBasket = getAllPassengersInBasket(response.getBasket().getInbounds());
        List<Basket.Passenger> allPassengersInAmendableBasket = new ArrayList<>(outboundPassengersInAmendableBasket);
        allPassengersInAmendableBasket.addAll(inboundPassengersInAmendableBasket);
        List<GetBookingResponse.Passenger> outboundPassengersInOriginalBooking = getAllPassengersInBooking(originalBooking.getOutbounds());
        List<GetBookingResponse.Passenger> inboundPassengersInOriginalBooking = getAllPassengersInBooking(originalBooking.getInbounds());
        List<GetBookingResponse.Passenger> allPassengersInOriginalBooking = new ArrayList<>(outboundPassengersInOriginalBooking);
        allPassengersInOriginalBooking.addAll(inboundPassengersInOriginalBooking);

        assertThat(getPassengerCodesFor(allPassengersInAmendableBasket).containsAll(passengersRequestingAmendableBasket)).isTrue();
        assertThat(holdItemCountMatched(passengersRequestingAmendableBasket, allPassengersInAmendableBasket, allPassengersInOriginalBooking)).isTrue();
        assertThat(fareProductMatched(passengersRequestingAmendableBasket, allPassengersInAmendableBasket, allPassengersInOriginalBooking)).isTrue();
        assertThat(cabinBagsCountMatched(passengersRequestingAmendableBasket, allPassengersInAmendableBasket, allPassengersInOriginalBooking)).isTrue();
        assertThat(fareProductPriceMatched(passengersRequestingAmendableBasket, allPassengersInAmendableBasket, allPassengersInOriginalBooking)).isTrue();
        assertThat(nameDetailsMatched(passengersRequestingAmendableBasket, allPassengersInAmendableBasket, allPassengersInOriginalBooking)).isTrue();

        return this;
    }

    private boolean nameDetailsMatched(List<String> passengersRequestingAmendableBasket, List<Basket.Passenger> allPassengersInAmendableBasket, List<GetBookingResponse.Passenger> allPassengersInOriginalBooking) {
        for (String passenger : passengersRequestingAmendableBasket) {

            GetBookingResponse.Passenger matchedPassengerFromBooking = getMatchingPassengerFromBooking(passenger, allPassengersInOriginalBooking);

            String expFullName =
                    new StringBuilder().append(matchedPassengerFromBooking.getPassengerDetails().getName().getTitle()).append(matchedPassengerFromBooking.getPassengerDetails().getName().getFirstName()).append(matchedPassengerFromBooking.getPassengerDetails().getName().getLastName()).toString();

            Basket.Passenger matchedPassengerFromBasket = getMatchingPassengerFromBasket(passenger, allPassengersInAmendableBasket);

            String actFullName =
                    new StringBuilder().append(matchedPassengerFromBasket.getPassengerDetails().getName().getTitle()).append(matchedPassengerFromBasket.getPassengerDetails().getName().getFirstName()).append(matchedPassengerFromBasket.getPassengerDetails().getName().getLastName()).toString();


            if (!expFullName.equalsIgnoreCase(actFullName)) {
                return false;
            }
        }
        return true;
    }

    private boolean cabinBagsCountMatched(List<String> passengersRequestingAmendableBasket, List<Basket.Passenger> allPassengersInAmendableBasket, List<GetBookingResponse.Passenger> allPassengersInOriginalBooking) {
        for (String passenger : passengersRequestingAmendableBasket) {

            int expCabinBagCount = getMatchingPassengerFromBooking(passenger, allPassengersInOriginalBooking).getCabinItems().size();

            int actualCabinBagCount = getMatchingPassengerFromBasket(passenger, allPassengersInAmendableBasket).getCabinItems().size();

            if (expCabinBagCount != actualCabinBagCount) {
                return false;
            }
        }
        return true;
    }

    private boolean fareProductPriceMatched(List<String> passengersRequestingAmendableBasket, List<Basket.Passenger> allPassengersInAmendableBasket, List<GetBookingResponse.Passenger> allPassengersInOriginalBooking) {
        for (String passenger : passengersRequestingAmendableBasket) {

            double expFareProductPrice = getMatchingPassengerFromBooking(passenger, allPassengersInOriginalBooking).getFareProduct().getPricing().getBasePrice();

            double actualFareProductPrice = getMatchingPassengerFromBasket(passenger, allPassengersInAmendableBasket).getFareProduct().getPricing().getBasePrice();

            if (expFareProductPrice != actualFareProductPrice) {//NOSONAR
                return false;
            }
        }
        return true;
    }

    private boolean fareProductMatched(List<String> passengersRequestingAmendableBasket, List<Basket.Passenger> allPassengersInAmendableBasket, List<GetBookingResponse.Passenger> allPassengersInOriginalBooking) {
        for (String passenger : passengersRequestingAmendableBasket) {

            String expFareProduct = getMatchingPassengerFromBooking(passenger, allPassengersInOriginalBooking).getFareProduct().getCode();

            String actualFareProduct = getMatchingPassengerFromBasket(passenger, allPassengersInAmendableBasket).getFareProduct().getCode();

            if (!expFareProduct.equals(actualFareProduct)) {
                return false;
            }
        }
        return true;
    }

    private boolean holdItemCountMatched(List<String> passengersRequestingAmendableBasket, List<Basket.Passenger> allPassengersInAmendableBasket, List<GetBookingResponse.Passenger> allPassengersInOriginalBooking) {
        for (String passenger : passengersRequestingAmendableBasket) {

            int expHoldItemCount = getMatchingPassengerFromBooking(passenger, allPassengersInOriginalBooking).getHoldItems().size();

            int actualHoldItemCount = getMatchingPassengerFromBasket(passenger, allPassengersInAmendableBasket).getHoldItems().size();

            if (actualHoldItemCount != expHoldItemCount) {
                return false;
            }
        }
        return true;
    }

    private GetBookingResponse.Passenger getMatchingPassengerFromBooking(String passengerCode, List<GetBookingResponse.Passenger> allPassengersInOriginalBooking) {
        return allPassengersInOriginalBooking.stream()
                .filter(p -> p.getCode().equals(passengerCode))
                .findFirst()
                .orElse(null);
    }

    private Basket.Passenger getMatchingPassengerFromBasket(String passengerCode, List<Basket.Passenger> allPassengersInAmendableBasket) {
        return allPassengersInAmendableBasket.stream()
                .filter(p -> p.getCode().equals(passengerCode))
                .findFirst()
                .orElse(null);
    }

    private List<GetBookingResponse.Passenger> getAllPassengersInBooking(List<GetBookingResponse.Flights> bounds) {
        return bounds.stream()
                .map(GetBookingResponse.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Basket.Passenger> getAllPassengersInBasket(List<Basket.Flights> bounds) {
        return bounds.stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<String> getPassengerCodesFor(List<Basket.Passenger> outboundPassengersInAmendableBasket) {
        return outboundPassengersInAmendableBasket
                .stream()
                .map(AbstractPassenger::getCode)
                .collect(Collectors.toList());
    }

}