package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.BasketDao;
import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.database.hybris.models.PassengerStatus;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AssociateInfantRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AssociateInfantRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.AssociateInfantService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.ASSOCIATE_INFANT;

/**
 * Created by robertadigiorgio on 03/05/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
@DirtiesContext
public class ChangeAssociatedAdultToInfantOnLapInTheBasketSteps {

    private AssociateInfantService associateInfantService;

    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketDao basketDao;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private BookingHelper bookingHelper;

    private AssociateInfantRequestBody associateInfantRequestBody;
    private BasketsResponse basketFlights;
    private String basketId;
    private String passengerId;
    private String passengerIdInfantOnLap;
    private String passengerIdOld;
    private PassengerStatus originalOldPassengerStatus;
    private PassengerStatus originalNewPassengerStatus;
    private PassengerStatus originalInfantPassengerStatus;

    @And("^I want to associate another to infant on lap that have one infant$")
    public void iWantToAssociateAnotherToInfantOnLapThatHaveOneInfant() throws Throwable {
        testData.getPassengerId();
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basketFlights = basketService.getResponse();
        basketId = basketFlights.getBasket().getCode();
        passengerId = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(
                        basketPassenger -> basketPassenger.getPassengerDetails().getPassengerType().equals("adult")
                ).findFirst().orElse(null).getCode();

        passengerIdInfantOnLap = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> !basketPassenger.getCode().equals(passengerId))
                .map(Basket.Passenger::getInfantsOnLap)
                .flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant doesn't exist"));
    }

    @And("^I want to associate (.*) to infant on lap$")
    public void iWantToAssociateAssociateToInfantOnLap(String typePassenger) throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        basketFlights = basketService.getResponse();
        basketId = basketFlights.getBasket().getCode();
        List<Basket.Passenger> passengers = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Basket.Passenger> passengerIdOutbound = passengers.stream()
                .filter(passenger -> Objects.nonNull(passenger.getInfantsOnLap()) && new ArrayList<>(passenger.getInfantsOnLap()).isEmpty())
                .collect(Collectors.toList());

        passengerId = passengerIdOutbound.stream()
                .filter(
                        passenger -> passenger.getPassengerDetails().getPassengerType().equals(typePassenger)
                ).findFirst().orElse(null).getCode();

        passengerIdInfantOnLap = passengers.stream()
                .map(Basket.Passenger::getInfantsOnLap)
                .flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant doesn't exist"));


    }

    @But("^the request contains (.*) parameter$")
    public void theRequestContainsParameter(String invalid) throws Throwable {
        switch (invalid) {
            case "invalid basketId":
                basketId = "INVALID_BASKET";
                break;
            case "invalid passengerId":
                passengerId = "INVALID_PASSENGER";
                break;
            case "invalid infantId":
                passengerIdInfantOnLap = "INVALID_INFANT_ID";
                break;
            default:
                break;
        }
    }


    @When("^I send a request to associate adult to infant on lap$")
    public void iSendARequestToAssociateAdultToInfantOnLap() throws Throwable {
        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).path(ASSOCIATE_INFANT).passengerId(passengerId).build();
        associateInfantRequestBody = AssociateInfantRequestBody.builder().infantPassengerId(passengerIdInfantOnLap).build();
        if (testData.keyExist(CHANNEL)) {
            testData.setChannel(testData.getData(CHANNEL));
        }
        associateInfantService = serviceFactory.associateInfant(new AssociateInfantRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, associateInfantRequestBody));
        testData.setData(SERVICE, associateInfantService);
        associateInfantService.invoke();
    }

    @And("^I will not change the association$")
    public void iWillNotChangeTheAssociation() throws Throwable {
        basketId = basketFlights.getBasket().getCode();
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();
        associateInfantService.assertThat().associationInfantNotChange(basketFlights.getBasket(), passengerId, passengerIdInfantOnLap);

    }

    @Then("^I will return an updated basket for infant$")
    public void iWillReturnAnUpdatedBasketForInfant() throws Throwable {
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();
        associateInfantService.assertThat().confirmationAssociatedInfant(basketFlights.getBasket(), passengerId, passengerIdInfantOnLap);
    }

    @Then("^I will add the extras cabin bag to the new passenger with (.*) on (.*)$")
    public void iWillAddTheExtrasCabinBagToTheNewPassengerWithOn(String fareType, String channel) throws Throwable {
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();
        associateInfantService.assertThat().checkAddExtraCabinBag(basketFlights.getBasket(), passengerIdInfantOnLap, fareType, channel);
    }

    @When("^I send a request to associate infant to another Adult$")
    public void iSendARequestToAssociateInfantToAnother() throws Throwable {
        associateInfantToAdultWithCheckStatus();
    }

    @Then("^will return an updated basket and check the total with (.*)$")
    public void willReturnAnUpdatedBasketAndCheckTheTotal(String fareType) throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        Basket[] basketsResponse = new Basket[1];
        try {
            pollingLoop().until(() -> {
                basketsResponse[0] =   basketHelper.getBasket(basket.getCode(),testData.getChannel());

                return basketsResponse[0].getOutbounds().stream()
                        .map(Basket.Flights::getFlights).flatMap(Collection::stream)
                        .map(Basket.Flight::getPassengers).flatMap(Collection::stream)
                        .anyMatch(basketPassenger -> basketPassenger.getCode().equals(passengerId) && basketPassenger.getInfantsOnLap().contains(passengerIdInfantOnLap));
            });
        } catch (ConditionTimeoutException e) {
            throw new EasyjetCompromisedException("The Adult does't have the infant");
        }

        FeesAndTaxesModel fee = feesAndTaxesDao.getAdminFees(basket.getCurrency().getCode(), fareType).stream().findFirst().orElse(null);
        Double feeValue;
        if (fee != null) {
            feeValue = fee.getFeeValue();
        } else {
            feeValue = 0.0;
        }
        basketHelper.getBasketService().assertThat().priceCalculationAreRight(Integer.valueOf(basket.getCurrency().getDecimalPlaces()), feeValue);

        basketFlights = basketHelper.getBasketService().getResponse();
        PassengerStatus actualOldPassengerStatus = bookingDao.getBookingPassengerStatus(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), passengerIdOld);
        PassengerStatus actualNewPassengerStatus = bookingDao.getBookingPassengerStatus(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), passengerId);
        PassengerStatus actualInfantPassengerStatus = bookingDao.getBookingPassengerStatus(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), passengerIdInfantOnLap);

        associateInfantService.assertThat().confirmationAssociatedInfant(basketFlights.getBasket(), passengerId, passengerIdInfantOnLap);
        associateInfantService.assertThat().checkAddExtraCabinBag(basketFlights.getBasket(), passengerIdInfantOnLap, fareType, testData.getChannel());
        associateInfantService.assertThat().checkPassengerStatus(originalOldPassengerStatus, actualOldPassengerStatus);
        associateInfantService.assertThat().checkPassengerStatus(originalNewPassengerStatus, actualNewPassengerStatus);
        associateInfantService.assertThat().checkPassengerStatus(originalInfantPassengerStatus, actualInfantPassengerStatus);
    }

    @When("^I send a request wrong to associate infant to another Adult with (.*) invalid$")
    public void iSendARequestWrongToAssociateInfantToAnotherAdult(String invalid) throws Throwable {
        testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));

        basketId = testData.getBasketId();
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();

        List<Basket.Passenger> passengers = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Basket.Passenger> passengerIdOutbound = passengers.stream()
                .filter(passenger -> Objects.nonNull(passenger.getInfantsOnLap()) && new ArrayList<>(passenger.getInfantsOnLap()).isEmpty())
                .collect(Collectors.toList());

        passengerId = passengerIdOutbound.stream()
                .filter(
                        passenger -> passenger.getPassengerDetails().getPassengerType().equals("adult")
                ).findFirst().orElse(null).getCode();


        passengerIdInfantOnLap = passengers.stream()
                .map(Basket.Passenger::getInfantsOnLap)
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
                passengerIdInfantOnLap = "INVALID_INFANT_ID";
                break;
            case "associate adult":
                passengerIdInfantOnLap = basketFlights.getBasket().getOutbounds().stream()
                        .map(Basket.Flights::getFlights)
                        .flatMap(Collection::stream)
                        .map(Basket.Flight::getPassengers)
                        .flatMap(Collection::stream)
                        .filter(
                                basketPassenger -> basketPassenger.getPassengerDetails().getPassengerType().equals("adult")
                        ).findFirst().orElse(null).getCode();
                break;
        }

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).path(ASSOCIATE_INFANT).passengerId(passengerId).build();
        associateInfantRequestBody = AssociateInfantRequestBody.builder().infantPassengerId(passengerIdInfantOnLap).build();
        associateInfantService = serviceFactory.associateInfant(new AssociateInfantRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, associateInfantRequestBody));
        testData.setData(SERVICE, associateInfantService);
        associateInfantService.invoke();
        associateInfantService.getResponse();
    }

    @When("^I send a request to associate infant to another Infant$")
    public void iSendARequestToAssociateInfantToAnotherInfant() throws Throwable {
        testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));

        basketId = testData.getBasketId();
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();

        List<Basket.Passenger> passengers = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Basket.Passenger> passengerIdOutbound = passengers.stream()
                .filter(passenger -> Objects.nonNull(passenger.getInfantsOnLap()) && new ArrayList<>(passenger.getInfantsOnLap()).isEmpty())
                .collect(Collectors.toList());

        passengerId = passengerIdOutbound.stream()
                .filter(
                        passenger -> passenger.getPassengerDetails().getPassengerType().equals("infant")
                ).findFirst().orElse(null).getCode();


        passengerIdInfantOnLap = passengers.stream()
                .map(Basket.Passenger::getInfantsOnLap)
                .flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant doesn't exist"));

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).path(ASSOCIATE_INFANT).passengerId(passengerId).build();
        associateInfantRequestBody = AssociateInfantRequestBody.builder().infantPassengerId(passengerIdInfantOnLap).build();
        associateInfantService = serviceFactory.associateInfant(new AssociateInfantRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, associateInfantRequestBody));
        testData.setData(SERVICE, associateInfantService);
        associateInfantService.invoke();
    }

    @When("^I send a request to associate infant to another Adult that have one infant$")
    public void iSendARequestToAssociateInfantToAnotherAdultThatHaveOneInfant() throws Throwable {
        testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));

        basketId = testData.getBasketId();
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();

        passengerId = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(
                        basketPassenger -> basketPassenger.getPassengerDetails().getPassengerType().equals("adult")
                ).findFirst().orElse(null).getCode();

        passengerIdInfantOnLap = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .filter(basketPassenger -> !basketPassenger.getCode().equals(passengerId))
                .map(Basket.Passenger::getInfantsOnLap)
                .flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant doesn't exist"));

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).path(ASSOCIATE_INFANT).passengerId(passengerId).build();
        associateInfantRequestBody = AssociateInfantRequestBody.builder().infantPassengerId(passengerIdInfantOnLap).build();
        associateInfantService = serviceFactory.associateInfant(new AssociateInfantRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, associateInfantRequestBody));
        testData.setData(SERVICE, associateInfantService);
        associateInfantService.invoke();
    }

    @When("^I send a request to associate infant to another Adult with seat$")
    public void iSendARequestToAssociateInfantToAnotherAdultWithSeatSeat() throws Throwable {
        basketId = testData.getBasketId();
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();
        associateInfantToAdultWithCheckStatus();
    }

    public void associateInfantToAdultWithCheckStatus() throws EasyjetCompromisedException {
        basketId = testData.getBasketId();
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();

        List<Basket.Passenger> passengers = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Basket.Passenger> passengerIdOutbound = passengers.stream()
                .filter(passenger -> Objects.nonNull(passenger.getInfantsOnLap()) && new ArrayList<>(passenger.getInfantsOnLap()).isEmpty())
                .collect(Collectors.toList());

        passengerId = passengerIdOutbound.stream()
                .filter(
                        passenger -> passenger.getPassengerDetails().getPassengerType().equals("adult")
                ).findFirst().orElse(null).getCode();


        passengerIdInfantOnLap = passengers.stream()
                .map(Basket.Passenger::getInfantsOnLap)
                .flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant doesn't exist"));


        passengerIdOld = passengers.stream()
                .filter(
                        passenger -> passenger.getPassengerDetails().getPassengerType().equals("adult") && passenger.getInfantsOnLap().contains(passengerIdInfantOnLap)
                ).findFirst().orElse(null).getCode();

        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).path(ASSOCIATE_INFANT).passengerId(passengerId).build();
        associateInfantRequestBody = AssociateInfantRequestBody.builder().infantPassengerId(passengerIdInfantOnLap).build();
        associateInfantService = serviceFactory.associateInfant(new AssociateInfantRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, associateInfantRequestBody));
        testData.setData(SERVICE, associateInfantService);
        associateInfantService.invoke();
        associateInfantService.getResponse();

        basketHelper.getBasket(basketId, testData.getChannel());
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    @When("^I send a request to associate infant to another Adult with different seat$")
    public void iSendARequestToAssociateInfantToAnotherAdultWithDifferentSeat() throws Throwable {
        testData.setChannel(testData.getData(SerenityFacade.DataKeys.CHANNEL));
        testData.setBasketId(testData.getData(SerenityFacade.DataKeys.BASKET_ID));

        basketId = testData.getBasketId();
        basketHelper.getBasket(basketId, testData.getChannel());
        basketFlights = basketHelper.getBasketService().getResponse();

        List<Basket.Passenger> passengers = basketFlights.getBasket().getOutbounds().stream()
                .map(Basket.Flights::getFlights)
                .flatMap(Collection::stream)
                .map(Basket.Flight::getPassengers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Basket.Passenger> passengerIdOutbound = passengers.stream()
                .filter(passenger -> Objects.nonNull(passenger.getInfantsOnLap()) && new ArrayList<>(passenger.getInfantsOnLap()).isEmpty())
                .collect(Collectors.toList());

        passengerId = passengerIdOutbound.stream()
                .filter(
                        passenger -> passenger.getPassengerDetails().getPassengerType().equals("adult")
                ).findFirst().orElse(null).getCode();


        passengerIdInfantOnLap = passengers.stream()
                .map(Basket.Passenger::getInfantsOnLap)
                .flatMap(Collection::stream)
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The passenger infant doesn't exist"));


        passengerIdOld = passengers.stream()
                .filter(
                        passenger -> passenger.getPassengerDetails().getPassengerType().equals("adult") && passenger.getInfantsOnLap().contains(passengerIdInfantOnLap)
                ).findFirst().orElse(null).getCode();
        BasketPathParams pathParams = BasketPathParams.builder().basketId(basketId).path(ASSOCIATE_INFANT).passengerId(passengerId).build();
        associateInfantRequestBody = AssociateInfantRequestBody.builder().infantPassengerId(passengerIdInfantOnLap).build();
        associateInfantService = serviceFactory.associateInfant(new AssociateInfantRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, associateInfantRequestBody));
        testData.setData(SERVICE, associateInfantService);
        associateInfantService.invoke();

        basketHelper.getBasket(basketId, testData.getChannel());
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());

    }

    @Then("^I will check that the infant is associate$")
    public void iWillCheckThatTheInfantIsAssociate() throws Throwable {

        basketId = testData.getBasketId();

        passengerIdInfantOnLap = (String) testData.getData("passengerIdInfant");
        passengerId = (String) testData.getData("passengerId");

        basketFlights = retrieveUpdatedBasketAfterChange();

        associateInfantService = testData.getData(SERVICE);
        associateInfantService.assertThat().confirmationAssociatedInfant(basketFlights.getBasket(), passengerId, passengerIdInfantOnLap);
    }

    private BasketsResponse retrieveUpdatedBasketAfterChange() {
        final int[] attempts = {3};
        try {
            pollingLoop().until(() -> {
                Basket basket = basketHelper.getBasket(basketId, testData.getChannel());
                Thread.sleep(1000); // required otherwise we call many get basket in the same time and is not useful
                basketFlights = basketHelper.getBasketService().getResponse();
                attempts[0]--;
                return basket.getOutbounds().stream()
                        .flatMap(f -> f.getFlights().stream())
                        .flatMap(g -> g.getPassengers().stream())
                        .filter(h -> h.getCode().equalsIgnoreCase(passengerId))
                        .flatMap(i -> i.getInfantsOnLap().stream())
                        .collect(Collectors.toList())
                        .contains(passengerIdInfantOnLap)
                        || attempts[0] == 0;
            });
        } catch (ConditionTimeoutException e) { } // no catch, I want to proceed with the flow
        return basketFlights;
    }

    @And("^I have amendable basket for (.*) fare and (.*) passenger$")
    public void iHaveAmendableBasketForFareTypeFareAndPassengerPassenger(String fare, String passenger) throws Throwable {
        String amendableBasket = bookingHelper.createBookingWithMultipleFlightAndGetAmendable(passenger, fare, 1);
        testData.setBasketId(amendableBasket);
    }

    @Then("^the basket should be updated with the new association$")
    public void theBasketShouldBeUpdatedWithTheNewAssociation() throws Throwable {
        Basket basket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());

        Basket.Passenger oldPassengerWithInfant = basket.getOutbounds().stream().flatMap(f->f.getFlights().stream()).flatMap(p->p.getPassengers().stream()).filter(pass -> pass.getCode().equalsIgnoreCase(passengerIdOld)).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with code " + passengerIdOld));
        Basket.Passenger updatedPassengerWithInfant = basket.getOutbounds().stream().flatMap(f->f.getFlights().stream()).flatMap(p->p.getPassengers().stream()).filter(pass -> pass.getCode().equalsIgnoreCase(passengerId)).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with code " + passengerId));
        associateInfantService.assertThat().verifyAssociationInfantHasBeenUpdated(basket, updatedPassengerWithInfant.getCode(), oldPassengerWithInfant.getCode());
    }

    @Then("^the booking should be updated with the new association$")
    public void theBookingShouldBeUpdatedWithTheNewAssociation() throws Throwable {

        GetBookingResponse.Booking bookingDetails = bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getData(CHANNEL)).getBookingContext().getBooking();

        GetBookingResponse.Passenger oldPassengerWithInfant = bookingDetails.getOutbounds().stream().flatMap(f->f.getFlights().stream()).flatMap(p->p.getPassengers().stream()).filter(pass -> pass.getCode().equalsIgnoreCase(passengerIdOld)).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with code " + passengerIdOld));

        GetBookingResponse.Passenger updatedPassengerWithInfant = bookingDetails.getOutbounds().stream().flatMap(f->f.getFlights().stream()).flatMap(p->p.getPassengers().stream()).filter(pass -> pass.getCode().equalsIgnoreCase(passengerId)).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with code " + passengerId));

        associateInfantService.assertThat().verifyAssociationInfantHasBeenUpdatedBooking(bookingDetails, updatedPassengerWithInfant.getCode(), oldPassengerWithInfant.getCode());
    }
}
