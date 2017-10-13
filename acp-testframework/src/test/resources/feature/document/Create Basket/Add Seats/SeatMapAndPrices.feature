@FCPH-3998
Feature:Return data from Seating Service to the Channel

  @negative
  Scenario Outline: 1 - Generate a error if flight key is not known
    Given I have a basket with a valid flight with 1 adult added via <Channel>
    When I make a request to retrieve the seat map with an invalid flight key
    Then the seat map and prices service should return the invalid flight key error: <Error code>
    Examples:
      | Channel   | Error code      |
      | Digital   | SVC_100400_2002 |
      | ADAirport | SVC_100400_2002 |

  @negative
  Scenario Outline: 3 - Generate error message if passenger limit is over for the channel BR_00470
    Given the max seat passenger limit is configured for channel: <Channel>
    And my basket contains flight with 1 more passenger than the config added via "<Channel>"
    When I make a request to retrieve seating plan and priced inventory
    Then the seat map and prices service should return the passenger limit error:<Error>
    And will not return the seat map
    Examples:
      | Channel | Error           |
      | Digital | SVC_100400_2104 |

  @negative
  Scenario Outline: 4 - Generate error if a standby bundle in the basket BR_00471
    Given I am using the channel <Channel>
    And a valid customer profile has been created
    And a valid request to associate staff member to member account
    And I have a flight in my basket with "Standby" fare via channel:<Channel>
    When I make a request to retrieve seating plan and priced inventory
    Then the seat map service should return error the "SVC_100400_2003" error message stating unable to return seat map for Standby bundles
    And will not return the seat map
    Examples:
      | Channel   |
      | Digital   |
      | ADAirport |

  Scenario Outline: 5 – Generate Get Seat Map Request to Seating Service
    Given my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I make a request to retrieve seating plan and priced inventory
    Then I should receive the seat map
    And the seat map Flight Key should be correct
    And the seat map Currency should be correct
  @regression
    Examples:
      | Channel   |
      | ADAirport |
    Examples:
      | Channel |
      | Digital |

  Scenario Outline: 7 – Bundle in the basket includes a seat band BR_4001
    Given my basket contains a flight with "<Fare bundle>" fare added via the "<Channel>" channel
    When I make a request to retrieve seating plan and priced inventory
    And the seating band is the same level as the bundle seat
    Then I will set the final offer price as zero
    Examples:
      | Channel   | Fare bundle |
      | Digital   | Flexi       |
      | ADAirport | Flexi       |

  @BR:BR_4002
  Scenario Outline: 8 - Calculate price difference for higher seat band
    Given my basket contains a flight with "<Fare bundle>" fare added via the "<Channel>" channel
    And the seating band is higher than the bundle seat
    When I make a request to retrieve seating plan and priced inventory
    Then I will set the final offer price as the price difference between the two bands
  @regression
    Examples:
      | Channel   | Fare bundle |
      | ADAirport | Flexi       |
    Examples:
      | Channel | Fare bundle |
      | Digital | Flexi       |

  Scenario Outline: 9 - Set the final offer price of lower seat bands to zero BR_4003
    Given I am using channel <Channel>
    And a valid customer profile has been created
    And a valid request to associate staff member to member account
    And my basket contains "1 Adult" with fare type "StaffStandard" using channel "<Channel>"
    When I make a request to retrieve seating plan and priced inventory
    And the seating band is lower than the bundle seat
    Then I will set the final offer price as zero
    Examples:
      | Channel |
      | Digital |

  Scenario Outline: 10 - Bundle in the basket does not include a seat band
    Given my basket contains a flight with "Standard" fare added via the "<Channel>" channel
    When I make a request to retrieve seating plan and priced inventory
    And the basket contains a fare type Bundle that does not includes a seat band product
    Then I will set the final offer price as the price received from the seating service
    Examples:
      | Channel   |
      | Digital   |
      | ADAirport |

  @local
  Scenario: 12 – Generate error message that no seat map available
    Given that I am using channel:Digital
    And my basket contains flight with passengerMix "1 Adult" added via "Digital"
    When the Seating Service is down or no seat map is returned
    Then the seat map and prices service should return the seat map unavailable error:SVC_100400_1001
    And the seat map is not returned