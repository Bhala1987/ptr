package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g.dimartino on 09/02/17.
 */
@Getter
@Setter
public class Response implements IResponse {

    @JsonProperty("additionalInformation")
    private List<AdditionalInformation> additionalInformations = new ArrayList<>();

}