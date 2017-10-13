package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;


/**
 * Created by Niyi  on 26/06/17.
 */

@Getter
@Setter
public class CheckInFlightResponse extends Response {

    public OperationConfirmation operationConfirmation;

    @Getter
    @Setter
    public static class OperationConfirmation {
        private String bookingReference;
        private String href;
    }
}
