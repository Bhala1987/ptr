package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.database.hybris.models.BookingPermissionModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.DateFormat;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.BasketContent;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.CommitBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.CustomerContext;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.PaymentMethod;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateSavedPassengerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for booking response object, provides reusable assertions to all tests
 */
@NoArgsConstructor
public class BookingAssertion extends Assertion<BookingAssertion, GetBookingResponse> {

    public static final String FLIGHT_ARRIVAL_OR_DEPARTURE_DATE_FORMAT = "EEE' 'dd-MMM-yyyy' 'HH-mm-ss";
    private static final Logger LOG = LogManager.getLogger(BookingAssertion.class);

    //
    private String[] allowedDocuments = new String[]{
            "EMAIL_BOOKING_CONFIRMATION",
            "PAYMENT_CONFIRMATION",
            "PRINT_BOOKING_CONFIRMATION",
            "VAT_INVOICE",
            "EMAIL_BOARDING_PASS",
            "EMAIL_FLIGHT_DETAILS",
            "EMAIL_INSURANCE_LETTER",
            "CUSTOMER_CANCELLED_CONFIRMATION",
            "PRINT_INSURANCE_LETTER"
    };

    public BookingAssertion(GetBookingResponse getBookingResponse) {
        this.response = getBookingResponse;
    }

    /**
     * Set respone to use the assertion class with @Steps annotation
     *
     * @param getBookingResponse the response received after invoking the service
     */
    public void setResponse(GetBookingResponse getBookingResponse) {
        this.response = getBookingResponse;
    }

    /**
     * Validate that things from the baskets were added properly to the booking.
     *
     * @param basket The basket.
     * @return Itself?
     */
    public BookingAssertion theBasketWasAddedToTheBooking(BasketsResponse basket) {
        // Assert that the booking reason is the same as what we sent through.
        assertThat(
                response.getBookingContext().getBooking().getBookingReason()
        ).isEqualToIgnoringCase(
                basket.getBasket().getBookingReason()
        );

        // Validate inbound flights if we have them.
        if (!basket.getBasket().getInbounds().isEmpty()) {
            validateBookedFlights(
                    basket.getBasket().getInbounds().stream(),
                    response.getBookingContext().getBooking().getInbounds()
            );
        }

        // Validate outbound flights if we have them.
        if (!basket.getBasket().getOutbounds().isEmpty()) {
            validateBookedFlights(basket.getBasket().getOutbounds().stream(), response.getBookingContext().getBooking().getOutbounds());
        }

        return this;
    }

    /**
     * Validate the flights booked and some key values.
     *
     * @param expectedFlights The expected flights in the basket.
     * @param actualFlights   The actual flights in the booking.
     */
    private void validateBookedFlights(Stream<Basket.Flights> expectedFlights, List<GetBookingResponse.Flights> actualFlights) {
        // Loop through all the outbound basket flights and validate that they're the same as the booking flights.
        expectedFlights.flatMap(flights -> flights.getFlights().stream()).forEach(expectedFlight -> {
            // Get the actual flight we're expecting.
            GetBookingResponse.Flight actualFlight = actualFlights.stream().flatMap(flights -> flights.getFlights().stream())
                    .filter(flight -> flight.getFlightKey().equals(expectedFlight.getFlightKey()))
                    .findFirst()
                    .orElseThrow(AssertionError::new);

            // Assert the actual flight values.
            assertThat(actualFlight.getFlightKey()).isEqualTo(expectedFlight.getFlightKey());
            assertThat(actualFlight.getSector().getCode()).isEqualTo(expectedFlight.getSector().getCode());
            assertThat(actualFlight.getDepartureDateTime()).isEqualTo(expectedFlight.getDepartureDateTime());
            assertThat(actualFlight.getArrivalDateTime()).isEqualTo(expectedFlight.getArrivalDateTime());

            pollingLoop().untilAsserted(() -> {

                // Assert the actual flights passenger values.
                // Basket and booking passengers, I'm not sure why both exist.
                expectedFlight.getPassengers().forEach(expectedBasketPassenger -> {
                    GetBookingResponse.Passenger actualBookingPassenger = actualFlight.getPassengers().stream()
                            .filter(bookingPassenger -> bookingPassenger.getPassengerDetails().getName().getFirstName().equals(expectedBasketPassenger.getPassengerDetails().getName().getFirstName()))
                            .findFirst()
                            .orElseThrow(AssertionError::new);
//                    PLEASE ENABLE BELOW COMMENTED VALIDATION WHEN PERSISTENCE ISSUE IS FIXED
//                            .orElse(
                    // If we're unable to find the passenger by their type then use their email, or throw an error.
                    // (Used on public API when we create a passenger 'on the fly'.)
//                                    actualFlight.getPassengers().stream().filter(
//                                            bookingPassenger -> StringUtils.equals(bookingPassenger.getPassengerDetails().getEmail(), expectedBasketPassenger.getPassengerDetails().getEmail())
//                                    ).findFirst().orElseThrow(AssertionError::new)
//                            );


                    // Check some general passenger things.
                    assertThat(
                            actualBookingPassenger.getPassengerDetails().getName().getFirstName()).isEqualTo(
                            expectedBasketPassenger.getPassengerDetails().getName().getFirstName());

                    assertThat(
                            actualBookingPassenger.getPassengerDetails().getName().getLastName()
                    ).isEqualTo(
                            expectedBasketPassenger.getPassengerDetails().getName().getLastName());

                    assertThat(
                            actualBookingPassenger.getFareProduct().getName()
                    ).isEqualTo(
                            expectedBasketPassenger.getFareProduct().getName());

                    // Check passenger flight options.
                    validateBookedFlightOptionsForPassenger(expectedBasketPassenger, actualBookingPassenger);
                });
            });

        });
    }

    /**
     * Validate booked passenger flight options.
     *
     * @param expectedBasketPassenger The request basket passenger.
     * @param actualBookingPassenger  The response booking passenger.
     */
    private void validateBookedFlightOptionsForPassenger(
            Basket.Passenger expectedBasketPassenger,
            GetBookingResponse.Passenger actualBookingPassenger
    ) {
        // Validate seat options if we're expecting one.
        if (expectedBasketPassenger.getSeat() != null) {
            assertThat(actualBookingPassenger.getSeat().getType()).isEqualTo(expectedBasketPassenger.getSeat().getType());
            assertThat(actualBookingPassenger.getSeat().getBundleCode()).isEqualTo(expectedBasketPassenger.getSeat().getBundleCode());
            assertThat(actualBookingPassenger.getSeat().getName()).isEqualTo(expectedBasketPassenger.getSeat().getName());
            assertThat(actualBookingPassenger.getSeat().getSeatNumber()).isEqualTo(expectedBasketPassenger.getSeat().getSeatNumber());
            assertThat(actualBookingPassenger.getSeat().getQuantity()).isEqualTo(expectedBasketPassenger.getSeat().getQuantity());
            assertThat(actualBookingPassenger.getSeat().getSeatBand()).isEqualTo(expectedBasketPassenger.getSeat().getSeatBand());
        }

        // Validate hold items.
        expectedBasketPassenger.getHoldItems().forEach(
                expectedHoldItem -> validateExpectedHoldItemIsPresent(expectedHoldItem, actualBookingPassenger)
        );
    }

