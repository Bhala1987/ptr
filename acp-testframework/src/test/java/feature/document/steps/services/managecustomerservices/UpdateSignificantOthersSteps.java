package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.SignificantOtherPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.significantothers.SignificantOtherRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateSignificantOtherRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.SignificantOtherService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * UpdateSignificantOthersSteps handle the communication with the updateSignificantOthers service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdateSignificantOthersSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private SignificantOtherService significantOtherService;
    private SignificantOtherPathParams.SignificantOtherPathParamsBuilder significantOtherPathParams;
    private SignificantOtherRequestBody.SignificantOtherRequestBodyBuilder updateSignificantOtherRequestBody;

    private void setPathParameter() {
        significantOtherPathParams = SignificantOtherPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .passengerId(testData.getData(SIGNIFICANT_OTHER_ID));
    }

    private void setRequestBody() {
        testData.dataFactory.randomize(new Random(System.currentTimeMillis()).nextInt());
        String[] fullname = testData.dataFactory.getName().split(" ");

        updateSignificantOtherRequestBody = SignificantOtherRequestBody.builder()
                .type("adult")
                .title("mr")
                .firstName(fullname[0])
                .lastName(fullname[1])
                .age(26)
                .phoneNumber(testData.dataFactory.getNumberText(12))
                .email("success" + fullname[0] + "_" + testData.dataFactory.getNumberText(10) + "@abctest.com")
                .ejPlusCardNumber("")
                .nifNumber("")
                .flightClubId("")
                .flightClubExpiryDate("");
    }

    private void invokeUpdateSignificantOthersService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        significantOtherService = serviceFactory.updateSignificantOtherService(new UpdateSignificantOtherRequest(headers.build(), significantOtherPathParams.build(), updateSignificantOtherRequestBody.build()));
        testData.setData(SERVICE, significantOtherService);
        significantOtherService.invoke();
    }

    private void sendUpdateSignificantOthersRequest() {
        setPathParameter();
        setRequestBody();
        invokeUpdateSignificantOthersService();
    }

    @When("^I send the request to updateSignificantOthers service$")
    public void updateSignificantOthers() {
        sendUpdateSignificantOthersRequest();
    }

}