@FCPH-273
Feature: Adding basic passenger details to a basket

  Scenario Outline: Add basic passenger details to a basket for passengers
    Given my basket contains "<passengerMix>"
    When I provide basic passenger details
    Then the basket is updated with the details
  @regression
    Examples:
      | passengerMix     |
      | 1 Adult, 1 Child |
    Examples:
      | passengerMix      |
      | 1 Adult           |
      | 2 Adults, 1 Child |

