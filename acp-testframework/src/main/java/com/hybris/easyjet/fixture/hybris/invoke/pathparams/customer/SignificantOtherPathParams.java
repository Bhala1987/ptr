package com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.PathParameters;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.SignificantOtherPathParams.SignificantOtherPaths.DEFAULT;

@Builder
public class SignificantOtherPathParams extends PathParameters implements IPathParameters {

    private static final String BASE_URI = "significant-others";
    private static final String IDENTITY_DOCUMENTS = "identity-documents";
    private static final String SAVED_SSRS = "saved-ssrs";

    private String customerId;
    private String passengerId;
    private String documentId;
    @Builder.Default
    private SignificantOtherPaths path = DEFAULT;

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

    public enum SignificantOtherPaths {
        DEFAULT,
        IDENTITY_DOCUMENT,
        SSR
    }

}