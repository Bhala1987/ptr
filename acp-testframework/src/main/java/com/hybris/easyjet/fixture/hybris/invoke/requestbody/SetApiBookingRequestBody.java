package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by robertadigiorgio on 27/07/2017.
 */

@Getter
@Setter
@Builder
public class SetApiBookingRequestBody implements IRequestBody {

    public Api api;
    public String addToSavedPassengerCode;

    @Getter
    @Setter
    @Builder
    public static class Api {
        private SetAPIName name;
        private String dateOfBirth;
        private String documentExpiryDate;
        private String gender;
        private String nationality;
        private String countryOfIssue;
        private String documentType;
        private String documentNumber;

    }

}
