@Sprint24
@backoffice:FCPH-393
Feature: Manage Credit File Funds
  As Authorised Back Office User
  I will be able to Manage credit files reference data
  In order to make credit files available as a payment method

  Scenario: 1 - View Credit File Fund
    Given I searched for Credit File Fund on the left navigator
    When I select Credit File Fund from the search results
    Then a list of Credit file funds are displayed

  Scenario: 2 - View Credit File Fund screen
    Given I am on the Manage Credit file fund page
    When I click on Add
    Then I see the following fields enter Code, Name, Currency, Start Balance, Current Balance, Cost Centre / Budget, warning Alert value, Alert email address, Active From Date, Active To date, Channel, user Group, Booking Type

  Scenario: 3 - Mandatory fields to create a credit file fund
    Given I am on the Manage Credit file fund screen
    When I create a credit file fund
    Then I must enter all the following fields Code, Name, Single Currency, Cost Centre / Budget, Active From Date, Active To date

  Scenario: 4 - Code needs to be unique
    Given I am on the Manage Credit file fund screen
    When I click on done
    And the code is not unique
    Then I will see a error message

  Scenario: 5 - Warning alert value can not be more than the start balance
    Given I am on the Manage Credit file fund screen
    When I enter a warning alert value
    And it is greater than the start balance
    Then I will see a error message that the warning alert value can not be greater than the start

  Scenario: 6 - Record Creation Date and Time with User ID
    Given I am on the Manage Credit file fund screen
    When all the mandatory fields are completed
    And I click on Done
    Then the credit file fund is created
    And I will create Creation Date and Time with User ID

  Scenario: 7 - Search for existing credit File fund
    Given I am on the Manage Credit file fund page
    When I select to search for a credit file
    Then I will be able to enter Code, Name, Active Date from, Active Date to

  Scenario: 8 - Update an existing credit file fund
    Given I am on the Manage Credit file fund page
    When I select to amend a credit file
    Then I should only be able to amend Active to and from date, Credit file Fund Name, Start Balance

  Scenario: 9 - Reset the Current balance
    Given I am on the Manage Credit file fund page
    When I amend the start balance of the credit file
    And I click on Done
    Then I will change the current balance to the new start balance

  Scenario: 10 - Record Modification Date and Time with User ID
    Given I am on the Manage Credit file fund screen
    When I click on Done
    Then the credit file fund will be updated
    And the Modification Date and Time with User ID will be recorded