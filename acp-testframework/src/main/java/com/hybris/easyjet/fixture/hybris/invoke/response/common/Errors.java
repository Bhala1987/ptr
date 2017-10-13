package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijayapalkayyam on 07/06/2017.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Errors extends Response {

    public List<Errors.AffectedData> affectedData = new ArrayList<>();
    public String code;
    public String message;
    public String href;

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AffectedData {
        public String dataName;
        public String dataValue;
        public String information;
    }
}
