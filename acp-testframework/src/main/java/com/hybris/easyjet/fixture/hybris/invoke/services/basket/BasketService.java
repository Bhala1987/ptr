package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.WaitHelper;
import com.hybris.easyjet.fixture.hybris.asserters.AmendableBasketAssertion;
import com.hybris.easyjet.fixture.hybris.asserters.BasketsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.jayway.jsonpath.JsonPath;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_FLIGHTS;

/**
 * Created by daniel on 26/11/2016.
 */
public class BasketService extends HybrisService implements IService {

    private WaitHelper waithelper;
    private BasketsResponse basketsResponse;

    private SerenityFacade testData = SerenityFacade.getTestDataFromSpring();

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public BasketService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public void invoke() {
        super.invoke();

        if (Objects.nonNull(request.getRequestBody()) && Objects.nonNull(basketsResponse)) {
            testData.setData(BASKET_FLIGHTS, getAllocatedTestFlights(request));
        }
    }

    @Override
    public BasketsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return basketsResponse;
    }

    @Override
    public BasketsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new BasketsAssertion(basketsResponse);
    }

    public AmendableBasketAssertion assertThatForAmandableBasket() {
        assertThatServiceCallWasSuccessful();
        return new AmendableBasketAssertion(basketsResponse);
    }

    @Override
    protected void mapResponse() {
        basketsResponse = restResponse.as(BasketsResponse.class);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(basketsResponse.getBasket());
    }

    /**
     * Gets all flights that were allocated in the test.
     *
     * @param request {@link IRequest} The request.
     * @return The list of flights.
     */
    private List<HashMap<String, String>> getAllocatedTestFlights(IRequest request) {
        Stream<Basket.Flights> stream = basketsResponse.getBasket().getOutbounds().stream();
        if (basketsResponse.getBasket().getInbounds() != null) {
            stream = Stream.concat(stream, basketsResponse.getBasket().getInbounds().stream());
        }

        return
                stream
                        .flatMap(flights -> flights.getFlights().stream())
                        .map(
                                flight -> new HashMap<String, String>() {{
                                    try {
                                        String jsonRequest = new ObjectMapper().writeValueAsString(
                                                request.getRequestBody()
                                        );

                                        put("basketId", basketsResponse.getBasket().getCode());
                                        put("flightKey", flight.getFlightKey());
                                        put("fareType", JsonPath.parse(jsonRequest).read("$.fareType"));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException("Unable to parse request JSON.", e);
                                    }
                                }}
                        ).distinct().collect(Collectors.toList());
    }

    public void setBasketsResponse(final BasketsResponse basketsResponse) {
        this.basketsResponse = basketsResponse;
    }

    public int getStatusCode() {
        return restResponse.getStatusCode();
    }
}
