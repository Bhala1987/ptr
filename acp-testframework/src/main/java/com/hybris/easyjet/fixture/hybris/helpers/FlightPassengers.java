package com.hybris.easyjet.fixture.hybris.helpers;


import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.Passenger;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by daniel on 01/12/2016.
 */
@Data
public class FlightPassengers {

    private List<Passenger> passengers = new ArrayList<>();

    /**
     * The constructor provide to init in the proper way the passenger mix in the basket
     * Two different way are allow from the method:
     * classic approach: does not consider the additional seat for the passenger
     * examples: 1 audult, 1 child, 1 infant OOS, 1 infant OL. The split on the passenger have to be ','
     * new approach: does consider the additional seat for the passenger
     * examples: 1,2 adult; 2,1 child; 2,1 infant. The split on the passenger have to be ';'. 1,2 adult means 1 adult with 2 additional seat. 2,1 infant means 2 infant, 1 on seat and 1 on lap
     * Also if you want just a type of passenger, it is required insert the ';' anyway like <1,1 adult;>
     * @param passengersString
     */
    public FlightPassengers(String passengersString) {
        if(passengersString.contains(";")) {
            buildPassengerWithAdditionalSeat(passengersString);
        } else {
            buildPassengerWithoutAdditionalSeat(passengersString);
        }
    }

    private void buildPassengerWithAdditionalSeat(String passengersString) {
        List<String> types = Arrays.asList(passengersString.split("\\s*;\\s*"));
        for (String t : types) {
            String[] a = t.split("\\s+");
            String[] quantityAndAdditional = a[0].split(",");
            if (t.toLowerCase().contains(CommonConstants.ADULT)) {
                passengers.add(setPassenger(CommonConstants.ADULT, Integer.parseInt(quantityAndAdditional[0]), quantityAndAdditional.length > 1 ? Integer.parseInt(quantityAndAdditional[1]) : 0, false));
            } else if (t.toLowerCase().contains(CommonConstants.CHILD)) {
                passengers.add(setPassenger(CommonConstants.CHILD, Integer.parseInt(quantityAndAdditional[0]), quantityAndAdditional.length > 1 ? Integer.parseInt(quantityAndAdditional[1]) : 0, false));
            } else if (t.toLowerCase().contains(CommonConstants.INFANT)) {
                if(Integer.parseInt(quantityAndAdditional[0]) == Integer.parseInt(quantityAndAdditional[1])) {
                    passengers.add(setPassenger(CommonConstants.INFANT, Integer.parseInt(quantityAndAdditional[0]), 0, Integer.parseInt(quantityAndAdditional[1]) > 0 ? true : false));
                } else if(Integer.parseInt(quantityAndAdditional[1]) == 0) {
                    passengers.add(setPassenger(CommonConstants.INFANT, Integer.parseInt(quantityAndAdditional[0]), 0,  false));
                } else {
                    int numInfantOnLap = Integer.parseInt(quantityAndAdditional[0]) - Integer.parseInt(quantityAndAdditional[1]);
                    int numInfantOnSeat = Integer.parseInt(quantityAndAdditional[0]) - numInfantOnLap;
                    passengers.add(setPassenger(CommonConstants.INFANT, numInfantOnLap, 0, false));
                    passengers.add(setPassenger(CommonConstants.INFANT, numInfantOnSeat, 0, true));
                }
            }
        }
    }

    private void buildPassengerWithoutAdditionalSeat(String passengersString) {
        List<String> types = Arrays.asList(passengersString.split("\\s*,\\s*"));
        for (String t : types) {
            String[] a = t.split("\\s+");
            if (t.toLowerCase().contains("adult")) {
                passengers.add(setPassenger("adult", Integer.parseInt(a[0]), 0, false));
            }
            if (t.toLowerCase().contains("child")) {
                passengers.add(setPassenger("child", Integer.parseInt(a[0]), 0, false));
            }
            if (t.toLowerCase().contains("infant")) {
                if (a.length > 2) {

                    Passenger pax = null;
                    switch (a[2]) {
                        case "OOS":
                            pax = setPassenger("infant", Integer.parseInt(a[0]), 0, true);
                            passengers.add(pax);
                            break;
                        case "OL":
                            pax = setPassenger("infant", Integer.parseInt(a[0]), 0, false);
                            passengers.add(pax);
                            break;
                        default:
                            pax = setPassenger("infant", Integer.parseInt(a[0]), 0, false);
                            passengers.add(pax);
                            break;
                    }

                    if (a.length == 5) {
                        if ("Additional".equalsIgnoreCase(a[4].toLowerCase())) {
                            pax.setAdditionalSeats(Integer.parseInt(a[3]));
                        }
                    }
                } else {
                    passengers.add(setPassenger("infant", Integer.parseInt(a[0]), 0, false));
                }
            }
        }
    }

    private Passenger setPassenger(String typeOfPassenger, int numberOfPassengers, int additionalSeats, boolean infantOnSeat) {
        return Passenger.builder().passengerType(typeOfPassenger).quantity(numberOfPassengers).additionalSeats(additionalSeats).infantOnSeat(infantOnSeat).build();
    }

    public int getTotalNumberOfPassengers() {
        int totalNumberOfPassengers = 0;
        for (Passenger passenger : passengers) {
            totalNumberOfPassengers += passenger.getQuantity();
        }
        return totalNumberOfPassengers;
    }

    /**
     * The following method can be used to compose a classic passenger mix with additional seat
     *
     * @param passengerMix normal passenger mix with ',' or ';' to split the different passenger type
     * @param quantitySeat quantity of additional seat that you wish add on each passenger
     * @return a new passenger mix with ';' to split the different passenger type and ',' to split the passenger type from the number of additional seat
     */
    public static String managePassengerMixWithAdditionalSeat(String passengerMix, int quantitySeat) {
        List<String> passengerMixAdditionalSeat = new ArrayList<>();
        List<String> passengerMixAdditionalSeatFinal = new ArrayList<>();
        if (passengerMix.contains(",")) {
            Collections.addAll(passengerMixAdditionalSeat, passengerMix.split(","));
        } else if (passengerMix.contains(";")) {
            Collections.addAll(passengerMixAdditionalSeat, passengerMix.split(";"));
        }

        int cycle = 0;
        for (String s : passengerMixAdditionalSeat) {
            if (s.contains(" ")) {
                String[] item = s.split(" ");
                String passengerWitAdditionalSeat = item[0 + cycle].concat(",").concat(String.valueOf(quantitySeat)).concat(" ").concat(item[1 + cycle]);
                passengerMixAdditionalSeatFinal.add(passengerWitAdditionalSeat);
            }
            if (cycle == 0)
                cycle++;
        }

        return String.join(";", passengerMixAdditionalSeatFinal);
    }
}
