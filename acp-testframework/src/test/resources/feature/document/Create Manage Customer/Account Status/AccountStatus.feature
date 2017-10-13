@FCPH-9633
@Sprint28
Feature: Set account status to active when archived customer profile is retrieved

  @manual
  Scenario: 1 - Set the status to active from achieved
    Given that a customer profile has been archieved
    When I attempt to login as a Customer
    Then customer profile status should be active
    And it should Record the date and time the customer logged in
    And it should have the customer context in the session
