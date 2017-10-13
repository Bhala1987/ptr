package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.helpers.*;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddCarToBasketService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.constants.CommonConstants.*;

@ContextConfiguration(classes = TestApplication.class)
public class AddCarHireToTheBasketSteps {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private FlightHelper flightHelper;
    @Autowired
    private CarHireHelper carHireHelper;
    @Autowired
    private AddCarToTheBasketHelper addCarToTheBasketHelper;
    @Autowired
    private TravellerHelper travellerHelper;
    private FlightsService flightsService;


    @Given("^Using channel (.*) with passenger mix (.*) for journey (.*)$")
    public void usingChannelWithPassengerMixForJourney(String channel, String passengerMix, String journeyType) throws Throwable {
        flightsService = flightHelper.getFlights(channel, passengerMix, testData.getOrigin(), testData.getDestination(), null, testData.getOutboundDate(), testData.getInboundDate());
        testData.setData(GET_FLIGHT_SERVICE, flightsService);
        basketHelper.addReturnFlightWithTaxToBasketAsChannelJourneyType(flightsService, passengerMix, testData.getCurrency(), testData.getChannel(), "Standard", journeyType);
        testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
        carHireHelper.getCarHireQuotes();
    }

    @When("^I request to add car product to the basket without mandatory objects (.*) having required fields (.*)$")
    public void iRequestToAddCarProductWithoutMandatoryFields(String mandatoryObjects, String fields) throws Throwable {
        iHaveUpdatedAgeForAdultToNewAge(20);
        Map<String, AddCarToBasketService> errorServiceResponsesMap = new HashMap<>();
        Arrays.asList(fields.split(",")).stream().forEach(field -> {
            addCarToTheBasketHelper.getBasicAddCartoTheBasketRequestBody();
            addCarToTheBasketHelper.emptyMandatoryFieldsFromRequestBody(mandatoryObjects, field);
            AddCarToBasketService addCarToBasketService = testData.getData(SERVICE);
            errorServiceResponsesMap.put(field, addCarToBasketService);
        });
        testData.setData(SerenityFacade.DataKeys.SERVICE_CALLS, errorServiceResponsesMap);
    }

    @Then("^I see an error with code (.*)$")
    public void iSeeAnErrorMessage(String errorCode) throws Throwable {
        Map<String, AddCarToBasketService> serviceResponsesMap = testData.getData(SerenityFacade.DataKeys.SERVICE_CALLS);
        serviceResponsesMap.keySet().stream().forEach(key -> {
            AddCarToBasketService addCarToBasketService = serviceResponsesMap.get(key);
            addCarToBasketService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
        });
    }

    @When("^I request to add car product with pickup and dropoff location in different countries$")
    public void iRequestToAddCarProductWithPickupAndDropoffLocationInDifferentCountries(Map<String, String> fields) throws Throwable {
        addCarToTheBasketHelper.getBasicAddCartoTheBasketRequestBody();
        fields.entrySet().stream().forEach(field -> {
            addCarToTheBasketHelper.modifyAddCarRequestForLocationObject(field.getKey(), field.getValue());
        });
        iHaveUpdatedAgeForAdultToNewAge(20);
        addCarToTheBasketHelper.addCarToTheBasket();
    }

