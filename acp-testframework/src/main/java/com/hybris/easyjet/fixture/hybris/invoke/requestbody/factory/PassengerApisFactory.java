package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetAPIName;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetAPIRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.SetApiBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.IdentityDocument;
import org.fluttercode.datafactory.impl.DataFactory;

import java.lang.reflect.Field;

/**
 * Created by giuseppecioce on 14/06/2017.
 */
public class PassengerApisFactory {
    private static DataFactory df = new DataFactory();

    private PassengerApisFactory() {
    }

    public static boolean setFieldUsingReflection(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }

    public static SetAPIName aBasicName() {
        return SetAPIName.builder()
                .firstName(df.getFirstName() + df.getRandomChars(5))
                .lastName(df.getLastName() + df.getRandomChars(5))
                .fullName(df.getName() + df.getRandomChars(5))
                .middleName("")
                .title("mr")
                .build();
    }

    public static SetAPIRequestBody aBasicPassengerApis() {
        return SetAPIRequestBody.builder()
                .countryOfIssue("GBR")
                .dateOfBirth("1918-06-17")
                .documentExpiryDate("2099-01-01")
                .documentNumber("YT123" + df.getRandomChars(5).toUpperCase())
                .documentType("PASSPORT")
                .gender("MALE")
                .nationality("GBR")
                .name(SetAPIName.builder()
                        .firstName(df.getFirstName() + df.getRandomChars(5))
                        .lastName(df.getLastName() + df.getRandomChars(5))
                        .fullName(df.getName() + df.getRandomChars(5))
                        .middleName("")
                        .title("mr")
                        .build())
                .build();
    }

    public static SetAPIRequestBody aCustomerPassengerApis(String dateOfBirth, String documentExpiryDate, String documentNumber,
                                                           String documentType, String gender, String nationality, String countryOfIssue, String fullName) {
        SetAPIName name = SetAPIName.builder().fullName(fullName).build();
        return SetAPIRequestBody.builder()
                .countryOfIssue(countryOfIssue)
                .dateOfBirth(dateOfBirth)
                .documentExpiryDate(documentExpiryDate)
                .documentNumber(documentNumber)
                .documentType(documentType)
                .gender(gender)
                .nationality(nationality)
                .name(name)
                .build();
    }

    public static SetAPIRequestBody setAPIRequestBody(AbstractPassenger.PassengerAPIS passengerApis) {
        return SetAPIRequestBody.builder()
                .countryOfIssue(passengerApis.getCountryOfIssue())
                .dateOfBirth(passengerApis.getDateOfBirth())
                .documentExpiryDate(passengerApis.getDocumentExpiryDate())
                .documentNumber(passengerApis.getDocumentNumber())
                .documentType(passengerApis.getDocumentType())
                .gender(passengerApis.getGender())
                .nationality(passengerApis.getNationality())
                .name(SetAPIName.builder()
                        .firstName(passengerApis.getName().getFirstName())
                        .lastName(passengerApis.getName().getLastName())
                        .fullName(passengerApis.getName().getFirstName() + " " + passengerApis.getName().getLastName())
                        .middleName(passengerApis.getName().getMiddleName())
                        .title(passengerApis.getName().getTitle())
                        .build())
                .build();
    }


    public static SetApiBookingRequestBody setAPIBookingRequestBody(AbstractPassenger.PassengerAPIS passengerApis) {
        return SetApiBookingRequestBody.builder()
                .api(SetApiBookingRequestBody.Api.builder()
                        .countryOfIssue(passengerApis.getCountryOfIssue())
                        .dateOfBirth(passengerApis.getDateOfBirth())
                        .documentExpiryDate(passengerApis.getDocumentExpiryDate())
                        .documentNumber(passengerApis.getDocumentNumber())
                        .documentType(passengerApis.getDocumentType())
                        .gender(passengerApis.getGender())
                        .nationality(passengerApis.getNationality())
                        .name(SetAPIName.builder()
                                .firstName(passengerApis.getName().getFirstName())
                                .lastName(passengerApis.getName().getLastName())
                                .fullName(passengerApis.getName().getFirstName() + " " + passengerApis.getName().getLastName())
                                .middleName(passengerApis.getName().getMiddleName())
                                .title(passengerApis.getName().getTitle())
                                .build())
                        .build())
                .addToSavedPassengerCode("")
                .build();
    }

    public static SetApiBookingRequestBody aBasicBookingPassengerApis() {
        return SetApiBookingRequestBody.builder()
                .api(SetApiBookingRequestBody.Api.builder()
                        .countryOfIssue("GBR")
                        .dateOfBirth("1918-06-17")
                        .documentExpiryDate("2099-01-01")
                        .documentNumber("YT123" + df.getRandomChars(5).toUpperCase())
                        .documentType("PASSPORT")
                        .gender("MALE")
                        .nationality("GBR")
                        .name(SetAPIName.builder()
                                .firstName(df.getFirstName() + df.getRandomChars(5))
                                .lastName(df.getLastName() + df.getRandomChars(5))
                                .fullName(df.getName() + df.getRandomChars(5))
                                .middleName("")
                                .title("mr")
                                .build())
                        .build())
                .addToSavedPassengerCode("")
                .build();
    }

    public static SetApiBookingRequestBody aBasicBookingPassengerApisFromIdentityDocument(IdentityDocument identityDocument, String passenger) {
        return SetApiBookingRequestBody.builder()
                .api(SetApiBookingRequestBody.Api.builder()
                        .countryOfIssue(identityDocument.getCountryOfIssue())
                        .dateOfBirth(identityDocument.getDateOfBirth())
                        .documentExpiryDate(identityDocument.getDocumentExpiryDate())
                        .documentNumber(identityDocument.getDocumentNumber())
                        .documentType(identityDocument.getDocumentType())
                        .gender(identityDocument.getGender())
                        .nationality(identityDocument.getNationality())
                        .name(SetAPIName.builder()
                                .firstName(identityDocument.getName().getFirstName())
                                .lastName(identityDocument.getName().getLastName())
                                .fullName(identityDocument.getName().getFullName())
                                .middleName(identityDocument.getName().getMiddleName())
                                .title(identityDocument.getName().getTitle())
                                .build())
                        .build())
                .addToSavedPassengerCode(passenger)
                .build();
    }


}
