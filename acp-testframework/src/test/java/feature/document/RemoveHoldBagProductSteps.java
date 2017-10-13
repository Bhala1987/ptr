package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.FlightsDao;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.INVALID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.HOLD_BAG;

/**
 * Created by giuseppedimartino on 31/03/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class RemoveHoldBagProductSteps {

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
    private RemoveProductService removeHoldBagFromBasketService;
    private BasketPathParams.BasketPathParamsBuilder pathParams = BasketPathParams.builder();
    private RemoveProductQueryParams.RemoveProductQueryParamsBuilder queryParams = RemoveProductQueryParams.builder();
    private Basket.Flight flight;
    private int initialStockLevel;
    private int finalStockLevel;
    private HashMap<String, BigDecimal> totals = new HashMap<>();
    private HashMap<String, BigDecimal> holdBagPrices = new HashMap<>();
    private AbstractPassenger.HoldItem holdItem;

    @Given("^I want to remove the hold bag from the basket$")
    public void iWantToRemoveTheHoldbagFromTheBasket() throws Throwable {

        flight = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0);
        testData.setData(SerenityFacade.DataKeys.OUTBOUND_FLIGHT,flight);
        holdItem = getHoldItem("20kgbag");
        pathParams
                .path(HOLD_BAG)
                .basketId(
                        basketHelper.getBasketService().getResponse().getBasket().getCode()
                );
        queryParams.productCode("20kgbag");
    }

    private AbstractPassenger.HoldItem getHoldItem(String productCode) {
            flight=testData.getData(SerenityFacade.DataKeys.OUTBOUND_FLIGHT);
        return flight.getPassengers().get(0).getHoldItems().stream().filter(
                 item -> item.getCode().equalsIgnoreCase(productCode)
         ).findFirst().get();
    }

    @Given("^I want to remove the hold bag from the basket for (.*)$")
    public void iWantToRemoveTheHoldBagFromTheBasketForRemove(String removal) throws Throwable {

        pathParams
                .path(HOLD_BAG)
                .basketId(
                        basketHelper.getBasketService().getResponse().getBasket().getCode()
                );

        switch (removal) {
            case "first passenger in first flight":
                flight = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0);
                queryParams
                        .orderEntryNumber(flight.getPassengers().get(0).getHoldItems().get(0).getOrderEntryNumber());
                break;
            case "all passenger in first flight":
                flight = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0);
                queryParams
                        .flightKey(flight.getFlightKey())
                .productCode("20kgbag")
                ;
                break;
            case "all passenger in all flight":
                queryParams .productCode("20kgbag");
                break;
        }

    }

    @Given("^I want to remove the hold bag with excess weight from the basket for (.*)$")
    public void iWantToRemoveTheHoldBagWithExcessWeightFromTheBasketForRemove(String removal) throws Throwable {
        holdItem=getHoldItem("20kgbag");
        pathParams
                .path(HOLD_BAG)
                .basketId(
                        basketHelper.getBasketService().getResponse().getBasket().getCode()
                );
        Optional<AbstractPassenger.HoldItem> quantity = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0)
                .getHoldItems().stream().filter(
                holdItem -> !holdItem.getExtraWeight().isEmpty()
        ).findFirst();
        switch (removal) {
            case "first passenger in first flight":
                flight = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0);
                queryParams
                        .orderEntryNumber(holdItem.getOrderEntryNumber());
                break;
            case "all passenger in first flight":
                setExcessWeightParameter(quantity);
                flight = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0);
                queryParams
                        .productCode("20kgbag")
                        .flightKey(flight.getFlightKey());
                break;
            case "all passenger in all flight":
                setExcessWeightParameter(quantity);
                queryParams
                        .productCode("20kgbag");
                break;
            case "only excess weight for specific product":
                setExcessWeightParameter(quantity);
                queryParams.orderEntryNumber(holdItem.getOrderEntryNumber());

        }

    }

    private void setExcessWeightParameter(Optional<AbstractPassenger.HoldItem> quantity) throws EasyjetCompromisedException {
        if (quantity.isPresent()) {
            queryParams
                    .excessWeightProductCode("3kgextraweight")
                    .excessWeightQuantity(
                            quantity.get().getExtraWeight().get(0).getQuantity().toString()
                    );
        }else{
            throw new EasyjetCompromisedException("Could not find hold item with extra weight");
        }
    }

    @But("^the request contains '(.*)'$")
    public void theRequestContainsInvalidValue(String invalidValue) throws Throwable {
        switch (invalidValue) {
            case "invalid basket":
                pathParams.basketId("000000");
                queryParams.orderEntryNumber(holdItem.getOrderEntryNumber());
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
                testData.setData(INVALID, pathParams.build().getBasketId().concat("_").concat("LTN009").concat("_000000"));
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
                testData.setData(INVALID, "20kgbag + 1qty 3kgExcessWeight");
                queryParams.excessWeightProductCode("3kgExcessWeight");
                queryParams.excessWeightQuantity("1");
                break;
            case "wrong passenger":
                testData.setData(INVALID,
                        basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(1).getCode()
                );
                queryParams.passengerCode(testData.getData(INVALID));
                break;
            case "product associated with bundle":
                break;
            case "invalid excess weight product":
                queryParams.excessWeightProductCode("000000");
                break;
            case "excess weight product not in the basket":
                testData.setData(INVALID, "1kgextraweight");
                queryParams.excessWeightProductCode(testData.getData(INVALID));
                break;
            case "invalid parameters":
                queryParams.excessWeightProductCode("3kgextraweight");
                queryParams.orderEntryNumber(null);
                break;
            case "invalid excess weight product quantity":
                queryParams.excessWeightQuantity("4");
                break;
            default:
                break;
        }
    }

    @When("^I send a request to removeHoldBagProduct\\(\\)$")
    public void iSendARequestToRemoveHoldBagProduct() throws Throwable {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        removeHoldBagFromBasketService = serviceFactory.removeProductFromBasket(
                new RemoveProductFromBasketRequest(
                        headers.build()
                        , pathParams.build()
                        , queryParams.build()
                )
        );

        testData.setData(SERVICE, removeHoldBagFromBasketService);
        initialStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "20kgbag");
        removeHoldBagFromBasketService.invoke();
    }

    @Then("^I will receive a confirmation$")
    public void iWillReceiveAConfirmation() throws Throwable {
        removeHoldBagFromBasketService.assertThat().theOperationIsConfirmed(basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @And("^the hold bag has been removed from the basket for (.*)$")
    public void theHoldBagHasBeenRemovedFromTheBasketForRemoval(String removal) throws Throwable {
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

        BigDecimal holdBagPrice = new BigDecimal(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getHoldItems().get(0).getPricing().getBasePrice().toString());

        final BigDecimal[] passengerQty = {BigDecimal.ZERO};
        switch (removal) {
            case "first passenger in first flight":
                holdBagPrices.put("totalDebit", holdBagPrice);
                holdBagPrices.put("totalCredit",
                        holdBagPrice
                                .multiply(crFeePercentage)
                                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                );

                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().assertThat().passengerDontHaveHoldItem(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "20kgbag") + 1;
                break;
            case "all passenger in first flight":
                passengerQty[0] = new BigDecimal(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().size());
                holdBagPrices.put("totalDebit",
                        holdBagPrice
                                .multiply(passengerQty[0])
                );
                holdBagPrices.put("totalCredit",
                        holdBagPrice
                                .multiply(crFeePercentage)
                                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                                .multiply(passengerQty[0])
                );

                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().forEach(
                        passenger -> basketHelper.getBasketService().assertThat().passengerDontHaveHoldItem(passenger.getCode())
                );
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "20kgbag") + passengerQty[0].intValue();
                break;
            case "all passenger in all flight":
                basketHelper.getBasketService().getResponse().getBasket().getOutbounds().forEach(
                        outbound -> outbound.getFlights().forEach(
                                flight -> {
                                    passengerQty[0] = passengerQty[0].add(new BigDecimal(flight.getPassengers().size()));
                                }
                        )
                );
                holdBagPrices.put("totalDebit",
                        holdBagPrice
                                .multiply(passengerQty[0])
                );
                holdBagPrices.put("totalCredit",
                        holdBagPrice
                                .multiply(crFeePercentage)
                                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                                .multiply(passengerQty[0])
                );

                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().assertThat().theBasketDoesntContainsHoldItem();
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "20kgbag") + passengerQty[0].intValue();
                break;
        }

    }

    @And("^the hold bag with excess weight has been removed from the basket for (.*)$")
    public void theHoldBagWithExcessWeightHasBeenRemovedFromTheBasketForRemoval(String removal) throws Throwable {
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
        BigDecimal holdBagPrice = new BigDecimal(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getHoldItems().get(0).getPricing().getBasePrice().toString());
        BigDecimal excessWeightPrice = new BigDecimal(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getHoldItems().get(0).getExtraWeight().get(0).getPricing().getBasePrice().toString());

        final BigDecimal[] passengerQty = {BigDecimal.ZERO};
        switch (removal) {
            case "first passenger in first flight":
                setTotalOfRemovedItem(holdBagPrice, excessWeightPrice, holdBagPrice
                        .multiply(crFeePercentage)
                        .setScale(decimalPlaces, RoundingMode.HALF_UP), excessWeightPrice
                        .multiply(crFeePercentage)
                        .setScale(decimalPlaces, RoundingMode.HALF_UP));

                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().assertThat().passengerDontHaveHoldItem(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getCode());
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "20kgbag") + 1;
                break;
            case "all passenger in first flight":
                passengerQty[0] = new BigDecimal(basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().size());
                setTotalOfRemovedItem(holdBagPrice
                        .multiply(passengerQty[0]), excessWeightPrice
                                .multiply(passengerQty[0]), holdBagPrice
                                        .multiply(crFeePercentage)
                                        .setScale(decimalPlaces, RoundingMode.HALF_UP)
                                        .multiply(passengerQty[0]), excessWeightPrice
                                                .multiply(crFeePercentage)
                                                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                                                .multiply(passengerQty[0]));

                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers().forEach(
                        passenger -> basketHelper.getBasketService().assertThat().passengerDontHaveHoldItem(passenger.getCode())
                );
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "20kgbag") + passengerQty[0].intValue();
                break;
            case "all passenger in all flight":
                basketHelper.getBasketService().getResponse().getBasket().getOutbounds().forEach(
                        outbound -> outbound.getFlights().forEach(
                                flight -> {
                                    passengerQty[0] = passengerQty[0].add(new BigDecimal(flight.getPassengers().size()));
                                }
                        )
                );
                setTotalOfRemovedItem(holdBagPrice
                        .multiply(passengerQty[0]), excessWeightPrice
                                .multiply(passengerQty[0]), holdBagPrice
                                        .multiply(crFeePercentage)
                                        .setScale(decimalPlaces, RoundingMode.HALF_UP)
                                        .multiply(passengerQty[0]), excessWeightPrice
                                                .multiply(crFeePercentage)
                                                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                                                .multiply(passengerQty[0]));

                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().assertThat().theBasketDoesntContainsHoldItem();
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "20kgbag") + passengerQty[0].intValue();
                break;
            case "only excess weight for specific product":
                holdBagPrices.merge("totalDebit",
                        excessWeightPrice,
                        BigDecimal::add
                );

                holdBagPrices.merge("totalCredit",
                        excessWeightPrice
                                .multiply(crFeePercentage)
                                .setScale(decimalPlaces, RoundingMode.HALF_UP),
                        BigDecimal::add
                );
                basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode());
                basketHelper.getBasketService().assertThat().theBasketDoesntContainsHoldItem();
                finalStockLevel = flightsDao.getReservedStockLevelForFlight(testData.getFlightKey(), "20kgbag") + passengerQty[0].intValue();
                break;
        }
    }
    private void setTotalOfRemovedItem(BigDecimal holdBagPrice, BigDecimal excessWeightPrice, BigDecimal value, BigDecimal value2) {
        holdBagPrices.put("totalDebit", holdBagPrice);
        holdBagPrices.put("totalCredit",
                value
        );
        holdBagPrices.merge("totalDebit",
                excessWeightPrice,
                BigDecimal::add
        );

        holdBagPrices.merge("totalCredit",
                value2,
                BigDecimal::add
        );
    }
    @And("^total amount will be reduced by hold bag price$")
    public void totalAmountWillBeReducedByHoldBagPrice() throws Throwable {
        basketHelper.getBasketService().assertThat().totalHasBeenReducedByHoldBagPrice(totals, holdBagPrices);
    }

    @And("^inventory is (not|) deallocated$")
    public void inventoryIsNotDeallocated(String not) throws Throwable {
        if (not.equals("not"))
            removeHoldBagFromBasketService.assertThat().stockLevelIsReduced(initialStockLevel, initialStockLevel);
        else
            removeHoldBagFromBasketService.assertThat().stockLevelIsReduced(initialStockLevel, finalStockLevel);
    }

}
