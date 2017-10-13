package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.BookingPermissionDao;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.UpdatePassengerDetailsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AmendBasicDetailsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AmendBasicDetailsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.AmendBasicDetailsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.Getter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;


/**
 * Created by premkumar 26/09/2017
 */
@ContextConfiguration(classes = TestApplication.class)

public class ChangePassengerBookingPermissionSteps {
    public static final String L_NAME = "lName";
    protected static Logger LOG = LogManager.getLogger(ManageBookingSteps.class);
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private ManageBookingHelper manageBookingHelper;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private PurchasedSeatHelper purchasedSeatHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    AmendableBasketHelper amendableBasketHelper;
    private BasketPathParams basketPathParams;
    private AmendBasicDetailsRequestBody.AmendBasicDetailsRequestBodyBuilder amendBasicDetailsRequestBody;
    private AmendBasicDetailsService amendBasicDetailsService;
    @Autowired
    private MembershipDao membershipDao;

    private String passengerCode;
    private String expectedPassengerType;
    private int expectedPassengerAge;
    private String allRelatedFlights = "false";
    private String bookingRef;
    private Double originalAdminFee;

    @Getter
    private CommitBookingService commitBookingService;
    @Autowired
    private BookingPermissionDao bookingPermissionDao;


    @And("^I change the passenger \"([^\"]*)\",\"([^\"]*)\" and \"([^\"]*)\"$")
    public void iChangeThePassengerAnd(String fName, String lName, String age) throws Throwable {
        basketHelper.getBasket(testData.getBasketId());
        Basket.Passenger passenger = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream()).flatMap(pgs -> pgs.getPassengers().stream()).findFirst().orElse(null);
        //buildRequestBody(mapFields);
        setRequestPathParameter(passenger);
        invokeUpdateBasicDetailsService();
        testData.setData(SerenityFacade.DataKeys.SAVED_PASSENGER_CODE, passenger.getCode());
    }

    private void retryInvoke() {
        int[] noOfRetrys = {5};
        try {
            pollingLoop().until(() -> {
                amendBasicDetailsService.invoke();
                noOfRetrys[0]--;
                return amendBasicDetailsService.getStatus() == 200 || noOfRetrys[0] == 0;
            });
        } catch (ConditionTimeoutException ct) {
            amendBasicDetailsService.getRestResponse();
        }
    }

    private void setRequestPathParameter(Basket.Passenger passenger) {
        basketPathParams = BasketPathParams.builder().basketId(testData.getData(BASKET_ID)).passengerId(passenger.getCode())
                .path(BasketPathParams.BasketPaths.UPDATE_BASIC_DETAILS).build();
    }

    private String getRandomFieldFromList(List<String> fields) {
        Random random = new Random();
        return fields.get(random.nextInt(fields.size()));
    }


    @And("^the action is not allowed for the (.*)$")
    public void theActionIsNotAllowedForThe(String error) throws Throwable {


    }

    @And("^I will check an access for the (.*)$")
    public void iWillCheckAnAccessForThe(String channel) throws Throwable {


    }

    @Then("^I will determine the booking is not editable with an \"([^\"]*)\" error message$")
    public void iWillDetermineTheBookingIsNotEditableWithAnErrorMessage(String error) throws Throwable {

    }

    @And("^I update below details for couple of passenger with \"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\"$")
    public void iUpdateBelowDetailsForCoupleOfPassengerWith(String channel, String bookingType, String accessType, List<String> fields)
            throws Throwable {
        basketHelper.getBasket(testData.getBasketId());

        List<Basket.Passenger> allPassengersList = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream()).flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        List<Basket.Passenger> updatePassengers = allPassengersList.subList(1, 3);
        updatePassengers.forEach(passenger -> {
            setRequestPathParameter(passenger);
            try {
                buildRequestBody(fields, passenger.getCode());
            } catch (EasyjetCompromisedException e) {
                e.printStackTrace();
            }

            String origChannel = testData.getChannel();
            testData.setChannel(channel);
            testData.setBookingType(bookingType);
            testData.setAccessType(accessType);
            invokeUpdateBasicDetailsService();
            testData.setChannel(origChannel);

        });

        testData.setData(SerenityFacade.DataKeys.PASSENGER_LIST, updatePassengers);
    }


    @And("^I update below details with for couple of passenger$")
    public void iUpdateBelowDetailsWithForCoupleOfPassenger(List<String> fields) throws Throwable {
        basketHelper.getBasket(testData.getBasketId());
        List<Basket.Passenger> allPassengersList = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream()).flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        List<Basket.Passenger> updatePassengers = allPassengersList.subList(1, 3);
        updatePassengers.forEach(passenger -> {
            try {
                buildRequestBody(fields, passenger.getCode());
            } catch (EasyjetCompromisedException e) {

            }
            setRequestPathParameter(passenger);

            invokeUpdateBasicDetailsService();
        });

        testData.setData(SerenityFacade.DataKeys.PASSENGER_LIST, updatePassengers);


    }

    private void buildRequestBody(List<String> fieldsList, String passengerCode) throws EasyjetCompromisedException {

        amendBasicDetailsRequestBody = AmendBasicDetailsRequestBody.builder();

        Map<String, Object> fieldsMap = new HashMap<>();
        MemberShipModel memberShipModel = membershipDao.getEJPlusMemberBasedOnStatus("COMPLETED");
        Name name;
        int counter = 1;
        for (String field : fieldsList) {
            fieldsMap.put("passengerCode" + counter, passengerCode);
            counter++;
            if ("name".equalsIgnoreCase(field)) {
                name = Name.builder().firstName("UpdatedfirstName").lastName("UpdatedlastName").build();
                amendBasicDetailsRequestBody.name(name);
                fieldsMap.put("name", name);
                fieldsMap.put("firstName", "UpdatedfirstName");
                fieldsMap.put("lastName", "UpdatedlastName");

            } else if ("age".equalsIgnoreCase(field)) {
                int age = 50;
                amendBasicDetailsRequestBody.age(age);
                fieldsMap.put("age", age);
            } else if ("email".equalsIgnoreCase(field)) {
                String email = "updatedemail@portaltech.com";
                amendBasicDetailsRequestBody.email(email);
                fieldsMap.put("email", email);
            } else if ("phonenumber".equalsIgnoreCase(field)) {
                String phNumber = "0999999999";
                amendBasicDetailsRequestBody.phoneNumber(phNumber);
                fieldsMap.put("phonenumber", phNumber);
            }

        }

        testData.setData(SerenityFacade.DataKeys.SESSION, fieldsMap);


    }

    private void invokeUpdateBasicDetailsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = HybrisHeaders.getValid(testData.getChannel());
        testData.setData(HEADERS, headers);
        amendBasicDetailsService = serviceFactory.amendBasicDetails(new AmendBasicDetailsRequest(headers.build(), basketPathParams,
                UpdatePassengerDetailsQueryParams.builder().operationTypeUpdate("UPDATE").build(), amendBasicDetailsRequestBody.build()));
        testData.setData(SERVICE, amendBasicDetailsService);
        retryInvoke();
        amendBasicDetailsService.getRestResponse();
    }

    private void invokeUpdateBasicDetailsService(String channel) {
        HybrisHeaders.HybrisHeadersBuilder headers = HybrisHeaders.getValid(channel);
        testData.setData(HEADERS, headers);
        amendBasicDetailsService = serviceFactory.amendBasicDetails(new AmendBasicDetailsRequest(headers.build(), basketPathParams,
                UpdatePassengerDetailsQueryParams.builder().operationTypeUpdate("UPDATE").build(), amendBasicDetailsRequestBody.build()));
        testData.setData(SERVICE, amendBasicDetailsService);
        retryInvoke();

    }


    @Then("^I will determine the booking is not editable for the channel \"([^\"]*)\"$")
    public void iWillDetermineTheBookingIsNotEditableForTheChannel(String channel) throws Throwable {
        if (Objects.nonNull(amendBasicDetailsService.getErrors())) {
            amendBasicDetailsService.assertThatErrors().containedTheCorrectErrorMessage("SVC_100123_1001");
        }

    }

    @Then("^I will determine the booking is editable for the action$")
    public void iWillDetermineTheBookingIsEditableForTheAction() throws Throwable {
        Map<String, Object> fieldsMap = testData.getData(SerenityFacade.DataKeys.SESSION);

        basketHelper.getBasketService().assertThat()
                .theBasketContainsUpdatedPassengerDetails(basketHelper.getBasketService().getResponse().getBasket(), fieldsMap);

    }

    @When("^I commit a booking with <fareType> fare and <passenger> passenger and <login> login$")
    public void iCommitABookingWithFareTypeFareAndPassengerPassengerAndLoginLogin() throws Throwable {
        // 		BasketsResponse basketsResponse=basketHelper.getBasketService().getResponse();

        throw new PendingException();
    }


    @And("^attempt to update below details with (.*),(.*),(.*) for couple of passenger$")
    public void attemptToUpdateBelowDetailsWithForCoupleOfPassenger(String channel, String bookingtype, String accesstype, List<String> fields)
            throws Throwable {
        basketHelper.getBasket(testData.getBasketId());
        List<Basket.Passenger> allPassengersList = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().stream()
                .flatMap(obs -> obs.getFlights().stream()).flatMap(pgs -> pgs.getPassengers().stream()).collect(Collectors.toList());
        List<Basket.Passenger> updatePassengers = allPassengersList.subList(1, 3);
        updatePassengers.forEach(passenger -> {
            try {
                buildRequestBody(fields, passenger.getCode());
            } catch (EasyjetCompromisedException e) {

            }
            setRequestPathParameter(passenger);

            invokeUpdateBasicDetailsService(channel);
        });

    }

    @And("^I get the amendable basket with updated details$")
    public void iGetTheAmendableBasketWithUpdatedDetails() throws Throwable {
        basketHelper.getBasket(testData.getData(SerenityFacade.DataKeys.BASKET_ID));
    }

    @Then("^I will determine the booking is editable and infantonlap to passenger$")
    public void iWillDetermineTheBookingIsEditableAndInfantonlapToPassenger() throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        basketHelper.getBasketService().assertThat().infantIsNowOnLapOfFirstAdult();

    }

    @Then("^booking should have added with APIs$")
    public void bookingShouldHaveAddedWithAPIs() throws Throwable {

        bookingHelper.getGetBookingService().assertThat().thePassengerContainsAPISDetails(basketHelper.getBasketService().getResponse());
    }

    @Then("^I get the amendable basket with (.*) SSR$")
    public void iGetTheAmendableBasketWithSSR(String ssrCode) throws Throwable {
        basketHelper.getBasket(testData.getBasketId(), testData.getChannel());
        basketHelper.getBasketService().assertThat().checkAddSSRInBasket(basketHelper.getBasketService().getResponse().getBasket(), ssrCode);
    }
}
