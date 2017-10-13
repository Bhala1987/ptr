package com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessagecreation;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentEventMessageRequestBody implements IRequestBody {

    private String commentCode;

}
