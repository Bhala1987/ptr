@FCPH-472
Feature: Price Rounding for Digital channel

  Scenario Outline: FindFlights request has currency of 2 decimal places
    Given the currency in the request has "<decPosition>" decimal places
    When I sent findFlight request from <channel> with <passengeMix> for for <journeyType>
    Then credit card fee per line item to the nearest <decPosition> decimal position should be rounded up
    And admin fee per passenger to the nearest <decPosition> decimal position should be rounded up
    And total should be rounded up <decPosition> decimal position
    Examples:
      | channel | passengeMix                    | decPosition | journeyType |
      | Digital | 4 Adults, 3 Child, 1 Infant OL | 2           | SINGLE      |
      | Digital | 5 Adults, 2 Child              | 0           | OUT/IN      |

  Scenario Outline: Add Flight to basket request from digital 2 decimal places or whole number
    Given the currency in the request has "<decPosition>" decimal places
    When I sent findFlight request from <channel> with <passengerMix> for <journeyType>
    And I add flight to the basket for <journeyType> journey
    Then credit card fee for basket rounded up to <decPosition> decimal position
    And admin fee per passenger for basket rounded up <decPosition> decimal position
    And total for basket should be rounded up <decPosition> decimal position
    Examples:
      | channel | passengerMix                   | decPosition | journeyType |
      | Digital | 2 Adults, 1 Child, 1 Infant OL | 5           | SINGLE      |
      | Digital | 3 Adults, 2 Child              | 5           | OUT/IN      |
