package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TravelDocumentTypesResponse extends Response {
    private List<TravelDocumentType> travelDocumentTypes = new ArrayList<>();

    @Getter
    @Setter
    public static class TravelDocumentType {
        private String code;
        private List<LocalizedName> localizedNames = new ArrayList<>();
    }

}