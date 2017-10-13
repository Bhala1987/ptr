package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.InitiateCancelBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.InitiateCancelBookingResponse.RefundOrFee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assertions for the initiate cancel booking request.
 *
 * @author Joshua Curtis <j.curtis@reply.com>
 */
public class InitiateCancelBookingAssertion extends Assertion<InitiateCancelBookingAssertion, InitiateCancelBookingResponse> {
    private static final String REFUND = "refund";

    private static final String FEE = "fee";

    private static final String CREDIT_CARD_FEE_NAME = "CRCardFee";

    private BigDecimal creditCardFeePercent;

    private final InitiateCancelBookingResponse initiateCancelBookingResponse;

    public InitiateCancelBookingAssertion(
            InitiateCancelBookingResponse initiateCancelBookingResponse,
            FeesAndTaxesDao feesAndTaxesDao
    ) {
        // Get the credit card fee percentage from the database.
        List<FeesAndTaxesModel> crCardFees = feesAndTaxesDao.getFeesBasedOnType(
                "GBP",
                CREDIT_CARD_FEE_NAME,
                null
        );

        assert crCardFees.size() == 1;

        this.initiateCancelBookingResponse = initiateCancelBookingResponse;
        this.creditCardFeePercent = BigDecimal.valueOf(crCardFees.get(0).getFeeValue());
    }

    public void bookingWasCancelled() {
        assertThat(initiateCancelBookingResponse.getInitiateCancellationConfirmation()
                .getRefundsAndFees()
                .isEmpty()
        ).isFalse();
    }

    public void refundAmountHasBeenCalculatedAppropriately(GetBookingResponse.Booking booking) {
        // Calculate the total amount payed.

        BigDecimal bookingTotal = BigDecimal.valueOf(booking.getPayments().stream()
                .mapToDouble(payment -> payment.getAmount().getAmount())
                .sum()).setScale(2, BigDecimal.ROUND_HALF_UP);


        // Calculate the total refund amount to be repaid.
        BigDecimal refundTotal = BigDecimal.valueOf(initiateCancelBookingResponse.getInitiateCancellationConfirmation()
                .getRefundsAndFees()
                .stream()
                .filter(refundOrFee -> refundOrFee.getType().equals(REFUND))
                .mapToDouble(RefundOrFee::getAmount)
                .sum()).setScale(2, BigDecimal.ROUND_HALF_UP);

        // refund = (refund - N fee) so calculate the total fees.
        BigDecimal feeTotal = BigDecimal.valueOf(initiateCancelBookingResponse.getInitiateCancellationConfirmation()
                .getRefundsAndFees()
                .stream()
                .filter(refundOrFee -> refundOrFee.getType().equals(FEE))
                .mapToDouble(RefundOrFee::getAmount)
                .sum()).setScale(2, BigDecimal.ROUND_HALF_UP);

        // If we're using a credit card at all.
        boolean isCreditCard = booking.getPriceSummary()
                .getFees()
                .getItems()
                .stream()
                .anyMatch(
                        augmentedPriceItem -> augmentedPriceItem.getCode().equals(CREDIT_CARD_FEE_NAME)
                );

        // Add the credit card fee onto the fee total.
        if (isCreditCard) {
            feeTotal = feeTotal.add(new BigDecimal(booking.getPriceSummary().getSubtotalAmount())).multiply(creditCardFeePercent).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        // Assert that the bookingTotal equals the sum of refundTotal + feeTotal.
        assertThat(bookingTotal).isEqualTo(refundTotal.add(feeTotal));
    }

    public void primaryReasonCodeAndNameIsSet(String reasonCode, String reasonName) {
        assertThat(initiateCancelBookingResponse.getInitiateCancellationConfirmation()
                .getRefundsAndFees()
                .stream()
                .anyMatch(
                        refundOrFee ->
                                refundOrFee.getPrimaryReasonCode().equals(reasonCode) &&
                                        refundOrFee.getPrimaryReasonName().equals(reasonName)
                )
        ).isTrue();
    }
}
