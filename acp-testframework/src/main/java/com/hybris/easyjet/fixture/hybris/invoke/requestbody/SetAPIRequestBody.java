package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * HybrisRequest body for Customer.setApi() service
 */

@Getter
@Setter
@Builder
public class SetAPIRequestBody implements IRequestBody {

    private String dateOfBirth;
    private String documentExpiryDate;
    private String documentNumber;
    private String documentType;
    private String gender;
    private String nationality;
    private String countryOfIssue;
    private SetAPIName name;
}
