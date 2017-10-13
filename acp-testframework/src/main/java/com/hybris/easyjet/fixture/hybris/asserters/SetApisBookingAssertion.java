package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetAPIName;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetApiBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.SetApisBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by robertadigiorgio on 08/06/2017.
 */
public class SetApisBookingAssertion extends Assertion<SetApisBookingAssertion, SetApisBookingResponse> {

    public SetApisBookingAssertion(SetApisBookingResponse setApisBookingResponse) {

        this.response = setApisBookingResponse;
    }

    public SetApisBookingAssertion checkTheStatusAndApis(SetApiBookingRequestBody requestBodyApisBooking, GetBookingResponse.BookingContext bookingContext, String passengerCode) {
        isEqualToComparingFieldByField(checkApisHasBeenStored (bookingContext, passengerCode), requestBodyApisBooking);
        return this;
    }

    private static AbstractPassenger.PassengerAPIS checkApisHasBeenStored (GetBookingResponse.BookingContext bookingContext, String passengerCode){
        GetBookingResponse.Passenger passenger = bookingContext.getBooking().getOutbounds().stream()
                .flatMap(outbound -> outbound.getFlights().stream())
                .flatMap(flight -> flight.getPassengers().stream())
                .filter(bookingPassenger -> bookingPassenger.getCode().equalsIgnoreCase(passengerCode))
                .findFirst()
                .orElse(null);

        assertThat(Objects.nonNull(passenger))
                .withFailMessage("Passenger " + passengerCode + " does not exist on booking")
                .isTrue();

        assertThat(passenger.getPassengerStatus())
                .withFailMessage("The passenger status is not BOOKED")
                .isEqualToIgnoringCase("BOOKED");

        AbstractPassenger.PassengerAPIS passengerAPIS = passenger.getPassengerAPIS();

        assertThat(Objects.nonNull(passengerAPIS))
                .withFailMessage("Apis for passenger " + passengerCode + " on booking does not exist")
                .isTrue();

        return passengerAPIS;
    }

    private static void isEqualToComparingFieldByField(AbstractPassenger.PassengerAPIS bookingPassengerApi, SetApiBookingRequestBody expectedValue) {
        isEqualName(bookingPassengerApi.getName(), expectedValue.getApi().getName());

        assertThat(bookingPassengerApi.getDateOfBirth())
                .withFailMessage("Date of birth between booking passenger apis and expected value does not match")
                .isEqualTo(expectedValue.getApi().getDateOfBirth());

        assertThat(bookingPassengerApi.getDocumentNumber())
                .withFailMessage("Document number between booking passenger apis and expected value does not match")
                .isEqualTo(expectedValue.getApi().getDocumentNumber());

        assertThat(bookingPassengerApi.getDocumentExpiryDate())
                .withFailMessage("Document expired between booking passenger apis and expected value does not match")
                .isEqualTo(expectedValue.getApi().getDocumentExpiryDate());

        assertThat(bookingPassengerApi.getDocumentType())
                .withFailMessage("Document type between booking passenger apis and expected value does not match")
                .isEqualTo(expectedValue.getApi().getDocumentType());

        assertThat(bookingPassengerApi.getGender())
                .withFailMessage("Gender between booking passenger apis and expected value does not match")
                .isEqualTo(expectedValue.getApi().getGender());

        assertThat(bookingPassengerApi.getNationality())
                .withFailMessage("Nationality between booking passenger apis and expected value does not match")
                .isEqualTo(expectedValue.getApi().getNationality());

        assertThat(bookingPassengerApi.getCountryOfIssue())
                .withFailMessage("Country between booking passenger apis and expected value does not match")
                .isEqualTo(expectedValue.getApi().getCountryOfIssue());

    }

    private static void isEqualName(com.hybris.easyjet.fixture.hybris.invoke.response.common.Name bookingName, SetAPIName expectedName) {
        assertThat(bookingName.getFirstName())
                .withFailMessage("Firstname between booking passenger apis and expected value does not match")
                .isEqualTo(expectedName.getFirstName());

        assertThat(bookingName.getLastName())
                .withFailMessage("Lastname between booking passenger apis and expected value does not match")
                .isEqualTo(expectedName.getLastName());

        assertThat(bookingName.getFullName())
                .withFailMessage("Fullname between booking passenger apis and expected value does not match")
                .isEqualTo(expectedName.getFullName());

        assertThat(bookingName.getMiddleName())
                .withFailMessage("Middlename between booking passenger apis and expected value does not match")
                .isEqualTo(expectedName.getMiddleName());

        assertThat(bookingName.getTitle().toLowerCase())
                .withFailMessage("Middlename between booking passenger apis and expected value does not match")
                .isEqualTo(expectedName.getTitle());
    }

    public boolean compareNameOnBooking(com.hybris.easyjet.fixture.hybris.invoke.response.common.Name bookingName, SetAPIName expectedName) {
        return bookingName.getFirstName().equalsIgnoreCase(expectedName.getFirstName())
                && bookingName.getLastName().equalsIgnoreCase(expectedName.getLastName())
                && bookingName.getFullName().equalsIgnoreCase(expectedName.getFullName());
    }

    public SetApisBookingAssertion verifyWarningMessage(String warning, boolean present) {

        assertThat(
                this.response.getAdditionalInformations().stream().anyMatch(
                        warnings -> warnings.getCode().equalsIgnoreCase(warning)
                )
        ).withFailMessage(
                "EXPECTED : " + warning
        ).isEqualTo(present);

        return this;
    }
}
