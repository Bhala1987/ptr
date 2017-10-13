package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customerpreferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AncillaryPreferences {
    public List<String> seatingPreferences;
    public String seatNumber;
    public String holdBagQuantity;
    public String holdBagWeight;
}