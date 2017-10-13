@FCPH-270 @FCPH-271
Feature: Update Basket Pricing for Age validation
# Example pending need more investigation

#  Background:
#    Given I have set values for Adult, Child and Infant

  Scenario Outline: Age validation with same base price
    Given my basket contains "<passengerMix>" added via "<channel>"
#    And all base prices are the same
    When I change the passenger age of "<original>" to "<new>" via "<channel>"
    Then the basket is updated and fees calculated
  @regression
    Examples:
      | passengerMix          | original | new   | channel |
      | 1 Adult, 1 Infant OOS | Infant   | Adult | Digital |
    Examples:
      | passengerMix          | original | new   | channel   |
      | 1 Adult, 1 Child      | Child    | Adult | Digital   |
      | 1 Adult, 1 Infant OOS | Infant   | Child | Digital   |
      | 1 Adult, 1 Child      | Adult    | Child | Digital   |
      | 1 Adult, 1 Child      | Child    | Adult | ADAirport |
      | 1 Adult, 1 Child      | Adult    | Child | ADAirport |
  @pending
    Examples:
      | passengerMix          | original | new   | channel   |
      | 1 Adult, 1 Infant OOS | Infant   | Adult | ADAirport |
      | 1 Adult, 1 Infant OOS | Infant   | Child | ADAirport |

  Scenario Outline: Age validation for changing age to infant age bracket from other when base price is the same
    Given my basket contains "<passengerMix>" added via "<channel>"
#    And all base prices are the same
    When I change the passenger age of "<original>" to "<new>" via "<channel>"
    Then the basket is updated and fees calculated
    And the infant is now on lap of the first adult
    Examples:
      | passengerMix     | original | new    | channel   |
      | 1 Adult, 1 Child | Child    | Infant | ADAirport |
      | 2 Adults         | Adult    | Infant | ADAirport |
      | 1 Adult, 1 Child | Child    | Infant | Digital   |
      | 2 Adults         | Adult    | Infant | Digital   |

  Scenario Outline: Age validation for changing age to infant age bracket from other when base price is different
    Given my basket contains "<passengerMix>"
#    And all base prices are different
    When I change the passenger age of "<original>" to "<new>" via "<channel>"
    Then the basket is updated and fees calculated
    And the infant is now on lap of the first adult
    Examples:
      | passengerMix     | original | new    | channel   |
      | 2 Adults         | Adult    | Infant | Digital   |
      | 1 Adult, 1 Child | Child    | Infant | Digital   |
      | 1 Adult, 1 Child | Child    | Infant | ADAirport |
      | 2 Adults         | Adult    | Infant | ADAirport |


  Scenario Outline: Changing from infant to adult or child recalculates basket where base fare is the same
    Given my basket contains "<passengerMix>"
  #    And all base prices are the same
    When I change the passenger age of "<original>" to "<new>" via "<channel>"
    Then the basket is updated and fees calculated
    And the "<new>" is now in their own seat
    Examples:
      | passengerMix          | original | new   | channel |
      | 1 Adult, 1 Infant OL  | Infant   | Adult | Digital |
      | 2 Adults, 1 Infant OL | Infant   | Child | Digital |
    Examples:
      | passengerMix          | original | new   | channel   |
      | 1 Adult, 1 Infant OL  | Infant   | Adult | ADAirport |
      | 2 Adults, 1 Infant OL | Infant   | Child | ADAirport |


  @FCPH-7841
  Scenario: Converting passenger to infant when first adult already has one infant on lap
    Given my basket contains "2 Adults, 1 Child, 1 Infant OL"
    When I change the passenger age of "Child" to "Infant"
    Then the Child seat is removed
    And the infant is assigned to the second adult onlap

  @pending
  @manual
  Scenario Outline: Changing from infant to adult or child recalculates basket where base fare is different
    Given my basket contains "<passengerMix>"
#    And all base prices are different
    When I change the passenger age of "<original>" to "<new>" via "<channel>"
    Then the basket is updated and fees calculated
    And a price change event is raised
    And the "<new>" is now in their own seat
    Examples:
      | passengerMix          | original | new   | channel   |
      | 1 Adult, 1 Infant OL  | Infant   | Adult | ADAirport |
      | 2 Adults, 1 Infant OL | Infant   | Child | ADAirport |
      | 1 Adult, 1 Infant OL  | Infant   | Adult | Digital   |
      | 2 Adults, 1 Infant OL | Infant   | Child | Digital   |

  @pending
  Scenario: Converting passenger to infant when all adults already have infant on lap
    Given my basket contains "2 Adults, 1 Child, 2 Infants OL"
    When I change the passenger age of "Child" to "Infant"
    Then the basket is marked as invalid

  @pending
  @manual
  Scenario Outline: Age validation for Agent Desktop when base prices differ
    Given my basket contains "<passengerMix>"
#    And all base prices are different
    When I change the passenger age of "<original>" to "<new>" via "<channel>"
    Then the basket is updated and fees calculated
    And a price change event is raised
    Examples:
      | passengerMix          | original | new   | channel   |
      | 1 Adult, 1 Child      | Child    | Adult | ADAirport |
      | 1 Adult, 1 Child      | Adult    | Child | ADAirport |
      | 1 Adult, 1 Infant OOS | Infant   | Adult | ADAirport |
      | 1 Adult, 1 Infant OOS | Infant   | Child | ADAirport |
      | 1 Adult, 1 Child      | Child    | Adult | Digital   |
      | 1 Adult, 1 Child      | Adult    | Child | Digital   |
      | 1 Adult, 1 Infant OOS | Infant   | Adult | Digital   |
      | 1 Adult, 1 Infant OOS | Infant   | Child | Digital   |