package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.CustomerHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveApisRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateCustomerDetailsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.REMOVE_APIS;

/**
 * Created by robertadigiorgio on 06/04/2017.
 */

@ContextConfiguration(classes = TestApplication.class)

public class RemoveAPISForACustomer {

    private UpdateCustomerDetailsService removeApisCustomerService;

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private SerenityFacade testData;

    private String customerId;
    private String documentId;

    @And("^I want to delete Apis information$")
    public void iWantToDeleteApisImformation() throws Throwable {
        customerId = testData.getData(CUSTOMER_ID);
        documentId = testData.getDocumentId();
    }

    @But("^the request contains \"([^\"]*)\"$")
    public void theRequestContains(String invalid) throws Throwable {

        switch (invalid) {
            case "invalid customerId":
                customerId = "000";
                testData.setData(CUSTOMER_ID, customerId);
                break;
            case "invalid documentId":
                documentId = "000";
                break;
        }
    }

    @When("^I send a request do delete APIS information of the Customer in Customer profile with \"([^\"]*)\"$")
    public void iSendARequestDoDeleteAPISInformationOfTheCustomerInCustomerProfileWith(String channel) throws Throwable {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).path(REMOVE_APIS).documentId(documentId).build();
        removeApisCustomerService = serviceFactory.removeApisService(new RemoveApisRequest(HybrisHeaders.getValid(channel).build(), pathParams));
        testData.setData(SERVICE, removeApisCustomerService);
        removeApisCustomerService.invoke();
    }

    @And("^return confirmation$")
    public void returnConfirmation() throws Throwable {
        removeApisCustomerService.assertThat().customerUpdated(customerId);
    }


}
