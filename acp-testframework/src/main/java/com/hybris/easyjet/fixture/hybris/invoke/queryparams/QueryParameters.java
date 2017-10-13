package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;

import java.util.List;

/**
 * Created by daniel on 28/11/2016.
 */
abstract class QueryParameters implements IQueryParams {

    boolean isPopulated(String stringToCheck) {
        return stringToCheck != null && !stringToCheck.isEmpty();
    }

    boolean isPopulated(List listToCheck) {
        return listToCheck != null && !listToCheck.isEmpty();
    }
}