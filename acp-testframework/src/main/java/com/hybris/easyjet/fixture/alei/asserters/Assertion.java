package com.hybris.easyjet.fixture.alei.asserters;

import com.hybris.easyjet.fixture.IAssertion;
import com.hybris.easyjet.fixture.alei.invokers.responses.Response;

import java.util.List;

/**
 * Created by g.dimartino on 07/03/17.
 * This abstract class is used to generalize the checks on the additional information for every service
 *
 * @param <T> Assertion class that will extend this class
 * @param <S> Response class used by the assertion class T
 */
public abstract class Assertion<T extends Assertion, S extends Response> implements IAssertion<T> {

    protected S response;

    @SuppressWarnings("unchecked")
    public T failureReturned(String... codes) {

        //TODO implement
//        for (String type : codes) {
//            assertThat(VALID_WARNINGS.containsKey(type))
//                    .withFailMessage("The warning message " + response.getAdditionalInformation().get(0).getType() + " was not in the list of expected additional information")
//                    .isTrue();
//            assertThat(response.getAdditionalInformation()).extracting("type", "message")
//                    .contains(Tuple.tuple(
//                            type,
//                            VALID_WARNINGS.get(type)
//                    ));
//        }

        return (T) this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public T containedTheCorrectWarningMessage(String... codes) {
//        Arrays.asList(codes).forEach(code ->
//                assertThat(response.getAdditionalInformations().stream()
//                        .anyMatch(warning -> warning.getCode().equalsIgnoreCase(code)))
//                        .withFailMessage("Missing expected warning: " + code)
//                        .isTrue()
//        );
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T containedTheCorrectWarningMessage(List<String> codes) {
//        Arrays.asList(codes).forEach(code ->
//                assertThat(response.getAdditionalInformations().stream()
//                        .anyMatch(warning -> warning.getCode().equalsIgnoreCase(code)))
//                        .withFailMessage("Missing expected warning: " + code)
//                        .isTrue()
//        );
        return (T) this;
    }

}