package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dwebb on 11/9/2016.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class FindBookingQueryParams extends QueryParameters {

    private String referenceNumber;
    private String bookingType;
    private String bookingStatus;
    private String bookingFromDate;
    private String bookingToDate;
    private String travelFromDate;
    private String travelToDate;
    private String searchInBooker;
    private String searchInPax;
    private String title;
    private String firstName;
    private String lastName;
    private String email;
    private String postcode;
    private String dob;
    private String contactNumber;
    private String travelDocumentType;
    private String travelDocumentNumber;
    private String ipAddress;
    private String cardNumber;
    private String paymentAmount;
    private String currencyIsoCode;
    private String transactionDate;
    private String channel;
    private String ejPlusNumber;
    private String flightClubNumber;
    private String employeeNumber;
    private String flightNumber;
    private String seatNumber;
    private String sequenceNumber;
    private String ssrCode;
    private String sortField;
    //TODO this field is not in the service contract
    private String apisToFutureFlight;
    private String destinationAirport;

    public Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(title)) {
            queryParams.put("title", title);
        }
        if (isPopulated(firstName)) {
            queryParams.put("first-name", firstName);
        }
        if (isPopulated(lastName)) {
            queryParams.put("last-name", lastName);
        }
        if (isPopulated(email)) {
            queryParams.put("email", email);
        }
        if (isPopulated(postcode)) {
            queryParams.put("postcode", postcode);
        }
        if (isPopulated(contactNumber)) {
            queryParams.put("contact-number", contactNumber);
        }
        if (isPopulated(dob)) {
            queryParams.put("dob", dob);
        }
        if (isPopulated(travelDocumentType)) {
            queryParams.put("travel-document-type", travelDocumentType);
        }
        if (isPopulated(travelDocumentNumber)) {
            queryParams.put("travel-document-number", travelDocumentNumber);
        }
        if (isPopulated(travelFromDate)) {
            queryParams.put("travel-from-date", travelFromDate);
        }
        if (isPopulated(travelToDate)) {
            queryParams.put("travel-to-date", travelToDate);
        }
        if (isPopulated(ipAddress)) {
            queryParams.put("ip-address", ipAddress);
        }
        if (isPopulated(ejPlusNumber)) {
            queryParams.put("ejplus-number", ejPlusNumber);
        }
        if (isPopulated(sequenceNumber)) {
            queryParams.put("sequence-number", sequenceNumber);
        }
        if (isPopulated(flightNumber)) {
            queryParams.put("flight-number", flightNumber);
        }
        if (isPopulated(bookingType)) {
            queryParams.put("booking-type", bookingType);
        }
        if (isPopulated(bookingStatus)) {
            queryParams.put("booking-status", bookingStatus);
        }
        if (isPopulated(channel)) {
            queryParams.put("channel", channel);
        }
        if (isPopulated(bookingFromDate)) {
            queryParams.put("booking-from-date", bookingFromDate);
        }
        if (isPopulated(bookingToDate)) {
            queryParams.put("booking-to-date", bookingToDate);
        }
        if (isPopulated(cardNumber)) {
            queryParams.put("card-number", cardNumber);
        }
        if (isPopulated(paymentAmount)) {
            queryParams.put("payment-amount", paymentAmount);
        }
        if (isPopulated(currencyIsoCode)) {
            queryParams.put("currency-iso-code", currencyIsoCode);
        }
        if (isPopulated(transactionDate)) {
            queryParams.put("transaction-date", transactionDate);
        }
        if (isPopulated(destinationAirport)) {
            queryParams.put("destination-airport", destinationAirport);
        }
        if (isPopulated(ssrCode)) {
            queryParams.put("ssr-code", ssrCode);
        }
        if (isPopulated(searchInBooker)) {
            queryParams.put("search-in-booker", String.valueOf(searchInBooker));
        }
        if (isPopulated(searchInPax)) {
            queryParams.put("search-in-pax", String.valueOf(searchInPax));
        }
        if (isPopulated(sortField)) {
            queryParams.put("sort-field", sortField);
        }
        if (isPopulated(referenceNumber)) {
            queryParams.put("reference-number", referenceNumber);
        }
        if (isPopulated(flightClubNumber)) {
            queryParams.put("flightclub-number", flightClubNumber);
        }
        if (isPopulated(employeeNumber)) {
            queryParams.put("employee-number", employeeNumber);
        }
        if (isPopulated(seatNumber)) {
            queryParams.put("seat-number", seatNumber);
        }
        if (isPopulated(apisToFutureFlight)) {
            queryParams.put("apply-to-future-flights", apisToFutureFlight);
        }
        return queryParams;
    }

    public void setParameter(String parameter, String value) {
        switch (parameter) {
            case "reference-number":
                this.referenceNumber = value;
                break;
            case "booking-type":
                this.bookingType = value;
                break;
            case "booking-status":
                this.bookingStatus = value;
                break;
            case "booking-from-date":
                this.bookingFromDate = value;
                break;
            case "booking-to-date":
                this.bookingToDate = value;
                break;
            case "travel-from-date":
                this.travelFromDate = value;
                break;
            case "travel-to-date":
                this.travelToDate = value;
                break;
            case "search-in-booker":
                this.searchInBooker = value;
                break;
            case "search-in-pax":
                this.searchInPax = value;
                break;
            case "title":
                this.title = value;
                break;
            case "first-name":
                this.firstName = value;
                break;
            case "last-name":
                this.lastName = value;
                break;
            case "email":
                this.email = value;
                break;
            case "postcode":
                this.postcode = value;
                break;
            case "dob":
                this.dob = value;
                break;
            case "contact-number":
                this.contactNumber = value;
                break;
            case "travel-document-type":
                this.travelDocumentType = value;
                break;
            case "travel-document-number":
                this.travelDocumentNumber = value;
                break;
            case "ip-address":
                this.ipAddress = value;
                break;
            case "card-number":
                this.cardNumber = value;
                break;
            case "payment-amount":
                this.paymentAmount = value;
                break;
            case "currency-iso-code":
                this.currencyIsoCode = value;
                break;
            case "transaction-date":
                this.transactionDate = value;
                break;
            case "channel":
                this.channel = value;
                break;
            case "ejplus-number":
                this.ejPlusNumber = value;
                break;
            case "flightclub-number":
                this.flightClubNumber = value;
                break;
            case "employee-number":
                this.employeeNumber = value;
                break;
            case "flight-number":
                this.flightNumber = value;
                break;
            case "seat-number":
                this.seatNumber = value;
                break;
            case "sequence-number":
                this.sequenceNumber = value;
                break;
            case "ssr-code":
                this.ssrCode = value;
                break;
            case "sort-field":
                this.sortField = value;
                break;
            case "apply-to-future-flights":
                this.apisToFutureFlight = value;
                break;
            case "destination-airport":
                this.destinationAirport = value;
                break;
            default:
                throw new IllegalArgumentException("The parameter you have provided is not a valid query parameter for findBooking.  You provided: " + parameter);
        }
    }
}
