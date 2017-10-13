@Sprint27
@FCPH-8421
Feature: Manage Hold items inventory as part of Commit booking for Public API

  Background:
    Given I am using channel PublicApiB2B

  Scenario Outline: Success when we request maximum allowed for passenger type
    And creating booking for <Passengers>
    When I create a commit booking request with <HoldBags> hold bag, <excessweights> excess weight, <sportequipments> sport equipment without seats
    And commit the booking
    Then a booking reference is returned
    Examples:
      | Passengers            | HoldBags | excessweights | sportequipments |
      | 1 Adult               | 1        | 1             | 1               |
      | 1 Adult               | 3        | 0             | 0               |
      | 1 Child               | 3        | 0             | 0               |
      | 1 Adult, 1 Infant OL  | 6        | 0             | 0               |
      | 1 Adult               | 0        | 0             | 1               |
      | 1 Child               | 0        | 0             | 1               |
      | 1 Adult, 1 Infant OL  | 0        | 0             | 1               |
      | 1 Adult, 1 Infant OOS | 0        | 0             | 1               |

  Scenario Outline: Hold Items - Error when exceeding thresholds for passenger type
    When I attempt to commit the booking for <Passengers> and <Quantity> hold bags
    Then the channel will receive an error with code SVC_100288_2005
    Examples:
      | Passengers            | Quantity |
      | 1 Adult               | 4        |
      | 1 Child               | 4        |
      | 1 Adult, 1 Infant OL  | 7        |
      | 1 Adult, 1 Infant OOS | 7        |

  Scenario Outline: Sports equipment - Error when exceeding thresholds for passenger type
    When I attempt to commit the booking for <Passengers> and <Quantity> sports equipment
    Then the channel will receive an error with code SVC_100290_2005
    Examples:
      | Passengers            | Quantity |
      | 1 Adult               | 2        |
      | 1 Child               | 2        |
      | 1 Adult, 1 Infant OL  | 2        |
      | 1 Adult, 1 Infant OOS | 2        |

  Scenario: Price for the product has changed for hold item
    When I attempt to commit the booking for 1 Adult with hold item price change
    Then the commit booking should fail with error SVC_100022_3053 affected data Price

  @manual
  Scenario: Inventory exceeds the capped threshold for flight
    And my flight has only 4 hold bag inventory available
    When I request for 5
    Then error is returned

  @manual
  Scenario Outline: Verify de-allocation to AL
    Given My commit booking fails for reason <reason>
    Then it should make de-allocation calls to AL
    Examples:
      | reason                                            |
      | hold item price changed                           |
      | inventory exceeds the capped threshold for flight |