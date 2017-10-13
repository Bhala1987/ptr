@FCPH-3459
@Sprint26
Feature:  Save Abandon basket to customer profile digital channel

  Background:
    Given I am using channel Digital
    And I am logged in as a standard customer

  Scenario Outline: Save basket but not personal details when customer logs out BR_00550, BR_00646
    And my basket contains "1 Adult, 1 Child, 1 Infant OL"
    When I provide basic passenger details
    Then the basket is updated with the details
    When I make session to expire through <method>
    Then the basket should store against the profile
    But not the personal details
    Examples:
      | method     |
      | logout     |
    @manual
    Examples:
      | method     |
      | inactivity |

  Scenario: Not store EjPlusMembership upon session expire
    And my basket contains "1 Adult"
    When I provide ejplus details as Porter and 03445610 and age 50
    Then the basket is updated with the details
    When I make session to expire through logout
    Then the basket should store against the profile
    But not the personal details
