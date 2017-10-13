package com.hybris.easyjet.fixture.hybris.invoke.response.booking.createamendablebooking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
@Getter
@Setter
public class OperationConfirmation {
    @JsonProperty("basketCode")
    private String basketCode;
    @JsonProperty("href")
    private String href;
}
