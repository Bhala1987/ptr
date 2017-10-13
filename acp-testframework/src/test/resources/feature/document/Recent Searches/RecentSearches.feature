@FCPH-3704 @Sprint28
Feature: Receive request Clear recent searches

  Scenario: 1 - Error message when unable to identify customer
    Given the channel has initiated a request to clear recent search
    When the customer cannot be identify in the request
    Then I will receive a error login customer message SVC_100370_3002

  Scenario: 2 - Error message when the recent search can not be identified
    Given customer has logged in
    And the channel has initiated a request to clear specific recent search
    When the recent search not been identified
    Then I will receive a error customer message SVC_100370_3001

  Scenario: 3 - Removing a single recent search
    Given customer has logged in
    And the customer search more than one flight
    When the channel send a request to clear specific recent search
    Then The recent search will be removed from the customer profile

  @regression
  Scenario: 4 - Remove multiple recent search from the profile
    Given customer has logged in
    And the customer has more than recent search
    When the channel has initiated a request to clear all recent searches
    Then The recent searches will be removed from the customer profile
