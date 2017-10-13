package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.SavedSSRsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddSSRRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateCustomerDetailsService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams.CustomerPaths.SSR;

/**
 * UpdateSpecialServiceRequestsSteps handle the communication with the updateSpecialServiceRequests service (aka SSRs).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdateSpecialServiceRequestsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private UpdateCustomerDetailsService updateCustomerDetailsService;
    private CustomerPathParams.CustomerPathParamsBuilder customerPathParamsBuilder;
    private SavedSSRsRequestBody.SavedSSRsRequestBodyBuilder savedSSRsRequestBody;

    private void setPathParameter() {
        customerPathParamsBuilder = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(SSR);
    }

    private void setRequestBody() {
        savedSSRsRequestBody = SavedSSRsRequestBody.builder()
                .ssrs(Collections.emptyList());
    }

    private void invokeUpdateSpecialServiceRequestsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updateCustomerDetailsService = serviceFactory.addSSRService(new AddSSRRequest(headers.build(), customerPathParamsBuilder.build(), savedSSRsRequestBody.build()));
        testData.setData(SERVICE, updateCustomerDetailsService);
        updateCustomerDetailsService.invoke();
    }

    private void sendUpdateSpecialServiceRequestsRequest() {
        setPathParameter();
        setRequestBody();
        invokeUpdateSpecialServiceRequestsService();
    }

    @When("^I send the request to updateSpecialServiceRequests service$")
    public void updateSpecialServiceRequests() {
        sendUpdateSpecialServiceRequestsRequest();
    }

}