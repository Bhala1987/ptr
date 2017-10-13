package com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods;

import com.hybris.easyjet.fixture.alei.invokers.responses.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



/**
 * Created by marco on 21/04/17.
 */
@ToString
@Getter
@Setter
public class EIPaymentMethodsResponse extends Response {
   public PaymentMethodsResponse paymentMethodsResponse;

//   @Getter
//   @Setter
//   public static class PaymentMethodsResponse {
//      public List<PaymentMethod> paymentMethods;
//   }
//
//   @Getter
//   @Setter
//   public static class PaymentMethod {
//      public String channelID;
//      public List<PaymentType> paymentType;
//   }
//
//   @Getter
//   @Setter
//   public static class PaymentType {
//      public AllowedCurrency allowedCurrencies;
//      public String allowedDaysTillDeparture;
//      public String allowedMarketCountryCode;
//      public String bankNameRequired;
//      public String cardHolderNameRequired;
//      public String cardNumberRequired;
//      public String type;
//      public String description;
//      public String isCreditCard;
//      public String issueNumberRequired;
//      public String paymentMethod;
//      public String paymentMethodId;
//      public String securityCodeLength;
//      public String securityNumberRequired;
//      public String startDateRequired;
//   }
//
//   @Getter
//   @Setter
//   public static class AllowedCurrency {
//      public List<Currency> currency;
//   }
//
//   @Getter
//   @Setter
//   public static class Currency {
//      public String type;
//   }

}