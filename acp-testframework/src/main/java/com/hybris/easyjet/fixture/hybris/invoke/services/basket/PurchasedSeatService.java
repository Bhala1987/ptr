package com.hybris.easyjet.fixture.hybris.invoke.services.basket;

import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.asserters.PurchasedSeatAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.AddPurchasedSeatsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.PassengerSeatChangeRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.operationconfirmation.BasketConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import net.serenitybdd.core.Serenity;

import java.util.*;

import static com.hybris.easyjet.config.SerenityFacade.testData.ALLOCATED_SEATS;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.ADD_PURCHASED_SEAT;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.CHANGE_PURCHASED_SEAT;

/**
 * Created by giuseppecioce on 12/04/2017.
 */
public class PurchasedSeatService extends HybrisService {
    private BasketConfirmationResponse purchasedSeatResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     */
    public PurchasedSeatService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public BasketConfirmationResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return purchasedSeatResponse;
    }

    @Override
    public void invoke() {
        super.invoke();

        if ((((BasketPathParams) request.getPathParameters()).getPath().equals(ADD_PURCHASED_SEAT) || ((BasketPathParams) request.getPathParameters()).getPath().equals(CHANGE_PURCHASED_SEAT)) && Objects.nonNull(request.getRequestBody()) && Objects.nonNull(purchasedSeatResponse)) {

            if (!Serenity.hasASessionVariableCalled(ALLOCATED_SEATS)) {
                Serenity.setSessionVariable(ALLOCATED_SEATS).to(new ArrayList<>());
            }
            List<Map<String, String>> allocatedSeats = Serenity.sessionVariableCalled(ALLOCATED_SEATS);
            IRequestBody iRequestBody = request.getRequestBody();
            if(iRequestBody instanceof AddPurchasedSeatsRequestBody) {
                ((AddPurchasedSeatsRequestBody) request.getRequestBody()).getPassengerAndSeats().forEach(seat -> {
                            if (Objects.nonNull(seat.getSeat())) {
                                HashMap<String, String> newSeat = new HashMap<>();
                                newSeat.put("basketId", ((BasketPathParams) request.getPathParameters()).getBasketId());
                                newSeat.put("flightKey", ((AddPurchasedSeatsRequestBody) request.getRequestBody()).getFlightKey());
                                newSeat.put("passengerId", seat.getPassengerId());
                                newSeat.put("seatNumber", seat.getSeat().getSeatNumber());
                                allocatedSeats.add(newSeat);
                            }
                        }
                );
            } else if(iRequestBody instanceof PassengerSeatChangeRequestBody) {
                ((PassengerSeatChangeRequestBody) request.getRequestBody()).getPassengerSeatChangeRequests().forEach(seat -> {
                            if (Objects.nonNull(seat.getSeat())) {
                                HashMap<String, String> newSeat = new HashMap<>();
                                newSeat.put("basketId", ((BasketPathParams) request.getPathParameters()).getBasketId());
                                newSeat.put("flightKey", ((BasketPathParams) request.getPathParameters()).getFlightKey());
                                newSeat.put("passengerId", seat.getPassengerOnFlightId());
                                newSeat.put("seatNumber", seat.getSeat().getSeatNumber());
                                allocatedSeats.add(newSeat);
                            }
                        }
                );
            }
            Serenity.setSessionVariable(ALLOCATED_SEATS).to(allocatedSeats);
        }
    }

    @Override
    public PurchasedSeatAssertion assertThat() {
        return new PurchasedSeatAssertion(purchasedSeatResponse);
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        checkThatResponseBodyIsPopulated(purchasedSeatResponse.getOperationConfirmation());
    }

    @Override
    protected void mapResponse() {
        purchasedSeatResponse = restResponse.as(BasketConfirmationResponse.class);
    }

    public int getStatusCode() {
        return super.restResponse.getStatusCode();
    }
}
