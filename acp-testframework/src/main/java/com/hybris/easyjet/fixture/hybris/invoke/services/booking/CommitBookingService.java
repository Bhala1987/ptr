package com.hybris.easyjet.fixture.hybris.invoke.services.booking;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.BookingConfirmationAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_FLIGHTS;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.COMMIT_FLIGHTS;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;

/**
 * Created by daniel on 26/11/2016.
 */
public class CommitBookingService extends HybrisService implements IService {

    private BookingConfirmationResponse bookingConfirmationResponse;

    private SerenityFacade testData = SerenityFacade.getTestDataFromSpring();

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    public CommitBookingService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    /**
     * Different passengers will (at some point) have the ability to have different fare types.
     * This method should be removed once this change is introduced.. and this class refactored.
     *
     * @param outboundsStream Stream The outbounds stream.
     * @return The fare type as a string.
     */
    private static String determineFairTypeFromOutbounds(Stream<Journey> outboundsStream) {
        return outboundsStream
                .map(Journey::getFlights).flatMap(Collection::stream)
                .map(Flight::getPassengers).flatMap(Collection::stream)
                .map(Passenger::getFareProduct)
                .map(FareProduct::getBundleCode)
                .filter(bundle -> !"InfantOnLap".equals(bundle))
                .findFirst().get();
    }

    @Override
    public void invoke() {
        super.invoke();

        CommitBookingRequestBody requestBody = (CommitBookingRequestBody) request.getRequestBody();
        if (requestBody.getBasketContent() != null && bookingConfirmationResponse != null && bookingConfirmationResponse.getConfirmation() != null) {
            testData.setData(COMMIT_FLIGHTS, getAllocatedTestFlights(requestBody));
        } else if (bookingConfirmationResponse != null && bookingConfirmationResponse.getConfirmation() != null) {
            testData.setData(COMMIT_FLIGHTS, moveAllocatedTestFlights());
            testData.setData(BASKET_FLIGHTS, null);
        }
    }

    @Override
    public BookingConfirmationAssertion assertThat() {
        pollingLoop().untilAsserted(this::assertThatServiceCallWasSuccessful);

        return new BookingConfirmationAssertion(bookingConfirmationResponse);
    }

    public BookingConfirmationAssertion assertThatErrorsOverride() {
        assertThatServiceCallWasNotSuccessful();
        return new BookingConfirmationAssertion(bookingConfirmationResponse);
    }

    @Override
    public BookingConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return bookingConfirmationResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        List<AdditionalInformation.AffectedData> affectedData = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(bookingConfirmationResponse.getAdditionalInformations())) {
            affectedData = bookingConfirmationResponse.getAdditionalInformations().stream()
                    .map(AdditionalInformation::getAffectedData)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        if (affectedData.isEmpty()) {
            checkThatResponseBodyIsPopulated(bookingConfirmationResponse.getConfirmation());
        }
    }

    @Override
    protected void mapResponse() {
        bookingConfirmationResponse = restResponse.as(BookingConfirmationResponse.class);
    }

    /**
     * If it's a request that's circumvented the basket service process and contains direct
     * basket content.
     *
     * @param requestBody {@link CommitBookingRequestBody} The request object.
     * @return List The list of flights.
     */
    private List<HashMap<String, String>> getAllocatedTestFlights(CommitBookingRequestBody requestBody) {
        Stream<Flight> stream = requestBody.getBasketContent().getOutbounds().stream().flatMap(
                outbound -> outbound.getFlights().stream());
        // Get both streams and create one big one.

        if (requestBody.getBasketContent().getInbounds() != null) {
            stream = Stream.concat(stream, requestBody.getBasketContent().getInbounds().stream().flatMap(
                    inbound -> inbound.getFlights().stream()
            ));
        }
        return stream.map(flight -> new HashMap<String, String>() {
            {
                put("bookingReference", bookingConfirmationResponse.getConfirmation().getBookingReference());
                put("flightKey", flight.getFlightKey());
                put("fareType", determineFairTypeFromOutbounds(requestBody.getBasketContent().getOutbounds().stream()));
            }
        }).collect(Collectors.toList());
    }

    private List<HashMap<String, String>> moveAllocatedTestFlights() {
        if (testData.getData(BASKET_FLIGHTS) != null) {
            return ((List<HashMap<String, String>>) testData.getData(BASKET_FLIGHTS)).stream()
                    .map(flight -> new HashMap<String, String>() {
                                {
                                    put("bookingReference", bookingConfirmationResponse.getConfirmation().getBookingReference());
                                    put("flightKey", flight.get("flightKey"));
                                    put("fareType", flight.get("fareType"));
                                }
                            }
                    ).collect(Collectors.toList());
        } else {
            return null;
        }

    }

    public int getStatusCode() {
        return restResponse.getStatusCode();
    }
}
