package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences.UpdateCustomerPreferencesAncillaryRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences.UpdateCustomerPreferencesCommunicationRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences.UpdateCustomerPreferencesFullRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences.UpdateCustomerPreferencesTravelRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateCustomerDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateCustomerDetailsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.UpdateCustomerPreferencesFactory.*;

/**
 * Created by jamie on 29/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class UpdateCustomerPreferencesSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private CustomerPathParams theCustomerPathParams;
    private UpdateCustomerDetailsService updateCustomerDetailsService;
    private CustomerProfileService customerProfileService;
    private UpdateType theChosenUpdateType;

    @And("^the update customer preferences service returns error:(.*)$")
    public void theUpdateCustomerPreferencesServiceReturnsErrorErrorCode(String aErrorCode) throws Throwable {
        updateCustomerDetailsService.assertThatErrors().containedTheCorrectErrorMessage(aErrorCode.trim());
    }

    @Then("^I will not update the Customer's profile with the preferences$")
    public void iWillNotUpdateTheCustomerSProfileWithThePreferences() throws Throwable {
        getCustomerProfile();
        updateCustomerDetailsService.assertThat().customerProfileIsNotUpdated(customerProfileService.getResponse());
    }

    @When("^I update my full customer preferences with (.*)$")
    public void iUpdateMyFullCustomerPreferencesWithInvalidValue(String condition) {
        UpdateCustomerPreferencesFullRequestBody fullRequestBody = getFullRequestBody();
        LocalDate myDate = LocalDate.now();
        switch (condition) {
            case "invalid hold bag quantity":
                fullRequestBody.getAncillaryPreferences().setHoldBagQuantity("ff");
                break;
            case "invalid seat number":
                fullRequestBody.getAncillaryPreferences().setSeatNumber("xxxxx");
                break;
            case "toDate is past fromDate for opt out period":
                myDate = myDate.plusDays(30);
                fullRequestBody.getCommunicationPreferences().getOptedOutPeriod().setFromDate(formatDateToString(myDate));
                break;
            case "toDate is past fromDate for travelling period":
                myDate = myDate.plusDays(30);
                fullRequestBody.getTravelPreferences().getTravellingPeriod().setFromDate(formatDateToString(myDate));
                break;
            case "invalid date for opt out period":
                fullRequestBody.getCommunicationPreferences().getOptedOutPeriod().setFromDate("xxxxxxx");
                break;
            case "invalid date for travelling period":
                fullRequestBody.getTravelPreferences().getTravellingPeriod().setFromDate("xxxxxxx");
                break;
            case "invalid season":
                fullRequestBody.getTravelPreferences().getTravellingSeasons().add("wintersummer");
                break;
            case "invalid frequency":
                fullRequestBody.getCommunicationPreferences().setFrequency("everymillisecond");
                break;
            case "invalid airport code for preferred airports":
                fullRequestBody.getTravelPreferences().getPreferredAirports().add("LGWLGW");
                break;
            case "invalid opt out marketing":
                fullRequestBody.getCommunicationPreferences().getOptedOutMarketing().add("pleasestopspammingme");
                break;
            case "missing travel preferences":
                fullRequestBody.setTravelPreferences(null);
                break;
            case "missing ancillary preferences":
                fullRequestBody.setAncillaryPreferences(null);
                break;
            case "invalid airport code for travellingTo":
                fullRequestBody.getTravelPreferences().getTravellingTo().add("LGWLGW");
                break;
            case "missing communication preferences":
                fullRequestBody.setCommunicationPreferences(null);
                break;
        }
        invokeRequestToUpdateFullPreferences(fullRequestBody);
    }

    @When("^I update my communication customer preferences with (.*)$")
    public void iUpdateMyCommunicationCustomerPreferencesWithInvalidValue(String condition) {
        UpdateCustomerPreferencesCommunicationRequestBody communicationRequestBody = getCommunicationRequestBody();
        LocalDate myDate = LocalDate.now();
        switch (condition) {
            case "toDate is past fromDate for opt out period":
                myDate = myDate.plusDays(30);
                communicationRequestBody.getCommunicationPreferences().getOptedOutPeriod().setFromDate(formatDateToString(myDate));
                break;
            case "invalid date for opt out period":
                communicationRequestBody.getCommunicationPreferences().getOptedOutPeriod().setFromDate("xxxxxxx");
                break;
            case "invalid frequency":
                communicationRequestBody.getCommunicationPreferences().setFrequency("everymillisecond");
                break;
            case "invalid opt out marketing":
                communicationRequestBody.getCommunicationPreferences().getOptedOutMarketing().add("pleasestopspammingme");
                break;
        }
        invokeRequestToUpdateCommunicationPreferences(communicationRequestBody);
    }

    @When("^I update my travel customer preferences with (.*)$")
    public void iUpdateMyTravelCustomerPreferencesWithInvalidValue(String condition) {
        UpdateCustomerPreferencesTravelRequestBody travelRequestBody = getTravelRequestBody();
        LocalDate myDate = LocalDate.now();
        switch (condition) {
            case "toDate is past fromDate for travelling period":
                myDate = myDate.plusDays(30);
                travelRequestBody.getTravelPreferences().getTravellingPeriod().setFromDate(formatDateToString(myDate));
                break;
            case "invalid date for travelling period":
                travelRequestBody.getTravelPreferences().getTravellingPeriod().setFromDate("xxxxxxx");
                break;
            case "invalid season":
                travelRequestBody.getTravelPreferences().getTravellingSeasons().add("wintersummer");
                break;
            case "invalid airport code for preferred airports":
                travelRequestBody.getTravelPreferences().getPreferredAirports().add("LGWLGW");
                break;
            case "invalid airport code for travellingTo":
                travelRequestBody.getTravelPreferences().getTravellingTo().add("LGWLGW");
                break;
        }
        invokeRequestToUpdateTravelPreferences(travelRequestBody);
    }

    @When("^I update my ancillary customer preferences with (.*)$")
    public void iUpdateMyAncillaryCustomerPreferencesWithInvalidValue(String condition) {
        UpdateCustomerPreferencesAncillaryRequestBody ancillaryRequestBody = getAncillaryRequestBody();
        LocalDate myDate = LocalDate.now();
        switch (condition) {
            case "invalid hold bag quantity":
                ancillaryRequestBody.getAncillaryPreferences().setHoldBagQuantity("ff");
                break;
            case "invalid seat number":
                ancillaryRequestBody.getAncillaryPreferences().setSeatNumber("xxxxx");
                break;
        }
        invokeRequestToUpdateAncillaryPreferences(ancillaryRequestBody);
    }

    @When("^I update my (.*) customer preferences$")
    public void iUpdateMyTravelCustomerPreferences(String type) throws Throwable {
        switch (type) {
            case "full":
                invokeRequestToUpdateFullPreferences(getFullRequestBody());
                break;
            case "travel":
                invokeRequestToUpdateTravelPreferences(getTravelRequestBody());
                break;
            case "ancillary":
                invokeRequestToUpdateAncillaryPreferences(getAncillaryRequestBody());
                break;
            case "communication":
                invokeRequestToUpdateCommunicationPreferences(getCommunicationRequestBody());
                break;
        }
    }

    @Then("^I will receive a customer preferences update confirmation$")
    public void iWillReceiveACustomerPreferencesUpdateConfirmation() throws Throwable {
        updateCustomerDetailsService.assertThat().customerUpdated(testData.getData(CUSTOMER_ID));
    }

    @And("^the updated values will be returned when retrieving the customer profile$")
    public void theUpdatedValuesWillBeReturnedWhenRetrievingTheCustomerProfile() throws Throwable {
        // Now get the full customer profile and pass it to the assertion method to check the updated values
        getCustomerProfile();

        switch (theChosenUpdateType) {
            case FULL:
                updateCustomerDetailsService
                        .assertThat()
                        .fullPreferencesAreUpdated(customerProfileService.getResponse());
                break;

            case ANCILLARY:
                updateCustomerDetailsService
                        .assertThat()
                        .ancillaryPreferencesAreUpdated(customerProfileService.getResponse());
                break;
            case COMMUNICATION:
                updateCustomerDetailsService
                        .assertThat()
                        .communicationPreferencesAreUpdated(customerProfileService.getResponse());
                break;
            case TRAVEL:
                updateCustomerDetailsService
                        .assertThat()
                        .travelPreferencesAreUpdated(customerProfileService.getResponse());
                break;
            default:
                break;
        }

    }

    @Given("^I am debugging with hardcoded customer$")
    public void iAmDebuggingWithHardcodedCustomer() throws Throwable {
        testData.setData(CUSTOMER_ID, "cus00000001");
    }

    private void invokeRequestToUpdateFullPreferences(IRequestBody aRequestBody) {
        theCustomerPathParams = CustomerPathParams
                .builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(CustomerPathParams.CustomerPaths.UPDATE_FULL_PREFERENCES)
                .build();

        updateCustomerDetailsService = serviceFactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), theCustomerPathParams, aRequestBody));
        updateCustomerDetailsService.invoke();
        theChosenUpdateType = UpdateType.FULL;
        //can be enabled once we resolved the issue related to missing attribute in json schema
        //eventMessageCreationHelper.validateCustomerUpdateMessage(testData.getData(CUSTOMER_ID));
    }

    private void invokeRequestToUpdateCommunicationPreferences(IRequestBody aRequestBody) {
        theCustomerPathParams = CustomerPathParams
                .builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(CustomerPathParams.CustomerPaths.UPDATE_COMMUNICATION_PREFERENCES)
                .build()
        ;
        updateCustomerDetailsService = serviceFactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), theCustomerPathParams, aRequestBody));
        updateCustomerDetailsService.invoke();

        theChosenUpdateType = UpdateType.COMMUNICATION;
    }

    private void invokeRequestToUpdateTravelPreferences(IRequestBody aRequestBody) {
        theCustomerPathParams = CustomerPathParams
                .builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(CustomerPathParams.CustomerPaths.UPDATE_TRAVEL_PREFERENCES)
                .build()
        ;

        updateCustomerDetailsService = serviceFactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), theCustomerPathParams, aRequestBody));
        updateCustomerDetailsService.invoke();

        theChosenUpdateType = UpdateType.TRAVEL;
    }

    private void invokeRequestToUpdateAncillaryPreferences(IRequestBody aRequestBody) {
        theCustomerPathParams = CustomerPathParams
                .builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(CustomerPathParams.CustomerPaths.UPDATE_ANCILLARY_PREFERENCES)
                .build()
        ;

        updateCustomerDetailsService = serviceFactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(HybrisHeaders.getValid(testData.getChannel()).build(), theCustomerPathParams, aRequestBody));
        updateCustomerDetailsService.invoke();
        theChosenUpdateType = UpdateType.ANCILLARY;
    }

    private void getCustomerProfile() {
        CustomerPathParams profilePathParams = CustomerPathParams
                .builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(PROFILE)
                .build();

        customerProfileService = serviceFactory.getCustomerProfile(new ProfileRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), profilePathParams, null));
        customerProfileService.invoke();
    }

    @Then("^update confirmation is success$")
    public void updateConfirmationIsSuccess() throws Throwable {
        updateCustomerDetailsService = testData.getData(SerenityFacade.DataKeys.SERVICE);
        updateCustomerDetailsService.assertThat().customerUpdated(testData.getData(CUSTOMER_ID));
    }

    private enum UpdateType {
        FULL, COMMUNICATION, TRAVEL, ANCILLARY
    }

}
