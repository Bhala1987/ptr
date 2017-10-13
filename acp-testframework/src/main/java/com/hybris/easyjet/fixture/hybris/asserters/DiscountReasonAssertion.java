package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.DiscountReasonResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 31/03/2017.
 */
public class DiscountReasonAssertion extends Assertion<DiscountReasonAssertion, DiscountReasonResponse> {
    /**
     * @param discountReasonResponse
     */
    public DiscountReasonAssertion(DiscountReasonResponse discountReasonResponse) {
        this.response = discountReasonResponse;
    }

    public void theResultIsTheExpected(List<String> expectedResult) {
        assertThat(expectedResult.size()).isEqualTo(response.getDiscountReasons().size());
        expectedResult.forEach(item -> {
            assertThat(response.getDiscountReasons().stream().map(f -> f.getDiscountCode()).collect(Collectors.toList()).contains(item)).isEqualTo(true);
        });
    }

    public void theResultNotContain(List<String> expectedResult) {
        assertThat(response.getDiscountReasons().stream().map(f -> f.getDiscountCode()).collect(Collectors.toList()).containsAll(expectedResult)).isEqualTo(false);
    }
}
