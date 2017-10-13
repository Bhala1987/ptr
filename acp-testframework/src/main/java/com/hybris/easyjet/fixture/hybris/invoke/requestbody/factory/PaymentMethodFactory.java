package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.*;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails.SavedPaymentMethodRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.PaymentMethodsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.managebooking.PaymentBalanceResponse;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hybris.easyjet.config.constants.CommonConstants.CREDIT;

/**
 * Created by daniel on 28/11/2016.
 */

@Component
public class PaymentMethodFactory {
    private static final String NEGATIVE_BOOKING = "Cannot commit the booking: the basket total is negative.";

    @Autowired
    private SerenityFacade testData;

    private PaymentMethodFactory() {
    }


    public static PaymentMethod generatePaymentMethodBodyDebitCardWithMissingParameter(BasketsResponse basket, String parameter) throws EasyjetCompromisedException {

        if ("MissingPaymentMethods".equals(parameter)) {
            return null;
        }

        PaymentMethod paymentMethod = generateDebitCardPaymentMethod(basket.getBasket());

        switch (parameter) {
            case "MissingPaymentMethod":
                paymentMethod.setPaymentMethod(null);
                break;
            case "MissingPaymentCode":
                paymentMethod.setPaymentCode(null);
                break;
            case "MissingPaymentAmount":
                paymentMethod.setPaymentAmount(null);
                break;
            case "MissingPaymentCurrency":
                paymentMethod.setPaymentCurrency(null);
                break;

            default:
                throw new IllegalArgumentException("the parameter you provided is not valid.");
        }

        return paymentMethod;
    }

    public static PaymentMethod generateValidPaymentMethodBody(List<PaymentMethodsResponse.PaymentMethod> paymentMethodsAvailable, Basket basket) {
        PaymentMethod.PaymentMethodBuilder builder = PaymentMethod.builder();
        builder.paymentMethod(paymentMethodsAvailable.get(0).getPaymentMethod());
        builder.paymentCode(paymentMethodsAvailable.get(0).getCode());
        if (paymentMethodsAvailable.get(0).getIsCreditCard()) {
            builder.paymentAmount(Objects.nonNull(basket.getPriceDifference()) ? basket.getPriceDifference().getAmountWithCreditCard() : basket.getTotalAmountWithCreditCard());
        } else {
            builder.paymentAmount(Objects.nonNull(basket.getPriceDifference()) ? basket.getPriceDifference().getAmountWithDebitCard() : basket.getTotalAmountWithDebitCard());
        }
        builder.paymentCurrency(basket.getCurrency().getCode());

        return builder.build();
    }

    public static PaymentMethod generateCashPaymentMethodsFromBasket(Basket basket, String currencyCode, boolean savePaymentMethod) {
        return PaymentMethod.builder()
                .paymentMethod(CommonConstants.CASH)
                .paymentCode(CommonConstants.CASH)
                .paymentAmount(Objects.nonNull(basket.getPriceDifference()) ? basket.getPriceDifference().getAmountWithDebitCard() : basket.getTotalAmountWithDebitCard())
                .paymentCurrency(currencyCode)
                .savePaymentMethod(savePaymentMethod)
                .cash(CashPayment.builder().receiptNumber("111111").build())
                .build();
    }

    public static PaymentMethod generateCreditFilePaymentMethod(String fileName, Double amount, String currency, String comment) {
        return PaymentMethod.builder()
                .paymentMethod(CommonConstants.CREDITFILEFUND)
                .paymentCode("CF")
                .paymentAmount(amount)
                .paymentCurrency(currency)
                .savePaymentMethod(true)
                .fundPayment(FundPayment.builder().
                        fundReference(fileName).
                        comment(comment).
                        build())
                .build();
    }

    public static PaymentMethod generateELVPaymentMethod(String accountHolderName, String accountNumber, String bankCity, String bankCode, String bankCountryCode, String bankName, Double amount, String currency) {
        return PaymentMethod.builder()
                .paymentMethod(CommonConstants.ELV)
                .paymentCode("EV")
                .bankAccount(BankAccount.builder()
                        .accountHolderName(accountHolderName)
                        .accountNumber(accountNumber)
                        .bankCity(bankCity)
                        .bankCode(bankCode)
                        .bankCountryCode(bankCountryCode)
                        .bankName(bankName)
                        .isDefault(false)
                        .build()
                )
                .paymentAmount(amount)
                .paymentCurrency(currency)
                .savePaymentMethod(true)
                .build();
    }

