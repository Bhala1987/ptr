package com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessageupdate;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
@Builder
public class UpdateBookingEventMessageRequestBody implements IRequestBody {
   private String bookingReferenceCode;
   private String baseStoreUid;
   private String versionId;
}
