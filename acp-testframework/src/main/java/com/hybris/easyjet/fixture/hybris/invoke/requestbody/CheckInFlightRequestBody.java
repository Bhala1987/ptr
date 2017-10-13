package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Niyi Falade on 26/06/17.
 */

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckInFlightRequestBody implements IRequestBody {
    private Boolean isCheckInAll;
    private List<CheckFlight> flights;
    private Boolean isDangerousGoodsAccepted;

    @Builder
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CheckFlight {
        private String flightKey;
        private List<String> passengers;

    }

}