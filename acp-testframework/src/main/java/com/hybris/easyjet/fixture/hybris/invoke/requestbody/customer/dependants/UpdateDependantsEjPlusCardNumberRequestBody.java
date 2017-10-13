package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateDependantsEjPlusCardNumberRequestBody implements IRequestBody {
    private String ejPlusCardNumber;
}