package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Niyi Falade on 18/09/17.
 */

@Builder
@Getter
@Setter
public class CreateCompensationRequestBody implements IRequestBody {

    public String passengerCode;
    public String flightKey;
    public String primaryReasonCode;
    public String secondaryReasonCode;
    public String currencyCode;
    public String amount;
    public PaymentMethod paymentMethod;

    @Getter
    @Setter
    @Builder
    public static class PaymentMethod {
        public String paymentType;
        public String email;
        public String nameOnVoucher;
        public String nameOnCheque;
        public Address address;
        public String accountNumber;
        public String bankName;
        public String bankCity;
        public String bankSortCode;
        public String creditFileName;

        @Getter
        @Setter
        @Builder
        public static class Address {
            public String code;
            public String addressLine1;
            public String addressLine2;
            public String addressLine3;
            public String city;
            public String county_state;
            public String country;
            public String postalCode;

        }

    }

}
