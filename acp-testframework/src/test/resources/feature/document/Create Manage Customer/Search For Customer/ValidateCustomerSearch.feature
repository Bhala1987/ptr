@FCPH-175 @FCPH-176 @FCPH-178 @FCPH-179
Feature: Validate criteria used for searching for a customer

  Background:
    Given a customer with valid data exists

  Scenario: Lastname must be provided with firstname or title
    When I search using "lastName" only
    Then I am informed that I cannot search using only lastname

  Scenario: Lastname must be provided along with a minimum of 1 character in first name
    When I search using lastname and firstname is empty
    Then I am informed that lastname needs a firstname value

  Scenario: Last name with one character in first name
    When I search using lastname and firstname is 1 character
    Then the customer profile is returned

  Scenario: Lastname with title
    When I search using lastname and title
    Then the customer profile is returned

  Scenario: Firstname must be provided with one or more other fields
    When I search using "firstName" only
    Then I am informed that I cannot search using only firstname

  Scenario Outline: Search request can be sent in any case
    When I search for the profile using "<case>"
    Then the customer profile is returned
    Examples:
      | case  |
      | upper |
      | lower |
      | mixed |

  @pending
  @manual
  Scenario Outline: Search using valid email
    And a customer with "<email>"exist
    When I search using valid "<email>"
    Then the customer profile is returned
    Examples:
      | email               |
      | name@testing.com    |
      | name@testing.co.uk  |
      | name@testing.ie     |
      | name@company.domain |

  @regression
  Scenario: Search request can be sent using partial data
    When I search for the profile using partial firstName and lastName
    Then the customer profile is returned

  Scenario: Title cannot be the only search parameter
    When I search using "title" only
    Then I am informed that I cannot search using only title

  @pending
  @manual
  Scenario: Search is not allowed when none of the query parameters are filled in
    When I search using empty query parameters
    Then I am informed that the search criteria are not valid

  Scenario Outline: does not search with invalid address
    When I search using the email address "<email>"
    Then I am informed that the email address is not valid
    Examples:
      | email        |
      | ted@test     |
      | @test.com    |
      | t$$@test.com |
      | ted@         |
      | ted@.com     |

  @manual
  Scenario: By default the list should be sorted by last name ascending
    When I search for multiple matching profiles
    Then the results are sorted by "lastName"

  @manual
  Scenario: Search results can be sorted by first name
    When I search for multiple matching profiles and choose to sort the results by "firstName"
    Then the results are sorted by "firstName"

  @pending
  @manual
  Scenario: change the sort order of the Country after search results populates
    When I search for multiple matching profiles along with sorting country name
    Then I can sort "countryName" from the populated list

  Scenario: Search results can be sorted by country name
    When I search for multiple matching profiles and choose to sort the results by "countryName"
    Then the results are sorted by "countryName"

  @pending
  @manual
  Scenario: Search should not return more than the maximum allowable number of results (is this configurable?)
    Given there are more ">max" customers from a location
    When I search for multiple matching profiles
    Then I should not see more than expected results
    And it should display "Too many results found, please narrow your search"

  @pending
  @manual
  Scenario: Search results should display when requesting max expected results
    Given there are more "=max" customers from a location
    When I search for multiple matching profiles
    Then I should  see more the expected results
    Then I should not see more than 200 results

  Scenario: No matching records found
    When I search for a customer with no matching data
    Then I should see "No results found" error message