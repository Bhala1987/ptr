package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.DependantsPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.IdentityDocumentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants.AddDependantsIdentityDocumentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants.UpdateDependantsEjPlusCardNumberRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants.UpdateDependantsIdentityDocumentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants.UpdateDependantsSavedSSRsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateDependantsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateDependantsService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.DependantsPathParams.DependantsPaths.*;

/**
 * UpdateDependentsSteps handle the communication with the updateDependents service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdateDependentsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private UpdateDependantsService updateDependantsService;
    private DependantsPathParams.DependantsPathParamsBuilder dependantsPathParams;
    private AddDependantsIdentityDocumentRequestBody.AddDependantsIdentityDocumentRequestBodyBuilder addDependantsIdentityDocumentRequestBody;
    private UpdateDependantsIdentityDocumentRequestBody.UpdateDependantsIdentityDocumentRequestBodyBuilder updateDependantsIdentityDocumentRequestBody;
    private UpdateDependantsEjPlusCardNumberRequestBody.UpdateDependantsEjPlusCardNumberRequestBodyBuilder updateDependantsEjPlusCardNumberRequestBody;
    private UpdateDependantsSavedSSRsRequestBody.UpdateDependantsSavedSSRsRequestBodyBuilder updateDependantsSavedSSRsRequestBody;

    private DependantsPathParams.DependantsPaths path;
    private String operation;

    private void setPathParameter() {
        dependantsPathParams = DependantsPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .passengerId(testData.getData(DEPENDENT_ID))
                .path(path);
    }

    private void setAddIdentityDocumentRequestBody() {
        testData.dataFactory.randomize(new Random(System.currentTimeMillis()).nextInt());
        String[] fullname = testData.dataFactory.getName().split(" ");

        IdentityDocumentRequestBody.Name name = IdentityDocumentRequestBody.Name.builder()
                .firstName(fullname[0])
                .lastName(fullname[1])
                .fullName(fullname[0] + " " + fullname[1])
                .title("mr")
                .build();

        addDependantsIdentityDocumentRequestBody = AddDependantsIdentityDocumentRequestBody.addBuilder()
                .name(name)
                .gender("MALE")
                .dateOfBirth("")
                .nationality("GBR")
                .documentType("PASSPORT")
                .documentNumber("")
                .countryOfIssue("GBR")
                .documentExpiryDate("");
    }

    private void setUpdateIdentityDocumentRequestBody() {
        String[] fullname = testData.dataFactory.getName().split(" ");

        IdentityDocumentRequestBody.Name name = IdentityDocumentRequestBody.Name.builder()
                .firstName(fullname[0])
                .lastName(fullname[1])
                .fullName(fullname[0] + " " + fullname[1])
                .title("mr")
                .build();

        updateDependantsIdentityDocumentRequestBody = UpdateDependantsIdentityDocumentRequestBody.updateBuilder()
                .name(name)
                .gender("MALE")
                .dateOfBirth("")
                .nationality("GBR")
                .documentType("PASSPORT")
                .documentNumber("")
                .countryOfIssue("GBR")
                .documentExpiryDate("");
    }

    private void setEjPlusMembershipRequestBody() {
        updateDependantsEjPlusCardNumberRequestBody = UpdateDependantsEjPlusCardNumberRequestBody.builder()
                .ejPlusCardNumber("");
    }

    private void setSavedSSRsRequestBody() {
        updateDependantsSavedSSRsRequestBody = UpdateDependantsSavedSSRsRequestBody.builder()
                .ssr(UpdateDependantsSavedSSRsRequestBody.SSR.builder()
                        .ssrs(Collections.emptyList())
                        .remarks(Collections.emptyList())
                        .build());
    }

    private void invokeUpdateDependentsAddIdentityDocumentService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updateDependantsService = serviceFactory.updateDependantsService(new UpdateDependantsRequest(headers.build(), dependantsPathParams.build(), addDependantsIdentityDocumentRequestBody.build()));
        testData.setData(SERVICE, updateDependantsService);
        updateDependantsService.invoke();
    }

    private void invokeUpdateDependentsUpdateIdentityDocumentService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updateDependantsService = serviceFactory.updateDependantsService(new UpdateDependantsRequest(headers.build(), dependantsPathParams.build(), updateDependantsIdentityDocumentRequestBody.build()));
        testData.setData(SERVICE, updateDependantsService);
        updateDependantsService.invoke();
    }

    private void invokeUpdateDependentsEjPlusCardNumberService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updateDependantsService = serviceFactory.updateDependantsService(new UpdateDependantsRequest(headers.build(), dependantsPathParams.build(), updateDependantsEjPlusCardNumberRequestBody.build()));
        testData.setData(SERVICE, updateDependantsService);
        updateDependantsService.invoke();
    }

    private void invokeUpdateDependentsSavesSSRsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updateDependantsService = serviceFactory.updateDependantsService(new UpdateDependantsRequest(headers.build(), dependantsPathParams.build(), updateDependantsSavedSSRsRequestBody.build()));
        testData.setData(SERVICE, updateDependantsService);
        updateDependantsService.invoke();
    }

    private void sendUpdateDependentsRequest() {
        setPathParameter();
        switch (path) {
            case EJ_PLUS_CARD_NUMBER:
                setEjPlusMembershipRequestBody();
                invokeUpdateDependentsEjPlusCardNumberService();
                break;
            case IDENTITY_DOCUMENT:
                if (operation.equals("add")) {
                    setAddIdentityDocumentRequestBody();
                    invokeUpdateDependentsAddIdentityDocumentService();
                } else if (operation.equals("update")) {
                    setUpdateIdentityDocumentRequestBody();
                    invokeUpdateDependentsUpdateIdentityDocumentService();
                }
                break;
            case SSR:
                setSavedSSRsRequestBody();
                invokeUpdateDependentsSavesSSRsService();
                break;
        }
    }

    @When("^I send the request to updateDependents service to (update ejPlusCardNumber|add identityDocuments|update identityDocuments|update savedSSRs)$")
    public void updateDependents(String service) {
        switch (service) {
            case "update ejPlusCardNumber":
                path = EJ_PLUS_CARD_NUMBER;
                break;
            case "add identityDocuments":
                path = IDENTITY_DOCUMENT;
                operation = "add";
                break;
            case "update identityDocuments":
                path = IDENTITY_DOCUMENT;
                operation = "update";
                break;
            case "update savedSSRs":
                path = SSR;
                break;
        }
        sendUpdateDependentsRequest();
    }

}