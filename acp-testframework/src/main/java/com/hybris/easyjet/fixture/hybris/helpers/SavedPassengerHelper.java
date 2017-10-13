package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.dao.SSRDataDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateIdentityDocumentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateSSRRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateSavedPassengerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.SavedPassengerFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.savedpassenger.SavedPassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger.operationconfirmation.UpdateIdentityDocumentResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.savedpassenger.operationconfirmation.UpdateSavedPassengerResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.*;

/**
 * Created by giuseppecioce on 21/02/2017.
 */
@Component
@ContextConfiguration(classes = TestApplication.class)

public class SavedPassengerHelper {

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private SSRDataDao ssrDataDao;
    @Autowired
    private MembershipDao membershipDao;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    SerenityFacade testData;

    public static final String COMPLETED = "COMPLETED";
    private final HybrisServiceFactory serviceFactory;
    private SavedPassengerFactory savedPassengerFactory;
    private HybrisService service;
    private GetSavedPassengerService getSavedPassengerService;
    private UpdateSavedPassengerService updateSavedPassengerService;
    private UpdateIdentityDocumentService updateIdentityDocumentService;
    private AddUpdateSavedPassengerRequestBody addUpdateSavedPassengerRequest;
    private AddUpdateIdentityDocumentRequestBody addUpdateIdentityDocumentRequest;
    private AddUpdateSSRRequestBody addUpdateSSRRequest;
    private String customerId;
    private String passengerId;
    private String documentId;

    @Autowired
    public SavedPassengerHelper(SavedPassengerFactory savedPassengerFactory, HybrisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.savedPassengerFactory = savedPassengerFactory;
    }


    public void aInvalidRequestWithMissingFieldForSavedPassenger(String field) {
        addUpdateSavedPassengerRequest = SavedPassengerFactory.missingFieldProfileSavedPassenger(field);
    }

