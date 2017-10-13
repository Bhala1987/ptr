@Sprint28
@FCPH-10121
Feature: Change Flight in a Basket

  Scenario Outline: Validation fields in change flight request
    Given I am using channel <channel>
    And I have 2 flights Standard fare on single journey in the basket with 1 adult
    When I request an invalid change flight with <fields> with <value>
    Then I expect error <error>
    Examples: miss field
      | channel      | fields         | value   | error           |
      | ADAirport    | new-flight-key | missing | SVC_100238_2001 |
      | PublicApiB2B | new-base-price | missing | SVC_100238_2006 |
    Examples: wrong parameter
      | channel         | fields    | value | error           |
      | PublicApiMobile | basket-id | wrong | SVC_100013_1001 |
    Examples: validation old and new flight key
      | channel           | fields         | value           | error           |
      | ADCustomerService | old-flight-key | not-present     | SVC_100238_2004 |
      | Digital           | new-flight-key | already-present | SVC_100238_2005 |

  Scenario Outline: Add new flight to basket, flight inventory available, flight being changed evaluate admin fee apportioned
    Given I am using channel <channel>
    And I have <number-flight> flights Standard fare on <journey-type> journey in the basket with 1 adult
    When I request a valid change flight request
    And I want to see new flight in the basket with new offer price using the current base price
    And I want to see the fees and taxes that are applicable to the new flight
    And I <should> apportion any admin fee apportioned to the flight being changed
    And I want to see any associated products for each passenger
    And I do not want to see the old flight in the basket
  @regression
    Examples:
      | channel | number-flight | journey-type | should |
      | Digital | 2             | single       | true   |
    Examples:
      | channel   | number-flight | journey-type | should |
      | ADAirport | 1             | return       | false  |
