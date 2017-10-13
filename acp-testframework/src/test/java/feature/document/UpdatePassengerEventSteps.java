package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.AmendBasicDetailsHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.EventMessageCreationHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendBasicDetailsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.AmendPassengerSSRService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;


/**
 *
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdatePassengerEventSteps {

   @Autowired
   private SerenityFacade testData;

   @Autowired
   private MembershipDao membershipDao;

   @Autowired
   private AmendBasicDetailsHelper amendBasicDetailsHelper;

   @Autowired
   private HybrisServiceFactory serviceFactory;

   @Autowired
   private BasketHelper basketHelper;

   @Autowired
   private BookingHelper bookingHelper;

   @Autowired
   private EventMessageCreationHelper eventMessageCreationHelper;

   @Steps
   private CommitBookingSteps commitBookingSteps;

   private AmendPassengerSSRService amendPassengerSSRService;


   @And("^I created a successful booking for (.*)$")
   public void theChannelHasInitiatedACommitBooking(String passengerMix) throws Throwable {
      BookingConfirmationResponse bookingResponse = bookingHelper.createNewBooking(testData.getChannel(), passengerMix);
      testData.setData(BOOKING_ID, bookingResponse.getBookingConfirmation().getBookingReference());
   }

   @When("^passenger (.*) requests to update their last name to (.*) and first name to (.*) and age to (.*)$")
   public void passengerRequestsToUpdateTheirLastNameTo(int passengerIndex, String lastName , String firstName, int age) throws Throwable {
      Name name = Name.builder()
            .lastName(lastName)
            .firstName(firstName)
            .title("mr")
            .build();

      AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder()
            .name(name)
            .age(age)
            .build();

      invokeAmendDetails(passengerIndex,amendBasicDetailsRequestBody);
   }

   @When("^passenger (.*) requests to update their nifNumber to (.*)$")
   public void passengerRequestsToUpdateTheirNifToNif(int passengerIndex, int nifNumber) {
      AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder()
            .nifNumber(String.valueOf(nifNumber))
            .build();

      invokeAmendDetails(passengerIndex,amendBasicDetailsRequestBody);
   }

   @When("^passenger (.*) requests to update their telephone to (.*)$")
   public void passengerPassengerIndexRequestsToUpdateTheirTelephoneToTelephone(int passengerIndex, String phoneNumber) throws Throwable {
      AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder()
            .phoneNumber(phoneNumber)
            .build();

      invokeAmendDetails(passengerIndex,amendBasicDetailsRequestBody);
   }

   @When("^passenger (.*) requests to update their email to (.*)$")
   public void passengerPassengerIndexRequestsToUpdateTheirEmailToEmail(int passengerIndex, String email) {
      AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder()
            .email(email)
            .build();

      invokeAmendDetails(passengerIndex,amendBasicDetailsRequestBody);
   }

   @When("^passenger (.*) requests to update their Ej plus number$")
   public void passengerPassengerIndexRequestsToUpdateTheirEjPlusNumber(int passengerIndex) throws EasyjetCompromisedException {
      MemberShipModel ejPlusMemberBasedOnStatus = membershipDao.getRandomValueForValidEJPlus();

      Name name = Name.builder()
            .lastName(ejPlusMemberBasedOnStatus.getLastname())
            .firstName(ejPlusMemberBasedOnStatus.getFirstname())
            .title("mr")
            .build();

      AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder()
            .name(name)
            .ejPlusCardNumber(ejPlusMemberBasedOnStatus.getEjMemberShipNumber())
            .build();

      invokeAmendDetails(passengerIndex,amendBasicDetailsRequestBody);
   }

   private void invokeAmendDetails(int passengerIndex, AmendBasicDetailsRequestBody amendBasicDetailsRequestBody) {
      String amendableBasketCode = testData.getAmendableBasket();

      Basket amendableBasket = basketHelper.getBasket(amendableBasketCode, "ADAirport");

      amendBasicDetailsHelper.invokeAmendBasicDetails(amendableBasketCode,
            getOutBoundPassengers(amendableBasket).get(passengerIndex - 1).getCode(), amendBasicDetailsRequestBody);
   }

   private List<Basket.Passenger> getOutBoundPassengers(Basket basket) {
      return basket.getOutbounds().stream()
            .flatMap(obs -> obs.getFlights().stream())
            .flatMap(pgs -> pgs.getPassengers().stream())
            .collect(Collectors.toList());
   }

   @And("^passenger (.*) requests to update their (.*), Ej plus number, (.*), (.*), (.*)$")
   public void passengerPassengerIndexRequestsToUpdateTheirAgeEjPlusNumberEmailTelephoneNifAndSSRsWithTheFollowing(int passengerIndex,
         int age, String email, String phoneNumber, String nifNumber) throws Throwable {

      //update name, surname and ejplus
      MemberShipModel ejPlusMemberBasedOnStatus = membershipDao.getRandomValueForValidEJPlus();

      Name name = Name.builder()
            .lastName(ejPlusMemberBasedOnStatus.getLastname())
            .firstName(ejPlusMemberBasedOnStatus.getFirstname())
            .title("mr")
            .build();

      AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder()
            .name(name)
            .phoneNumber(phoneNumber)
            .age(age)
            .nifNumber(String.valueOf(nifNumber))
            .email(email)
            .ejPlusCardNumber(ejPlusMemberBasedOnStatus.getEjMemberShipNumber())
            .build();

      invokeAmendDetails(passengerIndex,amendBasicDetailsRequestBody);
   }
}
