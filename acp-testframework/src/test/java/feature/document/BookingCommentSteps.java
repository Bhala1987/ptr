package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BookingCommentHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * All steps relating to the creation and updating of booking comments.
 *
 * @author rajam
 * @author Joshua Curtis <jcurtis@reply.com>
 * @author bhalasaravananthiruvarangamrajalakshmi
 */
@ContextConfiguration(classes = TestApplication.class)
public class BookingCommentSteps {
    protected static Logger LOG = LogManager.getLogger(AddEJPlusSeatToBasketSteps.class);

    @Autowired
    private SerenityFacade testData;


    @Autowired
    private BookingHelper commitBookingHelper;
    @Autowired
    private BookingCommentHelper bookingCommentHelper;

    private String INVALID_BOOKING_ID = "invalidBookingId";
    private String INVALID_COMMENT_CODE = "invalidCommentCode";
    private String commentCode;
    private String bookingComment;


    @Given("^the commit booking is done for the channel (.*) for flight from (.*) to (.*)$")
    public void the_commit_booking_is_done_for_the_channel_something_for_flight_from_something_to_something(String channel, String src, String dest) throws Throwable {
        testData.setChannel(channel);
        bookingCommentHelper.getCommitBookingDone(src, dest);
    }

    @And("^I have received a valid addComments request and invalid bookingID with (.*) and (.*)$")
    public void iHaveReceivedAValidAddCommentsRequestAndInvalidBookingIDWithCommentTypeAndComment(String commentType, String comment) throws Throwable {
        bookingCommentHelper.buildRequestToAddCommentToBooking(INVALID_BOOKING_ID, commentType, comment);
    }

    @And("^I have received a valid addComments request for type (.*) with comment (.*)$")
    public void i_have_received_a_valid_addcomments_request_for_type_something_with_comment_something(String type, String comment) throws Throwable {
        bookingComment = comment;
        bookingCommentHelper.buildRequestToAddCommentToBooking(testData.getData(SerenityFacade.DataKeys.BOOKING_ID), type, comment);
    }

    @Given("^I return the result for addComments request$")
    public void i_validate_and_return_the_result_for_addcomments_request() throws Throwable {
        commentCode = bookingCommentHelper.setCommentCode();
    }

    @When("^I get the booking$")
    public void i_get_the_booking() throws Throwable {
        bookingCommentHelper.invokeGetBookings();
    }

    @When("^I delete any comment from an invalid booking$")
    public void iDeleteAnyCommentFromAnInvalidBooking() throws EasyjetCompromisedException {
        // As this is checking against an invalid booking ID there's not need to create one.
        bookingCommentHelper.buildRequestToDeleteCommentOnBooking(
            INVALID_BOOKING_ID,
            INVALID_COMMENT_CODE
        );
    }

    @When("^I attempt to delete a comment that doesn't exist from the booking$")
    public void iDeleteAnyInvalidCommentFromTheBooking() {
        // As this is checking against an invalid booking ID there's not need to create one.
        bookingCommentHelper.buildRequestToDeleteCommentOnBooking(
                testData.getData(SerenityFacade.DataKeys.BOOKING_ID),
            INVALID_COMMENT_CODE
        );
    }

    @When("^I delete the added comment from the booking$")
    public void iDeleteCommentFromTheBooking() {
        bookingCommentHelper.buildRequestToDeleteCommentOnBooking(
                testData.getData(SerenityFacade.DataKeys.BOOKING_ID),
                commentCode
        );
    }

    @Then("^I will return the result (.*) and (.*) based on the channel configuration$")
    public void i_will_return_the_result_something_and_something_based_on_the_channel_configuration(String resultshouldbe, String error) throws Throwable {
        if (resultshouldbe.equals("SUCCESS")) {
            commentCode = bookingCommentHelper.setCommentCode();
        } else if (resultshouldbe.equals("FAIL")) {
            bookingCommentHelper.getAddCommentsToBookingService().assertThatErrors().containedTheCorrectErrorMessage(error);
        }
    }

    @Then("^I could see the comments added$")
    public void i_could_see_the_comments_added() throws Throwable {
        bookingCommentHelper.verifyTheBookingHasTheCommentsAdded(commentCode, bookingComment);
    }

    @Then("^the comment should be removed from the booking$")
    public void theCommentShouldBeRemovedFromTheBooking() {
        bookingCommentHelper.verifyTheBookingCommentIsRemoved(commentCode);
    }

    @When("^I make a request to getCommentTypes$")
    public void iMakeARequestToGetCommentTypes() throws Throwable {
        bookingCommentHelper.invokeGetCommentTypesService(null);
    }

    @Then("^I should get list of (.*) for (.*)$")
    public void iShouldGetListOfCommentTypesForContext(String commentTypes, String commentContext) throws Throwable {
        List<String> expectedCommentTypes = new ArrayList<>(Arrays.asList(commentTypes.split(",")));
        bookingCommentHelper.getGetCommentTypesService().assertThat().commentContextsWereReturned();
        bookingCommentHelper.getGetCommentTypesService().assertThat().commentContextsWereReturned(commentContext);
        bookingCommentHelper.getGetCommentTypesService().assertThat().getCommentTypes(commentContext, expectedCommentTypes);
    }

    @When("^I make a request to getCommentTypes for a (.*)$")
    public void iMakeARequestToGetCommentTypesForA(String commentContext) throws Throwable {
        bookingCommentHelper.invokeGetCommentTypesService(commentContext);
    }

    @Then("^I should get (.*) only$")
    public void iShouldGetCommentTypeOnly(String commentContext) throws Throwable {
        bookingCommentHelper.getGetCommentTypesService().assertThat().getParticularCommentContext(commentContext);
    }

    @When("^I update a comment from an invalid booking with (.*) and (.*)$")
    public void iUpdateACommentFromAnInvalidBookingWith(String commentType, String comment) throws EasyjetCompromisedException {
        // As this is checking against an invalid booking ID there's not need to create one.
        bookingCommentHelper.buildRequestToUpdateCommentsOnBooking(
                INVALID_BOOKING_ID,
                INVALID_COMMENT_CODE,
                commentType,
                comment
        );
    }

    @When("^I attempt to update a comment that doesn't exist from the booking with (.*) and (.*)$")
    public void iUpdateAnyInvalidCommentFromTheBooking(String commentType, String comment) {
        // As this is checking against an invalid booking ID there's not need to create one.
        bookingCommentHelper.buildRequestToUpdateCommentsOnBooking(
                testData.getData(SerenityFacade.DataKeys.BOOKING_ID),
                INVALID_COMMENT_CODE,
                commentType,
                comment
        );
    }

    @When("^I update the added comment from the booking with (.*) and (.*)$")
    public void iUpdateCommentFromTheBooking(String commentType, String comment) {
        bookingComment = comment;
        bookingCommentHelper.buildRequestToUpdateCommentsOnBookingWithPolling(
                testData.getData(SerenityFacade.DataKeys.BOOKING_ID),
                commentCode,
                commentType,
                comment
        );
    }

    @Then("^the comment should be updated on the booking.$")
    public void theCommentShouldBeUpdatedOnTheBooking() {
        bookingCommentHelper.verifyTheBookingCommentIsUpdated(commentCode, bookingComment);
    }

    @Then("^based on channel I (.*) see comments on booking$")
    public void basedOnChannelIShouldornotSeeCommentsOnBooking(String flag) throws Throwable {
        commitBookingHelper.getGetBookingService()
                .assertThat().theBookingHasCommentsAdded(flag, commentCode, bookingComment);
    }


}
