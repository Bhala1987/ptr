@FCPH-387
Feature: Get available fare types

  @regression
  Scenario: Retrieve fare type description
    Given that I have retrieved all the available fare types with descriptions and options
    When  I create a request to retrieve available fare types
    Then  I will get the description as part of the response
    And   I will get all the options available with fare

  Scenario Outline: Retrieve fare type with given gds fare class
    Given that I have retrieved all the available fare types with gds fare class "<gds_fare_class>"
    When  I create a request to retrieve available fare types with gds fare class "<gds_fare_class>"
    Then  I will get only the fare types with the given gds fare class "<gds_fare_class>"
    Examples:
      | gds_fare_class |
      | A              |
      | B              |
      | C              |
