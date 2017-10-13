package com.hybris.easyjet.fixture.hybris.invoke.response.customer.validationconfirmation;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by rajakm on 21/08/2017.
 */
@Getter
@Setter
public class ValidateMembershipResponse extends Response {
    private ValidationConfirmation validationConfirmation;
    @Getter
    @Setter
    public static class ValidationConfirmation {
        private Boolean isMembershipValid;
        private String membershipNumber;
        private String membershipHolderName;
        private String membershipStartingDate;
        private String membershipExpiringDate;
        private String customerId;
    }
}
