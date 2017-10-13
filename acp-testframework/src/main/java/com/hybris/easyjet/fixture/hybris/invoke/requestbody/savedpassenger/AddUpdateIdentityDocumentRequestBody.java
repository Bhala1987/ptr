package com.hybris.easyjet.fixture.hybris.invoke.requestbody.savedpassenger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 20/02/2017.
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddUpdateIdentityDocumentRequestBody implements IRequestBody {
    private Name name;
    private String dateOfBirth;
    private String documentExpiryDate;
    private String gender;
    private String nationality;
    private String countryOfIssue;
    private String documentType;
    private String documentNumber;
}