    /**
     * Hold items that are returned to the channel have no unique identifier, this method
     * ensures that every hold item returned has been added to the order.
     *
     * @param expectedHoldItem The hold item we are looking for (needle).
     * @param bookingPassenger Booking passenger to mutate from the response (haystack).
     */
    private void validateExpectedHoldItemIsPresent(AbstractPassenger.HoldItem expectedHoldItem, GetBookingResponse.Passenger bookingPassenger) {
        // Filter the collection based on everything we're looking for, if there's something left over
        // then we know that the order contains the product we're looking for. Once we find it, we should remove it
        // from the booking passenger so we cant 'find it' again.
        AbstractPassenger.HoldItem itemToRemove = bookingPassenger.getHoldItems().stream()
                .filter(holdItem -> holdItem.getCode().equals(expectedHoldItem.getCode()))
                .filter(holdItem -> holdItem.getName().equals(expectedHoldItem.getName()))
                .findAny()
                .orElseThrow(() -> new AssertionError("Unable to find hold item in response."));

        bookingPassenger.getHoldItems().remove(itemToRemove);
    }

    public void isCustomerProfileLinkedWithBooking(BasketContent basketContent) {
        CustomerContext customerContext = basketContent.getCustomerContext();
        GetBookingResponse.BookingContact bookingContact = response.getBookingContext().getBooking().getBookingContact();
        assertThat(customerContext.getName().getFirstName().equals(bookingContact.getName().getFirstName()));
        assertThat(customerContext.getName().getLastName().equals(bookingContact.getName().getLastName()));
        assertThat(customerContext.getEmail().equals(bookingContact.getEmailAddress()));
        assertThat(customerContext.getAddress().getPostalCode().equals(bookingContact.getAddress().getPostalCode()));
    }

    public void arePaymentDetailsRecordedOnBooking(CommitBookingRequestBody commitBooking) {
        PaymentMethod paymentMethod = commitBooking.getPaymentMethods().get(0);
        GetBookingResponse.Payment payment = response.getBookingContext().getBooking().getPayments().get(0);

        assertThat(payment).isEqualTo(paymentMethod);
    }

    public void theBookingDetailsAreReturnedWithAReferenceNumber(String expectedBookingReference) {

        assertThat(response.getBookingContext().getBooking().getBookingReference())
                .isEqualTo(expectedBookingReference).withFailMessage("The booking was not returned by the service.");
    }

    public void theBookingDetailsHasBookingCurrency(String currency) {

        assertThat(response.getBookingContext().getBooking().getBookingCurrency())
                .isEqualTo(currency).withFailMessage("The booking was not returned by the service.");
    }

    public void theBookingDetailsAreReturnedWithStatus(String status) {

        assertThat(response.getBookingContext()
                .getBooking()
                .getBookingStatus()
                .toLowerCase()).isEqualTo(status.toLowerCase());
    }

    public BookingAssertion theCustomerDetailsAreAssociatedWithTheBooking(CustomerProfileService customerProfileService) {
        customerProfileService.invoke();
        CustomerProfileResponse.Customer customer = customerProfileService.getResponse().getCustomer();
        GetBookingResponse.BookingContact bookingContact = response
                .getBookingContext()
                .getBooking()
                .getBookingContact();
        assertThat(bookingContact.getName().getFirstName()).isEqualToIgnoringCase(customer.getBasicProfile().getPersonalDetails().getFirstName());
        assertThat(bookingContact.getName().getLastName()).isEqualToIgnoringCase(customer.getBasicProfile().getPersonalDetails().getLastName());
        assertThat(bookingContact.getAddress().getAddressLine1()).isEqualToIgnoringCase(customer.getBasicProfile().getContactAddress().get(0).getAddressLine1());
        return this;
    }

    public BookingAssertion theBookingAssociatedToCustomer(CustomerProfileResponse.Customer customer, String bookingRef) {
        List<CustomerProfileResponse.RelatedBooking> relatedBookings = customer.getAdvancedProfile().getRelatedBookings().stream().filter(f -> f.getReferenceNumber().equals(bookingRef)).collect(Collectors.toList());
        assertThat(relatedBookings.get(0).getReferenceNumber()).isEqualTo(response.getBookingContext()
                .getBooking()
                .getBookingReference());
        //Need to confirm
        return this;
    }

    public BookingAssertion theBookingTimeIsRecorded() {

        assertThat(response.getBookingContext().getBooking().getBookingDateTime()).startsWith(new DateFormat().today()
                .asYearMonthDay());
        return this;
    }

    public BookingAssertion thePassengersDetailsAreStoredInTheBooking(BasketsResponse basket) {
        //check that all people in the basket are added to the booking with status of booked
        List<Basket.Passenger> paxOut = getOutboundPassengers(basket);
        List<Basket.Passenger> paxIn = getInboundPassengers(basket);
        assertPassengerDetails(paxOut);
        assertPassengerDetails(paxIn);
        return this;
    }

    public BookingAssertion checkPassengerDetailsUpdated(List<GetBookingResponse.Flights> outbounds, String passengerCode, Map<String, Object> passengerDetailsMap) {
        AbstractPassenger.PassengerDetails passengerDetails = outbounds.stream()
                .flatMap(k -> k.getFlights().stream()
                        .flatMap(p -> p.getPassengers().stream()
                                .filter(f -> f.getCode().equalsIgnoreCase(passengerCode))))
                .findFirst().orElse(null).getPassengerDetails();
        if (passengerDetailsMap.get("ejPlusCardNumber") != null) {
            Name name = (Name) passengerDetailsMap.get("name");
            assertThat(name.getFirstName().equals(passengerDetails.getName().getFirstName())).isTrue();
            assertThat(name.getLastName().equals(passengerDetails.getName().getLastName())).isTrue();
            assertThat(passengerDetailsMap.get("ejPlusCardNumber").equals(passengerDetails.getEjPlusCardNumber())).isTrue();
        } else if (passengerDetailsMap.get("name") != null) {
            Name name = (Name) passengerDetailsMap.get("name");
            assertThat(name.getFirstName().equals(passengerDetails.getName().getFirstName())).isTrue();
            assertThat(name.getLastName().equals(passengerDetails.getName().getLastName())).isTrue();
        } else if (passengerDetailsMap.get("email") != null) {
            assertThat(passengerDetailsMap.get("email").equals(passengerDetails.getEmail())).isTrue();
        } else if (passengerDetailsMap.get("nif") != null) {
            assertThat(passengerDetailsMap.get("nif").equals(passengerDetails.getNifNumber())).isTrue();
        } else if (passengerDetailsMap.get("age") != null) {
            Integer age = outbounds.stream().flatMap(f -> f.getFlights().stream().flatMap(p -> p.getPassengers().stream().filter(pc -> pc.getCode().equals(passengerCode)))).findFirst().orElse(null).getAge();
            assertThat(passengerDetailsMap.get("age") == (age)).isTrue();
        }
        return this;
    }


