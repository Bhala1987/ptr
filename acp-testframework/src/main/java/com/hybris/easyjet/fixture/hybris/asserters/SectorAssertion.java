package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.SectorsModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.SectorResponse;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
public class SectorAssertion extends Assertion<SectorAssertion, SectorResponse> {

    public SectorAssertion(SectorResponse sectorResponse) {

        this.response = sectorResponse;
    }

    public SectorAssertion allActiveSectorsWereReturned(List<SectorsModel> activeSectors) {

        List<SectorsModel> returnedSectors = new ArrayList<>();
        for (SectorResponse.Sector sector : response.getSectors()) {
            String distance = null;
            try {
                distance = String.valueOf(sector.getDistance());
            } catch (Exception ignore) {
            }
            returnedSectors.add(
                    new SectorsModel(
                            sector.getCode(),
                            distance,
                            String.valueOf(sector.getIsAPIS() )
                    )
            );
        }
        assertThat(returnedSectors.containsAll(activeSectors));
        assertThat(activeSectors.containsAll(returnedSectors));
        return this;
    }

    public SectorAssertion thereWereSectorsReturned() {

        assertThat(response.getSectors().size()).isGreaterThan(0);
        return this;
    }
}
