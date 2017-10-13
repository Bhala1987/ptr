@Sprint27
@backoffice:FCPH-9197
Feature: Setup of Primary and Secondary Refund Reason Codes

  Scenario: Mandatory fields for creating a primary reason code
    Given I'm in the backoffice
    And I choose to setup a primary reason code
    But the values are missing for mandatory fields
      | name | code |
    When I click on done
    Then I will see a error message for missing mandatory fields

  Scenario: Mandatory fields for creating a secondary reason code
    Given I'm in the backoffice
    And I choose to setup a primary reason code
    But the values are missing for mandatory fields
      | name | code | primarycode |
    When I click on done
    Then I will see a error message for missing mandatory fields

  Scenario Outline: Code must be unique
    Given I'm in the backoffice
    And I will create the <Level> code
    And I enter a code for the <Level> reason code
    But the code is not unique
    When I click on done
    Then I will see a error message
    Examples:
      | Level     |
      | primary   |
      | secondary |

  Scenario: Select a credit file fund and Set booking types for the secondary reason code
    Given I'm in the backoffice
    When I setup a secondary reason code
    Then I will be able to associate one or more credit file fund to the secondary reason code
    And I can set a booking type for the secondary reason code

  Scenario Outline: Maintain primary/secondary reason code
    Given I'm in the backoffice
    And <Level> reason code exists
    When I update a reason code
    Then I will be able to update the <Level> reason name
    And I will be saving successfully
    Examples:
      | Level     |
      | primary   |
      | secondary |

  Scenario: Audit creation or modification of reason code
    Given I'm in the backoffice
    When I setup or maintain a reason code
    Then An audit record for creation/modification will be created
