package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Request body used when a booking is being amended to update the passenger SSRs.
 *
 * @author Joshua Curtis <j.curtis@reply.com>
 */
@Builder
@Getter
@Setter
public class AmendPassengerSSRRequestBody implements IRequestBody {
    private boolean applyToAllFutureFlights;

    private List<SSRRequestBody> ssrs;

    @Getter
    @Setter
    @Builder
    public static class SSRRequestBody {
        private String code;
        private Boolean isTandCsAccepted;
        private boolean overrideSectorRestriction;
    }
}