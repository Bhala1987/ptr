package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FindBookingQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.FindBookingsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import cucumber.api.PendingException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INVALID_SEARCH_PARAMETER;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for find booking response object, provides reusable assertions to all tests
 */
public class FindBookingAssertion extends Assertion<FindBookingAssertion, FindBookingsResponse> {

    public FindBookingAssertion(FindBookingsResponse findBookingsResponse) {

        this.response = findBookingsResponse;
    }

    private static final String LASTNAME = "lastname";
    private static final String FIRSTNAME = "firstname";
    private static final String EMAIL = "email";
    private static final String TITLE = "title";
    private static final String TRAVEL_FROM_DATE = "travelfromdate";
    private static final String TRAVEL_TO_DATE = "traveltodate";
    private static final String DOB = "dob";
    private static final String TRAVEL_DOC_TYPE = "traveldocumenttype";
    private static final String TRAVEL_DOC_NUMBER = "traveldocumentnumber";
    private static final String POSTCODE = "postcode";

    private GetBookingResponse getBooking;
    private static final Logger LOG = LogManager.getLogger(BookingAssertion.class);

    public void theBookingHasABookingReference(String expectedBookingReference) {

        assertThat(response.getBookings()
                .stream()
                .filter(p -> p.getReferenceNumber().equals(expectedBookingReference)));
    }

    public void bookingsAreReturned() {
        assertThat(response.getBookings()).isNotEmpty();
    }

    public void theBookingHasCustomerDetails(String bookingRef, GetBookingResponse.BookingContact contact) {

        assertThat(response.getBookings().stream().filter(p -> p.getReferenceNumber().equals(bookingRef))
                .collect(Collectors.toList())).extracting("customerFirstName").contains(contact.getName().getFirstName());
        assertThat(response.getBookings().stream().filter(p -> p.getReferenceNumber().equals(bookingRef))
                .collect(Collectors.toList())).extracting("customerLastName").contains(contact.getName().getLastName());
        assertThat(response.getBookings().stream().filter(p -> p.getReferenceNumber().equals(bookingRef))
                .collect(Collectors.toList())).extracting("customerTitle")
                .contains(contact.getName().getTitle().toLowerCase());
//        assertThat(response.getBookings().stream().filter(p -> p.getReferenceNumber().equals(bookingRef))
//                .collect(Collectors.toList())).extracting("customerEmail").contains(contact.getEmailAddress());
    }

    public void theBookingHasFlightDetails(GetBookingResponse.BookingContact contact) {

        throw new PendingException();
    }

