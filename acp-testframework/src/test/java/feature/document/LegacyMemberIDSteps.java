package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.EventMessageCreationHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessagecreation.CreateCustomerEventMessageRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.services.eventmessagecreation.EventMessageService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;

/**
 * Created by Alberto
 */
@ContextConfiguration(classes = TestApplication.class)
public class LegacyMemberIDSteps {
    protected static Logger LOG = LogManager.getLogger(LegacyMemberIDSteps.class);

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private EventMessageCreationHelper eventMessageCreationHelper;
    @Autowired
    private CustomerHelper customerHelper;

    private CreateCustomerEventMessageRequestBody requestBody;
    private Integer memberId;
    private static final String MEMBER_ID = "memberId";
    private static final String MEMBER_ID_REGEX = "\"memberId\":\\d+";

    @And("^that the channel has initiated a registerCustomer request with ([^\"]*)$")
    public void thatTheChannelHasInitiatedARegisterCustomerReequestWithMemberID(Integer memberId) {
        this.memberId = memberId;
    }

    @When("^the system receive the valid request$")
    public void theSystemReceiveTheValidRequest()  throws Throwable {
        requestBody = CreateCustomerEventMessageRequestBody.builder().build();
        requestBody.setCustomerId(testData.getData(CUSTOMER_ID));
    }

    @And("^the request has the memberID$")
    public void theRequestHasTheMemberID() throws Throwable{
        customerHelper.createRandomCustomerWithMemberId(testData.getChannel(), memberId);
    }

    @Then("^it will generate a request to AL to getLegacyUserID$")
    public void itWillGenerateARequestToALToGetLegacyUserID() throws Throwable {
        eventMessageCreationHelper.validateCustomerCreationMessage(testData.getData(CUSTOMER_ID));
        EventMessageService createCustomerEventMessageService =
                eventMessageCreationHelper.getCustomerRegisterMessage(testData.getData(CUSTOMER_ID));

        createCustomerEventMessageService.assertThat()
                .checkThatMessageContainsFieldWithValue(MEMBER_ID, MEMBER_ID_REGEX, String.valueOf(memberId));

    }

    @When("^the system create the customer without memberId$")
    public void theSystemCreateTheCustomerWithoutMemberId() throws Throwable {
        customerHelper.createRandomCustomer(testData.getChannel());
    }

    @Then("^it will populate the member ID with the received legacyUser ID$")
    public void itWillPopulateTheMemberIDWithTheReceivedLegacyUserID() throws Throwable {
        eventMessageCreationHelper.validateCustomerCreationMessage(testData.getData(CUSTOMER_ID));
        EventMessageService createCustomerEventMessageService =
                eventMessageCreationHelper.getCustomerRegisterMessage(testData.getData(CUSTOMER_ID));

        createCustomerEventMessageService.assertThat().checkThatMessageContainsField(MEMBER_ID);
    }
}
