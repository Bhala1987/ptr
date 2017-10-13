package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.IdentityDocumentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.SetAPIRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.SetAPIService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams.CustomerPaths.APIS;

/**
 * SetAPIsSteps handle the communication with the setAPIs service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class SetAPIsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private SetAPIService setAPIService;
    private CustomerPathParams.CustomerPathParamsBuilder customerPathParamsBuilder;
    private IdentityDocumentRequestBody.IdentityDocumentRequestBodyBuilder identityDocumentRequestBody;

    private void setPathParameter() {
        customerPathParamsBuilder = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(APIS);
    }

    private void setRequestBody() {
        testData.dataFactory.randomize(new Random(System.currentTimeMillis()).nextInt());
        String[] fullname = testData.dataFactory.getName().split(" ");

        IdentityDocumentRequestBody.Name name = IdentityDocumentRequestBody.Name.builder()
                .firstName(fullname[0])
                .lastName(fullname[1])
                .fullName(fullname[0] + " " + fullname[1])
                .title("mr")
                .build();

        identityDocumentRequestBody = IdentityDocumentRequestBody.builder()
                .name(name)
                .gender("MALE")
                .dateOfBirth("")
                .nationality("GBR")
                .documentType("PASSPORT")
                .documentNumber("")
                .countryOfIssue("GBR")
                .documentExpiryDate("");
    }

    private void invokeSetAPIsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        setAPIService = serviceFactory.setApi(new SetAPIRequest(headers.build(), customerPathParamsBuilder.build(), identityDocumentRequestBody.build()));
        testData.setData(SERVICE, setAPIService);
        setAPIService.invoke();
    }

    private void sendSetAPIsRequest() {
        setPathParameter();
        setRequestBody();
        invokeSetAPIsService();
    }

    @When("^I send the request to setAPIs service$")
    public void setAPIs() {
        sendSetAPIsRequest();
    }

}