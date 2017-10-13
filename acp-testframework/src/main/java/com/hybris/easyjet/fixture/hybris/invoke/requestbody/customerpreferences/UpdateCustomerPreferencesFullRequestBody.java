package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customerpreferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "communicationPreferences",
        "travelPreferences",
        "ancillaryPreferences"
})

/**
 * Created by jamie on 29/03/2017.
 */
@Builder
@Getter
@Setter
public class UpdateCustomerPreferencesFullRequestBody implements IRequestBody {

    @JsonProperty("communicationPreferences")
    public CommunicationPreferences communicationPreferences;
    @JsonProperty("travelPreferences")
    public TravelPreferences travelPreferences;
    @JsonProperty("ancillaryPreferences")
    public AncillaryPreferences ancillaryPreferences;

}
