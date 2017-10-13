@FCPH-8222
@Sprint28
Feature: Infant on Lap

  @local
  Scenario Outline: 1 - Generate Error if passenger does not have a checked in status BR_02900
    Given I have valid basket with via the <channel> with <passengerTypes>
    And from <departureAirport> to <destinationAirport>
    When the channel initiates a generate boarding pass request for adult with infant on lap
    Then I should get error SVC_100173_001
    Examples:
      | channel | passengerTypes       | departureAirport | destinationAirport |
      | Digital | 1 Adult, 1 Infant OL | AMS              | CPH                |


  @local @ADTeam
  Scenario Outline: 2 - Boarding pass for Infant on Lap BR_01861
    Given I have valid basket with via the <channel> with <passengerTypes>
    And from <departureAirport> to <destinationAirport>
    And the adult passenger has status check-in
    When the channel initiates a generate boarding pass request for adult with infant on lap
    And the departureAirport requires a separate boarding pass for the infant
    Then I will receive a boarding pass for the adult
    And I will receive a boarding pass for the infant
    Examples:
      | channel | passengerTypes       | departureAirport | destinationAirport |
      | Digital | 1 Adult, 1 Infant OL | AMS              | CPH                |


  @local @ADTeam
  Scenario Outline: 3 - Boarding pass for adult with the infant on lap included in the name of the adult BR_01861
    Given I have valid basket with via the <channel> with <passengerTypes>
    And from <departureAirport> to <destinationAirport>
    And the adult passenger has status check-in
    When the channel initiates a generate boarding pass request for adult with infant on lap
    And the departureAirport does NOT require a separate boarding pass for the infant
    Then I will receive a boarding pass for the adult with plus infant next to adult name
    Examples:
      | channel | passengerTypes       | departureAirport | destinationAirport |
      | Digital | 1 Adult, 1 Infant OL | LTN              | ALC                |


  @manual
  Scenario: 5 -  Boarding pass for APIS routes for single boarding pass for adult with infant on own lap
    Given that the channel received a valid boarding pass request for adult and infant on lap
    When the sector requires APIS
    Then the submitted adult document ID number and document type is shown on the boarding pass
    And the submitted infant document ID number and document type is shown on the boarding pass


  @manual
  Scenario: 6 - Gate closes times content
    Given that a boarding pass template is setted up
    When a gate closure content slot is set up
    Then the slot will contain dynamic time (time in minutes) in the description
