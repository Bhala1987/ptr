package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.FlightsDao;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHoldItemsHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.RemoveProductQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveProductFromBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.RemoveProductService;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.INVALID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.SPORT_EQUIP;

/**
 * Created by giuseppedimartino on 31/03/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class RemoveSportsEquipmentSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHoldItemsHelper basketHoldItemsHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private FlightsDao flightsDao;
    private RemoveProductService removeProductBasketService;
    private BasketPathParams.BasketPathParamsBuilder pathParams = BasketPathParams.builder();
    private RemoveProductQueryParams.RemoveProductQueryParamsBuilder queryParams = RemoveProductQueryParams.builder();
    private Basket.Flight flight;
    private int initialStockLevel;
    private int finalStockLevel;
    private HashMap<String, BigDecimal> totals = new HashMap<>();
    private HashMap<String, BigDecimal> sportEquipmentPrices = new HashMap<>();
    private AbstractPassenger.HoldItem holdItem;

    @Given("^I want to remove the sport equipment from the basket$")
    public void iWantToRemoveTheSportEquipmentFromTheBasket() throws Throwable {
        holdItem = getHoldItem("Snowboard");
        flight = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0);
        pathParams
                .path(SPORT_EQUIP)
                .basketId(
                        basketHelper.getBasketService().getResponse().getBasket().getCode()
                );
        queryParams
                .productCode("Snowboard")
                .passengerCode(flight.getPassengers().get(0).getCode())
                .flightKey(flight.getFlightKey());
    }
    private AbstractPassenger.HoldItem getHoldItem(String productCode) {
        if(Objects.isNull(testData.getData(SerenityFacade.DataKeys.OUTBOUND_FLIGHT)))
            flight= basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0);
        flight=testData.getData(SerenityFacade.DataKeys.OUTBOUND_FLIGHT);
        return flight.getPassengers().get(0).getHoldItems().stream().filter(
                item -> item.getCode().equalsIgnoreCase(productCode)
        ).findFirst().get();
    }


    @Given("^I want to remove the sport equipment from the basket for (.*)$")
    public void iWantToRemoveTheSportEquipmentFromTheBasketForRemove(String removal) throws Throwable {
        holdItem = getHoldItem("Snowboard");
        pathParams
                .path(SPORT_EQUIP)
                .basketId(
                        basketHelper.getBasketService().getResponse().getBasket().getCode()
                );

        switch (removal) {
            case "first passenger in first flight":
                flight = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0);
                queryParams
                        .orderEntryNumber(holdItem.getOrderEntryNumber());;
                break;
            case "all passenger in first flight":
                flight = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0);
                queryParams
                        .flightKey(flight.getFlightKey())
                        .productCode("Snowboard")
                ;
                break;
            case "all passenger in all flight":
                queryParams .productCode("Snowboard");
                break;
        }


    }

    @But("^the request contains '(.*)' for removeSportsEquipment\\(\\)$")
    public void theRequestContainsInvalidValue(String invalidValue) throws Throwable {

        switch (invalidValue) {
            case "invalid basket":
                pathParams.basketId("000000");
                break;
            case "invalid product":
                queryParams.productCode("000000");
                break;
            case "invalid passenger":
                queryParams.passengerCode("000000");
                break;
            case "invalid flightKey":
                queryParams.flightKey("000000");
                break;
            case "passenger not in the basket":
                testData.setData(INVALID, pathParams.build().getBasketId().concat("_").concat(queryParams.build().getFlightKey()).concat("_000000"));
                queryParams.passengerCode(testData.getData(INVALID));
                break;
            case "flightKey not in the basket":
                List<HybrisFlightDbModel> flights = flightFinder.findValidFlights(1, "Standard", false);
                HybrisFlightDbModel anotherFlight = flights.stream().filter(
                        flight -> !flight.getFlightKey().equals(testData.getFlightKey())
                ).collect(Collectors.toList()).get(0);
                testData.setData(INVALID, anotherFlight.getFlightKey());
                queryParams.flightKey(testData.getData(INVALID));
                break;
            case "product not in the basket":
                testData.setData(INVALID, "CanoeKayak");
                pathParams.productId(testData.getData(INVALID));
                break;
            case "wrong passenger":
                testData.setData(INVALID,
                        basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(1).getCode()
                );
                queryParams.passengerCode(testData.getData(INVALID));
                break;
            case "product associated with bundle":
                break;
            default:
                break;
        }
    }

    @When("^I send a request to removeSportsEquipment\\(\\)$")
    public void iSendARequestToRemoveSportsEquipment() throws Throwable {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        removeProductBasketService = serviceFactory.removeProductFromBasket(
                new RemoveProductFromBasketRequest(
                        headers.build()
                        , pathParams.build()
                        , queryParams.build()
                )
        );

        testData.setData(SERVICE, removeProductBasketService);
        initialStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "Snowboard");
        removeProductBasketService.invoke();
    }

    @Then("^I will receive a confirmation for removeSportsEquipment\\(\\)$")
    public void iWillReceiveAConfirmation() throws Throwable {
        removeProductBasketService.assertThat().theOperationIsConfirmed(basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @And("^the sport equipment has been removed from the basket for (.*)$")
    public void theSportEquipmentHasBeenRemovedFromTheBasketForRemoval(String removal) throws Throwable {
        totals.put("totalDebit", new BigDecimal(basketHelper.getBasketService().getResponse().getBasket().getTotalAmountWithDebitCard().toString()));
        totals.put("totalCredit", new BigDecimal(basketHelper.getBasketService().getResponse().getBasket().getTotalAmountWithCreditCard().toString()));

        BigDecimal crFeePercentage = new BigDecimal("1");
        Optional<AugmentedPriceItem> optionalCRFee = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getFareProduct().getPricing().getFees()
                .stream()
                .filter(fee -> fee.getCode().equals("CRCardFee"))
                .findFirst();
        if (optionalCRFee.isPresent()) {
            crFeePercentage = new BigDecimal(optionalCRFee.get()
                    .getPercentage()
                    .toString()).multiply(new BigDecimal("0.01")).add(new BigDecimal("1"));
        }

        int decimalPlaces = Integer.valueOf(basketHelper.getBasketService().getResponse().getBasket().getCurrency().getDecimalPlaces());

        final BigDecimal[] passengerQty = {BigDecimal.ZERO};

        BigDecimal sportEquipmentPrice = new BigDecimal(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getHoldItems().get(0).getPricing().getBasePrice().toString());
        switch (removal) {
            case "first passenger in first flight":
                sportEquipmentPrices.put("totalDebit", sportEquipmentPrice);
                sportEquipmentPrices.put("totalCredit",
                        sportEquipmentPrice
                                .multiply(crFeePercentage)
                                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                );

                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().assertThat().passengerDontHaveHoldItem(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "Snowboard") + 1;
                break;
            case "all passenger in first flight":
                passengerQty[0] = new BigDecimal(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().size());
                sportEquipmentPrices.put("totalDebit",
                        sportEquipmentPrice
                                .multiply(passengerQty[0])
                );
                sportEquipmentPrices.put("totalCredit",
                        sportEquipmentPrice
                                .multiply(crFeePercentage)
                                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                                .multiply(passengerQty[0])
                );

                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().forEach(
                        passenger -> basketHelper.getBasketService().assertThat().passengerDontHaveHoldItem(passenger.getCode())
                );
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "Snowboard") + passengerQty[0].intValue();
                break;
            case "all passenger in all flight":
                basketHelper.getBasketService().getResponse().getBasket().getOutbounds().forEach(
                        outbound -> outbound.getFlights().forEach(
                                flight -> {
                                    passengerQty[0] = passengerQty[0].add(new BigDecimal(flight.getPassengers().size()));
                                }
                        )
                );
                sportEquipmentPrices.put("totalDebit",
                        sportEquipmentPrice
                                .multiply(passengerQty[0])
                );
                sportEquipmentPrices.put("totalCredit",
                        sportEquipmentPrice
                                .multiply(crFeePercentage)
                                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                                .multiply(passengerQty[0])
                );

                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().assertThat().theBasketDoesntContainsHoldItem();
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "Snowboard") + passengerQty[0].intValue();
                break;
        }
    }

    @And("^total amount will be reduced by sport equipment price$")
    public void totalAmountWillBeReducedByHoldBagPrice() throws Throwable {
        basketHelper.getBasketService().assertThat().totalHasBeenReducedByHoldBagPrice(totals, sportEquipmentPrices);
    }

    @And("^inventory is (not|) deallocated for removeSportsEquipment\\(\\)$")
    public void inventoryIsNotDeallocated(String not) throws Throwable {
        if (not.equals("not"))
            removeProductBasketService.assertThat().stockLevelIsReduced(initialStockLevel, initialStockLevel);
        else
            removeProductBasketService.assertThat().stockLevelIsReduced(initialStockLevel, finalStockLevel);
    }

}
