Feature: Checking totals for getFlights and addFlightToCart

  @regression
  Scenario Outline: Add flight to the basket and check the totals
    Given I am using the channel <channel>
    And I searched a flight for <passengerMix>
    And all the calculation in the getFlights response are right
    When I add it to the basket
    Then all the calculation in the basket are right
    Examples: Test for different channel
      | channel           | passengerMix                     |
      | ADCustomerService | 2,1 adult; 2,1 child; 2,1 infant |
      | Digital           | 2 adult; 2 child; 2,1 infant     |
