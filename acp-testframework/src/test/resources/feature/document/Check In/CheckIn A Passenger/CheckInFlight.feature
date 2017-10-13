@Sprint28
Feature: Receive request to check in for passenger

  @local
  @FCPH-2615 @FCPH-2654
  Scenario Outline:  Passenger status CHECKED IN
    Given I am using channel <channel>
    When the channel has initiated a CheckInForFlight for "<passengers>"
    Then Passenger status should change to checked-in on the flight for each passenger
    And channel send getbooking request
    Then updated passenger status should return "<status>"
  @regression
    Examples:
      | channel | passengers        | status     |
      | Digital | 1 Adult, 1 Infant | CHECKED_IN |
    Examples:
      | channel | passengers        | status     |
      | Digital | 1 Adult           | CHECKED_IN |
      | Digital | 2 Adult, 1 Infant | CHECKED_IN |

  @local
  @FCPH-2615 @FCPH-2654
  Scenario Outline: Agent Checked In At Airport
    Given I am using channel <channel>
    And I login as agent with username as "<usr>" and password as "<pwd>"
    When the channel has initiated a CheckInForFlight for "<passengers>"
    Then Passenger status should change to checked-in on the flight
    And channel send getbooking request
    Then updated passenger status should return "<status>"
    Examples:
      | usr    | pwd      | passengers | channel           | status     |
      | rachel | 12341234 | 1 Adult    | ADCustomerService | CHECKED_IN |


  @FCPH-2615 @FCPH-2654 @local
  Scenario Outline: CheckIn through PublicApiB2B
    Given I am using channel <channel>
    When do commit booking with <criteria> via <channel>
    And update APIs information before checkin
    Then do checkIn for booking
    Then updated passenger status should return "<status>"

    Examples:
      | criteria            | channel      | status     |
      | multiple passengers | PublicApiB2B | CHECKED_IN |

  @local
  @FCPH-2615 @FCPH-2654
  Scenario Outline: Checkin Without APIs supplied
    Given I am using channel Digital
    When intiated a check for "<passenger>" with APIs not provided
    Then response returns errorcode "<errorCode>"
    Examples:
      | passenger | errorCode       |
      | 1 Adult   | SVC_100165_0002 |

  @local
  @FCPH-2615 @FCPH-2654
  Scenario Outline: Invalid booking Reference
    Given I am using channel <channel>
    When the channel has initiated a CheckInForFlight for 1 Adult on single flight with an Invalid booking Reference
    Then response returns errorcode "<errorCode>"
    Examples:
      | channel | errorCode       |
      | Digital | SVC_100165_0009 |

  @local
  @FCPH-2615 @FCPH-2654
  Scenario Outline: Dangerous Goods set to false
    Given I am using channel <channel>
    When the channel has initiated a CheckInForFlight for "<passengers>"
    And the isDangerousGoodsAccepted is set to false
    Then response returns errorcode "<errorCode>"
    Examples:
      | channel | errorCode       | passengers |
      | Digital | SVC_100165_0001 | 2 Adults   |

  @manual
  Scenario: Create booking event for passenger who have checked in
    Given that the channel has initated a CheckInForFlight
    When I have returned confirmation to the channel
    Then I will create an history entry on the booking with the following details Channel initiated, Date
    And Time, User ID who initated the process, Event Type = Passenger checked in,
    And Event Description = including Passenger Name and flight Key who checked in

  @manual
  Scenario Outline:  Generate error message if the channel isnot allowed to check in a passenger with a standby bundle BR_01878
    Given that the channel has initated a CheckInForFlight
    And the requesting passenger has a bundle type of standby
    When the requesting "<channel>" is not "<allowed>" to checkin the passenger
    Then I will generate a error message to the channel
    Examples:
      | channel           | allowed |
      | ADAirport         | Y       |
      | ADCustomerService | N       |
      | Digital           | N       |


  @Sprint29 @FCPH-9508 @local @TeamA
  Scenario Outline: Allows check in when a booking is locked for amendment
    Given I am using channel <channel>
    When the channel has initiated a CheckInForFlight for "<passengers>"
    And I request an amendable basket for a booking
    Then Passenger status should change to checked-in on the flight
    And channel send getbooking request
    Then updated passenger status should return "<status>"
    Examples:
      | channel | passengers | status     |
      | Digital | 1 Adult    | CHECKED_IN |

  @Sprint29 @FCPH-9508 @local @TeamA
  Scenario Outline: Agent Checked In At Airport when a booking is locked for amendment
    Given I am using channel <channel>
    And I login as agent with username as "<usr>" and password as "<pwd>"
    When the channel has initiated a CheckInForFlight for "<passengers>"
    And I request an amendable basket for a booking
    Then Passenger status should change to checked-in on the flight
    And channel send getbooking request
    Then updated passenger status should return "<status>"
    Examples:
      | usr    | pwd      | passengers | channel           | status     |
      | rachel | 12341234 | 1 Adult    | ADCustomerService | CHECKED_IN |

  @Sprint29 @FCPH-9508 @local @TeamA
  Scenario Outline: Allows check in when a Passenger is locked for amendment
    Given I am using channel Digital
    When the channel has initiated a CheckInForFlight for "<passengers>"
    And create an amendable basket for passenger
    Then Passenger status should change to checked-in on the flight
    And channel send getbooking request
    Then updated passenger status should return "<status>"
    Examples:
      | status     | passengers |
      | CHECKED_IN | 2 Adults   |

  @Sprint30 @FCPH-10473 @TeamC
  Scenario Outline: Error if adult and infant don't check in together
    Given I am using channel <channel>
    When I send a check in request for 1 adult, 1 infant OL passenger without specifying the infant
    Then response returns errorcode "<errorCode>"
    Examples:
      | channel | errorCode       |
      | Digital | SVC_100165_0013 |

  @Sprint30 @FCPH-10473 @TeamC
  Scenario Outline: Error if adult and infant APIS has not been submitted
    Given I am using channel <channel>
    When I send a check in request for 1 adult, 1 infant OL passenger for APIS route without submit identity document for both passengers
    Then response returns errorcode "<errorCode>"
    Examples:
      | channel | errorCode       |
      | Digital | SVC_100165_0012 |
