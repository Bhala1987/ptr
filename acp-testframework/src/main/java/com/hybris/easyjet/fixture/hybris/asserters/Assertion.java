package com.hybris.easyjet.fixture.hybris.asserters;


import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.IAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by g.dimartino on 07/03/17.
 * This abstract class is used to generalize the checks on the additional information for every service
 *
 * @param <T> Assertion class that will extend this class
 * @param <S> Response class used by the assertion class T
 */
@NoArgsConstructor
public class Assertion<T extends Assertion, S extends Response> implements IAssertion<T> {

    protected S response;
    SerenityFacade testData = SerenityFacade.getTestDataFromSpring();

    @SuppressWarnings("unchecked")
    public T additionalInformationReturned(String... codes) {

        for (String code : codes) {
            assertThat(response.getAdditionalInformations().stream().anyMatch(
                    warning -> warning.getCode().contains(code)))
                    .withFailMessage("No additional information returned.");
        }
        return (T) this;
    }


    @SuppressWarnings("unchecked")
    public T affectedDataContains(String... informations) {

        for (String information : informations) {
            final List<AdditionalInformation.AffectedData> affectedData = response.getAdditionalInformations().stream().flatMap(additionalInformation -> additionalInformation.getAffectedData().stream()).collect(Collectors.toList());
            affectedData.forEach(affectedData1 -> assertThat(affectedData1.getInformation().contains(information)).isTrue());
        }

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T affectedDataNameReturned(String... dataNames) {

        for (String dataName : dataNames) {
            final boolean[] assertPassed = new boolean[1];
            assertPassed[0] = false;

            response.getAdditionalInformations().get(0).getAffectedData().forEach(affectedData -> {
                if (affectedData.dataName.contains(dataName)) {
                    assertPassed[0] = true;
                }
            });
            assertThat(assertPassed[0]).withFailMessage("Expected to contain '" + dataName + "' in the response but not found").isEqualTo(true);
        }

        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T additionalInformationOnlyReturned(String... codes) {
        assertThat(response.getAdditionalInformations().size())
                .withFailMessage("Additional information contains unexpected warning: " +
                        response.getAdditionalInformations().get(0).getCode())
                .isEqualTo(codes.length);

        return (T) this;

    }

    @SuppressWarnings("unchecked")
    public T additionalInformationContains(String... codes) {
        java.util.Arrays.asList(codes).forEach(code ->
                assertThat(
                        response.getAdditionalInformations().stream().anyMatch(
                                error -> error.getCode().equalsIgnoreCase(code)
                        )
                ).withFailMessage(
                        "EXPECTED : " + code
                ).isTrue()
        );
        return (T) this;
    }

    public T additionalInformationContainsMessage(String... messages) {
        java.util.Arrays.asList(messages).forEach(message ->
                assertThat(
                        response.getAdditionalInformations().stream().anyMatch(
                                error -> error.getMessage().equalsIgnoreCase(message)
                        )
                ).withFailMessage(
                        "EXPECTED : " + message
                ).isTrue()
        );
        return (T) this;
    }


    @SuppressWarnings("unchecked")
    @Step("Additional information {1}")
    public T additionalInformationContains(Response response, String... codes) {
        java.util.Arrays.asList(codes).forEach(code ->
                assertThat(response.getAdditionalInformations().stream()
                        .anyMatch(error -> error.getCode().equalsIgnoreCase(code)))
                        .withFailMessage("EXPECTED : " + code)
                        .isTrue()
        );
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T additionalInformationIsEmpty() {
        if (!"true".equalsIgnoreCase(System.getProperty("mocked"))) {
            assertThat(response.getAdditionalInformations())
                    .withFailMessage("Additional information contains unexpected messages" +
                            response.getAdditionalInformations())
                    .isEmpty();
        }
        return (T) this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public T containedTheCorrectWarningMessage(String... codes) {
        Arrays.asList(codes).forEach(code ->
                assertThat(response.getAdditionalInformations().stream()
                        .anyMatch(warning -> warning.getCode().equalsIgnoreCase(code)))
                        .withFailMessage("Missing expected warning: " + code)
                        .isTrue()
        );
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T containedTheCorrectWarningMessage(List<String> codes) {
        codes.forEach(code ->
                assertThat(response.getAdditionalInformations())
                        .withFailMessage(code + " warning code, not included in the response.")
                        .extracting("code")
                        .contains(code)
        );
        return (T) this;
    }

}