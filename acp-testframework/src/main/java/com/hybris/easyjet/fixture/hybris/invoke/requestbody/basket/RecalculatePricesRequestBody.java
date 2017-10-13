package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.BasketContent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 08/05/2017.
 */
@Builder
@Getter
@Setter
public class RecalculatePricesRequestBody implements IRequestBody {
    private String basketCode;
    private BasketContent basketContent;
}