package feature.document.steps.services.productmanagementservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.asserters.TermsAndConditionsAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.GetTermsAndConditionsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.GetTermsAndConditionsService;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

/**
 * TermsAndConditionsSteps handle the communication with the termsAndConditions service.
 * It makes use of testData to store parameters that can be used by other steps.
 * <p>
 * Created by rajakm on 13/09/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class TermsAndConditionsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private TermsAndConditionsAssertion termsAndConditionsAssertion;

    private GetTermsAndConditionsService getTermsAndConditionsService;

    private void invokeGetTermsAndConditionsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        getTermsAndConditionsService = serviceFactory.getTermsAndConditions(new GetTermsAndConditionsRequest(headers.build()));
        testData.setData(SERVICE, getTermsAndConditionsService);
        getTermsAndConditionsService.invoke();
    }

    @When("^I sent a request to getTermsAndConditions service$")
    public void iSendARequestToGetTermsAndConditionsService() {
        invokeGetTermsAndConditionsService();
        termsAndConditionsAssertion.setResponse(getTermsAndConditionsService.getResponse());
    }

    @Then("^I should receive the list of terms and conditions in the requested language$")
    public void iShouldReceiveTheListOfTermsAndConditionsInTheRequestedLanguage() {
        HybrisHeaders headers = ((HybrisHeaders.HybrisHeadersBuilder) testData.getData(SerenityFacade.DataKeys.HEADERS)).build();
        if (StringUtils.isBlank(headers.getAcceptLanguage())) {
            termsAndConditionsAssertion.verifyAllTheLocaleWereReturned();
        } else {
            termsAndConditionsAssertion.verifyTheSpecificLocaleWereReturned(headers.getAcceptLanguage());
        }
    }
}
