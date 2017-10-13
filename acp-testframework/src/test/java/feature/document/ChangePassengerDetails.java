package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.fixture.hybris.helpers.AmendBasicDetailsHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendBasicDetailsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.response.InternalPaymentFundsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Profile;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.IllformedLocaleException;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;

/**
 * Created by giuseppecioce on 15/09/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class ChangePassengerDetails {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private AmendBasicDetailsHelper amendBasicDetailsHelper;

    @When("^I request to manage passenger with flag (true|false) and saved passenger code (true|false)$")
    public void iSendARequestToManagePassengerWithFlagAndSavedPassengerCodeToSaveAgainstTheCustomerProfile(boolean saveToCustomerProfile, boolean updateSavedPassengerCode) throws Throwable {
        testData.setData(STORE_SAVED_PASSENGER_CODE, true);
        Pair <Boolean, Boolean> pair = new Pair<>(saveToCustomerProfile, updateSavedPassengerCode);
        String amendableBasket = bookingHelper.getAmendableBasketWithSavedPassenger(CommonConstants.ONE_ADULT, CommonConstants.STANDARD, pair);
        testData.setData(BASKET_ID, amendableBasket);
    }

    @Then("^I see the the passenger details against the customer profile$")
    public void iSeeTheThePassengerDetailsAgainstTheCustomerProfile() throws Throwable {
        customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
        CustomerProfileService customerProfileService = testData.getData(SERVICE);
        customerProfileService.assertThat().theSavedPassengerHasBeenCreated(testData.getData(SAVED_PASSENGER_SIZE), testData.getData(STORE_SAVED_PASSENGER_CODE));
    }

    @When("^I send a request to update basic details with flag (true|false) and saved passenger code (true|false)$")
    public void iSendARequestToUpdateBasicDetailsWithFlagToSaveAgainstTheCustomerProfile(boolean saveToCustomerProfile, boolean savedPassengerCode) throws Throwable {
        testData.setData(STORE_SAVED_PASSENGER_CODE, true);
        String amendableBasket = bookingHelper.getAmendableBasketWithSavedPassenger(CommonConstants.ONE_ADULT, CommonConstants.STANDARD, new Pair<>(false, savedPassengerCode));
        testData.setData(BASKET_ID, amendableBasket);

        AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = getAmendableBasicDetailsRequestBody(saveToCustomerProfile, savedPassengerCode);
        amendBasicDetailsHelper.invokeAmendBasicDetails(amendableBasket, testData.getData(PASSENGER_CODES), amendBasicDetailsRequestBody);
    }

    @And("^I commit again the booking$")
    public void iCommitAgainTheBooking() throws Throwable {
        bookingHelper.commitBookingFromBasket(bookingHelper.getBasketHelper().getBasketResponse(testData.getData(BASKET_ID), testData.getChannel()));
    }

    @When("^I request to amend the passenger details (.*) stored in the customer profile$")
    public void iRequestToAmendThePassengerDetailsFieldStoredInTheCustomerProfile(String field) throws Throwable {
        if("EJPlus".equals(field)) {
            testData.setData(REQUIRED_EJPLUS, true);
        }
        testData.setData(UPDATE_PASSENGER_BASED_SAVED_PASSENGER, true);
        testData.setData(STORE_SAVED_PASSENGER_CODE, false);
        String amendableBasket = bookingHelper.getAmendableBasketWithSavedPassenger(CommonConstants.ONE_ADULT, CommonConstants.STANDARD, new Pair<>(false, true));
        testData.setData(BASKET_ID, amendableBasket);

        amendBasicDetailsHelper.invokeAmendBasicDetails(amendableBasket, testData.getData(PASSENGER_CODES), getBodyWithField(field));
        this.iCommitAgainTheBooking();
    }

    private AmendBasicDetailsRequestBody getAmendableBasicDetailsRequestBody(boolean saveToCustomerProfile, boolean savedPassengerCode) {
        Basket.Passenger passenger = bookingHelper.getBasketHelper().getBasketService().getResponse().getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).findFirst().orElseThrow(() -> new IllformedLocaleException("No passenger in the basket"));
        return AmendBasicDetailsRequestBody.builder()
                .name(Name.builder()
                        .firstName(passenger.getPassengerDetails().getName().getFirstName())
                        .title(passenger.getPassengerDetails().getName().getTitle())
                        .build()
                )
                .age(passenger.getAge())
                .saveToCustomerProfile(saveToCustomerProfile)
                .savedPassengerCode(savedPassengerCode ? testData.getData(SAVED_PASSENGER_CODE) : "")
                .build();
    }

    private AmendBasicDetailsRequestBody getBodyWithField(String field) {
        testData.setData(FIELD, field);
        AmendBasicDetailsRequestBody amendBasicDetailsRequestBody = getAmendableBasicDetailsRequestBody(true, true);
        Profile profile = bookingHelper.getCustomerProfileService().getResponse().getCustomer().getAdvancedProfile().getSavedPassengers().stream().findFirst().orElseThrow(() -> new IllformedLocaleException("No saved passenger found"));
        switch (field) {
            case "Title":
                testData.setData(OLD_VALUE_FIELD, profile.getTitle());
                amendBasicDetailsRequestBody.getName().setTitle("mrs");
                break;
            case "FirstName":
                testData.setData(OLD_VALUE_FIELD, profile.getFirstName());
                amendBasicDetailsRequestBody.getName().setFirstName("UpdateFirstName");
                break;
            case "Surname":
                testData.setData(OLD_VALUE_FIELD, profile.getLastName());
                amendBasicDetailsRequestBody.getName().setLastName("UpdateLastName");
                break;
            case "Age":
                testData.setData(OLD_VALUE_FIELD, profile.getAge().toString());
                amendBasicDetailsRequestBody.setAge(Integer.parseInt("20"));
                break;
            case "EJPlus":
                testData.setData(OLD_VALUE_FIELD, profile.getEjPlusCardNumber());
                MemberShipModel memberShipModel = testData.getData(MEMBERSHIP_MODEL);
                amendBasicDetailsRequestBody.setEjPlusCardNumber(memberShipModel.getEjMemberShipNumber());
                break;
            case "Email":
                testData.setData(OLD_VALUE_FIELD, profile.getEmail());
                amendBasicDetailsRequestBody.setEmail("updateemail@abctest.com");
                break;
            case "TelephoneNumber":
                testData.setData(OLD_VALUE_FIELD, profile.getPhoneNumber());
                amendBasicDetailsRequestBody.setPhoneNumber("09987485899");
                break;
            case "NIF":
                testData.setData(OLD_VALUE_FIELD, profile.getNifNumber());
                amendBasicDetailsRequestBody.setNifNumber("123456789");
                break;
            default:
                break;
        }
        return amendBasicDetailsRequestBody;
    }

    @Then("^the saved passenger details has been created (true|false) or update in the customer profile$")
    public void theSavedPassengerDetailsHasBeenCreatedCreatedOrUpdateInTheCustomerProfile(boolean isCreatedOrUpdate) throws Throwable {
        pollingLoop().untilAsserted(() -> {
            customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
            CustomerProfileService customerProfileService = testData.getData(SERVICE);
            customerProfileService.assertThat().theSavedPassengerHasBeenCreated(testData.getData(SAVED_PASSENGER_SIZE), isCreatedOrUpdate);
            if (!isCreatedOrUpdate) {
                Profile profile = customerProfileService.getResponse().getCustomer().getAdvancedProfile().getSavedPassengers().stream().findFirst().orElseThrow(() -> new IllformedLocaleException("No saved passenger found"));
                customerProfileService.assertThat().theSavedPassengerHasBeenUpdateInField(testData.getData(OLD_VALUE_FIELD), getNewValueForUpdatedField(profile));
            }
        });
    }

    private String getNewValueForUpdatedField(Profile profile) {
        String newValue = "";
        String field = testData.getData(FIELD);
        switch (field) {
            case "Title":
                newValue = profile.getTitle();
                break;
            case "FirstName":
                newValue = profile.getFirstName();
                break;
            case "Surname":
                newValue = profile.getLastName();
                break;
            case "Age":
                newValue = profile.getAge().toString();
                break;
            case "EJPlus":
                newValue = "Impossible proceed with this check in the customer profile";
                break;
            case "Email":
                newValue = profile.getEmail();
                break;
            case "TelephoneNumber":
                newValue = profile.getPhoneNumber();
                break;
            case "NIF":
                newValue = profile.getNifNumber();
                break;
            default:
                break;
        }
        return newValue;
    }
}
