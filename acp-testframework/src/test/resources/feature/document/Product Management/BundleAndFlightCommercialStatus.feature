Feature: Commercial Status Changed Event for newly created flights

  @manual
  @TeamD
  @Sprint31
  @FCPH-10889
  Scenario: Generate event when a new flight is created
    Given that a new flight has been created
    When I create the flight
    Then I generate an event to inform downstream systems
    