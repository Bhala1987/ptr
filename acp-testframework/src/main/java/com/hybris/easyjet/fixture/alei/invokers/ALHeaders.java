package com.hybris.easyjet.fixture.alei.invokers;

import com.hybris.easyjet.fixture.IHeaders;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents a set of headers that can be sent to a AL service.
 * It offers builder pattern construction as well as standard getter/setter instantiation
 * The method get() returns a multivaluedMap which can be passed directly into the jersey client, for which it is designed to work with
 */
@Data
@Builder
public final class ALHeaders implements IHeaders {

    private String contentType;
    private String xClientId;
    private String accept;
    private String xClientTransactionId;
    private String xAcceptCharSet;
    private String xApplicationId;

    private static ALHeadersBuilder getBaseBuilder(String xClientTransactionId) {
        return ALHeaders.builder()
                .contentType("application/json")
                .xClientId(UUID.randomUUID().toString())
                .xClientTransactionId(xClientTransactionId)
                .accept("application/json");
    }

    public static ALHeadersBuilder getValid() {
        return getBaseBuilder(UUID.randomUUID().toString());
    }

    public static ALHeadersBuilder getValid(String xClientTransactionId) {
        return getBaseBuilder(xClientTransactionId);
    }

    /**
     * @return the MultivaluedMap that is required by the jersey client to set the headers for the request
     */
    public Map<String, Object> get() {
        Map<String, Object> headers = new HashMap<>();

        if (contentType != null) {
            headers.put("Content-Type", contentType);
        }

        if (xClientId != null) {
            headers.put("X-Client-Id", xClientId);
        }

        if (accept != null) {
            headers.put("Accept", accept);
        }

        if (xClientTransactionId != null) {
            headers.put("X-Client-Transaction-Id", xClientTransactionId);
        }
        if (xAcceptCharSet != null) {
            headers.put("Accept-Charset", xAcceptCharSet);
        }
        if (xApplicationId != null) {
            headers.put("X-Application-Id", xApplicationId);
        }


        return headers;
    }
}
