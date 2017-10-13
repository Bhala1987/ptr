package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.DependantsPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.DependantsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetDependantsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * GetDependentsSteps handle the communication with the getDependents service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetDependentsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private GetDependantsService getDependantsService;
    private DependantsPathParams.DependantsPathParamsBuilder dependantsPathParams;

    private void setPathParameter() {
        dependantsPathParams = DependantsPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID));
    }

    private void invokeGetDependentsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getDependantsService = serviceFactory.getDependantsService(new DependantsRequest(headers.build(), dependantsPathParams.build()));
        testData.setData(SERVICE, getDependantsService);
        getDependantsService.invoke();
    }

    private void sendGetDependentsRequest() {
        setPathParameter();
        invokeGetDependentsService();
    }

    @When("^I send the request to getDependents service$")
    public void getDependents() {
        sendGetDependentsRequest();
    }

}