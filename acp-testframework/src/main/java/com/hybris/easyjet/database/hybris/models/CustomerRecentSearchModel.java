package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by ptr-kvijayapal on 1/18/2017.
 */
@Data
public class CustomerRecentSearchModel {

    private java.sql.Timestamp createdDate;
    private String source;
    private String destination;
    private String inBoundDate;
    private String outBoundDate;
    private String numberOfAdults;
    private String numberOfChildren;
    private String numberOfInfants;
    private String customerPK;

    public CustomerRecentSearchModel(java.sql.Timestamp createdDate, String source, String destination, String inBoundDate, String outBoundDate, String numberOfAdults, String numberOfChildren, String numberOfInfants) {

        this.createdDate = createdDate;
        this.source = source;
        this.destination = destination;
        this.inBoundDate = inBoundDate;
        this.outBoundDate = outBoundDate;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.numberOfInfants = numberOfInfants;
    }
}
