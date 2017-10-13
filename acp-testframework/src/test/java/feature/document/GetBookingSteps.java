package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.BookingPermissionDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.PaymentServiceResponseHelper;
import com.hybris.easyjet.fixture.hybris.helpers.SetAPIHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.awaitility.core.ConditionTimeoutException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.junit.Assert.fail;

/**
 * Created by tejaldudhale on 14/11/2016.
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetBookingSteps {

    @Autowired
    private BookingHelper commitBookingHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private SetAPIHelper setAPIHelper = new SetAPIHelper();
    @Autowired
    private BookingPermissionDao bookingPermissionDao;

    private String bookingRef;
    private GetBookingService getBookingService;
    private String channel;
    private GetBookingResponse bookingResponse;
    private GetBookingResponse.Passenger passenger;


    @Autowired
    private PaymentServiceResponseHelper paymentServiceResponseHelper;

    @When("^I search for a booking with invalid reference number$")
    public void iSearchForABookingWithInvalidReferenceNumber() throws Throwable {
        String invalidBookingId = String.valueOf(0);
        BookingPathParams params = BookingPathParams.builder().bookingId(invalidBookingId).build();
        getBookingService = serviceFactory.getBookings(new GetBookingRequest(HybrisHeaders.getValid(channel).build(), params));
        getBookingService.invoke();
    }

    @Then("^I get error in response informing me that there are no bookings$")
    public void iGetErrorInResponseInformingMeThatThereAreNoBookings() throws Throwable {
        getBookingService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100024_1000");
    }

    @When("^I search for a booking with reference number$")
    public void iSearchForABookingWithReferenceNumber() throws Throwable {
        BookingPathParams params = BookingPathParams.builder().bookingId(bookingRef).build();
        getBookingService = serviceFactory.getBookings(new GetBookingRequest(HybrisHeaders.getValid(channel).build(), params));
        getBookingService.invoke();
    }

    @Then("^Booking details with matching reference number are returned$")
    public void bookingDetailsWithMatchingRefernceNumberAreReturned() throws Throwable {
        getBookingService.assertThat().theBookingDetailsAreReturnedWithAReferenceNumber(bookingRef);

        try {
            pollingLoop().untilAsserted(() -> {
                getBookingService.assertThat().theBookingDetailsAreReturnedWithStatus("COMPLETED");
                getBookingService.invoke();
            });
        } catch (ConditionTimeoutException e) {
            fail("BOOKING STATUS IS NOT COMPLETED !!!");
        }
    }

    @When("^I do get booking$")
    public void bookingDetailsAreReturned() throws Throwable {
        getBookingService.assertThat().theBookingDetailsAreReturnedWithAReferenceNumber(bookingRef);
        getBookingService.assertThat().theBookingDetailsAreReturnedWithStatus("COMPLETED");
    }


    @Given("^I have an existing booking using channel \"([^\"]*)\" and passenger mix \"([^\"]*)\"$")
    public void iHaveAnExistingBookingUsingChannel(String channel, String mix) throws Throwable {
        this.channel = channel;
        bookingRef = commitBookingHelper.createNewBooking(channel, mix).getConfirmation().getBookingReference();
        if (bookingRef.isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
    }

    @And("^I will set the APIS ([^\"]*) for the passenger on the booking$")
    public void IWillSetApiStatus(String status) throws Throwable {
        commitBookingHelper.getGetBookingService().getResponse().getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .forEach(passengerAPIS -> passengerAPIS.getApisStatus().equals(status));
    }

    @And("^I create a \"([^\"]*)\" status (booking|return booking) for \"([^\"]*)\"$")
    public void iCreateAStatusBookingFor(String statusToBeIn, String isReturn, String passengerMix) throws Throwable {
        if (isReturn.equals("return booking")) {
            bookingRef = commitBookingHelper.createNewBookingWithReturnFlight(testData.getChannel(), passengerMix).getBookingConfirmation().getBookingReference();
        } else {
            bookingRef = commitBookingHelper.createNewBooking(testData.getChannel(), passengerMix).getBookingConfirmation().getBookingReference();
        }
        testData.setData(BOOKING_ID, bookingRef);

        pollingLoop().untilAsserted(() -> {
                    String bookingStatus = commitBookingHelper.getBookingDetails(bookingRef, testData.getChannel()).getBookingContext().getBooking().getBookingStatus();
                    assertThat(bookingStatus).isEqualTo(statusToBeIn);
                }
        );
        testData.setData(GET_BOOKING_RESPONSE, commitBookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
    }

    @Then("^I do the apisRequired to ([^\"]*) against the flights on the booking$")
    public void iWillSetApisRequiredToAgainstTheFlightsOnTheBooking(String isApis) throws Throwable {
        commitBookingHelper.getGetBookingService().getResponse().getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getSector).forEach(apisRequired -> apisRequired.getApisRequired().equals(isApis));
    }

    @Given("^(.+) do the commit booking with \"([^\"]*)\"$")
    public void do_the_commit_booking_with_something(String channel, String passengerMix) throws Throwable {
        testData.setChannel(channel);
        testData.setData(CHANNEL, channel);
        bookingRef = commitBookingHelper.createNewBookingWithReturnFlight(testData.getChannel(), passengerMix).getBookingConfirmation().getBookingReference();
        testData.setData(BOOKING_ID, bookingRef);
        try {
            pollingLoop().untilAsserted(() -> {
                commitBookingHelper.getBookingDetails(bookingRef, testData.getChannel()).getBookingContext().getBooking().getBookingStatus().equals("COMPLETED");
            });
        } catch (ConditionTimeoutException e) {
            fail("BOOKING STATUS IS NOT COMPLETED !!!");
        }
    }

    @Given("^(.+) do the commit booking with holditems for \"([^\"]*)\"$")
    public void do_the_commit_booking_with_holditems_for_something(String channel, String passengerMix) throws Throwable {
        testData.setChannel(channel);
        bookingRef = commitBookingHelper.createBookingWithReturnFlightAndAllProducts(testData.getChannel(), passengerMix).getBookingConfirmation().getBookingReference();
        testData.setData(BOOKING_ID, bookingRef);
        try {
            pollingLoop().untilAsserted(() -> {
                commitBookingHelper.getBookingDetails(bookingRef, testData.getChannel()).getBookingContext().getBooking().getBookingStatus().equals("COMPLETED");
            });
        } catch (ConditionTimeoutException e) {
            fail("BOOKING STATUS IS NOT COMPLETED !!!");
        }
    }


    @Then("^I will check Passenger Status and APIS$")
    public void iWillCheckPassengerStatusAndAPIS() throws Throwable {
        final GetBookingResponse.BookingContext[] bookingContext = new GetBookingResponse.BookingContext[1];
        try {
            pollingLoop().until(() -> {
                bookingContext[0] = commitBookingHelper.getBookingDetails(testData.getData(BOOKING_ID),
                        testData.getChannel()).getBookingContext();
                GetBookingResponse.Passenger passenger = bookingContext[0].getBooking().getOutbounds().stream()
                        .flatMap(outbound -> outbound.getFlights().stream())
                        .flatMap(flight -> flight.getPassengers().stream())
                        .filter(bookingPassenger -> bookingPassenger.getCode().equalsIgnoreCase(testData.getPassengerId()))
                        .findFirst()
                        .orElse(null);

                AbstractPassenger.PassengerAPIS apis = passenger.getPassengerAPIS();

                return Objects.nonNull(apis) &&
                        setAPIHelper.getSetApisBookingService().assertThat().compareNameOnBooking(apis.getName(), setAPIHelper.getRequestBodyApisBooking().getApi().getName())
                        && passenger.getPassengerStatus().equalsIgnoreCase("BOOKED");
            });
        } catch (ConditionTimeoutException e) {
            fail("Apis for passenger on booking does not exist !!!");
        }
        setAPIHelper.getSetApisBookingService().assertThat().checkTheStatusAndApis(setAPIHelper.getRequestBodyApisBooking(), bookingContext[0], testData.getPassengerId());
    }

    @Then("^I get an (.*)")
    public void iGetAn(String code) throws Throwable {
        setAPIHelper.getSetApisBookingService().assertThatErrors().containedTheCorrectErrorMessage(code);
    }

    @When("^I send a request to setApis on (.*) flight with different (.*) for (.*)$")
    public void iSendPARequestToSetApisOnFlightWithDifferent(int numberFlight, String fieldToChange, String passengerType) throws Throwable {
        boolean apisToFutureFLight = isApisToFutureFLight(numberFlight, passengerType);
        setAPIHelper.invokeUpdateIdentityDocument(apisToFutureFLight, fieldToChange);
    }

    private boolean isApisToFutureFLight(int numberFlight, String passengerType) {
        testData.setPassengerId(testData.getBookingResponse().getBookingContext().getBooking().getOutbounds().stream()
                .flatMap(outbound -> outbound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(bookingPassenger -> bookingPassenger.getPassengerDetails().getPassengerType().equals(passengerType))
                .findFirst().orElse(null)
                .getCode()
        );

        if (Objects.isNull(testData.getPassengerId()))
            throw new IllegalArgumentException("No passenger with desired type " + passengerType);

        boolean apisToFutureFLight;
        apisToFutureFLight = numberFlight > 1;
        return apisToFutureFLight;
    }

    @Then("^I (.*) get a warning (.*) just on desired passenger$")
    public void iGetAWarningJustOnDesiredPassenger(boolean receiveWarning, String warning) throws Throwable {
        setAPIHelper.getSetApisBookingService().assertThat().verifyWarningMessage(warning, receiveWarning);
    }

    @When("^I send a request to setApis on (.*) flight with (.*) and different (.*) for (.*)")
    public void iSendARequestToSetApisOnFlightWithAndDifferentFor(int numberFlight, String channel, String fieldToChange, String passengerType) throws Throwable {
        boolean apisToFutureFLight = isApisToFutureFLight(numberFlight, passengerType);
        setAPIHelper.invokeUpdateIdentityDocument(apisToFutureFLight, fieldToChange, channel);

    }

    @When("^I send a request to update APIS with duplicate (.*) for (.*)")
    public void iSendARequestToSetApisWithDuplicateDocumentIdFlightFlightWithDifferentFieldToChangeForTypePassenger(String fieldName, String passengerType) throws Throwable {
        testData.setDocumentId(testData.getBookingResponse().getBookingContext().getBooking().getOutbounds().stream()
                .flatMap(outbound -> outbound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(bookingPassenger -> bookingPassenger.getPassengerAPIS().getDocumentNumber() != null)
                .findFirst().orElse(null)
                .getPassengerAPIS().getDocumentNumber());

        testData.setData("name", testData.getBookingResponse().getBookingContext().getBooking().getOutbounds().stream()
                .flatMap(outbound -> outbound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .findFirst().orElse(null)
                .getPassengerDetails().getName().getFirstName());

        List<GetBookingResponse.Passenger> passengers = testData.getBookingResponse().getBookingContext().getBooking().getOutbounds().stream()
                .flatMap(outbound -> outbound.getFlights().stream()).findFirst().orElse(null).getPassengers();
        for (GetBookingResponse.Passenger passenger : passengers) {
            AbstractPassenger.PassengerDetails passengerDetails = passenger.getPassengerDetails();
            if (Objects.nonNull(passengerDetails.getName())) {
                if (Objects.nonNull(passengerDetails.getName().getFirstName()) && !passengerDetails.getName().getFirstName().equals(testData.getData("name"))) {
                    testData.setPassengerId(passenger.getCode());
                    break;
                }
            }
        }

        if (Objects.isNull(testData.getPassengerId()))
            throw new IllegalArgumentException("No passenger with desired type " + passengerType);

        boolean apisToFutureFLight;
        apisToFutureFLight = false;

        setAPIHelper.invokeUpdateIdentityDocumentWithDuplicateDocumentNumber(apisToFutureFLight, fieldName);
    }

    @Then("^the set APIs should fail with (.*) error$")
    public void theSetAPIsShouldFailWithError(String error) throws Throwable {
        setAPIHelper.getSetApisBookingService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Then("^I see APIs not updated for both flights$")
    public void iSeeAPIsNotUpdatedForBothFlights() throws Throwable {

        List<AbstractPassenger.PassengerAPIS> collect = testData.getBookingResponse().getBookingContext().getBooking().getOutbounds()
                .stream().flatMap(flights -> flights.getFlights()
                        .stream().flatMap(passengers -> passengers.getPassengers()
                                .stream().map(passenger -> passenger.getPassengerAPIS()))).collect(Collectors.toList());
        Assert.assertThat(collect, everyItem(equalTo(null)));
    }

    @When("^I send a error request to setApis on (.*) flight with different (.*) for (.*)")
    public void iSendAErrorRequestToSetApisOnFlightFlightWithDifferentFieldToChangeForTypePassenger(int numberFlight, String fieldToChange, String passengerType) throws Throwable {
        boolean apisToFutureFLight = isApisToFutureFLight(numberFlight, passengerType);
        setAPIHelper.invokeUpdateIdentityDocumentError(apisToFutureFLight, fieldToChange);
    }

    @And("^I note down the booking currency$")
    public void i_note_down_the_booking_currency() throws Throwable {
        GetBookingResponse bookingResponse;
        bookingResponse = commitBookingHelper.getBookingDetails(bookingRef, testData.getChannel());
        testData.setActualCurrency(bookingResponse.getBookingContext().getBooking().getBookingCurrency().getCode());
    }

    @Then("^booking request has the association of Passenger to Infant to seat$")
    public void bookingRequestHasTheAssociationOfPassengerToInfantToSeat() throws Throwable {
        commitBookingHelper.getGetBookingService().assertThat()
                .checkBookingRequestForInfantOnSeatPassengerAssoication(commitBookingHelper.getGetBookingService().getResponse());
    }

    @And("^the bording Pass status is changed$")
    public void theBordingPassStatusIsChanged() throws Throwable {
        String bookingRef = testData.getData(BOOKING_ID);
        GetBookingResponse bookingResponse;
        bookingResponse = commitBookingHelper.getBookingDetails(bookingRef, testData.getChannel());
        testData.setData(SERVICE, bookingResponse);
        commitBookingHelper.getGetBookingService().assertThat().checkBordingPassStatus("NEVER_RERETRIEVE");
    }

    @And("^the bording Pass status is changed to (.*)$")
    public void theBordingPassStatusIsChangedToBoardingPassStatus(String boardingPassStatus) throws Throwable {
        Map<String, Object> fieldsMap = testData.getData(SESSION);
        if (Objects.nonNull(fieldsMap.get("ejPlusCardNumber"))) {
            String bookingRef = testData.getData(BOOKING_ID);
            GetBookingResponse bookingResponse;
            bookingResponse = commitBookingHelper.getBookingDetails(bookingRef, testData.getChannel());
            testData.setData(SERVICE, bookingResponse);
            commitBookingHelper.getGetBookingService().assertThat().checkBordingPassStatus(boardingPassStatus);
        }

    }

    @And("^I see the bording Pass status is changed to (.*)$")
    public void iSeetheBordingPassStatusIsChangedToBoardingPassStatus(String boardingPassStatus) throws Throwable {
            String bookingRef= testData.getData(BOOKING_ID);
            GetBookingResponse bookingResponse;
            bookingResponse = commitBookingHelper.getBookingDetails(bookingRef, testData.getChannel());
            testData.setData(SERVICE,bookingResponse);
            commitBookingHelper.getGetBookingService().assertThat().checkBordingPassStatus(boardingPassStatus);
    }

    @Then("^I will return an additional information \"([^\"]*)\" to the channel$")
    public void iWillReturnAnAdditionalInformationToTheChannel(String warningCode) throws Throwable {
        bookingRef = testData.getData(BOOKING_ID);
        GetBookingResponse bookingResponse;
        bookingResponse = commitBookingHelper.getBookingDetails(bookingRef, testData.getChannel());
        testData.setData(SERVICE, bookingResponse);
        commitBookingHelper.getGetBookingService().assertThat().additionalInformationContains(warningCode);
    }

    @And("^commit booking with passengerMix \"([^\"]*)\" using \"([^\"]*)\" and \"([^\"]*)\"$")
    public void commitBookingWithPassengerMixUsingAnd(String PaxMix, String paymentType, String paymentDetails) throws Throwable {
        commitBookingHelper.createBooking(PaxMix, paymentType, paymentDetails);
    }

    @Then("^booking contains payment transaction type (.*)$")
    public void bookingContainsPaymentTransactionType(String transType) throws Throwable {
        commitBookingHelper.getGetBookingService().assertThat().getTransactionType(transType);
    }

    @And("^the additional seat should exists in the booking details$")
    public void theAdditionalSeatShouldExistsInTheBookingDetails() throws Throwable {

        assertThat(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .allMatch(passengerID -> passengerID.getAdditionalSeats().size() > 0)).withFailMessage("Passenger doesn't have an additional seat").isTrue();


        assertThat(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .allMatch(passengerID -> passengerID.getAdditionalSeats().isEmpty())).withFailMessage("Passenger doesn't have an additional seat").isFalse();
    }

    @And("^the additional seat entry status should be (NEW|CHANGED|SAME)$")
    public void theAdditionalSeatEntryStatusShouldBe(String entryStatus) throws Throwable {

        assertThat(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .map(GetBookingResponse.Passenger::getAdditionalSeats)
                .flatMap(Collection::stream)
                .allMatch(passengerID -> passengerID.getFareProduct().getEntryStatus().equalsIgnoreCase(entryStatus))).withFailMessage("The entry status of the passenger's additional seat is not correct.").isTrue();
    }

    @And("^the additional seat active flag should be (TRUE|FALSE)$")
    public void theAdditionalSeatActiveFlagShouldBe(Boolean active) throws Throwable {

        assertThat(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .map(GetBookingResponse.Passenger::getAdditionalSeats)
                .flatMap(Collection::stream)
                .allMatch(passengerID -> passengerID.getFareProduct().getActive())).withFailMessage("The active flag of the passenger's additional seat is not correct.").isEqualTo(active);
    }

    @And("^the boarding pass status should be (.*)$")
    public void theBoardingPassStatusShouldBe(String boardingPassStatus) throws Throwable {
        commitBookingHelper.getGetBookingService().assertThat().checkBordingPassStatus(boardingPassStatus);
    }

    @And("^I get the booking details$")
    public void iGetTheBookingDetails() throws Throwable {
        bookingRef = testData.getData(BOOKING_ID);
        bookingResponse = commitBookingHelper.getBookingDetails(bookingRef, testData.getChannel());
        testData.setData(SERVICE, bookingResponse);
    }

    @And("^I should see that the passengers are added$")
    public void iShouldSeeThatThePassengersAreAdded() throws Throwable {

        assertThat(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream).count()).isGreaterThan(1);
    }

    @And("^the entry status for each passenger should be (NEW|CHANGED|SAME)$")
    public void theEntryStatusForEachPassengerShouldBe(String entryStatus) throws Throwable {

        assertThat(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .allMatch(passengerID -> passengerID.getFareProduct().getEntryStatus().equalsIgnoreCase(entryStatus))).withFailMessage("The entry status of the passenger's additional seat is not correct.").isTrue();
    }

    @And("^the active flag for each passenger should be (TRUE|FALSE)$")
    public void theActiveFlagForEachPassengerShouldBe(Boolean active) throws Throwable {

        assertThat(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .allMatch(passengerID -> passengerID.getActive())).withFailMessage("The active flag for each passenger is not "+active).isEqualTo(active);

        assertThat(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .allMatch(passengerID -> passengerID.getFareProduct().getActive())).withFailMessage("The active flag for each passenger's fare product is not "+active).isEqualTo(active);
    }

    @And("^I should see that the new flight is added$")
    public void iShouldSeeThatTheNewFlightIsAdded() throws Throwable {
        Matchers.either(Matchers.is(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream).count() > 1)).or(Matchers.is(bookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream).count() > 1)).matches(Boolean.TRUE);
    }
}
