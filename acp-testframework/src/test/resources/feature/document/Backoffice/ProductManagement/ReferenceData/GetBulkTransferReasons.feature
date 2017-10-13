@FCPH-10905 @TeamA @Sprint32
Feature:
  As the backoffice
  I want to be able to retrieve bulk transfer reasons

  Scenario: Return all bulk transfer reasons
    Given the channel Digital is used
    When I send the request to getBulkTransferReasons service
    Then the list of requested bulk transfer is returned

  Scenario Outline: Return bulk transfer reasons for selected languages
    Given the channel Digital is used
    And the header contains acceptLanguage = <language>
    When I send the request to getBulkTransferReasons service
    Then the list of requested bulk transfer is returned
    Examples:
      | language |
      | en       |
      | es       |
      | fr       |