    private void assertPassengerDetails(List<Basket.Passenger> paxOut) {
        for (Basket.Passenger passenger : paxOut) {
            GetBookingResponse.Passenger bookingPax = response.getBookingContext().getBooking().getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .filter(c -> c.getCode().equals(passenger.getCode()))
                    .findFirst()
                    .orElse(null);

            assertThat(bookingPax).isNotNull();
            assertThat(bookingPax.getPassengerDetails()
                    .getName()
                    .getFirstName()).isEqualTo(passenger.getPassengerDetails().getName().getFirstName());
            assertThat(bookingPax.getPassengerDetails()
                    .getName()
                    .getLastName()).isEqualTo(passenger.getPassengerDetails().getName().getLastName());
            assertThat(passenger.getPassengerDetails().getName().getTitle().equalsIgnoreCase(bookingPax.getPassengerDetails()
                    .getName()
                    .getTitle().replace(".", "")));
            assertThat(bookingPax.getPassengerDetails()
                    .getPassengerType()).isEqualToIgnoringCase(passenger.getPassengerDetails().getPassengerType());
            assertThat(bookingPax.getPassengerStatus()).isEqualToIgnoringCase("BOOKED");
        }
    }


    private void assertPassengerAPISDetails(List<Basket.Passenger> paxOut, List<GetBookingResponse.Flights> expectedFlights) {
        if (!CollectionUtils.isEmpty(paxOut)) {
            for (Basket.Passenger passenger : paxOut) {
                AbstractPassenger.PassengerAPIS passengerAPIS = expectedFlights.stream()
                        .flatMap(f -> f.getFlights().stream())
                        .flatMap(p -> p.getPassengers().stream())
                        .filter(c -> c.getCode().equals(passenger.getCode()))
                        .map(AbstractPassenger::getPassengerAPIS)
                        .findFirst()
                        .orElse(null);
                assertThat(passengerAPIS).isNotNull();
                assertThat(passengerAPIS.getDocumentExpiryDate()).isEqualTo(passenger.getPassengerAPIS().getDocumentExpiryDate());
                assertThat(passengerAPIS.getCountryOfIssue()).isEqualTo(passenger.getPassengerAPIS().getCountryOfIssue());
                assertThat(passengerAPIS.getDateOfBirth()).isEqualTo(passenger.getPassengerAPIS().getDateOfBirth());
                assertThat(passengerAPIS.getDocumentNumber()).isEqualTo(passenger.getPassengerAPIS().getDocumentNumber());
                assertThat(passengerAPIS.getName().getFirstName()).isEqualTo(passenger.getPassengerAPIS().getName().getFirstName());
                assertThat(passengerAPIS.getName().getLastName()).isEqualTo(passenger.getPassengerAPIS().getName().getLastName());
                assertThat(passengerAPIS.getName().getTitle()).isEqualTo(passenger.getPassengerAPIS().getName().getTitle());
            }
        }
    }

    private void assertPassengerAPIS(List<Basket.Passenger> paxOut, List<GetBookingResponse.Flights> expectedFlights) {
        if (!CollectionUtils.isEmpty(paxOut)) {
            for (Basket.Passenger passenger : paxOut) {
                AbstractPassenger.PassengerAPIS passengerAPIS = expectedFlights.stream()
                      .flatMap(f -> f.getFlights().stream())
                      .flatMap(p -> p.getPassengers().stream())
                      .filter(c -> c.getCode().equals(passenger.getCode()))
                      .map(AbstractPassenger::getPassengerAPIS)
                      .findFirst()
                      .orElse(null);
                assertThat(passengerAPIS).isNotNull();
                assertThat(passengerAPIS.getDocumentExpiryDate()).isEqualTo(passenger.getPassengerAPIS().getDocumentExpiryDate());
                assertThat(passengerAPIS.getCountryOfIssue()).isEqualTo(passenger.getPassengerAPIS().getCountryOfIssue());
                assertThat(passengerAPIS.getDocumentNumber()).isEqualTo(passenger.getPassengerAPIS().getDocumentNumber());
                assertThat(passengerAPIS.getName().getFirstName()).isEqualTo(passenger.getPassengerAPIS().getName().getFirstName());
                assertThat(passengerAPIS.getName().getLastName()).isEqualTo(passenger.getPassengerAPIS().getName().getLastName());
                assertThat(passengerAPIS.getName().getTitle()).isEqualTo(passenger.getPassengerAPIS().getName().getTitle());
            }
        }
    }

    private void assertPassengerEjPlusDetails(List<Basket.Passenger> paxOut, List<GetBookingResponse.Flights> expectedFlights) {
        String ejPlus = "";
        if (!CollectionUtils.isEmpty(paxOut)) {
            for (Basket.Passenger passenger : paxOut) {
                GetBookingResponse.Passenger bookingPax = expectedFlights.stream()
                        .flatMap(f -> f.getFlights().stream())
                        .flatMap(p -> p.getPassengers().stream())
                        .filter(c -> c.getCode().equals(passenger.getCode()))
                        .filter(passenger1 -> passenger1.getPassengerDetails().getEjPlusCardNumber() != null || !ejPlus.equalsIgnoreCase(passenger1.getPassengerDetails().getEjPlusCardNumber()))
                        .findFirst()
                        .orElse(null);
                assertThat(bookingPax).isNotNull();
                assertThat(bookingPax.getPassengerDetails().getEjPlusCardNumber()).isEqualToIgnoringCase(passenger.getPassengerDetails().getEjPlusCardNumber());
            }
        }
    }

    private void assertPassengerSeatDetails(List<Basket.Passenger> pax) {
        if (!CollectionUtils.isEmpty(pax)) {
            for (Basket.Passenger passenger : pax) {
                AbstractPassenger.Seat seat =
                        response.getBookingContext().getBooking().getOutbounds().stream()
                                .flatMap(f -> f.getFlights().stream())
                                .flatMap(p -> p.getPassengers().stream())
                                .filter(c -> c.getCode().equals(passenger.getCode()))
                                .map(AbstractPassenger::getSeat)
                                .findFirst()
                                .orElse(null);
                assertThat(seat).isNotNull();
                validateSeatDetails(passenger, seat);

            }
        }
    }

    private void validateSeatDetails(Basket.Passenger passenger, AbstractPassenger.Seat seat) {
        assertThat(seat.getSeatNumber()).isEqualTo(passenger.getSeat().getSeatNumber());
        assertThat(seat.getCode()).isEqualTo(passenger.getSeat().getCode());
        assertThat(seat.getSeatBand()).isEqualTo(passenger.getSeat().getSeatBand());
    }

