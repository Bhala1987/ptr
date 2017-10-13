Feature: Correct Basic Search Booking Request

  @local @TeamD @Sprint31 @Sprint32
  Scenario: 1 - CommitBooking
    Given I am using the channel Digital
    When I do the commit booking with eJplus and SSR with Standard and 1 adult
    Then The information are stored

  @TeamD @Sprint31 @Sprint32 @FCPH-11089
  Scenario Outline: Generate error message if the min number of combined fields not provided - basic search
    Given I am using the channel <channel>
    When  I search one booking with <parameter> for <user>
    Then I will receive an error with code '<error>'
    Examples:
      | channel   | parameter      | error           | user               |
      | ADAirport | passengerTitle | SVC_100144_2015 | passenger/customer |

  @TeamD @Sprint31 @Sprint32 @FCPH-11089
  Scenario Outline: Retrieve bookings based on basic criteria supplied
    Given I am using the channel <channel>
    When I search one booking with <parameter> for <user>
    Then I return the summary bookings that match the criteria entered
    Examples:
      | channel   | parameter                                | user               |
      | ADAirport | passengerContactNumber,passengerLastName | passenger/customer |
      | ADAirport | travelDocumentType,travelDocumentNumber  | passenger          |
      | ADAirport | travelToDate,customerLastName            | passenger/customer |
      | ADAirport | postcode,customerLastName                | customer           |
    Examples: First name 1 character or Last name 1 character
      | channel   | parameter                       | user               |
      | ADAirport | passengerTitle,name             | passenger/customer |
      | ADAirport | dob,firstName,passengerLastName | passenger          |
    Examples: standalone criteria only
      | channel   | parameter     | user               |
      | ADAirport | customerEmail | passenger/customer |
    Examples: standalone criteria with a non standalone one
      | channel   | parameter                        | user               |
      | ADAirport | travelFromDate,passengerLastName | passenger/customer |
    Examples: criteria relevant to Passenger
      | channel   | parameter                            | user      |
      | ADAirport | passengerFirstName,passengerLastName | passenger |
    Examples: criteria relevant to Customer
      | channel   | parameter                          | user     |
      | ADAirport | customerFirstName,customerLastName | customer |
    Examples: criteria relevant to Both
      | channel   | parameter                            | user               |
      | ADAirport | passengerFirstName,passengerLastName | passenger/customer |

  @TeamD @Sprint31 @Sprint32 @FCPH-367
  Scenario Outline: Generate error message if the min number of combined fields not provided - advance search
    Given I am using the channel <channel>
    When  I search one booking with <parameter> for <user>
    Then I will receive an error with code '<error>'
    Examples:
      | channel   | parameter          | error           | user               |
      | ADAirport | flightNumber       | SVC_100144_2029 | passenger/customer |
      | ADAirport | ejPlusNumber wrong | SVC_100144_2041 | passenger          |
      | ADAirport | ejPlusNumber       | SVC_100144_2025 | customer           |
    Examples: Booking To Date is before the Booking From Date
      | channel   | parameter                       | error           | user               |
      | ADAirport | bookingToDate < bookingFromDate | SVC_100144_2042 | passenger/customer |

  @TeamD @Sprint31 @Sprint32 @FCPH-367
  Scenario Outline: Retrieve bookings based on advance criteria supplied
    Given I am using the channel <channel>
    When  I search one booking with <parameter> for <user>
    Then  I return the summary bookings that match the criteria entered
    Examples: Default the Booking To date to the Booking From date when it not provided
      | channel   | parameter                              | user               |
      | ADAirport | bookingFromDate,bookingToDate is blank | passenger/customer |
    Examples: EJ Plus number and Ip Address
      | channel   | parameter    | user               |
      | ADAirport | ejPlusNumber | passenger          |
      | ADAirport | ipAddress    | passenger/customer |
    Examples: flight number
      | channel   | parameter                   | user               |
      | ADAirport | flightNumber,travelFromDate | passenger/customer |
    Examples: sequence number
      | channel   | parameter                                  | user               |
      | ADAirport | sequenceNumber,flightNumber,travelFromDate | passenger/customer |
    Examples: transaction date
      | channel   | parameter                                 | user               |
      | ADAirport | transactionDate,channel,passengerLastName | passenger/customer |
    Examples: Payment Amount
      | channel   | parameter                                             | user               |
      | ADAirport | paymentAmount,transactionDate,channel,currencyIsoCode | passenger/customer |
    Examples: SSR
      | channel   | parameter                                | user      |
      | ADAirport | ssrCode,travelFromDate,passengerLastName | passenger |
    Examples: Booking Type, Booking Status
      | channel   | parameter                                     | user               |
      | ADAirport | bookingType,travelFromDate,passengerLastName  | passenger/customer |
      | ADAirport | bookingStatus,travelFromDate,customerLastName | passenger/customer |
    Examples: Channel
      | channel   | parameter                                             | user               |
      | ADAirport | channel,customerLastName,bookingDate                  | customer           |
      | ADAirport | channel,passengerLastName,bookingDate                 | passenger          |
      | ADAirport | channel,customerLastName,bookingDate                  | passenger/customer |
      | ADAirport | channel,customerLastName,travelFromDate,travelToDate  | customer           |
      | ADAirport | channel,passengerLastName,travelFromDate,travelToDate | passenger          |
      | ADAirport | channel,passengerLastName,travelFromDate,travelToDate | passenger/customer |

  # the credit card is encrypted into database, we cannot check the Find Booking response
  @manual @TeamD @Sprint31 @Sprint32 @FCPH-367
  Scenario Outline: Retrieve bookings based on advance criteria supplied - card number
    Given I am using the channel <channel>
    When  I search one booking with <parameter> for <user>
    Then  I return the summary bookings that match the criteria entered
    Examples:
      | channel   | parameter  | user               |
      | ADAirport | cardNumber | passenger/customer |




