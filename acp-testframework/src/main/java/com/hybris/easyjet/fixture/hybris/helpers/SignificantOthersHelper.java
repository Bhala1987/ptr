package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.CustomerProfileQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.IdentityDocumentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.significantothers.SignificantOtherRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.significantothers.SignificantOtherSSRsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.significantothers.SignificantOtherSavedSSRsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.LOGOUT;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory.getRandomEmail;

/**
 * Created by claudiodamico on 09/03/2017.
 */
@Component
public class SignificantOthersHelper {

    public static final String COMPLETED = "COMPLETED";
    private static String DEFAULT_CHANNEL = "Digital";

    @Getter
    @Setter
    private boolean positiveScenario = true;

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffMembertoCustomerProfileAssociationHelper;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private DependantsHelpers dependantsHelpers;
    @Autowired
    private MembershipDao membershipDao;
    @Autowired
    private FlightHelper flightHelper;

    private SignificantOtherService significantOtherService;
    private SignificantOtherIdDocumentService significantOtherIdDocumentService;
    private CustomerProfileService customerProfileService;
    private FlightsService flightService;
    private AddSignificantOtherRequest addSignificantOtherRequest;
    private UpdateSignificantOtherRequest updateSignificantOtherRequest;
    private UpdateIdentityDocumentRequest updateIdentityDocumentRequest;
    private AddIdentityDocumentRequest addIdentityDocumentRequest;
    private DeleteSignificantOtherRequest deleteSignificantOtherRequest;
    private ProfileRequest profileRequest;
    private String customerId;
    private String passengerId;
    private String customerWithSignificantOtherId;
    private String documentId;
    private String customerSignificantOtherId;
    private String email;
    private int remainingChanges;


    public void createAddRequestWithMissingParameter(String param) throws Throwable {

        customerId = getTestStaffCustomerId();

        SignificantOtherRequestBody requestBody = getValidAddSignificantOtherRequestBody();

        switch (param) {
            case "type":
                requestBody.setType("");
                break;
            case "age":
                requestBody.setAge(null);
                break;
            case "title":
                requestBody.setTitle("");
                break;
            case "firstName":
                requestBody.setFirstName("");
                break;
            case "lastName":
                requestBody.setLastName("");
                break;
        }

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER)
                .build();

