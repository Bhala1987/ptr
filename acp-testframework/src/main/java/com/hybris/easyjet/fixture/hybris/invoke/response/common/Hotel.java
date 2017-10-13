package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Hotel extends AbstractProductItem {
    private Address hotelAddress;
    private LocalPricing localPricing;
    private List<Room> room = new ArrayList<>();
    private String phone;
    private String checkInDate;
    private String checkoutDate;
    private String bookingReference;
    private String leadPassenger;

    @Getter
    @Setter
    public static class LocalPricing {
        private String totalAmountWithCreditCard;
        private String totalAmountWithDebitCard;
        private String currency;
    }

    @Getter
    @Setter
    public static class Room {
        private String roomType;
        private Integer quantity;
    }

}