    public static PaymentMethod generateDebitCardPaymentMethod(Basket basket) throws EasyjetCompromisedException {
        if (basket.getTotalAmountWithDebitCard() < 0) {
            throw new EasyjetCompromisedException(NEGATIVE_BOOKING);
        }

        return PaymentMethod.builder()
                .paymentMethod(CommonConstants.CARD)
                .paymentCode("DM")
                .card(aBasicDebitCardDetails())
                .paymentAmount(Objects.nonNull(basket.getPriceDifference()) ? basket.getPriceDifference().getAmountWithDebitCard() : basket.getTotalAmountWithDebitCard())
                .paymentCurrency(basket.getCurrency().getCode())
                .savePaymentMethod(true)
                .build();
    }
    public static PaymentMethod generateCreditCardPaymentMethod(Basket basket) throws EasyjetCompromisedException {
        if (basket.getTotalAmountWithDebitCard() < 0) {
            throw new EasyjetCompromisedException(NEGATIVE_BOOKING);
        }

        return PaymentMethod.builder()
                .paymentMethod(CommonConstants.CARD)
                .paymentCode("VI")
                .card(aBasicCreditCardDetails())
                .paymentAmount(Objects.nonNull(basket.getPriceDifference()) ? basket.getPriceDifference().getAmountWithCreditCard() : basket.getTotalAmountWithCreditCard())
                .paymentCurrency(basket.getCurrency().getCode())
                .savePaymentMethod(true)
                .build();
    }

    public static PaymentMethod generateDebitCardPaymentMethod(String cardType, String cardNumber, String securityCode, String expireMonth, String expireYear, String holderName, double amount, String currency) throws EasyjetCompromisedException {
        if (amount < 0) {
            throw new EasyjetCompromisedException(NEGATIVE_BOOKING);
        }

        return PaymentMethod.builder()
                .paymentMethod(CommonConstants.CARD)
                .paymentCode(cardType)
                .card(aCustomizeCardDetails(cardType, cardNumber, securityCode, expireMonth, expireYear, holderName))
                .paymentAmount(amount)
                .paymentCurrency(currency)
                .savePaymentMethod(true)
                .build();
    }

    private static Card aBasicCreditCardDetails() {
        return Card.builder()
                .cardType("VI")
                .cardNumberOrToken("4444333322221111")
                .cardSecurityNumber("737")
                .cardExpiryMonth("08")
                .cardExpiryYear("2018")
                .cardHolderName("Testing card")
                .build();
    }


    private static Card aBasicDebitCardDetails() {
        return Card.builder()
                .cardType("DM")
                .cardNumberOrToken("5573471234567898")
                .cardSecurityNumber("123")
                .cardExpiryMonth("08")
                .cardExpiryYear("2018")
                .cardHolderName("Testing card")
                .build();
    }

    private static Card aCustomizeCardDetails(String cardType, String cardNumber, String securityCode, String expireMonth, String expireYear, String holderName) {
        return Card.builder()
                .cardType(cardType)
                .cardNumberOrToken(cardNumber)
                .cardSecurityNumber(securityCode)
                .cardExpiryMonth(expireMonth)
                .cardExpiryYear(expireYear)
                .cardHolderName(holderName)
                .build();
    }

    public static Double getPaymentAmountFromCardType(Basket basket, String cardType) {
        Double paymentAmount = 0.0;
        switch (cardType) {
            case "DM":
            case "DL":
            case "SW":
            case "CB":
                paymentAmount = basket.getTotalAmountWithDebitCard();
                if(Objects.nonNull(basket.getPriceDifference())) {
                    paymentAmount = basket.getPriceDifference().getAmountWithDebitCard();
                }
                break;
            case "VI":
            case "MC":
            case "AX":
            case "DC":
            case "TP":
                paymentAmount = basket.getTotalAmountWithCreditCard();
                if(Objects.nonNull(basket.getPriceDifference())) {
                    paymentAmount = basket.getPriceDifference().getAmountWithCreditCard();
                }
                break;
            default:
                paymentAmount = basket.getTotalAmountWithCreditCard();
                if(Objects.nonNull(basket.getPriceDifference())) {
                    paymentAmount = basket.getPriceDifference().getAmountWithCreditCard();
                }
                break;
        }

        return paymentAmount;
    }

