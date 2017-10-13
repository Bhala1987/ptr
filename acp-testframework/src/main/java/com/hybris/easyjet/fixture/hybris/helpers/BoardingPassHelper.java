package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BoardingPassParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.GenerateBoardingPassRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GenerateBoardingPassRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.GenerateBoardingPassService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BoardingPassParams.GenerateBoardingPassPaths.DEFAULT;

/**
 * Created by giuseppecioce on 11/10/2017.
 */
@Component
public class BoardingPassHelper {
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    SerenityFacade testData;
    @Autowired
    private BookingHelper bookingHelper;

    public void generateBoardingPassForFlightKey(String flightKey, List<GetBookingResponse.Passenger> passengers) {
        GenerateBoardingPassRequestBody requestBody = createBoardingPassRequestBody(flightKey, passengers);

        BoardingPassParams pathParams = BoardingPassParams.builder()
                .bookingId(bookingHelper.getGetBookingService().getResponse().getBookingContext().getBooking().getBookingReference())
                .path(DEFAULT)
                .build();

        GenerateBoardingPassService generateBoardingPassService = serviceFactory.getBoardingPassService(
                new GenerateBoardingPassRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, requestBody));
        generateBoardingPassService.invoke();
    }

    private GenerateBoardingPassRequestBody createBoardingPassRequestBody(String flightKey, List<GetBookingResponse.Passenger> passengers) {
        return GenerateBoardingPassRequestBody.builder()
                .language("fr")
                .flights(Arrays.asList(GenerateBoardingPassRequestBody.Flight.builder()
                        .flightKey(flightKey)
                        .passengers(new ArrayList<GenerateBoardingPassRequestBody.Flight.Passenger>() {{
                            for(GetBookingResponse.Passenger passenger: passengers) {
                                add(GenerateBoardingPassRequestBody.Flight.Passenger.builder()
                                        .passengerCode(passenger.getCode())
                                        .isAdditionalSeatsOnly(Objects.nonNull(passenger.getAdditionalSeats()))
                                        .build());
                            }
                        }})
                        .build()))
                .build();
    }
}
