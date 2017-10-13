@Sprint32 @TeamC @FCPH-10471
Feature: Validate and update passenger details
  I want to be able to update passenger details in the basket
  So that I can see updated passenger information on the basket

  @Sprint32 @TeamC @FCPH-10471
  Scenario Outline: Error when number of Infant exceed the number of adults
    Given I am using <channel> channel
    And my basket contains flight with passengerMix "<passengerMix>"
    When I send a request to update the age for the adult passenger to <newAge> age
    Then the channel will receive an error with code SVC_100148_3013
    Examples:
      | channel   | passengerMix          | newAge |
      | ADAirport | 1 Adult, 1 Infant OOS | 0      |
      | Digital   | 1 Adult, 1 Infant OL  | 1      |

  @Sprint32 @TeamC @FCPH-10471
  Scenario Outline: Move Infant to another Adult on the booking
    Given I am using <channel> channel
    And my basket contains flight with passengerMix "<passengerMix>"
    When I send a request to update the age for the adult passenger to <newAge> age
    Then the infant is assigned to the second adult on their lap
    Examples:
      | channel   | passengerMix          | newAge |
      | ADAirport | 3 Adult, 1 Infant OL  | 0      |
      | Digital   | 2 Adult, 1 Infant OOS | 1      |
