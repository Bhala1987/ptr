package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;
import static com.hybris.easyjet.config.constants.HttpMethods.POST;


public class FeesTaxesRequest  extends HybrisRequest implements IRequest {

   public FeesTaxesRequest(HybrisHeaders headers, IPathParameters pathParams, IQueryParams queryParams) {
      super(headers, GET, pathParams, queryParams, null);
   }
}