    private static Double getPaymentAmountFromPaymentType(Basket basket, String paymentType) {
        Double paymentAmount = 0.0;
        switch (paymentType) {
            case CommonConstants.DEBIT:
            case CommonConstants.CREDITFILEFUND:
            case CommonConstants.COMBINATION:
            case CommonConstants.CREDITFILE:
                paymentAmount = basket.getTotalAmountWithDebitCard();
                if(Objects.nonNull(basket.getPriceDifference())) {
                    paymentAmount = basket.getPriceDifference().getAmountWithDebitCard();
                }
                break;
            case CommonConstants.CREDIT:
                paymentAmount = basket.getTotalAmountWithCreditCard();
                if(Objects.nonNull(basket.getPriceDifference())) {
                    paymentAmount = basket.getPriceDifference().getAmountWithCreditCard();
                }
                break;
            default:
                paymentAmount = basket.getTotalAmountWithDebitCard();
                if(Objects.nonNull(basket.getPriceDifference())) {
                    paymentAmount = basket.getPriceDifference().getAmountWithDebitCard();
                }
        }

        return paymentAmount;
    }

    public List<PaymentMethod> generateMultiplePaymentMethod(Basket basket, int numberOfPayment, String paymentDetails, String paymentType) throws EasyjetCompromisedException {

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        String[] paymentMethodDetails = paymentDetails.split(";");
        List<BigDecimal> splitAmount = generateSplitAmount(getPaymentAmountFromPaymentType(basket, paymentType), numberOfPayment);

        int i = 0;
        do {
            String[] cardDetails = paymentMethodDetails[i].split("-");
            paymentMethodList.add(
                    selectPaymentMethod(cardDetails[0], cardDetails, splitAmount.get(i).doubleValue(), basket.getCurrency().getCode())
            );
            i++;
        } while (i < numberOfPayment);
        return paymentMethodList;
    }

    public List<PaymentMethod> generateMultiplePaymentMethodCreditDebit(Basket basket, int numberOfPayment, String paymentDetails) throws EasyjetCompromisedException {

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        String[] paymentMethodDetails = paymentDetails.split(";");
        List<BigDecimal> splitAmount = generateSplitAmount(getPaymentAmountFromPaymentType(basket, CommonConstants.DEBIT), numberOfPayment);

        int i = 0;
        do {
            String[] cardDetails = paymentMethodDetails[i].split("-");
            BigDecimal creditCardFee = BigDecimal.valueOf(0.0);
            if (cardDetails[i].equalsIgnoreCase(CREDIT)){
                BigDecimal creditCardAmount = BigDecimal.valueOf(splitAmount.get(i).doubleValue() * 1.05).setScale(2, BigDecimal.ROUND_HALF_UP);
                paymentMethodList.add(
                        selectPaymentMethod(cardDetails[0], cardDetails, creditCardAmount.doubleValue(), basket.getCurrency().getCode())
                );
                creditCardFee = creditCardFee.add(creditCardAmount.subtract(BigDecimal.valueOf(splitAmount.get(i).doubleValue())));
                testData.setData(SerenityFacade.DataKeys.CREDIT_CARD_FEE, creditCardFee.doubleValue());
            } else {
                paymentMethodList.add(
                        selectPaymentMethod(cardDetails[0], cardDetails, splitAmount.get(i).doubleValue(), basket.getCurrency().getCode())
                );
            }
            i++;
        } while (i < numberOfPayment);
        return paymentMethodList;
    }


