package com.hybris.easyjet.fixture.hybris.invoke.requestbody;


import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Request body used when a booking is being amended to delete the passenger SSRs.
 *
 * @author Joshua Curtis <j.curtis@reply.com>
 */
@Builder
@Getter
@Setter
public class DeletePassengerSSRRequestBody implements IRequestBody {
    private boolean removeFromAllFutureFlights;

    private List<String> ssrCodes;
}
