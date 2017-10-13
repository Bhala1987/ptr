package com.hybris.easyjet.fixture.hybris.invoke.requests.booking;

import com.hybris.easyjet.fixture.IPathParameters;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.HybrisRequest;

import static com.hybris.easyjet.config.constants.HttpMethods.DELETE;

/**
 * @author Joshua Curtis <j.curtis@reply.com>
 */
public class DeleteCommentOnBookingRequest extends HybrisRequest implements IRequest {
    public DeleteCommentOnBookingRequest(HybrisHeaders headers, IPathParameters pathParams) {
        super(headers, DELETE, pathParams, null, null);
    }
}
