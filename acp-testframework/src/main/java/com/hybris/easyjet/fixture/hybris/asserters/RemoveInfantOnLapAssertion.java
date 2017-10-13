package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.RemoveInfantOnLapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppedimartino on 20/06/17.
 */
@NoArgsConstructor
public class RemoveInfantOnLapAssertion extends Assertion<RemoveInfantOnLapAssertion, RemoveInfantOnLapResponse> {

    public RemoveInfantOnLapAssertion(RemoveInfantOnLapResponse removeInfantOnLapResponse) {
        this.response = removeInfantOnLapResponse;
    }

    @Step("Infant is removed for flight {1}: Basket {0}, Infant {2}")
    public RemoveInfantOnLapAssertion infantIsRemoved(Basket basket, String flightKey, String infantCode) {
        assertThat(basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals("infant"))
                .noneMatch(passenger -> passenger.getCode().equals(infantCode)))
                .withFailMessage("The infant is still present in the basket")
                .isTrue();
        return this;
    }

    @Step("Infant product is removed for flight {1}: Basket {0}, Related adult {2}, Infant {3}")
    public RemoveInfantOnLapAssertion infantProductIsRemoved(Basket basket, String flightKey, String relatedAdult, String infantCode) throws EasyjetCompromisedException {
        assertThat(basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> passenger.getCode().equals(relatedAdult))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger is not present in the basket"))
                .getInfantsOnLap().contains(infantCode))
                .withFailMessage("The passenger still have the infant product linked to it")
                .isFalse();
        return this;
    }

    @Step("Status is not changed: Old status {0}, New status {1}")
    public RemoveInfantOnLapAssertion statusIsNotChanged(PassengerStatus originalPassengerStatus, PassengerStatus actualPassengerStatus) {
        assertThat(originalPassengerStatus)
                .withFailMessage("The passenger status info have been changed")
                .isEqualToComparingFieldByField(actualPassengerStatus);
        return this;
    }
}
