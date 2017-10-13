package com.hybris.easyjet.fixture.alei.invokers.services.factories;

import com.hybris.easyjet.config.EasyjetEIConfig;
import com.hybris.easyjet.fixture.alei.invokers.requests.EIPaymentMethodsRequest;
import com.hybris.easyjet.fixture.alei.invokers.services.impl.EIPaymentMethodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Factory used to create service classes calling EI
 */
@Component
public class EIServiceFactory {

   private final EasyjetEIConfig config;

   @Autowired
   public EIServiceFactory(EasyjetEIConfig config) {
      this.config = config;
   }

   public EIPaymentMethodsService getEiPaymentMethods(EIPaymentMethodsRequest request) {
      return new EIPaymentMethodsService(request, config.getEiPaymentMethodsEndpoint());
   }

}
