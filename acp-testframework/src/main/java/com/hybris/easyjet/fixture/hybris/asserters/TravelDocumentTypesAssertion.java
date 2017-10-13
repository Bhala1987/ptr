package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.TravelDocumentTypesModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.TravelDocumentTypesResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 08/02/2017.
 */
public class TravelDocumentTypesAssertion extends Assertion<TravelDocumentTypesAssertion, TravelDocumentTypesResponse> {

    public TravelDocumentTypesAssertion(TravelDocumentTypesResponse travelDocumentTypesResponse) {

        this.response = travelDocumentTypesResponse;
    }

    public TravelDocumentTypesAssertion verifyTravelDocumentTypesAreReturned(List<TravelDocumentTypesModel> travelDocumentTypesModels) {

        int countItemExpected = travelDocumentTypesModels.size();
        int countItemActual = (int) response.getTravelDocumentTypes().stream().count();
        assertThat(countItemExpected).isEqualTo(countItemActual);

        for (TravelDocumentTypesModel travelDocumentType : travelDocumentTypesModels) {

            assertThat(response.getTravelDocumentTypes()
                    .stream()
                    .filter(t -> t.getCode().equals(travelDocumentType.getCode()))
                    .findAny()
                    .isPresent()).isEqualTo(true);
        }
        return this;
    }
}
