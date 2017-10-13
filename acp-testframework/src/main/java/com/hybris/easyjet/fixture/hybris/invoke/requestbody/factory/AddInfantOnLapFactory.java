package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.AddInfantOnLapRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.Name;
import org.fluttercode.datafactory.impl.DataFactory;

/**
 * Created by vijayapalkayyam on 27/06/2017.
 */
public class AddInfantOnLapFactory {
    private static DataFactory df = new DataFactory();
    private static final String DEFAULT_TITLE = "infant";

    private AddInfantOnLapFactory() {
    }

    public static AddInfantOnLapRequestBody getAddInfantOnLapBody() {
        return AddInfantOnLapRequestBody.builder()
                .age(0)
                .name(Name.builder()
                        .firstName(df.getFirstName() + df.getRandomChars(5))
                        .lastName(df.getLastName() + df.getRandomChars(8))
                        .fullName(df.getName() + df.getRandomChars(5))
                        .title(DEFAULT_TITLE)
                        .build())
                .build();
    }

    public static AddInfantOnLapRequestBody getAddInfantOnLapBodyWithMissing(String missingField) {
        AddInfantOnLapRequestBody body = getAddInfantOnLapBody();
        switch (missingField.toLowerCase()) {
            case "firstname":
                body.getName().setFirstName(null);
                break;
            case "lastname":
                body.getName().setLastName(null);
                break;
            case "age":
                body.setAge(null);
                break;
            case "title":
                body.getName().setTitle(null);
                break;
            default:
                break;
        }
        return body;
    }
}
