package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by g.dimartino on 14/05/17.
 */
@Getter
@Setter
public abstract class AbstractPayment {
    private String code;
    private String description;
    private Boolean isBankNameRequired;
    private Boolean isCardHolderNameRequired;
    private Boolean isCardNumberRequired;
    private Boolean isCreditCard;
    private Boolean isIssueNumberRequired;
    private Boolean isSecurityNumberRequired;
    private Boolean isStartDateRequired;
    private String issuers;
    private String paymentMethod;
    private String paymentMethodId;
    private String securityCodeLength;
}