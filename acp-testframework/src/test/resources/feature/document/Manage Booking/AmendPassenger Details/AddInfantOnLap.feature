@Sprint27
Feature: Add InfantOnLap in an amendable basket

  @FCPH-8727
  Scenario Outline: Add Infant On Lap successfully to an adult
    Given I am using channel <channel>
    And I create a "COMPLETED" status booking for "1 Adult"
    And I request an amendable basket for a booking
    When I attempt to add infantOnLap to validAdult passenger
    Then InfantOnLap gets added successfully
  @regression
    Examples:
      | channel           |
      | Digital           |
    Examples:
      | channel           |
      | ADAirport         |

  @FCPH-8726
  Scenario Outline: Error for an invalid basket
    Given I am using channel <channel>
    When I attempt to add infantOnLap with invalid basket
    Then I should receive an error SVC_100521_2003 while adding
    Examples:
      | channel           |
      | Digital           |
      | ADAirport         |
      | ADCustomerService |
      | PublicApiMobile   |

  @FCPH-8726
  Scenario Outline: Error while adding to a passenger who is not on the booking
    Given I am using channel <channel>
    And I create a "COMPLETED" status booking for "1 Adult"
    When I request an amendable basket for a booking
    When I attempt to add infantOnLap to invalid passenger
    Then I should receive an error SVC_100521_2002 while adding
    Examples:
      | channel           |
      | Digital           |
      | ADAirport         |
      | ADCustomerService |
      | PublicApiMobile   |

  @FCPH-8726
  Scenario Outline: Error while adding to a passenger who is not an adult
    Given I am using channel <Channel>
    And I create a "COMPLETED" status booking for "1 Adult, 1 Child, 1 Infant OL"
    When I request an amendable basket for a booking
    When I attempt to add infantOnLap to <PassengerType> passenger
    Then I should receive an error <ErrorCode> while adding
    Examples:
      | Channel           | PassengerType | ErrorCode       |
      | Digital           | child         | SVC_100521_2006 |
      | ADAirport         | child         | SVC_100521_2006 |
      | ADCustomerService | infant        | SVC_100521_2006 |
      | PublicApiMobile   | infant        | SVC_100521_2006 |

  @FCPH-8726
  Scenario: Error while adding to a passenger who has infant already
    Given I am using channel Digital
    And I create a "COMPLETED" status booking for "2 Adult, 1 Child, 1 Infant OL"
    When I request an amendable basket for a booking
    When I attempt to add infantOnLap to adultWithInfantOnLapAlready passenger
    Then I should receive an error SVC_100521_2005 while adding


  @FCPH-8726
  Scenario Outline: Error while adding a passenger with missing mandatory fields
    Given I am using channel <Channel>
    And I create a "COMPLETED" status booking for "1 Adult"
    When I request an amendable basket for a booking
    When I attempt to add add infantOnLap with no <Field> information
    Then I should receive an error <ErrorCode> while adding
    Examples:
      | Channel           | Field     | ErrorCode       |
      | Digital           | firstName | SVC_100521_2001 |
      | ADAirport         | LastName  | SVC_100521_2001 |
      | ADCustomerService | age       | SVC_100521_2001 |
      | PublicApiMobile   | title     | SVC_100521_2001 |

#      Check in service is not available at the time of writing this test, hence this has to be manual
  @FCPH-8727 @manual
  Scenario: add infant on lap to the checked in passenger
    Given I am using channel Digital
    And I create a "COMPLETED" status booking for "1 Adult"
    And I have done the check in for the same passenger mix
    And I request an amendable basket for a booking
    When I attempt to add infantOnLap to validAdult passenger
    Then InfantOnLap gets added successfully
    And adult passenger status should change to "Booked"
    And ICTS status has not changed
    And API status has not changed

