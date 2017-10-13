package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SavedSSRsRequestBody implements IRequestBody {
    private List<SSRs> ssrs;

    @Getter
    @Setter
    @Builder
    public static class SSRs {
        private String code;
        private Boolean isTandCsAccepted;
    }

}