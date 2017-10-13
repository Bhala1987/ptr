package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.PreferencesPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences.AncillaryPreferences;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences.CommunicationPreferences;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences.TravelPreferences;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences.UpdateCustomerPreferencesFullRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateCustomerDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateCustomerDetailsService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * UpdatePreferencesSteps handle the communication with the updatePreferences service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdatePreferencesSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private UpdateCustomerDetailsService updatePreferencesService;
    private PreferencesPathParams.PreferencesPathParamsBuilder preferencesPathParams;
    private UpdateCustomerPreferencesFullRequestBody.UpdateCustomerPreferencesFullRequestBodyBuilder updateCustomerPreferencesFullRequestBody;

    private void setPathParameter() {
        preferencesPathParams = PreferencesPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID));
    }

    private void setRequestBody() {
        TravelPreferences travelPreferences = TravelPreferences.builder()
                .preferredAirports(Collections.emptyList())
                .travellingPeriod(new CustomerProfileResponse.Period())
                .travellingSeasons(Collections.emptyList())
                .travellingTo(Collections.emptyList())
                .travellingWhen(Collections.emptyList())
                .travellingWith(Collections.emptyList())
                .tripTypes(Collections.emptyList())
                .build();

        CommunicationPreferences communicationPreferences = CommunicationPreferences.builder()
                .contactMethods(Collections.emptyList())
                .contactTypes(Collections.emptyList())
                .frequency("")
                .keyDates(Collections.emptyList())
                .optedOutMarketing(Collections.emptyList())
                .optedOutPeriod(new CustomerProfileResponse.Period())
                .build();

        AncillaryPreferences ancillaryPreferences = AncillaryPreferences.builder()
                .seatingPreferences(Collections.emptyList())
                .holdBagQuantity("")
                .holdBagWeight("")
                .seatNumber("")
                .build();

        updateCustomerPreferencesFullRequestBody = UpdateCustomerPreferencesFullRequestBody.builder()
                .travelPreferences(travelPreferences)
                .communicationPreferences(communicationPreferences)
                .ancillaryPreferences(ancillaryPreferences);
    }

    private void invokeUpdatePreferencesService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updatePreferencesService = serviceFactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(headers.build(), preferencesPathParams.build(), updateCustomerPreferencesFullRequestBody.build()));
        testData.setData(SERVICE, updatePreferencesService);
        updatePreferencesService.invoke();
    }

    private void sendUpdatePreferencesRequest() {
        setPathParameter();
        setRequestBody();
        invokeUpdatePreferencesService();
    }

    @When("^I send the request to updatePreferences service$")
    public void updatePreferences() {
        sendUpdatePreferencesRequest();
    }

}