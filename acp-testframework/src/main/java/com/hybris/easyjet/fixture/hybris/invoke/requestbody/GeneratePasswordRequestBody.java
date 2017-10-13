package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by robertadigiorgio on 13/02/2017.
 */

@Builder
@Getter
@Setter
public class GeneratePasswordRequestBody implements IRequestBody {

    private String agentId;

}