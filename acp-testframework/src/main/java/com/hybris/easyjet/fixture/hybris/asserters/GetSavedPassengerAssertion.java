package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger.GetSavedPassengerResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 16/02/2017.
 */
public class GetSavedPassengerAssertion extends Assertion<GetSavedPassengerAssertion, GetSavedPassengerResponse> {

    public GetSavedPassengerAssertion(GetSavedPassengerResponse getSavedPassengerResponse) {
        this.response = getSavedPassengerResponse;
    }

    public GetSavedPassengerAssertion allTheDetailsReturnedAreCorrect(String dateOfBirth, String documentExpiryDate, String documentNumber, String documentType, String gender, String nationality, String countryOfIssue, String fullName) {
        assertThat(response.getSavedPassengers().get(0).getIdentityDocuments().get(0).getDateOfBirth().equalsIgnoreCase(dateOfBirth)).isTrue();
        assertThat(response.getSavedPassengers().get(0).getIdentityDocuments().get(0).getDocumentExpiryDate().equalsIgnoreCase(documentExpiryDate)).isTrue();
        assertThat(response.getSavedPassengers().get(0).getIdentityDocuments().get(0).getDocumentType().equalsIgnoreCase(documentType)).isTrue();
        assertThat(response.getSavedPassengers().get(0).getIdentityDocuments().get(0).getGender().equalsIgnoreCase(gender)).isTrue();
        assertThat(response.getSavedPassengers().get(0).getIdentityDocuments().get(0).getNationality().equalsIgnoreCase(nationality)).isTrue();
        assertThat(response.getSavedPassengers().get(0).getIdentityDocuments().get(0).getCountryOfIssue().equalsIgnoreCase(countryOfIssue)).isTrue();
        assertThat(response.getSavedPassengers().get(0).getIdentityDocuments().get(0).getName().getFullName().equalsIgnoreCase(fullName)).isTrue();
        assertThat(response.getSavedPassengers().get(0).getIdentityDocuments().get(0).getDocumentNumber().equalsIgnoreCase(documentNumber)).isTrue();
        return this;
    }

    public GetSavedPassengerAssertion allTheDetailsReturnedAreCorrect() {
        assertThat(response.getSavedPassengers().get(0).getIdentityDocuments().size()).isEqualTo(0);
        return this;
    }

    public GetSavedPassengerAssertion savedPassengerDetailsAreReturned() {
        assertThat(response.getSavedPassengers().isEmpty()).isFalse();
        return this;
    }

}