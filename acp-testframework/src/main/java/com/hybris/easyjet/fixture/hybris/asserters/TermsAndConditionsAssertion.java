package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.LanguagesDao;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetTermsAndConditionsResponse;
import lombok.NoArgsConstructor;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by rajakm on 12/09/2017.
 */
@NoArgsConstructor
public class TermsAndConditionsAssertion extends Assertion<TermsAndConditionsAssertion, GetTermsAndConditionsResponse> {

    private LanguagesDao languagesDao = LanguagesDao.getLanguagesDaoFromSpring();

    public TermsAndConditionsAssertion(GetTermsAndConditionsResponse getTermsAndConditionsResponse) {
        this.response = getTermsAndConditionsResponse;
    }

    public void setResponse(GetTermsAndConditionsResponse response) {
        this.response = response;
    }

    public void verifyTheSpecificLocaleWereReturned(String expectedLocale) {
            assertThat(response.getTermsAndConditions().getLocale())
                    .withFailMessage("The locale value is not correct")
                    .isEqualTo(expectedLocale);
            assertThat(response.getTermsAndConditions().getLocalizedDescriptions())
                    .withFailMessage("The locale value is not correct")
                    .extracting("locale")
                    .contains(expectedLocale);
            assertThat(response.getTermsAndConditions().getLocalizedDescriptions())
                    .withFailMessage("The href value is null")
                    .extracting("href")
                    .isNotNull();
            assertThat(response.getTermsAndConditions().getLocalizedDescriptions())
                    .withFailMessage("The value is null")
                    .extracting("value")
                    .isNotNull();
    }

    public void verifyAllTheLocaleWereReturned() {
        List<String> expectedLanguages = languagesDao.getAvailableLanguageCodes();

        expectedLanguages.forEach(ignored -> {
            assertThat(response.getTermsAndConditions()
                    .getLocale())
                    .isEmpty();
            assertThat(response.getTermsAndConditions().getLocalizedDescriptions())
                    .withFailMessage("The locale value is null")
                    .extracting("locale")
                    .isNotNull();
            assertThat(response.getTermsAndConditions().getLocalizedDescriptions())
                    .withFailMessage("The href value is null")
                    .extracting("href")
                    .isNotNull();
            assertThat(response.getTermsAndConditions().getLocalizedDescriptions())
                    .withFailMessage("The value is null")
                    .extracting("value")
                    .isNotNull();
        });
        assertThat(expectedLanguages.size())
                .withFailMessage("Not all the terms and condition were returned")
                .isLessThanOrEqualTo(response.getTermsAndConditions().getLocalizedDescriptions().size());
    }
}
