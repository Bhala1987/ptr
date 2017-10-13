package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.significantothers;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by adevanna on 14/03/17.
 */

@Builder
@Getter
@Setter
public class SignificantOtherSavedSSRsRequestBody implements IRequestBody {
    List<SignificantOtherSSRsRequestBody> ssrs;
}
