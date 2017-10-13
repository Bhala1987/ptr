package com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractConfirmation;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppedimartino on 30/03/17.
 */
public class BasketConfirmationResponse extends AbstractConfirmation<BasketConfirmationResponse.OperationConfirmation> {

    @Getter
    @Setter
    public static class OperationConfirmation extends AbstractConfirmation.OperationConfirmation {
        private String basketCode;

        @JsonGetter("basketId")
        public String getBasketId() {
            return basketCode;
        }

        @JsonSetter("basketId")
        public void setBasketId(String operationConfirmation) {
            this.basketCode = operationConfirmation;
        }
    }



}