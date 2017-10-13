@Sprint27
@backoffice:FCPH-3495
Feature: Setup of refund type

  Scenario: Mandatory fields for creating refund type
    Given I'm in the backoffice
    And I choose to setup a refund type code
    But the values are missing for mandatory fields
      | name | code |
    When I click on done
    Then I will see a error message for missing mandatory fields

  Scenario: Code must be unique
    Given I'm in the backoffice
    And I want create the refund type code
    But the code is not unique
    When I click on done
    Then I will see a error message

  Scenario: Maintain refund type reason code
    Given I'm in the backoffice
    And refund type code exists
    When I update a reason code
    Then the refund type name will be updated

  Scenario: Audit creation or modification of refund type
    Given I'm in the backoffice
    When I setup or maintain a refund type
    Then An audit record for creation/modification will be created
