package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdditionalSeatReasonsResponse extends Response {

    @Getter
    @Setter
    private List<AdditionalSeatReasons> additionalSeatReasons = new ArrayList<>();

    @Getter
    @Setter
    public static class AdditionalSeatReasons {
        private String code;
        private List<LocalizedName> localizedDescriptions = new ArrayList<>();
    }
}
