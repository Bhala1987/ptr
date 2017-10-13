package com.hybris.easyjet.fixture.hybris.asserters.models;

import lombok.Builder;
import lombok.Data;

/**
 * Created by daniel on 11/11/2016.
 */
@Data
@Builder
public class ExpectedLanguage {
    String code;
    String isoCode;
    boolean isActive;
}
