@backoffice:FCPH-9956
@backoffice:FCPH-10340
@backoffice:FCPH-10341
@backoffice:FCPH-10343
@backoffice:FCPH-10344
@backoffice:FCPH-10345
@backoffice:FCPH-10868
@Sprint29
@Sprint30
Feature: DEL Extract reference data

  @manual
  Scenario: 1 - Configure extraction period/frequency from backoffice
    Given I am in backoffice
    When I select the cronjob for extraction data
    Then I can set the frequency of extraction which could be hourly or daily

  @manual
  Scenario: 2 - Extract any new transactions since the last extraction period
    Given I export an entity with delta export enable attribute set to true
    When the system start the cronjob of selected entity
    Then the file generated will contain only the data with time after last extraction period

  @manual
  Scenario: 4 8 - Extracts data to be provided in logical groupings
    Given the system has initiated the cronjob to extract the logical groups from hybris replica database
    And the system identifies the entities
    When the system exports the data
    Then there will be individual files exported for each of the entities defined in the cronjob
    And the referential integrity of entities is extracted for related entities

  @manual
  Scenario: 10 - An extract can be re-run at entity/group level if required
    Given I am in backoffice
    When I choose a cronjob of extraction data
    Then I can run manually that particular cronjob

  @manual
  Scenario: 12 - Extract Data from hybris database into file with custom headers
    Given the system has initiated the cronjob to extract the data from hybris replica database
    And the system identifies the fields to extract from the cronjob configuration
    When the system exports the data
    Then the file created will have the fields selected as headers in the correct order
    And all the entities in cronjob configuration will be exported
