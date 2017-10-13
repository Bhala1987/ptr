package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajakm on 03/08/2017.
 */
@Builder
@Getter
@Setter

public class UpdateCommentsOnBookingRequestBody implements IRequestBody{
    private String commentType;
    private String comment;
}
