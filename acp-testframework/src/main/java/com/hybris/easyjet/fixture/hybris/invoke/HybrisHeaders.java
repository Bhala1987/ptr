package com.hybris.easyjet.fixture.hybris.invoke;

import com.hybris.easyjet.fixture.IHeaders;
import com.hybris.easyjet.fixture.hybris.helpers.DateFormat;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;

/**
 * Created by webbd on 10/31/2016.
 * this class represents a set of headers that can be sent to a hybris service
 * it offers builder pattern construction as well as standard getter/setter instantiation
 * the method get() returns a multivaluedMap which can be passed directly into the jersey client, which it is designed
 * to work with
 */
@Data
@Builder
public final class HybrisHeaders implements IHeaders {

    public static final String TRUE = "true";
    public static final String INVALID = "Invalid";
    public static final String VALID = "Valid";
    public static final String FALSE = "false";
    public String xTest;
    private String prefer;
    private String authorization;
    private String xPosId;
    private String acceptLanguage;
    private String connection;
    private String acceptEncoding;
    private String xClientTransactionId;
    private String acceptCharset;
    private String accept;
    private String xForwardedFor;
    private String date;
    private String debug;
    private String xOfficeId;
    private String xCorporateId;
    private String xApplicationId;
    private String xCountry;
    private String cookie;

    private static HybrisHeadersBuilder getBaseBuilder(String channel) {

        if (channel == null) {
            fail("Cannot set xPosId to NULL");
        }

        HybrisHeadersBuilder builder = HybrisHeaders.builder()
                .prefer("FULL")
                .xPosId(channel)
                .connection("keep-alive")
                .acceptEncoding("application/gzip")
                .xClientTransactionId(getNewClientTransactionIdHeaderValue())
                .acceptCharset("UTF-8")
                .accept("application/json")
                .xForwardedFor("123")
                .date(new DateFormat().today().asUK());

        return addSystemPropertyBasedHeaders(builder, channel);
    }

    public static HybrisHeaders getBasicHeader() {

        HybrisHeaders headers = HybrisHeaders.builder()
                .prefer("FULL")
                .acceptCharset("UTF-8")
                .accept("application/json")
                .build();

        return headers;
    }

    public static String getNewClientTransactionIdHeaderValue() {
        return UUID.randomUUID().toString();
    }

    private static HybrisHeadersBuilder addSystemPropertyBasedHeaders(HybrisHeadersBuilder builder, String channel) {
        if (System.getProperty("mocked", FALSE).equals(TRUE)) {
            builder.xTest("true");
        }

        if (System.getProperty("debug", FALSE).equals(TRUE)) {
            builder.debug("true");
        }

        // If the channel passed "supports" XML responses.
        if (System.getProperty("AsXml", FALSE).equals(TRUE) && Arrays.asList(HybrisService.ALLOWABLE_XML_CHANNELS).contains(channel)) {
            builder.accept("application/xml");
        }

        return builder;
    }

    public static HybrisHeadersBuilder getValid(String channel) {
        return getBaseBuilder(channel);
    }

    public static HybrisHeadersBuilder getValidWithDealInfo(String channel, String applicationId, String officeId, String corporateId) {
        HybrisHeadersBuilder builder = getBaseBuilder(channel);

        builder
                .xApplicationId(applicationId)
                .xOfficeId(officeId)
                .xCorporateId(corporateId);

        return builder;
    }

    public static HybrisHeadersBuilder getValidXClientTransactionId(String channel, String xClientTransactionId) {
        HybrisHeadersBuilder builder = getBaseBuilder(channel);

        builder
                .xClientTransactionId(xClientTransactionId);

        return builder;
    }

    public static HybrisHeadersBuilder getValidWithToken(String channel, String token) {
        HybrisHeadersBuilder builder = getBaseBuilder(channel);

        if (token != null) {
            builder.authorization("Bearer " + token);
        }

        return builder;
    }

    public static HybrisHeadersBuilder getValidWithDeal(String channel, String system, String office, String corporate) {
        HybrisHeadersBuilder builder = getBaseBuilder(channel);

        if (StringUtils.isNotEmpty(system)) {
            builder.xApplicationId(system);
        }
        if (StringUtils.isNotEmpty(office)) {
            builder.xOfficeId(office);
        }
        if (StringUtils.isNotEmpty(corporate)) {
            builder.xCorporateId(corporate);
        }

        return builder;
    }

    public static HybrisHeadersBuilder getBuilder() {
        return HybrisHeaders.builder();
    }

    /**
     * @return the MultivaluedMap that is required by the jersey client to set the headers for the request
     */
    public Map<String, Object> get() {
        Map<String, Object> headers = new HashMap<>();

        if (prefer != null) {
            headers.put("Prefer", prefer);
        }
        if (authorization != null) {
            headers.put("Authorization", authorization);
        }
        if (xPosId != null) {
            headers.put("X-POS-Id", xPosId);
        }

        if (acceptLanguage != null) {
            headers.put("Accept-Language", acceptLanguage);
        }
        if (connection != null) {
            headers.put("Connection", connection);
        }
        if (acceptEncoding != null) {
            headers.put("Accept-Encoding", acceptEncoding);
        }
        if (xClientTransactionId != null) {
            headers.put("X-Client-Transaction-id", xClientTransactionId);
        }
        if (acceptCharset != null) {
            headers.put("Accept-Charset", acceptCharset);
        }
        if (accept != null) {
            headers.put("Accept", accept);
        }
        if (xForwardedFor != null) {
            headers.put("X-Forwarded-For", xForwardedFor);
        }
        if (date != null) {
            headers.put("Date", date);
        }
        if (debug != null) {
            headers.put("X-Debug", debug);
        }
        if (xTest != null) {
            headers.put("X-Test", xTest);
        }

        if (xApplicationId != null) {
            headers.put("X-Application-Id", xApplicationId);
        }
        if (xCorporateId != null) {
            headers.put("X-Corporate-Id", xCorporateId);
        }
        if (xOfficeId != null) {
            headers.put("X-Office-Id", xOfficeId);
        }
        if (xCountry != null) {
            headers.put("X-Country", xCountry);
        }

        return headers;
    }
}
