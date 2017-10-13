package com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.PathParameters;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.DependantsPathParams.DependantsPaths.DEFAULT;

@Builder
public class DependantsPathParams extends PathParameters implements IPathParameters {

    private static final String BASE_URI = "dependents";
    private static final String PERSONAL_DETAILS = "personal-details";
    private static final String EJ_PLUS_CARD_NUMBER = "ej-plus-card-number";
    private static final String IDENTITY_DOCUMENTS = "identity-documents";
    private static final String SAVED_SSRS = "saved-ssrs";

    private String customerId;
    private String passengerId;
    private String documentId;
    @Builder.Default
    private DependantsPaths path = DEFAULT;

    @Override
    public String get() {

        if (!isPopulated(customerId)) {
            throw new IllegalArgumentException("You must specify a customerId for this service.");
        }

        List<String> uri = new ArrayList<>();
        uri.add(customerId);
        uri.add(BASE_URI);
        if (StringUtils.isNotBlank(passengerId)) {
            uri.add(passengerId);
        }
        switch (this.path) {
            case DEFAULT:
                break;
            case PERSONAL_DETAILS:
                uri.add(PERSONAL_DETAILS);
                break;
            case EJ_PLUS_CARD_NUMBER:
                uri.add(PERSONAL_DETAILS);
                uri.add(EJ_PLUS_CARD_NUMBER);
                break;
            case IDENTITY_DOCUMENT:
                uri.add(IDENTITY_DOCUMENTS);
                if (StringUtils.isNotBlank(documentId)) {
                    uri.add(documentId);
                }
                break;
            case SSR:
                uri.add(SAVED_SSRS);
                break;
        }

        return StringUtils.join(uri, '/');

    }

    public enum DependantsPaths {
        DEFAULT,
        PERSONAL_DETAILS,
        EJ_PLUS_CARD_NUMBER,
        IDENTITY_DOCUMENT,
        SSR
    }

}