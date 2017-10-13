package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.AddSportEquipmentToBasketAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;

/**
 * Created by giuseppecioce on 01/03/2017.
 */
public class AddSportToBasketService extends HybrisService implements IService {
    private BasketConfirmationResponse addSportEquipmentResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public AddSportToBasketService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return addSportEquipmentResponse;
    }

    @Override
    public AddSportEquipmentToBasketAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new AddSportEquipmentToBasketAssertion(addSportEquipmentResponse);
    }

    @Override
    protected void mapResponse() {
        addSportEquipmentResponse = restResponse.as(BasketConfirmationResponse.class);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(addSportEquipmentResponse.getOperationConfirmation());
    }

    public AddSportEquipmentToBasketAssertion assertThatErrorsOverride() {
        assertThatServiceCallWasNotSuccessful();
        return new AddSportEquipmentToBasketAssertion(addSportEquipmentResponse);
    }
}