    public void theBookingHasDateAndStatus(String bookingRef, String bookingDate, String bookingStatus) {
        //TODO: Format datetime so they match
        pollingLoop().untilAsserted(() -> {
            try {
                assertThat(response.getBookings().stream().filter(p -> p.getReferenceNumber().equals(bookingRef))
                        .collect(Collectors.toList())).extracting("bookingStatus").contains(bookingStatus.toUpperCase());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void theBookingBasedOnTravelToDate(String travelToDate) {

        for (FindBookingsResponse.Booking booking : response.getBookings()) {
            assertThat(booking.getOutboundDepartureDate().equals(travelToDate));
        }
    }

    public void theBookingsAreInRangeOfTravelDate(String travelToDateStr, String travelFromDateStr) throws ParseException {

        List<Date> depatureDates = new ArrayList<>();
        for (FindBookingsResponse.Booking booking : response.getBookings()) {
            depatureDates.add(getDate(booking.getOutboundDepartureDate()));
        }
        Date travelToDate = getDate(travelToDateStr);
        Date travelFromDate = getDate(travelFromDateStr);
        Optional<Date> suspectedDate = depatureDates.stream()
                .filter(date -> date.compareTo(travelFromDate) < 0 || date.compareTo(travelToDate) > 0)
                .findAny();
        assertThat(suspectedDate.isPresent()).isFalse();
    }

    public void theBookingHasBookingDate(String bookingDate, String bookingRef) {

        assertThat(response.getBookings()
                .stream()
                .filter(p -> p.getReferenceNumber().equals(bookingRef))
                .collect(Collectors.toList())).extracting("bookingDate").contains(bookingDate.subSequence(0, (bookingDate.length() - 3)));
    }

    public void theBookingHasStatus(String bookingStatus, String bookingRef) {

        assertThat(response.getBookings()
                .stream()
                .filter(p -> p.getReferenceNumber().equals(bookingRef))
                .collect(Collectors.toList())).extracting("bookingStatus").contains(bookingStatus);
    }

    public void theBookingHasOutboundDate(List<String> outboundDate, String bookingRef) throws ParseException {
        List<String> outboundDateParsed = new ArrayList<>();
        for (String data : outboundDate) {
            outboundDateParsed.add(com.hybris.easyjet.fixture.hybris.helpers.DateFormat.getDate(data));
        }
        assertThat(outboundDateParsed.contains(response.getBookings().stream().filter(p -> p.getReferenceNumber().equals(bookingRef)).map(q -> q.getOutboundDepartureDate()).findFirst().orElse(null))).isEqualTo(true);
    }


    public void theBookingHasPersonalDetails(GetBookingResponse.BookingContact bookingContact, String bookingRef) {

        FindBookingsResponse.Booking booking = response.getBookings().stream().filter(p -> p.getReferenceNumber().equals(bookingRef)).findFirst().orElse(null);
        assertThat(booking.getCustomerFirstName().equals(bookingContact.getName().getFirstName())).isEqualTo(true);
        assertThat(booking.getCustomerLastName().equals(bookingContact.getName().getLastName())).isEqualTo(true);
        assertThat(booking.getCustomerTitle().equalsIgnoreCase(bookingContact.getName().getTitle())).isEqualTo(true);
//        assertThat(booking.getCustomerEmail().equals(bookingContact.getEmailAddress())).isEqualTo(true);
    }

    public void theBookingHasSectorDetails(GetBookingResponse getBookingResponse, String bookingRef) {
        List<String> sectors = getBookingResponse.getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).map(g -> g.getSector().getCode()).collect(Collectors.toList());
        assertThat(sectors.contains(getBooking(bookingRef).getOutboundSectorCode())).isEqualTo(true);
    }

    public void theBookingsAreInDateTimeOrder(List<FindBookingsResponse.Booking> bookings) {

        List<FindBookingsResponse.Booking> sortedBookingList = bookings.stream().sorted(
                new Comparator<FindBookingsResponse.Booking>() {

                    @Override
                    public int compare(FindBookingsResponse.Booking o1, FindBookingsResponse.Booking o2) {

                        return o1.getOutboundDepartureDate().compareTo(o2.getOutboundDepartureDate());
                    }
                }
        ).collect(Collectors.toList());
        assertThat(org.apache.commons.collections.CollectionUtils.subtract(bookings, sortedBookingList).size() == 0);
    }

    private Date getDate(String dateValue) throws ParseException {

        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        return fromFormat.parse(dateValue);
    }

    private FindBookingsResponse.Booking getBooking(String bookingReference) {

        for (FindBookingsResponse.Booking booking : response.getBookings()
                ) {
            if (booking.getReferenceNumber().equals(bookingReference)) {
                return booking;
            }
        }
        return null;
    }

    public void theBookingWithSearchCriteria(String params, FindBookingQueryParams criteria) throws EasyjetCompromisedException {

        List<String> parameters = Arrays.asList(params.split("\\s*,\\s*"));
        for (String param : parameters) {
            validateSearchResult(param, criteria);
        }
    }

    public void theBookingWithSearchCriteriaForTravellers(BookingHelper commitBookingHelper, String params, FindBookingQueryParams criteria) throws EasyjetCompromisedException {

        List<String> parameters = Arrays.asList(params.split("\\s*,\\s*"));
        for (String param : parameters) {
            validateSearchResultForTraveller(commitBookingHelper, param, criteria);
        }
    }

    private void validateSearchResult(String params, FindBookingQueryParams criteria) throws EasyjetCompromisedException {

        assertThat(response.getBookings()).isNotEmpty();

        for (FindBookingsResponse.Booking booking : response.getBookings()) {
            switch (params.toLowerCase()) {
                case FIRSTNAME:
                    assertThat(booking.getCustomerFirstName()).contains(criteria.getFirstName());
                    break;
                case LASTNAME:
                    assertThat(booking.getCustomerLastName()).contains(criteria.getLastName());
                    break;
                case EMAIL:
                    assertThat(booking.getCustomerEmail()).contains((criteria.getEmail()));
                    break;
                case POSTCODE:
                    assertThat(booking.getCustomerPostalCode()).contains(criteria.getPostcode());
                    break;
                case TRAVEL_FROM_DATE:
                    theBookingBasedOnTravelToDate(criteria.getTravelFromDate());
                    break;
                case TRAVEL_TO_DATE:
                    theBookingBasedOnTravelToDate(criteria.getTravelToDate());
                    break;
                case TITLE:
                    assertThat(booking.getCustomerTitle().toLowerCase().contentEquals(criteria.getTitle()));
                    break;
                default:
                    throw new EasyjetCompromisedException(INVALID_SEARCH_PARAMETER);
            }
        }
    }

    private void validateSearchResultForTraveller(BookingHelper commitBookingHelper, String params, FindBookingQueryParams criteria) throws EasyjetCompromisedException {

        assertThat(response.getBookings()).isNotEmpty();

        for (FindBookingsResponse.Booking booking : response.getBookings()) {
            getBooking = commitBookingHelper.getBookingDetails(booking.getReferenceNumber(), testData.getChannel());

            List<GetBookingResponse.Passenger> Passengers = getBooking.getBookingContext().getBooking().getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(p -> p.getPassengers().stream())
                    .collect(Collectors.toList());

            switch (params.toLowerCase()) {
                case FIRSTNAME:
                    for (GetBookingResponse.Passenger pax : Passengers) {
                        if (pax.getPassengerAPIS().getName().getFirstName().contains(criteria.getFirstName())) {
                            assertThat(pax.getPassengerAPIS().getName().getFirstName().contains(criteria.getFirstName()));
                        }
                    }
                    break;
                case LASTNAME:
                    for (GetBookingResponse.Passenger pax : Passengers) {
                        if (pax.getPassengerAPIS().getName().getLastName().contains(criteria.getLastName())) {
                            assertThat(pax.getPassengerAPIS().getName().getLastName().contains(criteria.getLastName()));
                        }
                    }
                    break;
                case DOB:

                    for (GetBookingResponse.Passenger pax : Passengers) {
                        if (pax.getPassengerAPIS().getDateOfBirth().contains(criteria.getDob())) {
                            assertThat(pax.getPassengerAPIS().getDateOfBirth().contains(criteria.getDob()));
                        }
                    }
                    break;
                case TRAVEL_DOC_TYPE:
                    for (GetBookingResponse.Passenger pax : Passengers) {
                        if (pax.getPassengerAPIS().getDocumentType().contains(criteria.getTravelDocumentType())) {
                            assertThat(pax.getPassengerAPIS().getDocumentType().contains(criteria.getTravelDocumentType()));
                        }
                    }
                    break;
                case TRAVEL_DOC_NUMBER:
                    for (GetBookingResponse.Passenger pax : Passengers) {
                        if (pax.getPassengerAPIS().getDocumentNumber().contains(criteria.getTravelDocumentNumber())) {
                            assertThat(pax.getPassengerAPIS().getDocumentNumber().contains(criteria.getTravelDocumentNumber()));
                        }
                    }
                    break;
                case TRAVEL_FROM_DATE:
                    theBookingBasedOnTravelToDate(criteria.getTravelFromDate());
                    break;
                case TRAVEL_TO_DATE:
                    theBookingBasedOnTravelToDate(criteria.getTravelToDate());
                    break;
                case TITLE:
                    assertThat(booking.getCustomerTitle().toLowerCase().contentEquals(criteria.getTitle()));
                    break;
                default:
                    throw new EasyjetCompromisedException(INVALID_SEARCH_PARAMETER);
            }
        }
    }

    public void validateSearcForBooking(List<String> bookingReferenceDB) {

        List<String> listBookingResponse = response.getBookings().stream().map(FindBookingsResponse.Booking::getReferenceNumber).collect(Collectors.toList());

        if (listBookingResponse.size() >= 200) {
            assertThat(bookingReferenceDB)
                    .as("Unexpected results returned")
                    .contains(listBookingResponse.toArray(new String[listBookingResponse.size()]));
        } else {
            assertThat(listBookingResponse)
                    .as("The occurrences in the response not match")
                    .containsExactlyInAnyOrder(bookingReferenceDB.toArray((new String[bookingReferenceDB.size()])));
        }
    }
}
