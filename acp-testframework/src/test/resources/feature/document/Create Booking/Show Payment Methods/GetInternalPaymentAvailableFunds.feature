@Sprint24
@FCPH-394
Feature: Hybris returning list of available internal credit files

  Scenario: Invalid get internal payment available funds request
    Given I am logged in via channel "ADAirport"
    When the "ADAirport" send getInternalPaymentAvailableFunds request using an invalid parameter
    Then I will return a error message "SVC_100012_2087"

  @regression
  Scenario Outline: List of active credit files based on filters
    Given I am logged in via channel "<channel>"
    When the "<channel>" send getInternalPaymentAvailableFunds request using fund type "<fund-type>"
    Then I will return a list of active credit files
    Examples:
      | channel      | fund-type   |
      | ADAirport    | credit-file |
      | PublicApiB2B | credit-file |

  Scenario: Error returned when unknown booking type is used
    Given I am logged in via channel "ADAirport"
    When the "ADAirport" send getInternalPaymentAvailableFunds request using filter "UNKNOWN" and fund type "credit-file"
    Then I will return a error message "SVC_100188_3001"
