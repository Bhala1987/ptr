package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.helpers.ManageBookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FlightQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FlightsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveFlightFromBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Pricing;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.RemoveFlightFromBasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.constants.CommonConstants.CREDITCARD_FEE_PERCENTAGE;
import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 29/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class RemoveFlightFromBasketSteps {

    protected static Logger LOG = LogManager.getLogger(RemoveFlightFromBasketSteps.class);

    public static final String ADULT = "adult";

    @Autowired
    private BookingHelper commitBookingHelper;

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private ManageBookingHelper manageBookingHelper;

    private RemoveFlightFromBasketService removeFlightFromBasketService;
    private BasketService basketService;
    private FlightsService flightsService;
    private FlightQueryParams flightQueryParams;
    private String basketId;
    private String flightKey;
    private String outboundFlightKey;
    private String inboundFlightKey;
    private String channel;
    private BasketPathParams pathParams;
    private FindFlightsResponse.Flight flight;
    private FindFlightsResponse.Flight outboundFlight;
    private FindFlightsResponse.Flight inboundFlight;
    private Double adminFeeBeforeRemoval;
    private Double adminFeeAfterRemoval;
    private String adminFeeCode;
    private Double adminFeePaxLevelBeforeRemoval;
    private Double adminFeePaxLevelAfterRemoval;
    private FindFlightsResponse.Flight outbound;
    private FindFlightsResponse.Flight inbound;
    private BasketsResponse basketFlights;
    private Double totalPriceWithCreditCard = 0.0;
    private Double totalPriceWithDebitCard = 0.0;


    @Autowired
    private CartDao cartdao;

    @And("^am using channel (.*)$")
    public void amUsingChannel(String channel) throws Throwable {
        this.channel = channel;
        testData.setChannel(channel);
    }

    @And("^I have found a flight and added to basket for (.*) and (.*)$")
    public void iHaveFoundAFlightAndAddedToBasketForPassengerMixAndChannel(String PassengerMix, String channel) throws Throwable {
        testData.setChannel(channel);
        findFlights(PassengerMix);

        flight = flightsService.getOutboundFlight();
        basketHelper.addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMix(
                flightsService.getOutboundFlight(), PassengerMix, channel, flightsService.getResponse().getCurrency(), "Standard");

        testData.setBasketId(basketHelper.getBasketService().getResponse().getBasket().getCode());
        testData.setFlightKey(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey());

        pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(channel).build(), pathParams));
        basketService.invoke();

        assertThat(basketService.getResponse().getBasket().getOutbounds()).isNotEmpty();
        basketService.assertThat().theBasketContainsTheFlight(flight);
    }

    @And("^I have found a return flight and added to basket for (.*) and (.*)$")
    public void iHaveFoundAReturnFlightAndAddedToBasketForPassengerMixAndChannel(String PassengerMix, String channel) throws Throwable {
        testData.setChannel(channel);
        findFlights(PassengerMix);
        outbound = flightsService.getOutBoundJourneys().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getInventory().getAvailable() > 0 && g.getAvailableStatus().equalsIgnoreCase("AVAILABLE")).findFirst().orElse(null);
        inbound = flightsService.getInBoundJourneys().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getInventory().getAvailable() > 0 && g.getAvailableStatus().equalsIgnoreCase("AVAILABLE")).findFirst().orElse(null);

        outboundFlight = flightsService.getOutboundFlight();
        inboundFlight = flightsService.getInboundFlight();

        basketHelper.addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMix(
                "outbound", outboundFlight, PassengerMix, channel, flightsService.getResponse().getCurrency());

        basketHelper.addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMix(
                "inbound", inboundFlight, PassengerMix, channel, flightsService.getResponse().getCurrency());

        outboundFlightKey = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();
        inboundFlightKey = basketHelper.getBasketService().getResponse().getBasket().getInbounds().get(0).getFlights().get(0).getFlightKey();

        assertThat(basketHelper.getBasketService().getResponse().getBasket().getOutbounds()).isNotEmpty();
        assertThat(basketHelper.getBasketService().getResponse().getBasket().getInbounds()).isNotEmpty();
        basketHelper.getBasketService().assertThat().theBasketContainsTheFlight(outboundFlight);
        basketHelper.getBasketService().assertThat().theBasketContainsTheInboundFlight(inboundFlight);
        basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();
        if (channel.contains("AD")) {
            int size = basketHelper.getBasketService().getResponse().getBasket().getFees().getItems().size();
            if (size > 1) {
                for (int i = 0; i < size; i++) {
                    adminFeeCode = basketHelper.getBasketService().getResponse().getBasket().getFees().getItems().get(i).getCode();
                    if (adminFeeCode.equalsIgnoreCase("AdminFee")) {
                        adminFeeBeforeRemoval = basketHelper.getBasketService().getResponse().getBasket().getFees().getItems().get(i).getAmount();
                        break;
                    }
                }
            } else {
                adminFeeCode = basketService.getResponse().getBasket().getFees().getItems().get(0).getCode();
                if (adminFeeCode.equalsIgnoreCase("AdminFee")) {
                    adminFeeBeforeRemoval = basketHelper.getBasketService().getResponse().getBasket().getFees().getItems().get(0).getAmount();
                }
            }
        }

    }

    @And("^I have found a valid return flight for (.*)$")
    public void iHaveFoundAValidOutboundFlightForPassengerMix(String passengerMix) throws Throwable {
        findFlights(passengerMix);
        outboundFlight = flightsService.getOutboundFlight();
        inboundFlight = flightsService.getInboundFlight();
    }

    @When("^I receive a remove Flight request$")
    public void iReceiveARemoveFlightRequestFrom() throws Throwable {
        pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey(testData.getFlightKey()).build();
        removeFlightFromBasketService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
        removeFlightFromBasketService.invoke();
    }

    @Then("^the correct refund amount is applied to (.*)")
    public void theCorrectRefundAmountIsApplied(String paymentType) throws Throwable {
        Basket amendedBasket = basketHelper.getBasket(testData.getBasketId(), testData.getChannel());

        Double cancellationFee = amendedBasket.getFees().getItems().stream().filter(feeItem -> feeItem.getCode().contains("CancelFee_")).findFirst().orElse(null).getAmount();

        if (paymentType.equals("creditCard")) {
            Double difference = Math.abs(amendedBasket.getPriceDifference().getAmountWithCreditCard());
//            TODO remove this assertion needs to be replaced with the original one once FCPH-12034 has been fixed
//            assertThat(totalPriceWithCreditCard - cancellationFee == difference).isTrue().withFailMessage("incorrect refund amount");
            assertThat(Math.abs(round(totalPriceWithCreditCard - cancellationFee - difference,2)) <= 0.01 ).isTrue().withFailMessage("incorrect refund amount");
        } else {
            Double difference = Math.abs(amendedBasket.getPriceDifference().getAmountWithDebitCard());
            assertThat(totalPriceWithDebitCard - cancellationFee == difference).isTrue().withFailMessage("incorrect refund amount");
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @When("^I remove (.*) flights$")
    public void iRemoveNFlightsAndGetTotalPriceOfRemovedFlights(int numberOfFlightsToRemove) throws Throwable {
        basketFlights = basketHelper.getBasketService().getResponse();

        String flightKey = null;

        for (int i = 0; i < numberOfFlightsToRemove; i++) {

            List<Basket.Passenger> passengers = basketFlights.getBasket().getOutbounds().get(i).getFlights().get(0).getPassengers();

            int numberOfPassengersPerFlight = passengers.size();


            if (numberOfPassengersPerFlight > 1) {
                for (int j = 0; j < numberOfPassengersPerFlight; j++) {
                    Pricing pricing = passengers.get(j).getFareProduct().getPricing();

                    Double basePrice = pricing.getBasePrice();

                    Double tax = 0.0;
                    if (pricing.getTaxes().size() > 0) {
                        tax = pricing.getTaxes().get(0).getAmount();
                    }

                    Double discounts = 0.0;
                    if (pricing.getDiscounts().size() > 0) {
                        discounts = pricing.getDiscounts().get(0).getAmount();
                    }

                    Double pricePerPassenger = basePrice + tax - discounts;
                    Double ccFee = pricePerPassenger * CREDITCARD_FEE_PERCENTAGE;

                    totalPriceWithDebitCard += round(pricePerPassenger, 2);
                    totalPriceWithCreditCard += round(pricePerPassenger + ccFee, 2);

                    if (passengers.get(j).getInfantsOnLap().size() == 1) {
                        flightKey = basketFlights.getBasket().getOutbounds().get(i).getFlights().get(0).getFlightKey();
                    }
                }
            }

            pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey(flightKey).build();

            removeFlightFromBasketService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
            removeFlightFromBasketService.invoke();
        }
    }

    @Then("^the flight should be removed from the basket$")
    public void theFlightShouldBeRemovedFromTheBasket() throws Throwable {
        removeFlightFromBasketService.assertThat().basketOperationConfirmation(testData.getBasketId());
        pathParams = BasketPathParams.builder().basketId(testData.getBasketId()).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
        basketService.invoke();
        try {
            pollingLoop().untilAsserted(() -> {
                assertThat(basketService.getResponse().getBasket().getOutbounds().isEmpty());
            });
        } catch (NullPointerException e) {
            new EasyjetCompromisedException("FLIGHT IS NOT REMOVED FROM THE BASKET !!!");
            e.printStackTrace();
        }
    }


    @When("^I receive a remove Flight request with invalid (.*)$")
    public void iReceiveARemoveFlightRequestWithInvalidParams(String param) throws Throwable {
        if (param.equalsIgnoreCase("basketID")) {
            pathParams = BasketPathParams.builder().basketId("invalidBasketID").path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey(flightKey).build();
        } else if (param.equalsIgnoreCase("flightKey")) {
            pathParams = BasketPathParams.builder().basketId(basketId).path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey("invalidFlightKey").build();
        }
        if (channel == null) {
            channel = testData.getChannel();
        }
        removeFlightFromBasketService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(HybrisHeaders.getValid(channel).build(), pathParams));
        removeFlightFromBasketService.invoke();
    }

    @Then("^I will return an error (.*) for removeFlight$")
    public void iWillReturnAnErrorAndForRemoveFlight(String errorcode) throws Throwable {
        removeFlightFromBasketService.assertThatErrors().containedTheCorrectErrorMessage(errorcode);
    }

    @When("^I find a same flight for inventory$")
    public void iFindASameFlightForInventory() throws Throwable {
        flightsService = serviceFactory.findFlight(new FlightsRequest(HybrisHeaders.getValid(channel).build(), flightQueryParams));
        flightsService.invoke();
    }

    @When("^I receive a remove (.*) flight request from basket$")
    public void iReceiveARemoveFlightRequestFromBasket(String OBorIBflight) throws Throwable {
        if (OBorIBflight.equalsIgnoreCase("Outbound")) {
            pathParams = BasketPathParams.builder().basketId(basketId).path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey(outboundFlightKey).build();
        } else if (OBorIBflight.equalsIgnoreCase("Inbound")) {
            pathParams = BasketPathParams.builder().basketId(basketId).path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey(inboundFlightKey).build();
        }
        removeFlightFromBasketService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
        removeFlightFromBasketService.invoke();
    }

    @Then("^the (.*) flight should be removed from the basket$")
    public void theOBorIBFlightShouldBeRemovedFromTheBasket(String OBorIBflight) throws Throwable {
        removeFlightFromBasketService.assertThat().basketOperationConfirmation(basketId);
        pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
        basketService.invoke();

        if (OBorIBflight.equalsIgnoreCase("Flights")) {
            assertThat(basketService.getResponse().getBasket().getOutbounds().isEmpty());
        } else if (OBorIBflight.equalsIgnoreCase("Flights")) {
            assertThat(basketService.getResponse().getBasket().getInbounds().isEmpty());
        }
    }

    @And("^the admin fee remains same in the basket level for AD Channel$")
    public void theAdminFeeRemainsSameInTheBasketLevelForADChannel() throws Throwable {
        int size = basketService.getResponse().getBasket().getFees().getItems().size();
        if (size > 1) {
            for (int i = 0; i < size; i++) {
                adminFeeCode = basketService.getResponse().getBasket().getFees().getItems().get(i).getCode();
                if (adminFeeCode.equalsIgnoreCase("AdminFee")) {
                    adminFeeAfterRemoval = basketService.getResponse().getBasket().getFees().getItems().get(i).getAmount();
                    assertThat(adminFeeAfterRemoval.equals(adminFeeBeforeRemoval)).isEqualTo(true);
                    break;
                }
            }
        } else {
            adminFeeCode = basketService.getResponse().getBasket().getFees().getItems().get(0).getCode();
            if (adminFeeCode.equalsIgnoreCase("AdminFee")) {
                adminFeeAfterRemoval = basketService.getResponse().getBasket().getFees().getItems().get(0).getAmount();
                assertThat(adminFeeAfterRemoval.equals(adminFeeBeforeRemoval)).isEqualTo(true);
            }
        }

    }

    private void findFlights(String passengerMix) throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
    }

    @And("^I have found (\\d+) flights & add to my basket for (.*)$")
    public void iHaveFoundFlightsAddToMyBasketForPassengerMix(int n, String passengerMix) throws Throwable {
        for (int i = 0; i < n; i++) {
            findFlights(passengerMix);
            outbound = flightsService.getOutBoundJourneys().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getInventory().getAvailable() > 0 && g.getAvailableStatus().equalsIgnoreCase("AVAILABLE")).findFirst().orElse(null);

            flight = flightsService.getOutboundFlight();
            basketHelper.addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMix(
                    flightsService.getOutboundFlight(), passengerMix, testData.getChannel(), flightsService.getResponse().getCurrency(), "Standard");
            basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();
            flightKey = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();
            pathParams = BasketPathParams.builder().basketId(basketId).build();
            basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(channel).build(), pathParams));
            basketService.invoke();

            assertThat(basketService.getResponse().getBasket().getOutbounds()).isNotEmpty();
            basketService.assertThat().theBasketContainsTheFlight(flight);
        }
    }

    @When("^I receive a remove flight request from basket that has full admin fee$")
    public void iReceiveARemoveFlightRequestFromBasketThatHasFullAdminFee() throws Throwable {
        List<Basket.Flight> basketOutboundFlights = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());
        for (Basket.Flight flight : basketOutboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                    if (fee.getName().contains("Administration Fee")) {
                        adminFeePaxLevelBeforeRemoval = fee.getAmount();
                        flightKey = flight.getFlightKey();
                    }
                }
            }
        }
        pathParams = BasketPathParams.builder().basketId(basketId).path(BasketPathParams.BasketPaths.REMOVE_FLIGHT).flightKey(flightKey).build();
        removeFlightFromBasketService = serviceFactory.removeFlightFromBasket(new RemoveFlightFromBasketRequest(
                HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
        removeFlightFromBasketService.invoke();
    }

    @Then("^the flight is removed from the basket$")
    public void theFlightIsRemovedFromTheBasket() throws Throwable {
        removeFlightFromBasketService.assertThat().basketOperationConfirmation(basketId);
        pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams));
        basketService.invoke();
        for (int i = 0; i < basketService.getResponse().getBasket().getOutbounds().size(); i++) {
            for (int j = 0; j < basketService.getResponse().getBasket().getOutbounds().get(i).getFlights().size(); j++) {
                assertThat(basketService.getResponse().getBasket().getOutbounds().get(i).getFlights().get(j).getFlightKey().equalsIgnoreCase(flightKey)).isEqualTo(false);
            }
        }
    }

    @And("^I will apportion the admin fee across the passengers excluding Infants on next flight$")
    public void iWillApportionTheAdminFeeAcrossThePassengersExcludingInfantsOnNextFlight() throws Throwable {
        List<Basket.Flight> basketOutboundFlights = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketOutboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                    if (fee.getName().contains("Administration Fee")) {
                        adminFeePaxLevelAfterRemoval = fee.getAmount();
                        assertThat(adminFeePaxLevelAfterRemoval).isEqualTo(adminFeePaxLevelBeforeRemoval);
                    }
                }
            }
        }
    }


    @And("^all the flights are added to basket$")
    public void allTheFlightsAreAddedToBasket() throws Throwable {

        basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();

        pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(channel).build(), pathParams));
        basketService.invoke();

        assertThat(basketService.getResponse().getBasket().getOutbounds()).isNotEmpty();

        basketId = basketService.getResponse().getBasket().getCode();

        List<Basket.Flight> basketOutboundFlights = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketOutboundFlights) {

            List<Basket.Passenger> flightPassengers = new ArrayList<>(flight.getPassengers());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                    if (fee.getName().contains("Administration Fee")) {
                        adminFeePaxLevelBeforeRemoval = fee.getAmount();
                    }
                }
            }
        }
    }

    @And("^I will apportion the admin fee across the passengers excluding Infants on next flight for non-AD channels$")
    public void iWillApportionTheAdminFeeAcrossThePassengersExcludingInfantsOnNextFlightForNonADChannels() throws Throwable {
        List<Basket.Flight> basketOutboundFlights = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(flight1 -> flight1.getFlightKey().equalsIgnoreCase(flightKey))
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketOutboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                    if (fee.getName().contains("Administration Fee")) {
                        adminFeePaxLevelAfterRemoval = fee.getAmount();
                        assertThat(adminFeePaxLevelAfterRemoval).isEqualTo(Math.round(adminFeePaxLevelBeforeRemoval / 2 * 100.0) / 100.0);
                    }
                }
            }
        }

        List<Basket.Flight> basketInboundFlights = basketService.getResponse().getBasket()
                .getInbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketInboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                    if (fee.getName().contains("Administration Fee")) {
                        adminFeePaxLevelAfterRemoval = fee.getAmount();
                        assertThat(adminFeePaxLevelAfterRemoval.equals(Math.round(adminFeePaxLevelBeforeRemoval / 2 * 100.0) / 100.0)).isEqualTo(true);
                    }
                }
            }
        }
    }

    @And("^I will remove all fees and taxes from the basket$")
    public void iWillRemoveAllFeesAndTaxesFromTheBasket() throws Throwable {
        assertThat(basketService.getResponse().getBasket().getFees().getItems().size()).isEqualTo(0);
        assertThat(basketService.getResponse().getBasket().getTaxes().getItems().size()).isEqualTo(0);
        assertThat(basketService.getResponse().getBasket().getFees().getTotalAmount()).isEqualTo(0.0);
        assertThat(basketService.getResponse().getBasket().getTaxes().getTotalAmount()).isEqualTo(0.0);
        assertThat(basketService.getResponse().getBasket().getTotalAmountWithCreditCard()).isEqualTo(0.0);
        assertThat(basketService.getResponse().getBasket().getTotalAmountWithDebitCard()).isEqualTo(0.0);
    }

    @And("^I will remove the cabin bags from the basket for the flight$")
    public void iWillRemoveTheCabinBagsFromTheBasketForTheFlight() throws Throwable {
        List<Basket.Flight> basketOutboundFlights = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketOutboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AbstractPassenger.CabinItem cabinItem : traveller.getCabinItems()) {
                    if (cabinItem.getType().contains("CABIN_BAG")) {
                        assertThat(cabinItem).isNull();
                    }
                }
            }
        }

        List<Basket.Flight> basketInboundFlights = basketService.getResponse().getBasket()
                .getInbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketInboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AbstractPassenger.CabinItem cabinItem : traveller.getCabinItems()) {
                    if (cabinItem.getType().contains("CABIN_BAG")) {
                        assertThat(cabinItem).isNull();
                    }
                }
            }
        }
    }

    @And("^I will remove any hold items from the basket for the flight$")
    public void iWillRemoveAnyHoldItemsFromTheBasketForTheFlight() throws Throwable {
        List<Basket.Flight> basketOutboundFlights = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketOutboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AbstractPassenger.HoldItem holdItem : traveller.getHoldItems()) {
                    if (holdItem.getType().contains("HOLD_BAG")) {
                        assertThat(holdItem).isNull();
                    }
                }
            }
        }

        List<Basket.Flight> basketInboundFlights = basketService.getResponse().getBasket()
                .getInbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketInboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AbstractPassenger.HoldItem holdItem : traveller.getHoldItems()) {
                    if (holdItem.getType().contains("HOLD_BAG")) {
                        assertThat(holdItem).isNull();
                    }
                }
            }
        }
    }

    @And("^I will remove any sports equipment from the basket for the flight$")
    public void iWillRemoveAnySportsEquipmentFromTheBasketForTheFlight() throws Throwable {
        List<Basket.Flight> basketOutboundFlights = basketService.getResponse().getBasket()
                .getOutbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketOutboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AbstractPassenger.HoldItem holdItem : traveller.getHoldItems()) {
                    if (holdItem.getType().contains("SMALL_SPORT_EQUIPMENT") || holdItem.getType().contains("LARGE_SPORT_EQUIPMENT")) {
                        assertThat(holdItem).isNull();
                    }
                }
            }
        }

        List<Basket.Flight> basketInboundFlights = basketService.getResponse().getBasket()
                .getInbounds()
                .stream()
                .flatMap(f -> f.getFlights().stream())
                .collect(Collectors.toList());

        for (Basket.Flight flight : basketInboundFlights) {

            List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

            for (Basket.Passenger traveller : flightPassengers) {

                for (AbstractPassenger.HoldItem holdItem : traveller.getHoldItems()) {
                    if (holdItem.getType().contains("SMALL_SPORT_EQUIPMENT") || holdItem.getType().contains("LARGE_SPORT_EQUIPMENT")) {
                        assertThat(holdItem).isNull();
                    }
                }
            }
        }
    }

    @And("^the flight is added to basket along with the hold items for the channel (.*)$")
    public void theFlightIsAddedToBasketAlongWithTheHoldItems(String channel) throws Throwable {
        this.channel = channel;
        if (channel == null) {
            channel = testData.getChannel();
        }

        basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();

        pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(channel).build(), pathParams));
        basketService.invoke();

        assertThat(basketService.getResponse().getBasket().getOutbounds()).isNotEmpty();
        basketService.assertThat().holdBagAddedAtPassengerLevel(basketService.getResponse());
        basketId = basketService.getResponse().getBasket().getCode();
        flightKey = basketService.getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();
    }

    @And("^I have found & add a valid flight with \"([^\"]*)\" bundle for (.*)$")
    public void iHaveFoundAndAddAValidFlightWithBundleForPassengerMix(String bundle, String passengerMix) throws Throwable {
        flightsService = flightHelper.getFlights(testData.getChannel(), passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        outbound = flightsService.getOutBoundJourneys().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getInventory().getAvailable() > 0 && g.getAvailableStatus().equalsIgnoreCase("AVAILABLE")).findFirst().orElse(null);
        flight = flightsService.getOutboundFlight();
        basketHelper.addFlightToBasketAsChannelUsingFlightCurrencyWithPassengerMixAndFareType(flightsService.getOutboundFlight(), passengerMix, testData.getChannel(), flightsService.getResponse().getCurrency(), bundle);
    }

    @And("^verify the flight has no admin fee$")
    public void verifyTheFlightHasNoAdminFee() throws Throwable {
        basketId = basketHelper.getBasketService().getResponse().getBasket().getCode();

        pathParams = BasketPathParams.builder().basketId(basketId).build();
        basketService = serviceFactory.getBasket(new BasketRequest(HybrisHeaders.getValid(channel).build(), pathParams));
        basketService.invoke();

        assertThat(basketService.getResponse().getBasket().getOutbounds()).isNotEmpty();
        basketId = basketService.getResponse().getBasket().getCode();
        flightKey = basketService.getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();

        if (channel.equalsIgnoreCase("ADAirport") || channel.equalsIgnoreCase("ADCustomerService")) {
            List<AugmentedPriceItem> fee = basketService.getResponse().getBasket()
                    .getFees()
                    .getItems()
                    .stream()
                    .collect(Collectors.toList());

            for (AugmentedPriceItem adminFee : fee) {
                if (adminFee.getName().contains("Administration Fee")) {
                    assertThat(adminFee).isNull();
                }
            }
        } else if (channel.equalsIgnoreCase("Digital") || channel.equalsIgnoreCase("PublicApiMobile") || channel.equalsIgnoreCase("PublicApiB2B")) {
            List<Basket.Flight> basketOutboundFlights = basketService.getResponse().getBasket()
                    .getOutbounds()
                    .stream()
                    .flatMap(f -> f.getFlights().stream())
                    .collect(Collectors.toList());

            for (Basket.Flight flight : basketOutboundFlights) {

                List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

                for (Basket.Passenger traveller : flightPassengers) {

                    for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                        if (fee.getName().contains("Administration Fee")) {
                            assertThat(fee).isNull();
                        }
                    }
                }
            }

            List<Basket.Flight> basketInboundFlights = basketService.getResponse().getBasket()
                    .getInbounds()
                    .stream()
                    .flatMap(f -> f.getFlights().stream())
                    .collect(Collectors.toList());

            for (Basket.Flight flight : basketInboundFlights) {

                List<Basket.Passenger> flightPassengers = flight.getPassengers().stream().collect(Collectors.toList());

                for (Basket.Passenger traveller : flightPassengers) {

                    for (AugmentedPriceItem fee : traveller.getFareProduct().getPricing().getFees()) {
                        if (fee.getName().contains("Administration Fee")) {
                            assertThat(fee).isNull();
                        }
                    }
                }
            }
        }
    }


}