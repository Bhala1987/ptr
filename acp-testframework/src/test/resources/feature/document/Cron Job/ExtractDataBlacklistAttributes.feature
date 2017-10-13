@backoffice:FCPH-10823
@Sprint30
Feature: DEL Extract data - Blacklist Attributes

  @manual
  Scenario: 1 - Single Entity Cronjob generate export file and .EOT file
    Given the system has initiated the cronjob to extract the entity from hybris replica database
    And the system identifies the entity
    When the system exports the data
    Then there will be a file exported for the selected entity defined in the cronjob
    And a single EOT file will be generated to signal the end of transmission

  @manual
  Scenario: 2 - Logical Group Cronjob generate export files and .EOT file
    Given the system has initiated the cronjob to extract the logical groups from hybris replica database
    And the system identifies the entities of the logical group
    When the system exports the data
    Then there will be individual files exported for each of the entities defined in the cronjob
    And a single EOT file will be generated to signal the end of transmission

  @manual
  Scenario: 3 - Orchestrator Cronjob generate export files and .EOT file
    Given the system has initiated the cronjob to extract all the logical groups from hybris replica database
    And the system identifies the entities of the logical groups
    When the system exports the data
    Then there will be individual files exported for each entity of the logical groups defined in the cronjob
    And a single EOT file will be generated to signal the end of transmission

  @manual
  Scenario: 4 - Extract any new transactions since the last extraction period
    Given I export an entity with delta export enable attribute set to true
    When the system start the cronjob of selected entity
    Then the file generated will contain only the data with time after last extraction period
    And a single EOT file will be generated to signal the end of transmission

  @manual
  Scenario: 5 - Error during composite cronjob
    Given the system has initiated the cronjob to extract all the logical groups from hybris replica database
    And the system identifies the entities of the logical groups
    When an entity generate an error during the export
    Then the status of the composite cronjob will be ERROR
    And the system will not generate any files

  @manual
  Scenario: 6 - Delete old export files
    Given the system has initiated the cronjob to extract an entity from hybris replica database
    And the system identifies the entities of the logical groups
    When the system start the extraction
    Then the old extraction files will be deleted before the generaion of the new files


