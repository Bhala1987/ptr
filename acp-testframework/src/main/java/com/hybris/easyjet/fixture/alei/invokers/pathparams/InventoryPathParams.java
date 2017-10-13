package com.hybris.easyjet.fixture.alei.invokers.pathparams;

import com.hybris.easyjet.fixture.IPathParameters;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InventoryPathParams extends PathParameters implements IPathParameters {

    private String operation;

    @Override
    public String get() {
        if (!isPopulated(operation))
            throw new IllegalArgumentException("You must specify an operation for this service.");

        return operation;

    }

}
