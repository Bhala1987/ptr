package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppedimartino on 27/03/17.
 */

@Builder
@Getter @Setter
public class UpdateCurrencyRequestBody implements IRequestBody{

    private String newCurrencyCode;
}
