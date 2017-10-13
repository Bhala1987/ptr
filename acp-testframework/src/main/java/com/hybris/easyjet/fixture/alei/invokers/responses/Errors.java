package com.hybris.easyjet.fixture.alei.invokers.responses;

import com.hybris.easyjet.fixture.IErrors;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppedimartino on 25/04/17.
 */
@Getter
@Setter
public class Errors extends Response implements IErrors {

    public List<Error> errors = new ArrayList<>();

    @Getter
    @Setter
    public static class Error {
        public String code;
        public String message;
        public List<Data> affectedData = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Data {
        public String dataName;
        public String dataValue;
    }
}
