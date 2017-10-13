package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.BasketDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AssociateInfantRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AssociateInfantRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.AssociateInfantService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.ASSOCIATE_INFANT;

/**
 * Created by robertadigiorgio on 29/08/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
@DirtiesContext
public class ChangeAssociatedAdultToInfantOnSeatSteps {

    private AssociateInfantService associateInfantService;

    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketDao basketDao;

    @Steps
    private feature.document.steps.services.createbasketservices.AddFlightSteps addFlightSteps;

    private AssociateInfantRequestBody associateInfantRequestBody;
    private BasketsResponse basketFlights;
    private String basketId;
    private String passengerId;
    private String passengerIdInfantOnSeat;
    private FlightHelper flightHelper;


    @When("^I send a request to associate adult to infant on seat with (.*) and (.*) fare type with (.*)$")
    public void iSendARequestToAssociateAdultToInfantOnSeatWithPassengerAndFareTypeFareType(String passengerMix,String fareType,String invalid) throws Throwable {
        testData.setPassengerMix(passengerMix);

        addFlightSteps.sendAddFlightRequest(null, null, null, null, null, null, null, null, null, testData.getOrigin(), testData.getDestination(), fareType, passengerMix, testData.getOutboundDate(), testData.getInboundDate(), null, null, null, null, null);

        BasketService basketService = testData.getData(BASKET_SERVICE);
        basketFlights = basketService.getResponse();
        basketId = basketFlights.getBasket().getCode();
        List<Basket.Passenger> passengers = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (invalid.equalsIgnoreCase("child")){
            passengerId=passengers.stream()
                    .filter(
                            passenger -> passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase("child")
                    ).findFirst().orElse(null).getCode();
        }else{
            List<Basket.Passenger> passengerIdAdult = passengers.stream()
                    .filter(
                            passenger -> passenger.getPassengerDetails().getPassengerType().equals("adult")
                    ).collect(Collectors.toList());


            String passengerIdOld = passengerIdAdult.stream().findFirst().orElse(null).getCode();
            passengerId = passengerIdAdult.stream()
                    .filter(
                            passenger -> !passenger.getCode().equals(passengerIdOld)
                    ).findFirst().orElse(null).getCode();
        }


        passengerIdInfantOnSeat = passengers.stream()
                .map(Basket.Passenger::getInfantsOnSeat)
                .flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant doesn't exist"));


        switch (invalid) {
            case "invalid basketId":
                basketId = "INVALID_BASKET";
                break;
            case "invalid passengerId":
                passengerId = "INVALID_PASSENGER";
                break;
            case "invalid infantId":
                passengerIdInfantOnSeat = "INVALID_INFANT_ID";
        }

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).path(ASSOCIATE_INFANT).passengerId(passengerId).build();
        associateInfantRequestBody = AssociateInfantRequestBody.builder().infantPassengerId(passengerIdInfantOnSeat).build();
        if (testData.dataExist(CHANNEL)){
            testData.setChannel(testData.getData(CHANNEL));
        }
        associateInfantService = serviceFactory.associateInfant(new AssociateInfantRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, associateInfantRequestBody));
        testData.setData(SERVICE, associateInfantService);
        associateInfantService.invoke();

    }

    @Then("^I will return an updated basket for infant on Seat$")
    public void iWillReturnAnUpdatedBasketForInfantOnSeat() throws Throwable {
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();
        associateInfantService.assertThat().confirmationAssociatedInfantOnSeat(basketFlights.getBasket(), passengerId, passengerIdInfantOnSeat);
    }




}
