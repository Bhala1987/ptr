@FCPH-11219
@Sprint32
@TeamE
Feature: Send the VAT invoice and create Booking history

  @manual
  Scenario: 1 - Send the VAT invoice to the requested email address
    Given that the channel has iniated a request to generate VAT invoice for a booking
    When the system has generated the VAT Invoice
    Then It will send the pdf as an attachement to the requested email address

  @manual
  Scenario: 2 - Generate Booking history event for the VAT Invoice
    Given that the channel has iniated a request to generate a VAT invoice
    When the system have generated the VAT Invoice
    Then It will generate a entity a row on the booking history containing Document Type (eg: VAT Invoice) Requesting Channel, Issue Date / Time, Requesting User ID - Agent or Customer, Issued To - email address(es)
