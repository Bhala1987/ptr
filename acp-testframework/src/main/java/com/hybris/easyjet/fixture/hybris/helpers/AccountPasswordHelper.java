package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.ResetPasswordRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.UpdatePasswordRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AccountPasswordFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ResetPasswordRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdatePasswordRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.ResetPasswordService;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdatePasswordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.*;

/**
 * Created by giuseppecioce on 03/03/2017.
 */
@Component
public class AccountPasswordHelper {
    private static final String F_NEW_PASS_WORD = "fNewpassword";

    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private SerenityFacade testData;
    private UpdatePasswordService updatePasswordService;
    private ResetPasswordService resetPasswordService;
    private UpdatePasswordRequestBody bodyRequestUpdatePassword;
    private ResetPasswordRequestBody resetPasswordRequestBody;
    private String currentCustomerId;
    private String currentEmail;
    public UpdatePasswordService getUpdatePasswordService() {
        return updatePasswordService;
    }
    public ResetPasswordService getResetPasswordService() {
        return resetPasswordService;
    }

    public void createNewAccountForCustomerAndLoginIt() {
        customerHelper.customerAccountExistsWithAKnownPassword();
        testData.setData(CUSTOMER_ID, customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId());
        testData.setAccessToken(customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getAuthentication().getAccessToken());
        testData.setEmail(customerHelper.getRequest().getPersonalDetails().getEmail());
        testData.setPassword(customerHelper.getRequest().getPersonalDetails().getPassword());
    }

    public void updatePassword(String channel, String newPassword) {
        testData.setChannel(channel);
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(PASSWORD).build();
        bodyRequestUpdatePassword = UpdatePasswordRequestBody.builder().currentPassword(testData.getPassword())
                .newPassword(newPassword).build();
        updatePasswordService = serviceFactory
                .getUpdatePassword(new UpdatePasswordRequest(HybrisHeaders.getValid(channel).build(), params, bodyRequestUpdatePassword));
        updatePasswordService.invoke();
    }

    public void getLogin(String password) {
        if (testData.getEmail() != null){
            currentEmail = testData.getEmail();
        }
        customerHelper.loginWithValidCredentials(StringUtils.EMPTY, currentEmail, password, false);
    }

    public void createRequestToUpdatePassword() {
        bodyRequestUpdatePassword = AccountPasswordFactory.aCompleteRequestToUpdatePassword("");
    }

    public void createNewAccountForCustomer() {
        customerHelper.customerAccountExistsWithAKnownPassword();
        currentCustomerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
        currentEmail = customerHelper.getRequest().getPersonalDetails().getEmail();
    }

    public void updatePasswordRequestBodyMissingField(String field) {
        bodyRequestUpdatePassword = AccountPasswordFactory.missingFieldProfileSavedPassenger(field, "");
    }

    public void callServiceUpdatePassword() {
        CustomerPathParams params = CustomerPathParams.builder().customerId(currentCustomerId).path(PASSWORD).build();
        updatePasswordService = serviceFactory
                .getUpdatePassword(new UpdatePasswordRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, bodyRequestUpdatePassword));
        updatePasswordService.invoke();
        testData.setPassword(bodyRequestUpdatePassword.getNewPassword());
    }

    public void updatePasswordRequestWithFieldLength(String field, int length) {

        String stringDataToUse = AccountPasswordFactory.getRandomString(12);

        if (field.equals(F_NEW_PASS_WORD)) {
            if (length >= stringDataToUse.length()) {
                stringDataToUse = AccountPasswordFactory.getRandomString(length + 1);
            }

            bodyRequestUpdatePassword.setNewPassword(stringDataToUse.substring(0, length));
        }
    }

    public void updatePasswordFieldWithSpace(String field) {

        if (field.equals(F_NEW_PASS_WORD)) {
            String newPassword = AccountPasswordFactory.getRandomString(12);
            newPassword = newPassword.replaceFirst(Character.toString(newPassword.charAt(2)), " ");
            bodyRequestUpdatePassword.setNewPassword(newPassword);
        }
    }

    public void updatePasswordFieldWithValue(String field, String value) {

        switch (field) {
            case F_NEW_PASS_WORD:
                bodyRequestUpdatePassword.setNewPassword(value);
                break;
            case "passwordResetToken":
                bodyRequestUpdatePassword.setPasswordResetToken(value);
                break;
            default:
                break;
        }
    }

    public void setUpdatePasswordRequestFieldWithSymbol(String field, String symbol) {
        char symbolToReplace = symbol.charAt(0);

        if (field.equals(F_NEW_PASS_WORD)) {
            StringBuilder newPassword = new StringBuilder(AccountPasswordFactory.getRandomString(12));
            newPassword.setCharAt(2, symbolToReplace);
            bodyRequestUpdatePassword.setNewPassword(newPassword.toString());
        }
    }

    public void verifyCustomerProfileIsStored() {
        String newPassword = bodyRequestUpdatePassword.getNewPassword();
        customerHelper.loginWithValidCredentials(StringUtils.EMPTY, currentEmail, newPassword, false);
        customerHelper.getLoginDetailsService().assertThat().theLoginWasSuccesful();
    }

    public void verifyAllDataRelatedCustomerAreClear() {
        CustomerPathParams profilePathParams = CustomerPathParams.builder().customerId(currentCustomerId).path(PROFILE).build();
        CustomerProfileService customerProfileService;
        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValid(testData.getChannel()).build(), profilePathParams));
        customerProfileService.invoke();

        customerProfileService.assertThat().verifyAllReferenceDataForCustomerAreClear();
    }

    public void buildRequestForResetPassword() {
        resetPasswordRequestBody = ResetPasswordRequestBody.builder().email(currentEmail).build();
    }

    public void buildRequestForResetPasswordForAnonymous(String email) {
        resetPasswordRequestBody = ResetPasswordRequestBody.builder().email(email).build();
    }

    public void updateResetPasswordEmail(String newEmail) {
        resetPasswordRequestBody.setEmail(newEmail);
    }

    public void callServiceResetPassword() {
        CustomerPathParams params = CustomerPathParams.builder().customerId(currentCustomerId).path(RESET_PASSWORD).build();
        resetPasswordService = serviceFactory.getResetPasswordService(new ResetPasswordRequest(HybrisHeaders.getValid(testData.getChannel()).build(), params, resetPasswordRequestBody));
        resetPasswordService.invoke();
    }


    public void callServiceResetPasswordForAnonymous() {
        resetPasswordService = serviceFactory.getAnonymousResetPasswordService(new ResetPasswordRequest(HybrisHeaders.getValid(testData.getChannel()).build(), resetPasswordRequestBody));
        resetPasswordService.invoke();
    }

    public String getCurrentCustomerId() {
        return currentCustomerId;
    }

    public String getTokenForCustomer() {
        return getToken();
    }

    private String getToken() {
        return customerDao.getCustomerTokenWithId(currentCustomerId).get(0);
    }
}
