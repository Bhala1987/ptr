package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response for Customer.setApi() service
 */
@Getter
@Setter
public class SetAPIResponse extends Response {
    private String customerId;
    private String href;
    private String documentId;
    private Boolean success;
}