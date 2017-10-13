package com.hybris.easyjet.fixture.hybris.invoke.services;


import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.asserters.RefundablePaymentMethodsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.RefundPaymentMethodsResponse;


/**
 * Created by aspiggia on 19/07/17.
 */
public class GetRefundablePaymentMethodsService extends HybrisService {

   private RefundPaymentMethodsResponse paymentMethodsResponse;

   /**
    * Create the service
    * @param request
    * @param endPoint
    */
   public GetRefundablePaymentMethodsService(IRequest request, String endPoint)  {
      super(request, endPoint);
   }

   @Override
   public RefundPaymentMethodsResponse getResponse() {
      assertThatServiceCallWasSuccessful();
      return paymentMethodsResponse;
   }

   @Override
   public RefundablePaymentMethodsAssertion assertThat() {
      return new RefundablePaymentMethodsAssertion(paymentMethodsResponse);
   }

   @Override
   protected void checkThatResponseBodyIsPopulated() {
      checkThatResponseBodyIsPopulated(paymentMethodsResponse);
   }

   @Override
   protected void mapResponse() {
      paymentMethodsResponse = restResponse.as(RefundPaymentMethodsResponse.class);
   }
}
