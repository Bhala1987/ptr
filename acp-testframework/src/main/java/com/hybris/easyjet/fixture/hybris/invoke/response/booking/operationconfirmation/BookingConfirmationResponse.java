package com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.LoginResponse;
import lombok.Getter;
import lombok.Setter;

public class BookingConfirmationResponse extends AbstractConfirmation<BookingConfirmationResponse.OperationConfirmation> {
    @Getter
    @Setter
    public class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private LoginResponse.Authentication authentication;
        private String bookingReference;
        private String bookingStatus;
    }

}