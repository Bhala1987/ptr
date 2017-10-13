package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.assertj.core.util.Lists;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.constants.CommonConstants.*;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;

/**
 * Created by giuseppecioce on 13/04/2017.
 */
@ToString
@Component

public class PurchasedSeatRequestBodyFactory {
    private static final DataFactory df = new DataFactory();
    private static Logger LOG = LogManager.getLogger(SavedPassengerFactory.class);
    private static Random random = new Random(System.currentTimeMillis());

    private static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // You might want to set modifier to public first.
                if (field.getName().equals(fieldName)) {
                    try {
                        Field fieldx = clazz.getDeclaredField(fieldName);
                        fieldx.setAccessible(true);
                        fieldx.set(object, fieldValue);
                        return true;
                    } catch (NoSuchFieldException e) {
                        clazz = clazz.getSuperclass();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return false;
    }

    private static String getRandomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    //TODO: Need to be dynamic with .type
    public static AddPurchasedSeatsRequestBody aBasicAddPurchasedSeat(String flightKey, String passengerCode, Seat purchasedSeat) {
        return AddPurchasedSeatsRequestBody.builder()
                .flightKey(flightKey)
                .passengerAndSeats(new ArrayList<PassengerAndSeat>() {{
                                       add(PassengerAndSeat.builder()
                                               .passengerId(passengerCode)
                                               .seat(Seat.builder()
                                                       .type(FARE_PRODUCT)
                                                       .code(purchasedSeat.getCode())
                                                       .price(purchasedSeat.getPrice())
                                                       .seatNumber(purchasedSeat.getSeatNumber())
                                                       .build())
                                               .additionalSeats(new ArrayList<AdditionalSeat>() {{
                                                                /*
                                                                    add(AdditionalSeat.builder()
                                                                            .type(FARE_PRODUCT)
                                                                            .type("UPFRONT")
                                                                            .price(30)
                                                                            .seatNumber("# purchased")
                                                                            .build());
                                                                */
                                                                }}
                                               ).build());
                                   }}
                )
                .build();
    }

    //TODO: Need to be dynamic with .type
    public static AddPurchasedSeatsRequestBody aMultiplePassengerAddPurchasedSeat(String flightKey, Map<String, Seat> purchasedSeat) {
        return AddPurchasedSeatsRequestBody.builder()
                .flightKey(flightKey)
                .passengerAndSeats(new ArrayList<PassengerAndSeat>() {{
                                       for (Map.Entry<String, Seat> entry : purchasedSeat.entrySet()) {
                                           System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
                                           Seat tempSeat = entry.getValue();
                                           add(PassengerAndSeat.builder()
                                                   .passengerId(entry.getKey())
                                                   .seat(Seat.builder()
                                                           .type(FARE_PRODUCT)
                                                           .code(tempSeat.getCode())
                                                           .price(tempSeat.getPrice())
                                                           .seatNumber(tempSeat.getSeatNumber())
                                                           .build())
                                                   .additionalSeats(new ArrayList<AdditionalSeat>() {{
                                                                    }}
                                                   ).build());
                                       }
                                   }}
                )
                .build();
    }

    public static RemovePurchasedSeatRequestBody aBasicRemovePurchasedSeat(String passengerCode, String purchasedSeat) {
        return RemovePurchasedSeatRequestBody.builder()
                .passengersAndSeatsNumbers(
                        new ArrayList<PassengersAndSeatsNumber>() {{
                            add(PassengersAndSeatsNumber.builder()
                                    .passengerId(passengerCode)
                                    .seats(
                                            new ArrayList<String>() {{
                                                add(purchasedSeat);
                                            }}
                                    )
                                    .build());
                        }}
                )
                .build();
    }

    public static PassengerSeatChangeRequestBody aMultiChangePurchasedSeat(Map<String, PassengerSeatChangeRequests.Seat> associatedPassengerSeat) {
        return PassengerSeatChangeRequestBody.builder()
                .passengerSeatChangeRequests(
                        new ArrayList<PassengerSeatChangeRequests>() {{
                            for (String passenger : associatedPassengerSeat.keySet()) {
                                add(PassengerSeatChangeRequests.builder()
                                        .passengerOnFlightId(passenger)
                                        .seat(
                                                associatedPassengerSeat.get(passenger)
                                        )
                                        .additionalSeats(new ArrayList<>())
                                        .build());
                            }
                        }}
                )
                .build();
    }

    public static AddPurchasedSeatsRequestBody aMultiPassengerAddPurchasedSeat(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        AddPurchasedSeatsRequestBody myBody = AddPurchasedSeatsRequestBody.builder().build();
        myBody.setFlightKey(flightKey);

        if(aChosenSeat.size() < passengers.size()) {
            throw new EasyjetCompromisedException("No enough seat for all desired passengers. Number of passenger " + passengers.size() + ". Number of available seat for type:" + aChosenSeat.size());
        }
        List<Basket.Passenger> infantPassengers = passengers.stream().filter(passenger1 -> passenger1.getPassengerDetails().getPassengerType().equalsIgnoreCase(INFANT) && !passenger1.getFareProduct().getType().equalsIgnoreCase(INFANTONLAP_PRODUCT)).collect(Collectors.toList());

        List<PassengerAndSeat> myPassengerAndSeatList = new ArrayList<>();
        final int[] index = {0};
        passengers.removeAll(infantPassengers);
        for (Basket.Passenger passenger : passengers) {
            skipCFSeats(aChosenSeat, index);
            if (CollectionUtils.isEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isEmpty(infantPassengers)) {
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
            } else if (CollectionUtils.isNotEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isNotEmpty(infantPassengers)) {

                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
                index[0]++;
                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(aChosenSeat.get(index[0]).getCode())
                                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                            .build()
                            );
                        }})
                        .build());

            }else if (CollectionUtils.isEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isNotEmpty(infantPassengers)) {

                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
                index[0]++;
                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());

            } else {
                List<Seat> continousSeats = getContinousSeatsInARow(aChosenSeat, index);
                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(continousSeats.get(0).getCode())
                        .price(continousSeats.get(0).getPrice())
                        .seatNumber(continousSeats.get(0).getSeatNumber())
                        .build();
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(continousSeats.get(1).getCode())
                                            .price(Double.valueOf(continousSeats.get(1).getPrice()))
                                            .seatNumber(continousSeats.get(1).getSeatNumber())
                                            .build()
                            );
                        }})
                        .build());
            }
            index[0]++;
        }

        myBody.setPassengerAndSeats(myPassengerAndSeatList);

        return myBody;
    }

    private static List<Seat> getContinousSeatsInARow(List<Seat> aChosenSeat, int[] index) throws EasyjetCompromisedException {
        skipCFSeats(aChosenSeat, index);
        Seat firstSeat = aChosenSeat.get(index[0]);
        index[0]++;
        skipCFSeats(aChosenSeat, index);
        Seat secondSeat = aChosenSeat.get(index[0]);

        if (checkIfTheAvailableSeatsAreConsecutive(firstSeat, secondSeat))
            return Lists.newArrayList(firstSeat, secondSeat);
        else
            return getContinousSeatsInARow(aChosenSeat, index);
    }

    private static boolean checkIfTheAvailableSeatsAreConsecutive(Seat firstSeat, Seat secondSeat) {
        return firstSeat.getSeatNumber().substring(0, firstSeat.getSeatNumber().length() - 1).equals(secondSeat.getSeatNumber().substring(0, secondSeat.getSeatNumber().length() - 1));
    }


    private static void skipCFSeats(List<Seat> aChosenSeat, int[] index) throws EasyjetCompromisedException {
        if(aChosenSeat.size()-1 < index[0])
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);

        if (aChosenSeat.get(index[0]).getSeatNumber().contains("C") ||
                aChosenSeat.get(index[0]).getSeatNumber().contains("F")) {
            index[0]++;
            skipCFSeats(aChosenSeat, index);
        }
    }

    public static AddPurchasedSeatsRequestBody aSinglePassengerAddPurchasedSeatWithAdditionalSeat(String flightKey, Basket.Passenger passenger, List<Seat> aChosenSeat, int addlSeat) throws EasyjetCompromisedException {
        AddPurchasedSeatsRequestBody myBody = AddPurchasedSeatsRequestBody.builder().build();
        myBody.setFlightKey(flightKey);

        List<PassengerAndSeat> myPassengerAndSeatList = new ArrayList<>();
        final int[] index = {0};

            if (aChosenSeat.get(index[0]).getSeatNumber().contains("C") ||
                    aChosenSeat.get(index[0]).getSeatNumber().contains("F")) {

                index[0]++;
            }

            if (CollectionUtils.isEmpty(passenger.getAdditionalSeats())) {
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
            } else if (CollectionUtils.isNotEmpty(passenger.getAdditionalSeats())) {

                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            for (int i = 0; i < addlSeat; i++) {
                                add(
                                        AdditionalSeat.builder()
                                                .type(FARE_PRODUCT)
                                                .code(aChosenSeat.get(index[0]).getCode())
                                                .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                                .build()
                                );
                                index[0]++;
                            }
                        }})
                        .build());

            }

        myBody.setPassengerAndSeats(myPassengerAndSeatList);

        return myBody;
    }

    public static AddPurchasedSeatsRequestBody aMultiPassengerAddPurchasedSeatWithAdditionalSeat(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat, int addlSeat) throws EasyjetCompromisedException {
        AddPurchasedSeatsRequestBody myBody = AddPurchasedSeatsRequestBody.builder().build();
        myBody.setFlightKey(flightKey);

        if(aChosenSeat.size() < passengers.size()) {
            throw new EasyjetCompromisedException("No enough seat for all desired passengers. Number of passenger " + passengers.size() + ". Number of available seat for type:" + aChosenSeat.size());
        }
        List<Basket.Passenger> infantPassengers = passengers.stream().filter(passenger1 -> passenger1.getPassengerDetails().getPassengerType().equalsIgnoreCase(INFANT) && !passenger1.getFareProduct().getType().equalsIgnoreCase(INFANTONLAP_PRODUCT)).collect(Collectors.toList());

        List<PassengerAndSeat> myPassengerAndSeatList = new ArrayList<>();
        final int[] index = {0};
        passengers.removeAll(infantPassengers);
        passengers.forEach(passenger -> {

            if (aChosenSeat.get(index[0]).getSeatNumber().contains("C") ||
                    aChosenSeat.get(index[0]).getSeatNumber().contains("F")) {

                index[0]++;
            }

            if (CollectionUtils.isEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isEmpty(infantPassengers)) {
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
            } else if (CollectionUtils.isNotEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isNotEmpty(infantPassengers)) {

                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
                index[0]++;
                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            for (int i = 0; i < addlSeat; i++) {
                                add(
                                        AdditionalSeat.builder()
                                                .type(FARE_PRODUCT)
                                                .code(aChosenSeat.get(index[0]).getCode())
                                                .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                                .build()
                                );
                                index[0]++;
                            }
                        }})
                        .build());

            }else if (CollectionUtils.isEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isNotEmpty(infantPassengers)) {

                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
                index[0]++;
                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());

            } else {

                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                         for (int i = 0; i < addlSeat; i++) {
                             add(
                                     AdditionalSeat.builder()
                                             .type(FARE_PRODUCT)
                                             .code(aChosenSeat.get(index[0]).getCode())
                                             .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                             .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                             .build()
                             );
                             index[0]++;
                         }
                        }})
                        .build());
            }
            index[0]++;
        });

        myBody.setPassengerAndSeats(myPassengerAndSeatList);

        return myBody;
    }

    public static AddPurchasedSeatsRequestBody aMultiPassengerAddContinuousPurchasedSeat(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        AddPurchasedSeatsRequestBody myBody = AddPurchasedSeatsRequestBody.builder().build();
        myBody.setFlightKey(flightKey);

        if(aChosenSeat.size() < passengers.size()) {
            throw new EasyjetCompromisedException("No enough seat for all desired passengers. Number of passenger " + passengers.size() + ". Number of available seat for type:" + aChosenSeat.size());
        }
        List<Basket.Passenger> infantPassengers = passengers.stream().filter(passenger1 -> passenger1.getPassengerDetails().getPassengerType().equalsIgnoreCase(INFANT) && !passenger1.getFareProduct().getType().equalsIgnoreCase(INFANTONLAP_PRODUCT)).collect(Collectors.toList());

        List<PassengerAndSeat> myPassengerAndSeatList = new ArrayList<>();
        final int[] index = {0};
        passengers.removeAll(infantPassengers);
        passengers.forEach(passenger -> {

            if (CollectionUtils.isEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isEmpty(infantPassengers)) {
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
            } else if (CollectionUtils.isNotEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isNotEmpty(infantPassengers)) {

                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
                index[0]++;
                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(aChosenSeat.get(index[0]).getCode())
                                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                            .build()
                            );
                        }})
                        .build());

            }else if (CollectionUtils.isEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isNotEmpty(infantPassengers)) {

                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());
                index[0]++;
                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {
                        })
                        .build());

            } else {

                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(aChosenSeat.get(index[0]).getCode())
                                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                            .build()
                            );
                        }})
                        .build());
            }
            index[0]++;
        });

        myBody.setPassengerAndSeats(myPassengerAndSeatList);

        return myBody;
    }

    public static AddPurchasedSeatsRequestBody aMultiPassengerAddPurchasedSeatWithoutAdditionalSeat(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        AddPurchasedSeatsRequestBody myBody = AddPurchasedSeatsRequestBody.builder().build();
        myBody.setFlightKey(flightKey);

        if(aChosenSeat.size() < passengers.size()) {
            throw new EasyjetCompromisedException("No enough seat for all desired passengers. Number of passenger " + passengers.size() + ". Number of available seat for type:" + aChosenSeat.size());
        }
        List<Basket.Passenger> infantPassengers = passengers.stream().filter(passenger1 -> passenger1.getPassengerDetails().getPassengerType().equalsIgnoreCase(INFANT) && !passenger1.getFareProduct().getType().equalsIgnoreCase(INFANTONLAP_PRODUCT)).collect(Collectors.toList());
        List<PassengerAndSeat> myPassengerAndSeatList = new ArrayList<>();
        final int[] index = {0};
        passengers.removeAll(infantPassengers);
        passengers.forEach(passenger -> {

            if (CollectionUtils.isEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isEmpty(infantPassengers)) {
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .build());
            } else if (CollectionUtils.isNotEmpty(infantPassengers)) {

                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .build());
                index[0]++;
                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .build());

            }else if (CollectionUtils.isEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isNotEmpty(infantPassengers)) {

                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode()).seat(
                        Seat.builder()
                                .type(FARE_PRODUCT)
                                .code(aChosenSeat.get(index[0]).getCode())
                                .price(aChosenSeat.get(index[0]).getPrice())
                                .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                .build()

                )
                        .build());
                index[0]++;
                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .build());

            } else {

                Seat seat = Seat.builder()
                        .type(FARE_PRODUCT)
                        .code(aChosenSeat.get(index[0]).getCode())
                        .price(aChosenSeat.get(index[0]).getPrice())
                        .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                        .build();
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .seat(seat)
                        .build());
            }
            index[0]++;
        });

        myBody.setPassengerAndSeats(myPassengerAndSeatList);

        return myBody;
    }

    public static AddPurchasedSeatsRequestBody aMultiPassengerAddPurchasedSeatWithoutPrimarySeat(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat) throws EasyjetCompromisedException {
        AddPurchasedSeatsRequestBody myBody = AddPurchasedSeatsRequestBody.builder().build();
        myBody.setFlightKey(flightKey);

        if(aChosenSeat.size() < passengers.size()) {
            throw new EasyjetCompromisedException("No enough seat for all desired passengers. Number of passenger " + passengers.size() + ". Number of available seat for type:" + aChosenSeat.size());
        }
        List<Basket.Passenger> infantPassengers = passengers.stream().filter(passenger1 -> passenger1.getPassengerDetails().getPassengerType().equalsIgnoreCase(INFANT) && !passenger1.getFareProduct().getType().equalsIgnoreCase(INFANTONLAP_PRODUCT)).collect(Collectors.toList());
        List<PassengerAndSeat> myPassengerAndSeatList = new ArrayList<>();
        final int[] index = {0};
        passengers.removeAll(infantPassengers);
        passengers.forEach(passenger -> {

            if (CollectionUtils.isNotEmpty(passenger.getAdditionalSeats()) && CollectionUtils.isEmpty(infantPassengers)) {
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(aChosenSeat.get(index[0]).getCode())
                                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                            .build()
                            );
                        }
                        })
                        .build());
            } else if (CollectionUtils.isNotEmpty(infantPassengers)) {
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode())
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(aChosenSeat.get(index[0]).getCode())
                                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                            .build()
                            );
                        }
                        })
                        .build());
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(aChosenSeat.get(index[0]).getCode())
                                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                            .build()
                            );
                        }})
                        .build());

            }else if (CollectionUtils.isNotEmpty(infantPassengers)) {

                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(infantPassengers.get(0).getCode())
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(aChosenSeat.get(index[0]).getCode())
                                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                            .build()
                            );
                        }
                        })
                        .build());
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(aChosenSeat.get(index[0]).getCode())
                                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                            .build()
                            );
                        }
                        })
                        .build());

            } else {
                index[0]++;
                myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode())
                        .additionalSeats(new ArrayList<AdditionalSeat>() {{
                            add(
                                    AdditionalSeat.builder()
                                            .type(FARE_PRODUCT)
                                            .code(aChosenSeat.get(index[0]).getCode())
                                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                            .build()
                            );
                        }})
                        .build());
            }
            index[0]++;
        });

        myBody.setPassengerAndSeats(myPassengerAndSeatList);

        return myBody;
    }

    public static AddPurchasedSeatsRequestBody aBasicAddPurchasedSeatWithAdditionalSeat(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat, int additionalSeat) {

        AddPurchasedSeatsRequestBody myBody = AddPurchasedSeatsRequestBody.builder().build();
        myBody.setFlightKey(flightKey);

        List<PassengerAndSeat> myPassengerAndSeatList = new ArrayList<>();
        final int[] index = {0};

        passengers.forEach(passenger -> {

            myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode()).seat(
                    Seat.builder()
                            .type(FARE_PRODUCT)
                            .code(aChosenSeat.get(index[0]).getCode())
                            .price(aChosenSeat.get(index[0]).getPrice())
                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                            .build()

            ).additionalSeats(
                    new ArrayList<AdditionalSeat>() {{
                            int count = 0;
                            while(count < additionalSeat) {
                                add(AdditionalSeat.builder()
                                        .type(FARE_PRODUCT)
                                        .code(aChosenSeat.get(index[0]).getCode())
                                        .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                        .seatNumber(aChosenSeat.get(index[0] + 1).getSeatNumber())
                                        .build());
                                index[0]++;
                                count ++;
                            }
                    }}
                    ).build());
            index[0]++;
        });

        myBody.setPassengerAndSeats(myPassengerAndSeatList);

        return myBody;
    }

    public static AddPurchasedSeatsRequestBody anAddPurchasedSeatWithAdditionalSeat(String flightKey, List<Basket.Passenger> passengers, List<Seat> aChosenSeat, int additionalSeat) {

        AddPurchasedSeatsRequestBody myBody = AddPurchasedSeatsRequestBody.builder().build();
        myBody.setFlightKey(flightKey);

        List<PassengerAndSeat> myPassengerAndSeatList = new ArrayList<>();
        final int[] index = {0};

        passengers.forEach(passenger -> {

            if (aChosenSeat.get(index[0]).getSeatNumber().contains("C")
                    || aChosenSeat.get(index[0]).getSeatNumber().contains("F")){

                index[0]++;
            }

            myPassengerAndSeatList.add(PassengerAndSeat.builder().passengerId(passenger.getCode()).seat(
                    Seat.builder()
                            .type(FARE_PRODUCT)
                            .code(aChosenSeat.get(index[0]).getCode())
                            .price(aChosenSeat.get(index[0]).getPrice())
                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                            .build()

            ).additionalSeats(
                    new ArrayList<AdditionalSeat>() {{
                        int count = 0;
                        while(count < additionalSeat) {
                            add(AdditionalSeat.builder()
                                    .type(FARE_PRODUCT)
                                    .code(aChosenSeat.get(index[0]).getCode())
                                    .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                    .seatNumber(aChosenSeat.get(index[0] + 1).getSeatNumber())
                                    .build());
                            index[0]++;
                            count ++;
                        }
                    }}
            ).build());
            index[0]++;
        });

        myBody.setPassengerAndSeats(myPassengerAndSeatList);

        return myBody;
    }

    public static PassengerSeatChangeRequestBody aBasicChangeSeatWithAdditionalSeat(List<Basket.Passenger> passengers, List<Seat> aChosenSeat, int additionalSeat) {
        PassengerSeatChangeRequestBody myBody = PassengerSeatChangeRequestBody.builder().build();
        List<PassengerSeatChangeRequests> myPassengerAndSeatList = new ArrayList<>();
        final int[] index = {0};
        passengers.forEach(passenger -> {

            myPassengerAndSeatList.add(PassengerSeatChangeRequests.builder().passengerOnFlightId(passenger.getCode()).seat(
                    PassengerSeatChangeRequests.Seat.builder()
                            .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                            .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                            .build()

            )

                    .additionalSeats(new ArrayList<PassengerSeatChangeRequests.Seats>() {{

                                         int count = 0;
                                         while (count < additionalSeat) {
                                             add(PassengerSeatChangeRequests.Seats.builder().seat(
                                                     PassengerSeatChangeRequests.Seat.builder()
                                                     .price(Double.valueOf(aChosenSeat.get(index[0]).getPrice()))
                                                     .seatNumber(aChosenSeat.get(index[0]).getSeatNumber())
                                                     .build()).build());
                                             index[0]++;
                                             count++;
                                         }

                                     }}
                    ).build());
        });


        myBody.setPassengerSeatChangeRequests(myPassengerAndSeatList);

        return myBody;
    }

}
