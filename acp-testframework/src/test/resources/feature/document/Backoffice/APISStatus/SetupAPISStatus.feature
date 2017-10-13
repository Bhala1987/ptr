@backoffice:FCPH-3462
@Sprint25
Feature: Set up APIS

  Scenario: 1 - Mandatory fields to create APIS status
    Given that I'm on the APIS status folder
    When I select to create a APIS status
    Then I must enter a code
    And I must enter a Name (as per http://conf.europe.easyjet.local/display/FCP/APIS+Status)

  Scenario: 2- Generate error if code is not unique
    Given that I'm on the APIS status folder
    When I select Done
    And the Code is not unique
    Then I will see a error message


  Scenario: 3 - store audit record of the creation
    Given that I'm on the APIS status folder
    When I select Done
    And the Code is unique
    Then I will store the new APIS status record
    And the creation, date, time and User ID will be stored