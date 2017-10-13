package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.dependants.UpdateDependantsEjPlusCardNumberRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateDependantsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateDependantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

/**
 * Created by markphipps on 24/03/2017.
 */
@Component
public class DependantsHelpers {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerHelper customerHelper;

    private UpdateDependantsService updateDependantsService;
    private UpdateDependantsRequest updateDependantsRequest;
    private String customerWithDependantId = "cus00000001";
    private String passengerId = "345678";
    private static String DEFAULT_CHANNEL = "Digital";


    public UpdateDependantsService getUpdateDependantsService() {
        return updateDependantsService;
    }

    public void createDependantUpdateEjPlusRequest(String customer, String dependant) throws EasyjetCompromisedException {
        String localCustomer, localDependant;

        if (customer == null) {
            customerHelper.loginWithValidCredentials(testData.getChannel(), "a.rossi@reply.co.uk", "1234", false);
            localCustomer = customerWithDependantId;
        } else {
            localCustomer = customer;
        }

        if (dependant == null) {
            localDependant = passengerId;
        } else {
            localDependant = dependant;
        }

        UpdateDependantsEjPlusCardNumberRequestBody ejPlusRequestBody = getValidUpdateDependantEjPlusRequestBody();

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(localCustomer)
                .passengerId(localDependant)
                .path(CustomerPathParams.CustomerPaths.UPDATE_DEPENDANTS_EJ_PLUS)
                .build();

        updateDependantsRequest = new UpdateDependantsRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), customerPathParams, ejPlusRequestBody);
    }

    public void processUpdateDependantEjPlusRequest() throws Throwable {
        updateDependantsService = serviceFactory.updateDependantsService(updateDependantsRequest);
        updateDependantsService.invoke();
        testData.setData(SERVICE, updateDependantsService);
    }

    public void createDependantUpdateEjPlusRequestWithEjPlusParameter(String ejPlusCardNumber) throws EasyjetCompromisedException {
        UpdateDependantsEjPlusCardNumberRequestBody ejPlusRequestBody = getValidUpdateDependantEjPlusRequestBody();
        ejPlusRequestBody.setEjPlusCardNumber(ejPlusCardNumber);

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithDependantId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.UPDATE_DEPENDANTS_EJ_PLUS) //!_EJ_PLUS?
                .build();

        updateDependantsRequest = new UpdateDependantsRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), customerPathParams, ejPlusRequestBody);
    }

    public boolean updateDependantWasSuccessful() {
        return !(getUpdateDependantsService().getResponse().getUpdateConfirmation().getCustomerId().isEmpty());
    }

    public String getCustomerWithDependantId() {
        customerHelper.loginWithValidCredentials(testData.getChannel(), "a.rossi@reply.co.uk", "1234", false);
        return customerWithDependantId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    private UpdateDependantsEjPlusCardNumberRequestBody getValidUpdateDependantEjPlusRequestBody() {
        UpdateDependantsEjPlusCardNumberRequestBody requestBody = UpdateDependantsEjPlusCardNumberRequestBody.builder()
                .ejPlusCardNumber("S630456")
                .build();
        return requestBody;
    }

    public void createDependantUpdateForDependantEjPlusRequestWithEjPlusParameter(String dependant, String eJPlus) {
        UpdateDependantsEjPlusCardNumberRequestBody ejPlusRequestBody = getValidUpdateDependantEjPlusRequestBody();
        ejPlusRequestBody.setEjPlusCardNumber(eJPlus);
        customerHelper.loginWithValidCredentials(testData.getChannel(), "a.rossi@reply.co.uk", "1234", false);
        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithDependantId)
                .passengerId(dependant)
                .path(CustomerPathParams.CustomerPaths.UPDATE_DEPENDANTS_EJ_PLUS)
                .build();

        updateDependantsRequest = new UpdateDependantsRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), customerPathParams, ejPlusRequestBody);
    }

    public void createDependantUpdateForDependantEjPlusRequestWithEjPlusParameter(String customer, String dependant, String eJPlus) {
        UpdateDependantsEjPlusCardNumberRequestBody ejPlusRequestBody = getValidUpdateDependantEjPlusRequestBody();
        ejPlusRequestBody.setEjPlusCardNumber(eJPlus);
        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customer)
                .passengerId(dependant)
                .path(CustomerPathParams.CustomerPaths.UPDATE_DEPENDANTS_EJ_PLUS)
                .build();

        updateDependantsRequest = new UpdateDependantsRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), customerPathParams, ejPlusRequestBody);
    }

    public void createUpdateDependantRequestWithEJPlusStatus(String eJPlus) {
        setDependantId("345678");
        UpdateDependantsEjPlusCardNumberRequestBody ejPlusRequestBody = getValidUpdateDependantEjPlusRequestBody();
        ejPlusRequestBody.setEjPlusCardNumber(eJPlus);
        customerHelper.loginWithValidCredentials(testData.getChannel(), "a.rossi@reply.co.uk", "1234", false);
        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithDependantId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.UPDATE_DEPENDANTS_EJ_PLUS)
                .build();

        updateDependantsRequest = new UpdateDependantsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), customerPathParams, ejPlusRequestBody);
    }

    public void setCustomerId(String id) {
        customerWithDependantId = id;
    }

    public void setDependantId(String id) {
        passengerId = id;
    }
}
