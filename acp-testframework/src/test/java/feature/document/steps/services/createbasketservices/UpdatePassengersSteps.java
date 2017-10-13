package feature.document.steps.services.createbasketservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.CartDao;
import com.hybris.easyjet.database.hybris.dao.PassengerTypeDao;
import com.hybris.easyjet.database.hybris.models.PassengerTypeDbModel;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.*;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BasketTravellerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractFlights;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketTravellerService;
import cucumber.api.java.en.And;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.fluttercode.datafactory.impl.DataFactory;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.PASSENGER;

/**
 * UpdatePassengersSteps handle the communication with the updatePassengers service (aka addTraveller).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 */
@ContextConfiguration(classes = TestApplication.class)
public class UpdatePassengersSteps {

    private static final String ADULT = "adult";
    private static final String INFANT = "infant";
    private static final String CHILD = "child";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private PassengerTypeDao passengerTypeDao;
    @Autowired
    private CartDao cartDao;

    @Steps
    private GetBasketSteps getBasketSteps;

    private BasketTravellerService basketTravellerService;
    private BasketPathParams.BasketPathParamsBuilder basketPathParams;
    private Passengers.PassengersBuilder updatePassengerRequestBody;

    private Boolean apis = true;

    private List<Passenger> createPassenger(Basket basket, List<Basket.Passenger> passengers, Map<String, String> adultCodes) {
        String title = "mr";
        String relatedAdult = null;
        String passengerType = passengers.get(0).getPassengerDetails().getPassengerType();
        if (passengerType.equalsIgnoreCase(INFANT)) {
            title = INFANT;
            String infantFlight = passengers.get(0).getCode().split("_")[1];
            for (Map.Entry<String, String> entry : adultCodes.entrySet()) {
                String[] infants = entry.getValue().split(",");
                if (entry.getKey().split("_")[1].equals(infantFlight) && passengers.get(0).getFareProduct().getBundleCode().equalsIgnoreCase("InfantOnLap") && infants[0].equals("0")) {
                    relatedAdult = entry.getKey();
                    adultCodes.put(entry.getKey(), "1," + infants[1]);
                    break;
                } else if (entry.getKey().split("_")[1].equals(infantFlight) && !passengers.get(0).getFareProduct().getBundleCode().equalsIgnoreCase("InfantOnLap") && Integer.parseInt(infants[1]) < 2) {
                    relatedAdult = entry.getKey();
                    adultCodes.put(entry.getKey(), infants[0] + "," + Integer.parseInt(infants[1]) + 1);
                    break;
                }
            }
        }

        PassengerTypeDbModel hybrisPassenger = passengerTypeDao.getPassengersOfType(passengerType);
        int age = new Random().nextInt(Math.min(99, hybrisPassenger.getMaxAge()) - hybrisPassenger.getMinAge()) + hybrisPassenger.getMinAge();

        Calendar newDate = Calendar.getInstance(); // creates calendar
        newDate.setTime(new Date()); // sets calendar time/date
        newDate.add(Calendar.YEAR, -Math.max(age, 1)); // subtracts the age to get a date
        newDate.add(Calendar.DAY_OF_MONTH, 1); // Adds one day
        String dateOfBirth = new SimpleDateFormat(DATE_PATTERN).format(newDate.getTime());

        String[] fullname = testData.dataFactory.getName().split(" ");
        Name name = Name.builder()
                .firstName(fullname[0])
                .lastName(fullname[1])
                .fullName(fullname[0] + " " + fullname[1])
                .title(title)
                .build();

        Passenger.PassengerBuilder passengerData = Passenger.builder()
                .relatedAdult(relatedAdult)
                .passengerDetails(
                        PassengerDetails.builder()
                                .name(name)
                                .email("success" + name.getFirstName() + "_" + testData.dataFactory.getNumberText(10) + "@abctest.com")
                                .phoneNumber(testData.dataFactory.getNumberText(12))
                                .ejPlusCardNumber("")
                                .nifNumber("")
                                .passengerType(passengerType)
                                .build()
                )
                .age(age)
                .isLead(true)
                .specialRequests(SpecialRequest.builder().build());

        if (apis) {
            passengerData.passengerAPIS(
                    PassengerAPIS.builder()
                            .name(name)
                            .countryOfIssue("GBR")
                            .nationality("GBR")
                            .gender("MALE")
                            .documentType("PASSPORT")
                            .documentNumber("YT123" + new DataFactory().getRandomChars(5).toUpperCase())
                            .documentExpiryDate("2099-01-01")
                            .dateOfBirth(dateOfBirth)
                            .build());
        }

        List<Passenger> passengersDetails = new ArrayList<>();
        if (passengerType.equalsIgnoreCase(INFANT)) {
            String relatedAdultFlight = relatedAdult.split("_")[1];
            List<Basket.Passenger> relatedAdultLinkedPassengers = getSamePassengerList(basket, relatedAdult);
            passengers.forEach(
                    passenger -> {
                        String infantFlight = passenger.getCode().split("_")[1];
                        if (!infantFlight.equals(relatedAdultFlight)) {
                            passengerData.relatedAdult(relatedAdultLinkedPassengers.stream()
                                    .filter(adult -> adult.getCode().split("_")[1].equals(infantFlight))
                                    .findFirst().get().getCode());
                        }
                        passengersDetails.add(passengerData.code(passenger.getCode()).build());
                    }
            );
        } else {
            passengers.forEach(
                    passenger -> passengersDetails.add(passengerData.code(passenger.getCode()).build())
            );
        }

        return passengersDetails;
    }

