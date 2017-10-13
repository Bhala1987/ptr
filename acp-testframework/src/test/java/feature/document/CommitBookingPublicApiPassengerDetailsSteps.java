package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.*;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.BasketContentFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PaymentMethodFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@ContextConfiguration(classes = TestApplication.class)
public class CommitBookingPublicApiPassengerDetailsSteps {
    protected static Logger LOG = LogManager.getLogger(CommitBookingPublicApiPassengerDetailsSteps.class);

    @Autowired
    private BasketHelper basketHelper;

    @Autowired
    private BookingHelper commitBookingHelper;

    @Autowired
    private SerenityFacade serenityFacade;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Calendar calendar = Calendar.getInstance();

    private BasketContent basketContent;

    @When("^I create a valid basket with the following scenario: \"([^\"]*)\"$")
    public void iCreateAValidBasketWithTheFollowingChanges(String scenarioName) {
        try {
            BasketContent basketContent = BasketContentFactory.getBasketContent(
                commitBookingHelper.createABasketWithPassengerMix(
                    "2 Adult, 1 Child, 2 Infant", serenityFacade.getChannel()
                ).getBasket()
            );

            this.basketContent = modifyBasketForScenario(scenarioName, basketContent);
        } catch (Throwable e) {
            throw new RuntimeException("Something went wrong creating the booking.", e);
        }
    }

    @And("^I commit the booking with the erroneous basket data$")
    public void iCommitTheBookingWithTheErroneousBasketData() throws EasyjetCompromisedException {
        Basket baseBasket = basketHelper.getBasketService().getResponse().getBasket();

        CommitBookingRequestBody commitBookingRequestBody = CommitBookingRequestBody.builder()
            .basketCode(null)
            .bookingType("STANDARD_CUSTOMER")
            .bookingReason("LEISURE")
            .basketContent(basketContent)
            .paymentMethods(
                Arrays.asList(PaymentMethodFactory.generateDebitCardPaymentMethod(baseBasket))
            )
            .build();

        commitBookingHelper.commitTheBooking(
            new CommitBookingRequest(
                HybrisHeaders.getValid(serenityFacade.getChannel()).build(),
                commitBookingRequestBody
            )
        );
    }

    @Then("^warning code (.*) should be returned$")
    public void warningCodeShouldBeReturned(String code) {
        commitBookingHelper.getCommitBookingService().assertThat().additionalInformationContains(code);
    }

    private BasketContent modifyBasketForScenario(String scenarioName, BasketContent basketContent) {
        switch (scenarioName) {
            case "NifLength":
                setNifLengthInBasketContent(basketContent);
                break;
            case "DuplicateNif":
                setDuplicateNifInBasketContent(basketContent);
                break;
            case "SSRThreshold":
                setSsrThresholdInBasketContent(basketContent);
                break;
            case "SSRSector":
                setSsrForDisallowedSector(basketContent);
                break;
            case "EJPlusLength":
                setEjPlusLengthTooLong(basketContent);
                break;
            case "StaffEJPlusLength":
                setStaffEjPlusLengthTooLong(basketContent);
                break;
            case "SurnameToEJSurname":
                setSurnameDifferentToCardNumber(basketContent);
                break;
            case "EJPlusExpiry":
                setEJMembershipAsExpired(basketContent);
                break;
            case "DuplicateEJPlus":
                setDuplicateEJPlusInfo(basketContent);
                break;
            case "EJMembershipStatus":
                setBadEJStatus(basketContent);
                break;
            case "DocumentNumber":
                setDocumentNumber(basketContent, "0987654321234567890987654321234567890987654321");
                break;
            case "DocumentChars":
                setDocumentNumber(basketContent, "})(^%$#$%^&*(");
                break;
            case "ApisAdultTooYoung":
                setPassengerAge(basketContent, "Adult", "2013-01-01");
                break;
            case "ApisInfantTooYoung":
                setPassengerAge(basketContent, "Infant", dateFormat.format(calendar.getTime()));
                break;
            case "ApisInfantTooOld":
                setPassengerAge(basketContent, "Infant", "1970-01-01");
                break;
            case "ApisChildTooYoung":
                setPassengerAge(basketContent, "Child", dateFormat.format(calendar.getTime()));
                break;
            case "ApisChildTooOld":
                setPassengerAge(basketContent, "Child", "1970-01-01");
                break;
            case "InfantExceedsAdult":
                removeAnAdult(basketContent);
                break;
            case "WarningChildAlone":
                removeAllPassengersOfType(basketContent, "Adult");
                removeAllPassengersOfType(basketContent, "Infant");
                break;
            default:
                throw new IllegalArgumentException("Unable to locate requested scenario: " + scenarioName);
        }

        return basketContent;
    }

    private void removeAnAdult(BasketContent basketContent) {
        List<String> passengerIds = getPassengerIds(basketContent, "Adult");

        // Remove from flights.
        List<Passenger> adultPassengers = basketContent.getOutbounds().stream()
            .flatMap(outbound -> outbound.getFlights().stream())
            .flatMap(flight -> flight.getPassengers().stream())
            .filter(passenger -> passengerIds.contains(passenger.getExternalPassengerId()))
            .collect(Collectors.toList());

        Passenger passengerToRemove = adultPassengers.get(0);

        basketContent.getOutbounds().stream()
            .flatMap(outbound -> outbound.getFlights().stream())
            .forEach(flight -> flight.getPassengers().remove(passengerToRemove));

        // Remove from unique passenger list.
        basketContent.getUniquePassengerList().removeIf(
            uniquePassengerList -> uniquePassengerList.getExternalPassengerId().equals(
                passengerToRemove.getExternalPassengerId()
            )
        );
    }

