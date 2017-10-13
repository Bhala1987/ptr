package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by claudiodamico on 08/03/2017.
 */
@Getter
@Setter
public class IdentityDocumentRequestBody implements IRequestBody {

    private Name name;
    private String dateOfBirth;
    private String documentExpiryDate;
    private String gender;
    private String nationality;
    private String countryOfIssue;
    private String documentType;
    private String documentNumber;

    @Builder
    protected IdentityDocumentRequestBody(Name name, String dateOfBirth, String documentExpiryDate, String gender, String nationality, String countryOfIssue, String documentType, String documentNumber) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.documentExpiryDate = documentExpiryDate;
        this.gender = gender;
        this.nationality = nationality;
        this.countryOfIssue = countryOfIssue;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
    }

    @Builder
    @Getter
    @Setter
    public static class Name {
        private String title;
        private String firstName;
        private String middleName;
        private String lastName;
        private String fullName;
    }
}
