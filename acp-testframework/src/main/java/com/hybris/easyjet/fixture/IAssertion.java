package com.hybris.easyjet.fixture;

import java.util.List;

/**
 * Created by daniel on 02/12/2016.
 */
public interface IAssertion<T> {
    T containedTheCorrectWarningMessage(String... codes);

    T containedTheCorrectWarningMessage(List<String> codes);
}