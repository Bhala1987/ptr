package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GetAmendableBookingRequestBody;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by vijayapalkayyam on 19/07/2017.
 */
@ToString
@Component
public class CreateAmendableBasketBodyFactory {

    public static GetAmendableBookingRequestBody createABodyForPassengerLevelAmendableBasket(List<String> passengers) {
        return GetAmendableBookingRequestBody
                .builder()
                .lockingLevel("PASSENGER")
                .overrideLocking(false)
                .passengerList(passengers)
                .build();
    }

    public static GetAmendableBookingRequestBody createABodyForBookingLevelAmendableBasket(Boolean overrideLocking) {
        return GetAmendableBookingRequestBody
                .builder()
                .lockingLevel("BOOKING")
                .overrideLocking(overrideLocking)
                .build();
    }
}

