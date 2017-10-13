package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.dao.HRStaffDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.database.hybris.models.HRStaffModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.StaffMemberRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RegisterStaffFaresRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.RegisterStaffFaresService;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.STAFF_PRIVILEGE;

/**
 * Created by Siva on 11/01/2017.
 */

@Component
public class StaffMembertoCustomerProfileAssociationHelper {

    @Autowired
    public SerenityFacade testData;

    private static String customerIdFromDatabase;
    private final HybrisServiceFactory serviceFactory;

    private RegisterStaffFaresService registerStaffFaresService;
    private StaffMemberRequest staffMemberRequest;
    private CustomerDao hybrisCustomersDao;
    private HRStaffDao hybrisHRStaffDao;
    private CustomerHelper customerHelper;

    @Autowired
    public StaffMembertoCustomerProfileAssociationHelper(HybrisServiceFactory serviceFactory, CustomerDao hybrisCustomersDao,
                                                         HRStaffDao hybrisHRStaffDao, CustomerHelper customerHelper) {

        this.serviceFactory = serviceFactory;
        this.hybrisCustomersDao = hybrisCustomersDao;
        this.hybrisHRStaffDao = hybrisHRStaffDao;
        this.customerHelper = customerHelper;
    }

    private void createforStaffMemberRequest(boolean associated) throws EasyjetCompromisedException {

        List<HRStaffModel> staffMembers;
        if (associated) {
            staffMembers = hybrisHRStaffDao.returnAssociatedHRStaffMember();
        } else {
            staffMembers = hybrisHRStaffDao.returnUnassociatedHRStaffMember();
        }
        if (staffMembers.isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
        staffMemberRequest = StaffMemberRequest.builder()
                .email(customerHelper.getRequest().getPersonalDetails().getEmail())
                .title(customerHelper.getRequest().getPersonalDetails().getTitle())
                .firstName(customerHelper.getRequest().getPersonalDetails().getFirstName())
                .lastName(customerHelper.getRequest().getPersonalDetails().getLastName())
                .employeeId(staffMembers.get(0).getP_employeeid())
                .employeeEmail(staffMembers.get(0).getP_email())
                .build();
    }

    private void createforStaffMemberRequest(boolean associated, CustomerModel customer) throws EasyjetCompromisedException {

        List<HRStaffModel> staffMembers;
        if (associated) {
            staffMembers = hybrisHRStaffDao.returnAssociatedHRStaffMember();
        } else {
            staffMembers = hybrisHRStaffDao.returnUnassociatedHRStaffMember();
        }
        if (staffMembers.isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
        staffMemberRequest = StaffMemberRequest.builder()
                .email(customer.getCustomerid())
                .title("Mr")
                .firstName("first")
                .lastName("last")
                .employeeId(staffMembers.get(0).getP_employeeid())
                .employeeEmail(staffMembers.get(0).getP_email())
                .build();
    }

    private String getCustomerIdAfterRegistrationConfirmation() {
        testData.setAccessToken(customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getAuthentication().getAccessToken());
        testData.setData(SerenityFacade.DataKeys.CUSTOMER_ACCESS_TOKEN,customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getAuthentication().getAccessToken());
        return customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
    }

    public void associateCustomerProfileWithStaffMemberFromRequest(boolean linked) throws Throwable {
        String customerId = getCustomerIdAfterRegistrationConfirmation();
        associateCustomerProfileWithStaffMemberFromId(customerId, linked);
    }

    public void associateCustomerProfileWithStaffMemberFromRequest(String channel, boolean linked) throws Throwable {
        String customerId = getCustomerIdAfterRegistrationConfirmation();
        testData.setData(CUSTOMER_ID, customerId);
        testData.setAccessToken(getCustomerIdAfterRegistrationConfirmation());
        createforStaffMemberRequest(linked);
        doCustomerStaffAssociation(customerId);
    }

    public RegisterStaffFaresService getRegisterStaffFaresService() {

        return registerStaffFaresService;
    }

    private void memberAccountIsLinkedWithEmployeeID() throws EasyjetCompromisedException {

        String employeeId;
        try {
            employeeId = hybrisHRStaffDao.returnAssociatedHRStaffMember().get(0).getP_employeeid();
        } catch (NullPointerException e) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }

        List<CustomerModel> customerModels = hybrisCustomersDao.returnValidCustomerDetailsFromUsersTable(employeeId);
        if (!customerModels.get(0).getUid().isEmpty()) {
            customerIdFromDatabase = customerModels.get(0).getUid();
        }
    }

    public void createforStaffMemberRequestWithMissingParameters(String missingParameter) {

        DataFactory df = new DataFactory();
        String email = df.getEmailAddress();
        String title = df.getRandomText(2);
        String firstName = df.getFirstName();
        String lastName = df.getLastName();
        //String employeeId="2369"; 'for debugging valid data
        //String employeeEmail="barry.russell2@easyjet.com"; 'for debugging valid data
        String employeeId = hybrisHRStaffDao.returnValidHRStaffFromHRStaffTable().get(0).getP_employeeid();
        String employeeEmail = hybrisHRStaffDao.returnValidHRStaffFromHRStaffTable().get(0).getP_email();

        switch (missingParameter) {
            case ("email"):
                email = "";
                break;
            case ("title"):
                title = "";
                break;
            case ("firstname"):
                firstName = "";
                break;
            case ("lastname"):
                lastName = "";
                break;
            case ("employeeId"):
                employeeId = "";
                break;
            case ("employeeEmail"):
                employeeEmail = "";
                break;
            default:
                break;
        }
        staffMemberRequest = StaffMemberRequest.builder().email(email)
                .title(title)
                .firstName(firstName)
                .lastName(lastName)
                .employeeId(employeeId)
                .employeeEmail(employeeEmail)
                .build();
    }

    public void associateCustomerProfileWithMissingStaffMemberFromRequest(String mandatoryField) throws Throwable {
        memberAccountIsLinkedWithEmployeeID();
        createforStaffMemberRequestWithMissingParameters(mandatoryField);
        doCustomerStaffAssociation(customerIdFromDatabase);
    }

    public void associateCustomerProfileWithStaffMemberFromId(String customerId, boolean linked) throws Throwable {
        createforStaffMemberRequest(linked);
        doCustomerStaffAssociation(customerId);
    }

    public void associateCustomerProfileWithStaffMemberFromId(String customerId, boolean linked, CustomerModel customer) throws Throwable {
        createforStaffMemberRequest(linked, customer);
        doCustomerStaffAssociation(customerId);
    }

    private void doCustomerStaffAssociation(String customerId) {
        CustomerPathParams registerStaffPathParams = CustomerPathParams.builder()
                .customerId(customerId)
                .path(STAFF_PRIVILEGE)
                .build();

        registerStaffFaresService = serviceFactory.createStaffMember(new RegisterStaffFaresRequest(HybrisHeaders.getValid(testData.getChannel()).build(), registerStaffPathParams, staffMemberRequest));
        registerStaffFaresService.invoke();
    }


}
