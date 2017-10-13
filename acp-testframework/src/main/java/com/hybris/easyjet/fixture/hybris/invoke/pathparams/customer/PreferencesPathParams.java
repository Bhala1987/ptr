package com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.PathParameters;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.PreferencesPathParams.PreferencesPaths.DEFAULT;

@Builder
public class PreferencesPathParams extends PathParameters implements IPathParameters {

    private static final String BASE_URI = "preferences";
    private static final String ANCILLARY_PREFERENCES_PATH = "ancillary-preferences";
    private static final String COMMUNICATION_PREFERENCES_PATH = "communication-preferences";
    private static final String TRAVEL_PREFERENCES_PATH = "travel-preferences";

    private String customerId;
    @Builder.Default
    private PreferencesPaths path = DEFAULT;

    @Override
    public String get() {

        if (!isPopulated(customerId)) {
            throw new IllegalArgumentException("You must specify a customerId for this service.");
        }

        List<String> uri = new ArrayList<>();
        uri.add(customerId);
        uri.add(BASE_URI);
        switch (this.path) {
            case DEFAULT:
                break;
            case ANCILLARY:
                uri.add(ANCILLARY_PREFERENCES_PATH);
                break;
            case COMMUNICATION:
                uri.add(COMMUNICATION_PREFERENCES_PATH);
                break;
            case TRAVEL:
                uri.add(TRAVEL_PREFERENCES_PATH);
                break;
        }

        return StringUtils.join(uri, '/');

    }

    public enum PreferencesPaths {
        DEFAULT,
        ANCILLARY,
        COMMUNICATION,
        TRAVEL
    }

}