        addSignificantOtherRequest = new AddSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);

    }

    public void createUpdateRequestWithMissingParameter(String param) throws Throwable {

        createStaffCustomerWithSignificantOther(null);

        SignificantOtherRequestBody requestBody = getValidUpdateSignificantOtherRequestBody();

        switch (param) {
            case "type":
                requestBody.setType("");
                break;
            case "age":
                requestBody.setAge(null);
                break;
            case "title":
                requestBody.setTitle("");
                break;
            case "firstName":
                requestBody.setFirstName("");
                break;
            case "lastName":
                requestBody.setLastName("");
                break;
        }

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.UPDATE_SIGNIFICANT_OTHER)
                .build();

        updateSignificantOtherRequest = new UpdateSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), customerPathParams, requestBody);

    }

    public void processAddSignificantOthersRequest() throws Throwable {
        significantOtherService = serviceFactory.addSignificantOtherService(addSignificantOtherRequest);
        significantOtherService.invoke();
        if (positiveScenario) {
            passengerId = significantOtherService.getResponse().getUpdateConfirmation().getPassengerId();
            testData.setData(SERVICE, significantOtherIdDocumentService);
        }
    }

    public void processUpdateSignificantOthersRequest() throws Throwable {
        significantOtherService = serviceFactory.updateSignificantOtherService(updateSignificantOtherRequest);
        significantOtherService.invoke();
        testData.setData(SERVICE, significantOtherService);
    }

    public void processUpdateSignificantOthersDocumentRequest() throws Throwable {
        significantOtherIdDocumentService = serviceFactory.updateIdentityDocumentService(updateIdentityDocumentRequest);
        significantOtherIdDocumentService.invoke();
        testData.setData(SERVICE, significantOtherIdDocumentService);
    }

    public void processAddSignificantOthersDocumentRequest() throws Throwable {
        significantOtherIdDocumentService = serviceFactory.addIdentityDocumentService(addIdentityDocumentRequest);
        significantOtherIdDocumentService.invoke();
        testData.setData(SERVICE, significantOtherIdDocumentService);
        try {
            testData.setDocumentId(significantOtherIdDocumentService.getIdentityDocumentResponse().getUpdateConfirmation().getDocumentId());
        } catch (Exception e) {
        }
    }

    public void processGetCustomerProfileRequest() throws Throwable {
        customerProfileService = serviceFactory.getCustomerProfile(profileRequest);
        customerProfileService.invoke();
        testData.setData(SERVICE, significantOtherIdDocumentService);
    }

    public void processDeleteSignificantOthersDocumentRequest() throws Throwable {
        significantOtherIdDocumentService = serviceFactory.deleteIdentityDocumentService(deleteSignificantOtherRequest);
        significantOtherIdDocumentService.invoke();
    }

    public void processDeleteSignificantOthersRequest() throws Throwable {
        significantOtherService = serviceFactory.deleteSignificantOtherService(deleteSignificantOtherRequest);
        significantOtherService.invoke();
        testData.setData(SERVICE, significantOtherIdDocumentService);
    }

    public void createAddRequest() throws Throwable {
        SignificantOtherRequestBody requestBody = getValidAddSignificantOtherRequestBody();

        customerId = getTestStaffCustomerId();

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER)
                .build();

        addSignificantOtherRequest = new AddSignificantOtherRequest(HybrisHeaders.getValid(testData.getChannel()).build(), requestBody, customerPathParams);
    }

    public void createAddRequestNoLogin() throws Throwable {
        SignificantOtherRequestBody requestBody = getValidAddSignificantOtherRequestBody();

        customerWithSignificantOtherId = getTestStaffCustomerIdNoLogin();

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER)
                .build();

        addSignificantOtherRequest = new AddSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);
    }

    public void createAddRequest(String customer) throws EasyjetCompromisedException {
        SignificantOtherRequestBody requestBody = getValidAddSignificantOtherRequestBody();

        customerId = customer;

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER)
                .build();

        addSignificantOtherRequest = new AddSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);
    }

    public void createAddSameCustomerRequest() throws EasyjetCompromisedException {
        SignificantOtherRequestBody requestBody = getValidAddSignificantOtherRequestBody();

        requestBody.setEmail(email);

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER)
                .build();

        addSignificantOtherRequest = new AddSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);
    }

    public void addXSignificantOthersToTheCustomer(int threshold) throws Throwable {

        for (int i = 0; i < threshold; i++) {
            SignificantOtherRequestBody requestBody = getValidAddSignificantOtherRequestBody();

            CustomerPathParams customerPathParams = CustomerPathParams.builder()
                    .customerId(customerId)
                    .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER)
                    .build();

            AddSignificantOtherRequest request = new AddSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);

            SignificantOtherService service = serviceFactory.addSignificantOtherService(request);
            service.invoke();

            if (i == 0) {
                remainingChanges = service.getResponse().getUpdateConfirmation().getRemainingChanges();
            }
        }
    }

    public void addAndDeleteXSignificantOthersToTheCustomer(int threshold) throws Throwable {

        for (int i = 0; i < threshold; i++) {
            SignificantOtherRequestBody requestBody = getValidAddSignificantOtherRequestBody();

            CustomerPathParams customerPathParams = CustomerPathParams.builder()
                    .customerId(customerId)
                    .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER)
                    .build();

            AddSignificantOtherRequest request = new AddSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);

            SignificantOtherService service = serviceFactory.addSignificantOtherService(request);
            service.invoke();

            if (i == 0) {
                remainingChanges = service.getResponse().getUpdateConfirmation().getRemainingChanges();
            }

            customerPathParams = CustomerPathParams.builder()
                    .customerId(customerId)
                    .passengerId(service.getResponse().getUpdateConfirmation().getPassengerId())
                    .path(CustomerPathParams.CustomerPaths.UPDATE_SIGNIFICANT_OTHER)
                    .build();

            DeleteSignificantOtherRequest delRequest = new DeleteSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), null, customerPathParams);

            service = serviceFactory.deleteSignificantOtherService(delRequest);
            service.invoke();
        }
    }

    public void createCompleteAddRequest() throws Throwable {
        SignificantOtherRequestBody requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();

        String email = getRandomEmail(10);
        getTestCustomerId(email);

        requestBody.setEmail(email);
        requestBody.setPhoneNumber("3453453456");
        requestBody.setNifNumber("123456789811");
        requestBody.setIdentityDocuments(Arrays.asList(getValidIdentityDocumentRequestBody()));
    }

    public void createUpdateRequest() throws Throwable {
        SignificantOtherRequestBody requestBody = getValidUpdateSignificantOtherRequestBody();
        createStaffCustomerWithSignificantOther(null);

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.UPDATE_SIGNIFICANT_OTHER)
                .build();

        updateSignificantOtherRequest = new UpdateSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), customerPathParams, requestBody);
    }

    public void createSSRsUpdateRequest() throws Throwable {
        SignificantOtherSavedSSRsRequestBody requestBody = SignificantOtherSavedSSRsRequestBody.builder()
                .ssrs(Arrays.asList(getValidSSRsRequestBody("BLND")))
                .build();

        createStaffCustomerWithSignificantOther(null);

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.UPDATE_SIGNIFICANT_OTHER_ADD_SSR)
                .build();

        updateSignificantOtherRequest = new UpdateSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), customerPathParams, requestBody);
    }

    public void changeFieldLength(String field, int length, SignificantOthersServiceType type) {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (type == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (type == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }

        switch (field) {
            case "firstName":
                requestBody.setFirstName(StringUtils.repeat("a", length));
                break;
            case "lastName":
                requestBody.setLastName(StringUtils.repeat("a", length));
                break;
            case "phoneNumber":
                requestBody.setPhoneNumber(StringUtils.repeat("1", length));
                break;
            case "nifNumber":
                requestBody.setNifNumber(StringUtils.repeat("r", length));
                break;
            case "ejPlusCardNumber":
                requestBody.setEjPlusCardNumber(StringUtils.repeat("c", length));
                break;
            case "documentNumber":
                requestBody.getIdentityDocuments().get(0).setDocumentNumber(StringUtils.repeat("c", length));
                break;
        }

    }

    public void changeIdentityDocumentFieldLength(String field, int length, SignificantOthersServiceType type) {
        IdentityDocumentRequestBody requestBody = IdentityDocumentRequestBody.builder().build();
        if (type == SignificantOthersServiceType.ADD) {
            requestBody = (IdentityDocumentRequestBody) addIdentityDocumentRequest.getRequestBody();
        } else if (type == SignificantOthersServiceType.UPDATE) {
            requestBody = (IdentityDocumentRequestBody) updateIdentityDocumentRequest.getRequestBody();
        }

        switch (field) {
            case "documentNumber":
                requestBody.setDocumentNumber(StringUtils.repeat("c", length));
                break;
        }

    }

    public void changeFieldValueWithInvalidChars(String field, String invalidChar, SignificantOthersServiceType type) {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (type == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (type == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }
        String nameSample = "abcdfe";
        String phoneNumberSample = "23452135";

        switch (field) {
            case "firstName":
                requestBody.setFirstName(nameSample + invalidChar);
                break;
            case "lastName":
                requestBody.setLastName(nameSample + invalidChar);
                break;
            case "phoneNumber":
                requestBody.setPhoneNumber(phoneNumberSample + invalidChar);
                break;
            case "documentNumber":
                requestBody.getIdentityDocuments().get(0).setDocumentNumber(nameSample + invalidChar);
                break;
        }
    }

    public void changeFieldValueWithSpecialChars(String field, String invalidChar, SignificantOthersServiceType type) {
        IdentityDocumentRequestBody requestBody = IdentityDocumentRequestBody.builder().build();
        if (type == SignificantOthersServiceType.ADD) {
            requestBody = (IdentityDocumentRequestBody) addIdentityDocumentRequest.getRequestBody();
        } else if (type == SignificantOthersServiceType.UPDATE) {
            requestBody = (IdentityDocumentRequestBody) updateIdentityDocumentRequest.getRequestBody();
        }

        String nameSample = "abcdfe";

        switch (field) {
            case "documentNumber":
                requestBody.setDocumentNumber(nameSample + invalidChar);
                break;
        }
    }

    public void createDeleteSignificantOthertRequest() {

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.UPDATE_SIGNIFICANT_OTHER)
                .build();

        deleteSignificantOtherRequest = new DeleteSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), null, customerPathParams);
    }

    public void createDeleteDocumentRequest() {

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .documentId(documentId)
                .path(CustomerPathParams.CustomerPaths.UPDATE_SIGNIFICANT_OTHER_ID_DOCUMENT)
                .build();

        deleteSignificantOtherRequest = new DeleteSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), null, customerPathParams);
    }

    public void createDeleteDependantDocumentRequest() {

        testData.setData(CUSTOMER_ID, dependantsHelpers.getCustomerWithDependantId());

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .passengerId(dependantsHelpers.getPassengerId())
                .documentId(testData.getDocumentId())
                .path(CustomerPathParams.CustomerPaths.UPDATE_DEPENDANTS_ID_DOCS)
                .build();

        deleteSignificantOtherRequest = new DeleteSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), null, customerPathParams);
    }

    public void createDeleteAllDocumentRequest() {

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER_ID_DOCUMENT)
                .build();

        deleteSignificantOtherRequest = new DeleteSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), null, customerPathParams);
    }

    public void changeDocumentDateOfBirth(SignificantOthersServiceType type) {
        IdentityDocumentRequestBody requestBody = IdentityDocumentRequestBody.builder().build();
        if (type == SignificantOthersServiceType.ADD) {
            requestBody = (IdentityDocumentRequestBody) addIdentityDocumentRequest.getRequestBody();
        } else if (type == SignificantOthersServiceType.UPDATE) {
            requestBody = (IdentityDocumentRequestBody) updateIdentityDocumentRequest.getRequestBody();
        }

        requestBody.setDateOfBirth("2016-06-09");
    }

    public void createAddRequestWithValidIdentityDocument() {
        SignificantOtherRequestBody requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();

        requestBody.setIdentityDocuments(Arrays.asList(getValidIdentityDocumentRequestBody()));
    }

    public void createRequestWithInvalidAge(String type, int age) {
        SignificantOtherRequestBody requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();

        requestBody.setAge(age);
        requestBody.setType(type);

        if ("infant".equals(type)) {
            requestBody.setTitle("infant");
        }
    }

    public void createUpdateRequestWithInvalidAge(String type, int age) {
        SignificantOtherRequestBody requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();

        requestBody.setAge(age);
        requestBody.setType(type);

        if ("infant".equals(type)) {
            requestBody.setTitle("infant");
        }
    }

    public void createAddRequestWithEjPlusMembership(SignificantOthersServiceType serviceType) {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (serviceType == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (serviceType == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }
        MemberShipModel memberShipModel = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        requestBody.setEjPlusCardNumber(memberShipModel.getEjMemberShipNumber());
    }

    public void createAddRequestWithEjPlusMembershipWithStatus(SignificantOthersServiceType serviceType, String status) throws EasyjetCompromisedException {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (serviceType == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (serviceType == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }
        MemberShipModel staffOtherThanCompleted = membershipDao.getValidEJPlusMembershipForStaffOtherThanStatus(status);
        requestBody.setEjPlusCardNumber(staffOtherThanCompleted.getEjMemberShipNumber());
        requestBody.setLastName(staffOtherThanCompleted.getLastname());
    }

    public void createAddRequestWithEjPlusMembership(SignificantOthersServiceType serviceType, MemberShipModel memberShipModel) {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (serviceType == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (serviceType == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }
        requestBody.setEjPlusCardNumber(memberShipModel.getEjMemberShipNumber());
    }

    public void createAddRequestWithInvalidEjPlusMembership(SignificantOthersServiceType serviceType) {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (serviceType == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (serviceType == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }
        requestBody.setLastName("notValidMembership");
    }

    public void createAddRequestWithInvalidEmail(String invalidMail, SignificantOthersServiceType serviceType) {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (serviceType == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (serviceType == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }
        requestBody.setEmail(invalidMail);
    }

    public void createAddRequestWithValidEmail(SignificantOthersServiceType serviceType) throws Throwable {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (serviceType == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (serviceType == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }
        email = getRandomEmail(10);
        customerSignificantOtherId = getTestCustomerId(email);

        requestBody.setEmail(email);
    }

    public void createAddRequestWithNotLinkedEmail(SignificantOthersServiceType serviceType) {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (serviceType == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (serviceType == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }
        requestBody.setEmail(getRandomEmail(10));
    }

    public void createAddRequestWithInvalidNumberOfSavedSSRs(int threshold) {
        SignificantOtherRequestBody requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        requestBody.setSavedSSRs(getXSavedSSRsRequestBody(threshold));
    }

    public void createUpdateRequestWithInvalidNumberOfSavedSSRs(int threshold) {
        SignificantOtherSavedSSRsRequestBody requestBody = (SignificantOtherSavedSSRsRequestBody) updateSignificantOtherRequest.getRequestBody();
        requestBody.setSsrs(getXSavedSSRsRequestBody(threshold).getSsrs());

    }

    public void createAddRequestWithAlreadyLinkedEmail(String email, SignificantOthersServiceType serviceType) throws EasyjetCompromisedException {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder().build();
        if (serviceType == SignificantOthersServiceType.ADD) {
            requestBody = (SignificantOtherRequestBody) addSignificantOtherRequest.getRequestBody();
        } else if (serviceType == SignificantOthersServiceType.UPDATE) {
            requestBody = (SignificantOtherRequestBody) updateSignificantOtherRequest.getRequestBody();
        }
        requestBody.setEmail(email);
    }

    public void createAddIdentityDocumentRequest() throws EasyjetCompromisedException {
        IdentityDocumentRequestBody requestBody = getValidIdentityDocumentRequestBody();
        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER_ID_DOCUMENT)
                .build();
        addIdentityDocumentRequest = new AddIdentityDocumentRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);
    }

    public void createUpdateIdentityDocumentRequest() throws EasyjetCompromisedException {
        IdentityDocumentRequestBody requestBody = getValidIdentityDocumentRequestBody();
        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .documentId(documentId)
                .path(CustomerPathParams.CustomerPaths.UPDATE_SIGNIFICANT_OTHER_ID_DOCUMENT)
                .build();
        updateIdentityDocumentRequest = new UpdateIdentityDocumentRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);
    }

    private SignificantOtherRequestBody getValidAddSignificantOtherRequestBody() {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder()
                .type("adult")
                .title("mr")
                .firstName("John")
                .lastName("Smith")
                .age(34)
                .build();

        return requestBody;
    }

    private SignificantOtherRequestBody getValidUpdateSignificantOtherRequestBody() {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder()
                .type("adult")
                .title("mr")
                .firstName("John")
                .lastName("Dorian")
                .age(34)
                .build();

        return requestBody;
    }

    private SignificantOtherRequestBody getValidUpdateDeleteEmailSignificantOtherRequestBody() {
        SignificantOtherRequestBody requestBody = SignificantOtherRequestBody.builder()
                .type("adult")
                .title("mr")
                .firstName("John")
                .lastName("Smith")
                .age(34)
                .email("")
                .build();

        return requestBody;
    }

    private String getTestStaffCustomerId() throws Throwable {
        String customerId = getTestCustomerId();
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromId(customerId, false);
        return customerId;
    }

    private String getTestStaffCustomerIdNoLogin() throws Throwable {
        String customerId = getTestCustomerId();
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromId(customerId, false);
        return customerId;
    }

    private String getTestCustomerId() throws Throwable {
        return getTestCustomerId(null);
    }

    private String getTestCustomerId(String email) throws Throwable {
        if (StringUtils.isNotEmpty(email)) {
            customerHelper.createNewCustomerProfileWithEmail(email);
        } else {
            customerHelper.createNewCustomerProfileWithEmail(getRandomEmail(10));
        }
        return customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
    }

    public void loginWithLastRequest() throws Throwable {
        customerHelper.loginWithValidCredentials(testData.getChannel());
    }

    public String createAlreadyLinkedStaffCustomerEmail() throws Throwable {
        String email = getRandomEmail(10);
        createStaffCustomerWithSignificantOther(email);
        return email;
    }

    public void createStaffCustomerWithSignificantOther(String email) throws Throwable {
        getTestCustomerId(email);
        customerWithSignificantOtherId = getTestStaffCustomerId();

        SignificantOtherRequestBody prevRequestBody = getValidAddSignificantOtherRequestBody();
        if (StringUtils.isNotEmpty(email)) {
            prevRequestBody.setEmail(email);
        }

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER)
                .build();

        AddSignificantOtherRequest request = new AddSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), prevRequestBody, customerPathParams);

        SignificantOtherService service = serviceFactory.addSignificantOtherService(request);
        service.invoke();
        passengerId = service.getResponse().getUpdateConfirmation().getPassengerId();
    }

    public void createStaffCustomerWithSignificantOtherWithEmail() throws Throwable {
        email = getRandomEmail(10);

        createStaffCustomerWithSignificantOther(email);
    }

    public void createStaffCustomerWithCompleteSignificantOther() throws Throwable {
        customerWithSignificantOtherId = getTestStaffCustomerId();

        SignificantOtherRequestBody prevRequestBody = getValidAddSignificantOtherRequestBody();

        testData.setFirstCustomerEmail(testData.getEmail());
        testData.setFirstCustomerPassword(testData.getPassword());

        logout(customerWithSignificantOtherId);

        email = getRandomEmail(10);
        String secondCustomerId = getTestCustomerId(email);

        logout(secondCustomerId);

        customerHelper.loginWithValidCredentials(testData.getChannel(), testData.getFirstCustomerEmail(), testData.getFirstCustomerPassword(), false);

        String ssrCode = "BLND";

        SignificantOtherSavedSSRsRequestBody savedSSRsRequestBody = SignificantOtherSavedSSRsRequestBody.builder()
                .ssrs(Arrays.asList(getValidSSRsRequestBody(ssrCode)))
                .build();

        prevRequestBody.setEmail(email);
        prevRequestBody.setPhoneNumber("3453453456");
        prevRequestBody.setNifNumber("123456789811");
        prevRequestBody.setIdentityDocuments(Arrays.asList(getValidIdentityDocumentRequestBody()));

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER)
                .build();

        AddSignificantOtherRequest request = new AddSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), prevRequestBody, customerPathParams);

        SignificantOtherService service = serviceFactory.addSignificantOtherService(request);
        service.invoke();
        passengerId = service.getResponse().getUpdateConfirmation().getPassengerId();
    }

    private void logout(String cID) {
        CustomerPathParams params = CustomerPathParams.builder().customerId(cID).path(LOGOUT).build();
        CustomerLogoutService customerLogoutService = serviceFactory.logoutCustomer(new LogoutRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params));
        customerLogoutService.invoke();
    }

    public void createSignificantOthersWithIdentityDocuments() throws Throwable {
        createStaffCustomerWithSignificantOther(null);

        IdentityDocumentRequestBody requestBody = getValidIdentityDocumentRequestBody();

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.ADD_SIGNIFICANT_OTHER_ID_DOCUMENT)
                .build();
        AddIdentityDocumentRequest request = new AddIdentityDocumentRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);

        SignificantOtherIdDocumentService service = serviceFactory.addIdentityDocumentService(request);
        service.invoke();
        documentId = service.getResponse().getUpdateConfirmation().getDocumentId();
        passengerId = service.getResponse().getUpdateConfirmation().getPassengerId();
    }

    public void createGetCustomerProfileRequest() {
        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .path(CustomerPathParams.CustomerPaths.PROFILE)
                .build();

        profileRequest = new ProfileRequest(HybrisHeaders.getValidWithToken(DEFAULT_CHANNEL, testData.getAccessToken()).build(), customerPathParams, CustomerProfileQueryParams.builder().sections("significant-others").build());
        testData.setData(SerenityFacade.DataKeys.CUSTOMER_ID,customerWithSignificantOtherId);
    }

    public void createGetCustomerProfileRequest(String customerId) {
        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerId)
                .path(CustomerPathParams.CustomerPaths.PROFILE)
                .build();

        profileRequest = new ProfileRequest(HybrisHeaders.getValidWithToken(DEFAULT_CHANNEL, testData.getAccessToken()).build(), customerPathParams, CustomerProfileQueryParams.builder().sections("significant-others").build());
    }

    public SignificantOtherService getSignificantOtherService() {
        return significantOtherService;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getCustomerWithSignificantOtherId() {
        return customerWithSignificantOtherId;
    }

    public void setCustomerWithSignificantOtherId(String customerWithSignificantOtherId) {
        this.customerWithSignificantOtherId = customerWithSignificantOtherId;
    }

    private IdentityDocumentRequestBody getValidIdentityDocumentRequestBody() {
        IdentityDocumentRequestBody.Name requestName = IdentityDocumentRequestBody.Name.builder()
                .firstName("Jim")
                .middleName("Douglas")
                .lastName("Morrison")
                .fullName("Jim Morrison")
                .title("mr")
                .build();

        IdentityDocumentRequestBody requestBody = IdentityDocumentRequestBody.builder()
                .name(requestName)
                .dateOfBirth("1983-01-09")
                .documentExpiryDate("2018-06-06")
                .gender("MALE")
                .nationality("GBR")
                .countryOfIssue("GBR")
                .documentType("PASSPORT")
                .documentNumber("7487714")
                .build();

        return requestBody;
    }

    private SignificantOtherSavedSSRsRequestBody getXSavedSSRsRequestBody(int threshold) {
        List<SignificantOtherSSRsRequestBody> ssrsList = new ArrayList<>();

        for (int i = 0; i < threshold; i++) {
            ssrsList.add(getValidSSRsRequestBody(null));
        }

        SignificantOtherSavedSSRsRequestBody requestBody = SignificantOtherSavedSSRsRequestBody.builder()
                .ssrs(ssrsList)
                .build();

        return requestBody;
    }

    private SignificantOtherSSRsRequestBody getValidSSRsRequestBody(String code) {
        String ssrCode = StringUtils.isNotEmpty(code) ? code : RandomStringUtils.randomAlphabetic(4).toUpperCase();

        SignificantOtherSSRsRequestBody requestBody = SignificantOtherSSRsRequestBody.builder()
                .code(ssrCode)
                .isTandCsAccepted(true)
                .build();

        return requestBody;
    }

    private SignificantOtherSSRsRequestBody getValidSSRsRequestBody(String code, Boolean accept) {
        String ssrCode = StringUtils.isNotEmpty(code) ? code : RandomStringUtils.randomAlphabetic(4).toUpperCase();
        SignificantOtherSSRsRequestBody requestBody;

        if (accept != null) {
            requestBody = SignificantOtherSSRsRequestBody.builder()
                    .code(ssrCode)
                    .isTandCsAccepted(accept)
                    .build();
        } else {
            requestBody = SignificantOtherSSRsRequestBody.builder()
                    .code(ssrCode)
                    .build();
        }


        return requestBody;
    }

    public SignificantOtherIdDocumentService getSignificantOtherIdDocumentService() {
        return significantOtherIdDocumentService;
    }

    public FlightsService getFlightService() {
        return flightService;
    }

    public void setFlightService(FlightsService flightService) {
        this.flightService = flightService;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public CustomerProfileService getCustomerProfileService() {
        return customerProfileService;
    }

    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    public void setServiceFactory(HybrisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public void setCustomerHelper(CustomerHelper customerHelper) {
        this.customerHelper = customerHelper;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRemainingChanges() {
        return remainingChanges;
    }

    public void createSSRsUpdateRequestForDependant(Boolean accept) throws Throwable {
        SignificantOtherSavedSSRsRequestBody requestBody = SignificantOtherSavedSSRsRequestBody.builder()
                .ssrs(Arrays.asList(getValidSSRsRequestBody("WCHC", accept)))
                .build();

        createStaffCustomerWithSignificantOther(null);

        testData.setData(CUSTOMER_ID, dependantsHelpers.getCustomerWithDependantId());
        testData.setPassengerId(dependantsHelpers.getPassengerId());

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .passengerId(testData.getPassengerId())
                .path(CustomerPathParams.CustomerPaths.UPDATE_DEPENDANTS_SSR)
                .build();

        updateSignificantOtherRequest = new UpdateSignificantOtherRequest(HybrisHeaders.getValidWithToken(DEFAULT_CHANNEL, testData.getAccessToken()).build(), customerPathParams, requestBody);
    }

    public void createSSRsDeleteRequestForDependant(Boolean accept) throws Throwable {
        SignificantOtherSavedSSRsRequestBody requestBody = SignificantOtherSavedSSRsRequestBody.builder()
                .ssrs(null)
                .build();

        createStaffCustomerWithSignificantOther(null);

        testData.setData(CUSTOMER_ID, dependantsHelpers.getCustomerWithDependantId());
        testData.setPassengerId(dependantsHelpers.getPassengerId());

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .passengerId(testData.getPassengerId())
                .path(CustomerPathParams.CustomerPaths.UPDATE_DEPENDANTS_SSR)
                .build();

        updateSignificantOtherRequest = new UpdateSignificantOtherRequest(HybrisHeaders.getValidWithToken(DEFAULT_CHANNEL, testData.getAccessToken()).build(), customerPathParams, requestBody);
    }

    public void createAddIdentityDocumentRequestForDependant() throws EasyjetCompromisedException {
        IdentityDocumentRequestBody requestBody = getValidIdentityDocumentRequestBody();
        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(dependantsHelpers.getCustomerWithDependantId())
                .passengerId(dependantsHelpers.getPassengerId())
                .path(CustomerPathParams.CustomerPaths.ADD_DEPENDANTS_ID_DOCS)
                .build();
        addIdentityDocumentRequest = new AddIdentityDocumentRequest(HybrisHeaders.getValid(testData.getChannel()).build(), requestBody, customerPathParams);
        testData.setDocumentId(requestBody.getDocumentNumber());
    }

    public void createUpdateRequestWithAllSSrsHavingFalseTsCsFlag() {
        SignificantOtherSavedSSRsRequestBody requestBody = (SignificantOtherSavedSSRsRequestBody) updateSignificantOtherRequest.getRequestBody();
        requestBody.setSsrs(getXSavedSSRsRequestBody(5).getSsrs());
        List<SignificantOtherSSRsRequestBody> ssrs = requestBody.getSsrs();
        for (SignificantOtherSSRsRequestBody ssr : ssrs) {
            ssr.setIsTandCsAccepted(false);
        }
    }

    public void createUpdateIdentityDocumentRequestForDependant() throws EasyjetCompromisedException {
        IdentityDocumentRequestBody requestBody = getValidIdentityDocumentRequestBody();
        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(dependantsHelpers.getCustomerWithDependantId())
                .passengerId(dependantsHelpers.getPassengerId())
                .documentId(testData.getDocumentId())
                .path(CustomerPathParams.CustomerPaths.UPDATE_DEPENDANTS_ID_DOCS)
                .build();
        updateIdentityDocumentRequest = new UpdateIdentityDocumentRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), requestBody, customerPathParams);
    }

    public void createUpdateRequestToRemoveEmail() throws Throwable {

        SignificantOtherRequestBody requestBody = getValidUpdateDeleteEmailSignificantOtherRequestBody();

        CustomerPathParams customerPathParams = CustomerPathParams.builder()
                .customerId(customerWithSignificantOtherId)
                .passengerId(passengerId)
                .path(CustomerPathParams.CustomerPaths.UPDATE_SIGNIFICANT_OTHER)
                .build();

        updateSignificantOtherRequest = new UpdateSignificantOtherRequest(HybrisHeaders.getValid(DEFAULT_CHANNEL).build(), customerPathParams, requestBody);

    }

    public void findAFlightRequest(String bundle) throws Throwable {
        flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), bundle, testData.getCurrency());
    }

    public boolean getSignificantOtherUpdateConfirmation() {
        return significantOtherService.getSignificantOtherResponse().getUpdateConfirmation().getCustomerId().equals(testData.getData(CUSTOMER_ID));
    }

    public boolean getSignificantOtherDocumentUpdateConfirmation() {
        return significantOtherIdDocumentService.getIdentityDocumentResponse().getUpdateConfirmation().getCustomerId().equals(testData.getData(CUSTOMER_ID));
    }

    public String getCustomerSignificantOtherId() {
        return customerSignificantOtherId;
    }

    public enum SignificantOthersServiceType {
        ADD,
        UPDATE
    }

}
