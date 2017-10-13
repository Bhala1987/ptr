@Sprint25
@FCPH-8244
Feature: Remove Flight from basket

  Scenario Outline: Remove a Flight with Passenger Mix
    Given I have found a flight and added to basket for <passengerMix> and <channel>
    When I receive a remove Flight request
    Then the flight should be removed from the basket
  @regression
    Examples:
      | channel | passengerMix                                |
      | Digital | 4 adult, 2 child, 3 infant OOS, 1 infant OL |
    Examples:
      | channel   | passengerMix     |
      | ADAirport | 1 adult, 1 child |

  Scenario Outline: Remove a Flight for invalid basketID or flightKey
    Given I have found a flight and added to basket for <passengerMix> and <channel>
    When I receive a remove Flight request with invalid <params>
    Then I will return an error <errorCode> for removeFlight
    Examples:
      | channel           | params    | errorCode       | passengerMix |
      | Digital           | basketID  | SVC_100015_1001 | 1 adult      |
      | ADAirport         | flightKey | SVC_100015_1002 | 1 adult      |
      | Digital           | flightKey | SVC_100015_1002 | 1 adult      |
      | ADAirport         | basketID  | SVC_100015_1001 | 1 adult      |
      | ADCustomerService | basketID  | SVC_100015_1001 | 1 adult      |
      | PublicApiMobile   | basketID  | SVC_100015_1001 | 1 adult      |
      | ADCustomerService | flightKey | SVC_100015_1002 | 1 adult      |
      | PublicApiMobile   | flightKey | SVC_100015_1002 | 1 adult      |

  Scenario Outline: Generate a deallocate inventory request BR_00961
    Given I have found a flight and added to basket for <passengerMix> and <channel>
    And I receive a remove Flight request
    And the flight should be removed from the basket
#    When I find a same flight for inventory
#    Then the inventory is de-allocated for the same flight
    Examples:
      | channel           | passengerMix |
      | ADAirport         | 1 adult      |

  Scenario Outline: Flight being removed is part of a return pair and no change on the admin fee if the channel is AD
      Given I am using the channel <channel>
    And I have found a return flight and added to basket for <passengerMix> and <channel>
    When I receive a remove <Flight> flight request from basket
    Then the <Flight> flight should be removed from the basket
    And the admin fee remains same in the basket level for AD Channel
    Examples:
      | channel           | passengerMix | Flight   |
      | ADAirport         | 1 adult      | Outbound |

  Scenario Outline: Flight being removed has full admin fee and the next flight is not part of a pair  BR_01266*
    Given am using channel <channel>
    And I have found <Flights> flights & add to my basket for <passengerMix>
    And all the flights are added to basket
    When I receive a remove flight request from basket that has full admin fee
    Then the flight is removed from the basket
    And I will apportion the admin fee across the passengers excluding Infants on next flight
    Examples:
      | channel | passengerMix | Flights |
      | Digital | 1 adult      | 2       |

  Scenario Outline: Flight being removed has full admin fee and the next flight is part of a pair BR_01266*
      Given I am using the channel <channel>
    And I have found a flight and added to basket for <passengerMix> and <channel>
    And I have found a return flight and added to basket for <passengerMix> and <channel>
    When I receive a remove flight request from basket that has full admin fee
    Then the flight is removed from the basket
    And I will apportion the admin fee across the passengers excluding Infants on next flight for non-AD channels
    Examples:
      | channel | passengerMix |
      | Digital | 1 adult      |

  Scenario Outline: All flights removed from the basket for AD Channels BR_01263
    Given I have found a flight and added to basket for <passengerMix> and <channel>
    And I receive a remove Flight request
    And the flight should be removed from the basket
#    When I find a same flight for inventory
#    Then the inventory is de-allocated for the same flight
#    And I will remove all fees and taxes from the basket
    Examples:
      | channel           | passengerMix |
      | ADAirport         | 1 adult      |

  Scenario Outline: All flights removed from the basket for non-AD Channels. See BR_01263.*
    Given I have found a flight and added to basket for <passengerMix> and <channel>
    When I receive a remove Flight request
    Then the flight should be removed from the basket
    And I will remove all fees and taxes from the basket
    Examples:
      | channel         | passengerMix |
      | PublicApiMobile | 1 adult      |

  Scenario Outline: Update the basket and flight total prices
    Given I have found a flight and added to basket for <passengerMix> and <channel>
    And I have received a valid addHoldBagProduct request for the channel "<channel>"
    And I validate the addHoldBagProduct request
    And I have received a valid addSportEquipment request for the channel "<channel>"
    And I valid the request to addSportsEquipment
    And the flight is added to basket along with the hold items for the channel <channel>
    When I receive a remove Flight request
    Then the flight should be removed from the basket
    And I will remove the cabin bags from the basket for the flight
    And I will remove any hold items from the basket for the flight
    And I will remove any sports equipment from the basket for the flight
    Examples:
      | channel   | passengerMix |
      | Digital   | 1 adult      |
      | ADAirport | 1 adult      |

  Scenario Outline: Flight being removed has no admin fee BR_01261*
    Given am using channel <channel>
    And I have found & add a valid flight with "Flexi" bundle for <passengerMix>
    And verify the flight has no admin fee
    When I receive a remove Flight request
    Then the flight should be removed from the basket
    And I will remove all fees and taxes from the basket
    Examples:
      | channel   | passengerMix |
      | Digital   | 1 adult      |
      | ADAirport | 1 adult      |