@backoffice:FCPH-8276
Feature: Manage Additional Seat Reasons

  Scenario: Add an Additional Seat Reason
    Given that I am on the Manage Additional Seat Reason page in backoffice
    When I click on Add
    Then I must be able to add an additional seat reason Code & Value

  Scenario: Update an Additional Seat Reason
    Given that I am on the Manage Additional Seat Reason page in backoffice
    And I clicked on an existing Additional Seat Reason Code & Value to be modified
    When I update or modify an existing Additional Seat Reason Code & Value
    Then an existing Additional Seat Reason Code & Value should be updated

  Scenario: Remove an Additional Seat Reason
    Given that I am on the Manage Additional Seat Reason page in backoffice
    And I clicked on an existing Additional Seat Reason Code & Value to be removed
    When I delete an existing Additional Seat Reason Code & Value
    Then an existing Additional Seat Reason Code & Value should be removed

  Scenario: Store audit record of modification
    Given that I have added or changed an Additional Seat Reason Code & Value in backoffice
    When I see the last changes
    Then an audit record should be created which includes the user ID, modified values, date and time modified