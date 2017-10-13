@FCPH-3420
Feature: Add Cabin Bags to basket

  Scenario Outline: Add Cabin bags to basket based on the bundle type
    Given I have added a flight with "<bundleType>" bundle to the basket
    Then I will see <numberOfBags> Cabin bags under the <bundleType> bundle in the basket
  @regression
    Examples:
      | bundleType | numberOfBags |
      | Standard   | 1            |
    Examples:
      | bundleType | numberOfBags |
      | Flexi      | 2            |
      | Inclusive  | 1            |

  Scenario Outline: Add Cabin bags to basket based on the bundle type for staff bundle
    Given a valid customer profile has been created
    And a valid request to associate staff member to member account
    Given I have added a flight with "<bundleType>" bundle to the basket
    Then I will see <numberOfBags> Cabin bags under the <bundleType> bundle in the basket
    Examples:
      | bundleType | numberOfBags |
      | Staff      | 1            |


  Scenario Outline: Add Cabin bags for each passenger type
    Given I have added the flight with <bundleType> and passengers as <passengerMix> to the basket
    Then I cabin bags <numberOfBags> auto added based on the passengers
    Examples:
      | bundleType | passengerMix                   | numberOfBags |
      | Standard   | 1 Adult, 1 Child               | 1            |
      | Standard   | 1 Adult, 1 Infants OOS         | 1            |
      | Flexi      | 1 Adult, 1 Child, 1 Infant OOS | 2            |

  Scenario Outline: Maximum allowance of Cabin bags
    Given I have added the flight with <bundleType> bundle to the basket
    And the passenger already has <numberOfBags> of Cabin bags for a sector
    When the EJ plus number is <EJStatus> to the passenger
    Then I <action> another Cabin bag for <bundleType> to the passenger for the sector
    Examples:
      | bundleType | EJStatus  | numberOfBags | action       |
      | Standard   | added     | 1            | will add     |
      | Standard   | not added | 1            | will not add |
      | Flexi      | added     | 2            | will not add |
      | Flexi      | not added | 2            | will not add |
