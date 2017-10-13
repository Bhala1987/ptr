package com.hybris.easyjet.fixture.hybris.invoke.requests;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.CurrencyConversionRequestBody;

import static com.hybris.easyjet.config.constants.HttpMethods.POST;

/**
 * Created by vijayapalkayyam on 15/05/2017.
 */
public class CurrencyConversionRequest extends HybrisRequest implements IRequest {

    public CurrencyConversionRequest(HybrisHeaders headers, CurrencyConversionRequestBody currencyConversionRequestBody) {
        super(headers, POST, null, null, currencyConversionRequestBody);
    }
}