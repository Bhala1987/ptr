@TeamC @Sprint29 @FCPH-9756
Feature: Calculate remaining payment balance and payment method fees

  Scenario Outline: Validation scenarios
    Given I am using <channel> channel
    When I request a calculate payment balance with <invalid-request>
    Then I see the process fail with <error>
    Examples: Error if basket ID is not able to be identified
      | channel | invalid-request   | error           |
      | Digital | INVALID_BASKET_ID | SVC_100013_1001 |
    Examples: Error if Payment Method amount is greater than total balance of the basket
      | channel         | invalid-request        | error           |
      | PublicApiMobile | GREATER_PAYMENT_AMOUNT | SVC_100530_2007 |
    Examples: Error if the Payment Method ID is not able to be recognised
      | channel   | invalid-request    | error           |
      | ADAirport | INVALID_PAYMENT_ID | SVC_100530_2005 |
    Examples: Error if the Payment Method ID is not able to be recognised
      | channel | invalid-request        | error           |
      | Digital | INVALID_PAYMENT_METHOD | SVC_100530_2004 |

  Scenario Outline: Return remaining balance to the channel and any payment method fees
    Given I am using <channel> channel
    When I request a valid calculate payment balance with fee depending to the payment method <payment-method>
    Then I should see the outstanding balance for the basket giving the payment methods and amounts requested
    And I should see the payment method fee based on the payment amounts and payment methods requested
    Examples:
      | channel         | payment-method |
      | ADAirport       | VI             |
      | Digital         | VI;DL          |