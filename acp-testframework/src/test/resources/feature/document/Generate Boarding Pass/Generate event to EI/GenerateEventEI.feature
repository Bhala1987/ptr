@FCPH-8054
@Sprint28
Feature: Generate event to EI that boarding pass has been generated

  @manual
  Scenario: 1 - Generate event to EI that a boarding pass has been generated
    Given that the channel send a generate boarding pass request
    When the boarding pass is sent to the channel
    Then it will generate an event to EI to inform them a boarding pass has been generated

