Feature: Set up Check In Window at Sector Level

  @Sprint28
  @manual @backoffice:FCPH-9596
  Scenario: 1 - Set up the Online Check In window for the sector BR_00152
    Given the I am in a sector in the back office
    When I select to create a sector
    Then I can enter Online Check In open in minutes
    And I can enter Online Check In close in minutes

  @Sprint28
  @manual @backoffice:FCPH-9596
  Scenario: 2 - Set up the Airport Check In window for the sector BR_00152
    Given the I am in a sector in the back office
    When I select to create a sector
    Then I can enter Airport Check In open in minutes
    And I can enter Airport Check In close in minutes

  @Sprint29
  @manual @backoffice:FCPH-9597
  Scenario: 1 - Set up twilight check open and close times
    Given that I am setting up an airport
    When I select Twilight Check In to true
    Then I must enter a Twilight Check In Open Time (local of airport must be stored)
    And I must enter a Twilight Check In Close Time (local of airport must be stored)
