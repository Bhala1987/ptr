package com.hybris.easyjet.fixture.alei.invokers.pathparams;

/**
 * Created by daniel on 28/11/2016.
 */
class PathParameters {

    boolean isPopulated(String stringToCheck) {
        return stringToCheck != null && !stringToCheck.isEmpty();
    }
}
