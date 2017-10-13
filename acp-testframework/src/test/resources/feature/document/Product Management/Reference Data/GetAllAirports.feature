Feature: Retrieve Airport information

  @FCPH-488 @FCPH-494
  Scenario: Active airport are returned with specified fields
    Given a channel is used
    And at least on airport is defined
    When I send the getAirports request
    Then a list of airports is returned

  @regression
  @AsXml @FCPH-2752
  Scenario: Active airport are returned with specified fields - AsXML
    Given one of this channel PublicApiMobile, PublicApiB2B is used
    And at least on airport is defined
    When I send the getAirports request
    Then a list of airports is returned

  @TeamD
  @Sprint29
  @backoffice:FCPH-10132
  Scenario: Enter timezone against the airport in the back office
    Given I am in a airport folder in the back office
    When I select to create a airport
    Then I will be able to enter a timezone for the airport
    And the timezone is mandatory

  @TeamD
  @Sprint29
  @FCPH-10132
  Scenario: Return timezone in the getAirports response
    Given a channel is used
    And at least on airport is defined
    When I send the getAirports request
    Then all the airports have a timezone specified
