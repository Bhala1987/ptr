package com.hybris.easyjet.fixture.alei.invokers.services.impl;

import com.hybris.easyjet.fixture.IAssertion;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.alei.asserters.EIPaymentMethodsAssertion;
import com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods.EIPaymentMethodsResponse;
import com.hybris.easyjet.fixture.alei.invokers.services.AbstractService;


/**
 * Created by marco on 21/04/17.
 */
public class EIPaymentMethodsService extends AbstractService {

   private EIPaymentMethodsResponse eiPaymentMethodsResponse;

   /**
    * a service comprises a request, a client and an endpoint
    *
    * @param request  the request object required
    * @param endPoint the endpoint of the service
    */
   public EIPaymentMethodsService(IRequest request, String endPoint) {
      super(request, endPoint);
   }

   @Override
   public EIPaymentMethodsResponse getResponse() {
      assertThatServiceCallWasSuccessful();
      return eiPaymentMethodsResponse;
   }

   @Override
   public IAssertion assertThat() {
       return new EIPaymentMethodsAssertion(eiPaymentMethodsResponse);
   }

   @Override
   protected void checkThatResponseBodyIsPopulated() {
      checkThatResponseBodyIsPopulated(eiPaymentMethodsResponse);
   }

   @Override
   protected void mapResponse() {
      eiPaymentMethodsResponse = restResponse.as(EIPaymentMethodsResponse.class);
   }
}
