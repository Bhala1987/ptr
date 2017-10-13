Feature: Retrieve languages information

  @regression
  Scenario: Active languages are returned
    Given there are active languages
    When I call the get languages service
    Then the active languages are returned

  @AsXml
  Scenario: Inactive languages are not returned
    Given there are inactive languages
    And I am using the channel PublicApiMobile
    When I call the get languages service
    Then the inactive languages are not returned
