package feature.document.steps.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.PassengerTypeDao;
import com.hybris.easyjet.database.hybris.models.PassengerTypeDbModel;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.*;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Currency;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.models.PassengerMix;
import lombok.Getter;
import org.fluttercode.datafactory.impl.DataFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static feature.document.steps.helpers.BasketContentHelper.TOTALS.*;

public class BasketContentHelper {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private final BigDecimal crFee;
    private final EnumMap<TOTALS, BigDecimal> totals = new EnumMap<>(TOTALS.class);

    private SerenityFacade testData = SerenityFacade.getTestDataFromSpring();
    private PassengerTypeDao passengerTypeDao = PassengerTypeDao.getTestDataFromSpring();

    @Getter
    private BasketContent basket;
    private FindFlightsResponse.Flight foundFlight;
    private FindFlightsResponse.Journey foundJourney;

    public BasketContentHelper() {
        foundJourney = testData.getData(OUTBOUND_JOURNEY);
        foundFlight = testData.getData(OUTBOUND_FLIGHT);
        testData.setData(FLIGHT_KEY, foundFlight.getFlightKey());

        crFee = foundFlight.getFareTypes().stream()
                .filter(fareType -> fareType.getFareTypeCode().equals(testData.getData(FARE_TYPE)))
                .findFirst().get()
                .getPassengers().get(0)
                .getFees().stream()
                .filter(fee -> fee.getCode().equals("CRCardFee"))
                .map(fee -> new BigDecimal(fee.getPercentageValue().toString()))
                .findFirst().orElse(ZERO)
                .multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_UP)
                .add(BigDecimal.ONE);

        List<UniquePassenger> uniquePassengerList = createUniquePassengerList();
        List<Passenger> passengers = createPassengerList(uniquePassengerList);

        totals.put(DISCOUNTS, ZERO);
        totals.put(CC_FEES, ZERO);
        totals.put(TAXES, ZERO);
        totals.put(SUBTOTAL_DEBIT, ZERO);
        totals.put(SUBTOTAL_CREDIT, ZERO);
        totals.put(TOTAL_DEBIT, ZERO);
        totals.put(TOTAL_CREDIT, ZERO);

        List<Flight> flights = createFlights(passengers, "outbound");
        calculateTotals(passengers);
        List<Journey> outbounds = createJourney(flights);

