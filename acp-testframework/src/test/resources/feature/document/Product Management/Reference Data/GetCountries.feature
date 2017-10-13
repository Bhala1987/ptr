Feature: Retrieve Country information

  @FCPH-202 @FCPH-343  @AsXml @FCPH-171 @FCPH-172 @FCPH-323 @FCPH-3329
  @regression
  Scenario: There are countries
    Given there are active countries in the database
    And I am using the channel PublicApiB2B
    When I call the get countries service
    Then there are countries returned

  @Sprint26 @FCPH-7998 @FCPH-2752
  Scenario: All countries are returned
    Given there are active countries in the database
    When I call the get countries service
    Then all active countries are returned
    And the country information includes the country international dialling code

  @manual @FCPH-202 @FCPH-171 @FCPH-172 @FCPH-323 @FCPH-3329
  Scenario: Inactive countries are not returned
    Given there are inactive countries in the database
    When I call the get countries service
    Then the inactive countries are not returned

  @manual @FCPH-202 @FCPH-171 @FCPH-172 @FCPH-323 @FCPH-3329
  Scenario: The correct countries are returned
    Given there are active countries in the database
    When I call the get countries service
    Then associated locales are displayed with each country