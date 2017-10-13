Feature: Retrieve booking types information

  @pending
  Scenario: Booking types are returned
    Given there are active booking types
    When I call the get booking types service
    Then all the active booking types are returned