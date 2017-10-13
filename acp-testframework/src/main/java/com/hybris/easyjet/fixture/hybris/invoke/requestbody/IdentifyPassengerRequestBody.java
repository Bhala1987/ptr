package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajakm on 11/10/2017.
 */
@Builder
@Getter
@Setter
public class IdentifyPassengerRequestBody implements IRequestBody{
    private String bookingReference;
    private String lastName;
    private String passengerOnFlightId;
}
