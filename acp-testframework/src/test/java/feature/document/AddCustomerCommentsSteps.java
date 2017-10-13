package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.database.hybris.dao.CommentsDao;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customercomments.AddCommentToCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddCommentToCustomerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveCommentToCustomerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateCommentToCustomerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.ADD_COMMENT_TO_CUSTOMER;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.MANAGE_CUSTOMER_COMMENTS;
import static feature.document.steps.constants.StepsRegex.CHANNELS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by jamie on 03/07/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class AddCustomerCommentsSteps {
    protected static Logger LOG = LogManager.getLogger(AddCustomerCommentsSteps.class);
    @Autowired
    CommentsDao commentsDao;
    @Steps
    UpdateCustomerDetailsSteps updateCustomerDetailsSteps;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private com.hybris.easyjet.fixture.hybris.helpers.ChannelPropertiesHelper channelPropertiesHelper;
    private String theComment;
    private String theCommentCode;
    @Steps
    private SerenityReporter reporter;

    @Then("^the updated comment (is|is not) returned to channel$")
    public void iCriteriaGetUpdatedCommentsToTheChannel(String criteria) throws Throwable {

        CustomerProfileService customerProfileService = testData.getData(GET_CUSTOMER_PROFILE_SERVICE);
        if (criteria.equals("is")) {
            pollingLoop().untilAsserted(() -> {
                customerProfileService.invoke();
                customerProfileService.assertThat().commentsExistInProfile(theComment, theCommentCode);
            });
        } else {
            customerProfileService.assertThat().noCommentsExistInProfile();
        }
    }

    @Then("^I will return Channel, User ID, comment type and created DateTime Stamp for the added comment$")
    public void iWillReturnChannelUserIDCommentTypeFreeTextCommentCreatedDateTimeStamp() throws Throwable {
        CustomerProfileService customerProfileService = testData.getData(GET_CUSTOMER_PROFILE_SERVICE);
        pollingLoop().untilAsserted(() -> {
            customerProfileService.invoke();
            customerProfileService.assertThat().commentsExistInProfile(theComment, theCommentCode);
        });

        customerProfileService.assertThat().createdCommentFieldsAreCorrectInDatabase(commentsDao.getCommentWithId(theCommentCode));
    }

    @Then("^I will return Channel, User ID, comment type and created DateTime Stamp for the updated comment$")
    public void iWillReturnChannelUserIDCommentTypeFreeTextCommentUpdatedDateTimeStamp() throws Throwable {
        CustomerProfileService customerProfileService = testData.getData(GET_CUSTOMER_PROFILE_SERVICE);
        pollingLoop().untilAsserted(() -> {
            customerProfileService.invoke();
            customerProfileService.assertThat().commentsExistInProfile(theComment, theCommentCode);
        });

        customerProfileService.assertThat().updatedCommentFieldsAreCorrectInDatabase(commentsDao.getCommentWithId(theCommentCode));
    }

    @When("^I attempt to update a comment to a non-existing customer$")
    public void iAttemptToUpdateACommentToANonExistingCustomer() throws Throwable {
        addComment();
        updateComment("rubbishcustomerid", "updated comment" + UUID.randomUUID(), theCommentCode);
    }

    @When("^I attempt to update a comment with a non-existing commentID$")
    public void iAttemptToUpdateACommentWithANonExistingCommentID() throws Throwable {
        addComment();
        updateComment(testData.getData(CUSTOMER_ID), "updated comment" + UUID.randomUUID(), "0");
    }

    @When("^I attempt to update a comment with a commentID not matching the customerID$")
    public void iAttemptToUpdateACommentWithACommentIDNotMatchingTheCustomerID() throws Throwable {
        addComment();
        updateCustomerDetailsSteps.iCreateANewValidCustomer();
        updateComment();
    }

    @When("^(?:I attempt to update|I have updated|I update) a customer comment$")
    public void iAttemptToUpdateACustomerComment() throws Throwable {
        addComment();
        updateComment();
    }

    @When("^(?:I attempt to remove|I have removed|I remove) a customer comment$")
    public void iAttemptToDeleteACommentForANonExistingCustomer() throws Throwable {
        addComment();
        removeComment();
    }

    @When("^I attempt to remove a comment for a non-existing customer$")
    public void iAttemptToRemoveACommentForANonExistingCustomer() throws Throwable {
        addComment();
        removeComment("rubbishcustomerid", theCommentCode);
    }

    @When("^I attempt to remove a comment with a non-existing commentID$")
    public void iAttemptToRemoveACommentWithANonExistingCommentID() throws Throwable {
        addComment();
        removeComment(testData.getData(CUSTOMER_ID), "0");
    }

    @Then("^the removed comment should not be in the profile$")
    public void theRemovedCommentShouldNotBeInTheProfile() throws Throwable {
        CustomerProfileService customerProfileService = testData.getData(GET_CUSTOMER_PROFILE_SERVICE);
        customerProfileService.assertThat().noCommentsExistInProfile();
    }

    @When("^I attempt to remove a comment with a commentID not matching the customerID$")
    public void iAttemptToRemoveACommentWithACommentIDNotMatchingTheCustomerID() throws Throwable {
        addComment();
        updateCustomerDetailsSteps.iCreateANewValidCustomer();
        removeComment();
    }

    private String getPropertyConfigFromString(ACTION action) {
        switch (action) {
            case add:
                return "addingCommentsAllowed";
            case read:
                return "readingCommentsAllowed";
            case update:
                return "updatingCommentsAllowed";
            case delete:
                return "deletingCommentsAllowed";
            default:
                fail("Could not find property configuration customer comments");
        }
        return null;
    }

    @And("^(?:that a comment has been added to (?:the|a) Customer|I attempt to add a customer comment|I add a comment to a customer)(?::|)(.*)$")
    public void thatACommentHasBeenAddedToTheCustomer(String comment) throws Throwable {
        comment = comment.replace(":", "");
        if (comment.isEmpty()) {
            addComment();
        } else {
            addComment(testData.getData(CUSTOMER_ID), comment);
        }
    }

    @When("^I attempt to add a comment to a non-existing customer$")
    public void iAttemptToAddACustomerCommentUsingAnIncorrectCustomerID() throws Throwable {
        addComment("wrongcustomerid", "comment with wrong customerid");
    }

    @And("^" + CHANNELS + " (is|is not) configured to (add|read|update|delete) a customer comment$")
    public void channelIsConfiguredToAddACustomerComment(String channel, String isIsNot, ACTION action) throws Throwable {
        testData.setChannel(channel);
        String propertyKey = getPropertyConfigFromString(action);
        boolean propertyValue = Boolean.parseBoolean(channelPropertiesHelper.getPropertyValueByChannelAndKey(testData.getChannel(), propertyKey));
        assertThat(propertyValue).isEqualTo(isIsNot.equals("is"));
        testData.setChannel(channel);
    }

    @Then("^I will return Channel, User ID, comment type and created DateTime Stamp for the deleted comment$")
    public void iWillReturnChannelUserIDCommentTypeFreeTextCommentDeletedDateTimeStamp() throws Throwable {
        CustomerProfileService customerProfileService = testData.getData(GET_CUSTOMER_PROFILE_SERVICE);
        pollingLoop().untilAsserted(() -> {
            customerProfileService.invoke();
            customerProfileService.assertThat().commentsExistInProfile(theComment, theCommentCode, "DELETED");
        });

        customerProfileService.assertThat().updatedCommentFieldsAreCorrectInDatabase(commentsDao.getCommentWithId(theCommentCode));
    }

    @Then("^the response doesn't contains the comment section$")
    public void theResponseDoesnTContainsTheCommentSection() {
        CustomerProfileService customerProfileService = testData.getData(GET_CUSTOMER_PROFILE_SERVICE);
        pollingLoop().untilAsserted(() -> {
            customerProfileService.invoke();
            customerProfileService.assertThat().commentsAreNotShowed();
        });
    }

    @Given("^I added a comment to a customer$")
    public void addComment() {
        addComment(testData.getData(CUSTOMER_ID), "some comment:" + UUID.randomUUID());
    }

    private void addComment(String customerId, String comment) {
        AddCommentToCustomerRequestBody requestBody = AddCommentToCustomerRequestBody.builder().comment(comment).build();
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).path(ADD_COMMENT_TO_CUSTOMER).build();
        AddCommentsToCustomerService customerCommentService = serviceFactory.addCommentsToCustomer(new AddCommentToCustomerRequest(HybrisHeaders.getValidWithToken(testData.getChannel(),testData.getAccessToken()).build(), pathParams, requestBody));

        customerCommentService.invoke();

        testData.setData(SERVICE, customerCommentService);

        theComment = comment;
        if (!Optional.ofNullable(customerCommentService.getErrors()).isPresent()) {
            theCommentCode = customerCommentService.getResponse().getOperationConfirmation().getCommentCode();
            testData.setData(SerenityFacade.DataKeys.COMMENT_CODE,theCommentCode);
        }
    }

    @When("^I update the customer comment$")
    public void updateComment() {
        updateComment(testData.getData(CUSTOMER_ID), "updated comment:" + UUID.randomUUID(), theCommentCode);
    }

    @When("^I update the customer comment from Digital, PublicApiMobile or PublicApiB2B$")
    public void updateCommentFromDigitalPublicApiMobileOrPublicApiBB() {
        String[] channels = {"Digital", "PublicApiMobile", "PublicApiB2B"};
        int rnd = new Random().nextInt(channels.length);
        reporter.info(channels[rnd] + " channel selected");
        testData.setChannel(channels[rnd]);
        updateComment(testData.getData(CUSTOMER_ID), "updated comment:" + UUID.randomUUID(), theCommentCode);
    }

    private void updateComment(String customerId, String comment, String aCommentCode) {
        AddCommentToCustomerRequestBody requestBody = AddCommentToCustomerRequestBody.builder().comment(comment).build();
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).commentCode(aCommentCode).path(MANAGE_CUSTOMER_COMMENTS).build();
        UpdateCommentsToCustomerService customerCommentService = serviceFactory.updateCommentsToCustomer(new UpdateCommentToCustomerRequest(HybrisHeaders.getValidWithToken(testData.getChannel(),testData.getAccessToken()).build(), pathParams, requestBody));
        customerCommentService.invoke();
        testData.setData(SERVICE, customerCommentService);
        theComment = comment;
        if (!Optional.ofNullable(customerCommentService.getErrors()).isPresent()) {
            theCommentCode = customerCommentService.getResponse().getUpdateConfirmation().getCommentCode();
        }
    }

    @When("^I remove the customer comment$")
    public void removeComment() {
        removeComment(testData.getData(CUSTOMER_ID), theCommentCode);
    }

    @When("^I remove the customer comment from Digital, PublicApiMobile or PublicApiB2B$")
    public void iRemoveTheCustomerCommentFromDigitalPublicApiMobileOrPublicApiBB() {
        String[] channels = {"Digital", "PublicApiMobile", "PublicApiB2B"};
        int rnd = new Random().nextInt(channels.length);
        reporter.info(channels[rnd] + " channel selected");
        testData.setChannel(channels[rnd]);
        removeComment(testData.getData(CUSTOMER_ID), theCommentCode);
    }

    private void removeComment(String customerId, String aCommentCode) {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).commentCode(aCommentCode).path(MANAGE_CUSTOMER_COMMENTS).build();

        RemoveCommentsToCustomerService customerCommentService = serviceFactory.removeCommentsToCustomer(new RemoveCommentToCustomerRequest(HybrisHeaders.getValidWithToken(testData.getChannel(),testData.getAccessToken()).build(), pathParams));
        customerCommentService.invoke();

        testData.setData(SERVICE, customerCommentService);

        if (!Optional.ofNullable(customerCommentService.getErrors()).isPresent()) {
            theCommentCode = customerCommentService.getResponse().getOperationConfirmation().getCommentCode();
        }
    }

    private enum ACTION {
        add, read, update, delete
    }
}