    private void validateAdditionalSeatFareProductsDetails(AbstractPassenger.Seat passengerSeat, AbstractPassenger.Seat seat) {
        assertThat(passengerSeat.getSeatNumber()).isEqualTo(seat.getSeatNumber());
        assertThat(passengerSeat.getCode()).isEqualTo(seat.getCode());
        assertThat(passengerSeat.getSeatBand()).isEqualTo(seat.getSeatBand());
    }

    private void assertPassengerProductDetails(List<Basket.Passenger> basketPaxWithHoldItems, List<GetBookingResponse.Flights> getBookingResponseFlightsList) {
        if (!CollectionUtils.isEmpty(basketPaxWithHoldItems)) {
            for (Basket.Passenger basketPassenger : basketPaxWithHoldItems) {
                List<AbstractPassenger.HoldItem> passengerGetBookingResponseHoldItems = getBookingResponseFlightsList
                        .stream()
                        .flatMap(f -> f.getFlights().stream())
                        .flatMap(p -> p.getPassengers().stream())
                        .filter(c -> c.getCode().equals(basketPassenger.getCode()))
                        .flatMap(passenger1 -> passenger1.getHoldItems().stream())
                        .collect(Collectors.toList());

                basketPassenger.getHoldItems().forEach(passengerHoldBag -> {
                    AbstractPassenger.HoldItem holdItem = passengerGetBookingResponseHoldItems.stream().filter(
                            pe -> passengerHoldBag.getCode().equals(pe.getCode())
                    ).findAny().orElseThrow(() -> new AssertionError("Product: " + passengerHoldBag.getCode() + " not found!"));
                    if (holdItem == null)
                        assertThat(passengerHoldBag).isNull();
                    else {
                        assertThat(passengerHoldBag.getCode()).isEqualToIgnoringCase(holdItem.getCode());
                        assertThat(passengerHoldBag.getPricing().getBasePrice().equals(holdItem.getPricing().getBasePrice())).isTrue();
                        if (!passengerHoldBag.getExtraWeight().isEmpty()) {
                            List<AbstractPassenger.ExtraWeight> holdItemExtraWeight = holdItem.getExtraWeight();
                            passengerHoldBag.getExtraWeight().forEach(
                                    extraWeight -> {
                                        assertThat(extraWeight.getCode()).isEqualToIgnoringCase(holdItemExtraWeight.get(0).getCode());
                                        assertThat(extraWeight.getPricing().getBasePrice()).isEqualTo(holdItemExtraWeight.get(0).getPricing().getBasePrice());
                                        assertThat(extraWeight.getPricing().getTotalAmountWithDebitCard()).isEqualTo(holdItemExtraWeight.get(0).getPricing().getTotalAmount());
                                        assertThat(extraWeight.getName().equalsIgnoreCase(holdItemExtraWeight.get(0).getName()));
                                    }
                            );
                        } else
                            assertThat(passengerHoldBag.getPricing().getTotalAmountWithDebitCard().equals(holdItem.getPricing().getTotalAmount())).isTrue();

                    }

                });

            }
        }
    }

    private void assertPassengerHasCabinbagDetails(List<Basket.Passenger> pax, List<GetBookingResponse.Flights> flightsList) {
        if (!CollectionUtils.isEmpty(pax)) {
            for (Basket.Passenger passenger : pax) {
                List<AbstractPassenger.CabinItem> cabinItems = flightsList.stream()
                        .flatMap(f -> f.getFlights().stream())
                        .flatMap(p -> p.getPassengers().stream())
                        .filter(c -> c.getCode().equals(passenger.getCode()))
                        .map(AbstractPassenger::getCabinItems)
                        .findFirst()
                        .orElse(null);
                cabinItems.forEach(cabinItem -> {
                    AbstractPassenger.CabinItem holdItem = passenger.getCabinItems().stream().filter(
                            pe -> pe.getCode().equals(cabinItem.getCode())
                    ).findAny().orElseThrow(() -> new AssertionError("Product: " + cabinItem.getCode() + " not found!"));
                    if (holdItem == null)
                        assertThat(cabinItem).isNull();
                    else {
                        assertThat(cabinItem.getCode()).isEqualTo(holdItem.getCode());
                        assertThat(cabinItem.getPricing().getBasePrice()).isEqualTo(holdItem.getPricing().getBasePrice());
                        assertThat(cabinItem.getPricing().getTotalAmount()).isEqualTo(holdItem.getPricing().getTotalAmountWithDebitCard());
                    }

                });

            }
        }
    }

    public BookingAssertion thePassengersDetailsHasExpectedBundleDetailsBooking(BasketsResponse basket) {
        //check that all people in the basket are added to the booking with status of booked
        List<GetBookingResponse.Flights> bookedFlights = response.getBookingContext().getBooking().getOutbounds();
        bookedFlights.stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .forEach(bookingPassenger ->
                        assertThat(bookingPassenger.getFareProduct().getBundleCode()).isEqualToIgnoringCase(bookingPassenger.getFareProduct().getName()));

        return this;
    }

    public BookingAssertion thePassengerHasAPISDetails(BasketsResponse basket) {
        //check that all people in the basket are added to the booking with status of booked
        List<Basket.Passenger> paxOut =
                getOutboundPassengers(basket).stream().filter(passenger -> passenger.getPassengerAPIS() != null)
                        .collect(Collectors.toList());
        List<Basket.Passenger> paxIn = getInboundPassengers(basket).stream().filter(passenger -> passenger.getPassengerAPIS() != null)
                .collect(Collectors.toList());
        try {
            assertPassengerAPISDetails(paxOut, response.getBookingContext().getBooking().getOutbounds());
            assertPassengerAPISDetails(paxIn, response.getBookingContext().getBooking().getInbounds());
        } catch (NullPointerException e) {
            LOG.error(e);
            assertPassengerAPISDetails(paxOut, response.getBookingContext().getBooking().getOutbounds());
            assertPassengerAPISDetails(paxIn, response.getBookingContext().getBooking().getInbounds());
        }

        return this;
    }

    public BookingAssertion thePassengerContainsAPISDetails(BasketsResponse basket) {
        //check that all people in the basket are added to the booking with status of booked
        List<Basket.Passenger> paxOut =
              getOutboundPassengers(basket).stream().filter(passenger -> passenger.getPassengerAPIS() != null)
                    .collect(Collectors.toList());
        List<Basket.Passenger> paxIn = getInboundPassengers(basket).stream().filter(passenger -> passenger.getPassengerAPIS() != null)
              .collect(Collectors.toList());
        try {
            assertPassengerAPIS(paxOut, response.getBookingContext().getBooking().getOutbounds());
            assertPassengerAPIS(paxIn, response.getBookingContext().getBooking().getInbounds());
        } catch (NullPointerException e) {
            LOG.error(e);
            assertPassengerAPIS(paxOut, response.getBookingContext().getBooking().getOutbounds());
            assertPassengerAPIS(paxIn, response.getBookingContext().getBooking().getInbounds());
        }

        return this;
    }

