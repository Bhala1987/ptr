package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GroupBookingQuoteRequestBody  implements IRequestBody {
    private String emailAddress;
}
