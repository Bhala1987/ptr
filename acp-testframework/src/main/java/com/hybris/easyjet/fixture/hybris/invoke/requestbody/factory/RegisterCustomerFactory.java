package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Customer;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.ContactAddress;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.PersonalDetails;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer.RegisterCustomerRequestBody;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

/**
 * Created by dwebb on 12/15/2016.
 */
@ToString
@Component
public class RegisterCustomerFactory {
    private static final Logger LOG = LogManager.getLogger(RegisterCustomerFactory.class);

    private static final DataFactory df = new DataFactory();

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
                        LOG.info(e);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }

        return false;
    }

    public static RegisterCustomerRequestBody aCustomerRequestWithMissingField(String channel, String field) {
        RegisterCustomerRequestBody request;

        if ("Digital".equals(channel)) {
            request = aDigitalProfile();
        } else {
            request = anADProfile();
        }

        switch (field) {
            case "title":
            case "firstName":
            case "lastName":
            case "email":
            case "password":
            case "phoneNumber":
            case "age":
                set(request.getPersonalDetails(), field, null);
                break;
            case "contactAddress":
                set(request, field, null);
            case "addressLine1":
            case "city":
            case "country":
            case "postalCode":
                set(request.getContactAddress().get(0), field, null);
                break;
            case "optedOutMarketing":
                set(request, field, null);
                break;
            default:
                break;
        }

        return request;
    }


    private static RegisterCustomerRequestBody anADProfile() {
        return aBasicProfile();
    }

    public static RegisterCustomerRequestBody aDigitalProfile() {
        DataFactory df = new DataFactory();
        RegisterCustomerRequestBody request = aBasicProfile();
        request.getPersonalDetails().setPassword(df.getRandomChars(15));
        return request;
    }

    public static RegisterCustomerRequestBody aDigitalChildProfile() {
        DataFactory df = new DataFactory();
        RegisterCustomerRequestBody request = aChildProfile();
        request.getPersonalDetails().setPassword(df.getRandomChars(15));
        return request;
    }

    private static RegisterCustomerRequestBody aChildProfile() {
        RegisterCustomerRequestBody request = aBasicProfile();
        request.getPersonalDetails().setAge(12);
        return request;
    }

    public static String getRandomEmail(int length) {
        StringBuilder buffer = new StringBuilder();
        while (buffer.length() < length) {
            buffer.append(shortUUID());
        }

//		this part controls the length of the returned string
//		return buffer.substring(0, length) + "@" + buffer.substring(0, length) + ".com";
//		BELOW WRITTEN @abctest.com DOMAIN FOR HYBRIS TO IGNORE EMAILS FROM TEST FRAMEWORK AND STOP SENDING THEM ACROSS
//        return "success+" + buffer.substring(0, length) + "@simulator.amazonses.com";
        return "success" + buffer.substring(0, length) + "@abctest.com";
    }

    private static String getRandomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    private static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    private static RegisterCustomerRequestBody aBasicProfile() {
        PersonalDetails personalDetails = PersonalDetails.builder()
            .age(26)
            .email(getRandomEmail(10).replace(" ", ""))
            .firstName(df.getFirstName() + getRandomString(5))
            .lastName(df.getLastName() + getRandomString(5))
            .phoneNumber(Integer.toString(df.getNumberBetween(1000000, 1000000000)))
            .title("mr")
            .ejPlusCardNumber("")
            .nifNumber("")
            .flightClubId("")
            .flightClubExpiryDate("")
            .build();

        return RegisterCustomerRequestBody.builder()
            .personalDetails(personalDetails)
            .contactAddress(
                Collections.singletonList(
                    ContactAddress.builder()
                        .addressLine1(df.getAddressLine2() + getRandomString(5))
                        .addressLine2(df.getAddressLine2())
                        .postalCode(df.getRandomChars(6))
                        .city(getRandomString(6))
                        .country("GBR")
                        .build()
                )
            )
            .optedOutMarketing(Collections.singletonList("OPT_OUT_EJ_COMMUNICATION"))
            .build();
    }

    public static Customer.ContactAddress.ContactAddressBuilder getContactAddress() {
        return Customer.ContactAddress.builder()
            .addressLine1("52, Main Street")
            .addressLine2("Flat 2B")
            .addressLine3("")
            .city("Oxford")
            .country("GBR")
            .postalCode("OX11 2ES");
    }

    public static Customer.PersonalDetails.PersonalDetailsBuilder getPersonalDetails() {
        return Customer.PersonalDetails.builder()
            .email(getRandomEmail(10))
            .type("adult")
            .age(20)
            .title("mr")
            .firstName("John")
            .lastName("Henry")
            .ejPlusCardNumber("")
            .nifNumber("876765512")
            .phoneNumber("774012854")
            .alternativePhoneNumber("0200123821")
            .flightClubId("543443")
            .flightClubExpiryDate("2017-02-09")
            .keyDates(new ArrayList<>());
    }
}
