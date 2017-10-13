package com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Builder
@Getter
@Setter
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddFlightRequestBody implements IRequestBody {
    private List<Flight> flights;
    private String toeiCode;
    private Boolean overrideWarning;
    private Double routePrice;
    private String currency;
    private String routeCode;
    private String fareType;
    private String journeyType;
    private List<Passenger> passengers;
    private String bookingType;
    private String bookingReason;
}