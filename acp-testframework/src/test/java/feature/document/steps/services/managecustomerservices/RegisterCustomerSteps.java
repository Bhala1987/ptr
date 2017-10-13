package feature.document.steps.services.managecustomerservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.ContactAddress;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.PersonalDetails;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RegisterNewCustomerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.RegisterCustomerService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * RegisterCustomerSteps handle the communication with the registerCustomer service (aka createCustomer).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class RegisterCustomerSteps {

    private static final String SVC_100047_2038 = "SVC_100047_2038"; //Customer succesfully created
    private static final String SVC_100047_2031 = "SVC_100047_2031"; //Email already exist
    private static final String POSTCODE = "postcode";
    private static final String ADDRESS_LINE_1 = "address line 1";
    private static final String ADDRESS_LINE_2 = "address line 2";
    private static final String CITY = "city";
    private static final String BLANK = "BLANK";


    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private RegisterStaffFaresSteps registerStaffFaresSteps;

    private RegisterCustomerService registerCustomerService;
    private RegisterCustomerRequestBody.RegisterCustomerRequestBodyBuilder registerCustomerRequestBody;

    private String postcode;
    private String addressLine1;
    private String addressLine2;
    private String city;

    private void setRequestBody() {
        testData.dataFactory.randomize(new Random(System.currentTimeMillis()).nextInt());
        String name = testData.dataFactory.getFirstName();
        if (StringUtils.isBlank(postcode)) {
            postcode = testData.dataFactory.getRandomChars(6);
        } else if (postcode.equals(BLANK)) {
            postcode = "";
        }
        if (StringUtils.isBlank(addressLine1)) {
            addressLine1 = testData.dataFactory.getAddress();
        } else if (addressLine1.equals(BLANK)) {
            addressLine1 = "";
        }
        if (StringUtils.isBlank(addressLine2)) {
            addressLine2 = testData.dataFactory.getAddressLine2();
        } else if (addressLine2.equals(BLANK)) {
            addressLine2 = "";
        }
        if (StringUtils.isBlank(city)) {
            city = testData.dataFactory.getRandomText(6);
        } else if (city.equals(BLANK)) {
            city = "";
        }
        registerCustomerRequestBody = RegisterCustomerRequestBody.builder()
                .personalDetails(
                        PersonalDetails.builder()
                                .age(26)
                                .email("success" + name + "_" + testData.dataFactory.getNumberText(10) + "@abctest.com")
                                .firstName(name)
                                .lastName(testData.dataFactory.getLastName())
                                .phoneNumber(testData.dataFactory.getNumberText(12))
                                .title("mr")
                                .ejPlusCardNumber("")
                                .nifNumber("")
                                .flightClubId("")
                                .flightClubExpiryDate("")
                                .password(testData.dataFactory.getRandomChars(15))
                                .build()
                ).contactAddress(
                        new ArrayList<ContactAddress>() {{
                            add(ContactAddress.builder()
                                    .addressLine1(addressLine1)
                                    .addressLine2(addressLine2)
                                    .postalCode(postcode)
                                    .city(city)
                                    .country("GBR")
                                    .build());
                        }}
                ).optedOutMarketing(
                        new ArrayList<String>() {{
                            add("OPT_OUT_EJ_COMMUNICATION");
                        }}
                );
        testData.setData(REGISTER_CUSTOMER_REQUEST, registerCustomerRequestBody.build());
    }

    private void invokeRegisterCustomerService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        registerCustomerService = serviceFactory.registerCustomer(new RegisterNewCustomerRequest(headers.build(), registerCustomerRequestBody.build()));
        testData.setData(SERVICE, registerCustomerService);
        registerCustomerService.invoke();
    }

    @Step("Register customer")
    @Given("^I created a(?: (staff))? customer$")
    public void sendRegisterCustomerRequest(String isStaff) {
        // TODO check if it is needed to wait up to 20 seconds for the create customer to be completed
        pollingLoop().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
            setRequestBody();
            invokeRegisterCustomerService();
            if (Objects.isNull(registerCustomerService.getErrors())) {
                assertThat(registerCustomerService.getResponse().getAdditionalInformations().stream()
                        .map(AdditionalInformation::getCode)
                        .collect(Collectors.toList()))
                        .contains(SVC_100047_2038);
                testData.setData(CUSTOMER_ID, registerCustomerService.getResponse().getRegistrationConfirmation().getCustomerId());
                testData.setData(CUSTOMER_ACCESS_TOKEN, registerCustomerService.getResponse().getRegistrationConfirmation().getAuthentication().getAccessToken());
                if (StringUtils.isNotBlank(isStaff) && (isStaff.equalsIgnoreCase("staff") || isStaff.equalsIgnoreCase("standby"))) {
                    registerStaffFaresSteps.sendRegisterStaffFaresRequest();
                }
            } else {
                assertThat(registerCustomerService.getErrors().getErrors().stream()
                        .map(Errors.Error::getCode)
                        .collect(Collectors.toList()))
                        .doesNotContain(SVC_100047_2031);
            }
        });
    }

    @And("^I want to create a customer profile with field (postcode|address line 1|address line 2|city) as (.*)$")
    public void iWantToCreateACustomerProfileWithFieldFieldAsInvalidValue(String field, String value) {
        switch (field.toLowerCase()) {
            case POSTCODE: {
                postcode = value;
                break;
            }
            case ADDRESS_LINE_1: {
                addressLine1 = value;
                break;
            }
            case ADDRESS_LINE_2: {
                addressLine2 = value;
                break;
            }
            case CITY: {
                city = value;
                break;
            }
        }
    }
}