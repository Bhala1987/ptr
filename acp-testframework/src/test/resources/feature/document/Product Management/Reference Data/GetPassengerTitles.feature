@FCPH-265 @FCPH-266 @FCPH-360 @FCPH-359 @FCPH-171 @FCPH-172 @FCPH-3235
Feature: Basic Passenger Details are returned - passenger titles

  @FCPH-3496 @AsXml @FCPH-2752
  Scenario: Basic request for all titles
    Given there are active passenger titles available
    And I am using the channel PublicApiMobile
    When I request passenger title reference data
    Then all applicable passenger titles are returned

  @pending
  Scenario: All passenger titles returned have applicable localisation
    Given there are active passenger titles available with localisations
    When I request passenger title reference data
    Then all localisation data is present

  @pending
  Scenario: Changing the language returns only the specified localisation language
    Given there are active passenger titles available with localisations
    When I request passenger title reference data for a language
    Then only language specific reference data is returned
