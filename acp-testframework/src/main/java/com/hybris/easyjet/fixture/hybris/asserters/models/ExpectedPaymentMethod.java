package com.hybris.easyjet.fixture.hybris.asserters.models;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by prite on 18/11/2016.
 */
public class ExpectedPaymentMethod {

    protected static Logger LOG = LogManager.getLogger(ExpectedPaymentMethod.class);

    private boolean bankNameRequired;
    private boolean cardHolderNameRequired;
    private boolean cardNumberRequired;
    private String code;
    private String description;
    private boolean isCreditCard;
    private boolean isIssueNumberRequired;
    private String issuers;
    private String paymentMethod;
    private int paymentMethodId;
    private int securityCodeLength;
    private boolean securityNumberRequired;
    private boolean startDateRequired;


}