    public List<PaymentMethod> generateMultiplePaymentMethodCalculatePaymentBalance(Basket basket, PaymentBalanceResponse paymentBalanceResponse, int numberOfPayment, String paymentDetails) throws EasyjetCompromisedException {

        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        String[] paymentMethodDetails = paymentDetails.split(";");

        int i = 0;
        do {
            String[] cardDetails = paymentMethodDetails[i].split("-");
            if (Objects.isNull(paymentBalanceResponse.getProposedPayments().getPaymentMethods().get(i).getFeeAmount())) {
                paymentMethodList.add(
                        selectPaymentMethod(cardDetails[0], cardDetails, paymentBalanceResponse.getProposedPayments().getPaymentMethods().get(i).getPaymentAmount(), basket.getCurrency().getCode())
                );
            } else
            {
                BigDecimal paymentAmount = BigDecimal.valueOf(paymentBalanceResponse.getProposedPayments().getPaymentMethods().get(i).getPaymentAmount());
                BigDecimal feeAmount = BigDecimal.valueOf(paymentBalanceResponse.getProposedPayments().getPaymentMethods().get(i).getFeeAmount());
                paymentMethodList.add(
                        selectPaymentMethod(cardDetails[0], cardDetails, BigDecimal.valueOf(paymentAmount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + feeAmount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(), basket.getCurrency().getCode())
                );
            }
            i++;
        } while (i < numberOfPayment);
        return paymentMethodList;
    }

    private PaymentMethod selectPaymentMethod(String typeOfPayment, String[] paymentDetails, double amount, String currency) throws EasyjetCompromisedException {
        switch (typeOfPayment) {
            case CommonConstants.DEBIT:
            case CommonConstants.CREDIT:
                return generateDebitCardPaymentMethod(paymentDetails[1], paymentDetails[2], paymentDetails[3], paymentDetails[4], paymentDetails[5], paymentDetails[6], amount, currency);
            case CommonConstants.CREDITFILE:
                return generateCreditFilePaymentMethod(paymentDetails[1], amount, currency, "FCPH-8461");
            case CommonConstants.ELV:
                return generateELVPaymentMethod(paymentDetails[1], paymentDetails[2], paymentDetails[3], paymentDetails[4], paymentDetails[5], paymentDetails[6], amount, currency);
            default:
                throw new IllegalArgumentException("Define a valid payment method type");
        }
    }

    private static List<BigDecimal> generateSplitAmount(Double amount, int numberOfPayment) {
        ArrayList<BigDecimal> list = new ArrayList<>();
        double f = amount / numberOfPayment;
        double r = amount % numberOfPayment;
        for (int i = 0; i < numberOfPayment; i++) {
            if (i <= numberOfPayment - r) {
                list.add(BigDecimal.valueOf(f).setScale(2, BigDecimal.ROUND_HALF_DOWN));
            } else {
                list.add(BigDecimal.valueOf(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).add(BigDecimal.ONE));
            }
        }
        final BigDecimal[] updatedTot = {BigDecimal.ZERO};
        list.forEach(partialAmount ->
                updatedTot[0] = updatedTot[0].add(partialAmount).setScale(2, BigDecimal.ROUND_HALF_DOWN)
        );

        if (updatedTot[0].equals(BigDecimal.valueOf(amount))) {
            return list;
        } else {
            BigDecimal diff = BigDecimal.valueOf(amount).subtract(updatedTot[0]).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            list.set(0, list.get(0).add(diff));
            return list;
        }
    }

    public static PaymentMethod generatePaymentRequestForCard(BasketsResponse basketResponse, String paymentMethod, Map<String, String> cardPaymentDetails, String currency) throws EasyjetCompromisedException {
        Basket basket = basketResponse.getBasket();
        String cardType = cardPaymentDetails.get("cardType");
        String cardNumber = cardPaymentDetails.get("cardNumber");
        String cardSecNumber = cardPaymentDetails.get("cardSecNumber");
        String cardExpiryMonth = cardPaymentDetails.get("cardExpiryMonth");
        String cardExpiryYear = cardPaymentDetails.get("cardExpiryYear");
        String cardHolderName = cardPaymentDetails.get("cardHolderName");
        String theCurrency = null;
        Double paymentAmount = 0.0;

        if ("basketCurrency".equals(currency)) {
            theCurrency = basket.getCurrency().getCode();
        } else {
            theCurrency = currency;
        }

        paymentAmount = getPaymentAmountFromCardType(basket, cardType);

        if (paymentAmount < 0) {
            throw new EasyjetCompromisedException("Cannot commit the booking: the basket total is negative.");
        }

        return PaymentMethod.builder()
                .paymentMethod(paymentMethod)
                .paymentCode(cardType)
                .card(aCustomizeCardDetails(cardType, cardNumber, cardSecNumber, cardExpiryMonth, cardExpiryYear, cardHolderName))
                .paymentAmount(paymentAmount)
                .paymentCurrency(theCurrency)
                .savePaymentMethod(true)
                .build();
    }

    public static PaymentMethod generatePaymentRequestForElv(BasketsResponse basketResponse, String paymentMethod, Map<String, String> elvPaymentDetails, String currency) throws EasyjetCompromisedException {
        Basket basket = basketResponse.getBasket();
        String accountHolderName = elvPaymentDetails.get("accountHolderName");
        String accountNumber = elvPaymentDetails.get("accountNumber");
        String bankCity = elvPaymentDetails.get("bankCity");
        String bankCode = "".equalsIgnoreCase(elvPaymentDetails.get("bankCode")) ? "12345678" : elvPaymentDetails.get("bankCode");
        String bankCountryCode = elvPaymentDetails.get("bankCountryCode");
        String bankName = elvPaymentDetails.get("bankName");
        String theCurrency = null;

        if ("basketCurrency".equals(currency)) {
            theCurrency = basket.getCurrency().getCode();
        } else {
            theCurrency = currency;
        }

        if (basket.getTotalAmountWithDebitCard() < 0) {
            throw new EasyjetCompromisedException(NEGATIVE_BOOKING);
        }

        return PaymentMethod.builder()
                .paymentMethod(paymentMethod)
                .paymentCode("EV")
                .bankAccount(BankAccount.builder()
                        .accountHolderName(accountHolderName)
                        .accountNumber(accountNumber)
                        .bankCity(bankCity)
                        .bankCode(bankCode)
                        .bankCountryCode(bankCountryCode)
                        .bankName(bankName)
                        .build()
                )
                .paymentAmount(Objects.nonNull(basket.getPriceDifference()) ? basket.getPriceDifference().getAmountWithDebitCard() : basket.getTotalAmountWithDebitCard())
                .paymentCurrency(theCurrency)
                .savePaymentMethod(true)
                .build();
    }

    public static SavedPaymentMethodRequestBody aBasicAddDebitCardPaymentDetails(boolean isDefault) {
        return SavedPaymentMethodRequestBody.builder()
                .paymentMethod(CommonConstants.CARD)
                .paymentCode("DM")
                .paymentMethodId("71")
                .card(com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails.Card.builder()
                        .cardToken("557347123456789811")
                        .cardIssueNumber("456")
                        .cardHolderName("John Smith")
                        .cardValidFromMonth("09")
                        .cardValidFromYear("2015")
                        .cardExpiryMonth("09")
                        .cardExpiryYear("2019")
                        .isDefault(isDefault)
                        .build())
                .build();
    }

    public static SavedPaymentMethodRequestBody aBasicAddCreditCardPaymentDetails(boolean isDefault) {
        return SavedPaymentMethodRequestBody.builder()
                .paymentMethod(CommonConstants.CARD)
                .paymentCode("VI")
                .paymentMethodId("71")
                .card(com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails.Card.builder()
                        .cardToken("4212345678901237")
                        .cardIssueNumber("737")
                        .cardHolderName("Testing card")
                        .cardValidFromMonth("09")
                        .cardValidFromYear("2015")
                        .cardExpiryMonth("08")
                        .cardExpiryYear("2018")
                        .isDefault(isDefault)
                        .build())
                .build();
    }

    public static SavedPaymentMethodRequestBody aBasicAddBankPaymentDetails(boolean isDefault) {
        return SavedPaymentMethodRequestBody.builder()
                .paymentMethod(CommonConstants.ELV)
                .paymentCode("EV")
                .paymentMethodId("71")
                .bankAccount(BankAccount.builder()
                        .accountHolderName("Hannah Muller")
                        .accountNumber("76653433")
                        .bankCity("Berlin")
                        .bankCode("123")
                        .bankCountryCode("DEU")
                        .bankName("Deutsche Bank")
                        .isDefault(isDefault)
                        .build())
                .build();
    }


    public static SavedPaymentMethodRequestBody aBasicAddExpiredCreditCardPaymentDetails(boolean isDefault) {
        return SavedPaymentMethodRequestBody.builder()
                .paymentMethod(CommonConstants.CARD)
                .paymentCode("VI")
                .paymentMethodId("71")
                .card(com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.managepaymentdetails.Card.builder()
                        .cardToken("4212345678901237")
                        .cardIssueNumber("737")
                        .cardHolderName("Testing card")
                        .cardValidFromMonth("09")
                        .cardValidFromYear("2015")
                        .cardExpiryMonth("08")
                        .cardExpiryYear("2016")
                        .isDefault(isDefault)
                        .build())
                .build();
    }
}
