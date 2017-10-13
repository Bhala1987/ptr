package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by marco on 20/04/17.
 */
@Data
public class PaymentModeModel {

    private String paymenttypes;

    public PaymentModeModel(String paymenttypes) {
        this.paymenttypes = paymenttypes;
    }

}
