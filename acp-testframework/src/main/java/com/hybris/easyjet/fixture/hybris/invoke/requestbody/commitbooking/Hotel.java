package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Hotel {
    private String bundleCode;
    private String code;
    private String name;
    private String description;
    private Integer quantity;
    private Pricing pricing;
    private HotelAddress hotelAddress;
    private List<Room> room = null;
    private String phone;
    private String checkInDate;
    private String checkoutDate;
    private String bookingReference;
    private String leadPassenger;
}
