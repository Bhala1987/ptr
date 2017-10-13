package com.hybris.easyjet.fixture.hybris.invoke.response;

/**
 * Created by webbd on 10/21/2016.
 */

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LanguagesResponse extends Response {
    private List<Language> languages = new ArrayList<>();

    @Getter
    @Setter
    public static class Language {
        private String code;
        private List<LocalizedName> localizedNames = new ArrayList<>();
        private String nativeName;
        private Boolean isActive;
    }

}