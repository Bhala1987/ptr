package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.ApiDao;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.CustomerProfileQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.IdentifyCustomerQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Customer;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.login.LoginDetails;
import com.hybris.easyjet.fixture.hybris.invoke.requests.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import cucumber.api.Scenario;
import org.apache.commons.lang3.StringUtils;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yecht.Data;

import java.util.List;
import java.util.Objects;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.REMOVE_APIS;
import static junit.framework.TestCase.fail;

/**
 * Created by AndrewGr on 28/12/2016.
 * <p>
 * All methods associated with customer authentication.
 *
 * @author AndrewGr
 * @author Joshua Curtis <j.curtis@reply.com>
 */
@Component
public class CustomerHelper {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private ApiDao apiDao;

    private final HybrisServiceFactory serviceFactory;
    private RegisterCustomerService registerCustomerService;
    private UpdateCustomerDetailsService updateCustomerDetailsService;
    private LoginDetailsService loginDetailsService;
    private RegisterCustomerRequestBody request;
    private String knownEmail;
    private String defaultChannel = "Digital";
    private CustomerProfileQueryParams customerProfileQueryParams;


    @Autowired
    public CustomerHelper(RegisterCustomerFactory registerCustomerFactory, HybrisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public void createRandomCustomer(String channel) {
        request = RegisterCustomerFactory.aDigitalProfile();
        registerCustomerService = serviceFactory.registerCustomer(new RegisterNewCustomerRequest(HybrisHeaders.getValid(channel).build(), request));
        registerCustomerService.invoke();
        testData.setData(CUSTOMER_ID, registerCustomerService.getResponse().getBookingConfirmation().getCustomerId());
        testData.setAccessToken(registerCustomerService.getResponse().getBookingConfirmation().getAuthentication().getAccessToken());
        testData.setData(CUSTOMER_ACCESS_TOKEN, registerCustomerService.getResponse().getBookingConfirmation().getAuthentication().getAccessToken());
        testData.setEmail(request.getPersonalDetails().getEmail());
        testData.setPassword(request.getPersonalDetails().getPassword());
    }

    public void createRandomCustomerWithMemberId(String channel, Integer memberId) {
        request = RegisterCustomerFactory.aDigitalProfile();
        request.getPersonalDetails().setMemberId(memberId);
        registerCustomerService = serviceFactory.registerCustomer(new RegisterNewCustomerRequest(HybrisHeaders.getValid(channel).build(), request));
        registerCustomerService.invoke();
        testData.setData(CUSTOMER_ID, registerCustomerService.getResponse().getBookingConfirmation().getCustomerId());
        testData.setAccessToken(registerCustomerService.getResponse().getBookingConfirmation().getAuthentication().getAccessToken());
        testData.setData(CUSTOMER_ACCESS_TOKEN, registerCustomerService.getResponse().getBookingConfirmation().getAuthentication().getAccessToken());
        testData.setEmail(request.getPersonalDetails().getEmail());
        testData.setPassword(request.getPersonalDetails().getPassword());
    }

    public void aValidRequestToCreateAProfileForCustomer() {
        request = RegisterCustomerFactory.aDigitalProfile();
    }

    public void setCustomerPasswordLength(int passwordLength) {
        DataFactory df = new DataFactory();
        request.getPersonalDetails().setPassword(df.getRandomChars(passwordLength));
    }

    public void loginWithValidCredentialsAndRememberMe() {
        loginWithValidCredentials(null, null, null, true);
    }

    public void loginWithValidCredentials() {
        loginWithValidCredentials(null, null, null, false);
    }

    public void loginWithInvalidCredentials() {
        loginWithValidCredentials("", "ewjuh88weh@uedgweyewtq.com", "ujse83gwev3wy", false);
    }

    public void loginWithValidCredentials(String channel) {
        loginWithValidCredentials(channel, null, null, false);
    }

    public void loginWithCustomXClientTransactionId(String transactionId) {

        if (StringUtils.isBlank(transactionId)) {
            fail("Please provide the custom X-client-transaction-id");
        }

        LoginDetails loginRequest = LoginDetails.builder()
                .email(testData.getEmail())
                .password(testData.getPassword())
                .rememberme(false)
                .build();

        loginDetailsService = serviceFactory.loginCustomer(
                new LoginRequest(HybrisHeaders.getValidXClientTransactionId(testData.getChannel(), transactionId).build(), loginRequest)
        );

        loginDetailsService.invoke();

    }

    public void loginWithValidCredentials(String channel, String email, String password, boolean rememberMe) {
        String channelUsing;
        if ((Objects.isNull(channel) || StringUtils.isBlank(channel))) {
            channelUsing = testData.getChannel();
        } else {
            channelUsing = channel;
        }

        if (StringUtils.isBlank(email)) {
            testData.setEmail(request.getPersonalDetails().getEmail());
        } else {
            testData.setEmail(email);
        }

        if (StringUtils.isBlank(password)) {
            testData.setPassword(request.getPersonalDetails().getPassword());
        } else {
            testData.setPassword(password);
        }

        LoginDetails loginRequest = LoginDetails.builder()
                .email(testData.getEmail())
                .password(testData.getPassword())
                .rememberme(rememberMe)
                .build();

        loginDetailsService = serviceFactory.loginCustomer(
                new LoginRequest(HybrisHeaders.getValid(channelUsing).build(), loginRequest)
        );

        loginDetailsService.invoke();

        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            testData.setAccessToken(loginDetailsService.getResponse().getAuthenticationConfirmation().getAuthentication().getAccessToken());
            testData.setData(CUSTOMER_ACCESS_TOKEN,loginDetailsService.getResponse().getAuthenticationConfirmation().getAuthentication().getAccessToken());
            testData.setData(CUSTOMER_ID, loginDetailsService.getResponse().getAuthenticationConfirmation().getCustomerId());
        }

        testData.setData(SERVICE, loginDetailsService);
    }

