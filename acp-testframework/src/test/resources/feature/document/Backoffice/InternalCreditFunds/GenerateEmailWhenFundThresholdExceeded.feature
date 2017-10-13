@backoffice:FCPH-228
@Sprint25
Feature: Generate Warning Email when Fund Threshold Value is Exceeded

  Scenario: 1 - Generate the threshold alert email - value reached
    Given that a credit file has been created
    When the warning value for a credit file fund is reached
    Then I generate the warning alert email once to the email address entered on the credit file fund
    And the email will contain Credit file Name, Cost Centre / Budget, Starting Balance, Current Balance, warning alert Value

  Scenario: 2 -Generate the threshold alert email - value exceeded
    Given that a credit file has been created
    When the warning value for a credit file fund is exceeded
    Then I generate the warning alert email once to the email address entered on the credit file fund
    And the email will contain Credit file Name, Cost Centre / Budget, Starting Balance, Current Balance, warning alert Value

  Scenario: 3 - Warning alert email not generated if start balance updated
    Given that a credit file has been created
    When the start balance has been reset
    Then I will only generate the warning alert email when the warning level is reached again