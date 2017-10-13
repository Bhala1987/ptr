package com.hybris.easyjet.database.hybris.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by marco on 27/02/17.
 */
@Data
@NoArgsConstructor
public class ProductModel {

    private String code;
    private String productType;
    private List<String> channels;

    public ProductModel(String code, String productType) {
        this.code = code;
        this.productType = productType;
    }

    public ProductModel(String code, String productType, List<String> channels) {
        this(code, productType);
        this.channels = channels;
    }

}
