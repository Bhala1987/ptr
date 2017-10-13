package com.hybris.easyjet.fixture.wiremock;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by jamie on 25/04/2017.
 */

//TODO: We shold replace this with restassured and remove unirest depends.
public class WireMockHelper {

    public static void updateWireMockTimeout(int timeout) throws UnirestException {
        Unirest.post("http://localhost:8080/__admin/settings")
                .body("{\n" +
                        "    \"fixedDelay\": "+String.valueOf(timeout)+"\n" +
                        "}")
                .asJson();
    }
}
