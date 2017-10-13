@Sprint24
@FCPH-8304
Feature: Data from Seating Service to the PublicAPIb2b Channel

  @Sprint25 @negative
  Scenario Outline: 2 - Generate error if a standby bundle in the basket BR_00471
    Given I am using channel <Channel>
    When I make a request to retrieve seating plan and priced inventory for PublicAPI using bundle:<Fare bundle>
    Then the seat map service should return error the "SVC_100400_2003" error message stating unable to return seat map for Standby bundles
    And will not return the seat map
    Examples:
      | Channel      | Fare bundle |
      | PublicApiB2B | Standby     |

  @Sprint25
  Scenario: 3 – Generate Get Seat Map Request to Seating Service
    Given I am using channel PublicApiB2B
    When I make a request to retrieve seating plan and priced inventory for PublicAPI
    Then I should receive the seat map
    And the seat map Flight Key should be correct
    And the seat map Currency should be correct

  @Sprint25 @negative
  Scenario Outline: 4 - Generate error if invalid currency received
    Given I am using channel PublicApiB2B
    When I make a request to retrieve seating plan and priced inventory with invalid currency for PublicAPI:<Invalid currency>
    Then the seat map service should return error the "SVC_100012_20013" error message stating currency is invalid
    Examples:
      | Invalid currency |
      | DOUBLOONS        |

  @Sprint25 @negative
  Scenario Outline: 5b - Generate a error if bundle is not known
    Given I am using channel PublicApiB2B
    When I make a request to retrieve seating plan and priced inventory with invalid bundle for PublicAPI:<Invalid bundle>
    Then the seat map service should return the "SVC_100000_2067" value based error message stating bundle is invalid
    Examples:
      | Invalid bundle                   |
      | staffsupersaverdiscountbonusfare |

  @Sprint25
  @regression
  Scenario Outline: 7 – Bundle in the basket includes a seat band BR_4001
    Given I am using channel <Channel>
    And the seating band is the same level as the bundle seat
    When I make a request to retrieve seating plan and priced inventory for PublicAPI using bundle:<Fare bundle>
    Then I will set the final offer price as zero
    Examples:
      | Channel      | Fare bundle |
      | PublicApiB2B | Flexi       |

  @Sprint25
  Scenario Outline: 8 - Calculate price difference for higher seat band BR_4002
    Given I am using channel <Channel>
    And the seating band is higher than the bundle seat
    When I make a request to retrieve seating plan and priced inventory for PublicAPI using bundle:<Fare bundle>
    Then I will set the final offer price as the price difference between the two bands
    Examples:
      | Channel      | Fare bundle |
      | PublicApiB2B | Flexi       |

  @Sprint25
  Scenario Outline: 9 - Set the final offer price of lower seat bands to zero BR_4003
    Given I am using channel <Channel>
    And the seating band is lower than the bundle seat
    When I make a request to retrieve seating plan and priced inventory for PublicAPI using bundle:<Fare bundle>
    Then I will set the final offer price as zero
    Examples:
      | Channel      | Fare bundle |
      | PublicApiB2B | Flexi       |

  @Sprint25
  Scenario Outline: 10 - Bundle in the basket does not include a seat band
    Given I am using channel <Channel>
    And the basket contains a fare type Bundle that does not includes a seat band product
    When I make a request to retrieve seating plan and priced inventory for PublicAPI using bundle:<Fare bundle>
    Then I will set the final offer price as the price received from the seating service
    Examples:
      | Channel      | Fare bundle |
      | PublicApiB2B | Staff       |

  @local
  Scenario: 12 – Generate error message that no seat map available
    Given that I am using channel:PublicApiB2B
    When the Seating Service is down or no seat map is returned for PublicApi
    Then the seat map and prices service should return the seat map unavailable error:SVC_100400_1001
    And the seat map is not returned
