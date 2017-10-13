package com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire;

import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.carhire.common.Result;
import lombok.Getter;
import lombok.Setter;
/**
 * Created by sudhir on 03/08/2017.
 */
@Getter
@Setter
public class CarHireResponse extends Response implements IResponse {
    public Result result;
}