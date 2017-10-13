package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = TestApplication.class)
/**
 * Created by daniel on 28/11/2016.
 * assertion wrapper for booking confirmation response object, provides reusable assertions to all tests
 */
public class BookingConfirmationAssertion extends Assertion<BookingConfirmationAssertion, BookingConfirmationResponse> {

    public BookingConfirmationAssertion(BookingConfirmationResponse bookingConfirmationResponse) {
        this.response = bookingConfirmationResponse;
    }

    public BookingConfirmationAssertion generatedBookingReference() {
        assertThat(response.getConfirmation().getBookingReference()).isNotNull();
        return this;
    }

    public BookingConfirmationAssertion gotAValidResponse() {
        pollingLoop().untilAsserted(() -> assertThat(response.getConfirmation().getHref()).isNotNull());
        return this;
    }

    public BookingConfirmationAssertion bookingStatusAs(BOOKING_STATUS bookingStatus) {
        assertThat(response.getConfirmation().getBookingStatus()).contains(bookingStatus.toString());
        return this;
    }

    public BookingConfirmationAssertion bookingHistoryCreated(int expectedHistoryItems, int actualHistoryItems) {
        assertThat(expectedHistoryItems >= actualHistoryItems).isTrue().withFailMessage("Recommit booking history is not created correctly Actual: " + actualHistoryItems + " Expected History items : " + expectedHistoryItems);
        return this;
    }
    public BookingConfirmationAssertion bookingOrderCount(int actualHistoryItemss, int expectedHistoryItems) {
        assertThat(actualHistoryItemss >= expectedHistoryItems).isTrue().withFailMessage("Recommit booking Order Count is not created correctly Actual: " + expectedHistoryItems + " Expected Order Count : " + actualHistoryItemss);
        return this;
    }

    public enum BOOKING_STATUS {COMPLETED, CONSIGNMENT_CREATED}

}


