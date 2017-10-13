package com.hybris.easyjet.fixture.hybris.invoke.response.basket;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasketsResponse extends Response {
    private Basket basket;
}