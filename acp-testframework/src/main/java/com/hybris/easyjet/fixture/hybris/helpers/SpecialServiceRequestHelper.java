package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateSSRRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddSSRRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateSSRRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.SavedSSRs;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateCustomerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE;

/**
 * Created by markphipps on 04/05/2017.
 */
@Component
public class SpecialServiceRequestHelper {
    // TODO: move everything out of step def class into here, then @Autowire it back into the step def class


    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private SerenityFacade testData;

    private AddUpdateSSRRequestBody addOrUpdateSSRRequestBody;
    private String customerId = null;
    private UpdateCustomerDetailsService addOrUpdateSSRCustomerService;

    public void sendAddSSRRequestWithChannel(String addOrUpdate, String channel) throws Throwable {
        if(customerId == null) {
            customerId = testData.getData(CUSTOMER_ID);
        }

        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).path(ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE).build();
        if("add".equals(addOrUpdate)) {
            addOrUpdateSSRCustomerService = serviceFactory.addSSRService(new AddSSRRequest(HybrisHeaders.getValid(channel).build(), pathParams, addOrUpdateSSRRequestBody));
        } else {
            addOrUpdateSSRCustomerService = serviceFactory.updateSSRService(new UpdateSSRRequest(HybrisHeaders.getValid(channel).build(), pathParams, addOrUpdateSSRRequestBody));
        }
        addOrUpdateSSRCustomerService.invoke();
        testData.setData(SERVICE, addOrUpdateSSRCustomerService);
    }

    public void iAddMoreThanXSSRInTheRequestWithChannel(String addOrUpdate, Integer numberOfRequest, String channel) throws Throwable {
        for(int i = 0; i <= numberOfRequest; i++) {
            sendAddSSRRequestWithChannel(addOrUpdate, channel);
        }
    }

    public void sendSSrBodyRequestWhereTCAcceptanceIsNull() throws Throwable {
        SavedSSRs.Ssr ssr = new SavedSSRs.Ssr();

        ssr.setCode("WCHC");
        ssr.setIsTandCsAccepted(null);

        addOrUpdateSSRRequestBody = AddUpdateSSRRequestBody.builder()
                .ssrs(new ArrayList<SavedSSRs.Ssr>() {{
                    add(ssr);
                }}).build();
    }

    public void sendAddWchcSSRRequest(String addOrUpdate) throws Throwable {
        addSSRFor(addOrUpdate, new ArrayList<String>(Arrays.asList("WCHC")));
    }

    public void addSSRFor(String addOrUpdate, List<String> ssrToAddOrUpdate) throws Throwable {
        List<SavedSSRs.Ssr> ssrs = new ArrayList<SavedSSRs.Ssr>();
        for(String ssrToAdd : ssrToAddOrUpdate) {
            SavedSSRs.Ssr ssr = new SavedSSRs.Ssr();
            ssr.setCode(ssrToAdd);
            if (ssrToAdd.equalsIgnoreCase("WCHC")) ssr.setIsTandCsAccepted(Boolean.TRUE);
            ssrs.add(ssr);
        }
        addOrUpdateSSRRequestBody = AddUpdateSSRRequestBody.builder().ssrs(ssrs).build();
        sendAddSsrRequest(addOrUpdate);
    }

    public void sendEmptySSRBlock(String addOrUpdate) throws Throwable {
        addOrUpdateSSRRequestBody = AddUpdateSSRRequestBody.builder()
                .ssrs(new ArrayList<SavedSSRs.Ssr>() {{}}).build();
        sendAddSSRRequestWithChannel(addOrUpdate, testData.getChannel());
    }

    public void sendSsrFromChannel(String addOrUpdate, String ssr, String channel) throws Throwable {
        SavedSSRs.Ssr customerSsr = new SavedSSRs.Ssr();
        customerSsr.setCode(ssr);
        customerSsr.setIsTandCsAccepted(true);
        addOrUpdateSSRRequestBody = AddUpdateSSRRequestBody.builder()
                .ssrs(new ArrayList<SavedSSRs.Ssr>() {{
                    add(customerSsr);
                }}).build();
        sendAddSSRRequestWithChannel(addOrUpdate, channel);
    }

    public void sendNumberOfSsrWithChannel(int maxSSRs, String addOrUpdate, String channel) throws Throwable {
        ArrayList<SavedSSRs.Ssr> ssrs = new ArrayList<>();

        for(int i=0;i < maxSSRs; i++) {
            SavedSSRs.Ssr ssr = new SavedSSRs.Ssr();
            if(i==0) ssr.setCode("BLND");
            else if(i==1) ssr.setCode("DEPU");
            else if(i==2) ssr.setCode("DEPA");
            else if(i==3) ssr.setCode("CARE");
            else if(i==4) ssr.setCode("VIP");
            else ssr.setCode("DEAF");
            ssrs.add(ssr);
        }

        addOrUpdateSSRRequestBody = AddUpdateSSRRequestBody.builder()
                .ssrs(ssrs).build();

        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE).build();
        if(addOrUpdate.equalsIgnoreCase("add")) {
            addOrUpdateSSRCustomerService = serviceFactory.addSSRService(new AddSSRRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, addOrUpdateSSRRequestBody));
        } else {
            addOrUpdateSSRCustomerService = serviceFactory.updateSSRService(new UpdateSSRRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, addOrUpdateSSRRequestBody));
        }
        testData.setData(SERVICE, addOrUpdateSSRCustomerService);
        addOrUpdateSSRCustomerService.invoke();
    }

    public void setCustomerId(String id) {
        customerId = id;
    }

    private void sendAddSsrRequest(String addOrUpdate) {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(ADD_OR_UPDATE_SSR_CUSTOMER_SERVICE).build();
        if(addOrUpdate.equalsIgnoreCase("add")) {
            addOrUpdateSSRCustomerService = serviceFactory.addSSRService(new AddSSRRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, addOrUpdateSSRRequestBody));
        } else {
            addOrUpdateSSRCustomerService = serviceFactory.updateSSRService(new UpdateSSRRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, addOrUpdateSSRRequestBody));
        }
        testData.setData(SERVICE, addOrUpdateSSRCustomerService);
        addOrUpdateSSRCustomerService.invoke();
    }

    public boolean ssrAddedSuccessfully() {
        return addOrUpdateSSRCustomerService.getResponse().getUpdateConfirmation().getCustomerId().equalsIgnoreCase(testData.getData(CUSTOMER_ID));
    }
}
