package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.GetCustomerAPIsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.IdentityDocument;
import cucumber.api.DataTable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by vijayapalkayyam on 30/03/2017.
 */
public class GetCustomerApisAssertion extends Assertion<GetCustomerApisAssertion, GetCustomerAPIsResponse> {

    public GetCustomerApisAssertion(GetCustomerAPIsResponse getCustomerAPIsResponse) {

        this.response = getCustomerAPIsResponse;
    }

    public GetCustomerApisAssertion onlyOneDocumentExistsAndAllTheDetailsReturnedAreCorrect(String dateOfBirth, String documentExpiryDate, String documentType, String gender, String nationality, String countryOfIssue, String fullName, String documentNumber) {
        
        assertThat( Optional.ofNullable(response.getIdentityDocuments()).isPresent() ).isTrue();

        List<IdentityDocument> documents = response.getIdentityDocuments();
        assertThat(documents.size()).isEqualTo(1);
        for (IdentityDocument document : documents)
            if (document.getDocumentType().equalsIgnoreCase(documentType)) {
                assertThat(document.getDateOfBirth().equalsIgnoreCase(dateOfBirth)).isTrue();
                assertThat(document.getDocumentExpiryDate().equalsIgnoreCase(documentExpiryDate)).isTrue();
                assertThat(document.getDocumentType().equalsIgnoreCase(documentType)).isTrue();
                assertThat(document.getGender().equalsIgnoreCase(gender)).isTrue();
                assertThat(document.getNationality().equalsIgnoreCase(nationality)).isTrue();
                assertThat(document.getCountryOfIssue().equalsIgnoreCase(countryOfIssue)).isTrue();
                assertThat(document.getName().getFullName().equalsIgnoreCase(fullName)).isTrue();
                assertThat(document.getDocumentNumber().equalsIgnoreCase(documentNumber)).isTrue();
            }

        return this;
    }

    public GetCustomerApisAssertion noDocumentsPresent() {
        assertThat(response.getIdentityDocuments().size()).isEqualTo(0);
        return this;
    }

    public GetCustomerApisAssertion verifyExpectedAPIsAreReturned(DataTable dt) {
        List<IdentityDocument> documents = response.getIdentityDocuments();
        List<List<String>> data = dt.raw();
        for (int i = 1; i < data.size(); i++) {
            String expDocNumber = data.get(i).get(2);
            String expDocType = data.get(i).get(3);
            List<IdentityDocument> matchedDoc = getMatchedDocs(expDocNumber, expDocType, documents);
            assertThat(matchedDoc.size()).isEqualTo(1);
            assertThat(matchedDoc.get(0).getDateOfBirth()).isEqualTo(data.get(i).get(0));
            assertThat(matchedDoc.get(0).getDocumentExpiryDate()).isEqualTo(data.get(i).get(1));
            assertThat(matchedDoc.get(0).getDocumentNumber()).isEqualTo(data.get(i).get(2));
            assertThat(matchedDoc.get(0).getDocumentType()).isEqualTo(data.get(i).get(3));
            assertThat(matchedDoc.get(0).getGender()).isEqualTo(data.get(i).get(4));
            assertThat(matchedDoc.get(0).getNationality()).isEqualTo(data.get(i).get(5));
            assertThat(matchedDoc.get(0).getCountryOfIssue()).isEqualTo(data.get(i).get(6));
            assertThat(matchedDoc.get(0).getName().getFullName()).isEqualTo(data.get(i).get(7));
        }
        return this;
    }

    private List<IdentityDocument> getMatchedDocs(String expDocNumber, String expDocType, List<IdentityDocument> documents) {
        return documents
                .stream()
                .filter(document -> document.getDocumentNumber().equals(expDocNumber) && document.getDocumentType().equals(expDocType))
                .collect(Collectors.toList());
    }

    public GetCustomerApisAssertion verifyNewAPISHasBeenCreated(Integer oldAPISize, Integer newAPISize) {
        assertThat(oldAPISize)
                .withFailMessage("The number of the old size " + oldAPISize + " does not less then the new size " + newAPISize)
                .isLessThan(newAPISize);
        return this;
    }

    public GetCustomerApisAssertion verifyNotUpdateAPIS(Integer oldAPISize, Integer newAPISize) {
        assertThat(oldAPISize)
                .withFailMessage("The number of the old size " + oldAPISize + " does not equal then the new size " + newAPISize)
                .isEqualTo(newAPISize);
        return this;
    }
}