    public BookingAssertion thePassengerHasEjPlusNumberDetails(BasketsResponse basket) {
        //check that all people in the basket are added to the booking with status of booked
        List<Basket.Passenger> paxOut = getOutboundPassengers(basket);

        List<Basket.Passenger> paxIn = getInboundPassengers(basket);
        assertPassengerEjPlusDetails(paxOut, response.getBookingContext().getBooking().getOutbounds());
        assertPassengerEjPlusDetails(paxIn, response.getBookingContext().getBooking().getInbounds());
        return this;
    }

    public BookingAssertion thePassengerHasSeatDetails(BasketsResponse basket) {
        //check that all people in the basket are added to the booking with status of booked
        List<Basket.Passenger> paxOut = getOutboundPassengers(basket).stream().
                filter(passenger -> passenger.getSeat() != null).collect(Collectors.toList());
        List<Basket.Passenger> paxIn = getInboundPassengers(basket).stream().
                filter(passenger -> passenger.getSeat() != null).collect(Collectors.toList());
        assertPassengerSeatDetails(paxOut);
        assertPassengerSeatDetails(paxIn);
        return this;
    }

    public BookingAssertion thePassengerHasProductDetails(BasketsResponse basket) {
        List<Basket.Passenger> basketPaxOutBound = getOutboundPassengers(basket).stream().
                filter(passenger -> passenger.getHoldItems() != null && !passenger.getHoldItems().isEmpty())
                .collect(Collectors.toList());
        List<Basket.Passenger> basketPaxInBound = getInboundPassengers(basket).stream().
                filter(passenger -> passenger.getHoldItems() != null && !passenger.getHoldItems().isEmpty())
                .collect(Collectors.toList());
        assertPassengerProductDetails(basketPaxOutBound, response.getBookingContext().getBooking().getOutbounds());
        assertPassengerProductDetails(basketPaxInBound, response.getBookingContext().getBooking().getInbounds());
        return this;
    }

    public BookingAssertion thePassengerHasCabinBagDetails(BasketsResponse basket) {
        List<Basket.Passenger> paxOut = getOutboundPassengers(basket).stream()
                .filter(passenger -> passenger.getCabinItems() != null && !passenger.getCabinItems().isEmpty())
                .collect(Collectors.toList());

        List<Basket.Passenger> paxIn = getInboundPassengers(basket).stream()
                .filter(passenger -> passenger.getCabinItems() != null && !passenger.getCabinItems().isEmpty())
                .collect(Collectors.toList());
        assertPassengerHasCabinbagDetails(paxOut, response.getBookingContext().getBooking().getOutbounds());
        assertPassengerHasCabinbagDetails(paxIn, response.getBookingContext().getBooking().getInbounds());
        return this;
    }

    public BookingAssertion thePassengerHasAdditionalSeatDetails(BasketsResponse basket) {
        List<Basket.Passenger> paxOut = getOutboundPassengers(basket).stream()
                .filter(passenger -> passenger.getAdditionalSeats() != null && !passenger.getAdditionalSeats().isEmpty())
                .collect(Collectors.toList());

        List<Basket.Passenger> paxIn = getInboundPassengers(basket).stream()
                .filter(passenger -> passenger.getAdditionalSeats() != null && !passenger.getAdditionalSeats().isEmpty())
                .collect(Collectors.toList());
        assertPassengerHasAdditionalSeatDetails(paxOut, response.getBookingContext().getBooking().getOutbounds());
        assertPassengerHasAdditionalSeatDetails(paxIn, response.getBookingContext().getBooking().getInbounds());
        return this;
    }

    public BookingAssertion theBookingHasDetailsOfAllowedDocuments() {
        List<String> bookingAllowedDocuments = response.getBookingContext().getAllowedDocuments();
        assertThat(bookingAllowedDocuments).containsOnlyElementsOf(Arrays.asList(this.allowedDocuments));
        return this;
    }


    private GetBookingResponse.Flight findFirstDepartingFlight() {
        List<GetBookingResponse.Flights> journeyList = response.getBookingContext().getBooking().getOutbounds();
        GetBookingResponse.Flight firstFlight = null;

        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(journeyList)) {
            List<GetBookingResponse.Flight> flightList = journeyList.stream().map(journey -> journey.getFlights().get(0))
                    .collect(Collectors.toList());

            Optional<GetBookingResponse.Flight> firstDepartingFlight = flightList.stream()
                    .min((f1, f2) -> (parseDate(f1.getDepartureDateTime()).compareTo(parseDate(f2.getDepartureDateTime()))));
            firstFlight = firstDepartingFlight.isPresent() ? firstDepartingFlight.get() : null;
        }

