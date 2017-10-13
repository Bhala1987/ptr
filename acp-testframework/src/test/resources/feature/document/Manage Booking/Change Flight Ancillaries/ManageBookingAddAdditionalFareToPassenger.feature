Feature: Manage Booking - Add Additional Fare to Passenger

  @TeamC
  @Sprint30
  @FCPH-9918
  Scenario Outline: Error when the passenger id or flight key is incorrect
    Given I am using channel <channel>
    And I have made a booking with passenger <passengerMix> and fare <fareType> without seat
    When I add 1 additional fare for each passenger in the amendable basket with invalid <parameter>
    Then I should receive an error with error code <errorCode>
    Examples:
      | channel           | passengerMix     | fareType | parameter   | errorCode       |
      | ADAirport         | 1 adult          | Flexi    | basketId    | SVC_100389_2001 |
      | ADCustomerService | 1 adult, 1 child | Standard | passengerId | SVC_100389_2002 |

  @TeamC
  @Sprint30
  @FCPH-9918
  Scenario Outline: Passenger has no purchased seat
    Given I am using channel <channel>
    And I have made a booking with passenger <passengerMix> and fare <fareType> without seat
    When I add 1 additional fare for each passenger in the amendable basket
    Then I should receive a successful operation confirmation response with the basket id
    And the passenger should have additional seat in the basket
    Examples:
      | channel   | passengerMix     | fareType |
      | ADAirport | 1 adult, 1 child | Standard |

  @TeamC
  @Sprint30
  @FCPH-9918
  Scenario Outline: Passenger has a purchased seat
    Given I am using channel <channel>
    And I have made a booking with passenger <passengerMix>, fare <fareType> and purchased seat
    When I add 1 additional fare for each passenger in the amendable basket
    Then I should receive a successful operation confirmation response with the basket id
    And I should receive a warning with warning code SVC_100389_2013
    And the passenger should have additional seat in the basket
    Examples:
      | channel           | passengerMix     | fareType |
      | ADCustomerService | 1 adult, 1 child | Flexi    |
