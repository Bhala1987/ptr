@FCPH-2610
@Sprint28
Feature: Validate Check In Status for Flight on the booking

  @manual
  Scenario Outline: 1 - Return the check in window when sector set BR_00132 BR_00152
    Given the channel receive a valid getBooking request with <origin> and <destination> and <channel>
    And check open and close times exists for the sector
    When the check in open and close times is calculated based on the STD of the flights in the booking
    Then the check in open of flight's sector is used for one or more flights on the booking
    Examples:
      | origin      | destination   | channel     |
      | LTN         | ALC           | Digital     |
      | LTN         | ALC           | ADAirport   |

  @manual
  Scenario Outline: 2 - Return the check in window when no sector is set BR_00132 BR_00152
    Given the channel receive a valid getBooking request with <origin> and <destination> and <channel>
    And check open and close times does not exists for the sector
    When the check in open and close times is calculated based on the STD of the flights in the booking
    Then the check in open of flight's departing airport is used for one or more flights on the booking
    Examples:
      | origin      | destination   | channel     |
      | LTN         | ALC           | Digital     |

  @manual
  Scenario Outline: 3 - Return check in open for a multiple flight booking based on channel BR_00129
    Given the channel receive a valid getBooking request with <origin> and <destination> and <channel>
    And the booking contains multiple flights
    When the check in is open for first flight on the booking
    And any other flights on the booking depart with x=30 days of the first flight STD
    Then the check in is open for the those other flights on the booking based on the channel
    Examples:
      | origin      | destination   | channel     |
      | LTN         | ALC           | Digital     |
      | LTN         | ALC           | ADAirport   |

  @manual
  Scenario Outline: 4 -  Return check in as allowed function
    Given the channel receive a valid getBooking request with <origin> and <destination> and <channel>
    When the check in window is open
    Then check in is returned in the allowed functions to the channel
    And the check in window is returned to the channel
    Examples:
      | origin      | destination   | channel     |
      | LTN         | ALC           | Digital     |
      | LTN         | ALC           | ADAirport   |

