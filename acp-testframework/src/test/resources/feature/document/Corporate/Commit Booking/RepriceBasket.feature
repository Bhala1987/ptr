@FCPH-3361
Feature: Reprice basket for a Corporate Booking

 #    Setup Deal value and Pos Value in the Backoffice
#|New Deal Value        | New POS Value|
#| 10£                   | 10£          |
#| 10%                   | 10£          |
#| 10£                   | 10%          |
#| 10%                   | 10%          |
#| 0                     | 0            |
#| 10                    | 0            |
#| 0                     | 10           |

  @pending
  @manual
  Scenario: Deal is still ACTIVE and Discount & POS tier has not changed
    Given I have a flight with the Active Deal
    When I add the flight to my basket
    And I call the commitBooking service
    Then the deal is added

  @pending
  @manual
  Scenario: Deal is still ACTIVE and Discount tier has changed
    Given I have a flight with the Active Deal
    And Discount Tier has been changed
    When I add the flight to my basket
    And I call the commitBooking service
    Then the deal is updated with new discount

  @pending
  @manual
  Scenario: Deal is still ACTIVE and POS tier has changed
    Given I have a flight with the Active Deal
    And POS Tier has been changed
    When I add the flight to my basket
    And I call the commitBooking service
    Then the deal is updated with new POS tier

  @pending
  @manual
  Scenario: Deal is INACTIVE and Discount tier has changed
    Given I have a flight with the Inactive Deal
    And Discount Tier has been changed
    When I add the flight to my basket
    And I call the commitBooking service
    Then the booking is not updated with new discount

  @pending
  @manual
  Scenario: Deal is INACTIVE and POS tier has changed
    Given I have a flight with the Inactive Deal
    And POS Tier has been changed
    When I add the flight to my basket
    And I call the commitBooking service
    Then the booking is not updated with new POS tier

  @pending
  @manual
  Scenario: Bundle type is removed from the Deal
    Given I have a flight with the Active Deal
    And Bundle has been removed
    When I add the flight to my basket
    And I call the commitBooking service
    Then the booking should not be created