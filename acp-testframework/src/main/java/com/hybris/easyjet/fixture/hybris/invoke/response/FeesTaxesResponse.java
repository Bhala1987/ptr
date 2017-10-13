package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IResponse;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class FeesTaxesResponse extends Response implements IResponse {

   @JsonProperty("result")
   private FeesTaxesResponse.ResultTaxes result;

   @JsonProperty("taxFeePricing")
   private FeesTaxesResponse.TaxFeePricing taxFeePricing;

   @Getter
   @Setter
   public static class ResultTaxes {

      @JsonProperty("fees")
      private List<TaxFeePricing> fees;

      @JsonProperty("taxes")
      private List<TaxFeePricing> taxes;

      @Getter
      @Setter
      public static class TaxFeePricing {

         @JsonProperty("code")
         private String code;

         @JsonProperty("type")
         private String type;

         @JsonProperty("level")
         private String level;

         @JsonProperty("localizedNames")
         private List<LocalizedName> localizedNames;

         @JsonProperty("taxFeeRows")
         private List<TaxFeeRows> taxFeeRows;

         @Getter
         @Setter
         public static class TaxFeeRows {

            @JsonProperty("currencyCode")
            private String currencyCode;

            @JsonProperty("value")
            private double value;

            @JsonProperty("type")
            private String type;

            @JsonProperty("sectorCode")
            private String sectorCode;

            @JsonProperty("channel")
            private String channel;

            @JsonProperty("tier")
            private String tier;

            @JsonProperty("passengerType")
            private String passengerType;

            @JsonProperty("startDate")
            private String startDate;

            @JsonProperty("endDate")
            private String endDate;

            @JsonProperty("chargedPerSeat")
            private Boolean chargedPerSeat;
         }

      }
   }

   @Getter
   @Setter
   public static class TaxFeePricing {

      @JsonProperty("code")
      private String code;

      @JsonProperty("type")
      private String type;

      @JsonProperty("level")
      private String level;

      @JsonProperty("localizedNames")
      private List<LocalizedName> localizedNames;

      @JsonProperty("taxFeeRows")
      private List<ResultTaxes.TaxFeePricing.TaxFeeRows> taxFeeRows;
   }

   @Getter
   @Setter
   private static class LocalizedName{
      private String value;
      private String locale;
   }
}
