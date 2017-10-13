package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.BookingDocumentsResponse;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Alberto on 9/21/17.
 */
public class BookingDocumentsAssertion extends Assertion<BookingDocumentsAssertion, BookingDocumentsResponse> {

    public static final String EMAIL_TYPE = "EMAIL";
    public static final String PRINT_TYPE = "PRINT";

    public BookingDocumentsAssertion(BookingDocumentsResponse bookingDocumentsResponse) {
        this.response = bookingDocumentsResponse;
    }

    public void responseHasPdfLinkDocuments() {

        List<BookingDocumentsResponse.DocumentRequestConfirmation.GeneratedDocuments> documents =
                response.getDocumentRequestConfirmation().getGeneratedDocuments();

        boolean allDocumentsHasPdfLink =
                documents.stream().anyMatch(d -> d.getOutputMode().equals(PRINT_TYPE) && Objects.nonNull(d.getDocumentPdfLink()));

        assertThat(allDocumentsHasPdfLink)
                .withFailMessage("NOT ALL DOCUMENTS HAVE PDF LINK").isTrue();
    }

    public void responseHasEmail() {

        List<BookingDocumentsResponse.DocumentRequestConfirmation.GeneratedDocuments> documents =
                response.getDocumentRequestConfirmation().getGeneratedDocuments();

        boolean allDocumentsHasEmail =
                documents.stream().anyMatch(d -> d.getOutputMode().equals(EMAIL_TYPE) && d.getSentByEmail());

        assertThat(allDocumentsHasEmail)
                .withFailMessage("NOT EMAIL SENT").isTrue();
    }

    public void responseHasSelectedType(String type) {

        List<BookingDocumentsResponse.DocumentRequestConfirmation.GeneratedDocuments> documents =
                response.getDocumentRequestConfirmation().getGeneratedDocuments();

        boolean allDocumentsHasSelectedType =
                documents.stream().anyMatch(d -> d.getType().equals(type));

        assertThat(allDocumentsHasSelectedType)
                .withFailMessage("THE DOCUMENT IS NOT A " + type).isTrue();
    }
}
