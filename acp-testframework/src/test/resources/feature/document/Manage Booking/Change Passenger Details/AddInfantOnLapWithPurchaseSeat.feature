Feature: Process Add 'Infant on Lap' with a purchased seat, not Public API B2B

  @Sprint31 @TeamC @FCPH-9244
  Scenario: Add infant on lap, adult has a purchased seat, validate seat
    Given I am using Digital channel
    When I send a request to add an Infant OL to an adult who has a purchase seat
    Then I receive a successful response

  @Sprint31 @TeamC @FCPH-9244
  Scenario: Add Infant on lap,adult has a purchased seat and seat is suitable and passenger is not checked in
    Given I am using Digital channel
    When I send the request to add Infant OL to an adult who has a purchase seat and passenger status is BOOKED
    Then I receive an updated basket
    And I see an Infant on Lap product in the basket
    And I see an infant passenger in the basket
    And I see the Infant on Lap assigned to the passenger
    And The adult passenger's APIS status remains the same
    And I see basket totals recalculated
    And The Passenger Status is BOOKED


  @Sprint31 @TeamC @FCPH-9244
  Scenario: Add Infant on lap,adult has a purchased seat and seat is suitable and passenger is checked in
    Given I am using ADAirport channel
    When I send the request to add Infant OL to an adult who has a purchase seat and passenger status is CHECKED_IN
    Then I receive an updated basket
    And I see an Infant on Lap product in the basket
    And I see an infant passenger in the basket
    And I see the Infant on Lap assigned to the passenger
    And The adult passenger's APIS status remains the same
    And I see basket totals recalculated
    And The Passenger Status is BOOKED


  @Sprint31 @TeamC @FCPH-9244
  Scenario: Add Infant on lap, adult has a purchased seat, seat is no longer suitable
    Given I am using ADAirport channel
    When I send the request to add Infant OL to an adult who has an invalid purchase seat
    Then the channel will receive an error with code SVC_100600_1012

  @Sprint32 @TeamC @FCPH-10589
  Scenario Outline:  Add Infant on Lap on the update booking
    Given I am using <channel> channel
    And I have amendable basket for <fareType> fare and <passenger> passenger
    And I added an infant on lap in the amendable basket
    When I commit booking
    Then I should be able to see booking is successful
    And the booking should have an infant on lap
    Examples:
      | channel   | passenger | fareType |
      | ADAirport | 1 adult   | Flexi    |