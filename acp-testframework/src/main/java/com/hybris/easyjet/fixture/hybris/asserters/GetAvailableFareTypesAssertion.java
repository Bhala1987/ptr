package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.helpers.dto.bundletemplate.BundleTemplateDTO;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetAvailableFareTypesResponse;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by marco on 23/02/17.
 */
public class GetAvailableFareTypesAssertion extends Assertion<GetAvailableFareTypesAssertion, GetAvailableFareTypesResponse> {

    public GetAvailableFareTypesAssertion(GetAvailableFareTypesResponse getAvailableFareTypesResponse) {

        this.response = getAvailableFareTypesResponse;
    }

    public GetAvailableFareTypesAssertion assertNotNull() {

        assertThat(response).isNotNull();
        return this;
    }

    public GetAvailableFareTypesAssertion assertNotEmpty() {

        assertThat(response.getAvailableFareTypes()).isNotEmpty();
        return this;
    }

    public GetAvailableFareTypesAssertion assertHasDescriptions() {

        this.assertNotNull().assertNotEmpty();
        response.getAvailableFareTypes()
                .forEach(availableFareTypeData -> availableFareTypeData.getLocalisedFareTypeDetails()
                        .forEach(fareTypeDetailsData -> assertThat(fareTypeDetailsData.getDescription()).isNotEmpty()));
        return this;
    }

    public GetAvailableFareTypesAssertion assertHasOnlyExpectedGdsFareClass(String gdsFareClass) {

        assertThat(response.getAvailableFareTypes()
                .stream()
                .map(availableFareTypeData -> availableFareTypeData.getGdsFareClass())
                .distinct()
                .collect(Collectors.toList())).containsOnly(gdsFareClass);
        return this;
    }

    public GetAvailableFareTypesAssertion assertHasOnlyExpectedOptions(List<BundleTemplateDTO> availableBundles) {

        if (CollectionUtils.isEmpty(availableBundles)) {
            return this;
        }
        availableBundles.forEach(bundleTemplateDTO -> {
            if (CollectionUtils.isNotEmpty(bundleTemplateDTO.getProducts())) {
                assertThat(response.getAvailableFareTypes().stream()
                        .filter(availableFareTypeData -> bundleTemplateDTO.getId()
                                .equalsIgnoreCase(availableFareTypeData.getCode()))
                        .findFirst().get().getOptionsIncluded().size()).isEqualTo(bundleTemplateDTO.getProducts().size());
            }
        });
        return this;
    }

}
