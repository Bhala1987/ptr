package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by albertowork on 7/7/17.
 */
@Getter
@Setter
public class DeleteRecentSearchesResponse extends AbstractConfirmation<DeleteRecentSearchesResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private String customerId;
    }
}
