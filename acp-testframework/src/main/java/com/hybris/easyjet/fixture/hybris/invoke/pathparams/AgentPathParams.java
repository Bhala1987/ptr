package com.hybris.easyjet.fixture.hybris.invoke.pathparams;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class AgentPathParams extends PathParameters {
    private String agentId;


    @Override
    public String get() {
        if (!isPopulated(agentId)) {
            throw new IllegalArgumentException("You must specify an agentId for this service.");
        }

        return agentId + "/logout-request";
    }

}
