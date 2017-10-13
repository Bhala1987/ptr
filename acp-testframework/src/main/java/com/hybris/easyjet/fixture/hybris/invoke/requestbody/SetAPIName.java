package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by marco on 15/02/17.
 */

@Getter
@Setter
@Builder
public class SetAPIName {
    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
}
