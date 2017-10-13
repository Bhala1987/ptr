@FCPH-496 @FCPH-3235
Feature: Retrieve Passenger Types and Rules

  @regression
  @FCPH-3496 @FCPH-2752
  Scenario: All passenger types can be returned
    Given there are passenger types
    When I call the get passenger types service
    Then the passenger types are returned

  @FCPH-10686 @TeamA @Sprint30
  Scenario: Associate the infant on seat to its adult in passenger types
    Given I call the get passenger types service
    Then the passenger rules associating infant on seat to it adult is returned

  @pending
  @manual
  Scenario: Passenger types have age ranges
    Given there are passenger types
    When I call the get passenger types service
    Then the minimum and maximum age for each passenger is correct

  @pending
  @manual
  Scenario: Passenger types have maximum number available
    Given there are passenger types
    When I call the get passenger types service
    Then the maximum number is correct for each passenger type