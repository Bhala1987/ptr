package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.ManageBookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by giuseppecioce on 29/06/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class ManageEJPlusOnBookingSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private ManageBookingHelper manageBookingHelper;
    private String manageEJPlus;
    private int cabinItemSize;

    @And("^I committed booking (.*) with purchased seat (.*) for (.*) with (.*) EJPlus$")
    public void iCommittedBookingWithPurchasedSeatForWithEJPlus(String fare, PurchasedSeatHelper.SEATPRODUCTS seat, String passengerMix, boolean withEJPlus) throws Throwable {
        testData.setFareType(fare);
        manageBookingHelper.setMembership(null);

        purchasedSeatHelper.getBasketHelper().myBasketContainsAFlightWithPassengerMix(passengerMix, testData.getChannel(), fare, false);
        purchasedSeatHelper.preRequirementsForRemovePurchasedSeat(seat);

        bookingHelper.createBasicBookingRequestWithEJPlusDetailsAndCommitIt(withEJPlus, false);

        String bookingRef = bookingHelper.getCommitBookingService().getResponse().getBookingConfirmation().getBookingReference();
        String amendableBasketCode = purchasedSeatHelper.getBasketHelper().createAmendableBasket(bookingRef);
        Basket basket = bookingHelper.getBasketHelper().getBasket(amendableBasketCode, testData.getChannel());
        testData.setBasketId(amendableBasketCode);
        cabinItemSize = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> h.getCode().equalsIgnoreCase(testData.getPassengerId())).findFirst().orElse(null).getCabinItems().size();
    }

    @When("^I send update passenger details for (.*)$")
    public void ISendUpdatePassengerDetailsFor(String field) throws Throwable {
        manageBookingHelper.manageUpdateBasicPassengerDetails(field);
    }

    @Then("^the eJ Plus Number has been (.*) to the passenger$")
    public void theEJPlusNumberHasBeenToThePassenger(String updateEJPlus) throws Throwable {
        manageEJPlus = updateEJPlus;
        switch (updateEJPlus) {
            case "added":
                manageBookingHelper.getAmendBasicDetailsService().assertThat().verifyEJPlusHasBeenUpdated(manageBookingHelper.getBasketAfterUpdateMembership(), manageBookingHelper.getMembership().getEjMemberShipNumber(), testData.getPassengerId());
                break;
            case "removed":
                manageBookingHelper.getAmendBasicDetailsService().assertThat().verifyEJPlusHasBeenRemoved(manageBookingHelper.getBasketAfterUpdateMembership(), testData.getPassengerId());
                break;
            default:
                break;
        }
    }

    @And("^the products associated with the eJ Plus Bundle has been (.*) to the basket$")
    public void theProductsAssociatedWithTheEJPlusBundleHasBeenAddedToTheBasket(String updateEJPlus) throws Throwable {
        manageBookingHelper.getAmendBasicDetailsService().assertThat().verifyProductBundleHasBeenUpdate(manageBookingHelper.getBasketAfterUpdateMembership(), updateEJPlus, testData.getFareType(), testData.getPassengerId(), cabinItemSize);
    }

    @And("^I will adjust the price of the purchased seat$")
    public void iWillAdjustThePriceOfThePurchasedSeat() throws Throwable {
        String purchasedSeatName = purchasedSeatHelper.initSeatName(testData.getTypeOfSeat());
        double basePriceForSeat = testData.getSeatingServiceHelper().getResponse().getProducts().stream().filter(f -> f.getName().equalsIgnoreCase(purchasedSeatName)).findFirst().orElse(null).getBasePrice();
        switch (manageEJPlus) {
            case "added":
                manageBookingHelper.getAmendBasicDetailsService().assertThat().verifyProductPriceAfterUpdate(manageBookingHelper.getBasketAfterUpdateMembership(), 0, 0, testData.getPassengerId());
                break;
            case "removed":
                manageBookingHelper.getAmendBasicDetailsService().assertThat().verifyProductPriceAfterUpdate(manageBookingHelper.getBasketAfterUpdateMembership(), basePriceForSeat, testData.getSeatDiscountForFare(),testData.getPassengerId());
                break;
            default:
                break;
        }
    }

    @And("^a message (.*) has been returned to the channel providing another card$")
    public void aMessageHasBeenReturnedToTheChannelProvidingAnotherCard(String warning) throws Throwable {
        manageBookingHelper.getAmendBasicDetailsService().assertThat().verifyWarningMessage(warning, true);
    }

    @And("^the basket total has been updated$")
    public void theBasketTotalHasBeenUpdated() throws Throwable {
        manageBookingHelper.getAmendBasicDetailsService().assertThat().verifyTotalBasketHasBeenUpdated(manageBookingHelper.getBasketAfterUpdateMembership(), purchasedSeatHelper.getBasketAfterAddingPurchasedSeat().getBasket(), manageEJPlus);
    }
}
