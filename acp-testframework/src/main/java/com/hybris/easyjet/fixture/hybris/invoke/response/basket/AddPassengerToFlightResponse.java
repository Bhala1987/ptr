package com.hybris.easyjet.fixture.hybris.invoke.response.basket;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class AddPassengerToFlightResponse extends AbstractConfirmation<AddPassengerToFlightResponse.OperationConfirmation>{
    @Getter
    @Setter
    public static class OperationConfirmation extends BasketConfirmationResponse.OperationConfirmation{
        private List<AdditionalInformation> additionalInformation = new ArrayList<>();
    }
}