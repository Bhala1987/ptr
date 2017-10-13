package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.fixture.WaitHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.CancelBookingRefundRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CancelBookingRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.CancelBookingRefundRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.InitiateCancelBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CancelBookingRefundService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.InitiateCancelBookingService;


import org.assertj.core.api.Java6Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;


import static com.hybris.easyjet.config.SerenityFacade.DataKeys.FLIGHT_KEY;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.STOCK_AFTER_CHANGE_FLIGHT;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.CANCEL;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.CANCEL_BOOKING;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 22/08/2017.
 */
@Component
public class CancelBookingHelper {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CancelBookingRequestBodyFactory cancelBookingRequestBodyFactory;


    private InitiateCancelBookingService initiateCancelBookingService;

    private CancelBookingRefundService cancelBookingRefundService;

    @Autowired
    private HoldItemsDao holdItemsDao;

    public void initiateCancelBooking() {
        InitiateCancelBookingRequest initiateCancelBookingRequest = new InitiateCancelBookingRequest(
                HybrisHeaders.getValid(
                        testData.getChannel()
                ).build(),
                BookingPathParams.builder()
                        .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                        .path(CANCEL)
                        .build()
        );

        initiateCancelBookingService = serviceFactory.initiateCancelBooking(initiateCancelBookingRequest);

        initiateCancelBookingService.invoke();

        testData.setData(SERVICE, initiateCancelBookingService);
    }

    public void cancelBooking() {

        CancelBookingRefundRequestBody cancelBookingRefundRequestBody = cancelBookingRequestBodyFactory.cancelBookingRequestBodyFactory(initiateCancelBookingService.getResponse().getInitiateCancellationConfirmation().getRefundsAndFees());

        CancelBookingRefundRequest cancelBookingRefundRequest = new CancelBookingRefundRequest(
                HybrisHeaders.getValid(
                        testData.getChannel()
                ).build(),
                BookingPathParams.builder()
                        .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                        .path(CANCEL_BOOKING)
                        .build(), cancelBookingRefundRequestBody
        );

        cancelBookingRefundService = serviceFactory.cancelBookingRefund(cancelBookingRefundRequest);
        int[] attempts = {5};
        WaitHelper.pollingLoop().until(() -> {
            cancelBookingRefundService.invoke();
            attempts[0]--;
            return cancelBookingRefundService.getRestResponse().getStatusCode() == 200 || attempts[0] == 0;
        });
        testData.setData(SERVICE, cancelBookingRefundService);
    }

    public void cancelBookingStatusCheck(String bookingstatus) {
        cancelBookingRefundService.assertThat().bookingReferenceAssertion(testData.getData(SerenityFacade.DataKeys.BOOKING_ID));
        cancelBookingRefundService.assertThat().bookingStatusAssertion(bookingstatus);
    }

    public void verifyStockLevelForHoldItems(String product, int stockBeforeCancellation) {
        if ((product.equals("hold bag"))) {
            testData.setData(STOCK_AFTER_CHANGE_FLIGHT, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "20kgbag").get(0)));
        } else if (product.equals("sport equipment")) {
            testData.setData(STOCK_AFTER_CHANGE_FLIGHT, Integer.parseInt(holdItemsDao.getReservedStockLevelForFlight(testData.getData(FLIGHT_KEY), "Snowboard").get(0)));
        }
        int beforeStock = testData.getData(SerenityFacade.DataKeys.STOCK_BEFORE_CHANGE_FLIGHT);
        int afterStock =  testData.getData(SerenityFacade.DataKeys.STOCK_AFTER_CHANGE_FLIGHT);
        Java6Assertions.assertThat(beforeStock).isGreaterThan(afterStock).withFailMessage("No change in stock level");
    }
}
