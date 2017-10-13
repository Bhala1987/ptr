package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by adevanna on 14/03/17.
 */


@Builder
@Getter
@Setter
public class SignificantOtherSSRsRequestBody implements IRequestBody {

    private String code;
    private Boolean isTandCsAccepted;

    public void setIsTandCsAccepted(Boolean isTandCsAccepted) {
        this.isTandCsAccepted = isTandCsAccepted;
    }
}
