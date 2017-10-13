package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.UpdatePasswordRequestBody;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Created by giuseppecioce on 03/03/2017.
 */
@ToString
@Component
public class AccountPasswordFactory {
    private static final DataFactory df = new DataFactory();
    private static Logger LOG = LogManager.getLogger(AccountPasswordFactory.class);
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
                        LOG.error(e);
                        clazz = clazz.getSuperclass();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return false;
    }

    public static String getRandomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static UpdatePasswordRequestBody missingFieldProfileSavedPassenger(String field, String currentToken) {
        UpdatePasswordRequestBody request = aCompleteRequestToUpdatePassword(currentToken);

        switch (field) {
            case "newPassword":
            case "currentPassword":
            case "passwordResetToken":
                set(request, field, null);
                break;
            default:
                break;
        }
        return request;
    }

    public static UpdatePasswordRequestBody aCompleteRequestToUpdatePassword(String currentToken) {
        return UpdatePasswordRequestBody.builder()
                .passwordResetToken(currentToken)
                .currentPassword("")
                .newPassword(getRandomString(12))
                .build();
    }
}
