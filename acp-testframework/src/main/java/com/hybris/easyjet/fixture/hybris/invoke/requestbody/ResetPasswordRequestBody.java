package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
@Builder
@Getter
@Setter
public class ResetPasswordRequestBody implements IRequestBody {
    private String email;
}
