package com.hybris.easyjet.fixture.alei.invokers.requests;


import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.alei.invokers.ALHeaders;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;


/**
 * Created by giuseppedimartino on 31/01/17.
 */
public class InventoryRequest extends AbstractRequest {

   /**
    * this class models a request that can be sent to a service
    *
    * @param headers        a ALHeaders wit Content-Type, X-Client-Id and Accept header
    * @param pathParameters a InventoryPathParametr with allocations or deallocations path
    * @param requestBody    a InventoryBodyRequest
    */
   public InventoryRequest(ALHeaders headers, IPathParameters pathParameters, IRequestBody requestBody) {
      super(headers, POST, pathParameters, null, requestBody);
   }
}
