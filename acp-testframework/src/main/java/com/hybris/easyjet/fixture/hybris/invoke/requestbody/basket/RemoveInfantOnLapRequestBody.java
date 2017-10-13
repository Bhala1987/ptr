package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppedimartino on 20/06/17.
 */
@Builder
@Getter
@Setter
public class RemoveInfantOnLapRequestBody implements IRequestBody {
    private String infantOnLapPassengerCode;
}