        return firstFlight;
    }

    protected Calendar parseDate(final String stringDate) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(FLIGHT_ARRIVAL_OR_DEPARTURE_DATE_FORMAT);
        try {
            Date date = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendarDate = dateFormat.getCalendar();
        return calendarDate;
    }


    private void assertPassengerHasAdditionalSeatDetails(List<Basket.Passenger> pax, List<GetBookingResponse.Flights> flightsList) {
        if (!CollectionUtils.isEmpty(pax)) {
            for (Basket.Passenger passenger : pax) {
                List<AbstractPassenger.AdditionalSeat> additionalSeats = flightsList.stream()
                        .flatMap(f -> f.getFlights().stream())
                        .flatMap(p -> p.getPassengers().stream())
                        .filter(c -> c.getCode().equals(passenger.getCode()))
                        .map(GetBookingResponse.Passenger::getAdditionalSeats)
                        .findFirst()
                        .orElse(null);
                additionalSeats.forEach(seat -> {
                    AbstractPassenger.AdditionalSeat passengerSeat = passenger.getAdditionalSeats().stream().filter(
                            pe -> pe.getFareProduct().getCode().equals(seat.getFareProduct().getCode())
                    ).findAny().orElseThrow(() -> new AssertionError("Product: " + seat.getFareProduct().getCode() + " not found!"));
                    assertThat(passengerSeat.getFareProduct().getCode()).isEqualTo(seat.getFareProduct().getCode());
                    assertThat(passengerSeat.getFareProduct().getType()).isEqualTo(seat.getFareProduct().getType());
                    assertThat(passengerSeat.getFareProduct().getPricing().getBasePrice()).isEqualTo(seat.getFareProduct().getPricing().getBasePrice());
                    if (!java.util.Objects.isNull(passengerSeat.getSeat()))
                        validateAdditionalSeatFareProductsDetails(passengerSeat.getSeat(), seat.getSeat());
                });

            }
        }
    }

    private List<Basket.Passenger> getOutboundPassengers(BasketsResponse basket) {
        return basket.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());
    }

    private List<Basket.Passenger> getInboundPassengers(BasketsResponse basket) {
        return basket.getBasket().getInbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());
    }

    public void theBookingHasCommentsAdded(String flag, String expectedCommentCode, String expectedComment) {
        List<Comment> comments = response.getBookingContext().getBooking().getComments();
        if ("should".equalsIgnoreCase(flag)) {
            assertThat(comments).isNotNull();
            assertThat(comments.size()).isNotZero();
            assertThat(comments.get(0).getCode()).isEqualTo(expectedCommentCode);
            assertThat(comments.get(0).getDescription()).isEqualTo(expectedComment);

        } else
            assertThat(comments.size()).isEqualTo(0);
    }

    public BookingAssertion thePassengerCheckedin(BasketsResponse basket, String passengerStatus) {
        List<Basket.Passenger> checkedIn = getOutboundPassengers(basket);
        validateThatPassengertIsCheckIn(checkedIn, passengerStatus);
        return this;
    }

    public BookingAssertion bookingStatus(String bookingStatus) {
        assertThat(response.getBookingContext().getBooking().getBookingStatus().equals(bookingStatus))
                .withFailMessage("Booking status is not cancelled after doing a refund")
                .isTrue();
        return this;
    }

    public void validateThatPassengertIsCheckIn(List<Basket.Passenger> checkedInPassenger, String passengerStatus) {
        pollingLoop().untilAsserted(() -> {

            for (Basket.Passenger passenger : checkedInPassenger) {
                response.getBookingContext().getBooking().getOutbounds().stream()
                        .flatMap(flights -> flights.getFlights().stream())
                        .flatMap(passengers -> passengers.getPassengers().stream())
                        .filter(c -> c.getPassengerStatus().equals(passengerStatus))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("PassengerStatus: " + passenger.getPassengerStatus() + " no passenger checked in!"));
            }
        });
    }

    public void validateAllPassengerCheckIn(List<GetBookingResponse.Passenger> checkedInPassenger, String passengerStatus) {
        pollingLoop().untilAsserted(() -> {
            checkedInPassenger.stream()
                    .filter(c -> c.getPassengerStatus().equals(passengerStatus))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("PassengerStatus: not checked in!"));
        });
    }

    public void checkCustomerProfileNotIsUpdated(CustomerProfileResponse customerProfile, GetBookingResponse getBooking, String parameter, Passengers updatePassengersRequestBody) {
        AddUpdateSavedPassengerRequestBody savedPassengerRequest = (AddUpdateSavedPassengerRequestBody) testData.getData("UpdateSavedPassengerRequest");

        String oldParameter;
        String newParameter;

        switch (parameter) {
            case "age":
                int oldAge = savedPassengerRequest.getAge();
                int newAge = updatePassengersRequestBody.getPassengers().get(0).getAge();

                assertThat(getBooking.getBookingContext().getBooking().getOutbounds().stream().map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                        .flatMap(Collection::stream)
                        .anyMatch(passenger -> passenger.getAge().equals(newAge)))
                        .withFailMessage("The new passenger " + parameter + newAge + " doesn't exist in the booking")
                        .isTrue();

                assertThat(customerProfile.getCustomer().getAdvancedProfile().getSavedPassengers().stream()
                        .map(Profile::getAge)
                        .noneMatch(age -> age.equals(oldAge)))
                        .withFailMessage("The old passenger " + parameter + oldAge + " doesn't exist")
                        .isTrue();

                assertThat(customerProfile.getCustomer().getAdvancedProfile().getSavedPassengers().stream()
                        .map(Profile::getAge)
                        .anyMatch(age -> age.equals(newAge)))
                        .withFailMessage("The old passenger " + parameter + newAge + " doesn't exist")
                        .isTrue();
                oldParameter = null;
                newParameter = null;
                break;

            case "ejPlusCardNumber":
                newParameter = updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getEjPlusCardNumber();

                assertThat(getBooking.getBookingContext().getBooking().getOutbounds().stream().map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                        .flatMap(Collection::stream)
                        .anyMatch(passenger -> passenger.getPassengerDetails().getEjPlusCardNumber().equals(newParameter)))
                        .withFailMessage("The new passenger " + parameter + newParameter + " doesn't exist in the booking")
                        .isTrue();

                assertThat(customerProfile.getCustomer().getAdvancedProfile().getSavedPassengers().stream()
                        .map(Profile::getEjPlusCardNumber)
                        .anyMatch(ejPlus -> ejPlus.equals(newParameter)))
                        .withFailMessage("The old passenger " + parameter + newParameter + " doesn't exist")
                        .isTrue();

                oldParameter = null;

                break;
            case "email":
                oldParameter = savedPassengerRequest.getEmail();
                newParameter = updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getEmail();
                break;
            case "phoneNumber":
                oldParameter = savedPassengerRequest.getPhoneNumber();
                newParameter = updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getPhoneNumber();
                break;
            case "nifNumber":
                oldParameter = savedPassengerRequest.getNifNumber();
                newParameter = updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getNifNumber();
                break;
            case "ssr":
                String newSsr = updatePassengersRequestBody.getPassengers().get(0).getSpecialRequests().getSsrs().get(0).getCode();

                assertThat(getBooking.getBookingContext().getBooking().getOutbounds().stream().map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                        .flatMap(Collection::stream)
                        .anyMatch(passenger -> passenger.getSpecialRequests().get(0).getSsrs().get(0).getSsrCode().equals(newSsr)))
                        .withFailMessage("The new passenger " + parameter + newSsr + " doesn't exist in the booking")
                        .isTrue();

                assertThat(customerProfile.getCustomer().getAdvancedProfile().getSavedPassengers().stream()
                        .map(Profile::getSavedSSRs)
                        .map(SpecialRequest::getSsrs)
                        .flatMap(Collection::stream)
                        .anyMatch(savedSSR -> savedSSR.getCode().equals(newSsr)))
                        .withFailMessage("The new passenger " + parameter + newSsr + " doesn't exist")
                        .isTrue();
                oldParameter = null;
                newParameter = null;

                break;
            default:
                oldParameter = "";
                newParameter = "";
        }


        if (oldParameter != null) {

            assertThat(getBooking.getBookingContext().getBooking().getOutbounds().stream().map(AbstractFlights::getFlights).flatMap(Collection::stream)
                    .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                    .flatMap(Collection::stream)
                    .anyMatch(passenger -> {
                        try {
                            return passenger.getPassengerDetails().getClass().getMethod("get" + StringUtils.capitalize(parameter)).invoke(passenger.getPassengerDetails()).equals(newParameter);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            LOG.error("Passenger details field changed", e);
                            return false;
                        }
                    }))
                    .withFailMessage("The new passenger " + parameter + newParameter + " doesn't exist in the booking")
                    .isTrue();

            assertThat(customerProfile.getCustomer().getAdvancedProfile().getSavedPassengers().stream()
                    .noneMatch(profile -> {
                        try {
                            return profile.getClass().getMethod("get" + StringUtils.capitalize(parameter)).invoke(profile).equals(oldParameter);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            LOG.error("Customer profile field changed", e);
                            return false;
                        }
                    }))
                    .withFailMessage("The old passenger " + parameter + oldParameter + " doesn't exist")
                    .isTrue();

            assertThat(customerProfile.getCustomer().getAdvancedProfile().getSavedPassengers().stream()
                    .anyMatch(profile -> {
                        try {
                            return profile.getClass().getMethod("get" + StringUtils.capitalize(parameter)).invoke(profile).equals(newParameter);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            LOG.error("Customer profile field changed", e);
                            return false;
                        }
                    }))
                    .withFailMessage("The old passenger " + parameter + newParameter + " doesn't exist")
                    .isTrue();
        }

    }


    public BookingAssertion checkNewCustomerProfileIsUpdated(CustomerProfileResponse customerProfile, GetBookingResponse getBooking, String parameter, Passengers updatePassengersRequestBody) {

        AddUpdateSavedPassengerRequestBody savedPassengerRequest = (AddUpdateSavedPassengerRequestBody) testData.getData("UpdateSavedPassengerRequest");

        if ("title".equalsIgnoreCase(parameter)) {
            String oldTitle = savedPassengerRequest.getTitle().toLowerCase();
            String newTitle = updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getName().getTitle().toLowerCase();

            List<String> titlesBooking = getBooking.getBookingContext().getBooking().getOutbounds().stream()
                    .flatMap(flights -> flights.getFlights().stream())
                    .flatMap(passengers -> passengers.getPassengers().stream())
                    .map(passenger -> passenger.getPassengerDetails().getName().getTitle().toLowerCase())
                    .collect(Collectors.toList());

            assertThat(titlesBooking.contains(newTitle))
                    .withFailMessage("The new passenger " + parameter + " " + newTitle + " doesn't exist in the booking")
                    .isEqualTo(true);

            List<String> titlesCustomer = customerProfile.getCustomer().getAdvancedProfile().getSavedPassengers().stream()
                    .map(profile -> profile.getTitle().toLowerCase())
                    .collect(Collectors.toList());

            assertThat(titlesCustomer.contains(oldTitle))
                    .withFailMessage("The old passenger " + parameter + " " + oldTitle + " doesn't exist")
                    .isEqualTo(true);

            assertThat(titlesCustomer.contains(newTitle))
                    .withFailMessage("The new passenger " + parameter + " " + newTitle + " doesn't exist")
                    .isEqualTo(true);

        } else {

            String oldName = savedPassengerRequest.getFirstName().toLowerCase();
            String newName = updatePassengersRequestBody.getPassengers().get(0).getPassengerDetails().getName().getFirstName().toLowerCase();

            List<String> firstNameBooking = getBooking.getBookingContext().getBooking().getOutbounds().stream()
                    .flatMap(flights -> flights.getFlights().stream())
                    .flatMap(passengers -> passengers.getPassengers().stream())
                    .map(passenger -> passenger.getPassengerDetails().getName().getFirstName().toLowerCase())
                    .collect(Collectors.toList());

            assertThat(firstNameBooking.contains(newName))
                    .withFailMessage("The new passenger " + parameter + " " + newName + " doesn't exist in the booking")
                    .isEqualTo(true);

            List<String> firstNameCustomer = customerProfile.getCustomer().getAdvancedProfile().getSavedPassengers().stream()
                    .map(profile -> profile.getFirstName().toLowerCase())
                    .collect(Collectors.toList());

            assertThat(firstNameCustomer.contains(oldName))
                    .withFailMessage("The old passenger " + parameter + " " + oldName + " doesn't exist")
                    .isEqualTo(true);

            assertThat(firstNameCustomer.contains(newName))
                    .withFailMessage("The new passenger " + parameter + " " + newName + " doesn't exist")
                    .isEqualTo(true);
        }
        return this;
    }


    public BookingAssertion checkAddSSRInTheBooking(GetBookingResponse getBooking) {

        Passengers updatePassengersRequestBody = (Passengers) testData.getData("updatePassengerRequestBody");
        String newSsr = updatePassengersRequestBody.getPassengers().get(0).getSpecialRequests().getSsrs().get(0).getCode();

        assertThat(getBooking.getBookingContext().getBooking().getOutbounds().stream().map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .anyMatch(passenger -> passenger.getSpecialRequests().get(0).getSsrs().get(0).getSsrCode().equals(newSsr)))
                .withFailMessage("The new passenger " + newSsr + " doesn't exist in the booking")
                .isTrue();

        return this;
    }

    public BookingAssertion checkThatAllPassengerHaveSeatAndAdditionalFare(GetBookingResponse getBooking) throws EasyjetCompromisedException {

        getBooking.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .forEach(passengers -> assertThat(passengers.stream()
                        .flatMap(passenger -> passenger.getAdditionalSeats().stream())
                        .anyMatch(passenger -> passenger.getSeatReasonCode().contains("COMFORT")))
                        .isTrue()
                );

        getBooking.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .forEach(passengers -> assertThat(passengers.stream()
                        .anyMatch(s -> s.getSeat().getSeatNumber().length() >= 1))
                        .isTrue()
                        .withFailMessage("Seat number not found")
                );
        return this;
    }

    public BookingAssertion checkBookingRequestForInfantOnSeatPassengerAssoication(GetBookingResponse getBooking) {

        List<GetBookingResponse.Passenger> passenger = getBooking.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .filter(p -> p.getCode().contains(testData.getPassengerId()))
                .collect(Collectors.toList());

        assertThat(!passenger.get(0).getInfantsOnSeat().isEmpty())
                .isTrue()
                .withFailMessage("No infant on lap attached to the first passenger");

        return this;

    }

    public BookingAssertion checkAdditionalInformation(String code) {
        GetBookingResponse getBooking = testData.getData(SerenityFacade.DataKeys.SERVICE);
        return this;
    }

    public BookingAssertion checkBordingPassStatus(String status) {
        GetBookingResponse getBooking = testData.getData(SerenityFacade.DataKeys.SERVICE);

        assertThat(getBooking.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .anyMatch(passenger -> passenger.getBoardingPassStatus().equalsIgnoreCase(status
                ))).withFailMessage("The Boarding Pass Status is wrong Expected is " + status).isTrue();

        return this;

    }

    public BookingAssertion bookingTypeIsRight(String expectedBookingType) {
        String actualBookingType = response.getBookingContext().getBooking().getBookingType();
        assertThat(actualBookingType)
                .withFailMessage("The booking type is wrong: expected " + expectedBookingType + "; actual " + actualBookingType)
                .isEqualTo(expectedBookingType);
        return this;
    }

    public void theBookingHasTheCorrectAllowedFunctions(List<BookingPermissionModel> expectedPermissions) {
        List<String> res = response.getBookingContext().getAllowedFunctions();

        assertThat(!res.isEmpty() && res.size() == expectedPermissions.size())
                .isTrue()
                .withFailMessage("The size of the allowed functions returned doesn't match the expected results");

        expectedPermissions.forEach(
                expectedPermission -> assertThat(res.contains(expectedPermission.getCapability()))
                        .isTrue()
                        .withFailMessage("Expected function is not in the response")
        );
    }

    public BookingAssertion thePassengerCheckedin(GetBookingResponse getBookingResponse, String passengerStatus) {
        List<GetBookingResponse.Passenger> checkedIn = getOutboundPassengers(getBookingResponse);
        validateAllPassengerCheckIn(checkedIn, passengerStatus);
        return this;
    }

    private List<GetBookingResponse.Passenger> getOutboundPassengers(GetBookingResponse getBookingResponse) {
        return getBookingResponse.getBookingContext().getBooking().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(p -> p.getPassengers().stream())
                .collect(Collectors.toList());
    }

    public BookingAssertion getTransactionType(String transType) {
        assertThat(response.getBookingContext().getBooking().getPayments().stream()
                .anyMatch(p -> p.getTransactionType().equalsIgnoreCase(transType)))
                .isTrue()
                .withFailMessage("Payment transaction status not found");
        return this;
    }

    public BookingAssertion getFlightActiveStatus(){
     assertThat(response.getBookingContext().getBooking().getOutbounds().stream()
                .map(f -> f.getFlights().stream().filter(e -> e.getActive().equals(false))).findFirst().orElseThrow(() -> new IllegalArgumentException("No Outbounds journey present in the booking")).findFirst().orElseThrow(() -> new IllegalArgumentException("No Flight with status inactive in outbound journey"))
                .getActive()
                .compareTo(false));
        return this;
    }

    public BookingAssertion getFlightEntryStatus() {
        assertThat(response.getBookingContext().getBooking().getOutbounds().stream()
                .map(f -> f.getFlights().stream().filter(e -> e.getEntryStatus().equalsIgnoreCase("changed"))).findFirst().orElseThrow(() -> new IllegalArgumentException("No Outbounds journey present in the booking")).findFirst().orElseThrow(() -> new IllegalArgumentException("No Flight with entry status changed in outbound journey"))
                .getEntryStatus()
                .compareToIgnoreCase("changed"));
        return this;
    }

    public BookingAssertion getPassengerActiveStatus(){
        assertThat(response.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .anyMatch(passenger -> passenger.getActive().equals(false))).isEqualTo(true);
        return this;
    }

    public BookingAssertion getPassengerEntryStatus() {
        assertThat(response.getBookingContext().getBooking().getOutbounds().stream()
                .map(AbstractFlights::getFlights)
                .flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers)
                .flatMap(Collection::stream)
                .anyMatch(passenger -> passenger.getEntryStatus().equalsIgnoreCase("changed"))).isEqualTo(true);
        return this;
    }

    public BookingAssertion verifyBasketTotalWithBookingTotal(Double basketTotal){
        assertThat(response.getBookingContext().getBooking().getPriceSummary().getTotalAmount())
                .withFailMessage("Basket Total and Booking Total is not matching")
                .isEqualTo(basketTotal);
        return this;
    }

    public BookingAssertion verifyMarginValueIntheOrder(BigDecimal expectedMarginValue, BookingDao bookingDao) {
        List<String> marginValues = bookingDao.getMarginValueFromOrderEntry(testData.getData(BOOKING_ID));

        for (String marginValue : marginValues) {
            assertThat(marginValue)
                    .withFailMessage("Margin value in the order entry is not right")
                    .isEqualTo(String.valueOf(expectedMarginValue.doubleValue()));
        }
        return this;
    }

    public BookingAssertion checkAdditionalFareActiveStatus(){
        assertThat(response.getBookingContext().getBooking().getOutbounds().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Outbounds journey present in the booking"))
                .getFlights().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No flight found for outbound journey"))
                .getPassengers().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No passengers found in the flight"))
                .getAdditionalSeats().stream().filter(s -> s.getFareProduct().getActive().equals(false))
        ).withFailMessage("No additional seat with status active = false").isNotNull();
        return this;
    }

    public BookingAssertion checkAdditionalFareEntryStatus() {
        assertThat(response.getBookingContext().getBooking().getOutbounds().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Outbounds journey present in the booking"))
                .getFlights().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No flight found for outbound journey"))
                .getPassengers().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No passengers found in the flight"))
                .getAdditionalSeats().stream().filter(s -> "CHANGED".equalsIgnoreCase(s.getFareProduct().getEntryStatus()))
        ).withFailMessage("No additional seat with entry status = CHANGED").isNotNull();
        return this;
    }

    /**
     * the following step, check the boarding pass for passenger
     * the booking should have two flight, each with 1 adult passenger
     * the method assert the status of the passenger on first flight to NEED_TO_RERETRIEVE
     * the method assert the status of the passenger on second flight to NEVER_RETRIEVED
     * @return
     */
    public BookingAssertion verifyBoardingPassStatusForPassenger(String numFlight, String boardingPassStatus) {
        List<GetBookingResponse.Flight> flights = response.getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).collect(Collectors.toList());
        switch (numFlight) {
            case "first":
                assertThat(flights.stream().flatMap(p -> p.getPassengers().stream()).filter(p -> p.getCode().equalsIgnoreCase(testData.getData(SerenityFacade.DataKeys.PASSENGER_CODES))).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger with desired code " + testData.getData(SerenityFacade.DataKeys.PASSENGER_CODES))).getBoardingPassStatus())
                        .withFailMessage("The boarding pass for passenger is not the expected " + boardingPassStatus)
                        .isEqualTo(boardingPassStatus);
                break;
            case "second":
                assertThat(flights.stream().flatMap(p -> p.getPassengers().stream()).filter(p -> !p.getCode().equalsIgnoreCase(testData.getData(SerenityFacade.DataKeys.PASSENGER_CODES))).findFirst().orElseThrow(() -> new IllegalArgumentException("No other passengers on booking")).getBoardingPassStatus())
                        .withFailMessage("The boarding pass for passenger is not the expected " + boardingPassStatus)
                        .isEqualTo(boardingPassStatus);
                break;
            default:
                throw new IllegalArgumentException("At least 2 flight are currently allow from the logic");
        }
        return this;
    }
    public void checkCorrectPaymentDetailsDisplayed(GetBookingResponse.Payment payment, String paymentType, String currencyType) {
        assertThat(payment.getType()).isEqualToIgnoringCase(paymentType);
        assertThat(payment.getAmount().getCurrencyCode()).isEqualToIgnoringCase(currencyType);
        assertThat(payment.getAmount().getAmount()).isEqualTo(100.0);

    }

    public void checkCorrectVoucherDetailsDisplayed(Map<String,String > voucherDetails) {
        voucherDetails.values().stream().forEach(value->{
            assertThat(value).isNotNull();
        });

    }
}
