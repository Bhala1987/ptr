package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.significantothers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.IdentityDocumentRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by claudiodamico on 08/03/2017.
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignificantOtherRequestBody implements IRequestBody {

    private String type;
    private String title;
    private String firstName;
    private String lastName;
    private Integer age;
    private String phoneNumber;
    private String ejPlusCardNumber;
    private String email;
    private String nifNumber;
    private String flightClubId;
    private String flightClubExpiryDate;
    private List<IdentityDocumentRequestBody> identityDocuments;
    private SignificantOtherSavedSSRsRequestBody savedSSRs;

}
