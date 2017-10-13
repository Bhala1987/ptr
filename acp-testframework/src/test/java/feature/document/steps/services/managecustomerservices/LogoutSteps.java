package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.LogoutRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerLogoutService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams.CustomerPaths.LOGOUT;

/**
 * LogoutSteps handle the communication with the logout service for the customer.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class LogoutSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private CustomerLogoutService customerLogoutService;
    private CustomerPathParams.CustomerPathParamsBuilder customerPathParams;

    private String customerId;

    private void setPathParameter() {
        customerPathParams = CustomerPathParams.builder()
                .customerId(customerId)
                .path(LOGOUT);
    }

    private void invokeLogoutService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        customerLogoutService = serviceFactory.logoutCustomer(new LogoutRequest(headers.build(), customerPathParams.build()));
        testData.setData(SERVICE, customerLogoutService);
        customerLogoutService.invoke();
    }

    private void sendLogoutRequest() {
        setPathParameter();
        invokeLogoutService();
    }

    @When("^I send a request to customer logout service$")
    public void logout() {
        customerId = testData.getData(CUSTOMER_ID);
        sendLogoutRequest();
    }

}