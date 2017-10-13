package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedValue;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PassengerTitlesResponse extends Response {
    private List<PassengerTitle> passengerTitles = new ArrayList<>();

    @Getter
    @Setter
    public static class PassengerTitle {
        private String code;
        private List<LocalizedValue> localizedNames = new ArrayList<>();
        private List<String> applicablePassengerTypes = new ArrayList<>();
        private String name;
    }

}