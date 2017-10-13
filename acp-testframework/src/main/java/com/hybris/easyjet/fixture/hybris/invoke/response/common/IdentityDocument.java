package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityDocument extends Response {
    private String documentId;
    private Name name;
    private String dateOfBirth;
    private String documentExpiryDate;
    private String documentNumber;
    private String documentType;
    private String gender;
    private String nationality;
    private String countryOfIssue;
}