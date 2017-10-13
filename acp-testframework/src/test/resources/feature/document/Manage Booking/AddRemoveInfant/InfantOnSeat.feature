@Sprint30 @TeamA @FCPH-10695
Feature: Infant on Own seat - booking and basket services

  Scenario: Associate the infant on seat to its adult in getbasket
    Given I am using Digital channel
    And my basket contains flight with passengerMix "2 Adults, 1 Infant OOS"
    And I have updated the passenger information
    Then associate the infant on own seat to its associated adult passenger

  Scenario: Associate the infant on seat to its adult in create amendable basket
    Given I am using Digital channel
    And I am logged in as a standard customer
    And my basket contains flight with passengerMix "2 Adults, 1 Infant OOS"
    And this passenger has an association with infant on seat
    And I have updated the passenger information
    And I do commit booking for given basket
    And I amend the basket
    When channel send getbooking request
    Then booking request has the association of Passenger to Infant to seat

  Scenario: Associate the infant on seat to its adult in commitBooking
    Given I am using Digital channel
    And I am logged in as a standard customer
    And my basket contains flight with passengerMix "2 Adults, 1 Infant OOS"
    And this passenger has an association with infant on seat
    And I have updated the passenger information
    And I do commit booking for given basket
    When channel send getbooking request
    Then booking request has the association of Passenger to Infant to seat
