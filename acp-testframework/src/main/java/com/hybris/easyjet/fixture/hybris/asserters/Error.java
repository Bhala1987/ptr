package com.hybris.easyjet.fixture.hybris.asserters;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by g.dimartino on 14/02/17.
 */
@Builder
@Getter
public class Error {

    private String code;
    private String message;
}
