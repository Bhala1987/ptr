Feature: Set up ICTS

  @backoffice:FCPH-3345
  @Sprint30
  @TeamD
  Scenario: Mandatory fields to create ICTS status
    Given that I'm on the ICTS status folder
    When I select to create a ICTS status
    Then I must enter a code
    And I must enter a Name (as per http://conf.europe.easyjet.local/display/FCP/ICTS+Status)

  @backoffice:FCPH-3345
  @Sprint30
  @TeamD
  Scenario: Generate error if code is not unique
    Given that I'm on the ICTS status folder
    When I complete the creation of ICTS status
    And the Code is not unique
    Then I will see a error message

  @backoffice:FCPH-3345
  @Sprint30
  @TeamD
  Scenario: store audit record of the creation
    Given that I'm on the ICTS status folder
    When I complete the creation of ICTS status
    And the Code is unique
    Then I will store the new ICTS status record
    And the creation, date, time and User ID will be stored