    public void loginWithInvalidPassword(String channel) {
        testData.setChannel(channel);
        if (StringUtils.isBlank(channel)) {
            testData.setChannel(defaultChannel);
        }

        LoginDetails loginRequest = LoginDetails.builder().email(request.getPersonalDetails().getEmail()).password("notvalidpass").rememberme(false).build();
        loginDetailsService = serviceFactory.loginCustomer(new LoginRequest(HybrisHeaders.getValid(testData.getChannel()).build(), loginRequest));
        loginDetailsService.invoke();

    }

    public LoginDetailsService getLoginDetailsService() {
        return loginDetailsService;
    }

    public void loginWithDifferentEmail(String email) {
        testData.setEmail(email);
        if (StringUtils.isBlank(email)) {
            testData.setEmail(request.getPersonalDetails().getEmail());
        }
        loginWithValidCredentials("", testData.getEmail(), null, false);
    }

    public void customerAccountExistsWithAKnownPassword() {
        request = RegisterCustomerFactory.aDigitalProfile();
        createCustomerProfileFromRequest(request);
    }

    public void childCustomerAccountExistsWithAKnownPassword() {
        request = RegisterCustomerFactory.aDigitalChildProfile();
        createCustomerProfileFromRequest(request);
    }

    public void requestCreationOfACustomerProfile() {
        createCustomerProfileFromRequest(request);
    }

