package com.hybris.easyjet.config;

import com.hybris.easyjet.config.constants.PropertyNameConstants;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by marco on 21/04/17.
 */
@Component
@Getter
public class EasyjetEIConfig extends EasyjetTestConfig {

   private final String eiPaymentMethodsEndpoint;

   @Autowired
   public EasyjetEIConfig(Environment environment) {
      super(environment);
      if("true".equalsIgnoreCase(System.getProperty("mocked"))) {
         this.eiPaymentMethodsEndpoint = environment.getProperty(PropertyNameConstants.EI_PAYMENT_METHOD_MOCKED);
      } else {
         this.eiPaymentMethodsEndpoint = environment.getProperty(PropertyNameConstants.EI_PAYMENT_METHOD);
      }
   }
}
