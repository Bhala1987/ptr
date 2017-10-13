@FCPH-403 @FCPH-404 @FCPH-7439
Feature: Seats are allocated following booking for non-AD

  @manual
  Scenario Outline: Inventory is allocated without passenger mix for non-agent channel
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When I do the commit booking
    Then the inventory is allocated
    Examples:
      | channel         |
      | Digital         |
      | PublicApiMobile    |

  @manual
  Scenario Outline: The inventory is allocated with passengermix for non-agent channel
    Given I am using channel <channel>
    And I have return flight for <passengers>
    When I do the commit booking
    Then the inventory is allocated
    Examples:
      | channel         | passengers                                       | passengers                                       |
      | Digital         | 1 Adult, 1 Child, 1 InfantOnLap                  | 1 Adult, 1 Child, 1 InfantOnLap                  |
      | PublicApiMobile | 2 Adults, 1 Child, 1 InfantOnLap, 1 InfantOnSeat | 2 Adults, 1 Child, 1 InfantOnLap, 1 InfantOnSeat |