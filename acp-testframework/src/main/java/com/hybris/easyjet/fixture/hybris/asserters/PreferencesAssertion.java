package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.PreferencesResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by PTR Scaffolder.
 * assertion wrapper for preferences response object, provides reusable assertions to all tests
 */
public class PreferencesAssertion extends Assertion<PreferencesAssertion, PreferencesResponse> {

    public PreferencesAssertion(PreferencesResponse preferencesResponse) {

        this.response = preferencesResponse;

    }

    public PreferencesAssertion theExampleAssertion() {

        return this;
    }

   /* public PreferencesAssertion theseContactMethodOptionsAllReturned() {
        assertThat(response.getPreferencesReferenceData().getContactMethodOptions()).size().isGreaterThan(0);
        return this;
    }*/

    public PreferencesAssertion theseContactMethodOptionsAllReturned(List<String> contactMethodOptions) {

        for (int i = 0; i < contactMethodOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getContactMethodOptions()).extracting("code")
                    .contains(contactMethodOptions.get(i));
        }
        return this;
    }

    public PreferencesAssertion theseContactTypeOptionsAllReturned(List<String> contactTypeOptions) {

        for (int i = 0; i < contactTypeOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getContactTypeOptions()).extracting("code")
                    .contains(contactTypeOptions.get(i));
        }
        return this;
    }

    public PreferencesAssertion theseFrequencyOptionsAllReturned(List<String> frequencyOptions) {

        for (int i = 0; i < frequencyOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getFrequencyOptions()).extracting("code")
                    .contains(frequencyOptions.get(i));
        }
        return this;
    }

    public PreferencesAssertion theseBagWeightOptionsAllReturned(List<String> bagWeightOptions) {

        for (int i = 0; i < bagWeightOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getHoldBagWeightOptions()).extracting("code")
                    .contains(bagWeightOptions.get(i));
        }
        return this;
    }

    public PreferencesAssertion theseMarketingCommunicationOptionsAllReturned(List<String> marketingCommOptions) {

        for (int i = 0; i < marketingCommOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getMarketingCommunicationOptions()).extracting("code")
                    .contains(marketingCommOptions.get(i));
        }
        return this;
    }

    public PreferencesAssertion theseSeatingPreferenceOptionsAllReturned(List<String> seatingPreferenceOptions) {

        for (int i = 0; i < seatingPreferenceOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getSeatingPreferenceOptions()).extracting("code")
                    .contains(seatingPreferenceOptions.get(i));
        }
        return this;
    }

    public PreferencesAssertion theseTravellingSeasonOptionsAllReturned(List<String> travellingSeasonOptions) {

        for (int i = 0; i < travellingSeasonOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getTravellingSeasonOptions()).extracting("code")
                    .contains(travellingSeasonOptions.get(i));
        }
        return this;
    }

    public PreferencesAssertion theseTravellingWhenOptionsAllReturned(List<String> travellingWhenOptions) {

        for (int i = 0; i < travellingWhenOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getTravellingWhenOptions()).extracting("code")
                    .contains(travellingWhenOptions.get(i));
        }
        return this;
    }

    public PreferencesAssertion theseTravellingWithOptionsAllReturned(List<String> travellingWithOptions) {

        for (int i = 0; i < travellingWithOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getTravellingWithOptions()).extracting("code")
                    .contains(travellingWithOptions.get(i));
        }
        return this;
    }

    public PreferencesAssertion theseTripTypeOptionsAllReturned(List<String> tripTypeOptions) {

        for (int i = 0; i < tripTypeOptions.size() - 1; i++) {
            assertThat(response.getPreferencesReferenceData().getTripTypeOptions()).extracting("code")
                    .contains(tripTypeOptions.get(i));
        }
        return this;
    }

}