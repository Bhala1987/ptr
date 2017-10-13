@Sprint27
@FCPH-414
Feature: Manage Hold items inventory as part of Commit booking for Digital

  Background:
    Given I am using channel Digital

  Scenario Outline: Success when we request maximum allowed for passenger type
    And my basket contains "<Passengers>"
    When I add "maximum" "Hold Bag" for all passengers on flight
    And I add "maximum" "Large Sporting Equipment" for all passengers on flight
    And commit the booking with hold items
    Then a booking reference is returned
    When I do get booking details via Digital
    Then the booking has details of respective Hold Bag, Large Sporting Equipment
    Examples:
      | Passengers           |
      | 1 Adult              |
      | 1 Child              |
      | 1 Adult, 1 Child     |
      | 1 adult ; 1,1 infant |
      | 1 adult ; 1,0 infant |

  @manual
  Scenario: Price for the product has changed for hold item
    When I attempt to commit the booking for 1 Adult with hold item price change
    Then the commit booking should fail with error SVC_100022_3053 affected data Price

  @manual
  Scenario Outline: Inventory exceeds the capped threshold for flight
    And my flight has only 2 <HoldItem> inventory available
    When I request for 3
    Then error is returned
    Examples:
      | HoldItem                    |
      | hold bag                    |
      | hold bag with excess weight |
      | sports equipment            |

  @manual
  Scenario Outline: Verify de-allocation to AL
    Given My commit booking fails for reason <reason>
    Then it should make de-allocation calls to AL

    Examples:
      | reason                                            |
      | hold item price changed                           |
      | inventory exceeds the capped threshold for flight |