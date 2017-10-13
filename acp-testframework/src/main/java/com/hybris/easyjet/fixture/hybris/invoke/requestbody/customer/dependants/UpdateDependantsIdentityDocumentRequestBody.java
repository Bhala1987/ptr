package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.IdentityDocumentRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateDependantsIdentityDocumentRequestBody extends IdentityDocumentRequestBody {

    @Builder(builderMethodName = "updateBuilder")
    private UpdateDependantsIdentityDocumentRequestBody(Name name, String dateOfBirth, String documentExpiryDate, String gender, String nationality, String countryOfIssue, String documentType, String documentNumber) {
        super(name, dateOfBirth, documentExpiryDate, gender, nationality, countryOfIssue, documentType, documentNumber);
    }
}