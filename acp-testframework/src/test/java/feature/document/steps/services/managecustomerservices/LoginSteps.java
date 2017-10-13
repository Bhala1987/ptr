package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.login.LoginDetails;
import com.hybris.easyjet.fixture.hybris.invoke.requests.LoginRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.LoginDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.REGISTER_CUSTOMER_REQUEST;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

/**
 * LoginSteps handle the communication with the login service for the customer.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class LoginSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private LoginDetailsService customerLoginService;
    private LoginDetails.LoginDetailsBuilder loginRequestBody;

    private String email;
    private String password;

    private void setRequestBody() {
        // This is the only known agent
        loginRequestBody = LoginDetails.builder()
                .email(email)
                .password(password)
                .rememberme(false);
    }

    private void invokeLoginService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        customerLoginService = serviceFactory.loginCustomer(new LoginRequest(headers.build(), loginRequestBody.build()));
        testData.setData(SERVICE, customerLoginService);
        customerLoginService.invoke();
    }

    private void sendLoginRequest() {
        setRequestBody();
        invokeLoginService();
    }

    public void login() {
        RegisterCustomerRequestBody registerCustomerRequestBody = testData.getData(REGISTER_CUSTOMER_REQUEST);
        email = registerCustomerRequestBody.getPersonalDetails().getEmail();
        password = registerCustomerRequestBody.getPersonalDetails().getPassword();
        sendLoginRequest();
    }

    public void login(String email, String password) {
        this.email = email;
        this.password = password;
        sendLoginRequest();
    }

}