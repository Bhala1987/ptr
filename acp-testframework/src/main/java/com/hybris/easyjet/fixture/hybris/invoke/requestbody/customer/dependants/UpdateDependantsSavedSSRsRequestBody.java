package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateDependantsSavedSSRsRequestBody implements IRequestBody {
    private SSR ssr;

    @Getter
    @Setter
    @Builder
    public static class SSR {
        private List<String> ssrs;
        private List<String> remarks;
    }
}