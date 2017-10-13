package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertadigiorgio on 08/06/2017.
 */
@Getter
@Setter
public class SetApisBookingResponse extends AbstractConfirmation<SetApisBookingResponse.OperationConfirmation> {

    @Getter
    @Setter
    public class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private String bookingReference;
        private String href;
        private List<AdditionalInformation> additionalInformation = new ArrayList<>();
    }
}


