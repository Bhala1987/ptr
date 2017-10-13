@TeamC @Sprint29 @FCPH-8689 @ADTeam
Feature: Process Change Passenger SSR Request with a purchased seat, not Public API

  Scenario Outline: Validate seat
    Given I am using channel <channel>
    When I send a manage passenger <code> SSRs request on edit booking with purchased seat <restricted> rule
    Then I <expect> receive error <error>
    And the passenger SSRs <should> be update
    Examples: Seat still suitable
      | channel | restricted | code | should | expect | error |
      | Digital | false      | BLND | true   | false  |       |
    Examples: Seat is no longer suitable
      | channel | restricted | code | should | expect | error           |
      | Digital | true       | BLND | false  | true   | SVC_100600_1012 |