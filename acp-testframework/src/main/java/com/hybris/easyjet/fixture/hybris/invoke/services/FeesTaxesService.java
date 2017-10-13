package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.FeesTaxesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.FeesTaxesResponse;


public class FeesTaxesService extends HybrisService implements IService {

   private FeesTaxesResponse feesTaxesResponse;
   private FeesAndTaxesDao feesAndTaxesDao;

   /**
    *
    * @param request
    * @param endPoint
    */
   public FeesTaxesService(IRequest request, String endPoint, FeesAndTaxesDao feesAndTaxesDao) {
      super(request, endPoint);
      this.feesAndTaxesDao = feesAndTaxesDao;
   }

   @Override
   public IResponse getResponse() {
      assertThatServiceCallWasSuccessful();
      return feesTaxesResponse;
   }

   @Override
   public FeesTaxesAssertion assertThat() {
      return new FeesTaxesAssertion(feesTaxesResponse, feesAndTaxesDao);
   }

   @Override
   protected void checkThatResponseBodyIsPopulated() {
      checkThatResponseBodyIsPopulated(feesTaxesResponse);
   }

   @Override
   protected void mapResponse() {
      feesTaxesResponse = restResponse.as(FeesTaxesResponse.class);
   }

}
