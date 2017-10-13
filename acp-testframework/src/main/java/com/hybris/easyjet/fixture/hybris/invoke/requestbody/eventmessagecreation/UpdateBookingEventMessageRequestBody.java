package com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessagecreation;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajakm on 19/09/2017.
 */
@Getter
@Setter
@Builder
public class UpdateBookingEventMessageRequestBody implements IRequestBody{
    private String bookingReferenceCode;
    private String baseStoreUid;
    private String versionId;
}
