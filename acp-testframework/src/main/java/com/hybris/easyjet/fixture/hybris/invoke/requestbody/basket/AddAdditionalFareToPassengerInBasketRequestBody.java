package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 12/04/2017.
 */
@Builder
@Getter
@Setter
public class AddAdditionalFareToPassengerInBasketRequestBody implements IRequestBody {
    private Integer numberOfFares;
     private String additionalSeatReason;
}
