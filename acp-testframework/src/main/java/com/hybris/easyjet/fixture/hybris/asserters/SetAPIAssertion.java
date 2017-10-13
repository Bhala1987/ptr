package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.SetAPIResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by marco on 15/02/17.
 */
public class SetAPIAssertion extends Assertion<SetAPIAssertion, SetAPIResponse> {

    public SetAPIAssertion(SetAPIResponse setAPIResponse) {

        this.response = setAPIResponse;
    }

    public SetAPIAssertion assertSuccess() {

        assertThat(response.getSuccess()).isTrue();
        return this;
    }

    public SetAPIAssertion assertIsAdded(int before, int after) {

        assertThat(before).isLessThan(after);
        return this;
    }

    public SetAPIAssertion assertIsAdded(int before, int after, int difference) {

        assertThat(before + difference).isEqualTo(after);
        return this;
    }
}
