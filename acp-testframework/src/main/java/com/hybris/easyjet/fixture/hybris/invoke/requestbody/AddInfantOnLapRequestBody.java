package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by vijayapalkayyam on 27/06/2017.
 */
@Getter
@Setter
@Builder
public class AddInfantOnLapRequestBody implements IRequestBody {
    @JsonProperty("name")
    private Name name;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    @JsonProperty("ejPlusCardNumber")
    private String ejPlusCardNumber;
    @JsonProperty("nifNumber")
    private String nifNumber;
    @JsonProperty("age")
    private Integer age;
}
