package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.SignificantOtherPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetSignificantOtherRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.SignificantOtherService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * GetSignificantOthersSteps handle the communication with the getSignificantOthers service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetSignificantOthersSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private SignificantOtherService significantOtherService;
    private SignificantOtherPathParams.SignificantOtherPathParamsBuilder significantOtherPathParams;

    private void setPathParameter() {
        significantOtherPathParams = SignificantOtherPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID));
    }

    private void invokeGetSignificantOthersService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        significantOtherService = serviceFactory.getSignificantOtherService(new GetSignificantOtherRequest(headers.build(), significantOtherPathParams.build()));
        testData.setData(SERVICE, significantOtherService);
        significantOtherService.invoke();
    }

    private void sendGetSignificantOthersRequest() {
        setPathParameter();
        invokeGetSignificantOthersService();
    }

    @When("^I send the request to getSignificantOthers service$")
    public void getSignificantOthers() {
        sendGetSignificantOthersRequest();
    }

}