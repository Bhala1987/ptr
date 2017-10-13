@Sprint27 @FCPH-8688 @ADTeam
Feature: Change passenger SSR details

  As a passenger on a flight
  I would like to be able add or change my SSR information
  So that I can appropriately manage my own needs.

  Scenario: Add an SSR to a passenger in the basket.
    Given basket contains return flight for 2 Adult passengers Standard fare via the Digital channel
    And I do the commit booking
    And the booking is amendable
    When passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | DPNA |                  | false                     |
      | MAAS |                  | false                     |
    Then an SSR with the code "MAAS" should be associated to selected passenger on all flights

  Scenario: Change passenger SSR.
    Given basket contains return flight for 2 Adult passengers Standard fare via the ADAirport channel
    And I do the commit booking
    And the booking is amendable
    And passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | DPNA |                  | false                     |
    When passenger "1" updates SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | MAAS |                  | false                     |
    Then an SSR with the code "MAAS" should be associated to selected passenger on all flights

  Scenario: Delete passenger SSR.
    Given basket contains return flight for 2 Adult passengers Standard fare via the Digital channel
    And I do the commit booking
    And the booking is amendable
    And passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | MAAS |                  | false                     |
    When the selected passenger deletes their "MAAS" SSR
    Then the selected passenger should have no SSRs in the basket

  @Sprint28 @FCPH-3958 @ADTeam
  Scenario: Return error when sector doesn't support SSR.
    Given basket contains return flight for 2 Adult passengers Standard fare via the Digital channel
    And I do the commit booking
    And the booking is amendable
    And I am using ADAirport channel
    And I login as agent with username as "rachel" and password as "12341234"
    And passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | DEAF |                  | false                     |
    Then the channel will receive a warning with code SVC_100520_3010

  Scenario: Override sector SSR restrictions.
    Given basket contains return flight for 2 Adult passengers Standard fare via the Digital channel
    And I do the commit booking
    And the booking is amendable
    And I am using ADAirport channel
    And I login as agent with username as "rachel" and password as "12341234"
    And passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | DEAF |                  | true                      |
    Then an SSR with the code "DEAF" should be associated to selected passenger on all flights

  @Sprint28 @FCPH-3958
  Scenario: Attempting to add an SSR to channel that doesn't allow that SSR.
    Given basket contains return flight for 2 Adult passengers Standard fare via the Digital channel
    And I do the commit booking
    And the booking is amendable
    And I am using Digital channel
    When passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | DEPU |                  | false                     |
    Then I will receive an error with code 'SVC_100520_3009'

  @Sprint28 @FCPH-3958 @local
  Scenario: Attempting to add an SSR to a checked in passenger on a channel that doesn't allow it.
    Given I am using channel Digital
    And the channel has initiated a CheckInForFlight for "1 Adult"
    And Passenger status should change to checked-in on the flight
    And channel send getbooking request
    And the booking is amendable
    When passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | BLND |                  | false                     |
    Then I will receive an error with code 'SVC_100520_3011'

  @Sprint28 @FCPH-3958 @ADTeam
  Scenario: Add an SSR that doesn't have mandatory Ts & Cs but set them and expect that nothing happens.
    Given basket contains return flight for 1 Adult passengers Standard fare via the Digital channel
    And I do the commit booking
    And the booking is amendable
    And I am using Digital channel
    When passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | MAAS | true             | false                     |
    Then the selected passenger should have no SSRs in the basket

  @Sprint28 @FCPH-3958
  Scenario: Test that exceeding the passenger SSR limit causes an error.
    Given basket contains return flight for 2 Adult passengers Standard fare via the Digital channel
    And I do the commit booking
    And the booking is amendable
    When passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | DPNA |                  | false                     |
      | BLND |                  | false                     |
      | DEAF |                  | false                     |
      | NUT  |                  | false                     |
      | PETC |                  | false                     |
      | MAAS |                  | false                     |
    Then I will receive an error with code 'SVC_100520_3008'

  @Sprint28 @FCPH-3958
  Scenario: Test that a partial success response is returned if an SSR can only be added for part of the flight roster.
    And basket contains return flight for 2 Adult passengers Standard fare via the Digital channel
    And I do the commit booking
    And the booking is amendable
    When passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | BLND | true             | false                     |
    Then the channel will receive a warning with code SVC_100520_3010
