@Sprint26
Feature: Generate Boarding Pass for flights only

  @FCPH-3430 @ADTeam
  Scenario Outline: Error if booking reference is invalid
    When the <channel> initiates a generate boarding pass request
    Then I will receive a error message SVC_100173_003
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |
      | PublicApiB2B      |

  @FCPH-3430 @ADTeam
  Scenario Outline: Error if the flight is not on the booking
    Given I have found a valid flight for the boarding pass via the <channel> and <passengerTypes>
    When the requested flight ID not been found in the booking
    Then I will receive a error message SVC_100173_002
    Examples:
      | channel   | passengerTypes |
      | ADAirport | 1 Adult        |

  @local @FCPH-3430 @FCPH-8950
  Scenario Outline: Boarding pass for one or more passenger for Non AD
    Given I am using channel <channel>
    And the channel has initiated a CheckInForFlight for "<passengerTypes>"
    And Passenger status should change to checked-in on the flight
    And updated passenger status should return "<status>"
    When I create a request to generate the boarding pass
    Then I will receive the boarding pass for all requested <passenger>
    And I will receive the URL to the location of the boarding pass
  @regression
    Examples:
      | channel         | passengerTypes | status     |
      | PublicApiMobile | 1 Adult        | CHECKED_IN |
    Examples:
      | channel | passengerTypes    | status     |
      | Digital | 1 Adult, 1 Infant | CHECKED_IN |

  @local
  @FCPH-3430 @FCPH-8950 @ADTeam
  Scenario Outline: Boarding pass for one or more passenger for AD
    Given I am using channel <channel>
    And the channel has initiated a CheckInForFlight for "<passengerTypes>"
    And Passenger status should change to checked-in on the flight
    And I login as agent with username as "rachel" and password as "12341234"
    When I create a request to generate the boarding pass
    Then I will receive the boarding pass for all requested <passenger>
    And I will receive the URL to the location of the boarding pass
    Examples:
      | channel           | passengerTypes    |
      | ADAirport         | 1 Adult, 1 Infant |
      | ADCustomerService | 1 Adult           |
