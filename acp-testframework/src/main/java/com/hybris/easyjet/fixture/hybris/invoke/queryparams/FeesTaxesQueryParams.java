package com.hybris.easyjet.fixture.hybris.invoke.queryparams;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@Data
@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
public class FeesTaxesQueryParams extends QueryParameters {

   private String channel;
   private String sectorCode;
   private String currencyCode;
   private String passengerType;

   @Override
   public Map<String, String> getParameters() {

      Map<String, String> queryParams = new HashMap<>();
      if (isPopulated(channel)) {
         queryParams.put("channel", channel);
      }
      if (isPopulated(sectorCode)) {
         queryParams.put("sector-code", sectorCode);
      }
      if (isPopulated(currencyCode)) {
         queryParams.put("currency-code", currencyCode);
      }
      if (isPopulated(passengerType)) {
         queryParams.put("passenger-type", passengerType);
      }

      return queryParams;
   }
}
