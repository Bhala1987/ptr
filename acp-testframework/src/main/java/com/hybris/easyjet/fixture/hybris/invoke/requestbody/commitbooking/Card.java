package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Card implements IRequestBody {

    private String cardType;
    private String cardNumberOrToken;
    private String cardSecurityNumber;
    private String cardIssueNumber;
    private String cardHolderName;
    private String cardValidFromMonth;
    private String cardValidFromYear;
    private String cardExpiryMonth;
    private String cardExpiryYear;
    private PayerAuthToken threeDPayerAuthToken;
}
