package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.UpdatePassengerDetailsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendBasicDetailsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AmendBasicDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.AmendBasicDetailsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rajakm on 14/06/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class ManageBookingSteps {
    protected static Logger LOG = LogManager.getLogger(ManageBookingSteps.class);
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private ManageBookingHelper manageBookingHelper;
    @Autowired
    private AmendableBasketHelper amendableBasketHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private BasketPathParams basketPathParams;
    private AmendBasicDetailsRequestBody.AmendBasicDetailsRequestBodyBuilder amendBasicDetailsRequestBody;
    private AmendBasicDetailsService amendBasicDetailsService;
    @Autowired
    private MembershipDao membershipDao;

    private String passengerCode;
    private String expectedPassengerType;
    private int expectedPassengerAge;
    private String allRelatedFlights = "false";
    private String bookingRef;
    private Double originalAdminFee;


    @And("^I have initiated a change passenger age action from (.+) to (.+) for single flight$")
    public void i_have_initiated_a_change_passenger_age_action_from_to_for_single_flight(String fromPassengerType, String toPassengerType) {
        manageBookingHelper.amendBooking();
        manageBookingHelper.getBasketInEditMode();
        if (fromPassengerType.contains("OS")) {
            fromPassengerType = "infant";
        }
        passengerCode = manageBookingHelper.getPassengerCodeInTheBasket(fromPassengerType);
        expectedPassengerType = toPassengerType.split("-")[0].trim();
        expectedPassengerAge = Integer.valueOf(toPassengerType.split("-")[1].trim());
        manageBookingHelper.amendPassengerAgeAndType(passengerCode, expectedPassengerAge, allRelatedFlights);
    }

    @And("^I have initiated a change passenger age action from (.+) to (.+) for all flights$")
    public void i_have_initiated_a_change_passenger_age_action_from_to_for_all_flights(String fromPassengerType, String toPassengerType) {
        allRelatedFlights = "true";
        manageBookingHelper.amendBooking();
        manageBookingHelper.getBasketInEditMode();
        if (fromPassengerType.contains("OS")) {
            fromPassengerType = "infant";
        }
        passengerCode = manageBookingHelper.getPassengerCodeInTheBasket(fromPassengerType);
        expectedPassengerType = toPassengerType.split("-")[0].trim();
        expectedPassengerAge = Integer.valueOf(toPassengerType.split("-")[1].trim());
        manageBookingHelper.amendPassengerAgeAndType(passengerCode, expectedPassengerAge, allRelatedFlights);
    }

    @When("^I send an invalid change passenger request from (.+) to (.+)$")
    public void i_send_an_invalid_change_passenger_request_from_to(String fromPassengerType, String toPassengerType) {
        manageBookingHelper.amendBooking();
        manageBookingHelper.getBasketInEditMode();
        passengerCode = manageBookingHelper.getPassengerCodeInTheBasket(fromPassengerType);
        expectedPassengerType = toPassengerType.split("-")[0].trim();
        expectedPassengerAge = (Integer.valueOf(toPassengerType.split("-")[1].trim()));
        expectedPassengerAge = -expectedPassengerAge;
        manageBookingHelper.amendPassengerAgeAndType(passengerCode, expectedPassengerAge, allRelatedFlights);
    }

    @When("^I process the request for change passenger age$")
    public void i_process_the_request_for_change_passenger_age() {
        manageBookingHelper.invokeAmendBasicDetailsService();
        manageBookingHelper.verifyAmendBookingIsSuccessful();
    }

    @Then("^I will validate the request is in the expected format$")
    public void i_will_validate_the_request_is_in_the_expected_format() {
        manageBookingHelper.verifyAmendBookingIsSuccessful();
    }

    @Then("^I will update the passenger age with the new value$")
    public void i_will_update_the_passenger_age_with_the_new_value() {
        manageBookingHelper.getPassengerAgeAndType(passengerCode);
        manageBookingHelper.verifyPassengerAge(expectedPassengerAge);
    }

    @And("^I will update the passenger type with the new value$")
    public void i_will_update_the_passenger_type_with_the_new_value() {
        manageBookingHelper.getPassengerAgeAndType(passengerCode);
        manageBookingHelper.verifyPassengerType(expectedPassengerType);
    }

    @Then("^I will update the passenger age for all the flights with the new value$")
    public void i_will_update_the_passenger_age_for_all_the_flights_with_the_new_value() {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(passengerCode);
        for (String aPassengerMap : passengerMap) {
            manageBookingHelper.getPassengerAgeAndType(aPassengerMap);
            manageBookingHelper.verifyPassengerAge(expectedPassengerAge);
        }
    }

    @And("^I will update the passenger type for all the flights with the new value$")
    public void i_will_update_the_passenger_type_for_all_the_flights_with_the_new_value() {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(passengerCode);
        for (String aPassengerMap : passengerMap) {
            manageBookingHelper.getPassengerAgeAndType(aPassengerMap);
            manageBookingHelper.verifyPassengerType(expectedPassengerType);
        }
    }

    @When("^the new passenger age does not change the passenger type$")
    public void the_new_passenger_age_does_not_change_the_passenger_type() {
        manageBookingHelper.getPassengerAgeAndType(passengerCode);
        manageBookingHelper.verifyPassengerType(expectedPassengerType);
    }

    @And("^I will receive the confirmation$")
    public void i_will_receive_the_confirmation() {
        manageBookingHelper.verifyAmendBookingIsSuccessful();
    }

    @Then("^I will get an (.+) error message$")
    public void i_will_get_an_error_message(String error) {
        manageBookingHelper.invokeAmendBasicDetailsService();
        manageBookingHelper.verifyAmendBookingIsNotSuccessful(error);
    }

    @And("^I will recalculate the fees and taxes like \"([^\"]*)\"$")
    public void i_will_recalculate_the_fees_and_taxes(String tax) throws EasyjetCompromisedException {
        manageBookingHelper.verifyTaxesAndFeesExist(passengerCode, expectedPassengerType, testData.getChannel(), tax);
    }

    @And("^I will update basket totals$")
    public void i_will_update_basket_totals() {
        manageBookingHelper.verifyBasketTotalAfterAmendBooking();
    }

    @And("^I do not change the passenger status$")
    public void i_do_not_change_the_passenger_status() {
        manageBookingHelper.verifyPassengerStatusAfterAmendBooking(passengerCode);
    }

    @And("^I do not change the passengers APIS status$")
    public void i_do_not_change_the_passengers_apis_status() {
        manageBookingHelper.verifyPassengerAPISStatusAfterAmendBooking(passengerCode);
    }

    @And("^I do not change the passengers ICTS status$")
    public void i_do_not_change_the_passengers_icts_status() {
        manageBookingHelper.verifyPassengerICTSStatusAfterAmendBooking(passengerCode);
    }

    @And("^I do not change the passenger status for all the flights$")
    public void i_do_not_change_the_passenger_status_for_all_the_flights() {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(passengerCode);
        for (String aPassengerMap : passengerMap) {
            manageBookingHelper.verifyPassengerStatusAfterAmendBooking(aPassengerMap);
        }
    }

    @And("^I do not change the passengers APIS status for all the flights$")
    public void i_do_not_change_the_passengers_apis_status_for_all_the_flights() {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(passengerCode);
        for (String aPassengerMap : passengerMap) {
            manageBookingHelper.verifyPassengerAPISStatusAfterAmendBooking(aPassengerMap);
        }
    }

    @And("^I do not change the passengers ICTS status for all the flights$")
    public void i_do_not_change_the_passengers_icts_status_for_all_the_flights() {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(passengerCode);
        for (String aPassengerMap : passengerMap) {
            manageBookingHelper.verifyPassengerICTSStatusAfterAmendBooking(aPassengerMap);
        }
    }

    @And("^I will return \"([^\"]*)\" message$")
    public void i_will_return_something_message(String additionalMessage) {
        manageBookingHelper.verifyAmendBasicDetailsAdditionalMessage(additionalMessage);
    }

    @When("^I request an amendable basket during manage booking$")
    public void i_request_an_amendable_basket_during_manage_booking() {
        manageBookingHelper.amendBooking();
        manageBookingHelper.getBasketInEditMode();
    }

    @And("^the currency in the amendable basket should be same as booking currency$")
    public void the_currency_in_the_amendable_basket_should_be_same_as_booking_currency() {
        manageBookingHelper.verifyAmendableBasketCurrency();
    }

    @Then("^I will remove the bundle associated to the passenger$")
    public void i_will_remove_the_bundle_associated_to_the_passenger() {
        manageBookingHelper.verifyExistingBundleHasRemoved(passengerCode);
    }

    @Then("^I will remove any products with tax and fees associated to the passenger$")
    public void i_will_remove_any_products_with_tax_and_fees_associated_to_the_passenger() {
        manageBookingHelper.verifyExistingProductHasRemoved(passengerCode);
    }

    @Then("^I will add an infant on lap product to the basket$")
    public void i_will_add_an_infant_on_lap_product_to_the_basket() {
        manageBookingHelper.verifyInfantOnLapProductIsAdded(passengerCode, expectedPassengerType);
    }

    @Then("^I will assign the infant on lap to first Adult$")
    public void i_will_assign_the_infant_on_lap_to_first_Adult() {
        manageBookingHelper.verifyInfantOnLapIsAssociatedToAdult(passengerCode);
    }

    @Then("^I will remove the bundle associated to the passenger for all flights$")
    public void i_will_remove_the_bundle_associated_to_the_passenger_for_all_flights() {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(passengerCode);
        for (String aPassengerMap : passengerMap) {
            manageBookingHelper.verifyExistingBundleHasRemoved(aPassengerMap);
        }
    }

    @Then("^I will remove any products with tax and fees associated to the passenger for all flights$")
    public void i_will_remove_any_products_with_tax_and_fees_associated_to_the_passenger_for_all_flights() {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(passengerCode);
        for (String aPassengerMap : passengerMap) {
            manageBookingHelper.verifyExistingProductHasRemoved(aPassengerMap);
        }
    }

    @Then("^I will add an infant on lap product to the basket for all flights$")
    public void i_will_add_an_infant_on_lap_product_to_the_basket_for_all_flights() {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(passengerCode);
        for (String aPassengerMap : passengerMap) {
            manageBookingHelper.verifyInfantOnLapProductIsAdded(aPassengerMap, expectedPassengerType);
        }
    }

    @Then("^I will assign the infant on lap to first Adult for all flights$")
    public void i_will_assign_the_infant_on_lap_to_Adult_for_all_flights() {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(passengerCode);
        for (String aPassengerMap : passengerMap) {
            manageBookingHelper.verifyInfantOnLapIsAssociatedToAdult(aPassengerMap);
        }
    }

    @When("^I process this request for change passenger age$")
    public void i_process_this_request_for_change_passenger_age() {
        manageBookingHelper.invokeAmendBasicDetailsService();
    }

    @Then("^I will return an (.+) that Adult to infant on lap ratio is violated$")
    public void i_will_return_an_that_adult_to_infant_on_lap_ratio_is_violated(String error) {
        manageBookingHelper.verifyAmendBookingIsNotSuccessful(error);
    }

    @And("^I have initiated change eJplus number for (\\d+) st passenger$")
    public void i_have_initiated_change_ejplus_number_for_something_st_passenger(int passengerIndex) {
        manageBookingHelper.amendBooking();
        manageBookingHelper.getBasketInEditMode();
        testData.setPassengerId(manageBookingHelper.getPassengerCodeInTheBasket(passengerIndex));
    }

    @And("^I have initiated change eJplus number with incorrect passenger id \"([^\"]*)\"$")
    public void i_have_initiated_change_ejplus_number_with_incorrect_passenger_id_something(String incorrectPaxId) {
        manageBookingHelper.amendBooking();
        manageBookingHelper.getBasketInEditMode();
        testData.setPassengerId(incorrectPaxId);
    }

    @When("^I send the request for change eJplus number$")
    public void i_send_the_request_for_change_ejplus_number() throws EasyjetCompromisedException {
        manageBookingHelper.updateEJPlusMembershipForStandardPassenger();
        manageBookingHelper.invokeUpdatePassengerDetails();
    }

    @When("^I send the request for change eJplus number with incorrect passenger id$")
    public void i_send_the_request_for_change_ejplus_number_with_incorrect_passenger_id() throws EasyjetCompromisedException {
        manageBookingHelper.updateEJPlusMembershipForIncorrectPaxId();
        manageBookingHelper.invokeUpdatePassengerDetailsForErroneous();
    }

    @And("^I have added eJplus number for (\\d+) st passenger$")
    public void i_have_added_ejplus_number_for_something_st_passenger(int passengerIndex) throws EasyjetCompromisedException {
        manageBookingHelper.amendBooking();
        manageBookingHelper.getBasketInEditMode();
        testData.setPassengerId(manageBookingHelper.getPassengerCodeInTheBasket(passengerIndex));
        manageBookingHelper.updateEJPlusMembershipForStandardPassenger();
        manageBookingHelper.invokeUpdatePassengerDetails();
        manageBookingHelper.verifyAmendBookingIsSuccessful();
    }

    @When("^I send the request to change eJplus number for (\\d+) nd passenger with same as 1 st$")
    public void i_send_the_reqquest_to_change_ejplus_number_for_something_nd_passenger_with_same_as_1_st(int passengerIndex) throws EasyjetCompromisedException {
        testData.setPassengerId(manageBookingHelper.getPassengerCodeInTheBasket(passengerIndex));
        manageBookingHelper.updateEJPlusMembershipForStandardPassenger();
        manageBookingHelper.invokeUpdatePassengerDetailsForErroneous();
    }

    @When("^I send the request for change eJplus number with incorrect last name$")
    public void i_send_the_request_for_change_ejplus_number_with_incorrect_last_name() throws EasyjetCompromisedException {
        manageBookingHelper.updateEJPlusMembershipWithIncorrectLastName();
        manageBookingHelper.invokeUpdatePassengerDetailsForErroneous();
    }

    @When("^I send the request for change eJplus number which is expired$")
    public void i_send_the_request_for_change_ejplus_number_which_is_expired() {
        manageBookingHelper.updateExpiredMembershipForPassenger();
        manageBookingHelper.invokeUpdatePassengerDetailsForErroneous();
    }

    @When("^I send the request for change eJplus number with incorrect format (.+)$")
    public void i_send_the_request_for_change_ejplus_number_with_incorrect_format(String incorrectFormat) throws EasyjetCompromisedException {
        manageBookingHelper.updateIncorrectMembershipForPassenger(incorrectFormat);
        manageBookingHelper.invokeUpdatePassengerDetailsForErroneous();
    }

    @When("^I send the request for change eJplus number which is not in complete status$")
    public void i_send_the_request_for_change_ejplus_number_which_is_not_in_complete_status() throws EasyjetCompromisedException {
        manageBookingHelper.updateMembershipWithNotCompleteStatus();
        manageBookingHelper.invokeUpdatePassengerDetailsForErroneous();
    }

    @And("^I notedown the admin fee in the basket$")
    public void i_notedown_the_admin_fee_in_the_basket() {
        originalAdminFee = manageBookingHelper.getAdminFeeFromBasket();
    }

    @Then("^the admin fee should not change$")
    public void the_admin_fee_should_not_change() {
        Double actualAdminFee = manageBookingHelper.getAdminFeeFromBasket();
        assertThat(actualAdminFee).isEqualTo(originalAdminFee);
    }

    @Then("^the admin fee should not apply for the new passenger for \"([^\"]*)\"$")
    public void the_admin_fee_should_not_apply_for_the_new_passenger_for_something(String newSector) {
        manageBookingHelper.verifyAdminFeeNotApplied(newSector);
    }

    @And("^I have added eJplus number for (\\d+) st passenger in first flight$")
    public void i_have_added_ejplus_number_for_st_passenger_in_first_flight(int passengerIndex) throws EasyjetCompromisedException {
        manageBookingHelper.amendBooking();
        manageBookingHelper.getBasketInEditMode();
        testData.setPassengerId(manageBookingHelper.getPassengerCodeInTheBasket(passengerIndex));
        manageBookingHelper.updateEJPlusMembershipForStandardPassenger();
        manageBookingHelper.invokeUpdatePassengerDetails();
        manageBookingHelper.verifyAmendBookingIsSuccessful();
    }

    @When("^I send the request to change the same eJplus number for same passenger in another flight$")
    public void i_send_the_request_to_change_the_same_ejplus_number_for_same_passenger_in_another_flight() throws EasyjetCompromisedException {
        List<String> passengerMap = manageBookingHelper.getPassengerMapFromTheBasket(testData.getPassengerId());
        for (String aPassengerMap : passengerMap) {
            if (!aPassengerMap.equalsIgnoreCase(testData.getPassengerId())) {
                testData.setPassengerId(aPassengerMap);
                break;
            }
        }
        manageBookingHelper.updateEJPlusMembershipForStandardPassenger();
    }

    @Then("^the response should be successful$")
    public void the_response_should_be_successful() {
        manageBookingHelper.invokeAmendBasicDetailsService();
        manageBookingHelper.verifyAmendBookingIsSuccessful();
    }


    @And("^I edit booking with passenger mix of (.*) with (true|false) purchased seat of (STANDARD|UPFRONT|EXTRA_LEGROOM)(?: having emergency exit seat (true|false))?$")
    public void iEditBookingWithPassengerMixOfPassengerMixWithContinuousPurchasedSeatOfSeatAndEmergencyExit(String passengerMix, boolean continuous, PurchasedSeatHelper.SEATPRODUCTS seat, boolean emergencyExit) throws Throwable {
        testData.setVerifySeatAllocation(true);
        testData.setTypeOfSeat(seat);

        String bookingRef = bookingHelper.createBookingWithPurchasedSeatAndGetAmendable(passengerMix, CommonConstants.STANDARD, true, emergencyExit, seat, continuous);

        pollingLoop().untilAsserted(
                () -> {
                    bookingHelper.getBookingDetails(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), testData.getData(SerenityFacade.DataKeys.CHANNEL));
                    assertThat(testData.getData(SerenityFacade.DataKeys.BOOKING_STATUS).equals("COMPLETED")).withFailMessage("Booking status is not COMPLETED").isTrue();
                }
        );

        Basket basketTmp = basketHelper.getBasket(basketHelper.createAmendableBasket(bookingRef), testData.getChannel());
        testData.setData(SerenityFacade.DataKeys.BASKET_ID, basketTmp.getCode());
    }

    @When("^I change below details$")
    public void iChangeBelowDetails(List<String> fields) throws Throwable {
        basketHelper.getBasket(testData.getBasketId());
        Basket.Passenger passenger = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream())
                .findFirst().orElse(null);
        buildRequestBody(fields);
        setRequestPathParameter(passenger);
        invokeUpdateBasicDetailsService();
        testData.setData(SerenityFacade.DataKeys.SAVED_PASSENGER_CODE, passenger.getCode());
    }

    private void setRequestPathParameter(Basket.Passenger passenger) {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .passengerId(passenger.getCode())
                .path(BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS)
                .build();
    }

    private void invokeUpdateBasicDetailsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = HybrisHeaders.getValid(testData.getChannel());
        testData.setData(HEADERS, headers);
        testData.setData(HEADERS, headers);
        amendBasicDetailsService = serviceFactory.amendBasicDetails(new AmendBasicDetailsRequest(headers.build(), basketPathParams,
                UpdatePassengerDetailsQueryParams.builder().operationTypeUpdate("UPDATE").build()
                , amendBasicDetailsRequestBody.build()));
        testData.setData(SERVICE, amendBasicDetailsService);
        retryInvoke();
        amendBasicDetailsService.getRestResponse();
    }

    private void retryInvoke() {
        int[] noOfRetrys = {5};
        try {
            pollingLoop().until(() -> {
                amendBasicDetailsService.invoke();
                noOfRetrys[0]--;
                return amendBasicDetailsService.getStatus() == 200 || noOfRetrys[0] == 0;
            });
        } catch (ConditionTimeoutException ct) {
            amendBasicDetailsService.getRestResponse();
        }
    }

    private String getRandomFieldFromList(List<String> fields) {
        Random random = new Random();
        return fields.get(random.nextInt(fields.size()));
    }

    private void buildRequestBody(List<String> fieldsList) throws EasyjetCompromisedException {
        amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder();

        Map<String, Object> fieldsMap = new HashMap<>();
        String randomField = getRandomFieldFromList(fieldsList);
        MemberShipModel memberShipModel = membershipDao.getEJPlusMemberBasedOnStatus("COMPLETED");
        Name name;
        switch (randomField.toLowerCase()) {
            case "name":
                name = Name.builder().firstName("UpdatedFirstName").lastName("UpdatedLastName").build();
                amendBasicDetailsRequestBody.name(name);
                fieldsMap.put(randomField, name);
                break;
            case "age":
                int age = 50;
                amendBasicDetailsRequestBody.age(age);
                fieldsMap.put(randomField, age);
                break;
            case "email":
                String email = "updatedemail@portaltech.com";
                amendBasicDetailsRequestBody.email(email);
                fieldsMap.put(randomField, email);
                break;
            case "ejpluscardnumber":
                name = Name.builder().firstName(memberShipModel.getFirstname()).lastName(memberShipModel.getLastname()).build();
                String ejPlusNo = memberShipModel.getEjMemberShipNumber();
                amendBasicDetailsRequestBody.ejPlusCardNumber(ejPlusNo);
                amendBasicDetailsRequestBody.name(name);
                fieldsMap.put(randomField, ejPlusNo);
                fieldsMap.put("name", name);
                break;
            case "nif":
                String nifNo = "123456789";
                amendBasicDetailsRequestBody.nifNumber(nifNo);
                fieldsMap.put(randomField, nifNo);
                break;
            default:
                throw new RuntimeException("invalid field selected " + randomField);
        }
        testData.setData(SerenityFacade.DataKeys.SESSION, fieldsMap);

    }

    @And("^I change below details for couple of passenger$")
    public void iChangeBelowDetailsForCoupleOfPassenger(List<String> fields) throws Throwable {
        basketHelper.getBasket(testData.getBasketId());
        List<Basket.Passenger> allPassengersList = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream())
                .flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        List<Basket.Passenger> updatePassengers = allPassengersList.subList(1, 3);
        buildRequestBody(fields);
        updatePassengers.forEach(passenger -> {
            setRequestPathParameter(passenger);
            invokeUpdateBasicDetailsService();
        });

        testData.setData(SerenityFacade.DataKeys.PASSENGER_LIST, updatePassengers);
    }
}
