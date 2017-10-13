package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.customerpreferences.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by jamie on 29/03/2017.
 */
public class UpdateCustomerPreferencesFactory {

    public static UpdateCustomerPreferencesFullRequestBody getFullRequestBody() {
        return UpdateCustomerPreferencesFullRequestBody
                .builder()
                .travelPreferences(getTravelPreferences())
                .communicationPreferences(getCommunicationPreferences())
                .ancillaryPreferences(getAncillaryPreferences())
                .build();
    }

    public static UpdateCustomerPreferencesTravelRequestBody getTravelRequestBody() {
        return UpdateCustomerPreferencesTravelRequestBody
                .builder()
                .travelPreferences(getTravelPreferences())
                .build();
    }

    public static UpdateCustomerPreferencesCommunicationRequestBody getCommunicationRequestBody() {
        return UpdateCustomerPreferencesCommunicationRequestBody
                .builder()
                .communicationPreferences(getCommunicationPreferences())
                .build();
    }

    public static UpdateCustomerPreferencesAncillaryRequestBody getAncillaryRequestBody() {
        return UpdateCustomerPreferencesAncillaryRequestBody
                .builder()
                .ancillaryPreferences(getAncillaryPreferences())
                .build();
    }

    private static AncillaryPreferences getAncillaryPreferences() {
        return AncillaryPreferences
                .builder()
                .seatingPreferences(new ArrayList<String>() {
                    {
                        add("WINDOW");
                    }
                })
                .holdBagQuantity("2")
                .holdBagWeight("_20")
                .seatNumber("1A")
                .build();
    }

    private static TravelPreferences getTravelPreferences() {
        return TravelPreferences
                .builder()
                .preferredAirports(new ArrayList<String>() {
                    {
                        add("LTN");
                    }
                })
                .travellingPeriod(getTravellingPeriod())
                .travellingSeasons(new ArrayList<String>() {
                                       {
                                           add("SUMMER");
                                       }
                                   }
                )
                .travellingTo(new ArrayList<String>() {
                                  {
                                      add("VCE");
                                  }
                              }
                )
                .travellingWhen(new ArrayList<String>() {
                                    {
                                        add("WITHIN_SIX_MONTHS");
                                    }
                                }
                )
                .travellingWith(new ArrayList<String>() {
                                    {
                                        add("COUPLE");
                                    }
                                }
                )
                .tripTypes(new ArrayList<String>() {
                    {
                        add("CULTURE");
                    }
                }).build();
    }

    private static CommunicationPreferences getCommunicationPreferences() {
        return CommunicationPreferences
                .builder()
                .contactMethods(new ArrayList<String>() {
                    {
                        add("EMAIL");
                    }
                })
                .contactTypes(new ArrayList<String>() {
                    {
                        add("SALES_AND_OFFERS");
                    }
                })
                .frequency("DAILY")
                .keyDates(getKeyDates())
                .optedOutMarketing(new ArrayList<String>() {
                    {
                        add("OPT_OUT_EJ_COMMUNICATION");
                    }
                })
                .optedOutPeriod(getOptedOutPeriod())
                .build();
    }

    private static CustomerProfileResponse.Period getOptedOutPeriod() {
        CustomerProfileResponse.Period myPeriod = new CustomerProfileResponse.Period();
        myPeriod.setFromDate(getTodayMinusOne());
        myPeriod.setToDate(getTodayPlusOne());
        return myPeriod;
    }

    private static ArrayList<KeyDate> getKeyDates() {
        return new ArrayList<KeyDate>() {
            {
                add(KeyDate
                        .builder()
                        .type("graduation")
                        .day("01")
                        .month("12")
                        .build()
                );
            }
        };
    }

    private static CustomerProfileResponse.Period getTravellingPeriod() {

        CustomerProfileResponse.Period myPeriod = new CustomerProfileResponse.Period();
        myPeriod.setFromDate(getTodayMinusOne());
        myPeriod.setToDate(getTodayPlusOne());

        return myPeriod;
    }

    private static String getTodayPlusOne() {
        return formatDateToString(LocalDate.now());
    }

    private static String getTodayMinusOne() {
        LocalDate myDate = LocalDate.now();
        return formatDateToString(myDate.minusDays(1));
    }

    public static String formatDateToString(LocalDate aDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        return aDate.format(formatter);
    }

}
