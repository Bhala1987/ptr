package com.hybris.easyjet.database.hybris.helpers;

/**
 * Created by ptr-kvijayapal on 2/9/2017.
 */
public class AirportsForIndirectRoutes {

    private String pk;
    private String code;

    public AirportsForIndirectRoutes(String pk, String code) {

        this.pk = pk;
        this.code = code;
    }

    public String getPk() {

        return pk;
    }

    public String getCode() {

        return code;
    }
}
