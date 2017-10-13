package com.hybris.easyjet.fixture.hybris.invoke.response.dependants;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Profile;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by markphipps on 29/03/2017.
 */
@Getter
@Setter
public class DependantsResponse extends Response {
    private Profile dependant;
}