@TeamD
Feature: Decrement Infant on Lap and Infant on Seat Consumed when a basket is cleared

  @Sprint32 @FCPH-11496
  Scenario Outline: 1 - Update the number of infants consumed on the flight when a basket is emptied
    Given the channel <channel> is used
    And I added a flight to the basket for <passengerMix>
    And infants limits and consumed values are stored for the flight
    When I send the request to emptyBasket service
    Then the number of <infantType> for the flight will be released

    Examples:
      | channel           | passengerMix        | infantType    |
      | ADAirport         | 1 adult; 1,0 infant | infants       |
      | ADCustomerService | 1 adult; 1 infant   | infantsOnSeat |

  @Sprint32 @FCPH-11496 @manual
  Scenario Outline: 2 -  Update the number of infants consumed on the flight when a session expires
    Given the channel <channel> is used
    And I added a flight to the basket for <passengerMix>
    And infants limits and consumed values are stored for the flight
    When the session is expired
    Then the number of <infantType> for the flight will be released

    Examples:
      | channel           | passengerMix        | infantType    |
      | ADAirport         | 1 adult; 1,0 infant | infants       |
      | ADCustomerService | 1 adult; 1 infant   | infantsOnSeat |
