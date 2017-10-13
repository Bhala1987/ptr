package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.IErrors;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Errors extends Response implements IErrors {
    private List<Error> errors = new ArrayList<>();

    @Getter
    @Setter
    public static class Error {
        private String code;
        private String message;
        private String href;
        private List<AffectedData> affectedData;
    }

    @Getter
    @Setter
    public static class AffectedData {
        public String dataName;
        public String dataValue;
        public String information;
    }

}