package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by dwebb on 1/11/2017.
 */
@Data
public class ChannelPropertiesModel {

    private String code;
    private String p_propertyname;
    private String p_propertyvalue;

    public ChannelPropertiesModel(String code, String p_propertyname, String p_propertyvalue) {

        this.code = code;
        this.p_propertyname = p_propertyname;
        this.p_propertyvalue = p_propertyvalue;
    }

}