    private void removeAllPassengersOfType(BasketContent basketContent, String type) {
        List<String> passengerIds = getPassengerIds(basketContent, type);

        // Remove from flights.
        basketContent.getOutbounds().stream()
            .flatMap(outbound -> outbound.getFlights().stream())
            .forEach(flight -> flight.getPassengers().removeIf(
                passenger -> passengerIds.contains(passenger.getExternalPassengerId())
            ));

        // Remove from unique passenger list.
        basketContent.getUniquePassengerList().removeIf(
            uniquePassengerList -> passengerIds.contains(uniquePassengerList.getExternalPassengerId())
        );
    }

    private void setPassengerAge(BasketContent basketContent, String type, String dateOfBirth) {
        getPassengerIds(basketContent, type).forEach(passengerId -> basketContent.getOutbounds().stream()
            .flatMap(outbound -> outbound.getFlights().stream())
            .flatMap(flight -> flight.getPassengers().stream())
            .filter(passenger -> passenger.getExternalPassengerId().equals(passengerId))
            .forEach(
                passenger -> passenger.setPassengerAPIS(
                    PassengerAPIS.builder()
                        .name(
                            Name.builder()
                                .firstName("Firstname")
                                .lastName("Lastname")
                                .fullName("Fullname")
                                .build()
                        )
                        .countryOfIssue("GBR")
                        .nationality("GBR")
                        .gender("MALE")
                        .documentType("PASSPORT")
                        .documentNumber("YT123COEZH")
                        .documentExpiryDate("2099-01-01")
                        .dateOfBirth(dateOfBirth)
                        .build()
                )
            )
        );
    }

    private void setDocumentNumber(BasketContent basketContent, String documentNumber) {
        basketContent.getOutbounds().stream()
            .flatMap(outbound -> outbound.getFlights().stream())
            .flatMap(flight -> flight.getPassengers().stream())
            .filter(passenger -> passenger.getAge() > 18) // Filter for adults.
            .forEach(
                passenger -> passenger.setPassengerAPIS(
                    PassengerAPIS.builder()
                        .name(
                            Name.builder()
                                .firstName("Firstname")
                                .lastName("Lastname")
                                .fullName("Fullname")
                                .build()
                        )
                        .countryOfIssue("GBR")
                        .nationality("GBR")
                        .gender("MALE")
                        .documentType("PASSPORT")
                        .documentNumber(documentNumber)
                        .documentExpiryDate("2099-01-01")
                        .dateOfBirth("1980-01-01")
                        .build()
                )
            );
    }

    private void setBadEJStatus(BasketContent basketContent) {
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().setEjPlusCardNumber("00453333");
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().getName().setLastName("barone");
    }

    private void setEJMembershipAsExpired(BasketContent basketContent) {
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().setEjPlusCardNumber("00000110");
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().getName().setLastName("Henry");
    }

    private void setNifLengthInBasketContent(BasketContent basketContent) {
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().setNifNumber("01234");
    }

    private void setDuplicateNifInBasketContent(BasketContent basketContent) {
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().setNifNumber("0123456789");
        basketContent.getUniquePassengerList().get(1).getPassengerDetails().setNifNumber("0123456789");
    }

    private void setSsrThresholdInBasketContent(BasketContent basketContent) {
        Passenger passenger = basketContent.getOutbounds().stream()
            .flatMap(outbound -> outbound.getFlights().stream())
            .flatMap(flight -> flight.getPassengers().stream())
            .findAny()
            .get();

        setSpecialRequestOnPassenger(passenger, "DEAF", 8);
    }

    private void setSsrForDisallowedSector(BasketContent basketContent) {
        basketContent.getOutbounds().stream()
            .flatMap(outbound -> outbound.getFlights().stream())
            .flatMap(flight -> flight.getPassengers().stream())
            .forEach(passenger -> setSpecialRequestOnPassenger(passenger, "BLND", 1));
    }

    private void setEjPlusLengthTooLong(BasketContent basketContent) {
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().setEjPlusCardNumber("01234");
    }

    private void setStaffEjPlusLengthTooLong(BasketContent basketContent) {
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().setEjPlusCardNumber("S01234567890");
    }

    private void setDuplicateEJPlusInfo(BasketContent basketContent) {
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().setEjPlusCardNumber("S01234567890");
        basketContent.getUniquePassengerList().get(1).getPassengerDetails().setEjPlusCardNumber("S01234567890");
    }

    private void setSpecialRequestOnPassenger(Passenger passenger, String code, int numberOfSsrs) {
        ArrayList<Ssr> ssrs = new ArrayList<>();
        for (int i = 0; i < numberOfSsrs; i++) {
            Ssr ssr = new Ssr();
            ssr.setCode(code);

            ssrs.add(ssr);
        }

        SpecialRequest specialRequest = new SpecialRequest();
        specialRequest.setSsrs(ssrs);

        passenger.setSpecialRequests(Arrays.asList(specialRequest));
    }

    private void setSurnameDifferentToCardNumber(BasketContent basketContent) {
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().setEjPlusCardNumber("03445610");
        basketContent.getUniquePassengerList().get(0).getPassengerDetails().getName().setLastName("NOTPORTER");
    }

    private List<String> getPassengerIds(BasketContent basketContent, String type) {
        return basketContent.getUniquePassengerList().stream()
            .filter(
                uniquePassengerList -> uniquePassengerList.getPassengerDetails()
                    .getPassengerType()
                    .equalsIgnoreCase(type)
            )
                .map(UniquePassenger::getExternalPassengerId)
            .collect(Collectors.toList());
    }
}
