@FCPH-456
Feature: Set bundles to deactivate

  @manual
  Scenario: Run regular job to make bundles inactive
    Given the system has initiated the cronjob to check the bundles active dates
    When the system identifies a bundle which offline date is in the past from the current date
    Then the system change the status of the bundle to archived
    And the version of the bundle is retained