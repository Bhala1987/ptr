package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.BankAccount;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 21/06/2017.
 */
@Builder
@Getter
@Setter
public class SavedPaymentMethodRequestBody implements IRequestBody {
    private String paymentMethod;
    private String paymentCode;
    private String paymentMethodId;
    private Card card;
    private BankAccount bankAccount;
}
