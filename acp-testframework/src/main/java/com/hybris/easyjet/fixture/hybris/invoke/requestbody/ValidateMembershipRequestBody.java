package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajakm on 21/08/2017.
 */

@Builder
@Getter
@Setter
public class ValidateMembershipRequestBody implements IRequestBody {
    private String membershipNumber;
    private String lastName;
    private String customerId;
}
