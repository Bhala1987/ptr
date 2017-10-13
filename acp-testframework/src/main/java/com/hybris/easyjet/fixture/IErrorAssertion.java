package com.hybris.easyjet.fixture;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;

import java.util.List;

/**
 * Created by daniel on 02/12/2016.
 */
public interface IErrorAssertion {
    void containedTheCorrectErrorMessage(String... codes);

    void containedTheCorrectErrorMessage(List<String> codes);

    void containedTheCorrectErrorAffectedData(String errorCode, List<String> params, List<String> values) throws EasyjetCompromisedException;

    void notContainedTheErrorAffectedData(String errorCode, List<String> params) throws EasyjetCompromisedException;
}