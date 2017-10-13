package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customerpreferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jamie on 29/03/2017.
 */
@Builder
@Setter
@Getter
public class UpdateCustomerPreferencesCommunicationRequestBody implements IRequestBody {
    @JsonProperty("communicationPreferences")
    public CommunicationPreferences communicationPreferences;
}
