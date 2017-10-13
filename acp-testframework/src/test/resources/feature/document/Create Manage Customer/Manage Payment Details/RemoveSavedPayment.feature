Feature: Receive request to remove saved payment method

  @Sprint28 @FCPH-9161
  Scenario Outline:  Generate Error message if invalid customer ID or payment reference or  channel or login session
    Given I am using Digital channel
    When I submit add payment details CREDIT_CARD valid request as default true
    And I remove saved payment with invalid <invalidType>
    Then I see remove payment error code <errorCode>
    Examples:
      | invalidType | errorCode       |
      | customerID  | SVC_100038_301  |
      | paymentRef  | SVC_100038_304  |
      | channel     | SVC_100123_1001 |
      | session     | SVC_100038_303  |

  @Sprint28 @FCPH-9161 @manual
  Scenario: Remove the payment methods from the profile and audit record
    Given I am using Digital channel
    When I submit add payment details CREDIT_CARD valid request as default true
    And I remove saved payment with invalid customerID
    And I will create a audit record of the saved payment method being remove on the customer profile
    And I will return confirmation to the channel

  @manual @TeamC @Sprint29 @FCPH-9974
  Scenario: Error if payment service is unable to remove
    When I send a remove payment details request
    But the payment service is not able to recognize the ID specified
    Then I should receive an error SVC_100038_304

  @manual @TeamC @Sprint29 @FCPH-9974
  Scenario: Remove the payment methods from the profile if expired or not been used
    When a saved payment method has expired or not been used
    Then the PaymentInfo entity against the customer profile should be removed

  @TeamC @Sprint29 @FCPH-9974 @ADTeam
  Scenario Outline: Successful removal
    Given I am using <channel> channel
    When I send a success remove payment details request
    Then the payment method should be removed against the customer profile
    Examples:
      | channel         |
      | PublicApiMobile |
