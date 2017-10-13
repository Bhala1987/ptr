package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by albertowork on 5/24/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class BookingDocumentsRequestBody implements IRequestBody {

    @JsonProperty("documents")
    private List<BookingDocumentsRequestBody.Documents> documents;

    @JsonProperty("flightkeys")
    private List<String> flightkeys;

    @JsonProperty("deliverToEmailList")
    private List<String> deliverToEmailList;

    @JsonProperty("comment")
    private String comment;

    @Getter
    @Setter
    public static class Documents {

        @JsonProperty("type")
        private String type;

        @JsonProperty("outputMode")
        private String outputMode;
    }
}
