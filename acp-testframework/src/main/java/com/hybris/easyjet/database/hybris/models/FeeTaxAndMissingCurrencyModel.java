package com.hybris.easyjet.database.hybris.models;


public class FeeTaxAndMissingCurrencyModel {

   private String feeCode;
   private String currencyIsocode;

   public String getFeeCode() {
      return feeCode;
   }

   public void setFeeCode(String feeCode) {
      this.feeCode = feeCode;
   }

   public String getCurrencyIsocode() {
      return currencyIsocode;
   }

   public void setCurrencyIsocode(String currencyIsocode) {
      this.currencyIsocode = currencyIsocode;
   }

   public FeeTaxAndMissingCurrencyModel(String feeCode, String currencyIsocode) {
      this.feeCode = feeCode;
      this.currencyIsocode = currencyIsocode;
   }
}
