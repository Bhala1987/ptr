package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.managebooking.PaymentBalanceRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.managebooking.PaymentBalanceResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 27/07/2017.
 */
public class PaymentBalanceAssertion extends Assertion<PaymentBalanceAssertion, PaymentBalanceResponse> {

    public PaymentBalanceAssertion(PaymentBalanceResponse paymentBalanceResponse) { this.response = paymentBalanceResponse; }

    public PaymentBalanceAssertion verifyAdminFeeBasedOnPaymentMethod(PaymentBalanceRequestBody paymentBalanceRequestBody) {
        List<PaymentBalanceRequestBody.PaymentMethod> paymentMethods = paymentBalanceRequestBody.getPaymentMethods();
        for(PaymentBalanceRequestBody.PaymentMethod paymentMethod: paymentMethods) {
            String paymentCode = paymentMethod.getPaymentCode();
            if(isCredit(paymentCode)) {
                BigDecimal amountRequested = BigDecimal.valueOf(paymentMethod.getPaymentAmount()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal expectedAmountFee = amountRequested.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal actualAmountFee = BigDecimal.valueOf(response.getProposedPayments().getPaymentMethods().stream().filter(pm -> pm.getPaymentCode().equalsIgnoreCase(paymentMethod.getPaymentCode())).findFirst().orElseThrow(() -> new IllegalArgumentException("No payment method with desired code "+ paymentCode)).getFeeAmount());
                assertThat(actualAmountFee.stripTrailingZeros())
                        .withFailMessage("The actual value for fee's amount " + actualAmountFee + " is not the expected " + expectedAmountFee)
                        .isEqualTo(expectedAmountFee.stripTrailingZeros());
            }
        }

        return this;
    }

    public PaymentBalanceAssertion verifyOutstandingBalanceBasedOnPaymentMethod(Basket basket) {
        BigDecimal paymentAmount = response.getProposedPayments().getPaymentMethods().stream().map(i -> new BigDecimal(i.getPaymentAmount())).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal basketTotal = BigDecimal.valueOf(basket.getTotalAmountWithDebitCard());
        BigDecimal expectedRemainingBalanceDebit = basketTotal.subtract(paymentAmount).setScale(2, RoundingMode.HALF_UP);

        //BigDecimal expectedRemainingBalanceCredit = calculateRemainingCredit(basketTotal);

        BigDecimal expectedRemainingBalanceCredit = expectedRemainingBalanceDebit.add((expectedRemainingBalanceDebit.multiply(BigDecimal.valueOf(0.05))).setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal actualRemainingDebitBalance = new BigDecimal(response.getRemainingBalance().getWithDebitCard()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualRemainingCreditBalance = new BigDecimal(response.getRemainingBalance().getWithCreditCard()).setScale(2, RoundingMode.HALF_UP);

        assertThat(actualRemainingCreditBalance.stripTrailingZeros())
                .withFailMessage("The actual value for remaining credit balance " + actualRemainingCreditBalance + " is not the expected " + expectedRemainingBalanceCredit)
                .isEqualTo(expectedRemainingBalanceCredit.stripTrailingZeros());

        assertThat(actualRemainingDebitBalance.stripTrailingZeros())
                .withFailMessage("The actual value for remaining debit balance " + actualRemainingDebitBalance + " is not the expected " + expectedRemainingBalanceDebit)
                .isEqualTo(expectedRemainingBalanceDebit.stripTrailingZeros());

        return this;
    }

    private BigDecimal calculateRemainingCredit(BigDecimal basketTotalDebit) {
        BigDecimal totAmountDebit = response.getProposedPayments().getPaymentMethods().stream().filter(j -> Objects.isNull(j.getFeeAmount())).map(i -> new BigDecimal(i.getPaymentAmount())).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tempDebit = basketTotalDebit.subtract(totAmountDebit).setScale(2, RoundingMode.HALF_UP);
        tempDebit = tempDebit.add((tempDebit.multiply(BigDecimal.valueOf(0.05))).setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totAmountCredit = response.getProposedPayments().getPaymentMethods().stream().filter(j -> Objects.nonNull(j.getFeeAmount())).map(i -> new BigDecimal(i.getPaymentAmount())).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totAmountCreditFee = response.getProposedPayments().getPaymentMethods().stream().filter(j -> Objects.nonNull(j.getFeeAmount())).map(i -> new BigDecimal(i.getFeeAmount())).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tempCredit = totAmountCredit.add(totAmountCreditFee).setScale(2, RoundingMode.HALF_UP);

        return tempDebit.subtract(tempCredit).setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean isCredit(String cardType) {
        switch (cardType) {
            case "DM":
            case "DL":
            case "SW":
            case "CB":
                return false;
            case "VI":
            case "MC":
            case "AX":
            case "DC":
            case "TP":
                return true;
            default:
                return false;
        }
    }

}
