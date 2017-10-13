package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper.SEATPRODUCTS;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Pricing;
import com.hybris.easyjet.fixture.hybris.invoke.services.AssociateInfantService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.document.steps.services.createbookingservices.GetPaymentMethodsForChannelSteps;
import feature.document.steps.services.managebookingservices.GetBookingSteps;
import feature.document.steps.services.managecustomerservices.RegisterCustomerSteps;
import net.thucydides.core.annotations.Steps;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.COMPLETED;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by jamie on 16/04/2017.
 */

@ContextConfiguration(classes = TestApplication.class)

public class AddPurchasedSeatSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private ManageAdditionalFareToPassengerInBasketHelper manageAdditionalFareToPassengerInBasketHelper;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private TravellerHelper travellerHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private MembershipDao membershipDao;
    private GetBookingService getBookingService;

    private List<Double> oldListOfPrices;
    private List<Double> newListOfPrices;
    @Steps
    private GetPaymentMethodsForChannelSteps getPaymentMethodsForChannelSteps;
    @Steps
    private feature.document.steps.services.createbookingservices.CommitBookingSteps commitBookingSteps;
    @Steps
    private GetBookingSteps getBookingSteps;
    @Steps
    private RegisterCustomerSteps registerCustomerSteps;
    private String passengerInfantId;


    @When("^I make a request to add an available \"(EXTRA_LEGROOM|STANDARD)\" seat product$")
    public void iMakeARequestToAddAnAvailableSeatProduct(SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatToBasket(aSeatProduct);
    }

    @Then("^I will add the purchased seat with that price to the basket$")
    public void iWillAddThePurchasedSeatWithThatPriceToTheBasket() throws Throwable {
        Basket oldBasket = basketHelper.getBasketService().getResponse().getBasket();
        basketHelper.getBasket(oldBasket.getCode(), testData.getChannel());
        Basket newBasket = basketHelper.getBasketService().getResponse().getBasket();

        GetSeatMapResponse.Product myChosenSeatProduct = purchasedSeatHelper.getProductFromSeat(testData.getSeatProductInBasket());

        purchasedSeatHelper.getPurchasedSeatService().assertThat().basketTotalsAreUpdatedAfterPurchasingSeatProduct(
                oldBasket,
                newBasket,
                myChosenSeatProduct
        );
    }

    @When("^I make a request to add an available \"(EXTRA_LEGROOM|UPFRONT|STANDARD)\" seat product for each passenger$")
    public void iMakeARequestToAddAnAvailableSeatProductForEachPassenger(SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatToBasketForEachPassengerAndFlight(aSeatProduct, false);
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    @Then("^the seat product is added to the basket(?:| for each of the passengers)$")
    public void theSeatProductIsAddedToTheBasket() throws Throwable {
        basketHelper.getBasket(purchasedSeatHelper.getPurchasedSeatService().getResponse().getOperationConfirmation().getBasketCode());
        basketHelper.getBasketService().assertThat().seatsArePurchasedForEachPassenger(testData.getPurchsedSeatRequestBody());
    }

    @When("^I make a request to add an available \"(EXTRA_LEGROOM|STANDARD)\" seat product with missing seat number$")
    public void iMakeARequestToAddAnAvailableSeatProductForInvalidSeat(SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addPurchasedSeatToBasketWithInvalidSeatNumber(aSeatProduct);
    }

    @When("^I make a request to add an available \"(EXTRA_LEGROOM|STANDARD)\" seat product with invalid seat code$")
    public void iMakeARequestToAddAnAvailableSeatProductForInvalidSeatCode(SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addPurchasedSeatToBasketWithInvalidSeatCode(aSeatProduct);
    }

    @When("^I make a request to add an available \"(EXTRA_LEGROOM|STANDARD)\" seat product with invalid price$")
    public void iMakeARequestToAddAnAvailableSeatProductForInvalidprice(SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addPurchasedSeatToBasketWithInvalidPrice(aSeatProduct);
    }

    @Then("^the add purchase seat service should return the error:(.*)$")
    public void theAddPurchaseSeatServiceShouldReturnTheErrorSVC__(String errorCode) throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThatErrors().containedTheCorrectErrorMessage(errorCode.trim());
    }

    @When("^I make a request to add an available \"(EXTRA_LEGROOM|STANDARD)\" seat product when seat map service is unavailable$")
    public void iMakeARequestToAddAnAvailableSeatProductWhenSeatMapServiceIsUnavailable(SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addPurchasedSeatToBasketWithSeatServiceDisabled();
    }

    @When("^I send a request to add an available \"([^\"]*)\" seat product with \"([^\"]*)\" parameter$")
    public void iSendARequestToAddAnAvailableSeatProductWithParameter(SEATPRODUCTS aSeatProduct, String invalidParameter) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatToBasketWithInvalidRequest(aSeatProduct, invalidParameter);
    }

    @Then("^I will recalculate the price of the purchased seat$")
    public void iWillRecalculateThePriceOfThePurchasedSeat() throws Throwable {
        basketHelper.getBasket(purchasedSeatHelper.getPurchasedSeatService().getResponse().getOperationConfirmation().getBasketCode());
        basketHelper.getBasketService().assertThat().checkPriseSeatProduct(testData.getPurchsedSeatRequestBody());

    }

    @When("^I make a request to add an available \"([^\"]*)\" seat product for one passenger$")
    public void iMakeARequestToAddAnAvailableSeatProductForOnePassenger(SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatToBasketJustOnePassenger(aSeatProduct);
    }

    @Then("^the seat product is added to the basket for one of the passengers$")
    public void theSeatProductIsAddedToTheBasketForOneOfThePassengers() throws Throwable {
        basketHelper.getBasket(testData.getBasketId());
        basketHelper.getBasketService().assertThat().seatsArePurchasedForEachPassenger(testData.getPurchsedSeatRequestBody());
    }

    @Then("^I will also add all associated products in the bundle \"([^\"]*)\"$")
    public void iWillAlsoAddAllAssociatedProductsInTheBundle(String fareType) throws Throwable {
        basketHelper.getBasket(purchasedSeatHelper.getPurchasedSeatService().getResponse().getOperationConfirmation().getBasketCode());
        basketHelper.getBasketService().assertThat().checkTheCabinBagForProductSeat(testData.getPurchsedSeatRequestBody(), fareType);
    }

    @Then("^I will verify the seat has been allocated properly$")
    public void iWillVerifyTheSeatHasBeenAllocatedProperly() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().theSeatHasBeenAllocated(purchasedSeatHelper.getAvailableSeat(), purchasedSeatHelper.getAllocatePurchasedSeat());
    }

    @Then("^I will verify the seat has not been allocated$")
    public void iWillVerifyTheSeatHasNotBeenAllocated() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().theSeatHasNotBeenAllocated(purchasedSeatHelper.getAvailableSeat(), purchasedSeatHelper.getAllocatePurchasedSeat());
    }

    @When("^I make a request to add an available \"([^\"]*)\" seat product for one \"([^\"]*)\"$")
    public void iMakeARequestToAddAnAvailableSeatProductForOne(SEATPRODUCTS aSeatProduct, String typePassenger) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatForFirstPassengerType(aSeatProduct, typePassenger);
    }

    @Then("^the seat product is added for the required passenger \"([^\"]*)\"$")
    public void theSeatProductIsAddedForTheRequiredPassenger(String type) throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().getResponse();
        purchasedSeatHelper.getPurchasedSeatService().assertThat().theSeatHasBeenAddedForPassenger(purchasedSeatHelper.getBasketHelper().getBasket(purchasedSeatHelper.getBasketCode(), testData.getChannel()), purchasedSeatHelper.getPassengerCode(type), purchasedSeatHelper.getSeatForPassenger(purchasedSeatHelper.getPassengerCode(type)));
    }

    @When("^I make a request to add an available \"([^\"]*)\" seat product for one passenger with already allocated seat$")
    public void iMakeARequestToAddAnAvailableSeatProductForOnePassengerWithAlreadyAllocatedSeat(SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addAlreadyAllocateSeatOnePassenger(aSeatProduct);
    }

    @And("^I want to proceed with add purchased seat (.*)$")
    public void iWantToProceedWithAddPurchasedSeat(SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setVerifySeatAllocation(true);
        testData.setTypeOfSeat(aSeatProduct);
    }

    @And("^I want to proceed with add already allocated purchased seat (.*)$")
    public void iWantToProceedWithAddAlreadyAllocatedPurchasedSeat(SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setVerifySeatAllocation(true);
        testData.setTypeOfSeat(aSeatProduct);
        testData.setData(IS_ALREADY_ALLOCATED_SEAT, true);
    }

    @When("^I make a request to add an available \"([^\"]*)\" seat for all passenger$")
    public void iMakeARequestToAddAnAvailableSeatForAllPassenger(SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatForAllPassenger(aSeatProduct);
    }

    @When("^I make a request to add an available \"([^\"]*)\" seat for all passengers$")
    public void iMakeARequestToAddAnAvailableSeatForAllPassengers(SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.setSeatMapService(null);
        purchasedSeatHelper.addPurchasedSeatForAllPassenger(aSeatProduct);
    }

    @Then("^The passenger have the same seat as before$")
    public void thePassengerHaveTheSameSeatAsBefore() throws Throwable {
        basketHelper.getBasket(purchasedSeatHelper.getPurchasedSeatService().getResponse().getOperationConfirmation().getBasketCode());
        basketHelper.getBasketService().assertThat().checkThatThepassengerHaveTheSameSeat(purchasedSeatHelper.getSeatForPassenger(testData.getPassengerIdFromChange()), testData.getPassengerIdFromChange());
    }

    @And("^I make a request to add an available \"([^\"]*)\" seat for all passenger without infantOL$")
    public void iMakeARequestToAddAnAvailableSeatForAllPassengerWithoutInfantOL(SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatForAllPassengerWithOutInfantOL(aSeatProduct);
    }

    @When("^I add (\\d+) additonal seats that exceeds allowed for a passenger$")
    public void iAddAdditonalSeatsThatExceedsAllowedForAPassenger(int additionalSeats) throws Throwable {
        manageAdditionalFareToPassengerInBasketHelper.addAdditionalFareExceedsAllowableQuantity(additionalSeats);

    }

    @When("^I attempt to add another seat for the passenger$")
    public void iAttemptToAddAnotherSeatForThePassenger() throws Throwable {
        int additionalSeat = 1;
        manageAdditionalFareToPassengerInBasketHelper.addAdditionalFareExceedsAllowableQuantity(additionalSeat);
    }

    @Given("^I have exceeded the maximum allowed configured seat for \"([^\"]*)\" Passenger on the \"([^\"]*)\" channel$")
    public void iHaveExceededTheMaximumAllowedConfiguredSeatForPassengerOnTheChannel(String passengerMix, String channel) throws Throwable {
        int maximumAllowedConfigurationSeatsPerPassenger = 3;

        testData.setChannel(channel);
        testData.setData(HEADERS, HybrisHeaders.getValid(channel));
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix);
        manageAdditionalFareToPassengerInBasketHelper.addAdditionalFareExceedsAllowableQuantity(maximumAllowedConfigurationSeatsPerPassenger);
    }

    @When("^I added the seat with (.*) additional seat with (.*) and (.*) and (.*)$")
    public void iAddedTheSeatWithAdditionalSeatWithPassengerMixAndFareTypeAndSeat(Integer additionalSeat, String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addSeatWithAdditionalSeat(passengerMix, fareType, aSeatProduct, additionalSeat);
    }

    @When("^I sent a invalid request to added the seat with (.*) and (.*) and (.*)$")
    public void iSentAInvalidRequestToAddedTheSeatWithPassengerMixAndFareTypeAndSeat(String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addSeatWithoutAdditionalSeat(passengerMix, fareType, aSeatProduct);
    }

    @Then("^I check that the seat for additional is added$")
    public void iCheckThatTheSeatForAdditionalIsAdded() throws Throwable {
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        purchasedSeatHelper.getBasketResponse(purchasedSeatHelper.getBasketCode());
        basketHelper.getBasketService().assertThat().checkAddSeatForAdditionalSeat(basket, testData.getPurchsedSeatRequestBody());
    }

    @Then("^I note the seat price$")
    public void iNoteTheSeatPrice() throws Throwable {
        basketHelper.getBasket(purchasedSeatHelper.getPurchasedSeatService().getResponse().getOperationConfirmation().getBasketCode());

        oldListOfPrices = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(bounds -> bounds.getFlights().stream())
                .flatMap(flight1 -> flight1.getPassengers().stream())
                .map(passenger -> passenger.getSeat().getPricing().getBasePrice())
                .collect(Collectors.toList());
    }

    @Then("^the seat price is changed$")
    public void theSeatPriceIsChanged() throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        newListOfPrices = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(bounds -> bounds.getFlights().stream())
                .flatMap(flight1 -> flight1.getPassengers().stream())
                .map(passenger -> passenger.getSeat().getPricing().getBasePrice())
                .collect(Collectors.toList());
        assertThat(newListOfPrices.
                equals(oldListOfPrices.stream().map(price -> price + 0.5).collect(Collectors.toList()))).isTrue();
    }

    @When("^I sent a request to add purchased (.*) with (.*) additional seat already allocated for (.*) on (.*) fare flight$")
    public void iSentARequestToAddPurchasedWithAdditionalSeatAlreadyAllocatedForOnFareFlight(PurchasedSeatHelper.SEATPRODUCTS aSeatProduct, int additionalSeat, String passengerMix, String fare) throws Throwable {
        purchasedSeatHelper.addSeatWithAdditionalSeatAlreadyAllocated(passengerMix, fare, aSeatProduct, additionalSeat);
    }

    @When("^I sent a request to commit booking for (.*) on (.*) fare flight with a seat (.*) already allocated$")
    public void iSentARequestToCommitBookingForOnFareFlightWithASeatSeatAllocated(String passengerMix, String fare, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        testData.setFareType(fare);
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fare, false);
        purchasedSeatHelper.addAlreadyAllocateSeatOnePassenger(aSeatProduct);

        Basket basket = purchasedSeatHelper.verifySeatHasBeenUpdate(testData.getPassengerId(), true);
        testData.setBasketId(basket.getCode());
        testData.setData("oldBasket", basket);

        bookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(false, true);
    }

    @Then("^I see failure commit booking with error (.*)$")
    public void iSeeFailureCommitBookingWithError(String error) throws Throwable {
        bookingHelper.getCommitBookingService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @And("^I see the seat has been removed$")
    public void iSeeTheSeatHasBeenRemoved() throws Throwable {
        Basket basket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        testData.setData("newBasket", basket);

        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifySeatHasBeenRemovedFromPassenger(basket, testData.getPassengerId());
    }

    @And("^I see the price of the basket has been updated$")
    public void iSeeThePriceOfTheBasketHasBeenUpdated() throws Throwable {
        Basket oldBasket = (Basket) testData.getData("oldBasket");
        Pricing seatPrice = oldBasket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(testData.getPassengerId())).findFirst().orElse(null).getSeat().getPricing();
        PricingHelper priceOfSeat = new PricingHelper(seatPrice.getTotalAmountWithCreditCard() - (testData.getFareType().equalsIgnoreCase("Flexi") ? purchasedSeatHelper.getDiscountForFlexiFare() : 0.0),
                seatPrice.getTotalAmountWithDebitCard() - (testData.getFareType().equalsIgnoreCase("Flexi") ? purchasedSeatHelper.getDiscountForFlexiFare() : 0.0),
                0.0,
                0.0);
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPriceForBasketHasBeenUpdateAfterRemoving((Basket) testData.getData("oldBasket"), (Basket) testData.getData("newBasket"), priceOfSeat);
    }

    @And("^I verify the product associated to the original bundle$")
    public void iVerifyTheProductAssociatedToTheOriginalBundle() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyTheNewProductHasBeenStored((Basket) testData.getData("oldBasket"), (Basket) testData.getData("newBasket"), 0);
    }

    @When("^I update the age for (.*) as (.*) to (.*) not allowed from the emergency exit seat already allocated on the passenger$")
    public void iUpdateTheAgeForAsNotAllowedFromTheemErgencyExitSeatAlreadyAllocatedOnThePassenger(String passengerMix, String passengerTypeFrom, String passengerTypeTo) throws Throwable {
        testData.setTypeOfSeat(SEATPRODUCTS.EMERGENCY_EXIT);
        testData.setVerifySeatAllocation(true);
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), STANDARD, false);
        purchasedSeatHelper.addPurchasedSeatEmergencyExitToBasket(null);

        Basket basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        Passengers updatePassengers = travellerHelper.createRequestUpdatePassengerAge(basket, passengerTypeFrom, passengerTypeTo);
        basketHelper.updatePassengersForChannel(updatePassengers, testData.getChannel(), basketHelper.getBasketService().getResponse()
                .getBasket()
                .getCode());
    }

    @Then("^I see failure updating passenger with error (.*)$")
    public void iSeeFailureUpdatingPassengerWithError(String error) throws Throwable {
        basketHelper.getBasketPassengerService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @And("^I have in my basket (.*) where first adult associated a emergency exit seat and the second adult associated the infant$")
    public void iHaveInMyBasketPassengerWhereFirstAdultAssociatedAEmergencyExitSeatTypeAndTheSecondAdultAssociatedTheInfant(String passengerMix) throws Throwable {
        testData.setTypeOfSeat(SEATPRODUCTS.EMERGENCY_EXIT);
        testData.setVerifySeatAllocation(true);
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), STANDARD, false);

        Basket basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        String passengerCodeWithoutInfant = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getPassengerDetails().getPassengerType().equalsIgnoreCase("adult") && h.getInfantsOnLap().isEmpty()).findFirst().orElse(null).getCode();
        assertThat(Objects.nonNull(passengerCodeWithoutInfant))
                .withFailMessage("All passengers adult contains an infant")
                .isTrue();
        testData.setPassengerId(passengerCodeWithoutInfant);
        purchasedSeatHelper.addEmergencyExitSeatToBasketOnSpecificPassenger(null, passengerCodeWithoutInfant);
    }

    @When("^I send change association adult to infant on lap$")
    public void iSendChangeAssociationAdultToInfantOnLap() throws Throwable {
        Basket basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        String passengerInfant = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getPassengerDetails().getPassengerType().equalsIgnoreCase("infant")).findFirst().orElse(null).getCode();
        passengerInfantId = passengerInfant;
        assertThat(Objects.nonNull(passengerInfant))
                .withFailMessage("No infant passengers in basket")
                .isTrue();
        testData.setData(SERVICE, basketHelper.changeAssociationInfantAdult(testData.getPassengerId(), passengerInfant));
    }

    @And("^I see the association has not been changed$")
    public void iSeeTheAssociationHasNotBeenChanged() throws Throwable {
        Basket basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        String passengerWithOutInfant = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getPassengerDetails().getPassengerType().equalsIgnoreCase("adult") && !h.getCode().equalsIgnoreCase(testData.getPassengerId())).findFirst().orElse(null).getCode();
        AssociateInfantService associateInfantService = testData.getData(SERVICE);
        associateInfantService.assertThat().confirmationAssociatedInfant(basket, passengerWithOutInfant, passengerInfantId);
    }

    @When("^I am requesting to change a (.*) seat for (.*) on (.*) flight with (.*) seat already allocated$")
    public void iAmRequestingToChangeASeatFromSeatForPassengerMixWithSeatToSeatAlreadyAllocated(PurchasedSeatHelper.SEATPRODUCTS seatFrom, String passengerMix, String fare, PurchasedSeatHelper.SEATPRODUCTS seatTo) throws Throwable {
        testData.setValidationScenarios(true);

        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fare, false);
        purchasedSeatHelper.addPurchasedSeatToBasket(seatFrom);

        Basket basket = basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        String passengerCodeWithSeat = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> Objects.nonNull(h.getSeat())).findFirst().orElse(null).getCode();
        assertThat(Objects.nonNull(passengerCodeWithSeat))
                .withFailMessage("No passengers with seat in basket")
                .isTrue();
        purchasedSeatHelper.changePurchasedSeatAlreadyAllocated(seatTo, passengerCodeWithSeat);
        purchasedSeatHelper.invokeChangePurchasedSeatService(basket.getCode());
    }

    @Then("^I see error (.*) for failure changing$")
    public void iSeeErrorErrorForFailureChanging(String error) throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @When("^I added the seat (.*) and passenger detail with (.*) and (.*) and (.*)$")
    public void iAddedTheSeatAndPassengerDetailWithPassengerMixAndFareTypeAndSeat(String correctSeat, String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addSeatAndUpadatePassenger(correctSeat, passengerMix, fareType, aSeatProduct);
    }

    @When("^I updated passenger detail and add Seat (.*) with (.*) and (.*) and (.*)$")
    public void iUpdatedPassengerDetailAndAddSeatWrongSeatWithPassengerMixAndFareTypeAndSeat(String correctSeat, String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.updatePassengerAndAddSeat(correctSeat, passengerMix, fareType, aSeatProduct);
    }

    @When("^I added the seat with (.*) update passenger detail and commitBooking with (.*) and (.*) and (.*)$")
    public void iAddedTheSeatWithWrongSeatUpdatePassengerDetailAndCommitBookingWithPassengerMixAndFareTypeAndSeat(String correctSeat, String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        registerCustomerSteps.sendRegisterCustomerRequest(null);
        testData.setAccessToken(testData.getData(SerenityFacade.DataKeys.CUSTOMER_ACCESS_TOKEN));

        purchasedSeatHelper.upadatePassengerAndAddSeatWithCommmitBooking(correctSeat, passengerMix, fareType, aSeatProduct);
        testData.setData(SerenityFacade.DataKeys.BASKET_ID, testData.getBasketId());

        getPaymentMethodsForChannelSteps.getMockedCardPaymentMethod();
        // TODO check problem for commit booking "mismatch payment amount" with credit card
//        getPaymentMethodsForChannelSteps.getValidCardPaymentMethod();

        commitBookingSteps.sentCommitBookingRequest();
    }

    @Then("^I check that SSR is added (.*)$")
    public void iCheckThatSSRIsAdded(String parameter) throws Throwable {

        if (parameter.equals("in the Booking")) {
            final int[] attempt = {3};
            try {
                pollingLoop().until(
                        () -> {
                            getBookingSteps.sendGetBookingRequest();
                            testData.setBookingResponse(testData.getData(SerenityFacade.DataKeys.GET_BOOKING_RESPONSE));
                            GetBookingResponse.Booking tmpBooking = testData.getBookingResponse().getBookingContext().getBooking();
                            attempt[0]--;
                            return tmpBooking.getOutbounds().stream()
                                    .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                                    .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                                    .noneMatch(passenger -> passenger.getSpecialRequests().get(0).getSsrs().get(0).getSsrCode().isEmpty())
                                    && attempt[0] > 0;
                        }
                );
            } catch (ConditionTimeoutException ignored) {
                fail("The SSR is empty in the booking");
            }
            GetBookingResponse getBooking = testData.getBookingResponse();
            getBookingService = testData.getData(SerenityFacade.DataKeys.GET_BOOKING_SERVICE);
            getBookingService.assertThat().checkAddSSRInTheBooking(getBooking);
        } else {
            Basket basket = basketHelper.getBasketService().getResponse().getBasket();
            basketHelper.getBasketService().assertThat().checkAddSSRInBasket(basket);
        }
    }

    @When("^I send a request to associate adult to infant on lap with (.*) with (.*) and (.*) fare type$")
    public void iSendARequestToAssociateAdultToInfantOnLap(String passengerMix, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct, String fareType) throws Throwable {
        purchasedSeatHelper.addSeatWithAssociateInfant(passengerMix, aSeatProduct, fareType);
    }

    @Then("^I will check that the new passenger don't have seat$")
    public void iWillCheckThatTheNewPassengerDonTHaveSeat() throws Throwable {
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        basketHelper.getBasketService().assertThat().checkThatThePassengerNotHaveSeat(basket);
    }

    @When("^I add the purchased seat only for primary seat with (.*) and (.*) and (EXTRA_LEGROOM|UPFRONT|STANDARD)$")
    public void iAddThePurchasedSeatOnlyForPrimarySeatWithPassengerMixAndFareTypeAndSeat(String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addPurchasedSeatwithPrimarySeatOnly(passengerMix, aSeatProduct, fareType);
    }

    @Then("^booking request should contain standard and additional seats$")
    public void bookingRequestShouldContainStandardAndAdditionalSeats() throws Throwable {
        pollingLoop().ignoreExceptions().untilAsserted(
                () -> {
                    bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getChannel());

                    bookingHelper.getGetBookingService().assertThat().checkThatAllPassengerHaveSeatAndAdditionalFare
                            (bookingHelper.getGetBookingService().getResponse());
                });
    }

    @When("^I add the purchased seat only for additional seat with (.*) and (.*) and (.*)$")
    public void iAddThePurchasedSeatOnlyForAdditionalSeatWithPassengerMixAndFareTypeAndSeat(String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        purchasedSeatHelper.addPurchasedSeatwithAdditionalSeatOnly(passengerMix, aSeatProduct, fareType);
    }

    @When("^I add purchased seat only for additional seat with (.*) and (.*) and (.*) with (.*)$")
    public void iAddPurchasedSeatOnlyForAdditionalSeatWithPassengerMixAndFareTypeAndSeatWithAdditionalSeat(String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct, Integer additionalSeat) throws Throwable {
        purchasedSeatHelper.addPurchasedSeatwithAdditionalSeatOnly(passengerMix, aSeatProduct, fareType, additionalSeat);
    }

    @Then("^the additional seat product is added to the basket$")
    public void theAdditionalSeatProductIsAddedToTheBasket() throws Throwable {
        basketHelper.getBasket(purchasedSeatHelper.getPurchasedSeatService().getResponse().getOperationConfirmation().getBasketCode());
        basketHelper.getBasketService().assertThat().additionalSeatsArePurchasedForEachPassenger(testData.getPurchsedSeatRequestBody());
    }

    @When("^I add purchased seat (EXTRA_LEGROOM|UPFRONT|STANDARD) with additional seat (\\d+) to the booking$")
    public void iAddPurchasedSeatToTheBooking(SEATPRODUCTS seatproducts, int addlSeat) throws Throwable {
        purchasedSeatHelper.addPurchasedSeatWithAdditionalSeatToBasketForEachPassengerAndFlight(seatproducts, addlSeat);
        if (purchasedSeatHelper.getPurchasedSeatService().getStatusCode() != 200){
               throw new EasyjetCompromisedException("Couldn't find additional seat consecutive to the primary seat.");
        }
    }

    @When("^I add purchased seat (EXTRA_LEGROOM|UPFRONT|STANDARD) to the booking$")
    public void iAddPurchasedSeatSeatProductToTheBooking(SEATPRODUCTS aSeatProduct) throws Throwable {
        basketHelper.invokeGetBasket(testData.getBasketId(), testData.getChannel());
        testData.setSeatProductInBasket(aSeatProduct);
        purchasedSeatHelper.addPurchasedSeatToBasketForEachPassengerAndFlight(aSeatProduct, true);
        testData.setData(BASKET_SERVICE, basketHelper.getBasketService());
    }

    @When("^I change the purchased seat (EXTRA_LEGROOM|UPFRONT|STANDARD) to the passenger")
    public void iChangeThePurchasedSeatSeatProductToThePassenger(SEATPRODUCTS aSeatProduct) throws Throwable {

        testData.setSeatProductInBasket(aSeatProduct);
        basketHelper.invokeGetBasket(testData.getBasketId(), testData.getChannel());
        Basket basket = basketHelper.getBasketService().getResponse().getBasket();
        String passengerCodeWithSeat = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> Objects.nonNull(h.getSeat())).findFirst().orElse(null).getCode();
        assertThat(Objects.nonNull(passengerCodeWithSeat))
                .withFailMessage("No passengers with seat in basket")
                .isTrue();
        purchasedSeatHelper.changePurchasedSeatAlreadyAllocated(aSeatProduct, passengerCodeWithSeat, testData.getFlightKey());
        purchasedSeatHelper.invokeChangePurchasedSeat();
    }

    @And("^I have the basket updated to add purchased seat$")
    public void iHaveTheBasketUpdatedToAddPurchasedSeat() throws Throwable {
        basketHelper.invokeGetBasket(testData.getBasketId(), testData.getChannel());
    }

    @Then("^I should see seat number against the passenger$")
    public void iShouldSeeSeatNumberAgainstThePassenger() throws Throwable {
        basketHelper.invokeGetBasket(testData.getBasketId(), testData.getChannel());

        assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .allMatch(passenger -> passenger.getSeat().getSeatNumber().isEmpty())).withFailMessage("There is no seat associated to the passenger").isFalse();
    }

    @And("^the seat entry status should be (NEW|CHANGED|SAME)")
    public void theSeatEntryStatusShouldBe(String entryStatus) throws Throwable {

        assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .allMatch(passenger -> passenger.getSeat().getEntryStatus().equalsIgnoreCase(entryStatus))).withFailMessage("The entry status of the passenger's seat is not correct.").isTrue();

    }

    @And("^the seat active flag should be (TRUE|FALSE)$")
    public void theSeatActiveFlagShouldBe(Boolean active) throws Throwable {
        assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .allMatch(passenger -> passenger.getSeat().getActive())).withFailMessage("The entry status of the passenger's seat is not correct.").isEqualTo(active);
    }

    @And("^the basket totals should be updated$")
    public void theBasketTotalsShouldBeUpdated() throws Throwable {
        basketHelper.getBasketService().assertThat().seatEntitlementBasedOnFareType();
    }

    @And("^the price of the seat should be correct$")
    public void thePriceOfTheSeatShouldBeCorrect() throws Throwable {
        testData.getSeatingServiceHelper();

        if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("EXTRA_LEGROOM") && testData.getData(EJPLUS_MEMBERSHIPTYPE).toString().equalsIgnoreCase("customer"))
        {
            assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                    .map(AbstractFlights::getFlights)
                    .flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                    .flatMap(Collection::stream)
                    .allMatch(passenger -> passenger.getSeat().getPricing().getTotalAmountWithDebitCard().doubleValue() == testData.getSeatingServiceHelper().getResponse().getProducts().stream().filter(seat -> seat.getId().equalsIgnoreCase("1") && seat.getName().equalsIgnoreCase("Extra Legroom")).findFirst().get().getOfferPrices().getWithEJPlus().getStandard().getWithDebitCardFee().doubleValue())).withFailMessage("The passenger seat price is incorrect.").isTrue();
        }
        else if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("UPFRONT") && testData.getData(EJPLUS_MEMBERSHIPTYPE).toString().equalsIgnoreCase("customer"))
        {
            assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                    .map(AbstractFlights::getFlights)
                    .flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                    .flatMap(Collection::stream)
                    .allMatch(passenger -> passenger.getSeat().getPricing().getTotalAmountWithDebitCard().doubleValue() == testData.getSeatingServiceHelper().getResponse().getProducts().stream().filter(seat -> seat.getId().equalsIgnoreCase("2") && seat.getName().equalsIgnoreCase("Up Front")).findFirst().get().getOfferPrices().getWithEJPlus().getStandard().getWithDebitCardFee().doubleValue())).withFailMessage("The passenger seat price is incorrect.").isTrue();
        }
        else if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("STANDARD") && testData.getData(EJPLUS_MEMBERSHIPTYPE).toString().equalsIgnoreCase("customer"))
        {
            assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                    .map(AbstractFlights::getFlights)
                    .flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                    .flatMap(Collection::stream)
                    .allMatch(passenger -> passenger.getSeat().getPricing().getTotalAmountWithDebitCard().doubleValue() == testData.getSeatingServiceHelper().getResponse().getProducts().stream().filter(seat -> seat.getId().equalsIgnoreCase("3") && seat.getName().equalsIgnoreCase("Standard")).findFirst().get().getOfferPrices().getWithEJPlus().getStandard().getWithDebitCardFee().doubleValue())).withFailMessage("The passenger seat price is incorrect.").isTrue();
        }
        else if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("EXTRA_LEGROOM") && testData.getData(EJPLUS_MEMBERSHIPTYPE).toString().equalsIgnoreCase("staff"))
        {
            assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                    .map(AbstractFlights::getFlights)
                    .flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                    .flatMap(Collection::stream)
                    .allMatch(passenger -> passenger.getSeat().getPricing().getTotalAmountWithDebitCard().doubleValue() == (testData.getSeatingServiceHelper().getResponse().getProducts().stream().filter(seat -> seat.getId().equalsIgnoreCase("1") && seat.getName().equalsIgnoreCase("Extra Legroom")).findFirst().get().getOfferPrices().getWithEJPlus().getStaff().getWithDebitCardFee().doubleValue() - testData.getSeatingServiceHelper().getResponse().getProducts().stream().filter(seat -> seat.getId().equalsIgnoreCase("2") && seat.getName().equalsIgnoreCase("Up Front")).findFirst().get().getOfferPrices().getWithEJPlus().getStaff().getWithDebitCardFee().doubleValue()))).withFailMessage("The passenger seat price is incorrect.").isTrue();
        }
        else if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("UPFRONT") && testData.getData(EJPLUS_MEMBERSHIPTYPE).toString().equalsIgnoreCase("staff"))
        {
            assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                    .map(AbstractFlights::getFlights)
                    .flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                    .flatMap(Collection::stream)
                    .allMatch(passenger -> passenger.getSeat().getPricing().getTotalAmountWithDebitCard().doubleValue() == testData.getSeatingServiceHelper().getResponse().getProducts().stream().filter(seat -> seat.getId().equalsIgnoreCase("2") && seat.getName().equalsIgnoreCase("Up Front")).findFirst().get().getOfferPrices().getWithEJPlus().getStaff().getWithDebitCardFee().doubleValue())).withFailMessage("The passenger seat price is incorrect.").isTrue();
        }
        else if (testData.getSeatProductInBasket().toString().equalsIgnoreCase("STANDARD") && testData.getData(EJPLUS_MEMBERSHIPTYPE).toString().equalsIgnoreCase("staff"))
        {
            assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                    .map(AbstractFlights::getFlights)
                    .flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                    .flatMap(Collection::stream)
                    .allMatch(passenger -> passenger.getSeat().getPricing().getTotalAmountWithDebitCard().doubleValue() == testData.getSeatingServiceHelper().getResponse().getProducts().stream().filter(seat -> seat.getId().equalsIgnoreCase("3") && seat.getName().equalsIgnoreCase("Standard")).findFirst().get().getOfferPrices().getWithEJPlus().getStaff().getWithDebitCardFee().doubleValue())).withFailMessage("The passenger seat price is incorrect.").isTrue();
        }

    }

    @And("^I purchased a seat (.*) for ejPlus membership (.*) passenger (.*) for (.*) fare$")
    public void iPurchasedASeatSeatProductForEjPlusMembershipEjPlusCardNumberPassengerPassengerForFareTypeFare(SEATPRODUCTS seatproducts, String ejPlusMembershipType, String passengerMix, String fareType) throws Throwable {

        customerHelper.createCustomerAndLoginIt();
        basketHelper.myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fareType, false);

        testData.setData(EJPLUS_MEMBERSHIPTYPE, ejPlusMembershipType);
        MemberShipModel eJPlus = null;
        Passengers updatePassengers = travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse());
        if (ejPlusMembershipType.equalsIgnoreCase("customer")) {
            eJPlus = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        } else if (ejPlusMembershipType.equalsIgnoreCase("staff")) {
            eJPlus = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        }
        updatePassengers.getPassengers().get(0).getPassengerDetails().getName().setLastName(eJPlus.getLastname());
        updatePassengers.getPassengers().get(0).getPassengerDetails().setEjPlusCardNumber(eJPlus.getEjMemberShipNumber());
        updatePassengers.getPassengers().get(0).setPassengerAPIS(null);

        basketHelper.updatePassengersForChannel(updatePassengers, testData.getChannel(), basketHelper.getBasketService().getResponse()
                .getBasket()
                .getCode());

        testData.setSeatProductInBasket(seatproducts);
        purchasedSeatHelper.addPurchasedSeatToBasketForEachPassengerAndFlight(seatproducts, true);
        purchasedSeatHelper.getPurchasedSeatService().getResponse();

        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
    }
}
