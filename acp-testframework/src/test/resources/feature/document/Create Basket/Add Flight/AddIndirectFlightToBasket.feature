@FCPH-260
Feature: Add Indirect Flight to basket

  Scenario Outline: Add indirect flights to basket for a channel
    Given "<channel>" has configured to search for indirect flights
    And that indirect flights are configured for "LTN" to "BCN"
    And I request for indirect flights for that route from "<channel>"
    When I add the flight to my basket for indirect routes
    Then all the indirect flights are added to my basket
  @regression
    Examples:
      | channel   |
      | ADAirport |

  Scenario Outline: Request the allocation to all flights
    Given "<channel>" has configured to search for indirect flights
    And that indirect flights are configured for "LTN" to "BCN"
    And I request for indirect flights for that route from "<channel>"
    When I add the flight to my basket for indirect routes
    Then the inventory is allocated to all the flights
    Examples:
      | channel           |
      | ADCustomerService |

  Scenario Outline: Flight base price is the same when added to the basket
    Given "<channel>" has configured to search for indirect flights
    And that indirect flights are configured for "LTN" to "BCN"
    And I request for indirect flights for that route from "<channel>"
    When I add the flight to my basket for indirect routes
    Then the base price and associated taxes are the same in the basket for indirect flight
    Examples:
      | channel   |
      | ADAirport |

  @FCPH-200
  Scenario Outline: Check AddFlight request returns a response for an all outbound flight
    Given "<channel>" has configured to search for indirect flights
    And that indirect flights are configured for "LTN" to "BCN"
    And I request for indirect flights for that route from "<channel>"
    When I add the flight to my basket for indirect routes
    Then the flight is added to the basket via the "<channel>"
    Examples:
      | channel           |
      | ADCustomerService |

  @negative
  Scenario Outline: Incorrect channel can't add flight to the basket
    Given "ADAirport" has configured to search for indirect flights
    And that indirect flights are configured for "LTN" to "BCN"
    And I request for indirect flights for that route from "ADAirport"
    When I try adding the flight to my basket for indirect routes from "<channel>"
    Then error "SVC_100012_20018" should be returned
    Examples:
      | channel      |
      | Digital      |
      | PublicApiB2B |

  @manual
  Scenario Outline: If One or more Flight has a status of cancelled no flight added to basket BR_00082
    Given I have added indirect flights from "<origin>" to "<destination>" to the basket via "<channel>"
    When I check the operational status of both sectors
    And one or more of the sectors has a operational status of cancelled
    Then I will return a error to the channel
    And the journey will not be added to the basket
    Examples:
      | origin | destination | channel           |
      | LTN    | BCN         | ADCustomerService |

  @manual
  Scenario Outline: Verify that if One of the flight in the pair is unavailable no flights added to the basket
    Given I have added indirect flights from "<origin>" to "<destination>" to the basket via "<channel>"
    And I have requested the allocation for the requested sectors
    When I receive a response that one of the flights in the pair is no longer available
    Then generate request to deallocate other flight
    And Error message is returned to the channel
    Examples:
      | origin | destination | channel   |
      | LTN    | BCN         | ADAirport |

  @manual
  Scenario Outline: Sucessful allocation of flights but price has changed
    Given I have added indirect flights from "<origin>" to "<destination>" to the basket via "<channel>"
    And I have requested the allocation for the requested sectors
    When I receive a response that price has changed for one or more sectors
    Then I will continue to add the flights to the basket
    And generate a Price Change message in the expected format
    And return the price change message to the channel
    Examples:
      | origin | destination | channel           |
      | LTN    | BCN         | ADCustomerService |

  @manual
  Scenario Outline: One or more of the flights has exceed number of infants booked on own seats BR_00041, BR_00042
    Given I have added indirect flights from "<origin>" to "<destination>" to the basket via "<channel>"
    When the flight has been successfully added to the basket
    And for one of the flights has exceeded the number of infants booked on own seats
    Then I will return Error message and code returned with an Override flag returned
    Examples:
      | origin | destination | channel   |
      | LTN    | BCN         | ADAirport |


  @manual
  Scenario Outline: Validate STD of first sector of journey with flights already in the basket BR_00080
    Given I have added indirect flights from "<origin>" to "<destination>" to the basket via "<channel>"
    When the flight has been successfully added to the basket
    And the STD of the first sector of the journey is within 24 hours of the arrival time of a flight already in the basket
    And is departing from a different airport
    Then I will return a warning message to the channel
    Examples:
      | origin | destination | channel           |
      | LTN    | BCN         | ADCustomerService |

  @manual
  Scenario Outline: Inbound flight is before the outbound flight BR_00081
    Given I have added indirect flights from "<origin>" to "<destination>" to the basket via "<channel>"
    And the request is for a inbound journey
    And the basket already contains a outbound journey
    When I validate the request
    And the STD of the first sector of the of journey is before the outbound already in the basket
    Then return a error to the channel
    Examples:
      | origin | destination | channel   |
      | LTN    | BCN         | ADAirport |

  @manual
  Scenario Outline: selected return flight within x configurable hours of outbound flight landing x=2
    Given I have added indirect flights from "<origin>" to "<destination>" to the basket via "<channel>"
    And the request is for a inbound journey
    And the basket already contains a outbound journey
    When the flight has been successfully added to the basket
    And the he STD of the first sector of the of journey is x hours of the outbound journey last sector arrival time
    Then I will return a warning message
    Examples:
      | origin | destination | channel           |
      | LTN    | BCN         | ADCustomerService |

  @manual
  Scenario Outline: flight is departing in x hours today BR_00072
    Given I have added indirect flights from "<origin>" to "<destination>" to the basket via "<channel>"
    When the flight has been successfully added to the basket
    And the first sector STD is departing within 2 hours
    Then I will return a warning message
    Examples:
      | origin | destination | channel   |
      | LTN    | BCN         | ADAirport |
