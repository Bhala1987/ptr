package com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.IdentityDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
@Getter
@Setter
public class GetSavedPassengerResponse extends Response {
    private List<SavedPassenger> savedPassengers = new ArrayList<>();

    @Getter
    @Setter
    public static class SavedPassenger {
        private Integer age;
        private String code;
        private String ejPlusCardNumber;
        private String email;
        private String firstName;
        private String flightClubExpiryDate;
        private String flightClubId;
        private List<IdentityDocument> identityDocuments = new ArrayList<>();
        private String lastName;
        private String nifNumber;
        private String phoneNumber;
        private SavedSSRs savedSSRs;
        private String title;
        private String type;
    }

    @Getter
    @Setter
    public static class SavedSSRs {
        private List<Ssr> ssrs = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Ssr {
        private String code;
        private Boolean isTandCsAccepted;
        private String description;
    }
}