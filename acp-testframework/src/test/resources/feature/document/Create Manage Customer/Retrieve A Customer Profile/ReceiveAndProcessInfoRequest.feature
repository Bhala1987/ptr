@Sprint24
@FCPH-3992
Feature: Retrieve Saved Passengers, Significant others and Dependents

  @regression
  Scenario: 1 - Retrieve Saved Passenger
    Given I am using channel Digital
    And I create a customer and change their password
    And that a customer has saved passenger information
    And the customer is logged in
    And that the "Digital" has initiated a request to getSavedPassenger with accessToken
    When I receive an authenticated request to getSavedPassenger from the channel
    Then I will get the saved passenger for the customer to the channel

  Scenario: 2 - Retrieve dependents
    Given I create a customer and change their password
    And that a staff Customer is logged in
    And that the "Digital" has initiated an authenticated request to getDependants
    When I receive an authenticated request to getDependents from the channel
    Then I will return a list of Dependents associated to the authenticated customer

  Scenario: 3 - Retrieve significant others
    Given I create a customer and change their password
    And that a staff Customer is logged in
    When that the "Digital" has initiated an authenticated request to getSignificantOthers
    When I receive a request to getSignificantOthers from the channel
    Then I will return a list of Significant associated to the customer

  Scenario: GET Saved Passenger request when not logged in
    Given that an unauth customer has saved passenger information
    And the customer is not logged in
    And that the "Digital" has initiated a request to getSavedPassenger with no access token
    When I receive an unauthenticated request to getSavedPassenger from the channel
    Then I should add error message "SVC_100000_2069" to the SavedPassenger return message

  Scenario: GET Dependants request when not logged in
    Given that the "Digital" has initiated a request to getDependents
    And customer is not logged in
    When I receive a request to getDependents from the channel
    Then I should add error message "SVC_100000_2069" to the Dependants return message

  Scenario: GET Significant Other request when not logged in
    Given the staff customer has significant others
    And the customer is not logged in
    When the "Digital" has initiated an unauthenticated request to getSignificantOthers
    When I receive a request to getSignificantOthers from the channel
    Then I should add error message "SVC_100012_2069" to the Significant Others return message