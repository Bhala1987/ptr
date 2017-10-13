package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.RecalculatePricesAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RecalculatePricesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.RecalculatePricesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation.AffectedData;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 08/05/2017.
 */
public class RecalculatePricesService extends HybrisService implements IService {

    private RecalculatePricesResponse recalculatePricesResponse;

    /**
     * a service comprises a request and an endpoint
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public RecalculatePricesService(RecalculatePricesRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public RecalculatePricesResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return recalculatePricesResponse;
    }

    @Override
    public RecalculatePricesAssertion assertThat() {
        return new RecalculatePricesAssertion(recalculatePricesResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {

        List<AffectedData> affectedData = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(recalculatePricesResponse.getAdditionalInformations()))
            affectedData = recalculatePricesResponse.getAdditionalInformations().stream()
                    .map(AdditionalInformation::getAffectedData).flatMap(Collection::stream)
                    .collect(Collectors.toList());

        if (affectedData.size() == 0)
            checkThatResponseBodyIsPopulated(recalculatePricesResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        recalculatePricesResponse = restResponse.as(RecalculatePricesResponse.class);
    }
}

