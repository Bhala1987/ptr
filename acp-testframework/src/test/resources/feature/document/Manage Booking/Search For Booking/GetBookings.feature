@FCPH-7360 @FCPH-7992
@Sprint26
Feature: Retrieve Full Booking Details - adding flight options, documents

  @Sprint27 @FCPH-9715 @FCPH-7992 @schema
  Scenario Outline: Full E2E
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I provide basic passenger details
    And  I make a request to add an available "<seat>" seat product for each passenger
    And I add product Hold Bag with 1 excess weight to all passengers
    And I add product Large Sporting Equipment to all passenger on all flights
    When I do commit booking for given basket
    Then I do get booking details via <channel>
    And booking has APIS details for each passenger
    And the booking has details of respective <products>
    And the booking has seat details for respective passengers
    And the booking has details of allowed documents
    And I validate the json schema for created booking event
    And I validate the json schema for created customer event
  @regression
    Examples:
      | channel | passenger        | journey          | fareType | seat     | products                                        |
      | Digital | 2 adult; 1 child | outbound/inbound | Standard | STANDARD | Hold Bag,Large Sporting Equipment,Excess Weight |
    Examples:
      | channel   | passenger | journey          | fareType | seat    | products                                        |
      | ADAirport | 1 adult   | outbound/inbound | Standard | UPFRONT | Hold Bag,Large Sporting Equipment,Excess Weight |

  @Sprint27 @FCPH-9715 @FCPH-7992
  Scenario Outline: Create booking with additional seats and verify that booking has details of additional seat
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I provide basic passenger details
    And I do commit booking for given basket
    When I do get booking details via <channel>
    And the booking has details of additional seat
    Examples:
      | channel           | passenger | journey          | fareType |
      | ADCustomerService | 1 adult   | outbound/inbound | Flexi    |

  Scenario Outline: Create and Get booking details with EjPlusNumber and passenger APIS details
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I add valid passenger details with ejPlus type customer
    And I add product Hold Bag with 1 excess weight to all passengers
    And I add product Large Sporting Equipment to all passenger on all flights
    And I do commit booking for given basket
    When I do get booking details via <channel>
    And booking has APIS details for each passenger
    And booking has EjPlusNumber details for each passenger
    Examples:
      | channel   | passenger       | journey          | fareType | userType |
      | ADAirport | 1 adult;1 child | outbound/inbound | Standard | customer |
#      | PublicApiMobile | 1 adult         | outbound/inbound | Standard | customer |

  Scenario Outline: Verify that booking has comment details
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I provide basic passenger details
    And I do commit booking for given basket
    And I login as agent with username as "rachel" and password as "12341234"
    And I have received a valid addComments request for type "PASSENGER" with comment "test"
    And I validate and return the result for addComments request
    When I do get booking details via <channel>
    Then based on channel I <shouldornot> see comments on booking
    Examples:
      | channel   | passenger           | journey          | fareType | shouldornot |
      | ADAirport | 1 adult; 1,1 infant | outbound/inbound | Standard | should      |

  Scenario Outline: Get Booking details for public API B2B with seats,products and APIS details
    Given I am using the channel Digital
    And I have created a new customer
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I provide basic passenger details
    And  I make a request to add an available "<seat>" seat product for each passenger
    And I add product Hold Bag with 1 excess weight to all passengers
    And I add product Large Sporting Equipment to all passenger on all flights
    And I do commit booking for given basket
    When I do get booking details via PublicApiB2B
    And the booking has details of respective <products>
    And booking has APIS details for each passenger
    And the booking has seat details for respective passengers
    And the booking has the cabin bag for each passenger
    And the booking has details of allowed functions
#    This can be enabled once we fix the issues related to schema validation FCPH-10488
#    And I validate the json schema for created booking event
#    And I validate the json schema for created customer event
    Examples:
      | passenger           | journey          | fareType | seat    |
      | 2 adult; 1,1 infant | outbound/inbound | Standard | UPFRONT |

  Scenario Outline: Create booking with additional seats and verify that booking has details of additional seat for Public API
    Given I am using the channel ADAirport
    And I have created a new customer
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I provide basic passenger details
    And I do commit booking for given basket
    When I do get booking details via PublicApiB2B
    And the booking has details of additional seat
    Examples:
      | passenger | journey          | fareType |
      | 1 adult   | outbound/inbound | Standard |

  @TeamA @Sprint29 @FCPH-10125
  Scenario: Test that linked flights are added when a booking is commited with a return flight.
    Given I am using the channel Digital
    And I have created a new customer
    And I searched a 'Standard' flight with return for 1 Adult
    And I added it to the basket with Standard fare as outbound/inbound journey
    And I provide basic passenger details
    When I do commit booking for given basket
    And I do get booking details via Digital
    Then the booking flights should be linked together using the linkedFlights attribute


  @Sprint31 @TeamC @FCPH-10577 @local
  Scenario Outline: return additional information to regenerate the boarding pass with checkin
    Given the channel <channel> is used
    When I commit a booking with <fareType> fare and <passenger> passenger
    And I've checked in all passengers outbound flight
    When I create a request to generate the boarding pass
    And I create amendable basket for the booking created
    And I change the flight to <changeFlightPassenger> passenger in amendable basket
    And I recommit the booking
    Then  I will return an additional information "SVC_100024_1001" to the channel
    And I've checked in again for <changeFlightPassenger> passenger outbound flight
    When I create new boarding pass for updated customer
    And I see the bording Pass status is changed to <boardingPassStatus>
    Examples:
      | channel | passenger | fareType | changeFlightPassenger | boardingPassStatus |
      | Digital | 2 adult   | Standard | first                 | RETRIEVED          |
      | Digital | 2 adult   | Standard | Second                | RETRIEVED          |
