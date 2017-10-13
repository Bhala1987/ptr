package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by robertadigiorgio on 11/07/2017.
 */
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangeFlightRequestBody implements IRequestBody {
    private String newFlightKey;
    private String price;
    private List<String> passengers;
}