    public void addValidPassengerToExistingCustomer() throws Throwable {
        customerHelper.createRandomCustomer(testData.getChannel());
        customerId = testData.getData(CUSTOMER_ID);
        addUpdateSavedPassengerRequest = SavedPassengerFactory.aBasicProfileSavedPassenger();
        if(testData.keyExist(REQUIRED_EJPLUS) && testData.keyExist(REQUIRED_EJPLUS)) {
            MemberShipModel memberShipModels = membershipDao.getRandomValueForValidEJPlus();
            testData.setData(MEMBERSHIP_MODEL, memberShipModels);
            addUpdateSavedPassengerRequest.setLastName(memberShipModels.getLastname());
        }
        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(GET_SAVED_PASSENGER).build();
        updateSavedPassengerService = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, addUpdateSavedPassengerRequest));
        updateSavedPassengerService.invoke();
        service = updateSavedPassengerService;
        UpdateSavedPassengerResponse updateConfirmation = updateSavedPassengerService.getResponse();
        passengerId = updateConfirmation.getUpdateConfirmation().getPassengerId();
        testData.setData(SAVED_PASSENGER_CODE, passengerId);
    }

    public void addPassengerToExistingCustomer() throws Throwable {
        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(GET_SAVED_PASSENGER).build();
        updateSavedPassengerService = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, addUpdateSavedPassengerRequest));
        updateSavedPassengerService.invoke();
        service = updateSavedPassengerService;
    }

    public void addValidPassengerToExistingCustomerWithPassword(String customerId) throws Throwable {
        addUpdateSavedPassengerRequest = SavedPassengerFactory.aBasicProfileSavedPassenger();

        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(GET_SAVED_PASSENGER).build();
        updateSavedPassengerService = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid("Digital").build(), params, addUpdateSavedPassengerRequest));
        updateSavedPassengerService.invoke();
        service = updateSavedPassengerService;

        UpdateSavedPassengerResponse updateConfirmation = updateSavedPassengerService.getResponse();
        passengerId = updateConfirmation.getUpdateConfirmation().getPassengerId();
    }

    public void addValidPassengerToExistingCustomer(String idCustomer) throws Throwable {
        customerId = idCustomer;

        addUpdateSavedPassengerRequest = SavedPassengerFactory.aBasicProfileSavedPassenger();

        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(GET_SAVED_PASSENGER).build();
        updateSavedPassengerService = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, addUpdateSavedPassengerRequest));
        updateSavedPassengerService.invoke();
        service = updateSavedPassengerService;

        UpdateSavedPassengerResponse updateConfirmation = updateSavedPassengerService.getResponse();
        passengerId = updateConfirmation.getUpdateConfirmation().getPassengerId();
    }

    public void addValidIdentityDocumentToToExistingPassenger() throws Throwable {
        addUpdateIdentityDocumentRequest = SavedPassengerFactory.aBasicProfileIdentitytDocument();

        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).passengerId(passengerId).path(ADD_IDENTITY_DOCUMENT).build();
        updateIdentityDocumentService = serviceFactory.updateIdentityDocument(new SavedPassengerRequest(HybrisHeaders.getValid("Digital").build(), params, addUpdateIdentityDocumentRequest));
        updateIdentityDocumentService.invoke();
        service = updateIdentityDocumentService;

        UpdateIdentityDocumentResponse identityDocumentResponse = updateIdentityDocumentService.getResponse();
        documentId = identityDocumentResponse.getUpdateConfirmation().getDocumentId();
    }

    public void addValidIdentityDocumentToToExistingPassenger(int n) throws Throwable {
        for (int i = 0; i < n; i++) {
            addUpdateIdentityDocumentRequest = SavedPassengerFactory.aBasicProfileIdentitytDocument();

            CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).passengerId(passengerId).path(ADD_IDENTITY_DOCUMENT).build();
            updateIdentityDocumentService = serviceFactory.updateIdentityDocument(new SavedPassengerRequest(HybrisHeaders.getValid("Digital").build(), params, addUpdateIdentityDocumentRequest));
            updateIdentityDocumentService.invoke();
            service = updateIdentityDocumentService;

            UpdateIdentityDocumentResponse identityDocumentResponse = updateIdentityDocumentService.getResponse();
            documentId = identityDocumentResponse.getUpdateConfirmation().getDocumentId();
        }
    }

    public String addValidIdentityDocumentToToExistingPassengerViaChannel(String channel, String dateOfBirth, String documentExpiryDate, String documentNumber,
                                                                          String documentType, String gender, String nationality, String countryOfIssue, String fullName) throws Throwable {
        addUpdateIdentityDocumentRequest = savedPassengerFactory.aBasicProfileIdentityDocument(dateOfBirth, documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, fullName);

        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).passengerId(passengerId).path(ADD_IDENTITY_DOCUMENT).build();
        updateIdentityDocumentService = serviceFactory.updateIdentityDocument(new SavedPassengerRequest(HybrisHeaders.getValid(channel).build(), params, addUpdateIdentityDocumentRequest));
        updateIdentityDocumentService.invoke();
        service = updateIdentityDocumentService;

        UpdateIdentityDocumentResponse identityDocumentResponse = updateIdentityDocumentService.getResponse();
        return identityDocumentResponse.getUpdateConfirmation().getDocumentId();
    }

    public String addValidIdentityDocumentToToExistingPassenger(String dateOfBirth, String documentExpiryDate, String documentNumber,
                                                                String documentType, String gender, String nationality, String countryOfIssue, String fullName) throws Throwable {
        addUpdateIdentityDocumentRequest = savedPassengerFactory.aBasicProfileIdentityDocument(dateOfBirth, documentExpiryDate, documentNumber, documentType, gender, nationality, countryOfIssue, fullName);

        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).passengerId(passengerId).path(ADD_IDENTITY_DOCUMENT).build();
        updateIdentityDocumentService = serviceFactory.updateIdentityDocument(new SavedPassengerRequest(HybrisHeaders.getValid("Digital").build(), params, addUpdateIdentityDocumentRequest));
        updateIdentityDocumentService.invoke();
        service = updateIdentityDocumentService;

        UpdateIdentityDocumentResponse identityDocumentResponse = updateIdentityDocumentService.getResponse();
        return identityDocumentResponse.getUpdateConfirmation().getDocumentId();
    }

    public void aValidRequestToCreateASavedPassenger() {
        addUpdateSavedPassengerRequest = SavedPassengerFactory.aBasicProfileSavedPassenger();
    }

    private String getValidMemebership() {
        return membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED).getEjMemberShipNumber();
    }

    public void aValidRequestToCreateACompleteSavedPassenger() {
        MemberShipModel memberShipModel = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        addUpdateSavedPassengerRequest = SavedPassengerFactory.aCompleteProfileSavedPassenger(memberShipModel.getEjMemberShipNumber(), memberShipModel.getLastname());
    }

    public void aValidRequestToCreateAIdentityDocument() {
        addUpdateIdentityDocumentRequest = SavedPassengerFactory.aBasicProfileIdentitytDocument();
    }

    public void aValidRequestToCreateASSR() {
        List<String> SSRCode = ssrDataDao.getSSRDataActive(true, 2);
        addUpdateSSRRequest = SavedPassengerFactory.addProfileSSR(SSRCode);
    }

    public HybrisService getSavedPassengerSevice() {
        return service;
    }

    public void savedPassengerRequestWithFieldAndFieldLength(String field, int length) {

        DataFactory df = new DataFactory();
        String stringDataToUse = df.getRandomText(100);
        String numberDataToUse = df.getNumberText(100);

        switch (field) {
            case "fPhonenumber":
                addUpdateSavedPassengerRequest.setPhoneNumber(numberDataToUse.substring(0, length));
                break;
            case "fFirstname":
                addUpdateSavedPassengerRequest.setFirstName(stringDataToUse.substring(0, length));
                break;
            case "fLastname":
                addUpdateSavedPassengerRequest.setLastName(stringDataToUse.substring(0, length));
                break;
            case "fNIF":
                addUpdateSavedPassengerRequest.setNifNumber(numberDataToUse.substring(0, length));
                break;
            case "fDocumentnumber":
                addUpdateIdentityDocumentRequest.setDocumentNumber(numberDataToUse.substring(0, length));
                break;
            case "fMembership":
                addUpdateSavedPassengerRequest.setEjPlusCardNumber(numberDataToUse.substring(0, length));
                break;
        }
    }

    public void setSavedPassengerProfileFieldWithSymbol(String field, String symbol) {
        DataFactory df = new DataFactory();

        char symbolToReplace = symbol.charAt(0);

        switch (field) {
            case "fFirstname":
                StringBuilder firstName = new StringBuilder(df.getFirstName());
                firstName.setCharAt(2, symbolToReplace);
                addUpdateSavedPassengerRequest.setFirstName(firstName.toString());
                break;
            case "fLastname":
                StringBuilder lastName = new StringBuilder(df.getLastName());
                lastName.setCharAt(2, symbolToReplace);
                addUpdateSavedPassengerRequest.setLastName(lastName.toString());
                break;
            case "fDocumentNumber":
                StringBuilder document = new StringBuilder(df.getRandomChars(15));
                document.setCharAt(2, symbolToReplace);
                addUpdateIdentityDocumentRequest.setDocumentNumber(document.toString());
                break;
            case "fPhonenumber":
                StringBuilder phonenumber = new StringBuilder(df.getNumberText(18));
                phonenumber.setCharAt(2, symbolToReplace);
                addUpdateSavedPassengerRequest.setPhoneNumber(phonenumber.toString());
                break;
        }
    }

    public void updateTypeAndAgeSavedPassenger(String type, int age) {
        addUpdateSavedPassengerRequest.setType(type);
        addUpdateSavedPassengerRequest.setAge(age);
        if (type.equalsIgnoreCase("infant"))
            addUpdateSavedPassengerRequest.setTitle("infant");
    }

    public void updateEmailSavedPassenger(String email) {
        addUpdateSavedPassengerRequest.setEmail(email);
    }

    public void addSavedPassengerFromRequest() {
        AddUpdateSavedPassengerRequestBody request = addUpdateSavedPassengerRequest;
        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).passengerId(passengerId).path(MANAGE_SAVED_PASSENGER).build();
        updateSavedPassengerService = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, request, "Update"));
        updateSavedPassengerService.invoke();
        service = updateSavedPassengerService;
    }

    public void addIdentityDocumentFromRequest() {
        AddUpdateIdentityDocumentRequestBody request = addUpdateIdentityDocumentRequest;
        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).passengerId(passengerId).documentId(documentId).path(UPDATE_IDENTITY_DOCUMENT).build();
        updateSavedPassengerService = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid("Digital").build(), params, request, "Update"));
        updateSavedPassengerService.invoke();
        service = updateSavedPassengerService;
    }

    public void addMoreSSRToTheRequest(int number) {
        List<String> SSRCode = ssrDataDao.getSSRDataActive(true, number);
        addUpdateSSRRequest = SavedPassengerFactory.addProfileSSR(SSRCode);
    }

    public void addSSRsFromRequest() {
        AddUpdateSSRRequestBody request = addUpdateSSRRequest;
        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).passengerId(passengerId).documentId(documentId).passengerId(passengerId).path(ADD_SSR).build();
        updateSavedPassengerService = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid("Digital").build(), params, request, "Update"));
        updateSavedPassengerService.invoke();
        service = updateSavedPassengerService;
    }

    public void replaceFirstChar(String field, String character) {
        if (field.equals("fMembership")) {
            String ejPlusCardNumber = character + addUpdateSavedPassengerRequest.getEjPlusCardNumber();
            addUpdateSavedPassengerRequest.setEjPlusCardNumber(ejPlusCardNumber);

        }
    }

    public void addValidEJPlusMembership() {
        String ejPlusMembership = getValidMemebership();
        addUpdateSavedPassengerRequest.setEjPlusCardNumber(ejPlusMembership);
    }

    public void updateDateOfBirth() {
        addUpdateIdentityDocumentRequest.setDateOfBirth(SavedPassengerFactory.getFutureDateFromNow());
    }

    public void prepareRemovePassengerFromCustomer() {
        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).passengerId(passengerId).path(MANAGE_SAVED_PASSENGER).build();
        getSavedPassengerService = serviceFactory.getSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid("Digital").build(), params));
    }

    public void removePassengerFromCustomer() {
        getSavedPassengerService.invoke();
        service = getSavedPassengerService;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void aValidRequestToCreateASSRForTsAndCsMandatory(boolean ssrsThatCsTsMandatory) {
        List<String> ssrCodes;
        if (ssrsThatCsTsMandatory) ssrCodes = ssrDataDao.getSSRsForTermsAndConditionsMandatoryToBeAccepted(true, 2);
        else ssrCodes = ssrDataDao.getSSRsForTermsAndConditionsNotMandatoryToBeAccepted(true, 2);
        addUpdateSavedPassengerRequest = SavedPassengerFactory.aBasicSSRProfileSavedPassenger(ssrCodes);
    }

    public void aValidRequestToCreateASSRForTsAndCsWithoutTsAndCsParameter() {
        List<String> ssrCodes = ssrDataDao.getSSRsForTermsAndConditionsMandatoryToBeAccepted(true, 2);
        addUpdateSavedPassengerRequest = savedPassengerFactory.aBasicSSRProfileSavedPassengerWithoutTsCs(ssrCodes);
    }

    public List<CustomerModel> getAllCustomers() {
        return customerDao.getAllCustomers();
    }

    public void updateEjPlusNumberWitthStatus(String status) throws EasyjetCompromisedException {
        MemberShipModel ejPlusMemberOtherThanStatus = membershipDao.getEJPlusMemberOtherThanStatus(status);
        addUpdateSavedPassengerRequest.setEjPlusCardNumber(ejPlusMemberOtherThanStatus.getEjMemberShipNumber());
        addUpdateSavedPassengerRequest.setLastName(ejPlusMemberOtherThanStatus.getLastname());
    }

    public void getAllSavedPassenger(String surname) {
        CustomerPathParams params = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(PROFILE)
                .build();

        CustomerProfileService customerProfileService = serviceFactory.getCustomerProfile(
                new ProfileRequest(HybrisHeaders.getValid(testData.getChannel()).build(),
                        params
                )
        );

        customerProfileService.invoke();
        customerProfileService.assertThat().savePassengerToCustomerProfile(surname);
    }

    public void addCompleteValidPassengerToExistingCustomer(String idCustomer) throws Throwable {
        customerId = idCustomer;
        testData.setData("ejPlusCardNumber", addUpdateSavedPassengerRequest.getEjPlusCardNumber());
        addUpdateSavedPassengerRequest.setEjPlusCardNumber("");

        CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(GET_SAVED_PASSENGER).build();
        updateSavedPassengerService = serviceFactory.updateSavedPassenger(new SavedPassengerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, addUpdateSavedPassengerRequest));
        updateSavedPassengerService.invoke();
        service = updateSavedPassengerService;

        UpdateSavedPassengerResponse updateConfirmation = updateSavedPassengerService.getResponse();
        passengerId = updateConfirmation.getUpdateConfirmation().getPassengerId();
        testData.setData("UpdateSavedPassengerRequest", addUpdateSavedPassengerRequest);
        testData.setData("SavedPassengerId", passengerId);
    }

    public UpdateIdentityDocumentService getUpdateIdentityDocumentService() {
        return this.updateIdentityDocumentService;
    }
}