Feature: Validation Errors for Add Direct Flight

  @FCPH-165 @FCPH-166 @negative
  Scenario Outline: Validate the request body of addFlight() service.
    Given I have a flight request with <condition> "<field>"
    When I attempt to add the flight to my basket
    Then the "<error>" error should be returned
    Examples:
      | field | error           | condition |
#      | fareType           | SVC_100012_2003  | missing   |
      | flightKey          | SVC_100012_2002  | missing   |
#      | journeyType        | SVC_100012_2004  | missing   |
#      | price | SVC_100012_2005 | missing   |
      | passengers         | SVC_100012_2007  | missing   |
#      | journeyTypeInvalid | SVC_100012_20016 | invalid   |

  @negative
  Scenario: Cannot add multiple infants on lap to single adult
    Given I am using channel Digital
    And a valid flight exists with 1 Adult, 2 Infants OOS seats available
    When I try to add the flight to my basket with the passenger mix 1 Adult, 2 Infants OL
    Then the "SVC_100148_3006" error should be returned

  @FCPH-170
  Scenario: Verify that the hybris ACP sends the notification and provide a flag to the channel for price change.
    Given I have a flight fare that has different price now to what it was when first received
    When I try to add the flight to my basket
    Then the "flight fare" warning should be returned
    And the flight is added to the basket


 #curently set to 9999 as BR requires it to be unlimited
  @manual
  @FCPH-165 @FCPH-166
  Scenario Outline: Validate additional flights added does not exceed maximum - BR_00212
    Given the property maximumNumberOfAdditionalFlightsAdded is configured to 6 for "<channel>"
    And I have added 5 flights to my basket
    When I add the flight to my basket
    Then the "maximum number of flights exceeded" warning should be returned
    Examples:
      | channel      |
      | ADAirport    |
      | Digital      |
      | PublicApiB2B |

  @manual
  @FCPH-165 @FCPH-166
  Scenario Outline: Validate added flight's arrival time is not less than X hrs of any flight departure time from a different airport already in the basket - BR_00080
    Given the property minTimeBetweenArrivalAndDeparture is configured to 24 hours for "<channel>"
    And I have a valid departure flight to an airport added to the basket
    When I add a new flight from a different airport within 24 hours
    Then I should get warning as "There is another flight departing within 24 hours"
    Examples:
      | channel      |
      | ADAirport    |
      | Digital      |
      | PublicApiB2B |

  @manual
  @FCPH-165 @FCPH-166
  Scenario: Validate a return flight request date/time is not prior to the outbound flight in the basket - BR_00081
    Given I have a valid departure flight from an airport added to the basket
    When I add a new inbound flight with departure date/time before the departure date/time of the outbound
    Then I should get warning as "The return flight is prior to departure"

  @manual
  @FCPH-165 @FCPH-166
  Scenario: Cannot add a cancelled flight to the basket - BR_00082
    Given I have a cancelled flight
    When I have added a flight to the basket
    Then I should get "SVC_100012_3001" error

  @pending
  @manual
  @FCPH-165 @FCPH-166
  Scenario Outline: Validate the selected flight is not STD minus x hours on today's date - BR_00090
    Given I have the property minTimeBeforeScheduledTimeDepartureInHours is set to 2 hours for "<channel>"
    And I have a flight departing within the valid booking hours
    When I have added a flight to the basket
    Then I should get "SVC_100012_3005" error
    Examples:
      | channel      |
      | ADAirport    |
      | Digital      |
      | PublicApiB2B |

  @manual
  @FCPH-165 @FCPH-166
  Scenario Outline: Validate the selected return flight is not less than 2 hours of arrival time - BR_OOO71
    Given I have the property minTimeBetweenInboundAndOutbound is set to 2 hours for "<channel>"
    And I have added a flight to the basket
    When I try to add a new flight inbound within 2 hours of arrival
    Then I should get warning as "The return flight is less than 2 hours of arrival"
    Examples:
      | channel           |
      | ADCustomerService |
      | PublicApiMobile   |
      | PublicApiB2B      |

  @FCPH-165 @FCPH-166
  Scenario Outline: Maximum number of x infants own seat per adult on a booking - BR_01800
    Given I am using the channel <channel>
    And I have a valid flight that exceeds the infant on own seat limit
    When I try to add the flight to my basket
    Then the "SVC_100148_3007" error should be returned
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |
      | PublicApiB2B      |

  @FCPH-165 @FCPH-166
  Scenario Outline: We should get the error when number of infants on their own seats when it exceed the flight's limit - BR_00041
    Given I am using the channel <channel>
    And I have a valid flight that exceeds the infant on own seat limit for flight
    When I try to add the flight to my basket
    Then the "SVC_100012_3006" error should be returned
    Examples:
      | channel      |
      | ADAirport    |
      | Digital      |
      | PublicApiB2B |

  @FCPH-165 @FCPH-166
  Scenario Outline: AD can ovveride the number of infants on their own seats when it exceed the flight's limit - BR_00042
    Given I am using the channel <channel>
    And I have a valid flight that exceeds the infant on own seat limit for flight
    When I try to add the flight to my basket with override warning as true
    Then the flight is added to the basket
    Examples:
      | channel   |
      | ADAirport |

  @FCPH-165 @FCPH-166
  Scenario Outline: Selected flight price has increased - Alternate scenario
    Given I am using the channel <channel>
    When I add flight to basket with different price and <passengerMix>
    Then I should get the waring with message as Please review the price of the flight you are adding to the basket as it has been updated
    And the flight is added to the basket
    Examples:
      | channel   | passengerMix    |
      | ADAirport | 1 adult,1 child |
      | Digital   | 1 adult,1 child |

  @pending
  @manual
  @FCPH-170
  Scenario Outline: Validate the response for addFlight() service due to business errors.
    Given I have flights available
    When I request addFlight with no X-Test header
    Then I should receive the error "<Code>" and "<Message>"
    Examples:
      | Code            | Message                                                                     |
      | SVC_100012_3011 | Error as the inventory returned back has no data or inventory not allocated |