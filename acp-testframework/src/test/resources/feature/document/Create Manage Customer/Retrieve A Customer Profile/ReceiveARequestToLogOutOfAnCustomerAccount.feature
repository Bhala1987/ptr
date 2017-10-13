@FCPH-211
Feature: Receive a Request to log out of an Customer account

  @regression
  Scenario: Process logout request from Channel
    Given I create a new customer
    When I logout
    Then I will end the Customer's active session

  Scenario: Process logout request for an already logged out customer
    Given the customer already exist
    When I logout
    And I logout
    Then I return the error message SVC_100348_2001 to the channel

  Scenario: Process logout request for an invalid customer
    Given the customer 000000 doesn't exist
    When I logout
    Then I return the error message SVC_100348_2002 to the channel