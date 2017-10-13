package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.GroupBookingQuoteResponse;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_ID;
import static org.assertj.core.api.Assertions.assertThat;


public class GroupBookingQuoteAssertion extends Assertion<GroupBookingQuoteAssertion, GroupBookingQuoteResponse> {

    public GroupBookingQuoteAssertion(GroupBookingQuoteResponse groupBookingQuoteResponse) {
        this.response = groupBookingQuoteResponse;
    }

    public void checkTheResponse(String email) {

        assertThat(response.getQuoteGenerationConfirmation().getBasketCode())
                .withFailMessage("The basket id in the response is wrong")
                .isEqualTo(testData.getData(BASKET_ID));

        assertThat(response.getQuoteGenerationConfirmation().getBookingQuotePdfLink().toLowerCase())
                .withFailMessage("In the response the link to PDF missing")
                .startsWith("http").endsWith(".pdf");

        assertThat(response.getQuoteGenerationConfirmation().getEmailAddress())
                .withFailMessage("The email in the response is wrong")
                .isEqualTo(email);
    }
}
