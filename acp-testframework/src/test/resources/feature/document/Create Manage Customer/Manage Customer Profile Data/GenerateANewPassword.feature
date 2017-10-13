@FCPH-3350
Feature: Receive request to Generate a new Password

  Scenario: Receive Generate New Password request
    Given There are customer Id into Database
    When I send a request to update password from "ADCustomerService"
    And I will generate a new Medium Strength password
    Then I will store the new password against the profile

  @regression
  Scenario: Remove Sensitive attribute
    Given There are customer Id into Database
    When I send a request to update password from "ADCustomerService"
    Then I will store the new password against the profile
    And I will remove any Saved APIS, Saved Payment methods, SSR and Saved Passengers details from the Customer's profile


