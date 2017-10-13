package com.hybris.easyjet.database.hybris.models;

import lombok.*;

import java.math.BigDecimal;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
@Data
@EqualsAndHashCode
@Builder
@Getter
@Setter
public class SectorsModel {

    private String code;
    private String departureAirport;
    private String arrivalAirport;
    private Double distance;
    private Boolean apis;

    public SectorsModel(String code, String distance, String apis) {

        this.code = code;
        this.departureAirport = code.substring(0, 3);
        this.arrivalAirport = code.substring(3);
        try {
            this.distance = BigDecimal.valueOf(Double.valueOf(distance))
                    .setScale(2, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
        } catch (Exception e) {
            this.distance = null;
        }
        this.apis = Boolean.getBoolean(apis);
    }


    public SectorsModel(String code, String apis) {

        this.code = code;
        this.departureAirport = code.substring(0, 3);
        this.arrivalAirport = code.substring(3);
        this.apis = Boolean.getBoolean(apis);

    }
    public SectorsModel(String code, String distance, String apis, Double ds, Boolean Apis) {

        this.code = code;
        this.departureAirport = code.substring(0, 3);
        this.arrivalAirport = code.substring(3);
        this.apis = Boolean.getBoolean(apis);
    }
}
