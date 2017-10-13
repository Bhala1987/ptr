package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeesAndTaxesModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Created by giuseppecioce on 02/02/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class CalculateOfferPriceRequirementsSteps {

    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private CurrenciesDao currenciesDao;
    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private FlightHelper flightHelper;
    private FlightsService flightService;


    @Given("^I sent findFlight request from (.+) with (.+) for (.+)$")
    public void iHaveReceivedAFindFlightRequestForDigitalChannelForPassengersAndStandardBundle(String channel, String passengerMix, String journeyType) throws Throwable {
        testData.setChannel(channel);
        testData.setPassengerMix(passengerMix);

        switch (journeyType) {
            case "SINGLE":
                flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), journeyType, testData.getOutboundDate(), testData.getFareType(), testData.getFareType(), testData.getCurrency());
                break;
            case "OUT/IN":
                flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), journeyType, testData.getOutboundDate(), testData.getFareType(), testData.getFareType(), testData.getCurrency());
                break;
            default:
                flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
                break;

        }

    }

    @And("^the currency in the request has \"([^\"]*)\" decimal places$")
    public void theCurrencyInTheRequestHasDecimalPlaces(int decimal) throws Throwable {
        if (decimal == 5) {
            testData.setCurrency("GBP");
        } else {
            List<String> listCurrency = currenciesDao.getCurrenciesWithSpecifiedDecimalDigit(true, decimal);
            testData.setCurrency(listCurrency.get(new Random().nextInt(listCurrency.size())));
        }
    }

    @Then("^credit card fee per line item to the nearest (.+) decimal position should be rounded up$")
    public void shouldRoundedUp(int decimal) throws Throwable {
        flightService.assertThat().theCreditCardFeeForEachPassengerIsCorrect(decimal);
    }

    @And("^admin fee per passenger to the nearest (.+) decimal position should be rounded up$")
    public void adminFeeRoundedUp(int decimal) throws Throwable {

        List<FeesAndTaxesModel> adminFees = feesAndTaxesDao.getFees("AdminFee", null, testData.getCurrency(), "adult");
        if (adminFees.size() > 0) {
            BigDecimal expectedAdminFee = new BigDecimal(adminFees.get(0)
                    .getFeeValue()
                    .toString());

            BigDecimal passengers = new BigDecimal(
                    flightService.getResponse().getOutbound().getJourneys().stream().filter(Objects::nonNull)
                            .flatMap(journey -> journey.getFlights().stream().filter(Objects::nonNull))
                            .flatMap(flight -> flight.getFareTypes().stream().filter(Objects::nonNull))
                            .filter(fareType -> fareType.getFareTypeCode().equalsIgnoreCase("Standard")).limit(1)
                            .flatMapToInt(fareTypeCope -> fareTypeCope.getPassengers().stream()
                                    .filter(passenger -> !passenger.getType().equalsIgnoreCase("infant"))
                                    .mapToInt(FindFlightsResponse.Passenger::getQuantity))
                            .sum());
            if (flightService.getResponse().getInbound() != null) {
                passengers = passengers.multiply(new BigDecimal("2")).setScale(decimal, RoundingMode.HALF_UP);
            }

            flightService.assertThat().theAdminFeeForEachPassengerIsCorrect(decimal, passengers, expectedAdminFee);
        }
    }

    @And("^total should be rounded up (.+) decimal position$")
    public void totalRoundedUp(int decimal) throws Throwable {
        flightService.assertThat().theTotAmountForEachPassengerIsCorrect(decimal);
    }

    @When("^I add flight to the basket for (.+) journey$")
    public void iAddFlightToBasket(String journey) throws Throwable {
        basketHelper.addReturnFlightWithTaxToBasketAsChannelJourneyType(flightService, testData.getPassengerMix(), testData.getCurrency(), testData.getChannel(), testData.getFareType(), journey);
    }

    @Then("^credit card fee for basket rounded up to (.+) decimal position$")
    public void creditCardFeeRundedForBasket(int decimal) throws Throwable {
        basketHelper.getBasketService().assertThat().theCreditCardFeeForEachPassengerIsCorrect(decimal);
    }

    @And("^admin fee per passenger for basket rounded up (.+) decimal position$")
    public void adminFeeForPassengerRoundedUp(int decimal) throws Throwable {

        List<FeesAndTaxesModel> adminFees = feesAndTaxesDao.getFees("AdminFee", null, testData.getCurrency(), "adult");
        if (adminFees.size() > 0) {
            BigDecimal expectedAdminFee = new BigDecimal(adminFees.get(0)
                    .getFeeValue()
                    .toString());

            if (flightService.getResponse().getInbound() != null) {
                expectedAdminFee = expectedAdminFee.multiply(new BigDecimal("0.5")).setScale(decimal, RoundingMode.UP);
            }

            basketHelper.getBasketService()
                    .assertThat()
                    .theAdminFeeForEachPassengerIsCorrect(decimal, expectedAdminFee);
        }

    }

    @And("^total for basket should be rounded up (.+) decimal position$")
    public void basketTotalShouldRoundedUp(int decimal) throws Throwable {
        basketHelper.getBasketService()
                .assertThat()
                .theTotAmountForEachPassengerIsCorrect(decimal);
    }
}
