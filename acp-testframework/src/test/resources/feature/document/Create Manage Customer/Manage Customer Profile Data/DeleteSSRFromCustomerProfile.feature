@FCPH-8391
@Sprint25
Feature: Delete Customer SSR Data

  Background:
    Given am using channel ADAirport
    And I have a customer with saved SSRs

  Scenario: 1 - delete SSR request not in required format (unknown customer)
    Given the channel has initiated an invalid delete SSR request
    When the delete SSR request is sent
    Then I will receive an error with code 'SVC_100342_3003'
  
  Scenario: 2 remove a single SSR from the Customer profile
    Given that the channel creates a request to delete a single SSR from a customer profile
    When the delete SSR request is sent
    Then SSR delete confirmation is returned to the channel

  Scenario: 3 remove All SSR from the Customer profile
    Given that the channel creates a request to delete all SSRs from a customer profile
    When the delete SSR request is sent
    Then SSR delete confirmation is returned to the channel

  Scenario: 4 attempt to remove all SSRs when customer has none saved
    Given that the channel creates a request to delete all SSRs from a customer profile
    And the delete SSR request is sent
    When the channel attempts to delete all SSRs again
    Then I will receive an error with code 'SVC_100342_3004'

  Scenario: 5 attempt to remove non-existing SSR from customer
    Given that the channel creates a request to delete all SSRs from a customer profile
    And the delete SSR request is sent
    When the channel attempts to remove a single SSR
    Then I will receive an error with code 'SVC_100342_3001'
