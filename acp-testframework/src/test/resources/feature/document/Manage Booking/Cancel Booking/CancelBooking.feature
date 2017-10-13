Feature: Cancel Booking

  @TeamC
  @Sprint30
  @FCPH-10690
  Scenario Outline: Cancel booking for flight inventory
    Given I am using <channel> channel
    And I have made a booking with passenger <passengerMix> and fare <fareType> with additional seat <addlSeat>
    When I request to cancel my full booking
    Then the booking status should be CANCELLED
    Examples:
      | channel   | passengerMix                                | fareType | addlSeat |
      | ADAirport | 1,1 adult;                                  | Flexi    | 1        |
      | Digital   | 1 adult, 1 child, 1 infant OOS, 1 infant OL | Standard | 0        |

  @TeamC
  @Sprint30
  @FCPH-10761
  Scenario Outline: Cancel booking for seat inventory
    Given I am using <channel> channel
    And I have made a booking with passenger <passengerMix> and fare <fareType> with purchased seat for <numFlights> flights
    When I request to cancel my full booking
    Then the booking status should be CANCELLED
    Examples:
      | channel   | passengerMix     | fareType | numFlights |
      | Digital   | 1 adult          | Standard | 3          |
      | ADAirport | 1 adult, 1 child | Flexi    | 2          |

  @Sprint31 @TeamC @FCPH-10762
  Scenario Outline: Cancel booking for hold bag and sport equipment inventory
    Given I am using the channel <channel>
    And add flight to the basket with passenger "<passengers>" with "<fareType>"
    And I add "1" "Hold Bag" for all passengers on flight
    And I add "1" "Large Sporting Equipment" for all passengers on flight
    And I add "1" "Small Sporting Equipment" for all passengers on flight
    When commit the booking with hold items
    And I request for cancel booking
    Then I see increment in stock level for the flight for the number of requested hold items, sports equipment
    Examples:
      | channel   | passengers | fareType |
      | Digital   | 1 adult    | Standard |
      | ADAirport | 1 adult    | Flexi    |
