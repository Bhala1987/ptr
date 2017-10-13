package com.hybris.easyjet.database.hybris.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by giuseppedimartino on 20/06/17.
 */
@Data
@AllArgsConstructor
public class PassengerStatus {
    private String apisStatus;
    private String ictsStatus;
    private String consignmentStatus;
}