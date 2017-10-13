package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajakm on 12/09/2017.
 */
@Getter
@Setter
public class GetTermsAndConditionsResponse extends Response {
    private TermsAndConditions termsAndConditions;

    @Getter
    @Setter
    public static class TermsAndConditions{
        private String locale;
        private List<LocalizedDescriptions> localizedDescriptions = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class LocalizedDescriptions {
        private String href;
        private String value;
        private String locale;
    }

}
