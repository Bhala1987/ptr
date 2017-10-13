package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;

import java.util.List;

/**
 * Created by giuseppedimartino on 17/02/17.
 */
@Builder
public class Customer implements IRequestBody {

    public PersonalDetails personalDetails;
    public List<ContactAddress> contactAddress;

    @Builder
    public static class PersonalDetails {
        public String email;
        public String type;
        public Integer age;
        public String title;
        public String firstName;
        public String lastName;
        public String ejPlusCardNumber;
        public String nifNumber;
        public String phoneNumber;
        public String alternativePhoneNumber;
        public String flightClubId;
        public String flightClubExpiryDate;
        public List<KeyDates> keyDates;

        @Builder
        public static class KeyDates {
            public String type;
            public String month;
            public String day;
        }

    }

    @Builder
    public static class ContactAddress {
        public String addressLine1;
        public String addressLine2;
        public String addressLine3;
        public String city;
        public String county_state;
        public String country;
        public String postalCode;
    }
}
