@FCPH-498
Feature: Retrieve currency information

  @regression
  Scenario: Active Currencies are returned
    Given there are active currencies
    When I call the get currencies service
    Then the active currencies are returned

  @AsXml
  Scenario: Inactive currencies are not returnedWhen I call the get currencies service
    Given there are inactive currencies
    When I call the get currencies service
    Then the inactive currencies are not returned