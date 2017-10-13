package com.hybris.easyjet.database.hybris.models;

import lombok.Getter;

@Getter
public class BookingPermissionModel {
    private final String category;
    private final String capability;
    private final String channel;
    private final String accessType;
    private final String bookingType;
    private final boolean isPermitted;

    public BookingPermissionModel(String category, String capability,
                                  String channel, String accessType, String bookingType, boolean isPermitted) {

        this.category = category;
        this.capability = capability;
        this.channel = channel;
        this.accessType = accessType;
        this.bookingType = bookingType;
        this.isPermitted = isPermitted;
    }

}
