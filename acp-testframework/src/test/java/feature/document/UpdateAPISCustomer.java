package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.SetAPIHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetApiBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PassengerApisFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetAPIsForCustomerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.SetAPIRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetCustomerAPIsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.IdentityDocument;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetCustomerAPIsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.SetApisBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.constants.CommonConstants.STANDARD;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.SET_APIS_BOOKING;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.APIS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by giuseppecioce on 07/08/2017.
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdateAPISCustomer {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BookingHelper bookingHelper;
    private SetApisBookingService setAPIsService;
    private GetCustomerAPIsService getCustomerAPIsService;
    private IdentityDocument identityDocument;
    @Autowired
    SetAPIHelper setAPIHelper;
    @Autowired
    BasketHelper basketHelper;
    @When("^I send a valid request to set new apis to customer profile$")
    public void iSendAValidRequestToSetNewApisToCustomerProfile() throws Throwable {
        bookingHelper.createBookingAndGetAmendable(testData.getPassengerMix(), STANDARD, true);
        retrieveAPIForCustomer("ORIGINAL_CUSTOMER_APIS_SIZE");

        createOrUpdateAPI(null, false);
        retrieveAPIForCustomer("MODIFIED_CUSTOMER_APIS_SIZE");
    }

    /**
     * Create or update identity document for a specific passenger
     * @param iRequestBody identity document to add (new or modified one)
     * @param update boolean to request to update
     */
    private void createOrUpdateAPI(IRequestBody iRequestBody, boolean update) {
        BookingPathParams params = BookingPathParams.builder().bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID)).passengerId(testData.getPassengerId()).path(SET_APIS_BOOKING).build();
        SetApiBookingRequestBody requestBody;
        if (Objects.isNull(iRequestBody)) {
            requestBody = PassengerApisFactory.aBasicBookingPassengerApis();
            requestBody.setAddToSavedPassengerCode(testData.getData(CUSTOMER_ID));
        } else {
            requestBody = (SetApiBookingRequestBody) iRequestBody;
        }
        SetAPIRequest request = update ? new SetAPIRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), params, null, requestBody) : new SetAPIRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), params, requestBody);
        setAPIsService = serviceFactory.setApisBooking(request);
        final int[] attempts = {3};
        pollingLoop().until(() -> {
            setAPIsService.invoke();
            attempts[0]--;

            return setAPIsService.getStatusCode() == 200 || attempts[0] == 0;
        });
        setAPIsService.getResponse();
    }

    /**
     * Retrieve the list of identity document for a specific customer and store the size of it
     * @param key key associated to the map
     */
    private void retrieveAPIForCustomer(String key) {
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(APIS).build();
        getCustomerAPIsService = serviceFactory.getCustomerAPIs(new GetAPIsForCustomerRequest((HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build()), params));
        final int[] attempts = {3};
        pollingLoop().until(() -> {
            getCustomerAPIsService.invoke();
            attempts[0]--;

            GetCustomerAPIsResponse apisResponse = (GetCustomerAPIsResponse) getCustomerAPIsService.getResponse();
            testData.setData(key, apisResponse.getIdentityDocuments().size());
            return getCustomerAPIsService.getStatus() == 200 || attempts[0] == 0;
        });
    }

    @Then("^new apis document should (.*) be created against the customer$")
    public void newApisDocumentShouldBeCreatedAgainstTheCustomer(boolean should) throws Throwable {
        if(should) {
            final int[] attempts = {3};
            try {
                pollingLoop().until(() -> {
                    retrieveAPIForCustomer("MODIFIED_CUSTOMER_APIS_SIZE");
                    attempts[0]--;
                    return ((Integer) testData.getData("ORIGINAL_CUSTOMER_APIS_SIZE") < (Integer) testData.getData("MODIFIED_CUSTOMER_APIS_SIZE"))
                            || attempts[0] == 0;
                });
            } catch(Exception e) { } // keep on the flow
            getCustomerAPIsService.assertThat().verifyNewAPISHasBeenCreated((Integer) testData.getData("ORIGINAL_CUSTOMER_APIS_SIZE"), (Integer) testData.getData("MODIFIED_CUSTOMER_APIS_SIZE"));
        } else {
            final int[] attempts = {3};
            try {
                pollingLoop().until(() -> {
                    retrieveAPIForCustomer("MODIFIED_CUSTOMER_APIS_SIZE");
                    attempts[0]--;
                    return (testData.getData("ORIGINAL_CUSTOMER_APIS_SIZE") == testData.getData("MODIFIED_CUSTOMER_APIS_SIZE"))
                            || attempts[0] == 0;
                });
            } catch(Exception e) { } // keep on the flow
            getCustomerAPIsService.assertThat().verifyNotUpdateAPIS((Integer) testData.getData("ORIGINAL_CUSTOMER_APIS_SIZE"), (Integer) testData.getData("MODIFIED_CUSTOMER_APIS_SIZE"));
        }
    }

    @And("^the travel document has been retrieved for a customer profile$")
    public void theTravelDocumentHasBeenRetrievedForACustomerProfile() throws Throwable {
        bookingHelper.createBookingAndGetAmendable(testData.getPassengerMix(), STANDARD, true);
        createOrUpdateAPI(null, false);
        final GetCustomerAPIsResponse[] apisResponse = new GetCustomerAPIsResponse[1];
        final int[] attempts = {3};
        try {
            pollingLoop().until(() -> {
                retrieveAPIForCustomer("ORIGINAL_CUSTOMER_APIS_SIZE");
                attempts[0]--;
                apisResponse[0] = (GetCustomerAPIsResponse) getCustomerAPIsService.getResponse();
                return apisResponse[0].getIdentityDocuments().size() > 0 || attempts[0] == 0;
            });
        } catch(Exception e) {
            assertThat(apisResponse[0].getIdentityDocuments().size())
                    .withFailMessage("No identity document against the customer profile after added")
                    .isEqualTo(0);
        } // keep on the flow
        identityDocument = apisResponse[0].getIdentityDocuments().get(0); // only 1 travel document at this point against customer profile
    }

    @When("^I send a valid request to amend apis to customer profile (.*) modification$")
    public void iSendAValidRequestToAmendApisToCustomerProfileWithModification(boolean isModifying) throws Throwable {
        SetApiBookingRequestBody requestBody = PassengerApisFactory.aBasicBookingPassengerApisFromIdentityDocument(identityDocument, testData.getData(CUSTOMER_ID));
        if(isModifying) {
            requestBody.getApi().setDocumentExpiryDate(modifyRequestForData(requestBody.getApi().getDocumentExpiryDate()));
            createOrUpdateAPI(requestBody, true);
        } else {
            createOrUpdateAPI(requestBody, true);
        }
        retrieveAPIForCustomer("MODIFIED_CUSTOMER_APIS_SIZE");
    }

    /**
     * Modify the identity document (EXPIRED DATE FIELD) for a customer
     * @param originalDate
     * @return the new expired date (year += 1)
     * @throws ParseException
     */
    private String modifyRequestForData(String originalDate) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        Date startDate = df.parse(originalDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.YEAR, 1);
        df.setCalendar(cal);
        return df.format(cal.getTime());
    }


    @When("^I created an amendable basket for (.*) fare and (.*) passenger without apis$")
    public void iCreatedAnAmendableBasketForStandardFareAndAdultPassengerWithoutApis(String fare,String passengers) throws Throwable
    {
        bookingHelper.createBookingAndGetAmendable(passengers, STANDARD, false);
     }

    @And("^add APIs and recommit booking$")
    public void addAPIsAndRecommitBooking() throws Throwable
    {
        testData.setBookingResponse(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
        testData.setPassengerId(testData.getBookingResponse().getBookingContext().getBooking().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(p -> p.getPassengers().stream()).findFirst().orElseThrow(() -> new IllegalArgumentException("No passenger on booking")).getCode());
        setAPIHelper.invokeUpdateIdentityDocument(false, "");
        testData.setData(SerenityFacade.DataKeys.APIS_DETAILS,setAPIHelper.getRequestBodyApisBooking());
        basketHelper.getBasket(testData.getBasketId());
        bookingHelper.commitBookingFromBasket(basketHelper.getBasketService().getResponse());

    }
}
