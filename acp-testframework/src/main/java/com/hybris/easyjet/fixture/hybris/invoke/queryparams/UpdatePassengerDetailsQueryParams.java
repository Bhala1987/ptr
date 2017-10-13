package com.hybris.easyjet.fixture.hybris.invoke.queryparams;
import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rajakm on 20/06/2017.
 */

@Builder
@Getter
public class UpdatePassengerDetailsQueryParams extends QueryParameters implements IQueryParams {
    private static final String OPERATION_TYPE = "operation-type";
    private static final String UPDATE = "UPDATE";
    private static final String DELETE = "DELETE";
    private String allRelatedFlights;
    private String operationTypeUpdate;
    private String operationTypeDelete;

    @Override
    public Map<String, String> getParameters() {

        Map<String, String> queryParams = new HashMap<>();
        if( isPopulated(allRelatedFlights)) {
            queryParams.put("all-related-flights", allRelatedFlights);
        }
        if(isPopulated(operationTypeUpdate)){
            queryParams.put(OPERATION_TYPE, UPDATE);

        }
        if(isPopulated(operationTypeDelete)){
            queryParams.put(OPERATION_TYPE, DELETE);

        }
        return queryParams;
    }
}
