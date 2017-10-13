@Sprint27
Feature: Generate event to EI for abandon basket

  @manual
  @FCPH-3645
  Scenario: Generate event to EI for abandon basket
    Given I am using an amendable basket for a booking
    And the basket is updated with the passenger details
    When I dont use the basket with in the session time
    Then Hybris will send event to EI for abandon basket
    And the event will contain the same basket content
    And I should see the message count in the AL