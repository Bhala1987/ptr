package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by giuseppedimartino on 26/06/17.
 */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateBasicDetailsRequestBody implements IRequestBody {
    private Name name;
    private Integer age;
    private String ejPlusCardNumber;
    private String diallingCode;
    private String phoneNumber;
    private String email;
}