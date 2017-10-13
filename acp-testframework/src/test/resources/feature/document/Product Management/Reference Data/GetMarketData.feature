@FCPH-493 @FCPH-499
Feature: Retrieve market data

  @pending
  @manual
  Scenario: All market data can be returned
    Given there is active market data
    When I call the get market data service
    Then the active market data is returned

  @pending
  @manual
  Scenario: All inactive market data is not returned
    Given there is inactive market data
    When I call the get market data service
    Then the inactive market data is not returned