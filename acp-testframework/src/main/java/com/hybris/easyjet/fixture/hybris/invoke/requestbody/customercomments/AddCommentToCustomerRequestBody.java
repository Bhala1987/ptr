package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customercomments;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jamie on 03/07/2017.
 */
@Getter
@Setter
@Builder
public class AddCommentToCustomerRequestBody implements IRequestBody {

        private String comment;
}
