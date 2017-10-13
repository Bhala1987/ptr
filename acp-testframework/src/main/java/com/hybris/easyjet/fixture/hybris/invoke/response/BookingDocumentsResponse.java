package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Alberto
 */
@Getter
@Setter
public class BookingDocumentsResponse extends Response implements IResponse {

    @JsonProperty("documentRequestConfirmation")
    private BookingDocumentsResponse.DocumentRequestConfirmation documentRequestConfirmation;

    @Getter
    @Setter
    public static class DocumentRequestConfirmation {

        @JsonProperty("generatedDocuments")
        private List<BookingDocumentsResponse.DocumentRequestConfirmation.GeneratedDocuments> generatedDocuments;

        @Getter
        @Setter
        public static class GeneratedDocuments {

            @JsonProperty("type")
            private String type;

            @JsonProperty("outputMode")
            private String outputMode;

            @JsonProperty("documentPdfLink")
            private String documentPdfLink;

            @JsonProperty("sentByEmail")
            private Boolean sentByEmail;
        }
    }
}
