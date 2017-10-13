package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppedimartino on 19/06/17.
 */
@Getter
@Setter
public class SavedSSRs {
    private List<Ssr> ssrs = new ArrayList<>();

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Ssr {
        private String code;
        private Boolean isTandCsAccepted;
        private String description;
    }
}
