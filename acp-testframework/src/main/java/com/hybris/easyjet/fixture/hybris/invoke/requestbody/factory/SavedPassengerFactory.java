package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateIdentityDocumentRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateSSRRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.savedpassenger.AddUpdateSavedPassengerRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.SavedSSRs;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


/**
 * Created by giuseppecioce on 20/02/2017.
 */
@ToString
@Component
public class SavedPassengerFactory {
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

    public static String getFutureDateFromNow() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String date = sdf.format(cal.getTime());
        String year = date.split("-")[0];
        year = (Integer.parseInt(year) + 1) + "";
        date = date.replace(date.split("-")[0], year + "");
        return date;
    }

    public static AddUpdateSavedPassengerRequestBody aBasicProfileSavedPassenger() {
        return AddUpdateSavedPassengerRequestBody.builder()
                .type("adult")
                .title("mr")
                .firstName(df.getFirstName() + getRandomString(5))
                .lastName(df.getLastName() + getRandomString(5))
                .age(25)
                .phoneNumber("44" + Integer.toString(df.getNumberBetween(1000000, 1000000000)))

                .ejPlusCardNumber("")
                .email(RegisterCustomerFactory.getRandomEmail(10))
                .nifNumber(Integer.toString(df.getNumberBetween(100000000, 1000000000)))
                .flightClubId(Integer.toString(df.getNumberBetween(100000000, 1000000000)))
                .flightClubExpiryDate(getFutureDateFromNow())
                .build();
    }

    public static AddUpdateSavedPassengerRequestBody missingFieldProfileSavedPassenger(String field) {
        AddUpdateSavedPassengerRequestBody request = aBasicProfileSavedPassenger();
        switch (field) {
            case "type":
            case "title":
            case "firstName":
            case "lastName":
            case "age":
            case "phoneNumber":
                set(request, field, null);
                break;
            default:
                break;
        }
        return request;
    }

    public static AddUpdateSavedPassengerRequestBody aCompleteProfileSavedPassenger(String membership, String surname) {
        return AddUpdateSavedPassengerRequestBody.builder()
                .type("adult")
                .title("mr")
                .firstName(df.getFirstName() + getRandomString(5))
                .lastName(surname)
                .age(25)
                .phoneNumber("44" + Integer.toString(df.getNumberBetween(1000000, 1000000000)))

                .ejPlusCardNumber(membership)
                .email(df.getEmailAddress())
                .nifNumber(Integer.toString(df.getNumberBetween(100000000, 1000000000)))
                .flightClubId(Integer.toString(df.getNumberBetween(100000000, 1000000000)))
                .flightClubExpiryDate(getFutureDateFromNow())
                .build();
    }

    public static AddUpdateIdentityDocumentRequestBody aBasicProfileIdentitytDocument() {
        return AddUpdateIdentityDocumentRequestBody.builder()
                .name(Name.builder()
                        .title("Ms")
                        .firstName(df.getFirstName())
                        .middleName("")
                        .lastName("Jones")
                        .fullName("")
                        .build())
                .dateOfBirth("1991-05-23")
                .documentExpiryDate(getFutureDateFromNow())
                .gender("FEMALE")
                .nationality("GBR")
                .countryOfIssue("GBR")
                .documentType("PASSPORT")
                .documentNumber(Integer.toString(df.getNumberBetween(100000000, 1000000000)))
                .build();
    }

    public static AddUpdateSSRRequestBody addProfileSSR(List<String> ssrCode) {

        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr ssrToAdd = new SavedSSRs.Ssr();

        for (String ssr : ssrCode) {
            ssrToAdd.setCode(ssr);
            ssrToAdd.setIsTandCsAccepted(true);
            mySsrList.add(ssrToAdd);
        }

        return AddUpdateSSRRequestBody.builder()
                .ssrs(mySsrList)
                .build();
    }

    public static AddUpdateSavedPassengerRequestBody aBasicSSRProfileSavedPassenger(List<String> ssrCodes) {
        return AddUpdateSavedPassengerRequestBody.builder()
                .type("adult")
                .title("mr")
                .firstName(df.getFirstName() + getRandomString(5))
                .lastName(df.getLastName() + getRandomString(5))
                .age(25)
                .phoneNumber("44" + Integer.toString(df.getNumberBetween(1000000, 1000000000)))

                .ejPlusCardNumber("")
                .email(df.getEmailAddress())
                .nifNumber(Integer.toString(df.getNumberBetween(100000000, 1000000000)))
                .flightClubId(Integer.toString(df.getNumberBetween(100000000, 1000000000)))
                .flightClubExpiryDate(getFutureDateFromNow())
                .savedSSRs(buildSsrBodyFor(ssrCodes))
                .build();
    }

    public static AddUpdateSSRRequestBody buildSsrBodyFor(List<String> ssrCode) {

        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr ssrToAdd = new SavedSSRs.Ssr();

        for (String ssr : ssrCode) {
            ssrToAdd.setCode(ssr);
            ssrToAdd.setIsTandCsAccepted(true);
            mySsrList.add(ssrToAdd);
        }

        return AddUpdateSSRRequestBody.builder()
                .ssrs(mySsrList)
                .build();
    }

    public AddUpdateSavedPassengerRequestBody aBasicSSRProfileSavedPassengerWithoutTsCs(List<String> ssrCodes) {

        return AddUpdateSavedPassengerRequestBody.builder()
                .type("adult")
                .title("mr")
                .firstName(df.getFirstName() + getRandomString(5))
                .lastName(df.getLastName() + getRandomString(5))
                .age(25)
                .phoneNumber("44" + Integer.toString(df.getNumberBetween(1000000, 1000000000)))
                .ejPlusCardNumber("")
                .email(df.getEmailAddress())
                .nifNumber(Integer.toString(df.getNumberBetween(100000000, 1000000000)))
                .flightClubId(Integer.toString(df.getNumberBetween(100000000, 1000000000)))
                .flightClubExpiryDate(getFutureDateFromNow())
                .savedSSRs(buildSsrBodyForWithoutTsCs(ssrCodes))
                .build();
    }

    private AddUpdateSSRRequestBody buildSsrBodyForWithoutTsCs(List<String> ssrCodes) {
        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr ssrToAdd = new SavedSSRs.Ssr();

        for (String ssr : ssrCodes) {
            ssrToAdd.setCode(ssr);
            ssrToAdd.setIsTandCsAccepted(null);
            mySsrList.add(ssrToAdd);
        }

        return AddUpdateSSRRequestBody.builder()
                .ssrs(mySsrList)
                .build();
    }

    public AddUpdateIdentityDocumentRequestBody aBasicProfileIdentityDocument(String dateOfBirth, String documentExpiryDate, String documentNumber, String documentType, String gender, String nationality, String countryOfIssue, String fullName) {
        return AddUpdateIdentityDocumentRequestBody.builder()
                .name(Name.builder()
                        .title("Ms")
                        .firstName(df.getFirstName())
                        .middleName("")
                        .lastName("Jones")
                        .fullName(fullName)
                        .build())
                .dateOfBirth(dateOfBirth)
                .documentExpiryDate(documentExpiryDate)
                .gender(gender)
                .nationality(nationality)
                .countryOfIssue(countryOfIssue)
                .documentType(documentType)
                .documentNumber(documentNumber)
                .build();
    }
}
