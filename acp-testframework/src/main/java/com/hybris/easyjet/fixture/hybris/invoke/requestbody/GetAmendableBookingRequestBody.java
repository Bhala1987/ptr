package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAmendableBookingRequestBody implements IRequestBody {

    private Boolean overrideLocking;
    @Builder.Default
    private String lockingLevel = "BOOKING";
    private List<String> passengerList;
}