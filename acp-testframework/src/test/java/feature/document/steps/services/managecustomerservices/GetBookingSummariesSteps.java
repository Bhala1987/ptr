package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetBookingSummaryRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingSummaryService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.GET_BOOKING_SUMMARIES;

/**
 * GetBookingSummariesSteps handle the communication with the getBookingSummaries service.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author Raja
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetBookingSummariesSteps {

    private static final String INCORRECT_CUSTOMER_ID = "0000";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private GetBookingSummaryService getBookingSummaryService;
    private CustomerPathParams.CustomerPathParamsBuilder customerPathParams;

    private String customerId;

    private void setPathParameter() {
        customerPathParams = CustomerPathParams.builder()
                .path(GET_BOOKING_SUMMARIES)
                .customerId(customerId);
    }

    private void invokeGetBookingSummaryService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        getBookingSummaryService = serviceFactory.getBookingSummaries(new GetBookingSummaryRequest(headers.build(), customerPathParams.build()));
        testData.setData(SERVICE, getBookingSummaryService);
        getBookingSummaryService.invoke();
    }

    private void sendGetBookingRequest() {
        setPathParameter();
        invokeGetBookingSummaryService();
    }

    @When("^I send a getBookingSummaries request$")
    public void iSendAGetbookingsummariesRequest() {
        customerId = testData.getData(CUSTOMER_ID);
        sendGetBookingRequest();
    }

    @When("^I send a getBookingSummaries request with incorrect customer id$")
    public void iSendAGetbookingsummariesRequestWithIncorrectCustomerId() {
        customerId = INCORRECT_CUSTOMER_ID;
        sendGetBookingRequest();
    }

}