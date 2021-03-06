package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Niyi Falade on 01/08/17.
 */

@Builder
@Getter
@Setter
public class SetReasonForTravelRequestBody implements IRequestBody {
    private String bookingReason;
}
