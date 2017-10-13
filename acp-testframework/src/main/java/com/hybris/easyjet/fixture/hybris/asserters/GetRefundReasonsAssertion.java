package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.GetRefundReasonsResponse;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppedimartino on 04/07/17.
 */
@NoArgsConstructor
public class GetRefundReasonsAssertion extends Assertion<GetRefundReasonsAssertion, GetRefundReasonsResponse> {

    public GetRefundReasonsAssertion(GetRefundReasonsResponse getRefundReasonsResponse) {
        this.response = getRefundReasonsResponse;
    }

    public void setResponse(GetRefundReasonsResponse getRefundReasonsResponse) {
        this.response = getRefundReasonsResponse;
    }

    @Step("Primary reason returned")
    public GetRefundReasonsAssertion primaryReasonsReturned() {
        assertThat(response.getPrimaryRefundReasons())
                .withFailMessage("No primary reason returned")
                .isNotEmpty();
        return this;
    }

    @Step("Secondary reasons contain allowed booking")
    public GetRefundReasonsAssertion secondaryReasonsContainAllowedBooking() {
        response.getPrimaryRefundReasons().stream()
                .map(GetRefundReasonsResponse.PrimaryRefundReasons::getSecondaryRefundReasons).flatMap(Collection::stream).forEach(
                secondaryRefundReasons -> assertThat(secondaryRefundReasons.getBookingType())
                        .withFailMessage("No booking type returned for the secondary reason")
                        .isNotNull()
        );

        return this;
    }
}
