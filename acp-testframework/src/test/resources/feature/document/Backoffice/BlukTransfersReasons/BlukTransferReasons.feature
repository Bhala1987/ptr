@backoffice:FCPH-3699
@FCPH-3699
@Sprint32
@TeamA
@manual

Feature: Set up Bulk transfer Reasons

  Scenario:  - Mandatory fields to set up of Bulk transfer reasons
    Given that I am in the back office with admin login
    When I select to create/modify a new Bulk transfer reasons
    Then I see bulk transfer reason code is mandatory
    And I see localised Name is mandatory


  Scenario:  - Store the creation/modification date
    Given that I am in the back office with admin login
    And than I'm in the bulk transfer reason folder
    When I select to create/modify a new Bulk transfer reasons with valid details
    When I save the bulk transfer reason
    Then I will store creation/modification date, time User ID