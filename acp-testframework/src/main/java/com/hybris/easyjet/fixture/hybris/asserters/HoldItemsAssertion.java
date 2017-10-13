package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.invoke.response.HoldItemsResponse;
import org.assertj.core.groups.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppedimartino on 31/01/17.
 */
public class HoldItemsAssertion extends Assertion<HoldItemsAssertion, HoldItemsResponse> {

    public HoldItemsAssertion(HoldItemsResponse holdItemsResponse) {

        this.response = holdItemsResponse;
    }

    public HoldItemsAssertion returnedListIsCorrect(Map<String, List<HashMap<String, Double>>> expectedList) {
        if(testData.getData(SerenityFacade.DataKeys.CHANNEL).toString().equalsIgnoreCase(CommonConstants.PUBLIC_API_B2B_CHANNEL) || testData.getData(SerenityFacade.DataKeys.CHANNEL).toString().equalsIgnoreCase(CommonConstants.PUBLIC_API_MOBILE_CHANNEL))
        {
                expectedList.remove("10kgbag");
        }

        assertThat(response.getHoldItems().size()).isEqualTo(expectedList.size());

        response.getHoldItems().forEach(
                item -> {
                    assertThat(expectedList).containsKey(item.getProductCode());
                    List<HashMap<String, Double>> priceList = expectedList.get(item.getProductCode());
                    for (HashMap<String, Double> price : priceList) {
                        assertThat(item.getPrices()).extracting("basePrice", "qtyFrom").contains(Tuple.tuple(
                                price.get("price"),
                                price.get("qtyFrom").intValue()
                        ));
                    }
                }
        );

        return this;
    }
}
