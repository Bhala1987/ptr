package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

import java.util.List;

/**
 * Created by dwebb on 12/2/2016.
 */
@Data
public class CustomerModel {

    private String uid;
    private String customerid;
    private String status;
    private String title;
    private String firstname;
    private String lastname;
    private String email;
    private String postalcode;
    private String country;
    private String town;
    private String ejmembership;
    private String flightclubnumber;
    private String nifNumber;
    private String type;
    private String group;
    private String nif;
    private String phonenumber;
    private String alternativephonenumber;
    private String employeemail;
    private String pwd;
    private List<KeyDateModel> keydates;
    private String employeeid;
    private List<BookingModel> bookings;

    public CustomerModel(String uid, String p_customerid) {

        this.uid = uid;
        this.customerid = p_customerid;
    }

    public CustomerModel(String uid, String customerid, String pwd) {

        this.uid = uid;
        this.customerid = customerid;
        this.pwd = pwd;
    }

    public CustomerModel(String uid, String customerid, String status, String firstname, String lastname, String ejmembership, String flightclubnumber) {
        this.uid = uid;
        this.customerid = customerid;
        this.status = status;
        this.firstname = firstname;
        this.lastname = lastname;
        this.ejmembership = ejmembership;
        this.flightclubnumber = flightclubnumber;
    }

    public CustomerModel(String uid, String customerid, String status, String title, String firstname, String lastname, String ejmembership, String flightclubnumber, String postalcode, String town, String country) {
        this.uid = uid;
        this.customerid = customerid;
        this.status = status;
        this.title = title;
        this.firstname = firstname;
        this.lastname = lastname;
        this.ejmembership = ejmembership;
        this.flightclubnumber = flightclubnumber;
        this.postalcode = postalcode;
        this.town = town;
        this.country = country;
    }

    public CustomerModel(String uid, String customerid, String status, String title, String firstname, String lastname, String ejmembership, String flightclubnumber,
                         String postalcode, String town, String country, String employeeid, String type, String group, String nif, String phonenumber, String alternativephonenumber,
                         String employeemail, String email) {
        this(uid, customerid, status, title, firstname, lastname, ejmembership, flightclubnumber, postalcode, town, country);
        this.employeeid = employeeid;
        this.employeemail = employeemail;
        this.type = type;
        this.nif = nif;
        this.alternativephonenumber = alternativephonenumber;
        this.phonenumber = phonenumber;
        this.group = group;
        this.email = email;
        this.nifNumber = nif;
    }

}



