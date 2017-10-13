package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.IErrorAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for errors response object, provides reusable assertions to all tests
 */
public class ErrorsAssertion implements IErrorAssertion {

    private final Errors errors;

    public ErrorsAssertion(Errors errors) {
        this.errors = errors;
    }

    @Override
    public void containedTheCorrectErrorMessage(String... codes) {
        Arrays.asList(codes).forEach(code ->
                assertThat(
                        errors.getErrors().stream().anyMatch(
                                error -> error.getCode().equalsIgnoreCase(code)
                        )
                ).withFailMessage(
                        "EXPECTED : " + code
                ).isTrue()
        );
    }

    @Override
    public void containedTheCorrectErrorMessage(List<String> codes) {
        codes.forEach(code ->
                assertThat(errors.getErrors())
                        .withFailMessage(code + " error code, not included in the response.")
                        .extracting("code")
                        .contains(code)
        );
    }

    @Override
    public void containedTheCorrectErrorAffectedData(String errorCode, List<String> params, List<String> values) throws EasyjetCompromisedException {
        List<Errors.AffectedData> affetctedData = errors.getErrors().stream()
                .filter(error -> error.getCode().equals(errorCode))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The error code " + errorCode + " was not present"))
                .getAffectedData();

        for (int i = 0; i < params.size(); i++) {
            assertThat(affetctedData)
                    .withFailMessage("Additional information " + params.get(i) + " with value " + values.get(i) + " was not present")
                    .extracting("dataName", "dataValue")
                    .contains(tuple(
                            params.get(i),
                            values.get(i)));
        }
    }

    @Override
    public void notContainedTheErrorAffectedData(String errorCode, List<String> params) throws EasyjetCompromisedException {
        List<Errors.AffectedData> affetctedData = errors.getErrors().stream()
                .filter(error -> error.getCode().equals(errorCode))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The error code " + errorCode + " was not present"))
                .getAffectedData();

        for (int i = 0; i < params.size(); i++) {
            assertThat(affetctedData)
                    .withFailMessage("Additional information contains " + params.get(i))
                    .extracting("dataName")
                    .doesNotContain(params.get(i));
        }
    }

    public void containedTheAffectedData(String... affectedData) {

        for (String information : affectedData) {
            final List<String> affectedData1 = errors.getErrors().stream().flatMap(error -> error.getAffectedData().stream().map(data -> data.getDataName())).collect(Collectors.toList());
            assertThat(affectedData1.stream().anyMatch(data -> data.equalsIgnoreCase(information))).isTrue();
        }

    }
}
