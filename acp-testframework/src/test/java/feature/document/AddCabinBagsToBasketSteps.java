package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.SeatMapQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by rajakm on 02/03/2017.
 */

@ContextConfiguration(classes = TestApplication.class)


public class AddCabinBagsToBasketSteps {

    private static final Logger LOG = LogManager.getLogger(AddFlightToBasketSteps.class);

    private FlightsService flightsService;
    private SeatMapQueryParams params;
    @Autowired
    MembershipDao membershipDao;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private TravellerHelper passengerHelper;
    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;

    private void findAFlightForOneAdult(String bundleType) throws Throwable {
        flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid("Digital").build(), params));
        flightsService.invoke();
    }

    @Given("^I have added the flight with (.+) bundle to the basket$")
    public void i_have_added_the_flight_with_bundle_to_the_basket(String bundleType) throws Throwable {
        String channel = "Digital";
        String journey = "SINGLE";
        String passengerMix = "1 Adult";
        String currency = "GBP";
        switch (bundleType) {
            case "Staff":
                customerHelper.loginWithValidCredentials(StringUtils.EMPTY, "a.rossi@reply.co.uk", "1234", false);
                basketHelper.myBasketContainsAFlightWithPassengerMixForStaff(passengerMix, channel, bundleType);
                break;
            case "Inclusive":
                basketHelper.myBasketContainsWithPassengerMixWithDeal("Digital", "ApplicationId,OfficeId,CorporateId", passengerMix);
                break;
            default:
                basketHoldItemsHelper.addValidFlightToTheBasket(channel, bundleType, journey, passengerMix, currency);
                break;
        }
    }

    @Then("^I will see (.+) Cabin bags under the (.+) bundle in the basket$")
    public void i_will_see_cabin_bags_under_the_bundle_in_the_basket(String numberOfBags, String bundletype) throws Throwable {
        basketHelper.getBasketService().assertThat().theNumberOfCabinBagsAddedToThePassenger(numberOfBags);
    }

    @Given("^I have added the flight with (.+) and passengers as (.+) to the basket$")
    public void i_have_added_the_flight_with_and_passengers_as_to_the_basket(String bundleType, String passengerMix) throws Throwable {
        String channel = "Digital";
        String journey = "SINGLE";
        String currency = "GBP";
        basketHoldItemsHelper.addValidFlightToTheBasket(channel, bundleType, journey, passengerMix, currency);
    }

    @Then("^I cabin bags (.+) auto added based on the passengers$")
    public void i_will_add_cabin_bags_based_on_the_passengers_entered(String numberOfBags) throws Throwable {
        basketHelper.getBasketService().assertThat().theNumberOfCabinBagsAddedToThePassenger(numberOfBags);
    }

    @When("^the EJ plus number is (.+) to the passenger")
    public void the_passengers_ej_plus_number_is_to_the_passenger(String ejstatus) throws Throwable {
        String passengerMix = "1 Adult";
        MemberShipModel ejPlusMemberBasedOnStatus = membershipDao.getEJPlusMemberBasedOnStatus("COMPLETED");
        String validEjPlusNumber = ejPlusMemberBasedOnStatus.getEjMemberShipNumber();
        String validSurname =ejPlusMemberBasedOnStatus.getLastname();
        if (ejstatus.equals("added")) {
            List<FieldAndValue> myFields = new ArrayList<>();
            myFields.add(new FieldAndValue("ejPlus", validEjPlusNumber));
            myFields.add(new FieldAndValue("ejPlusSurname", validSurname));
            Passengers request = passengerHelper.createRequestWithFieldSetAs(myFields, basketHelper.getBasketService().getResponse(), passengerMix);
            Passenger pax = request.getPassengers().stream().filter(h -> (!h.getRelatedAdult().isEmpty()) && h.getPassengerDetails().getPassengerType().equals(CommonConstants.ADULT))
                    .findFirst()
                    .orElse(null);
            basketHelper.updatePassengersForChannel(request, "Digital", basketHelper.getBasketService().getResponse().getBasket().getCode());
        }
    }

    @Then("^I (.+) another Cabin bag for (.+) to the passenger for the sector$")
    public void i_another_cabin_bag_for_to_the_passenger_for_the_sector(String action, String bundleType) throws Throwable {
        String oneBag = "1";
        String twoBags = "2";
        basketHelper.getBasketPassengerService().getResponse();
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());

        if (action.equals("will add") || bundleType.equals("Flexi")) {
            basketHelper.getBasketService().assertThat().theNumberOfCabinBagsAddedToThePassenger(twoBags);
        } else if (action.equals("will not add") && !Objects.equals(bundleType, "Flexi")) {
            basketHelper.getBasketService().assertThat().theNumberOfCabinBagsAddedToThePassenger(oneBag);
        }
    }

    @Then("^the passenger already has (.+) of Cabin bags for a sector$")
    public void the_passenger_already_has_of_cabin_bags_for_a_sector(String numberOfBags) throws Throwable {
        basketHelper.getBasketService().assertThat().theNumberOfCabinBagsAddedToThePassenger(numberOfBags);
    }

    @Given("^I have added a valid flight to my basket for the channel \"([^\"]*)\" with passenger mix \"([^\"]*)\"$")
    public void iHaveAddedAValidFlightToMyBasketForTheChannelWithPassengerMix(String channel, String mix) throws Throwable {
        String bundle = "Standard";
        String journey = "SINGLE";
        String currency = "GBP";

        basketHoldItemsHelper.addValidFlightToTheBasket(channel, bundle, journey, mix, currency);
    }

}
