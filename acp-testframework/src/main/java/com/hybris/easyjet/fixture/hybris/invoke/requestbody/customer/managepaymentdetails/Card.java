package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 21/06/2017.
 */
@Builder
@Getter
@Setter
public class Card implements IRequestBody {
    private String cardToken;
    private String cardIssueNumber;
    private String cardHolderName;
    private String cardValidFromMonth;
    private String cardValidFromYear;
    private String cardExpiryMonth;
    private String cardExpiryYear;
    private Boolean isDefault;
}
