@Sprint31
Feature: Generate Booking Documents and email

  @TeamE
  @FCPH-8220
  Scenario Outline: 1 - Generate Payment confirmation for requested booking
    Given I am using channel Digital
    And the system has a valid booking
    When the system has received a valid requestBookingDocuments for a <documentType> with <outputMode>
    Then it will generate the <documentType> requested
    Examples:
      | documentType                  | outputMode        |
      | BOOKING_CONFIRMATION          | PRINT             |

  @TeamE
  @FCPH-8220 @manual
  Scenario Outline: 2 - Information in the Payment confirmation Document for requested booking
    Given that the system have received a valid requestBookingDocuments for a payment confirmation
    When the system identify the booking
    Then it will generate a payment confirmation with the following <bookingInformation>
    Examples:
      | bookingInformation                            |
      | Customer Name                                 |
      | Customer Address                              |
      | Customer NIF (if applicable)                  |
      | Booking Reference                             |
      | Booking Date                                  |
      | Flight Route(s)                               |
      | Flight No(s)                                  |
      | Flight Date(s)                                |
      | Bundle Name i.e                               |
      | Qty of product                                |
      | Price                                         |
      | Currency                                      |
      | Product Name                                  |
      | Pricing Level                                 |
      | Qty                                           |
      | Price Total                                   |
      | Taxes                                         |
      | Fees (Admin / Group)                          |
      | Credit Card Fee                               |
      | Credit Card Fee Amount                        |
      | Flight Total                                  |
      | Other Total                                   |
      | Grand Total                                   |
      | Payment Type                                  |
      | Payment Reference Data e.g. last 4 digits     |
      | Payment Amount                                |
      | Currency                                      |
      | eJ Company Name                               |
      | eJ Company Registered Address                 |
      | Issue Date / Date of generation               |

  @TeamE
  @FCPH-8220 @manual
  Scenario: 3 - Show each product which is associated to the flight
    Given that the system have received a valid requestBookingDocuments for a payment confirmation
    When there are multiple flights on the booking
    Then under each flight it will list out each product associated to the flight
    And it will show the quantity of each product for each flight

  @TeamE
  @FCPH-7352 @regression
  Scenario Outline: 4 - Generate VAT invoice for requested booking
    Given I am using channel Digital
    And the system has a valid booking
    When the system has received a valid requestBookingDocuments for a <documentType> with <outputMode>
    Then it will generate the <documentType> requested
    Examples:
      | documentType                  | outputMode        |
      | VAT_INVOICE                   | PRINT             |

  @TeamE
  @FCPH-7352 @manual
  Scenario: 5 - Add duplicate to the VAT invoice
    Given that the channel has initiated a request to generate VAT invoice for a booking
    When the VAT invoice has already been generated
    Then the system will add duplicate to the top of the VAT invoice
    And the invoice number remains the same

  @TeamE
  @FCPH-7352 @manual
  Scenario Outline: 6 - Generate VAT Invoice PDF
    Given that the channel has initiated a request to generate VAT Invoice for a booking
    When the system generate the VAT Invoice
    Then it will populate the invoice with the <bookingData>
    And it will generate a unique 10 digit sequential number with a prefix of INVH
    And it will calculate the VAT amount for each product for each VAT product and sector in the currency of the booking
    And it will only include products which have been paid for (not those which are zero price)
    And it will show a line item per passenger type
    And it will show the quantity of each passenger type
    Examples:
      | bookingData                                      |
      | Booking reference no                             |
      | Booking Creation date                            |
      | Customer First Name                              |
      | Customer Last Name                               |
      | Customer Address                                 |
      | Address line 1 / 2                               |
      | Town / City                                      |
      | Postcode                                         |
      | Country                                          |
      | Passenger details                                |
      | Passenger type and NIF number = Fiscal Code      |
      | VAT number                                       |
      | Invoice number                                   |
      | Invoice issue date / re-issue date               |
      | Booking Date                                     |
      | Flight date                                      |
      | Sector                                           |
      | Flight Number                                    |
      | Flight Time                                      |
      | Product description                              |
      | Booking Totals                                   |
      | Payment amount and Currency of the booking       |
      | Invoice Tax Point Date                           |
      | VAT Statement                                    |
      | VAT Rate                                         |
      | VAT Number of the departing Airport              |

  @TeamE
  @FCPH-9886
  Scenario Outline: 7 - Generate error if document type is not recognised
    Given I am using channel Digital
    And the system has a valid booking
    When the system has received a valid requestBookingDocuments for a <documentType> with <outputMode>
    Then I will receive a error message ERR-REQUEST-DOCUMENTS-002
    Examples:
      | documentType                    | outputMode        |
      | INVALID_TYPE                    | EMAIL             |

  @TeamE
  @FCPH-9886
  Scenario Outline: 8 - Generate flight details email
    Given I am using channel Digital
    And the system has a valid booking
    When the system has received a valid requestBookingDocuments with email for a <documentType> with <outputMode>
    Then it will generate the flight details email
    Examples:
      | documentType                  | outputMode        |
      | BOOKING_CONFIRMATION          | EMAIL             |

  @TeamE
  @FCPH-9886 @manual
  Scenario Outline: 9 - Information in the flight details email
    Given that the channel has initiated a request to send a flight details email
    When the system receive a valid request
    Then it will generate the flight details email with the following <flightInformation>
    And it will send to the email address(s) provided in the request
    And it will generate the email based on the language of the booking
    And it will order the flights be earliest Departure date first
    Examples:
      | flightInformation                  |
      | Flight key                         |
      | Flight Number                      |
      | Departure Airport                  |
      | Departure Date and Time            |
      | Departure Terminal                 |
      | Arrival Airport                    |
      | Arrival Terminal                   |
      | Arrival Date and Time              |
      | Booking reference                  |

