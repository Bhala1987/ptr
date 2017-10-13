package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import lombok.NoArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppedimartino on 27/03/17.
 */
@NoArgsConstructor
public class ConvertBasketCurrencyAssertion extends Assertion<ConvertBasketCurrencyAssertion, BasketConfirmationResponse> {

    private BookingDao bookingDao = BookingDao.getTestDataFromSpring();

    public ConvertBasketCurrencyAssertion(BasketConfirmationResponse confirmationResponse) {
        this.response = confirmationResponse;
    }

    public ConvertBasketCurrencyAssertion currencyIsUpdated(String basketCode) {
        assertThat(response.getOperationConfirmation().getBasketCode()).isEqualTo(basketCode);
        return this;
    }

    public ConvertBasketCurrencyAssertion originalCurrencyIsStoredAgainstTheBooking(String bookingReference, String currency) {
        assertThat(bookingDao.getBookingOriginalCurrency(bookingReference))
                .withFailMessage("The original currency on the booking is wrong")
                .isEqualTo(currency);

        return this;
    }
}