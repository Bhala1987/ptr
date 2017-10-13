@TeamD
@Sprint31
@FCPH-10058
@schema
@FCPH-10058 @regression
Feature: Send Booking Updated Event - Change flight and Add Passenger

  Scenario: Generate event to downstream systems when a Passenger has been added to one or more a flight
    Given one of this channel ADAirport, Digital is used
    And I created an amendable basket for 1 adult
    And I want to add an adult to a flight
    And I send the addPassenger request
    When I proceed to commit the booking
    Then I validate the json schema for updated booking event

  Scenario: Generate event to downstream systems when a New Flight added
    Given one of this channel ADAirport, Digital is used
    And I created an amendable basket for 1 adult
    And I added a flight to the basket
    When I proceed to commit the booking
    Then I validate the json schema for updated booking event

  Scenario:  Generate event to downstream systems when changing a flight
    Given one of this channel ADAirport, Digital is used
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    And I send the changeFlight request
    And the new flight is added to the basket
    When I proceed to commit the booking
    Then I validate the json schema for updated booking event