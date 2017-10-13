package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.PassengerTypeDao;
import com.hybris.easyjet.database.hybris.models.PassengerTypeDbModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.Profile;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SAVED_TRAVELLER;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CommitBookingFactory.aPassenger;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.CommitBookingFactory.aPassengerWithApis;

/**
 * Created by dwebb on 11/25/2016.
 */
@Component
public class TravellerHelper {

    @Setter
    private int adultMin;
    @Setter
    private int adultMax;
    @Setter
    private int childMax;
    @Setter
    private int childMin;
    @Setter
    private int infantMax;

    @Autowired
    private SerenityFacade testData;

    private Passengers savedTraveller;
    private static final String ADULT = "adult";
    private static final String CHILD = "child";
    private static final String INFANT = "infant";
    private static final String NO_ADULT = "No more adult passenger for this flight";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private final PassengerTypeDao hybrisPassengerTypeDao;

    @Autowired
    public TravellerHelper(PassengerTypeDao hybrisPassengerTypeDao) {
        this.hybrisPassengerTypeDao = hybrisPassengerTypeDao;
    }

    /**
     * The method is useful to just to change the age
     *
     * @param original age
     * @param changeTo new age
     * @param basket
     * @return Passengers
     * @throws Exception
     */
    public Passengers createRequestToChangePassengerAge(String original, String changeTo, BasketsResponse basket) throws Exception {
        List<Passenger> travellers = new ArrayList<>();
        Passenger traveller = aPassenger();
        traveller.getPassengerDetails().setPassengerType(original.toLowerCase());
        traveller.setAge(getAge(changeTo));
        traveller.getPassengerAPIS().setDateOfBirth(getAValidDateOfBirth(traveller.getPassengerDetails().getPassengerType()));
        traveller.getPassengerDetails().getName().setTitle(getTitle(original));
        traveller.setCode(getPassengerCode(basket, original));
        if (INFANT.equalsIgnoreCase(original)) {
            traveller.setRelatedAdult(basket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).filter(h -> ADULT.equalsIgnoreCase(h.getPassengerDetails().getPassengerType())).findFirst().orElse(null).getCode());
        }
        travellers.add(traveller);
        return Passengers.builder().passengers(travellers).build();
    }


    /**
     * The method is useful to just to change the age and type
     * This method is temporarily built for addPassengerToFlight() service for manage booking
     * @param changeTo new age
     * @param passengerCode
     * @return Passengers
     * @throws Exception
     */
    public Passengers createRequestToChangePassengerAgeAndType(String changeTo, String passengerCode, boolean withApis) throws Exception {
        List<Passenger> travellers = new ArrayList<>();
        Passenger traveller;
        if (withApis) {
            traveller = aPassengerWithApis();
        } else {
            traveller = aPassenger();
        }
        traveller.setAge(getAge(changeTo));
        traveller.getPassengerAPIS().setDateOfBirth(getAValidDateOfBirth(traveller.getPassengerDetails().getPassengerType()));
        traveller.setCode(passengerCode);
        travellers.add(traveller);
        return Passengers.builder().passengers(travellers).build();
    }

    public Passengers createRequestWithFieldSetAs(List<FieldAndValue> fields, BasketsResponse basket, String passengerMix, boolean withApis) throws Exception {
        List<Passenger> passengers = new ArrayList<>();
        Passenger passenger;
        if (withApis) {
            passenger = aPassengerWithApis();
        } else {
            passenger = aPassenger();
        }

        boolean setAge = true;

        passenger.setCode(getPassengerCode(basket, passengerMix));

        for (FieldAndValue fv : fields) {
            switch (fv.getField()) {
                case "firstname":
                    passenger.getPassengerDetails().getName().setFirstName(fv.getValue());
                    break;
                case "lastname":
                    passenger.getPassengerDetails().getName().setLastName(fv.getValue());
                    break;
                case "title":
                    passenger.getPassengerDetails().getName().setTitle(fv.getValue());
                    break;
                case "passengertype":
                    passenger.getPassengerDetails().setPassengerType(fv.getValue());
                    break;
                case "age":
                    if (fv.getValue() != null) {
                        passenger.setAge(Integer.parseInt(fv.getValue()));
                    } else {
                        passenger.setAge(null);
                    }
                    setAge = false;
                    break;
                case "phoneNumber":
                    passenger.getPassengerDetails().setPhoneNumber(fv.getValue());
                    break;
                case "email":
                    passenger.getPassengerDetails().setEmail(fv.getValue());
                    break;
                case "code":
                    passenger.setCode(fv.getValue());
                    break;
                case "ejPlus":
                    passenger.getPassengerDetails().setEjPlusCardNumber(fv.getValue());
                    break;
                case "ejPlusSurname":
                    passenger.getPassengerDetails().getName().setLastName(fv.getValue());
                    break;
                case "DocumentNumber":
                    passenger.getPassengerAPIS().setDocumentNumber(fv.getValue());
                    break;
                case "DocumentType":
                    passenger.getPassengerAPIS().setDocumentType(fv.getValue());
                    break;
                case "DocumentExpiryDate":
                    passenger.getPassengerAPIS().setDocumentExpiryDate(fv.getValue());
                    break;
                case "DateOfBirth":
                    passenger.getPassengerAPIS().setDateOfBirth(fv.getValue());
                    break;
                case "Gender":
                    passenger.getPassengerAPIS().setGender(fv.getValue());
                    break;
                case "Nationality":
                    passenger.getPassengerAPIS().setNationality(fv.getValue());
                    break;
                case "CountryOfIssue":
                    passenger.getPassengerAPIS().setCountryOfIssue(fv.getValue());
                    break;
                default:
                    throw new IllegalArgumentException("the parameter you provided is not valid.  you provided: " + fv.getField());
            }
        }

        if (setAge && passenger.getPassengerDetails().getPassengerType() != null) {
            passenger.setAge(getAge(passenger.getPassengerDetails().getPassengerType()));
        }
        // passenger.setCode(getPassengerCode(basket, passengerMix));
        passengers.add(passenger);
        return Passengers.builder().passengers(passengers).build();
    }

    public Passengers createRequestWithFieldSetAs(List<FieldAndValue> myFields, BasketsResponse basket, String passengerMix) throws Exception {
        return createRequestWithFieldSetAs(myFields, basket, passengerMix, false);
    }

    public Passengers createValidRequestToAddAllPassengersForBasket(BasketsResponse basket, boolean withApis) {
        List<Passenger> travellers = new ArrayList<>();
        List<Basket.Passenger> basketPassengers = getAllOutboundPassengers(basket);
        List<String> adultCodes = new ArrayList<>();

        basketPassengers.addAll(getAllInboundPassengers(basket));
        for (Basket.Passenger basketPassenger : basketPassengers) {
            if (basketPassenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(ADULT)) {
                adultCodes.add(basketPassenger.getCode());
            }
        }

        for (Basket.Passenger basketPassenger : basketPassengers) {
            Passenger passenger;

            if (withApis) {
                passenger = aPassengerWithApis();
            } else {
                passenger = aPassenger();
                passenger.setPassengerAPIS(null);
            }

            passenger.getPassengerDetails().setPassengerType(basketPassenger.getPassengerDetails().getPassengerType());
            passenger.setAge(getAge(basketPassenger.getPassengerDetails().getPassengerType()));
            passenger.setCode(basketPassenger.getCode());

            if (passenger.getPassengerAPIS() != null) {
                passenger.getPassengerAPIS().setDateOfBirth(getAValidDateOfBirth(basketPassenger.getPassengerDetails().getPassengerType()));
                passenger.setRelatedAdult(null);
            }
            //0 as there will always be only one infant on lap
            if (basketPassenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(PassengerType.INFANT) && basketPassenger.getFareProduct().getType().equalsIgnoreCase(PassengerType.INFANT_ON_LAP_PRODUCT)) {
                passenger.getPassengerDetails().getName().setTitle(INFANT);
                Basket.Passenger relatedPassenger = basketPassengers.stream().filter(passengeSelected -> passengeSelected.getPassengerDetails().getPassengerType().equalsIgnoreCase(PassengerType.ADULT)
                        && passengeSelected.getInfantsOnLap() != null && CollectionUtils.isNotEmpty(passengeSelected.getInfantsOnLap())
                        && passengeSelected.getInfantsOnLap().get(0).equalsIgnoreCase(basketPassenger.getCode())).collect(Collectors.toList()).get(0);
                passenger.setRelatedAdult(relatedPassenger.getCode());
            } else if (basketPassenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(PassengerType.INFANT) && !basketPassenger.getFareProduct().getType().equalsIgnoreCase(PassengerType.INFANT_ON_LAP_PRODUCT)) {
                passenger.setRelatedAdult(null);
                passenger.getPassengerDetails().getName().setTitle(INFANT);
            }
            travellers.add(passenger);
        }

        savedTraveller = Passengers.builder().passengers(travellers).build();
        return savedTraveller;
    }

    Passengers createValidRequestToUpdateFirstPassengersForBasket(BasketsResponse basket, String lastName, boolean withApis) {
        List<Passenger> travellers = new ArrayList<>();
        List<Basket.Passenger> basketPassengers = getAllOutboundPassengers(basket);
        Basket.Passenger firstAdultPassenger = null;

        basketPassengers.addAll(getAllInboundPassengers(basket));
        for (Basket.Passenger basketPassenger : basketPassengers) {
            if (basketPassenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(ADULT)) {
                firstAdultPassenger = basketPassenger;
                break;
            }
        }

        Passenger passenger;

        if (withApis) {
            passenger = aPassengerWithApis();
        } else {
            passenger = aPassenger();
            passenger.setPassengerAPIS(null);
        }

        if (firstAdultPassenger != null) {
            passenger.getPassengerDetails().setPassengerType(firstAdultPassenger.getPassengerDetails().getPassengerType());
            passenger.setAge(getAge(firstAdultPassenger.getPassengerDetails().getPassengerType()));
            passenger.setCode(firstAdultPassenger.getCode());
            passenger.getPassengerDetails().getName().setLastName(lastName);
        }

        travellers.add(passenger);

        savedTraveller = Passengers.builder().passengers(travellers).build();
        return savedTraveller;
    }

    Passengers createValidRequestToAddAllPassengersForBasketWithSignificantOther(BasketsResponse basket, Profile passanger) {
        List<Passenger> travellers = new ArrayList<>();
        List<String> adultCodes = new ArrayList<>();
        List<Basket.Passenger> basketPassengers = getAllOutboundPassengers(basket);

        basketPassengers.addAll(getAllInboundPassengers(basket));

        for (Basket.Passenger basketPassenger : basketPassengers) {
            if (basketPassenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(PassengerType.ADULT)) {
                adultCodes.add(basketPassenger.getCode());
            }
        }

        for (Basket.Passenger basketPassenger : basketPassengers) {
            Passenger passenger = aPassenger();
            passenger.getPassengerDetails().setPassengerType(passanger.getType());
            passenger.setAge(passanger.getAge());
            passenger.setCode(basketPassenger.getCode());
            passenger.setPassengerAPIS(null);
            if (passenger.getPassengerDetails().getName() != null) {
                passenger.getPassengerDetails().getName().setTitle(passanger.getTitle());
                passenger.getPassengerDetails().getName().setFirstName(passanger.getFirstName());
                passenger.getPassengerDetails().getName().setLastName(passanger.getLastName());
                passenger.getPassengerDetails().getName().setFullName(passanger.getFirstName() + " " + passanger.getLastName());
            }
            passenger.getPassengerDetails().setEmail(passanger.getEmail());
            travellers.add(passenger);
        }
        savedTraveller = Passengers.builder().passengers(travellers).build();
        return savedTraveller;

    }

    public Passengers createValidRequestToAddAllPassengersForBasket(BasketsResponse basket) {
        return createValidRequestToAddAllPassengersForBasket(basket, testData.getUpdatePassengerWithApis());
    }

    public Passengers createValidRequestToAddPassengersForBasket(BasketsResponse basket) {
        List<Passenger> travellers = new ArrayList<>();

        List<Basket.Passenger> passengers = getOutboundPassengers(basket);
        passengers.addAll(getInboundPassengers(basket));

        String passengerCodeForAdult = "";

        for (Basket.Passenger basketPassenger : passengers) {
            if (basketPassenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(ADULT)) {
                passengerCodeForAdult = basketPassenger.getCode();
            }
            Passenger passenger = aPassenger();
            passenger.getPassengerDetails().setPassengerType(basketPassenger.getPassengerDetails().getPassengerType());
            passenger.setAge(getAge(basketPassenger.getPassengerDetails().getPassengerType()));
            if (passenger.getPassengerAPIS() != null) {
                passenger.getPassengerAPIS().setDateOfBirth(getAValidDateOfBirth(basketPassenger.getPassengerDetails().getPassengerType()));
            }
            if (basketPassenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(INFANT)) {
                passenger.getPassengerDetails().getName().setTitle(INFANT);
                passenger.setRelatedAdult(passengerCodeForAdult);
            }
            passenger.setCode(basketPassenger.getCode());
            travellers.add(passenger);
        }
        savedTraveller = Passengers.builder().passengers(travellers).build();
        testData.setData(SAVED_TRAVELLER, savedTraveller);
        return savedTraveller;
    }

    Passengers getPassengersUsedInBookingRequest(BasketsResponse basket) {
        List<Passenger> passengers = new ArrayList<>();
        List<Basket.Passenger> basketPassengers = getAllOutboundPassengers(basket);
        basketPassengers.addAll(getAllInboundPassengers(basket));

        for (Basket.Passenger basketPassenger : basketPassengers) {
            Passenger passenger = savedTraveller.getPassengers().stream().filter(c -> c.getPassengerDetails().getPassengerType().equals(
                    basketPassenger.getPassengerDetails().getPassengerType())).findFirst().orElse(null);
            passenger.getPassengerDetails().setPassengerType(basketPassenger.getPassengerDetails().getPassengerType());
            passenger.setAge(getAge(basketPassenger.getPassengerDetails().getPassengerType()));
            passenger.setCode(basketPassenger.getCode());
            passengers.add(passenger);
        }

        return Passengers.builder().passengers(passengers).build();
    }

    private List<Basket.Passenger> getAllOutboundPassengers(BasketsResponse basket) {
        return basket.getBasket().getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());
    }

    private static List<Basket.Passenger> getOutboundPassengers(BasketsResponse basket) {
        return basket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).collect(Collectors.toList());
    }

    private static List<Basket.Passenger> getInboundPassengers(BasketsResponse basket) {
        return basket.getBasket().getInbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).collect(Collectors.toList());
    }

    private static List<Basket.Passenger> getAllInboundPassengers(BasketsResponse basket) {
        return basket.getBasket().getInbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .flatMap(g -> g.getPassengers().stream())
                .collect(Collectors.toList());

    }

    public int getValidAgeForPassengerType(String type) {
        return getAge(type);
    }

    private static String getPassengerCode(BasketsResponse basket, String original) throws Exception {
        String passengerMix;
        if (original.toLowerCase().contains(ADULT)) {
            passengerMix = ADULT;
        } else if (original.toLowerCase().contains(CHILD)) {
            passengerMix = CHILD;
        } else {
            passengerMix = INFANT;
        }
        List<Basket.Passenger> passengers =
                basket.getBasket().getOutbounds().get(0).getFlights().get(0).getPassengers();

        for (Basket.Passenger passenger : passengers) {
            if (passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(passengerMix)) {
                return passenger.getCode();
            }
        }

        throw new Exception("No passenger type of " + original + " was found in the basket!");

    }

    private int getAge(String paxType) {

        if (adultMin == 0 && infantMax == 0 && childMin == 0) {
            PassengerTypeDbModel hybrisPassengers = hybrisPassengerTypeDao.getPassengersOfType(ADULT);
            this.setAdultMax(99);
            this.setAdultMin(hybrisPassengers.getMinAge());
            hybrisPassengers = hybrisPassengerTypeDao.getPassengersOfType(CHILD);
            this.setChildMax(hybrisPassengers.getMaxAge());
            this.setChildMin(hybrisPassengers.getMinAge());
            hybrisPassengers = hybrisPassengerTypeDao.getPassengersOfType(INFANT);
            this.setInfantMax(hybrisPassengers.getMaxAge());
        }

        switch (paxType) {
            case ADULT:
            case "Adult":
            case "Adults":
                return adultMin + 10;
            case INFANT:
            case "Infant":
            case "Infants":
                return infantMax - 1;
            case CHILD:
            case "Child":
            case "Children":
                return childMax - 1;
            default:
                break;
        }

        return 0;
    }

    private static String getTitle(String paxType) {
        switch (paxType) {
            case ADULT:
            case "Adult":
            case "Adults":
                return "mr";
            case INFANT:
            case "Infant":
            case "Infants":
                return "infant";
            case CHILD:
            case "Child":
            case "Children":
                return "miss";
            default:
                break;
        }

        return "mr";
    }

    private String getAValidDateOfBirth(String pax) {

        if (adultMin == 0 && infantMax == 0 && childMin == 0) {
            PassengerTypeDbModel hybrisPassengers = hybrisPassengerTypeDao.getPassengersOfType(ADULT);
            this.setAdultMax(99);
            this.setAdultMin(hybrisPassengers.getMinAge());
            hybrisPassengers = hybrisPassengerTypeDao.getPassengersOfType("child");
            this.setChildMax(hybrisPassengers.getMaxAge());
            this.setChildMin(hybrisPassengers.getMinAge());
            hybrisPassengers = hybrisPassengerTypeDao.getPassengersOfType(INFANT);
            this.setInfantMax(hybrisPassengers.getMaxAge());
        }
        switch (pax) {
            case ADULT:
            case "Adult":
            case "Adults":
                return getAValidDateOfBirthForTheAge(adultMax);
            case INFANT:
            case "Infant":
            case "Infants":
                return getAValidDateOfBirthForTheAge(infantMax);
            case CHILD:
            case "Child":
            case "Children":
                return getAValidDateOfBirthForTheAge(childMax);
            default:
                break;
        }
        return null;
    }

    private String getAValidDateOfBirthForTheAge(Integer age) {
        Date currentDate = new Date();
        if (age != null) {
            Calendar newDate = Calendar.getInstance(); // creates calendar
            newDate.setTime(currentDate); // sets calendar time/date
            newDate.add(Calendar.YEAR, age * -1); // subtracts the age to get a date
            newDate.add(Calendar.DAY_OF_MONTH, 1); // Adds one day
            return getFormattedDate(newDate.getTime(), DATE_PATTERN);
        } else {
            return null;
        }
    }

    private String getFormattedDate(Date myDate, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(myDate);
    }

    public void setDob(List<Passenger> passengers, String passengerToChange, String dobToUseForPassenger) {
        for (Passenger passenger : passengers) {
            if (passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(passengerToChange)) {
                passenger.getPassengerAPIS().setDateOfBirth(dobToUseForPassenger);
                break;
            }
        }
    }

    public String buildAStringOfLength(int aNumber) {
        String myNumber = "";
        for (int i = 1; i <= aNumber; i++) {
            myNumber = myNumber + "1";
        }
        return myNumber;
    }

    public Passengers createARequestWithAPIsWithFieldSetAs(List<FieldAndValue> fieldAndValue, BasketsResponse basket, String pax) throws Exception {
        return createRequestWithFieldSetAs(fieldAndValue, basket, pax, true);
    }

    public Passengers createRequestUpdatePassengerMixWithExceeds(BasketsResponse basket) throws Exception {
        List<Passenger> passengers = new ArrayList<>();
        final String[] adultPassengerCode = {""};
        List<Basket.Passenger> basketPassengers = basket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).collect(Collectors.toList());
        basketPassengers.forEach(item -> {
            Passenger passenger = aPassenger();
            String passengerType;
            String title;
            int age;
            if (basketPassengers.size() == 1) {
                passengerType = ADULT;
                title = "mr";
                adultPassengerCode[0] = item.getCode();
                age = 0;
            } else {
                if (passengers.isEmpty() && basketPassengers.size() > 1) { // Manage passenger as only adult
                    passengerType = ADULT;
                    title = "mr";
                    adultPassengerCode[0] = item.getCode();
                } else {
                    passengerType = INFANT;
                    title = INFANT;
                    passenger.setRelatedAdult(adultPassengerCode[0]);
                }
                age = getAge(passengerType);
            }
            passenger.getPassengerDetails().setPassengerType(passengerType);
            passenger.setAge(age);
            passenger.getPassengerDetails().getName().setTitle(title);

            passenger.setPassengerAPIS(null);
            passenger.setCode(item.getCode());
            passengers.add(passenger);
        });

        return Passengers.builder().passengers(passengers).build();
    }

    public Passengers createRequestUpdatePassengerMixWithType(BasketsResponse basket, String passengerType) throws Exception {
        List<Passenger> passengers = new ArrayList<>();
        List<Basket.Passenger> basketPassengers = basket.getBasket().getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).collect(Collectors.toList());
        basketPassengers.forEach(item -> {
            String title = ADULT.equalsIgnoreCase(passengerType) || CHILD.equalsIgnoreCase(passengerType) ? "mr" : INFANT;

            Passenger passenger = aPassenger();
            passenger.getPassengerDetails().setPassengerType(passengerType);
            passenger.setAge(getAge(passenger.getPassengerDetails().getPassengerType()));
            passenger.getPassengerDetails().getName().setTitle(title);

            passenger.setPassengerAPIS(null);
            passenger.setCode(item.getCode());

            passengers.add(passenger);
        });

        return Passengers.builder().passengers(passengers).build();
    }

    public Passengers createRequestUpdatePassengerAge(Basket basket, String passengerFromChange, String passengerChanged) throws Exception {

        List<Basket.Passenger> basketPassengers = basket.getOutbounds().stream().flatMap(f -> f.getFlights().stream()).flatMap(g -> g.getPassengers().stream()).collect(Collectors.toList());

        String passengerIdToChange = basketPassengers.stream().filter(
                basketPassenger -> basketPassenger.getPassengerDetails().getPassengerType().equals(passengerFromChange)
        ).findFirst().get().getCode();

        Optional passengerAdult = basketPassengers.stream().filter(
                basketPassenger -> !basketPassenger.getCode().equalsIgnoreCase(passengerIdToChange) && ADULT.equalsIgnoreCase(basketPassenger.getPassengerDetails().getPassengerType())
        ).findAny();

        String passengerIdAdult = null;
        if (passengerAdult.isPresent()) {
            passengerIdAdult = ((Basket.Passenger) passengerAdult.get()).getCode();
        }

        testData.setPassengerIdFromChange(passengerIdToChange);

        String title = ADULT.equalsIgnoreCase(passengerChanged) || CHILD.equalsIgnoreCase(passengerChanged) ? "mr" : INFANT;
        List<Passenger> listToAdd = new ArrayList<>();
        Passenger passenger = aPassenger();
        passenger.getPassengerDetails().setPassengerType(passengerChanged);
        passenger.setAge(getAge(passenger.getPassengerDetails().getPassengerType()));
        passenger.getPassengerDetails().getName().setTitle(title);
        passenger.setPassengerAPIS(null);
        passenger.setCode(passengerIdToChange);


        if (Objects.nonNull(passengerIdAdult) && INFANT.equalsIgnoreCase(passengerChanged)) {
            passenger.setRelatedAdult(passengerIdAdult);
        }

        listToAdd.add(passenger);

        return Passengers.builder().passengers(listToAdd).build();
    }

    Passengers updateInformationForFirstPassenger(Basket basket, String flightKey) throws EasyjetCompromisedException {
        List<Passenger> travellers = new ArrayList<>();

        Basket.Passenger firstBasketPassengers = basket.getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(l -> l.getFlightKey().equalsIgnoreCase(flightKey))
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> ADULT.equalsIgnoreCase(h.getPassengerDetails().getPassengerType()))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(firstBasketPassengers)) {
            throw new EasyjetCompromisedException("No adult passenger for this flight");
        }

        Passenger passenger = aPassengerWithApis();
        passenger.getPassengerDetails().setPassengerType(firstBasketPassengers.getPassengerDetails().getPassengerType());
        passenger.setAge(getAge(firstBasketPassengers.getPassengerDetails().getPassengerType()));
        passenger.setCode(firstBasketPassengers.getCode());

        passenger.setPassengerAPIS(null);
        travellers.add(passenger);

        return Passengers.builder().passengers(travellers).build();
    }

    Passengers updateInformationForFirstPassengerExcludeCode(Basket basket, String passengerCode, String flightKey) throws EasyjetCompromisedException {
        List<Passenger> travellers = new ArrayList<>();

        Basket.Passenger firstBasketPassengers = basket.getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(l -> l.getFlightKey().equalsIgnoreCase(flightKey))
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> ADULT.equalsIgnoreCase(h.getPassengerDetails().getPassengerType()) && !h.getCode().equalsIgnoreCase(passengerCode))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(firstBasketPassengers)) {
            throw new EasyjetCompromisedException(NO_ADULT);
        }

        Passenger passenger = aPassengerWithApis();
        passenger.getPassengerDetails().setPassengerType(firstBasketPassengers.getPassengerDetails().getPassengerType());
        passenger.setAge(getAge(firstBasketPassengers.getPassengerDetails().getPassengerType()));
        passenger.setCode(firstBasketPassengers.getCode());

        passenger.setPassengerAPIS(null);
        travellers.add(passenger);

        return Passengers.builder().passengers(travellers).build();
    }

    Passengers updateInformationForAllPassengerOnSameFlight(Basket basket, String flightKey) throws EasyjetCompromisedException {
        List<Passenger> travellers = new ArrayList<>();

        List<Basket.Passenger> passengers = basket.getOutbounds().stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(l -> l.getFlightKey().equalsIgnoreCase(flightKey))
                .flatMap(g -> g.getPassengers().stream())
                .filter(h -> ADULT.equalsIgnoreCase(h.getPassengerDetails().getPassengerType()))
                .collect(Collectors.toList());

        if (Objects.isNull(passengers) || passengers.isEmpty()) {
            throw new EasyjetCompromisedException(NO_ADULT);
        }

        for (Basket.Passenger firstBasketPassengers : passengers) {
            Passenger passenger = aPassengerWithApis();
            passenger.getPassengerDetails().setPassengerType(firstBasketPassengers.getPassengerDetails().getPassengerType());
            passenger.setAge(getAge(firstBasketPassengers.getPassengerDetails().getPassengerType()));
            passenger.setCode(firstBasketPassengers.getCode());

            passenger.setPassengerAPIS(null);
            travellers.add(passenger);
        }

        return Passengers.builder().passengers(travellers).build();
    }

    Passengers updateInformationForAllPassenger(Basket basket,boolean fliterRequired) throws EasyjetCompromisedException {
        List<Passenger> travellers = new ArrayList<>();

        List<Basket.Passenger> passengers = null;

        if(fliterRequired) {

            passengers = basket.getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(g -> g.getPassengers().stream())
                    .filter(h -> ADULT.equalsIgnoreCase(h.getPassengerDetails().getPassengerType()))
                    .collect(Collectors.toList());
        }else{
             passengers = basket.getOutbounds().stream()
                    .flatMap(f -> f.getFlights().stream())
                    .flatMap(g -> g.getPassengers().stream())
                    .collect(Collectors.toList());
        }

        if (Objects.isNull(passengers) || passengers.isEmpty()) {
            throw new EasyjetCompromisedException(NO_ADULT);
        }

        for (Basket.Passenger firstBasketPassengers : passengers) {
            Passenger passenger = aPassengerWithApis();
            passenger.getPassengerDetails().setPassengerType(firstBasketPassengers.getPassengerDetails().getPassengerType());
            passenger.setAge(getAge(firstBasketPassengers.getPassengerDetails().getPassengerType()));
            passenger.setCode(firstBasketPassengers.getCode());
            if(passenger.getAge()==infantMax - 1){
                passenger.getPassengerDetails().getName().setTitle(firstBasketPassengers.getPassengerDetails().getPassengerType());
                Basket.Passenger adultPassenger = basket.getOutbounds().stream()
                        .flatMap(f -> f.getFlights().stream())
                        .flatMap(g -> g.getPassengers().stream())
                        .filter(h -> ADULT.equalsIgnoreCase(h.getPassengerDetails().getPassengerType())).findFirst().get();
                passenger.setRelatedAdult(adultPassenger.getCode());
            }

            passenger.setPassengerAPIS(null);
            travellers.add(passenger);
        }

        return Passengers.builder().passengers(travellers).build();
    }

    public void removeAPIsFromRequestFor(List<Passenger> passengers, String passengerToRemoveAPIs) {
        for (Passenger passenger : passengers) {
            if (passenger.getPassengerDetails().getPassengerType().equalsIgnoreCase(passengerToRemoveAPIs)) {
                passenger.setPassengerAPIS(null);
                break;
            }
        }
    }

    public Passengers createRequestWithMultipleFieldsSetAs(String lastname, String ejPlus, int age, BasketsResponse response, String thePassengerMix) throws Exception {
        List<Passenger> passengers = new ArrayList<>();
        Passenger passenger;
        passenger = aPassenger();
        passenger.setAge(age);
        passenger.setCode(getPassengerCode(response, thePassengerMix));
        passenger.getPassengerDetails().getName().setLastName(lastname);
        passenger.getPassengerDetails().setEjPlusCardNumber(ejPlus);
        passengers.add(passenger);
        return Passengers.builder().passengers(passengers).build();
    }

    public void setSaveToCustomerProfile(List<Passenger> passengers, boolean saveProfileStatus) {
        for (Passenger passenger : passengers) {
            passenger.setSaveToCustomerProfile(saveProfileStatus);
        }
    }

    public Passengers createValidRequestSaveCustomerToProfile(BasketsResponse basket) {
        return createValidRequestToAddAllPassengersForBasket(basket, true);
    }
}

