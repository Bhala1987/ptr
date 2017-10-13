package com.hybris.easyjet.fixture.alei.asserters;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.IErrorAssertion;
import com.hybris.easyjet.fixture.alei.invokers.responses.Errors;

import java.util.List;

/**
 * Created by giuseppedimartino on 25/04/17.
 */
public class ErrorAssertion implements IErrorAssertion {

    private final Errors errors;

    public ErrorAssertion(Errors errors) {
        this.errors = errors;
    }

    @Override
    public void containedTheCorrectErrorMessage(String... codes) {

    }

    @Override
    public void containedTheCorrectErrorMessage(List<String> codes) {

    }

    @Override
    public void containedTheCorrectErrorAffectedData(String errorCode, List<String> params, List<String> values) {

    }

    @Override
    public void notContainedTheErrorAffectedData(String errorCode, List<String> params) throws EasyjetCompromisedException {

    }
}
