package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupBookingQuoteResponse extends Response {

    private QuoteGenerationConfirmation quoteGenerationConfirmation;

    @Getter
    @Setter
    public static class QuoteGenerationConfirmation {
        private String basketCode;
        private String bookingQuotePdfLink;
        private String emailAddress;
    }


}
