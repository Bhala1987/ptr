@FCPH-3680
Feature: Update Basket for Add Direct Flight - Deal Discount applied

 #    Setup Deal value and Pos Value in the Backoffice
#| Deal Value        | POS Value    |
#| 10£               | 10£          |
#| 10%               | 10£          |
#| 10£               | 10%          |
#| 10%               | 10%          |
#| 0                 | 0            |
#| 10                | 0            |
#| 0                 | 10           |

  @pending
  @manual
  Scenario: Deal found has at least one bundle with a discount tier associated inclusive POS Fees.
    Given I have a flight with the Active Deal
    When I add the flight to my basket
    Then I should see basketType as "Business"
    And I should see Discounts in the Response on passenger level
    And I should see POS Fees in the Response on passenger level
    And I should see Total Amount in the Response

  @pending
  @manual
  Scenario: No Deal Found
    Given I have a flight with the No Active Deal
    When I add the flight to my basket
    Then I should see basketType as "Standard"
    And I should not see Discounts in the Response on passenger level
    And I should not see POS Fees in the Response on passenger level
