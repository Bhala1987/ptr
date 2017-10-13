package com.hybris.easyjet.fixture.hybris.helpers;

import org.apache.commons.lang.WordUtils;

/**
 * Created by dwebb on 11/11/2016.
 */
public class CaseConverter {

    public CaseConverter() {

    }

    public String convert(String toConvert, String caseFormat) {

        switch (caseFormat.toLowerCase()) {
            case "camel":
                return toCamel(toConvert);
            case "mixed":
                return toMixed(toConvert);
            case "upper":
                return toUpper(toConvert);
            case "lower":
                return toLower(toConvert);
            default:
                break;
        }

        return toConvert;
    }

    private String toCamel(String toConvert) {

        return WordUtils.capitalizeFully(toConvert);
    }

    private String toUpper(String toConvert) {

        return toConvert.toUpperCase();
    }

    private String toLower(String toConvert) {

        return toConvert.toLowerCase();
    }

    private String toMixed(String toConvert) {

        return WordUtils.capitalizeFully(toConvert);
    }

}
