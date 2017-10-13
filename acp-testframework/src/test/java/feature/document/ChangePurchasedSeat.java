package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PaymentServiceResponseHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.PurchasedSeatService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_SERVICE;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;

/**
 * Created by giuseppecioce on 10/05/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class ChangePurchasedSeat {

    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private PaymentServiceResponseHelper paymentServiceResponseHelper;
    @Autowired
    private BasketHelper basketHelper;
    private String priceChangeForMoveSeat;


    @And("^I am requesting to change a purchased seat for \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\" with fare type \"([^\"]*)\"$")
    public void iAmRequestingToChangeAPurchasedSeatForFromToWithFareType(String passenger, PurchasedSeatHelper.SEATPRODUCTS seatFrom, PurchasedSeatHelper.SEATPRODUCTS seatTo, String fare) throws Throwable {
        testData.setFareType(fare);
        purchasedSeatHelper.preRequirementsForChangePurchasedSeat(passenger, fare, seatFrom, seatTo);
    }

    @But("^the request missing in the mandatory field \"([^\"]*)\"$")
    public void theRequestMissingInTheMandatoryField(String field) throws Throwable {
        purchasedSeatHelper.removeFieldFromRequestBody(field, "CHANGE");
    }

    @When("^I send the move seat product request$")
    public void iSendTheMoveSeatProductRequest() throws Throwable {
        purchasedSeatHelper.invokeChangePurchasedSeatService();
    }

    @Then("^I will generate the error message \"([^\"]*)\"$")
    public void iWillGenerateTheErrorMessage(String error) throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Then("^I will receive the successful response$")
    public void iWillReceiveTheSuccessfulResponse() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifySuccessfullyResponseAfterRemoveSeat(purchasedSeatHelper.getBasketCode());
    }

    @And("^I will verify the new seating configuration of passenger with applied discount \"([^\"]*)\"$")
    public void iWillVerifyTheNewSeatingConfigurationOfPassengerWithAppliedDiscount(boolean applyDiscount) throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verfiyNewConfigurationHasBeenStored(purchasedSeatHelper.getBasketAfterChangingPurchasedSeat(), purchasedSeatHelper.getNewAssociationPassengerSeat(), applyDiscount ? purchasedSeatHelper.getDiscountForFlexiFare() : 0.0);
    }

    @And("^I will verify the new passenger totals price$")
    public void iWillVerifyTheNewPassengerTotalsPrice() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPriceForPassengerHasBeenUpdateAfterChanging(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat(), purchasedSeatHelper.getBasketAfterChangingPurchasedSeat(), purchasedSeatHelper.getSeatTotalPrice(), purchasedSeatHelper.getPassengerCode());
    }

    @And("^I will verify the new basket totals$")
    public void iWillVerifyTheNewBasketTotals() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPriceForBasketHasBeenUpdateAfterChanging(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat(), purchasedSeatHelper.getBasketAfterChangingPurchasedSeat(), purchasedSeatHelper.getSeatTotalPrice());
    }

    @And("^I will verify the new flight totals price$")
    public void iWillVerifyTheNewFlightTotalsPrice() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPriceForFlightHasBeenUpdateAfterChanging(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat(), purchasedSeatHelper.getBasketAfterChangingPurchasedSeat(), purchasedSeatHelper.getSeatTotalPrice());
    }

    @And("^I will verify any associated products with the old purchased seat has been removed$")
    public void iWillVerifyAnyAssociatedProductsWithTheOldPurchasedSeatHasBeenRemoved() throws Throwable {
        // Impossible to very this kind of stuff because the change seat automatically and immediately delete and add a purchased seat
        // So the configuration of the product does not change
    }

    @And("^I will verify any associated products with the new purchased seat has been added$")
    public void iWillVerifyAnyAssociatedProductsWithTheNewPurchasedSeatHasBeenAdded() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyTheNewProductHasBeenStored(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat().getBasket(), purchasedSeatHelper.getBasketAfterChangingPurchasedSeat(), testData.getFareType().equalsIgnoreCase(STANDARD) ? 1 : 0);
    }

    @And("^I will verify any associated products with the new purchased seat has been added with \"([^\"]*)\" product$")
    public void iWillVerifyAnyAssociatedProductsWithTheNewPurchasedSeatHasBeenAddedWithProduct(int item) throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyTheNewProductHasBeenStored(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat().getBasket(), purchasedSeatHelper.getBasketAfterChangingPurchasedSeat(), item);
    }


    @And("^I sent a request to change seat from (.*) to (.*) for (.*) on (.*) fare flight with price (.*)$")
    public void iSentARequestToChangeSeatFromSeatFromToSeatToWithPriceTypePrice(PurchasedSeatHelper.SEATPRODUCTS seatFrom, PurchasedSeatHelper.SEATPRODUCTS seatTo, String passenger, String fare, String typeOfChange) throws Throwable {
        priceChangeForMoveSeat = typeOfChange;

        purchasedSeatHelper.preRequirementsForChangePurchasedSeat(passenger, fare, seatFrom, seatTo);
        paymentServiceResponseHelper.createBasicBookingRequestAndCommitIt();
        paymentServiceResponseHelper.sendSuccessCommitBookingRequest();

        String bookingRef = paymentServiceResponseHelper.getCommitBookingService().getResponse().getBookingConfirmation().getBookingReference();
        String amendableBasketCode = purchasedSeatHelper.getBasketHelper().createAmendableBasket(bookingRef);

        purchasedSeatHelper.invokeChangePurchasedSeatService(amendableBasketCode);
        purchasedSeatHelper.getPurchasedSeatService().getResponse();
    }

    @Then("^I want the new purchased seat into the basket with the new price$")
    public void iWantTheNewPurchasedSeatIntoTheBasketWithTheNewPrice() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPriceForPassengerHasBeenUpdateAfterChanging(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat(), purchasedSeatHelper.getBasketAfterChangingPurchasedSeat(), purchasedSeatHelper.getSeatTotalPrice(), purchasedSeatHelper.getPassengerCode());
    }

    @And("^I want an updated basket totals$")
    public void iWantAnUpdatedBasketTotals() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPriceChangeForMoveSeat(purchasedSeatHelper.getAssociationPassengerSeat(), purchasedSeatHelper.getNewAssociationPassengerSeat(), priceChangeForMoveSeat);
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifyPriceForBasketHasBeenUpdateAfterChanging(purchasedSeatHelper.getBasketAfterAddingPurchasedSeat(), purchasedSeatHelper.getBasketAfterChangingPurchasedSeat(), purchasedSeatHelper.getSeatTotalPrice());
    }

    @And("^I dont want the old purchased seat into the basket$")
    public void iDontWantTheOldPurchasedSeatIntoTheBasket() throws Throwable {
        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifySeatRemoveAfterChange(purchasedSeatHelper.getAssociationPassengerSeat(), purchasedSeatHelper.getNewAssociationPassengerSeat());
    }

    @And("^I change booking with (.*) additional seat with (.*) and (.*) and (.*)$")
    public void iChangeMyToSeatTo(Integer additionalSeat, String passengerMix, String fareType, PurchasedSeatHelper.SEATPRODUCTS aSeatProduct) throws Throwable {
        BasketService basketService = testData.getData(BASKET_SERVICE);
        testData.setData("oldBasket", basketService);
        purchasedSeatHelper.changeSeatWithAdditionalSeat(passengerMix, aSeatProduct, additionalSeat);
        basketHelper.getBasket(testData.getAmendableBasket());
        BasketService basketServiceUpdated = testData.getData(BASKET_SERVICE);
        testData.setData("amendedBasket", basketServiceUpdated);
    }

    @Then("^I see new purchased seat and additional seat added$")
    public void iSeeNewPurchasedSeatBeenAdded() throws Throwable {
        BasketService basketServiceUpdated = (BasketService) testData.getData("amendedBasket");
        List<AbstractPassenger.AdditionalSeat> updatedAdditionalSeats = basketServiceUpdated.getResponse().getBasket().getOutbounds()
                .stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null).getPassengers().stream().findFirst().orElse(null).getAdditionalSeats();
        AbstractPassenger.Seat updatedSeat = basketServiceUpdated.getResponse().getBasket().getOutbounds().
                stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null).getPassengers().stream().findFirst().orElse(null).getSeat();
        String updatedAdditionalSeatNo = updatedAdditionalSeats.stream().findFirst().orElse(null).getSeat().getSeatNumber();
        String purchasedSeatNumber = updatedSeat.getSeatNumber();
        PurchasedSeatService purchasedSeatService = (PurchasedSeatService) testData.getData("purchasedSeatService");
        purchasedSeatService.assertThat().assertNotNull(updatedAdditionalSeatNo);
        purchasedSeatService.assertThat().assertNotNull(purchasedSeatNumber);
    }

    @Then("^I see previous purchased seat and additional seat removed$")
    public void iSeePreviousPurchasedSeatAndAdditionalSeatRemoved() throws Throwable {

        purchasedSeatHelper.getPurchasedSeatService().assertThat().verifySeatRemoveAfterChange(purchasedSeatHelper.getAssociationPassengerSeat(), purchasedSeatHelper.getNewAssociationPassengerSeat());


        BasketService basketServiceUpdated = (BasketService) testData.getData("amendedBasket");
        BasketService committedBasketResponse = (BasketService) testData.getData("oldBasket");

        AbstractPassenger.Seat updatedSeat = basketServiceUpdated.getResponse().getBasket().getOutbounds().
                stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null).getPassengers().stream().findFirst().orElse(null).getSeat();
        String purchasedSeatNumber = updatedSeat.getSeatNumber();
        List<AbstractPassenger.AdditionalSeat> updatedAdditionalSeats = basketServiceUpdated.getResponse().getBasket().getOutbounds()
                .stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null).getPassengers().stream().findFirst().orElse(null).getAdditionalSeats();
        String updatedAdditionalSeatNo = updatedAdditionalSeats.stream().findFirst().orElse(null).getSeat().getSeatNumber();


        List<AbstractPassenger.AdditionalSeat> additionalSeats = committedBasketResponse.getResponse().getBasket().getOutbounds().stream().findFirst().orElse(null).getFlights().
                stream().findFirst().orElse(null).getPassengers().stream().findFirst().orElse(null).getAdditionalSeats();
        AbstractPassenger.Seat seat = committedBasketResponse.getResponse().getBasket().getOutbounds().stream().findFirst().orElse(null).getFlights().stream().findFirst().orElse(null)
                .getPassengers().stream().findFirst().orElse(null).getSeat();
        PurchasedSeatService purchasedSeatService = (PurchasedSeatService) testData.getData("purchasedSeatService");

        purchasedSeatService.assertThat().assertFalse(seat.getSeatNumber().equals(purchasedSeatNumber));
        purchasedSeatService.assertThat().assertFalse(additionalSeats.stream().findFirst().orElse(null).getSeat().getSeatNumber().equals(updatedAdditionalSeatNo));

    }

    @When("^I try to change booking with out providing seating information$")
    public void iTryToChangeBookingWithOutProvidingSeatingInformation() throws Throwable {
        purchasedSeatHelper.changePurchasedSeatAdditionalSeatToNull(PurchasedSeatHelper.SEATPRODUCTS.UPFRONT, 1);
    }

    @Then("^I see error for missing seat and additional seat numbers (.*)$")
    public void iSeeAndErrorMissingSeatAndAdditionalSeatNumbers(String error) throws Throwable {
        PurchasedSeatService purchasedSeatService = (PurchasedSeatService) testData.getData("purchasedSeatService");
        if(error.contains(",")) {
            String[] splitted = error.split(",");
            purchasedSeatService.assertThatErrors().containedTheCorrectErrorMessage(splitted);
        } else {
            purchasedSeatService.assertThatErrors().containedTheCorrectErrorMessage(error);
        }
    }

    @When("^I try to change booking with already allocated seat$")
    public void iTryToChangeBookingWithAlreadyAllocatedSeat() throws Throwable {
        purchasedSeatHelper.changePurchasedSeatAdditionalSeatsToAlreadyAllocated(PurchasedSeatHelper.SEATPRODUCTS.UPFRONT, 1);
    }
}
