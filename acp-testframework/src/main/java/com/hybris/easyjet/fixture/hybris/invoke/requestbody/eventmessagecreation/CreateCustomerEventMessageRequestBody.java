package com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessagecreation;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tejaldudhale on 08/08/2017.
 */
@Getter
@Setter
@Builder
public class CreateCustomerEventMessageRequestBody implements IRequestBody {
    private String customerId;
}
