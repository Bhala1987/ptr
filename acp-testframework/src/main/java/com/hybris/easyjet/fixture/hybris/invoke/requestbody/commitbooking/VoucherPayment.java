package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajakm on 12/05/2017.
 */
@Builder
@Getter
@Setter
public class VoucherPayment implements IRequestBody {
    private String validationNumber;
}
