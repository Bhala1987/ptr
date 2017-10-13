package com.hybris.easyjet.database.hybris.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by antonellospiggia on 23/02/2017.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class FlightInterestModel {
    String flightKey;
    String fareType;
}
