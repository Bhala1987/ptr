package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.DeleteRecentSearchesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by albertowork on 7/7/17.
 */
public class DeleteRecentSearchesAssertion extends Assertion<DeleteRecentSearchesAssertion, DeleteRecentSearchesResponse> {

    public DeleteRecentSearchesAssertion(DeleteRecentSearchesResponse deleteRecentSearchesResponse) {
         this.response = deleteRecentSearchesResponse;
    }

    public void verifyInformationInAffectedData(final String errorCode) {
        final List<AdditionalInformation.AffectedData> affectedData =
                response.getAdditionalInformations().stream().filter(additionalInformation -> additionalInformation.getCode().equals(errorCode)).flatMap(additionalInformation -> additionalInformation.getAffectedData().stream()).collect(Collectors.toList());
        assertThat(affectedData.size()).isNotZero();
    }

}
