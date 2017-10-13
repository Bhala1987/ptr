@Sprint28 @FCPH-2619
Feature: Request seat allocation for check in where passenger does not have a purchased seat

  @FCPH-2619 @local
  Scenario Outline: Set passenger status to SAG
    Given I am using channel <channel>
    When the channel has initiated a CheckInForFlight for "<passengers>"
    Then check in passenger with custom "<client>" transaction id
    And channel send getbooking request
    Then updated passenger status should return "<status>"

    Examples:
      | channel | passengers | client                               | status |
      | Digital | 1 Adult    | 00000000-0000-0000-0000-000000123456 | SAG    |


  @FCPH-2619 @local
  Scenario Outline:  De-allocate Inventory when Check-In fails downstream (Seat Allocated)
    Given I am using channel <channel>
    When the channel has initiated a CheckInForFlight for "<passengers>"
    Then check in passenger with custom "<client>" transaction id
    Then response returns errorcode "<errorCode>"

    Examples:
      | channel | passengers | client          | errorCode       |
      | Digital | 1 Adult    | 999999999999999 | SVC_100165_0011 |


  @FCPH-2619 @local
  Scenario Outline: No Inventory De-allocation when Check-In fails downstream (SAG)
    Given I am using channel <channel>
    When the channel has initiated a CheckInForFlight for "<passengers>"
    Then check in passenger with custom "<client>" transaction id
    And channel send getbooking request
    Then updated passenger status should return "<status>"

    Examples:
      | channel | passengers | client                                  | status |
      | Digital | 1 Adult    | 00000000-0000-0000-0000-000000123456999 | SAG    |

  @manual
  Scenario: Create booking event for passenger who have checked in
    Given I am using channel <channel>
    When the channel has initiated a CheckInForFlight for passenger
    When I have returned confirmation to the channel
    Then I will create an history entry on the booking with the
    And following details Channel initiated, Date and Time,
    And User ID who initiated the process, Event Type = Passenger checked in,
    And Event Description including Passenger Name and flight key who checked in




