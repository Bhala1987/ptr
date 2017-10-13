package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.asserters.models.ExpectedLanguage;
import com.hybris.easyjet.fixture.hybris.invoke.response.LanguagesResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 02/12/2016.
 * assertion wrapper for languages response object, provides reusable assertions to all tests
 */
public class LanguagesAssertion extends Assertion<LanguagesAssertion, LanguagesResponse> {

    public LanguagesAssertion(LanguagesResponse languagesResponse) {

        this.response = languagesResponse;
    }

    public void onlyTheseLanguagesWereReturned(List<ExpectedLanguage> expectedLanguages) {

        for (ExpectedLanguage expectedLanguage : expectedLanguages) {
            assertThat(response.getLanguages())
                    .extracting(
                            "code")
                    .contains(
                            expectedLanguage.getCode().replace("_", "-"));
        }
        assertThat(expectedLanguages.size()).isEqualTo(response.getLanguages().size());
    }

    public void theseLanguagesWereNotReturned(List<ExpectedLanguage> notExpectedLanguages) {

        for (ExpectedLanguage notExpectedLanguage : notExpectedLanguages) {
            assertThat(response.getLanguages())
                    .extracting("code")
                    .doesNotContain(notExpectedLanguage.getIsoCode());
        }
    }
}
