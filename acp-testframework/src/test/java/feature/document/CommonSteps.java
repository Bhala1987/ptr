package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.helpers.BookingCommentHelper;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.EventMessageCreationHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * Created by giuseppe on 16/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class CommonSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BookingCommentHelper bookingCommentHelper;

    @Autowired
    private EventMessageCreationHelper eventMessageCreationHelper;
    private IService service;
    @Autowired
    private CustomerHelper customerHelper;

    @Given("^I am using the channel (.*)$")
    public void iAmUsingTheChannel(String channel) throws Throwable {
        testData.setChannel(channel);
        testData.setData(SerenityFacade.DataKeys.CHANNEL, channel);
        testData.setData(HEADERS, HybrisHeaders.getValid(channel));

        if (channel.trim().equals(CommonConstants.AD_CHANNEL) || channel.trim().equals(CommonConstants.AD_CUSTOMER_SERVICE)) {
            this.iLoginAsAgentWithUsernameAndPassword("rachel", "12341234");
            customerHelper.createRandomCustomer(channel);
            customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
        }
    }

    @Given("^I am using one of this channel (.*)$")
    public void iAmUsingOneChannel(String channel) throws Throwable {
        String[] channels = channel.split(",\\s*");
        int rnd = new Random().nextInt(channels.length);
        testData.setChannel(channels[rnd]);
        testData.setData(HEADERS, HybrisHeaders.getValid(channels[rnd]));

        if (channel.trim().equals(CommonConstants.AD_CHANNEL) || channel.trim().equals(CommonConstants.AD_CUSTOMER_SERVICE)) {
            this.iLoginAsAgentWithUsernameAndPassword("rachel", "12341234");
            customerHelper.createRandomCustomer(channel);
            customerHelper.getCustomerProfile(testData.getData(CUSTOMER_ID));
        }
    }

    @Then("^I will receive an error with code '(SVC_\\d+_\\d+)'$")
    public void iWillReceiveAnErrorWithCodeCode(String error) throws Throwable {
        service = testData.getData(SERVICE);
        service.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Given("^I login as agent with username as \"([^\"]*)\" and password as \"([^\"]*)\"$")
    public void iLoginAsAgentWithUsernameAndPassword(String username, String password) throws Throwable {
            bookingCommentHelper.agentLogin(username, password);
    }

    @Given("^I login as agent$")
    public void iLoginAsAgent() throws Throwable {
        iLoginAsAgentWithUsernameAndPassword("rachel","12341234");
    }


    @And("^I validate the json schema for (?:(add|update|remove) )?(.*) event$")
    public void validateJsonSchemaForEvent(String type, String eventType) throws Throwable {
//        below sleep is intetion
        Thread.sleep(3000);
        switch (eventType) {
            case "created booking":
                eventMessageCreationHelper.validateBookingCreationMessage(testData.getData(BOOKING_ID));
                break;
            case "updated booking":
                eventMessageCreationHelper.validateBookingUpdateMessage(testData.getData(BOOKING_ID));
                break;
            case "created customer":
                eventMessageCreationHelper.validateCustomerCreationMessage(testData.getData(CUSTOMER_ID));
                break;
            case "updated customer":
                eventMessageCreationHelper.validateCustomerUpdateMessage(testData.getData(CUSTOMER_ID));
                break;
            case "booking cancelled":
                eventMessageCreationHelper.validateBookingCancelledMessage(testData.getData(BOOKING_ID));
                break;
            case "comment to booking":
                eventMessageCreationHelper.validateCustomerCommentMessage(testData.getData(COMMENT_CODE),type);
                break;
            case "comment to customer":
                eventMessageCreationHelper.validateBookingCommentMessage(testData.getData(COMMENT_CODE),type);
                break;
        }
    }

    @And("^the (.*) event contains the correct (.*) name$")
    public void theBookingCancelledEventContainsTheCorrectName(String eventType, String performer) throws Throwable {

        switch (eventType) {
            case "booking cancelled":
                eventMessageCreationHelper.checkBookingCancelledEventContainsCorrectName(testData.getData(BOOKING_ID), performer);
                break;
            default:
                throw new IllegalArgumentException("Something strange happened");
        }
    }


}
