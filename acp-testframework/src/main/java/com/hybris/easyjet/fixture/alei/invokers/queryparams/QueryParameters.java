package com.hybris.easyjet.fixture.alei.invokers.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;


/**
 * Created by daniel on 28/11/2016.
 */
abstract class QueryParameters implements IQueryParams {
   boolean isPopulated(String stringToCheck) {
      return stringToCheck != null && !stringToCheck.isEmpty();
   }
}
