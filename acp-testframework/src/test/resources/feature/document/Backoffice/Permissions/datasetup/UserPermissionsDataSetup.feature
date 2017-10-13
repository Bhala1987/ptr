@TeamA
@Sprint32
@FCPH-11465
@backoffice
@manual

Feature: Able to define if a user group is allowed each capability or not- With admin login

  Scenario: 1 - Able to define if a user group is allowed each capability or not
    Given that I am in the back office with admin login
    When I see all Agent Permission records are available in backoffice
    And compare agent permission records against spreadsheet for accessstatus
    And verify "<accessstatus>" with the combination of "<category>","<capability>","<usergroup>"
    Then I will determine the datasetup is done correctly for each "<usergroup>"

