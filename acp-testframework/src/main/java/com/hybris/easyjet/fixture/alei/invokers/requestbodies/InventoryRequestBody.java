package com.hybris.easyjet.fixture.alei.invokers.requestbodies;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Created by giuseppedimartino on 27/04/17.
 */
@AllArgsConstructor
abstract class InventoryRequestBody<T extends InventoryRequestBody.Fare> implements IRequestBody {
    public String uniqueIdentifier;
    public List<T> fares;

    abstract static class Fare {
    }

}
