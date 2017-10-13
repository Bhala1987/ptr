package com.hybris.easyjet.fixture.alei.invokers.requests;

import com.hybris.easyjet.fixture.IQueryParams;
import com.hybris.easyjet.fixture.alei.invokers.ALHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.GET;


/**
 * Created by marco on 21/04/17.
 */
public class EIPaymentMethodsRequest extends AbstractRequest {
   /**
    * this class models a request that can be sent to a service
    *
    * @param headers         the headers that should be sent as part of the request
    * @param queryParameters any query parameters required
    */
   public EIPaymentMethodsRequest(ALHeaders headers, IQueryParams queryParameters) {
      super(headers, GET, null, queryParameters, null);
   }
}
