@Sprint29
@FCPH-10082
@TeamA
@manual
@backoffice:FCPH-10082
Feature: Back office - Manage Disruption levels and Reasons


  Scenario: 1 - Mandatory Fields to create Disruption Level
    Given that I am in the back-office
    And the Disruption Level folder is open
    When I select the option to create a new Disruption Level
    Then I will be able to enter a Code and Name
    And the Code must be a mandatory input field
    And an initial list of disruption level codes must be set-up. See DL_ACP_15

  Scenario: 2 - Generate error if code is not unique
    Given that I have input the details to create a new Disruption Level
    When I submit the data, e.g. Done
    And the Code is not unique
    Then the system must display an error message

  Scenario: 3 - Store audit record of the creation
    Given that I have submitted the data to create a new Disruption Level
    When the Code is unique
    Then the system must create the new record
    And store the Creation Date & Time and User ID

  Scenario: 4 - Mandatory Fields to create Disruption Reason
    Given that I am in the back-office
    And the Disruption Reason folder is open
    When I select the option to create a new Disruption Reason
    Then I will be able to enter a Code and Name
    And the Code must be a mandatory input field
    And at least one name must be populated in one language
    And an initial list of Disruption Reasons must be set-up. See DL_ACP_25

  Scenario: 5 - Generate error if Disruption Reason code is not unique
    Given that I have input the details to create a new Disruption Reason
    When I submit the data, e.g. Done
    And the Code is not unique
    Then the system must display an error message

  Scenario: 6 - Generate error if Disruption Reason name not provided
    Given that I have input the details to create a new Disruption Reason
    When I submit the data, e.g. Done
    And the name is not provided in at least one language
    Then the system must display an error message

  Scenario: 7 - Store audit record of the creation for Disruption Reason
    Given that I have submitted the data to create a new Disruption Reason
    When the Code is unique
    And at least one name must be populated in one language
    Then the system must create the new record
    And store the Creation Date & Time and User ID

  Scenario: 8 - Amend Disruption Reason FCPREQ-1109
    Given that the Disruption Reason folder is open
    When I select a Disruption Reason to edit
    And I am an authorised user
    Then the system will allow me to edit the Name field only
    And I can save and view the new value

  Scenario: 9 - Amend Disruption Reason FCPREQ-1109
    Given that the Disruption Reason folder is open
    When I select a Disruption Reason to edit
    And I am not an authorised user
    Then I will see an error message
    And the system will not allow me to edit the Name field or any other field