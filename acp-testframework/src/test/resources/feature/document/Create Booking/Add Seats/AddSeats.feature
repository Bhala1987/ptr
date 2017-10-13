@TeamD
Feature: Return additional seat reason to the channel

  @Sprint32 @FCPH-11180
  Scenario Outline: 1 - Return a list of additional seat reasons
    Given the channel <channel> is used
    When I send the request to Additional Seat Reason service
    Then a list of Additional Seat Reason is returned
    Examples:
      | channel   |
      | Digital   |
      | ADAirport |