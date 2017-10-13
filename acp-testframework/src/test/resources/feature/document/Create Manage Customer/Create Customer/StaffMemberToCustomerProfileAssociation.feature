@FCPH-2814
Feature: Validate Receive request to Associate Staff Member to Customer Profile

  @regression
  Scenario: Mark registered member account as staff customer group, Registered member account is not linked to another employee
    Given a valid customer profile has been created
    When a valid request to associate staff member to member account
    Then member account is associated

  Scenario: Mark registered member account as staff customer group, Registered member account is already linked to another employee record
    Given a valid customer profile has been created
    When a invalid request to associate staff member to member account
    Then member account is not associated

  Scenario Outline: Mandatory fields email,title,firstname,lastname,employee Id and employee Email are not passed in the request
    Given a request to associate staff member to member account to validate "<mandatoryField>" mandatory field
    When the mandatory field "<mandatoryField>" is not passed in the request
    Then It will return a error message "<errorMessage>" to the channel
    Examples:
      | mandatoryField | errorMessage    |
      | email          | SVC_100269_2002 |
      | title          | SVC_100269_2003 |
      | firstname      | SVC_100269_2004 |
      | lastname       | SVC_100269_2005 |
      | employeeId     | SVC_100269_2006 |
      | employeeEmail  | SVC_100269_2007 |

  @ignore
  @manual
  Scenario: Unable to identify employee appears on HR data store
    Given that I have received a valid request to associate staff member to member account
    When I validate if the employee ID is appears on the HR data store
    And I can not find the employee ID
    Then I will abort the Associate staff member to customer profile process
    And Return Error code to the channel

  @ignore
  @manual
  Scenario: Registered member account is already linked to another employee record
    Given that I have received a valid request to associate staff member to member account
    And the employee is known on the HR data store
    When I check the member email account exists
    And the registered member account already linked to another employee record
    Then I will abort the associate staff member to customer profile process
    And return error code to the channel

  @ignore
  @manual
  Scenario: Registered member account is already linked to another employee record
    Given that I have received a valid request to associate staff member to member account
    And the employee is known on the HR data store
    When I check the member email account exists
    And the registered member account already linked to another employee record
    Then I will abort the associate staff member to customer profile process and return error code to the channel