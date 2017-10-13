package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.CommentTypesQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.AddCommentToBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.UpdateCommentsOnBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.BasketContentFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.login.LoginDetails;
import com.hybris.easyjet.fixture.hybris.invoke.requests.LoginRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.operationconfirmation.BookingConfirmationResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.AgentLoginService;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.*;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.EMPLOYEE_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.EMPLOYEE_NAME;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoopForSearchBooking;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by rajakm on 05/05/2017.
 */
@Component
public class BookingCommentHelper {

    @Getter
    private GetCommentTypesService getCommentTypesService;

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    FlightHelper flightHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private BookingPathParams bookingPathParams;
    private AddCommentsToBookingService addCommentsToBookingService;
    private BookingConfirmationResponse bookingConfirmationResponse;
    private GetBookingService getBookingService;
    private AddCommentToBookingRequestBody requestBody;
    private UpdateCommentsOnBookingRequestBody updateRequestBody;
    private UpdateCommentsOnBookingService updateCommentsOnBookingService;
    private static final String SVC_100219_1002 = "SVC_100219_1002"; //Invalid Comment Id code
    private String bookingId;
    private String commentId;
    private String commentType;
    private String comment;

    //    BUILD
    public void buildRequestToDeleteCommentOnBooking(String bookingId, String commentId) {
        this.bookingId = bookingId;
        this.commentId = commentId;
        setDeleteCommentPathParams();
        invokeDeleteCommentService();
    }

    public void buildRequestToAddCommentToBooking(String bookingId, String commentType, String comment) throws EasyjetCompromisedException {
        this.bookingId = bookingId;
        this.commentType = commentType;
        this.comment = comment;
        setAddCommentPathParams();
        invokeAddCommentService();
        setCommentCode();
    }

    //this method is to verify the error scenario, so no polling required
    public void buildRequestToUpdateCommentsOnBooking(String bookingId, String commentId, String commentType, String comment) {

        this.bookingId = bookingId;
        this.commentId = commentId;
        this.commentType = commentType;
        this.comment = comment;
        setUpdateCommentPathParams();
        invokeUpdateCommentService();
    }

    //this method is to verify the happy flow, so if any error, it will do polling
    public void buildRequestToUpdateCommentsOnBookingWithPolling(String bookingId, String commentId, String commentType, String comment) {
        this.bookingId = bookingId;
        this.commentId = commentId;
        this.commentType = commentType;
        this.comment = comment;
        setUpdateCommentPathParams();

        pollingLoop().untilAsserted(() -> {
            invokeUpdateCommentService();

            if (!Objects.isNull(updateCommentsOnBookingService.getErrors())) {
                assertThat(updateCommentsOnBookingService.getErrors().getErrors().stream()
                        .map(Errors.Error::getCode)
                        .collect(Collectors.toList()))
                        .doesNotContain(SVC_100219_1002);
            }
        });
    }

    //    SET
    private void setAddCommentPathParams() {
        bookingPathParams = BookingPathParams.builder().bookingId(bookingId).path(ADD_COMMENT).build();
        requestBody = BasketContentFactory.aBasicAddCommentToBooking();
        requestBody.setCommentType(commentType);
        requestBody.setComment(comment);
    }

    private void setDeleteCommentPathParams() {
        bookingPathParams = BookingPathParams.builder()
                .bookingId(bookingId)
                .commentId(commentId)
                .path(DELETE_COMMENT)
                .build();
    }

    private void setUpdateCommentPathParams() {
        bookingPathParams = BookingPathParams.builder()
                .bookingId(bookingId)
                .commentId(commentId)
                .path(UPDATE_COMMENT)
                .build();

        updateRequestBody = BasketContentFactory.aBasicUpdateCommentOnBooking();
        updateRequestBody.setCommentType(commentType);
        updateRequestBody.setComment(comment);
    }

