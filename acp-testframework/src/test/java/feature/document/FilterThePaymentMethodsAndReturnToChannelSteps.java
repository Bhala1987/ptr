package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.PaymentModeDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.database.hybris.models.PaymentModeModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.alei.invokers.ALHeaders;
import com.hybris.easyjet.fixture.alei.invokers.queryparams.EIPaymentMethodsQueryParameters;
import com.hybris.easyjet.fixture.alei.invokers.requests.EIPaymentMethodsRequest;
import com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods.PaymentType;
import com.hybris.easyjet.fixture.alei.invokers.services.factories.EIServiceFactory;
import com.hybris.easyjet.fixture.alei.invokers.services.impl.EIPaymentMethodsService;
import com.hybris.easyjet.fixture.hybris.asserters.models.ExpectedPaymentMethod;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.PaymentMethodsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.PaymentMethodsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;


/**
 * Created by marco on 20/04/17.
 */
@ContextConfiguration(classes = TestApplication.class)

public class FilterThePaymentMethodsAndReturnToChannelSteps {

    private static final String X_CLIENT_TRANSACTION_ID = "e6a2034e-1291-43b0-bbc3-a71385f324f3";
    private static final Map<String, String> CHANNEL_MAPPING;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("Digital", "Web");
        map.put("ADAirport", "Airport");
        map.put("PublicApiB2B", "B2B");
        map.put("PublicApiMobile", "Mobile");
        map.put("ADCustomerService", "CallCentre");
        CHANNEL_MAPPING = map;
    }

    @Autowired
    private SerenityFacade testData;

    @Autowired
    private HybrisServiceFactory hybrisServiceFactory;
    @Autowired
    private EIServiceFactory eiServiceFactory;

    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private CustomerProfileHelper customerProfileHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private TravellerHelper travellerHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private PaymentMethodHelper paymentMethodHelper;

    @Autowired
    private PaymentModeDao paymentModeDao;

    private PaymentMethodsService paymentMethodsService;
    private CustomerProfileService customerProfileService;
    private EIPaymentMethodsService eiPaymentMethodsService;

    private CustomerModel dbCustomer;
    private String channelUsed;
    private List<ExpectedPaymentMethod> paymentModes;
    private List<PaymentModeModel> paymentModeModels;
    private List<String> list;
    private String country;
    private List<PaymentType> paymentTypes;
    private List<PaymentType> paymentTypeListBasedOnCurrency;
    private PaymentType paymentTypeWithAllowedDepartureDate;
    private List<String> criterias;

    @Given("^that I have all the payment types defined in the back office$")
    public void thatIHaveAllThePaymentTypesDefinedInTheBackOffice() throws Throwable {
        paymentModeModels = paymentModeDao.getBackOfficePaymentModes(true);

    }

    @When("^I receive the response from the payment service$")
    public void iReceiveTheResponseFromThePaymentService() throws Throwable {
        customerHelper.createRandomCustomer(testData.getChannel());
        paymentMethodsService = hybrisServiceFactory
                .getPaymentMethods(new PaymentMethodsRequest(HybrisHeaders.getValid(testData.getChannel()).build()));
        paymentMethodsService.invoke();
    }

    @Then("^I will filter out any payment types which are not set up in the back office$")
    public void iWillFilterOutAnyPaymentTypesWhichAreNotSetUpInTheBackOffice() throws Throwable {
        paymentMethodsService.assertThat().paymentModesReturnedAreIn(paymentModeModels);
    }

    @Given("^I have all the payment types for (.*)$")
    public void iHaveAllThePaymentTypesFor(String channel) throws Throwable {
        channelUsed = channel;
        EIPaymentMethodsQueryParameters queryParameters = EIPaymentMethodsQueryParameters.builder()
                .channel(CHANNEL_MAPPING.get(channel)).build();
        EIPaymentMethodsRequest request = new EIPaymentMethodsRequest(ALHeaders.getValid().xClientTransactionId(X_CLIENT_TRANSACTION_ID)
                .xApplicationId("12345")
                .xAcceptCharSet("UTF-8")
                .build(), queryParameters);
        eiPaymentMethodsService = eiServiceFactory.getEiPaymentMethods(request);
        // after invoking the service, in its response we can find all payment methods for the given channel
        eiPaymentMethodsService.invoke();


    }

    @When("^I call the service to retrieve the payment methods$")
    public void iCallTheServiceToRetrievePaymentMethods() throws Throwable {
        customerHelper.createRandomCustomer(channelUsed);
        paymentMethodsService = hybrisServiceFactory
                .getPaymentMethods(new PaymentMethodsRequest(HybrisHeaders.getValid(channelUsed)
                        .build()));
        paymentMethodsService.invoke();
    }

    @When("^I call the service to retrieve \"([^\"]*)\" payment methods$")
    public void iCallTheServiceRetrievePaymentMethods(String channel) throws Throwable {
        customerHelper.createRandomCustomer(channel);
        paymentMethodsService = hybrisServiceFactory
                .getPaymentMethods(new PaymentMethodsRequest(HybrisHeaders.getValid(channel)
                        .build()));
        paymentMethodsService.invoke();
    }

    @Then("^the applicable payment methods for \"([^\"]*)\" are returned$")
    public void theApplicablePaymentMethodsForAreReturned(String channel) throws Throwable {
        paymentMethodsService.assertThat().applicablePaymentMethodAreReturnedToChannel(eiPaymentMethodsService.getResponse().getPaymentMethodsResponse().getPaymentMethods());
    }

    @Given("^I have valid basket with passengers via \"([^\"]*)\"$")
    public void iHaveAValidBasketWithPassengersAndAssociatedCustomerCreatedVia(String channel) throws Throwable {
        channelUsed = channel;
        paymentMethodHelper.getBasket(channel, "1 Adult");
    }

    @Then("^I will return the available payment methods to channel$")
    public void iWillReturnTheAvailablePaymentMethodsToChannel() throws Throwable {
        paymentMethodsService.assertThat().applicablePaymentMethodAreReturned();
    }


    @And("^I have valid basket with booking type as (.*) for (.*)")
    public void iHaveValidBasketWithFor(String basketType, String channel) throws Throwable {
        paymentMethodHelper.createBasketBasedOnBookingType(basketType, channel, "1 Adult");
    }

    @Then("^I will received the payments methods \"([^\"]*)\" based on booking type \"([^\"]*)\" in the basket$")
    public void iWillReceivedThePaymentsMethodsBasedOnBookingTypeInTheBasket(String allowedPaymentMethod, String channel) throws Throwable {
        list = Arrays.asList(allowedPaymentMethod.split(","));
        paymentMethodsService.assertThat().returnedPaymentMethodBasedOnBasketType(list);
    }

    @When("^I request the payment methods with booking type as (.*)$")
    public void iRequestThePaymentMethodsBasedOn(String bookingType) throws Throwable {
        customerHelper.createRandomCustomer(channelUsed);
        PaymentMethodsQueryParams paymentMethodsQueryParams = PaymentMethodsQueryParams.builder()
                .bookingType(bookingType).build();
        paymentMethodsService = hybrisServiceFactory
                .getPaymentMethods(new PaymentMethodsRequest(HybrisHeaders.getValid(channelUsed).build(), paymentMethodsQueryParams));
        paymentMethodsService.invoke();
    }

    @When("^I request the payment methods with country as (.*)$")
    public void iRequestThePaymentMethodsBasedOnCountry(String country) throws Throwable {
        this.country = country;
        customerHelper.createRandomCustomer(channelUsed);
        PaymentMethodsQueryParams paymentMethodsQueryParams = PaymentMethodsQueryParams.builder()
                .bookingType(basketHelper.getBasketService().getResponse().getBasket().getBasketType()).build();
        paymentMethodsService = hybrisServiceFactory
                .getPaymentMethods(new PaymentMethodsRequest(HybrisHeaders.getValid(channelUsed)
                        .xCountry(country)
                        .build(), paymentMethodsQueryParams));
        paymentMethodsService.invoke();
    }

    @And("^response should not have payment methods (.*) for booking type$")
    public void responseShouldNotHavePaymentMethodsForBookingType(String paymentMethods) throws Throwable {
        list = Arrays.asList(paymentMethods.split(","));
        paymentMethodsService.assertThat().shouldNotReturnedPaymentMethods(list);
        if (testData.keyExist(CUSTOMER_ID)) {
            paymentMethodHelper.deleteCustomer( testData.getData(CUSTOMER_ID));
        }
    }

    @And("^the payment type (.*) has an value set for allowedDaysTillDeparture$")
    public void thePaymentTypeHasAnValueSetForAllowedDaysTillDeparture(String cardType) throws Throwable {
        paymentTypes = eiPaymentMethodsService.getResponse().getPaymentMethodsResponse().getPaymentMethods().stream()
                .flatMap(paymentMethod -> paymentMethod.getPaymentType().stream())
                .filter(paymentType -> paymentType.getAllowedDaysTillDeparture() != null && Integer.valueOf(paymentType.getAllowedDaysTillDeparture()) > 0)
                .filter(paymentType -> paymentType.getCode().equals(cardType))
                .collect(Collectors.toList());
    }

    @Then("^I should get the payment types based on country$")
    public void iShouldGetThePaymentTypesBasedOnCountry() throws EasyjetCompromisedException {
        paymentTypes = eiPaymentMethodsService.getResponse().getPaymentMethodsResponse().getPaymentMethods().stream()
                .filter(paymentType -> paymentType != null)
                .flatMap(paymentType -> paymentType.getPaymentType().stream())
                .filter(paymentType -> paymentType.getAllowedMarketCountryCode() == null || paymentType.getAllowedMarketCountryCode().equalsIgnoreCase(country) || paymentType.getAllowedMarketCountryCode() == "")
                .collect(Collectors.toList());
        if (paymentTypes.size() == 0)
            throw new EasyjetCompromisedException("There are no payment methods");
        else
            paymentMethodsService.assertThat().shouldReturnCorrectPaymentTypes(paymentTypes);
    }

    @Then("^I will filter out any payment types based on the currency set in the basket$")
    public void iWillFilterOutAnyPaymentTypesBasedOnTheCurrencySetInTheBasket() throws Throwable {
        paymentMethodsService.assertThat().shouldReturnCorrectPaymentTypes(paymentTypeListBasedOnCurrency);
    }

    @And("^I have the allowed currencies set for each payment type$")
    public void iHaveTheAllowedCurrenciesSetForEachPaymentType() throws Throwable {
        paymentTypeListBasedOnCurrency = eiPaymentMethodsService.getResponse().getPaymentMethodsResponse().getPaymentMethods().stream()
                .flatMap(paymentType -> paymentType.getPaymentType().stream())
                .filter(paymentType -> paymentType.getAllowedCurrencies().getCurrency() == null || paymentType.getAllowedCurrencies().getCurrency().size() == 0
                        || paymentType.getAllowedCurrencies().getCurrency()
                        .stream().anyMatch(currency -> currency.getCode().equals(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode())))
                .collect(Collectors.toList());
        if (paymentTypeListBasedOnCurrency.size() == 0)
            throw new EasyjetCompromisedException("There no no matching payment methods");
    }

    @And("^I have valid basket with flight departing after allowed days till departure$")
    public void iHaveValidBasketWithFlightDepartingBeforeAllowedDaysTillDeparture() throws Throwable {
        paymentTypeWithAllowedDepartureDate = paymentTypes.get(0);
        paymentMethodHelper.createBasketBasedOoMaxDaysTillDeparture(Integer.valueOf(paymentTypeWithAllowedDepartureDate.getAllowedDaysTillDeparture()), channelUsed);
    }

    @And("^I get the payment type with allowedDaysTillDeparture$")
    public void iGetThePaymentTypeWithAllowedDaysTillDeparture() throws Throwable {
        paymentTypes = eiPaymentMethodsService.getResponse().getPaymentMethodsResponse().getPaymentMethods().stream()
                .flatMap(paymentMethod -> paymentMethod.getPaymentType().stream())
                .filter(paymentType -> paymentType.getAllowedDaysTillDeparture() != null)
                .collect(Collectors.toList());

    }

    @And("^filter out payment methods which are less than the allowedDays till Departure$")
    public void filterOutThoseWhichAreLessThanTheAllowedDaysTillDeparture() throws Throwable {
        if (paymentTypes.size() == 0)
            throw new EasyjetCompromisedException("There are no payment methods");
        else
            paymentMethodsService.assertThat().shouldNotReturnedPaymentMethod(paymentTypes);
    }

    @When("^I send request to payment service with filter criteria (.*)$")
    public void iSendRequestToPaymentServiceWithSearchCriteria(String criteria) throws Throwable {
        // Write type here that turns the phrase above into concrete actions
        criterias = Arrays.asList(criteria.split(","));
        paymentMethodHelper.involePaymentService(criterias, channelUsed);
        paymentMethodsService = paymentMethodHelper.getPaymentMethodsService();

    }

    public List<PaymentType> getUpdatedResponseFromEIService(List<String> criterias) {
        for (String criteria : criterias
                ) {
            paymentTypes = eiPaymentMethodsService.getResponse().getPaymentMethodsResponse().getPaymentMethods().stream()
                    .flatMap(paymentMethod -> paymentMethod.getPaymentType().stream())
                    .collect(Collectors.toList());

            switch (criteria) {
                case "country":
                    paymentTypes = paymentTypes.stream().filter(paymentType -> paymentType.getAllowedMarketCountryCode() != null && paymentType.getAllowedMarketCountryCode().equalsIgnoreCase("GBR"))
                            .collect(Collectors.toList());
                    break;
                case "currency":
                    paymentTypes.stream()
                            .filter(paymentType -> paymentType.getAllowedCurrencies().getCurrency() == null || paymentType.getAllowedCurrencies().getCurrency().size() == 0
                                    || paymentType.getAllowedCurrencies().getCurrency()
                                    .stream().anyMatch(currency -> currency.getCode().equals(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode())))
                            .collect(Collectors.toList());
                    break;
            }
        }
        return paymentTypes;

    }

    @And("^response should have the payment menthod as (.*)$")
    public void responseShouldHaveThePaymentMenthodAs(String paymentMethods) throws Throwable {
        list = Arrays.asList(paymentMethods.split(","));
        paymentMethodsService.assertThat().returnedPaymentMethodBasedOnBasketType(list);
    }

    @And("^I add flight departing (.*) allowed days till departure to my basket with fare type (.*)$")
    public void iAddFlightDepartingAllowedDaysTillDepartureToMyBasketWithFareType(String condition, String fareType) throws Throwable {
        paymentMethodHelper.createBasketBasedOnMaxDaysTillDeparture(fareType);
    }

    @And("^I search for flight departing \"([^\"]*)\" the allowedDaysTillDeparture with following details$")
    public void iSearchForFlightDepartingTheAllowedDaysTillDeparture(String condition, Map<String, String> data) throws Throwable {
        paymentTypeWithAllowedDepartureDate = paymentTypes.get(0);
        paymentMethodHelper.searchFlights(channelUsed, data.get("passengerMix"), data.get("origin"), data.get("destination"), data.get("journey"), Integer.valueOf(paymentTypeWithAllowedDepartureDate.getAllowedDaysTillDeparture()));
    }

    @Then("^I \"([^\"]*)\" have the payment method as EV$")
    public void iHaveThePaymentMethodAsEV(String condition) throws Throwable {
        if (condition.equalsIgnoreCase("should")) {
            paymentMethodsService.assertThat().shouldContainExpectedPaymentType(paymentTypes);
        } else
            paymentMethodsService.assertThat().shouldNotReturnedPaymentMethod(paymentTypes);
    }

    @Then("^I (.*) have the payment method (.*)$")
    public void iHaveThePaymentMethod(String condition, String paymentCode) throws Throwable {
        paymentTypeListBasedOnCurrency = eiPaymentMethodsService.getResponse().getPaymentMethodsResponse().getPaymentMethods().stream()
                .flatMap(paymentType -> paymentType.getPaymentType().stream())
                .filter(paymentType -> paymentType.getAllowedCurrencies().getCurrency() == null || paymentType.getAllowedCurrencies().getCurrency().size() == 0
                        || paymentType.getAllowedCurrencies().getCurrency()
                        .stream().anyMatch(currency -> currency.getCode().equals(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getCode())))
                .filter(paymentType -> paymentType.getAllowedMarketCountryCode() == null || paymentType.getAllowedMarketCountryCode().equals("") || paymentType.getAllowedMarketCountryCode().equalsIgnoreCase(country))
                .filter(paymentType -> paymentType.getCode().equalsIgnoreCase(paymentCode))
                .collect(Collectors.toList());
        if (condition.equalsIgnoreCase("should")) {

            paymentMethodsService.assertThat().shouldReturnedPaymentMethod(paymentTypeListBasedOnCurrency);
        } else
            paymentMethodsService.assertThat().shouldNotReturnedPaymentMethod(paymentTypeListBasedOnCurrency);
    }

    @Then("^I should get the payment types based on filter as (.*)$")
    public void iShouldGetThePaymentTypesBasedOnFilterAs(String filter) throws Throwable {
        paymentMethodsService.assertThat().shouldContainExpectedPaymentType(getUpdatedResponseFromEIService(Arrays.asList(filter.split(","))));
    }
}

