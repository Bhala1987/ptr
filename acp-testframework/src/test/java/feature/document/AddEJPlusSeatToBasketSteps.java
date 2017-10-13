package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.AddEJPlusSeatToBasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by Raja on 04/05/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class AddEJPlusSeatToBasketSteps {

    protected static Logger LOG = LogManager.getLogger(AddEJPlusSeatToBasketSteps.class);

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private AddEJPlusSeatToBasketHelper addEJPlusSeatToBasketHelper;
    private FindFlightsResponse.Flight flight;
    @Autowired
    private FlightHelper flightHelper;

    @Given("^I have added a ([^\"]*) to ([^\"]*) flight to basket with faretype ([^\"]*) for ([^\"]*)$")
    public void i_have_added_a_something_to_something_flight_to_basket_with_faretype_something_for_something(String src, String dest, String fareType, String channel) throws Throwable {
        testData.setChannel(channel);
        testData.setCurrency("GBP");
        testData.setPassengerMix("1 Adult");
        basketHelper.myBasketContainsAFlightWithPassengerMix(testData.getPassengerMix(), testData.getChannel(), fareType, false);
    }

    @When("^I receive a valid updatePassengerDetails with ejPlus type ([^\"]*)$")
    public void i_receive_a_valid_updatepassengerdetails_with_ejplus_type_something(String type) throws Throwable {
        addEJPlusSeatToBasketHelper.updateFirstPassengerWithEJPlus(testData.getChannel(), type);
    }
    @When("^I add valid passenger details with ejPlus type ([^\"]*)$")
    public void ivalid_updatepassengerdetails_with_ejplus_type_something(String type) throws Throwable {
        addEJPlusSeatToBasketHelper.updateSelectedPassengerWithEJPlus(testData.getChannel(), type,basketHelper.getPassenger("adult"));
    }


    @And("^update traveller with ejplus information$")
    public void update_traveller_with_ejplus_information() throws Throwable {
        addEJPlusSeatToBasketHelper.updateFirstPassengerWithEJPlus(testData.getChannel(), "customer");

    }


    @Then("^the seats are updated to ([^\"]*) and seat price is \"([^\"]*)\"$")
    public void the_seats_are_updated_to_something_and_seat_price_is_something(String seat, String price) throws Throwable {
        addEJPlusSeatToBasketHelper.verifySeatIsUpdated(seat, price);
    }

    @Then("^the seats are not changed still it is ([^\"]*) and seat price is \"([^\"]*)\"$")
    public void the_seats_are_not_changed_still_it_is_something_and_seat_price_is_something(String seat, String price) throws Throwable {
        addEJPlusSeatToBasketHelper.verifySeatIsUpdated(seat, price);
    }

    @When("^I added a seat (EXTRA_LEGROOM|STANDARD|UPFRONT) to the first passenger$")
    public void i_added_a_seat_something_to_the_first_passenger(PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatToBasket(aSeatProduct);
        basketHelper.getBasket(purchasedSeatHelper.getPurchasedSeatService().getResponse().getOperationConfirmation().getBasketCode(), testData.getChannel());
    }

    @Then("^the seats are not changed still it is \"([^\"]*)\" but the price will be \"([^\"]*)\"$")
    public void the_seats_are_not_changed_still_it_is_something_but_the_price_will_be_something(String seat, String priceDiff) throws Throwable {
        DecimalFormat df = new DecimalFormat("#.##");
        String firstSeatKey = priceDiff.split("MINUS")[0].split("-")[1].trim();
        String secondSeatKey = priceDiff.split("MINUS")[1].split("-")[1].trim();
        HashMap<String, String> seatValuePrice = new HashMap<String, String>();
        for (int i = 0; i < purchasedSeatHelper.getSeatMapService().getResponse().getProducts().size(); i++) {
            seatValuePrice.put(purchasedSeatHelper.getSeatMapService().getResponse().getProducts().get(i).getId(), String.valueOf(purchasedSeatHelper.getSeatMapService().getResponse().getProducts().get(i).getBasePrice()));
        }

        Double firstSeatPrice = Double.parseDouble(seatValuePrice.get(firstSeatKey));
        Double secondSeatPrice = Double.parseDouble(seatValuePrice.get(secondSeatKey));
        Double expectedSeatPrice = firstSeatPrice - secondSeatPrice;

        addEJPlusSeatToBasketHelper.verifySeatIsUpdated(seat, df.format(expectedSeatPrice));
    }
}
