package com.hybris.easyjet.fixture.hybris.helpers.traveller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.SavedSSRs;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
//TODO it is a response or a request body?
public class SpecialRequest {
    private List<SavedSSRs.Ssr> ssrs = new ArrayList<>();
}