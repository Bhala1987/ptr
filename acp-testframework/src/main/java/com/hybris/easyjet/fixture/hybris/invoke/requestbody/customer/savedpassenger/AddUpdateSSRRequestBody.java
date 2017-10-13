package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.SavedSSRs;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Created by giuseppecioce on 20/02/2017.
 */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddUpdateSSRRequestBody implements IRequestBody {
    private List<SavedSSRs.Ssr> ssrs;
}