    private List<Passenger> createCustomerPassenger(Basket.Passenger passenger) {
        RegisterCustomerRequestBody customer = testData.getData(REGISTER_CUSTOMER_REQUEST);

        String passengerType = passenger.getPassengerDetails().getPassengerType();
        String relatedAdult = null;

        Calendar newDate = Calendar.getInstance(); // creates calendar
        newDate.setTime(new Date()); // sets calendar time/date
        newDate.add(Calendar.YEAR, -26); // subtracts the age to get a date
        newDate.add(Calendar.DAY_OF_MONTH, 1); // Adds one day
        String dateOfBirth = new SimpleDateFormat(DATE_PATTERN).format(newDate.getTime());

        Name name = Name.builder()
                .firstName(customer.getPersonalDetails().getFirstName())
                .lastName(customer.getPersonalDetails().getLastName())
                .fullName(customer.getPersonalDetails().getFirstName() + " " + customer.getPersonalDetails().getLastName())
                .title(customer.getPersonalDetails().getTitle())
                .build();

        Passenger.PassengerBuilder passengerData = Passenger.builder()
                .relatedAdult(relatedAdult)
                .passengerDetails(
                        PassengerDetails.builder()
                                .name(name)
                                .email(customer.getPersonalDetails().getEmail())
                                .phoneNumber(customer.getPersonalDetails().getPhoneNumber())
                                .ejPlusCardNumber("")
                                .nifNumber("")
                                .passengerType(passengerType)
                                .build()
                )
                .age(26)
                .isLead(true)
                .passengerAPIS(
                        PassengerAPIS.builder()
                                .name(name)
                                .countryOfIssue("GBR")
                                .nationality("GBR")
                                .gender("MALE")
                                .documentType("PASSPORT")
                                .documentNumber("YT123" + new DataFactory().getRandomChars(5).toUpperCase())
                                .documentExpiryDate("2099-01-01")
                                .dateOfBirth(dateOfBirth)
                                .build())
                .specialRequests(SpecialRequest.builder().build());

        List<Passenger> passengersDetails = new ArrayList<>();
        passengersDetails.add(passengerData.code(passenger.getCode()).build());

        return passengersDetails;
    }

    private void setPathParameter() {
        basketPathParams = BasketPathParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .path(PASSENGER);
    }

    private void setRequestBody() {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        List<Passenger> passengers = new ArrayList<>();

        List<List<Basket.Passenger>> basketPassengers = getUniquePassengerList(basket);

        Map<String, String> adultCodes = basketPassengers.stream()
                .filter(passenger -> !passenger.isEmpty())
                .filter(passenger -> passenger.get(0).getPassengerDetails().getPassengerType().equals(ADULT))
                .map(passenger -> passenger.get(0))
                .collect(Collectors.toMap(AbstractPassenger::getCode, item -> "0,0"));

        basketPassengers.forEach(
                passenger -> passengers.addAll(createPassenger(basket, passenger, adultCodes))
        );

        updatePassengerRequestBody = Passengers.builder()
                .passengers(passengers);
    }

