package com.hybris.easyjet.fixture.alei.invokers.queryparams;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marco on 21/04/17.
 */
@Builder
public class EIPaymentMethodsQueryParameters extends QueryParameters {

   private static final String X_CHANNEL_ID = "X-Channel-ID";

   private String channel;

   @Override
   public Map<String, String> getParameters() {
      Map<String, String> queryParams = new HashMap<>();
      queryParams.put(X_CHANNEL_ID, channel);
      return queryParams;
   }
}
