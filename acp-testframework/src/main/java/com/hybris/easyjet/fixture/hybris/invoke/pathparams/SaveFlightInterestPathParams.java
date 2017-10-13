package com.hybris.easyjet.fixture.hybris.invoke.pathparams;

import com.hybris.easyjet.fixture.IPathParameters;
import lombok.Builder;


@Builder
public class SaveFlightInterestPathParams extends PathParameters implements IPathParameters {

    private String customerId;

    /**
     * @return the Path Parameters to update password
     */
    @Override
    public String get() {
        return "/" + customerId + "/flight-interests";
    }

}
