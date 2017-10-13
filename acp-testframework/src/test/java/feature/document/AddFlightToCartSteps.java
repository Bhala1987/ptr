package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddFlightRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.GET_FLIGHT_SERVICE;
import static com.hybris.easyjet.config.constants.CommonConstants.*;

/**
 * Created by giuseppedimartino on 28/03/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class AddFlightToCartSteps {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private FlightHelper flightHelper;

    private FlightsService flightsService;
    private FindFlightsResponse.Flight flight;

    public void iAddedItToTheBasket(String fareType, String journeyType) throws Throwable {
        testData.setActualFareType(fareType);
        basketHelper.addFlightsToBasket(fareType, journeyType);
        testData.setData(BASKET_ID, testData.getBasketId());
    }

    @Given("^I added it to the basket with (.*) fare as (.*) journey$")
    public void iAddItToTheBasket(String fareType, String journeyType) throws Throwable {
        fareType = fareType.replace("'","");
        journeyType = journeyType.replace("'","");
        testData.setFareType(fareType);
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        switch (journeyType) {
            case OUTBOUND:
                testData.setFlightKey(flightsService.getOutboundFlight().getFlightKey());
                iAddedItToTheBasket(fareType, OUTBOUND);
                break;
            case INBOUND:
                testData.setFlightKey(flightsService.getInboundFlight().getFlightKey());
                iAddedItToTheBasket(fareType, INBOUND);
                break;
            case OUTBOUND_INBOUND:
                testData.setFlightKey(flightsService.getOutboundFlight().getFlightKey());
                iAddedItToTheBasket(fareType, OUTBOUND);
                testData.setFlightKey(flightsService.getInboundFlight().getFlightKey());
                iAddedItToTheBasket(fareType, INBOUND);
                break;
            default:
                testData.setFlightKey(flightsService.getOutboundFlight().getFlightKey());
                iAddedItToTheBasket(fareType, SINGLE);
                break;
        }

    }

    @Given("^I have basket with '(Standard|Flexi|Staff)' fare as 'outbound/inbound' journey to build basket content$")
    public void iItToTheBasket(String fareType) throws Throwable {
        iAddedItToTheBasket(fareType, OUTBOUND);
        iAddedItToTheBasket(fareType, INBOUND);
    }

    @Given("^I have basket with '(Standard|Flexi|Staff)' fare , 'outbound/inbound' journey and seats (.*) to build basket content$")
    public void iItToTheBasket1(String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        iAddedItToTheBasket(fareType, OUTBOUND);
        iAddedItToTheBasket(fareType, INBOUND);
    }

    @When("^I add it to the basket$")
    public void iAddItToTheBasket() throws Throwable {
        FlightsService flightsService = testData.getData(GET_FLIGHT_SERVICE);
        basketHelper.addFlightToBasketWithAdditionalSeat(
                flightsService.getOutboundFlights()
                , flightsService.getResponse().getCurrency()
                , testData.getPassengerMix()
                , "single"
                , "Standard"
        );

    }

    @When("^add passengers\"([^\"]*)\" with bookingtype \"([^\"]*)\" and faretype \"([^\"]*)\" to basket$")
    public void addPassengerswithBookingtypeandFaretypetobasket(String passengers, String bookingType, String fareType) throws Throwable {

        flightsService = flightHelper.getFlights(testData.getChannel(), "2 adult", testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        flight = flightsService.getOutboundFlight();
        AddFlightRequestBody aFlight = basketHelper.createAddFlightRequestWithBookingTypeAndFareType(flight, bookingType, fareType, "2 Adult");
        basketHelper.addFlightToBasketAsChannel(aFlight, testData.getChannel());

     }
}