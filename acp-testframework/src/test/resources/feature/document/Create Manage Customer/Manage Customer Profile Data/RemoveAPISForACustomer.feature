@Sprint24
@FCPH-3339
Feature: Receive request to remove APIS for a Customer

  Scenario Outline: Remove APIS request received for Customer Profile with invalid parameter
    Given I create a Customer
    And I sent a request to SetAPI
    And I want to delete Apis information
    But the request contains "<invalid>"
    When I send a request do delete APIS information of the Customer in Customer profile with "<channel>"
    Then I will receive an error with code '<error>'
    Examples:
      | invalid            | error           | channel |
      | invalid customerId | SVC_100345_2001 | Digital |
      | invalid documentId | SVC_100345_2002 | Digital |

  @regression
  Scenario Outline: Remove APIS for Customer
    Given I create a Customer
    And I sent a request to SetAPI
    And I want to delete Apis information
    When I send a request do delete APIS information of the Customer in Customer profile with "<channel>"
    And return confirmation
    Examples:
      | channel |
      | Digital |
