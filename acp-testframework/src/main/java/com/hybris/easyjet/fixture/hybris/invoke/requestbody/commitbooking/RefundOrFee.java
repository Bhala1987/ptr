package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundOrFee implements IRequestBody {
    private String type;
    private Double amount;
    private String currency;
    private String primaryReasonCode;
    private String primaryReasonName;
    private String originalPaymentMethod;
    private String originalPaymentMethodContext;
    private String originalPaymentTransactionReference;
    private String feeCode;
    private String feeName;

}