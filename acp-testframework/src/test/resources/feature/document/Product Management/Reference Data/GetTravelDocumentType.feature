Feature: Provide Reference data for Manage APIS (Advanced Passenger Information)

  @Sprint28 @FCPH-361 @regression
  @ADTeam
  Scenario: I receive a list of correct document types for Advanced Passenger Information (APIS) - Json
    Given I have access to APIS
    When I select document types
    Then I will receive a list of available document types

  @AsXml
  @FCPH-343 @FCPH-3329 @FCPH-3235
  Scenario: I receive a list of correct document types for Advanced Passenger Information (APIS) - Xml
    Given I have access to APIS
    When I select document types
    Then I will receive a list of available document types

  @manual
  @FCPH-3329 @FCPH-3235
  Scenario: A user wanting to add their Advanced Passenger Information - APIS (system) No data returned
    Given I have access to APIS
    When I select document types
    And There is no data to return
    Then I will receive an empty list