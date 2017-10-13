package com.hybris.easyjet.fixture.hybris.invoke.response.basket;

import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 08/05/2017.
 */
@Getter
@Setter
public class RecalculatePricesResponse extends BasketConfirmationResponse {
    public Basket basket;
}