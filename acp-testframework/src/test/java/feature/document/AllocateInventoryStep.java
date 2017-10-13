package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddFlightRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.But;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.Optional;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;

/**
 * Created by christianmilia on 12/05/2017.
 */

@ContextConfiguration(classes = TestApplication.class)
public class AllocateInventoryStep {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private AddFlightRequestBodyFactory addFlightRequestBodyFactory;
    @Autowired
    private BasketHelper basketHelper;

    @Autowired
    FlightHelper flightHelper;

    @But("^The base price is changed with information code '(SVC_\\d+_\\d+)'$")
    public void theBasePriceIsChanged(final String errorCode) throws Throwable {

        String previousJSession = HybrisService.theJSessionCookie.get();
        BasketsResponse previousBasket = basketHelper.getBasketService().getResponse();

        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        FindFlightsResponse.Flight flight = flightsService.getResponse().getOutbound().getJourneys()
                .stream().filter(journey -> journey.getFlights().get(0).getFlightKey().equals(testData.getActualFlightKey()))
                .map(FindFlightsResponse.Journey::getFlights).flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The flight key " + testData.getActualFlightKey() + " was not returned from find flight"));

        HybrisService.theJSessionCookie.set("");
        basketHelper.getBasketService().setBasketsResponse(null);

        int quantity = 0;

        if (CollectionUtils.isNotEmpty(flight.getFareTypes())) {

            Optional<FindFlightsResponse.FareType> actualFare = flight.getFareTypes().stream().filter(fareType -> fareType.getFareTypeCode().equalsIgnoreCase(testData.getActualFareType())).findFirst();

            if (actualFare.isPresent() && actualFare.get().getFareClass() != null) {

                quantity = actualFare.get().getFareClass().getAvailableUnits();
            }
        }


        do {
            basketHelper.addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMixAndFaretypeAndJourney(flight,
                    quantity + " adult",
                    "ADAirport",
                    flightsService.getResponse().getCurrency(),
                    "Standard",
                    "single");
        }
        while (basketHelper.getBasketService().getResponse().getAdditionalInformations().stream().noneMatch(info -> info.getCode().equalsIgnoreCase(errorCode)));

        HybrisService.theJSessionCookie.set(previousJSession);
        basketHelper.getBasketService().setBasketsResponse(previousBasket);

    }
}
