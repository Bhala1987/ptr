package feature.document.steps.services.productmanagementservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AdditionalSeatReasonsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.AdditionalSeatReasonsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.HEADERS;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

@ContextConfiguration(classes = TestApplication.class)
public class getAdditionalSeatReasonsSteps {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private AdditionalSeatReasonsService additionalSeatReasonsService;


    private void invokeGetAdditionalSeatReasonsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        additionalSeatReasonsService = serviceFactory.getAdditionalSeatReasons(new AdditionalSeatReasonsRequest(headers.build()));
        testData.setData(SERVICE, additionalSeatReasonsService);
        additionalSeatReasonsService.invoke();
    }

    @When("^I send the request to Additional Seat Reason service$")
    public void iSendTheRequestToAdditionalSeatReasonService() throws Throwable {
        invokeGetAdditionalSeatReasonsService();
    }

    @Then("^a list of Additional Seat Reason is returned$")
    public void aListOfAdditionalSeatReasonIsReturned() throws Throwable {
        additionalSeatReasonsService.assertThat().theResponseNotIsEmpty();
    }
}