    public void createCustomerProfileFromRequest(RegisterCustomerRequestBody request) {
        customerProfileQueryParams = CustomerProfileQueryParams.builder().registrationtype("REGISTERED").build();

        registerCustomerService = serviceFactory.registerCustomer(new RegisterNewCustomerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), request, customerProfileQueryParams));
        registerCustomerService.invoke();
        Scenario scenario = testData.getData(SCENARIO);
        if (!scenario.getSourceTagNames().contains("@negative")) {
            testData.setData(REGISTER_CUSTOMER_REQUEST, request);
            testData.setData(CUSTOMER_ID, registerCustomerService.getResponse().getBookingConfirmation().getCustomerId());
            testData.setAccessToken(registerCustomerService.getResponse().getBookingConfirmation().getAuthentication().getAccessToken());
        }
    }

    public void createTemporaryCustomerProfile() {
        request = RegisterCustomerFactory.aDigitalProfile();
        customerProfileQueryParams = CustomerProfileQueryParams.builder().registrationtype("TEMPORARY").build();
        registerCustomerService = serviceFactory.registerCustomer(new RegisterNewCustomerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), request, customerProfileQueryParams));
        registerCustomerService.invoke();
        testData.setData(REGISTER_CUSTOMER_REQUEST, request);
        testData.setData(CUSTOMER_ID, registerCustomerService.getResponse().getBookingConfirmation().getCustomerId());

    }

    public void searchForTemporaryCustomer() {
        IdentifyCustomerQueryParams identifyQueryParams = IdentifyCustomerQueryParams.builder().title(request.getPersonalDetails().getTitle()).firstname(request.getPersonalDetails().getFirstName()).lastname(request.getPersonalDetails().getLastName()).build();
        IdentifyCustomerService identifyCustomerService = serviceFactory.identifyCustomer(new IdentifyCustomerRequest(HybrisHeaders.getValid(testData.getChannel()).build(), identifyQueryParams));
        identifyCustomerService.invoke();
        testData.setData(SerenityFacade.DataKeys.IDENTIFY_CUSTOMER_SERVICE, identifyCustomerService);
    }

    public RegisterCustomerService getRegisterCustomerService() {
        return registerCustomerService;
    }

    public void customerRequestWithMissingField(String channel, String field) {
        request = RegisterCustomerFactory.aCustomerRequestWithMissingField(channel, field);
    }

    public void setCustomerPasswordWithSpace() {
        DataFactory df = new DataFactory();
        String password = df.getRandomChars(15);
        password = password.replace(password.charAt(2), ' ');
        request.getPersonalDetails().setPassword(password);
    }

    public void setCustomerPasswordWithAGuessableWord(String password) {
        request.getPersonalDetails().setPassword(password);
    }

    public void setCustomerPasswordWithSymbol(String symbolToInsertIntoPassword) {
        setCustomerProfileFieldWithSymbol("password", symbolToInsertIntoPassword);
    }

    public void setCustomerProfileFieldWithSymbol(String field, String symbol) {
        DataFactory df = new DataFactory();

        char symbolToReplace = symbol.charAt(0);

        switch (field) {
            case "password":
                String password = df.getRandomChars(15);
                password = password.replace(password.charAt(2), symbolToReplace);
                request.getPersonalDetails().setPassword(password);
                break;
            case "customerfirstName":
                String firstName = df.getFirstName();
                request.getPersonalDetails().setFirstName(
                        firstName.replace(firstName.charAt(1), symbolToReplace)
                );
                break;
            case "customerlastName":
                String lastName = df.getLastName();
                request.getPersonalDetails().setLastName(
                        lastName.replace(lastName.charAt(1), symbolToReplace)
                );
                break;
            default:
                break;
        }
    }

    public void customerAccountExistsWithAKnownEmail() {
        request = RegisterCustomerFactory.aDigitalProfile();
        knownEmail = request.getPersonalDetails().getEmail();

        createCustomerProfileFromRequest(request);
    }

    public void creatNewCustomerProfileWithPeviouslyUsedEmail() {
        request = RegisterCustomerFactory.aDigitalProfile();
        request.getPersonalDetails().setEmail(knownEmail);

        createCustomerProfileFromRequest(request);
    }

    public void createNewCustomerProfileWithEmail(String email) {
        request = RegisterCustomerFactory.aDigitalProfile();
        request.getPersonalDetails().setEmail(email);

        createCustomerProfileFromRequest(request);
        testData.setEmail(request.getPersonalDetails().getEmail());
        testData.setPassword(request.getPersonalDetails().getPassword());
    }

    public void customerRequestWithFieldAndFieldLength(String field, int length) {
        DataFactory df = new DataFactory();
        String stringDataToUse = df.getRandomText(100);

        switch (field) {
            case "fName":
                request.getPersonalDetails().setFirstName(stringDataToUse.substring(0, length));
                break;
            case "lName":
                request.getPersonalDetails().setLastName(stringDataToUse.substring(0, length));
                break;
            case "pNumber":
                request.getPersonalDetails().setPhoneNumber(parsePhoneNumber(length, df));
                break;
            case "aLine1":
                request.getContactAddress().get(0).setAddressLine1(stringDataToUse.substring(0, length));
                break;
            case "addressLine2":
                request.getContactAddress().get(0).setAddressLine2(stringDataToUse.substring(0, length));
                break;
            case "acity":
                request.getContactAddress().get(0).setCity(stringDataToUse.substring(0, length));
                break;
            case "country":
                request.getContactAddress().get(0).setCountry(stringDataToUse.substring(0, length));
                break;
            case "pCode":
                request.getContactAddress().get(0).setPostalCode(stringDataToUse.substring(0, length));
                break;
            default:
                break;
        }
    }

    private String parsePhoneNumber(int length, DataFactory df) {
        String phoneNumber = "";

        if (length == 5) {
            phoneNumber = Integer.toString(df.getNumberBetween(10000, 19999));
        } else if (length == 19) {
            phoneNumber = Integer.toString(df.getNumberBetween(1000000000, 1999999999)) + Integer.toString(df.getNumberBetween(1000000000, 1999999990));
        }

        return phoneNumber;
    }

    public void loginWithMissingParametersInBody(String parameter) {
        LoginDetails loginRequest = createValidLoginRequest();

        if ("MissingEmail".equals(parameter)) {
            loginRequest.setEmail(null);
        }

        if ("MissingPassword".equals(parameter)) {
            loginRequest.setPassword(null);
        }

        loginDetailsService = serviceFactory.loginCustomer(
                new LoginRequest(
                        HybrisHeaders.getValid("Digital").build(),
                        loginRequest
                )
        );
        loginDetailsService.invoke();
        testData.setData(SERVICE, loginDetailsService);
    }

    public LoginDetails createValidLoginRequest() {
        return LoginDetails.builder()
                .email(request.getPersonalDetails().getEmail())
                .password(request.getPersonalDetails().getPassword())
                .rememberme(false)
                .build();
    }

    public RegisterCustomerRequestBody getRequest() {
        return request;
    }

    public void updateCustomerDetails(String customerId, Customer body) {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).path(PROFILE).build();
        updateCustomerDetailsService = serviceFactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), pathParams, body));
        updateCustomerDetailsService.invoke();
    }

    public void updateCustomerDetailsUsingAccessToken(String customerId, Customer body) {
        CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).path(PROFILE).build();
        updateCustomerDetailsService = serviceFactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), pathParams, body));
        updateCustomerDetailsService.invoke();
        testData.setData(SERVICE, updateCustomerDetailsService);
    }

    public UpdateCustomerDetailsService getUpdateCustomerDetailsService() {
        return updateCustomerDetailsService;
    }

    public CustomerProfileResponse getCustomerProfile(String customerId) {
        CustomerPathParams profilePathParams = CustomerPathParams.builder()
                .customerId(customerId)
                .path(PROFILE)
                .build();

        HybrisHeaders headers;
        if (testData.getAccessToken() != null) {
            headers = HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build();
        } else {
            headers = HybrisHeaders.getValid(testData.getChannel()).build();
        }

        CustomerProfileService customerProfileService = serviceFactory.getCustomerProfile(
            new ProfileRequest(headers, profilePathParams)
        );
        customerProfileService.invoke();

        testData.setData(SERVICE, customerProfileService);

        return customerProfileService.getResponse();
    }

    public void removeAllAPIsForCustomer(String customerId, String channel) {
        List<String> listOfDocumentInHybris = apiDao.getDocumentIdsForCustomer(customerId);
        for (String documentId : listOfDocumentInHybris) {
            CustomerPathParams pathParams = CustomerPathParams.builder().customerId(customerId).path(REMOVE_APIS).documentId(documentId).build();
            UpdateCustomerDetailsService removeApisCustomerService = serviceFactory.removeApisService(new RemoveApisRequest(HybrisHeaders.getValid(channel).build(), pathParams));
            testData.setData(SERVICE, removeApisCustomerService);
            removeApisCustomerService.invoke();
            removeApisCustomerService.assertThat().customerUpdated(customerId);
        }
    }

    public void createCustomerAndLoginIt() {
        if(!testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CHANNEL) && !testData.getChannel().equalsIgnoreCase(CommonConstants.AD_CUSTOMER_SERVICE)) {
            createCustomerProfile();
            loginCustomer();
        }
    }

    private void createCustomerProfile() {
        customerAccountExistsWithAKnownPassword();
    }

    private void loginCustomer() {
        loginWithValidCredentialsAndRememberMe();
        getLoginDetailsService().assertThat().theLoginWasSuccesful();
    }
}
