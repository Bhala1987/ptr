package com.hybris.easyjet.fixture.hybris.invoke.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PreferencesResponse extends Response {
    private PreferencesReferenceData preferencesReferenceData;

    @Getter
    @Setter
    public static class PreferencesReferenceData {
        private List<PreferencesReferenceOption> marketingCommunicationOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> contactMethodOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> contactTypeOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> frequencyOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> tripTypeOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> travellingWhenOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> travellingWithOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> travellingSeasonOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> seatingPreferenceOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> holdBagWeightOptions = new ArrayList<>();
        private List<PreferencesReferenceOption> keyDatesOptions = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class PreferencesReferenceOption {
        private String code;
        private List<PreferencesResponse.LocalizedValue> localizedNames = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class LocalizedValue {
        private String name;
        private String locale;
    }

}