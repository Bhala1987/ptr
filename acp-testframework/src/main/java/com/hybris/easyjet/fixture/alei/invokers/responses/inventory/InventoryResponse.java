package com.hybris.easyjet.fixture.alei.invokers.responses.inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.alei.invokers.responses.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppedimartino on 25/04/17.
 */
@Getter
@Setter
public abstract class InventoryResponse<T extends InventoryResponse.Result> extends Response {

    @JsonProperty("result")
    public List<T> results = new ArrayList<>();

    @Getter
    @Setter
    public static abstract class Result {
        public String flightKey;
        public String fareType;
    }

}
