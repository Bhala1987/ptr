package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g.dimartino on 22/04/17.
 */
@Getter
@Setter
public class SpecialRequest {
    private List<Ssr> ssrs = new ArrayList<>();
    private List<Remark> remarks = new ArrayList<>();

    @Getter
    @Setter
    public static class Ssr {
        private String code;
        private String ssrDescription;
        private Boolean isTandCsAccepted;
    }

    @Getter
    @Setter
    public static class Remark {
        private String code;
        private String name;
    }
}
