package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.fixture.hybris.invoke.response.FeesTaxesResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


public class FeesTaxesAssertion extends Assertion<FeesTaxesAssertion, FeesTaxesResponse> {

   private FeesAndTaxesDao feesAndTaxesDao;

   public FeesTaxesAssertion(FeesTaxesResponse feesTaxesResponse, FeesAndTaxesDao feesAndTaxesDao) {
      this.response = feesTaxesResponse;
      this.feesAndTaxesDao = feesAndTaxesDao;
   }

   public void responseHasAllFeesAndTaxes() {

      int numberOfFeesTaxes = response.getResult().getTaxes().stream().mapToInt(t -> t.getTaxFeeRows().size()).sum();
      numberOfFeesTaxes += response.getResult().getFees().stream().mapToInt(t -> t.getTaxFeeRows().size()).sum();

      int numberOfFeesTaxesFromDAO = feesAndTaxesDao.countFeesAndTaxes();

      assertThat(numberOfFeesTaxes == numberOfFeesTaxesFromDAO).withFailMessage("NOT THE SAME NUMBER OF FEES AND TAXES").isTrue();
   }

   public void responseHasExpectedValues(String taxCode, String channel, String currencyCode, String passengerType,
         String sectorCode) {

      if(Objects.nonNull(taxCode)) {
         assertThat(checkTaxCode(taxCode)).withFailMessage("WRONG TAX FEE PRICING CODE").isTrue();
      }
      if(Objects.nonNull(channel)) {
         assertThat(checkChannel(channel)).withFailMessage("WRONG CHANNEL").isTrue();
      }
      if(Objects.nonNull(currencyCode)) {
         assertThat(checkCurrencyCode(currencyCode)).withFailMessage("WRONG CURRENCY CODE").isTrue();
      }
      if(Objects.nonNull(passengerType)) {
         assertThat(checkPassengerType(passengerType)).withFailMessage("WRONG PASSENGER TYPE").isTrue();
      }
      if(Objects.nonNull(sectorCode)) {
         assertThat(checkSectorCode(sectorCode)).withFailMessage("WRONG SECTOR CODE").isTrue();
      }

   }

   private boolean checkTaxCode(String taxCode) {
      if(StringUtils.isNotEmpty(response.getTaxFeePricing().getCode()) && taxCode.equalsIgnoreCase(response.getTaxFeePricing().getCode())) {
            return true;
      }
      return false;
   }

   private boolean checkChannel(String channel) {
      if(Objects.nonNull(response.getTaxFeePricing()) && CollectionUtils.isNotEmpty(response.getTaxFeePricing().getTaxFeeRows())) {
         return response.getTaxFeePricing().getTaxFeeRows().stream().allMatch(t -> t.getChannel().equalsIgnoreCase(channel));
      }
      return true;
   }

   private boolean checkCurrencyCode(String currencyCode) {
      if(Objects.nonNull(response.getTaxFeePricing()) && CollectionUtils.isNotEmpty(response.getTaxFeePricing().getTaxFeeRows())) {
         return response.getTaxFeePricing().getTaxFeeRows().stream().allMatch(t -> t.getCurrencyCode().equalsIgnoreCase(currencyCode));
      }
      return true;
   }

   private boolean checkPassengerType(String passengerType) {
      if(Objects.nonNull(response.getTaxFeePricing()) && CollectionUtils.isNotEmpty(response.getTaxFeePricing().getTaxFeeRows())) {
         return response.getTaxFeePricing().getTaxFeeRows().stream().allMatch(t -> t.getPassengerType().equalsIgnoreCase(passengerType));
      }
      return true;
   }

   private boolean checkSectorCode(String sectorCode) {
      if(Objects.nonNull(response.getTaxFeePricing()) && CollectionUtils.isNotEmpty(response.getTaxFeePricing().getTaxFeeRows())) {
         return response.getTaxFeePricing().getTaxFeeRows().stream().allMatch(t -> t.getSectorCode().equalsIgnoreCase(sectorCode));
      }
      return true;
   }

   public void responseHasFeesTaxesInASpecificCurrency(String currencyCode) {
      assertThat(checkFeesCurrency(currencyCode)).withFailMessage("WRONG FEES CURRENCY").isTrue();
      assertThat(checkTaxesCurrency(currencyCode)).withFailMessage("WRONG TAXES CURRENCY").isTrue();
   }

   private boolean checkFeesCurrency(String currencyCode) {
      if(Objects.nonNull(response.getResult()) && CollectionUtils.isNotEmpty(response.getResult().getFees())) {
         return response.getResult().getFees().stream().allMatch(t -> t.getTaxFeeRows().stream().allMatch(r -> r.getCurrencyCode().equalsIgnoreCase(currencyCode)));
      }
      return true;
   }

   private boolean checkTaxesCurrency(String currencyCode) {
      if(Objects.nonNull(response.getResult()) && CollectionUtils.isNotEmpty(response.getResult().getTaxes())) {
         return response.getResult().getTaxes().stream().allMatch(t -> t.getTaxFeeRows().stream().allMatch(r -> r.getCurrencyCode().equalsIgnoreCase(currencyCode)));
      }
      return true;
   }

}