    public String setCommentCode() {
        String commentCode= addCommentsToBookingService.getResponse().getOperationConfirmation().getCommentCode();
        testData.setData(SerenityFacade.DataKeys.COMMENT_CODE,commentCode);

        return commentCode;
    }

    //    INVOKE
    public void invokeGetCommentTypesService(String commentContext) {
        if (commentContext == null || commentContext.equalsIgnoreCase("")) {
            getCommentTypesService = serviceFactory.getCommentTypes(new GetCommentTypesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), null));
        } else {
            CommentTypesQueryParams commentTypesQueryParams;
            if (commentContext.toLowerCase().contains("invalid")) {
                commentTypesQueryParams = CommentTypesQueryParams.builder().commentContext("invalid").build();
            } else {
                commentTypesQueryParams = CommentTypesQueryParams.builder().commentContext(commentContext).build();
            }
            getCommentTypesService = serviceFactory.getCommentTypes(new GetCommentTypesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), commentTypesQueryParams));
        }
        getCommentTypesService.invoke();
        testData.setData(SERVICE, getCommentTypesService);
    }

    private void invokeAddCommentService() {
        addCommentsToBookingService = serviceFactory.getAddCommentsToBooking(
                new AddCommentToBookingRequest(
                        HybrisHeaders.getValid(testData.getChannel()).build(),
                        bookingPathParams,
                        requestBody
                )
        );
        testData.setData(SERVICE, addCommentsToBookingService);
        addCommentsToBookingService.invoke();
    }

    private void invokeUpdateCommentService() {
        updateCommentsOnBookingService = serviceFactory.getUpdateCommentsOnBooking(
                new UpdateCommentsOnBookingRequest(
                        HybrisHeaders.getValid(testData.getChannel()).build(),
                        bookingPathParams,
                        updateRequestBody
                )
        );
        testData.setData(SERVICE, updateCommentsOnBookingService);
        updateCommentsOnBookingService.invoke();
    }

    private void invokeDeleteCommentService() {
        DeleteCommentOnBookingService deleteCommentOnBookingService = serviceFactory.getDeleteCommentsOnBooking(
                new DeleteCommentOnBookingRequest(
                        HybrisHeaders.getValid(testData.getChannel()).build(),
                        bookingPathParams
                )
        );
        testData.setData(SERVICE, deleteCommentOnBookingService);
        deleteCommentOnBookingService.invoke();
    }

    public void invokeGetBookings() {
        BookingPathParams bookingPathParameters = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).build();
        getBookingService = serviceFactory.getBookings(new GetBookingRequest(HybrisHeaders.getValid(testData.getChannel()).build(), bookingPathParameters));
        getBookingService.invoke();
    }

    //    GET
    public void getCommitBookingDone(String src, String dest) throws Throwable {

        FlightsService flightsService = flightHelper.getFlights(testData.getChannel(), "1 Adult", src, dest, "SINGLE");
        basketHelper.addFlightToBasketAsChannel(flightsService.getOutboundFlight());
        CommitBookingRequest commitBookingRequest = bookingHelper.createNewBookingRequestForChannelBasedOnBasket(
                basketHelper.getBasketService().getResponse(),
                testData.getChannel()
        );
        Basket basket = basketHelper.getBasket(
                basketHelper.getBasketService().getResponse().getBasket().getCode(),
                testData.getChannel()
        );
        try {
            List<Basket.Passenger> passengers = basketHelper.getAllPassengerRecordsFromAllFlights(basket);
            if (CollectionUtils.isNotEmpty(passengers)) {
                pollingLoop().untilAsserted(() -> {
                    assertThat(passengers.stream().filter(pg -> StringUtils.isNotBlank(pg.getPassengerDetails().getName().getTitle()) && pg.getAge() != null));
                });
            }

        } catch (ConditionTimeoutException e) {
            fail("Missing mandatory passenger information");
        }
        CommitBookingService commitBookingService = serviceFactory.commitBooking(commitBookingRequest);
        commitBookingService.invoke();
        commitBookingService.assertThat().gotAValidResponse();

        bookingConfirmationResponse = commitBookingService.getResponse();
        getBookingContext();
        assertThat(bookingConfirmationResponse.getConfirmation().getBookingReference()).isNotEmpty();

        bookingHelper.getGetBookingService()
                .assertThat()
                .theBasketWasAddedToTheBooking(basketHelper.getBasketService().getResponse());

        testData.setData(BOOKING_ID, bookingConfirmationResponse.getConfirmation().getBookingReference());
    }

    private void getBookingContext() {
        bookingHelper.getBookingDetails(bookingConfirmationResponse.getConfirmation()
                .getBookingReference(), testData.getChannel());
    }

    public void agentLogin(String username, String password) {
        if (!testData.getChannel().contains("AD")) {
            testData.setChannel("ADAirport");
        }
        LoginDetails loginRequest = LoginDetails.builder().email(username).password(password).rememberme(false).build();
        AgentLoginService agentLoginService = serviceFactory.loginAgent(new LoginRequest(HybrisHeaders.getValid(testData.getChannel()).build(), loginRequest));
        agentLoginService.invoke();
        testData.setData(EMPLOYEE_ID, agentLoginService.getResponse().getAuthenticationConfirmation().getAgent().getAgentId());
        testData.setData(EMPLOYEE_NAME, agentLoginService.getResponse().getAuthenticationConfirmation().getAgent().getName());
        testData.setAccessToken(agentLoginService.getResponse().getAuthenticationConfirmation().getAuthentication().getAccessToken());
    }

    public AddCommentsToBookingService getAddCommentsToBookingService() {
        return addCommentsToBookingService;
    }

    //    VERIFY
    public void verifyTheBookingHasTheCommentsAdded(String expectedCommentCode, String expectedComment) {

        pollingLoopForSearchBooking().ignoreExceptions().untilAsserted(() -> {
            invokeGetBookings();

            assertThat(getBookingService.getResponse().getBookingContext().getBooking().getComments()).isNotNull();
            assertThat(!getBookingService.getResponse().getBookingContext().getBooking().getComments().isEmpty()).isEqualTo(true);
            assertThat(getBookingService.getResponse().getBookingContext().getBooking().getComments().get(0).getCode()).isEqualTo(expectedCommentCode);
            assertThat(getBookingService.getResponse().getBookingContext().getBooking().getComments().get(0).getDescription()).isEqualTo(expectedComment);

        });
    }

    public void verifyTheBookingCommentIsRemoved(String bookingCommentCode) {
        pollingLoopForSearchBooking().ignoreExceptions().untilAsserted(() -> {
            invokeGetBookings();

            if (getBookingService.getResponse().getBookingContext().getBooking().getComments().isEmpty()) {
                return;
            }

            boolean commentIsPresent = getBookingService.getResponse().getBookingContext().getBooking().getComments().stream()
                    .anyMatch(comment -> comment.getCode().equals(bookingCommentCode));

            assertThat(commentIsPresent).isFalse();
        });
    }

    public void verifyTheBookingCommentIsUpdated(String bookingCommentCode, String updatedComment) {
        pollingLoopForSearchBooking().ignoreExceptions().untilAsserted(() -> {
            invokeGetBookings();

            assertThat(getBookingService.getResponse().getBookingContext().getBooking().getComments()).isNotNull();
            assertThat(!getBookingService.getResponse().getBookingContext().getBooking().getComments().isEmpty()).isEqualTo(true);
            assertThat(getBookingService.getResponse().getBookingContext().getBooking().getComments().stream()
                    .filter(c -> c.getCode().equalsIgnoreCase(bookingCommentCode))
                    .findFirst()
                    .get().getDescription().equalsIgnoreCase(updatedComment)).isTrue();
        });

    }
}
