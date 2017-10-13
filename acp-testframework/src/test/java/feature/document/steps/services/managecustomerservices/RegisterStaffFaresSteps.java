package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.HRStaffDao;
import com.hybris.easyjet.database.hybris.models.HRStaffModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.StaffMemberRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.StaffMemberNewRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.StaffMemberNewService;
import net.thucydides.core.annotations.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.STAFF_PRIVILEGE;


/**
 * RegisterStaffFaresSteps handle the communication with the registerStaffFares service (association of customer to HR members).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class RegisterStaffFaresSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private HRStaffDao hybrisHRStaffDao;

    private StaffMemberNewService staffMemberNewService;
    private CustomerPathParams.CustomerPathParamsBuilder registerStaffPathParams;
    private StaffMemberRequest.StaffMemberRequestBuilder staffMemberRequest;

    private HRStaffModel staffMember;

    private void setPathParameter() {
        registerStaffPathParams = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(STAFF_PRIVILEGE);
    }

    private void setRequestBody() {
        List<HRStaffModel> staffMembers = hybrisHRStaffDao.returnUnassociatedHRStaffMember();
        staffMember = staffMembers.get(new Random().nextInt(staffMembers.size()));

        RegisterCustomerRequestBody registerCustomerRequestBody = testData.getData(REGISTER_CUSTOMER_REQUEST);
        staffMemberRequest = StaffMemberRequest.builder()
                .email(registerCustomerRequestBody.getPersonalDetails().getEmail())
                .title(registerCustomerRequestBody.getPersonalDetails().getTitle())
                .firstName(registerCustomerRequestBody.getPersonalDetails().getFirstName())
                .lastName(registerCustomerRequestBody.getPersonalDetails().getLastName())
                .employeeId(staffMember.getP_employeeid())
                .employeeEmail(staffMember.getP_email());
    }

    private void invokeRequestStaffFareEligibilityService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        staffMemberNewService = serviceFactory.checkStaffMember(new StaffMemberNewRequest(headers.build(), registerStaffPathParams.build(), staffMemberRequest.build()));
        staffMemberNewService.invoke();
    }

    @Step("Register customer as Staff")
    public void sendRegisterStaffFaresRequest() {
        setPathParameter();
        setRequestBody();
        invokeRequestStaffFareEligibilityService();
    }
}