        basket = BasketContent.builder()
                //TODO where should we get this?
                .basketLanguage("en")
                //TODO where should we get this?
                .currency(Currency.builder()
                        .code(testData.getData(CURRENCY))
                        .build())
                //TODO where should we get this?
                .defaultCardType("CREDIT")
                .customerContext(createCustomer())
                .uniquePassengerList(uniquePassengerList)
                .discounts(Discounts.builder()
                        .totalAmount("0.0")
                        .build())
                .taxes(Taxes.builder()
                        .totalAmount("0.0")
                        .build())
                .fees(Fees.builder()
                        .totalAmount("0.0")
                        .build())
                .outbounds(outbounds)
                .inbounds(null)
                .carHires(null)
                .hotels(null)
                .travelInsurances(null)
                .comments(null)
                .subtotalAmountWithDebitCard(totals.get(SUBTOTAL_DEBIT).doubleValue())
                .subtotalAmountWithCreditCard(totals.get(SUBTOTAL_CREDIT).doubleValue())
                .totalAmountWithDebitCard(totals.get(TOTAL_DEBIT).doubleValue())
                .totalAmountWithCreditCard(totals.get(TOTAL_CREDIT).doubleValue())
                .build();
    }

    private CustomerContext createCustomer() {
        testData.dataFactory.randomize(new Random(System.currentTimeMillis()).nextInt());
        String[] fullName = testData.dataFactory.getName().split(" ");

        return CustomerContext.builder()
                .name(Name.builder()
                        .firstName(fullName[0])
                        .lastName(fullName[1])
                        .fullName(fullName[0] + " " + fullName[1])
                        .title("MR")
                        .build())
                .address(Address.builder()
                        .addressLine1(testData.dataFactory.getAddress())
                        .addressLine2(testData.dataFactory.getAddressLine2())
                        .addressLine3("")
                        .postalCode(testData.dataFactory.getRandomChars(6))
                        .city(testData.dataFactory.getCity())
                        .country("GBR")
                        .build())
                .internationalDiallingCode("+44")
                .email("success" + fullName[0] + "_" + testData.dataFactory.getNumberText(10) + "@abctest.com")
                .phoneNumber(testData.dataFactory.getNumberText(12))
                .build();
    }

    private UniquePassenger createUniquePassenger(String passengerType, String title, String basketCode) {
        String[] fullname = testData.dataFactory.getName().split(" ");
        Name name = Name.builder()
                .firstName(fullname[0])
                .lastName(fullname[1])
                .fullName(fullname[0] + " " + fullname[1])
                .title(title)
                .build();

        PassengerDetails passengerDetails = PassengerDetails.builder()
                .name(name)
                .email("success" + fullname[0] + "_" + testData.dataFactory.getNumberText(10) + "@abctest.com")
                .phoneNumber(testData.dataFactory.getNumberText(12))
                .passengerType(passengerType)
                .nifNumber("")
                .ejPlusCardNumber("")
                .build();

        return UniquePassenger.builder()
                .externalPassengerId(basketCode + "_" + foundFlight.getFlightKey() + "_" + testData.dataFactory.getNumberText(13))
                .passengerDetails(passengerDetails)
                .build();
    }

    private List<UniquePassenger> createUniquePassengerList() {
        PassengerMix passengerMix = testData.getData(PASSENGERS);
        String basketCode = testData.dataFactory.getNumberText(21);

        List<UniquePassenger> uniquePassengerList = new ArrayList<>();

        for (int i = 0; i < passengerMix.getAdult(); i++) {
            uniquePassengerList.add(createUniquePassenger("adult", "mr", basketCode));
        }
        for (int i = 0; i < passengerMix.getChild(); i++) {
            uniquePassengerList.add(createUniquePassenger("child", "mr", basketCode));
        }
        for (int i = 0; i < passengerMix.getInfantOnLap(); i++) {
            uniquePassengerList.add(createUniquePassenger("infant", "infant", basketCode));
        }
        for (int i = 0; i < passengerMix.getInfantOnSeat(); i++) {
            uniquePassengerList.add(createUniquePassenger("infant", "infant", basketCode));
        }

        return uniquePassengerList;
    }

    private List<Passenger> createPassengerList(List<UniquePassenger> uniquePassengerList) {
        List<Passenger> passengerList = new ArrayList<>();

        List<String> infantsList = uniquePassengerList.stream()
                .filter(passenger -> passenger.getPassengerDetails().getPassengerType().equals("infant"))
                .map(UniquePassenger::getExternalPassengerId)
                .collect(Collectors.toList());

        PassengerMix passengerMix = testData.getData(PASSENGERS);
        List<String> infantsOnLap = new ArrayList<>();
        List<String> assignedInfantsOnSeat = new ArrayList<>();
        if (passengerMix.getInfantOnLap() > 0 && passengerMix.getInfantOnSeat() > 0) {
            infantsOnLap = new ArrayList<>(infantsList.subList(0, passengerMix.getInfantOnLap() - 1));
            assignedInfantsOnSeat = new ArrayList<>(infantsList.subList(passengerMix.getInfantOnLap(), infantsList.size() - 1));
        } else {
            if (passengerMix.getInfantOnLap() > 0) {
                infantsOnLap = infantsList;
            } else if (passengerMix.getInfantOnSeat() > 0) {
                assignedInfantsOnSeat = infantsList;
            }
        }
        List<String> assignedInfantsOnLap = new ArrayList<>(infantsOnLap);

        FindFlightsResponse.FareType passengersFares = foundFlight.getFareTypes().stream()
                .filter(fareType -> fareType.getFareTypeCode().equals(testData.getData(FARE_TYPE)))
                .findFirst().get();

        for (UniquePassenger uniquePassenger : uniquePassengerList) {
            PassengerTypeDbModel hybrisPassenger = passengerTypeDao.getPassengersOfType(uniquePassenger.getPassengerDetails().getPassengerType());

            FindFlightsResponse.Passenger passengerFare = passengersFares.getPassengers().stream()
                    .filter(passenger -> passenger.getType().equals(uniquePassenger.getPassengerDetails().getPassengerType()))
                    .findFirst().get();

            List<Item> discounts = new ArrayList<>();
            passengerFare.getDiscounts().forEach(
                    augmentedPriceItem -> discounts.add(
                            Item.builder()
                                    .code(augmentedPriceItem.getCode())
                                    .amount(augmentedPriceItem.getValue())
                                    .percentage(Double.valueOf(augmentedPriceItem.getPercentageValue()))
                                    .build()));
            List<Item> taxes = new ArrayList<>();
            passengerFare.getTaxes().forEach(
                    augmentedPriceItem -> taxes.add(
                            Item.builder()
                                    .code(augmentedPriceItem.getCode())
                                    .amount(augmentedPriceItem.getValue())
                                    .percentage(Double.valueOf(augmentedPriceItem.getPercentageValue()))
                                    .build()));
            List<Item> fees = new ArrayList<>();
            passengerFare.getFees().forEach(
                    augmentedPriceItem -> fees.add(
                            Item.builder()
                                    .code(augmentedPriceItem.getCode())
                                    .amount(augmentedPriceItem.getValue())
                                    .percentage(Double.valueOf(augmentedPriceItem.getPercentageValue()))
                                    .build()));
            Pricing pricing = Pricing.builder()
                    .basePrice(passengerFare.getBasePrice())
                    .discounts(discounts)
                    .taxes(taxes)
                    .fees(fees)
                    .totalAmountWithCreditCard(passengerFare.getTotalPassengerFare().getWithCreditCardFee())
                    .totalAmountWithDebitCard(passengerFare.getTotalPassengerFare().getWithDebitCardFee())
                    .build();

            String bundleCode = testData.getData(FARE_TYPE);
            String fareCode = testData.getData(FARE_TYPE);
            String fareName = testData.getData(FARE_TYPE);
            String fareType = testData.getData(FARE_TYPE);

            List<String> infantOnLap = null;
            List<String> infantOnSeat = null;

            if (uniquePassenger.getPassengerDetails().getPassengerType().equals("infant")) {
                if (!infantsOnLap.isEmpty()) {
                    infantsOnLap.remove(0);
                    //TODO where should we get this?
                    bundleCode = "InfantOnLap";
                    fareCode = "INFANTONLAP";
                    fareName = "Infant on lap";
                    fareType = "InfantOnLapProduct";
                } else {
                    if (fareCode.equals("Standard")) {
                        fareCode = "STD";
                    }
                }
            } else {
                if (fareCode.equals("Standard")) {
                    fareCode = "STD";
                }
                if (!assignedInfantsOnLap.isEmpty()) {
                    infantOnLap = new ArrayList<>();
                    infantOnLap.add(assignedInfantsOnLap.remove(0));
                }
                if (!assignedInfantsOnSeat.isEmpty()) {
                    infantOnSeat = new ArrayList<>();
                    infantOnSeat.add(assignedInfantsOnSeat.remove(0));
                    if (!assignedInfantsOnSeat.isEmpty()) {
                        infantOnSeat.add(assignedInfantsOnSeat.remove(0));
                    }
                }
            }

            FareProduct fareProduct = FareProduct.builder()
                    .bundleCode(bundleCode)
                    .code(fareCode)
                    .name(fareName)
                    .quantity(1)
                    .pricing(pricing)
                    .build();

            int age = new Random().nextInt(Math.min(99, hybrisPassenger.getMaxAge()) - hybrisPassenger.getMinAge()) + hybrisPassenger.getMinAge();
            Calendar newDate = Calendar.getInstance(); // creates calendar
            newDate.setTime(new Date()); // sets calendar time/date
            newDate.add(Calendar.YEAR, -Math.max(age, 1)); // subtracts the age to get a date
            newDate.add(Calendar.DAY_OF_MONTH, 1); // Adds one day
            String dateOfBirth = new SimpleDateFormat(DATE_PATTERN).format(newDate.getTime());

            PassengerAPIS passengerAPIS = PassengerAPIS.builder()
                    .name(uniquePassenger.getPassengerDetails().getName())
                    .documentExpiryDate("2099-01-01")
                    .documentNumber("YT123" + new DataFactory().getRandomChars(5).toUpperCase())
                    .documentType("PASSPORT")
                    .gender("MALE")
                    .nationality("GBR")
                    .countryOfIssue("GBR")
                    .dateOfBirth(dateOfBirth)
                    .build();

            Passenger passenger = Passenger.builder()
                    .isLead(false)
                    .fareProduct(fareProduct)
                    //TODO check if we should send it or not; at the moment is not accepted
//                    .fareType(fareType)
                    .externalPassengerId(uniquePassenger.getExternalPassengerId())
                    .infantsOnLap(infantOnLap)
                    .infantsOnSeat(infantOnSeat)
                    .age(age)
                    .passengerAPIS(passengerAPIS)
                    .holdItems(null)
                    .seat(null)
                    .passengerTotalWithCreditCard(String.valueOf(passengerFare.getTotalPassengerFare().getWithCreditCardFee()))
                    .passengerTotalWithDebitCard(String.valueOf(passengerFare.getTotalPassengerFare().getWithDebitCardFee()))
                    .build();

            passengerList.add(passenger);
        }

        return passengerList;
    }

    private List<Journey> createJourney(List<Flight> flights) {
        List<Journey> journey = new ArrayList<>();
        journey.add(Journey.builder()
                .isDirect(foundJourney.getIsDirect())
                .totalDuration(foundJourney.getTotalDuration())
                .stops(foundJourney.getStops())
                .journeyTotalWithDebitCard(totals.get(JOURNEY_DEBIT).doubleValue())
                .journeyTotalWithCreditCard(totals.get(JOURNEY_CREDIT).doubleValue())
                .flights(flights)
                .build());

        return journey;
    }

    private List<Flight> createFlights(List<Passenger> passengers, String journey) {
        List<Flight> flights = new ArrayList<>();
        flights.add(Flight.builder()
                .flightKey(foundFlight.getFlightKey())
                .flightNumber(foundFlight.getFlightNumber())
                .carrier(foundFlight.getCarrier())
                .departureDateTime(foundFlight.getDeparture().getDate())
                .arrivalDateTime(foundFlight.getArrival().getDate())
                .sector(createSector(journey))
                .passengers(passengers)
                .build());

        return flights;
    }

    private Sector createSector(String journey) {
        Airport airportOne = Airport.builder()
                .code(foundFlight.getDeparture().getAirportCode())
                .name(foundFlight.getDeparture().getAirportName())
                //TODO where should we get this?
                .terminal("1")
                .build();
        Airport airportTwo = Airport.builder()
                .code(foundFlight.getArrival().getAirportCode())
                .name(foundFlight.getArrival().getAirportName())
                //TODO where should we get this?
                .terminal("1")
                .build();
        Airport departure;
        Airport arrival;
        if (journey.equals("outbound")) {
            departure = airportOne;
            arrival = airportTwo;
        } else {
            departure = airportTwo;
            arrival = airportOne;
        }
        return Sector.builder()
                .code(foundFlight.getDeparture().getAirportCode() + foundFlight.getArrival().getAirportCode())
                .departure(departure)
                .arrival(arrival)
                .apisRequired(foundFlight.getIsApisRequired())
                //TODO where should we get this?
                .nifNumberRequired(false)
                .build();
    }

    private void calculateTotals(List<Passenger> passengers) {
        totals.put(JOURNEY_DEBIT, ZERO);
        totals.put(JOURNEY_CREDIT, ZERO);
        passengers.forEach(
                passenger -> {
                    totals.merge(
                            JOURNEY_DEBIT,
                            new BigDecimal(passenger.getFareProduct().getPricing().getTotalAmountWithDebitCard().toString()),
                            BigDecimal::add
                    );
                    totals.merge(
                            JOURNEY_CREDIT,
                            new BigDecimal(passenger.getFareProduct().getPricing().getTotalAmountWithCreditCard().toString()),
                            BigDecimal::add
                    );
                    totals.merge(
                            SUBTOTAL_DEBIT,
                            new BigDecimal(passenger.getFareProduct().getPricing().getBasePrice().toString()),
                            BigDecimal::add
                    );
                    totals.merge(
                            SUBTOTAL_CREDIT,
                            new BigDecimal(passenger.getFareProduct().getPricing().getBasePrice().toString())
                                    .multiply(crFee)
                                    .setScale(2, RoundingMode.HALF_UP),
                            BigDecimal::add
                    );
                }
        );
        totals.merge(
                TOTAL_DEBIT,
                totals.get(JOURNEY_DEBIT),
                BigDecimal::add
        );
        totals.merge(
                TOTAL_CREDIT,
                totals.get(JOURNEY_CREDIT),
                BigDecimal::add
        );
    }

    protected enum TOTALS {
        DISCOUNTS, CC_FEES, TAXES, TOTAL_DEBIT, TOTAL_CREDIT, SUBTOTAL_DEBIT, SUBTOTAL_CREDIT, JOURNEY_DEBIT, JOURNEY_CREDIT, PASSENGER_DEBIT, PASSENGER_CREDIT, FARE_DEBIT, FARE_CREDIT, PRODUCT_DEBIT, PRODUCT_CREDIT, PRODUCT_DISCOUNTS, PRODUCT_CC_FEES, PRODUCT_TAXES, PRODUCT_PRICE, EXTRA_WEIGHT_DEBIT, EXTRA_WEIGHT_CREDIT, SEAT_DEBIT, SEAT_CREDIT
    }

}