package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.SSRDataModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.SSRDataResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 09/02/2017.
 */
public class SSRDataAssertions extends Assertion<SSRDataAssertions, SSRDataResponse> {

    public SSRDataAssertions(SSRDataResponse ssrDataResponse) {

        this.response = ssrDataResponse;
    }

    public SSRDataAssertions verifySSRDataAreReturned(List<SSRDataModel> ssrDataModelList) {

        int countItemExpected = ssrDataModelList.size();
        int countItemActual = (int) response.getSsrdata().stream().count();
        assertThat(countItemExpected).isEqualTo(countItemActual);

        for (SSRDataModel ssrData : ssrDataModelList) {
            assertThat(response.getSsrdata()
                    .stream()
                    .filter(t -> t.getCode().equals(ssrData.getCode()))
                    .findAny()
                    .isPresent()).isEqualTo(true);
        }
        return this;
    }

    public SSRDataAssertions eachSSRContainsTAndC() {

        List<SSRDataResponse.SSRData> ssrDataList = response.getSsrdata();
        for (SSRDataResponse.SSRData ssrData : ssrDataList) {
            if (ssrData.getCode().equals("WCHC")) {
                assertThat(ssrData.getIsTsandCsMandatory()).isTrue();
            } else {
                assertThat(ssrData.getIsTsandCsMandatory()).isFalse();
            }
        }
        return this;
    }
}
