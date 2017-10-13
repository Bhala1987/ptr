package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Customer;
import com.hybris.easyjet.fixture.hybris.invoke.requests.UpdateCustomerDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.UpdateCustomerDetailsService;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.When;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.customer.CustomerPathParams.CustomerPaths.PROFILE;

/**
 * UpdateCustomerProfileSteps handle the communication with the updateCustomerProfile service (aka updateCustomerDetails).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdateCustomerProfileSteps {

    private static final String POSTCODE = "postcode";
    private static final String ADDRESS_LINE_1 = "address line 1";
    private static final String ADDRESS_LINE_2 = "address line 2";
    private static final String CITY = "city";
    private static final String BLANK = "BLANK";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    private UpdateCustomerDetailsService updateCustomerProfileService;
    private CustomerPathParams.CustomerPathParamsBuilder customerPathParamsBuilder;
    private Customer.CustomerBuilder updateCustomerProfileRequestBody;
    private Customer.PersonalDetails personalDetails;
    private Customer.ContactAddress contactAddress;

    private String postcode;
    private String addressLine1;
    private String addressLine2;
    private String city;

    private void setPathParameter() {
        customerPathParamsBuilder = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(PROFILE);
    }

    private void setRequestBody() {
        testData.dataFactory.randomize(new Random(System.currentTimeMillis()).nextInt());
        String[] fullname = testData.dataFactory.getName().split(" ");

        if(StringUtils.isBlank(postcode)){
            postcode = testData.dataFactory.getRandomChars(6);
        }
        else if(postcode.equals(BLANK)){
            postcode = "";
        }
        if(StringUtils.isBlank(addressLine1)){
            addressLine1 = testData.dataFactory.getAddress();
        }
        else if(addressLine1.equals(BLANK)){
            addressLine1 = "";
        }
        if(StringUtils.isBlank(addressLine2)){
            addressLine2 = testData.dataFactory.getAddressLine2();
        }
        else if(addressLine2.equals(BLANK)){
            addressLine2 ="";
        }
        if(StringUtils.isBlank(city)){
            city = testData.dataFactory.getRandomText(6);
        }
        else if(city.equals(BLANK)){
            city = "";
        }

        personalDetails = Customer.PersonalDetails.builder()
                .email("success" + fullname[0] + "_" + testData.dataFactory.getNumberText(10) + "@abctest.com")
                .type("adult")
                .age(26)
                .title("mr")
                .firstName(fullname[0])
                .lastName(fullname[1])
                .ejPlusCardNumber("")
                .nifNumber("")
                .phoneNumber(testData.dataFactory.getNumberText(12))
                .alternativePhoneNumber(testData.dataFactory.getNumberText(12))
                .flightClubId("")
                .flightClubExpiryDate("")
                .keyDates(new ArrayList<>())
                .build();
        contactAddress = Customer.ContactAddress.builder()
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .addressLine3("")
                .city(city)
                .country("GBR")
                .postalCode(postcode)
                .build();
        updateCustomerProfileRequestBody = Customer.builder()
                .personalDetails(personalDetails)
                .contactAddress(Collections.singletonList(contactAddress));
    }

    private void invokeUpdateCustomerProfileService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        updateCustomerProfileService = serviceFactory.updateCustomerDetails(new UpdateCustomerDetailsRequest(headers.build(), customerPathParamsBuilder.build(), updateCustomerProfileRequestBody.build()));
        testData.setData(SERVICE, updateCustomerProfileService);
        updateCustomerProfileService.invoke();
    }

    private void sendUpdateCustomerProfileRequest() {
        setPathParameter();
        setRequestBody();
        invokeUpdateCustomerProfileService();
    }

    private void setAuthorization(){
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        if (((String) testData.getData(CHANNEL)).startsWith("AD"))
            testData.setData(HEADERS, headers.authorization("Bearer " + testData.getData(AGENT_ACCESS_TOKEN)));
        else
            testData.setData(HEADERS, headers.authorization("Bearer " + testData.getData(CUSTOMER_ACCESS_TOKEN)));
    }

    @When("^I send the request to updateCustomerProfile service$")
    public void updateCustomerProfile() {
        setAuthorization();
        sendUpdateCustomerProfileRequest();
    }

    @And("^I want to update a customer profile with field (postcode|address line 1|address line 2|city) as (.*)$")
    public void iWantToUpdateACustomerProfileWithFieldFieldAsInvalidValue(String field, String value) {

        switch (field.toLowerCase()){
            case POSTCODE:
            {
                postcode = value;
                break;
            }
            case ADDRESS_LINE_1:
            {
                addressLine1 = value;
                break;
            }
            case ADDRESS_LINE_2:
            {
                addressLine2 = value;
                break;
            }
            case CITY:
            {
                city = value;
                break;
            }
        }
    }
}