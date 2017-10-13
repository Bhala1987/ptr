package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Sudhir Talluri on 04/07/2017.
 */
@Builder
@Getter
@Setter
public class RemoveSavedPaymentRequestBody implements IRequestBody {
    private String savedPaymentMethodRef;
}