    @Then("^I receive an error with code (.*)$")
    public void iReceiveAnErrorWithCode(String errorCode) throws Throwable {
        AddCarToBasketService addCarToBasketService = testData.getData(SERVICE);
        addCarToBasketService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^I have updated age for adult passenger to (.*) age$")
    public void iHaveUpdatedAgeForAdultToNewAge(int newAge) throws Throwable {
        if (newAge != 0) {
            testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
            Passengers savedTraveller = travellerHelper.createValidRequestToAddPassengersForBasket(basketHelper.getBasketService().getResponse());
            List<Passenger> passengers = getAdultPassenger(savedTraveller);

            if (CollectionUtils.isNotEmpty(passengers))
                for (Passenger passenger: passengers)
                    changeAgeOfAdultPassenger(newAge, savedTraveller, passenger);
        }
    }

    private List<Passenger> getAdultPassenger(Passengers savedTraveller) throws IllegalAccessException {
        return savedTraveller.getPassengers().stream()
                .filter(h -> h.getPassengerDetails().getPassengerType().equals(CommonConstants.ADULT)).collect(Collectors.toList());
    }

    private void changeAgeOfAdultPassenger(int newAge, Passengers savedTraveller, Passenger passenger) throws IllegalAccessException {
        passenger.setAge(newAge);
        basketHelper.updatePassengersForChannel(savedTraveller,
                testData.getChannel(),
                basketHelper.getBasketService().getResponse().getBasket().getCode());
    }

    @When("^I request to add car product associated with passenger type (.*)$")
    public void iRequestToAddCarProductAssociatedWithPassengerType(String passengerType) throws Throwable {
        addCarToTheBasketHelper.addCarToBasket(passengerType, 1, 5, 1, -1);
    }

    @When("^I request to add car product to the basket$")
    public void iRequestToAddCarProductToTheBasket() throws Throwable {
        testData.setData(ORIGINAL_BASKET, basketHelper.getBasketService().getResponse().getBasket());
        iHaveUpdatedAgeForAdultToNewAge(20);
        addCarToTheBasketHelper.getBasicAddCartoTheBasketRequestBody();
        addCarToTheBasketHelper.addCarToBasket();
    }

    @Then("^Car product is added to the basket$")
    public void carProductIsAddedToTheBasket() throws Throwable {
        AddCarToBasketService addCarToBasketService = testData.getData(SERVICE);
        addCarToBasketService.assertThat().theBasketHasAtleastOneCarProduct(basketHelper.getBasketService().getResponse());
    }

    @And("^I see the total price is updated with car hire product$")
    public void iSeeTheTotalPriceIsUpdatedWithCarHireProduct() throws Throwable {
        Basket originalBasket = testData.getData(ORIGINAL_BASKET);
        AddCarToBasketService addCarToBasketService = testData.getData(SERVICE);
        addCarToBasketService.assertThat().checkBasketTotals(originalBasket, basketHelper.getBasketService().getResponse());
    }

    @When("^I request to add car product to the basket with (.*) car equipments$")
    public void iRequestToAddCarProductWithCarEquipments(int noOfCarEquipments) throws Throwable {
        iHaveUpdatedAgeForAdultToNewAge(20);
        addCarToTheBasketHelper.addCarToBasket(CommonConstants.ADULT, noOfCarEquipments, 2, 1, -1);
    }

    @When("^I request to add car product to the basket with (.*) dropOff date and (.*) dropOff time$")
    public void iRequestToAddCarProductWithDropOffDateAfterStInboundFlight(String dropOffDate, int dropOffTime) throws Throwable {
        if (dropOffDate.equals("afterInboundFlightDate")) {
            String departureDateTime = testData.getData(INBOUND_DATE);
            Calendar calender = getCalendarObject(departureDateTime, 2, getDateFormatterWithDay());
            testData.setData(INBOUND_DATE, getDateFormatterWithDay().format(calender.getTime()));
        }
        iHaveUpdatedAgeForAdultToNewAge(20);
        addCarToTheBasketHelper.addCarToBasket(CommonConstants.ADULT, 1, 0, 1, dropOffTime);
    }

    @When("^I request to add car product to the basket with (.*) pickUp date and (.*) pickUp time$")
    public void iRequestToAddCarProductWithPickUpDateBeforeOutboundArrivalDate(String pickUpDate, int pickUpTime) throws Throwable {
        if (pickUpDate.equals("beforeOutBoundArrivalDate")) {
            String arrivalDateTime = testData.getData(OUTBOUND_DATE);
            Calendar calendar = getCalendarObject(arrivalDateTime, -2, getDateFormatterWithDay());
            testData.setData(OUTBOUND_DATE, getDateFormatterWithDay().format(calendar.getTime()));
        }
        iHaveUpdatedAgeForAdultToNewAge(20);
        addCarToTheBasketHelper.addCarToBasket(CommonConstants.ADULT, 1, 5, pickUpTime, -1);
    }


    private Calendar getCalendarObject(String departureDateTime, int modifyDays, SimpleDateFormat simpleDateFormat) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(departureDateTime));
        calendar.add(Calendar.DATE, modifyDays);
        return calendar;
    }

    private static SimpleDateFormat getDateFormatterWithDay() {
        return new SimpleDateFormat("EEE d-MMM-yyyy HH:mm:ss");
    }

    @And("^I request to add another car product to the basket$")
    public void iRequestToAddAnotherCarProductToTheBasket() throws Throwable {
        AddCarToBasketService addCarToBasketService = testData.getData(SERVICE);
        addCarToBasketService.assertThat().theBasketHasAtleastOneCarProduct(basketHelper.getBasketService().getResponse());
        iRequestToAddCarProductToTheBasket();
    }

    @Given("^Inbound and outbound flight date difference is more than 28 days$")
    public void inboundAndOutboundFlightDateDifferenceIsMoreThanDays() throws Throwable {
        String outboundDate = testData.getOutboundDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = getCalendarObject(outboundDate, 30, simpleDateFormat);
        testData.setInboundDate(simpleDateFormat.format(calendar.getTime()));
    }
}