    //It works only for 1 adult; to add more than 1 passenger to a staff bundle is a manual test
    private void setRequestBodyForCustomer() {
        getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
        BasketService basketService = testData.getData(BASKET_SERVICE);
        Basket basket = basketService.getResponse().getBasket();

        List<Passenger> passengers = new ArrayList<>();

        List<List<Basket.Passenger>> basketPassengers = getUniquePassengerList(basket);

        passengers.addAll(createCustomerPassenger(basketPassengers.get(0).get(0)));

        updatePassengerRequestBody = Passengers.builder()
                .passengers(passengers);
    }

    private void invokeUpdatePassengerService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        basketTravellerService = serviceFactory.updatePassengers(new BasketTravellerRequest(headers.build(), basketPathParams.build(), updatePassengerRequestBody.build()));
        basketTravellerService.invoke();
    }

    private List<List<Basket.Passenger>> getUniquePassengerList(Basket basket) {

        List<List<Basket.Passenger>> uniquePassengerList = new ArrayList<>();

        List<Basket.Passenger> outboundPassengers = basket.getOutbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<String> samePerson = new ArrayList<>();
        for (int i = 0; i < outboundPassengers.size(); i++) {
            boolean toAdd = true;
            for (List<Basket.Passenger> anUniquePassengerList : uniquePassengerList) {
                if (anUniquePassengerList.contains(outboundPassengers.get(i))) {
                    toAdd = false;
                    break;
                }
            }
            if (toAdd) {
                uniquePassengerList.add(new ArrayList<>(Collections.singletonList(outboundPassengers.get(i))));
                List<Basket.Passenger> samePassenger = getSamePassengerList(basket, outboundPassengers.get(i).getCode());
                uniquePassengerList.get(i).addAll(samePassenger);

                samePerson.addAll(samePassenger.stream().map(AbstractPassenger::getCode).collect(Collectors.toList()));
            }
        }

        basket.getInbounds().stream()
                .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
                .filter(passenger -> !samePerson.contains(passenger.getCode()))
                .forEach(
                        passenger -> uniquePassengerList.add(new ArrayList<>(Collections.singletonList(passenger)))
                );

        return uniquePassengerList;
    }

    private List<Basket.Passenger> getPassengerList(Basket basket) {
        return Stream.concat(
                basket.getOutbounds().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream),
                basket.getInbounds().stream()
                        .map(AbstractFlights::getFlights).flatMap(Collection::stream)
                        .map(AbstractFlights.AbstractFoundFlight::getPassengers).flatMap(Collection::stream)
        ).collect(Collectors.toList());
    }

    private List<Basket.Passenger> getSamePassengerList(Basket basket, String passengerCode) {
        return getPassengerList(basket).stream()
                .filter(passenger -> cartDao.getAssociatedPassenger(basket.getCode(), passengerCode).contains(passenger.getCode()))
                .collect(Collectors.toList());
    }


    @Step("Update passenger")
    @And("^I updated the passenger information( excluding APIS information)?$")
    public void sendUpdatePassengerRequest(String apis) {
        this.apis = StringUtils.isBlank(apis);
        setPathParameter();
        setRequestBody();
        invokeUpdatePassengerService();
    }

    @Step("Update passenger")
    @And("^I updated the passenger information with same surname$")
    public void sendUpdatePassengerWithSameSurnameRequest() {
        setPathParameter();
        setRequestBody();
        List<Passenger> passengers = updatePassengerRequestBody.build().getPassengers();
        String familyName = passengers.get(0).getPassengerDetails().getName().getLastName();
        for (Passenger passenger:passengers) {
            passenger.getPassengerDetails().getName().setLastName(familyName);
        }
        updatePassengerRequestBody.passengers(passengers);
        invokeUpdatePassengerService();
    }

    @Step("Update passenger with customer information")
    @And("^I updated the passenger information with customer details$")
    public void sendUpdatePassengerRequestForCustomer() {
        setPathParameter();
        setRequestBodyForCustomer();
        invokeUpdatePassengerService();
    }

}