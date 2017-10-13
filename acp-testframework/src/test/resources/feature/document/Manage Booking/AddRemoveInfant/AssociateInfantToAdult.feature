@Sprint31
@FCPH-10489
@TeamA
Feature: Infant on Own seat - Association to an Adult as Change Passenger details

  @FCPH-10489
  Scenario: Associate an infant on seat to the first Adult passenger on the flight
    Given I am using ADCustomerService channel
    And I login as agent
    And my basket contains flights with departure date 5 days ahead with passengerMix "1 Adults, 2 Infant OOS"
    And I have updated the passenger information
    And I select the first passenger to associate an infant on seat to
    Then associate infant on seat to first adult passenger until ratio is exceeded

  @FCPH-10489
  Scenario:  Associate an infant on own seat to the next adult passenger on the flight
    Given I am using ADCustomerService channel
    And I login as agent
    And my basket contains flights with departure date 5 days ahead with passengerMix "2 Adults, 4 Infant OOS"
    And I have updated the passenger information
    And I select the firstPassenger passenger to associate an infant on seat to
    Then associate infant on seat to Second adult passenger until ratio is exceeded

  @local @FCPH-10489
  Scenario Outline: Associate an infant on own seat to the selected adult passenger on the flight
    Given I am using ADAirport channel
    And I login as agent
    And I get customer profile <profile>
    And my basket contains flights with departure date 5 days ahead with passengerMix "<passengerMix>"
    And I select the firstPassenger passenger to associate an infant on seat to
    And Add <noOfInfant> infant on seat to flight and associate to the select passenger
    And I have updated the passenger information
    And associate infant on seat to <Passenger> adult passenger until ratio is exceeded
    And I do commit booking for given basket
    And Passenger status should change to checked-in on the flight for all passenger
    Then updated passenger status should return "<status>"

    Examples:
      | status     | passengerMix         | Passenger | profile     | noOfInfant |
      | CHECKED_IN | 1 Adult, 1 Infant OL | second    | cus00000001 | 2          |
      | CHECKED_IN | 2 Adults             | selected  | cus00000001 | 1          |


  @local @FCPH-10489
  Scenario Outline: Associate an infant on own seat to the selected adult passenger that is checked In on the flight and Change status to Booked
    Given I am using ADAirport channel
    And I login as agent
    And I get customer profile <profile>
    And my basket contains flights with departure date 5 days ahead with passengerMix "<passengerMix>"
    And I have updated the passenger information
    And I select the firstPassenger passenger to associate an infant on seat to
    And I do commit booking for given basket
    And Passenger status should change to checked-in on the flight for all passenger
    And I request an amendable basket for a booking
    And Add <noOfInfant> infant on seat to flight and associate to the select passenger
    And I have updated the passenger information
    And I do commit booking for given basket
    Then updated passenger status should return "<status>"
    Examples:
      | passengerMix         | profile     | noOfInfant | status |
      | 1 Adult, 1 Infant OL | cus00000001 | 2          | BOOKED |

  @FCPH-10489
  Scenario Outline: Return an error to the Channel if passenger has exceed infant ratio/Infant as responsible adult/Passenger doesn't exist
    Given I am using ADAirport channel
    And I login as agent
    And my basket contains flights with departure date 5 days ahead with passengerMix "<passengerMix>"
    And I select the <PassengerType> passenger to associate an infant on seat to
    When Add <noOfInfant> infant on seat to flight and associate to the select passenger
    Then the add passenger to flight service returns <errorCode>
    Examples:
      | passengerMix          | PassengerType  | errorCode       | noOfInfant |
      | 1 Adult, 1 Infant OOS | doesNotExist   | SVC_100524_2011 | 1          |
      | 1 Adult, 1 Infant OOS | infant         | SVC_100524_2012 | 1          |
      | 1 Adult, 2 Infant OOS | firstPassenger | SVC_100524_2009 | 1          |
