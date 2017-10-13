@TeamD
Feature: Record the number of infants against the flight

  @Sprint30 @FCPH-10360
  Scenario Outline: Generate error message if the number of infants on flight has been reached - AD
    Given one of this channel ADAirport, ADCustomerService is used
    And I searched a flights for <passengerMix>
    But the <infantType> limit is consumed
    When I add the flight to the basket
    Then the channel will receive an error with code <error>
    When I send the request to remove flight
    Examples:
      | passengerMix        | infantType    | error           |
      | 1 adult; 1,0 infant | infants       | SVC_100012_3007 |
      | 1 adult; 1 infant   | infantsOnSeat | SVC_100012_3006 |

  @Sprint30 @FCPH-10360
  Scenario Outline: Generate error message if the number of infants on flight has been reached - Digital (AddFlight)
    Given one of this channel Digital, PublicApiMobile is used
    And I searched a flights for <passengerMix>
    But the <infantType> limit is consumed
    When I add the flight to the basket
    Then the channel will receive an error with code <error>
    Examples:
      | passengerMix        | infantType    | error           |
      | 1 adult; 1,0 infant | infants       | SVC_100012_3007 |
      | 1 adult; 1 infant   | infantsOnSeat | SVC_100012_3006 |

  @Sprint30 @FCPH-10360
  Scenario Outline: Generate error message if the number of infants on flight has been reached - Digital (CommitBooking)
    Given one of this channel Digital, PublicApiMobile is used
    And I created a customer
    And I added a flight to the basket for <passengerMix>
    And I updated the passenger information
    And I have a valid payment method
    But the <infantType> limit is consumed
    When I send the request to commitBooking service with override false
    Then the channel will receive an error with code <error>
    Examples:
      | passengerMix        | infantType    | error           |
      | 1 adult; 1,0 infant | infants       | SVC_100012_3007 |
      | 1 adult; 1 infant   | infantsOnSeat | SVC_100012_3006 |

  @Sprint31 @FCPH-11248
  Scenario Outline: Generate error message if the number of infants on flight has been reached - PublicApiB2B
    Given the channel PublicApiB2B is used
    And I searched a flights for <passengerMix>
    But the <infantType> limit is consumed
    When I send the request to commitBooking service with override false
    Then the channel will receive an error with code <error>
    Examples:
      | passengerMix        | infantType    | error           |
      | 1 adult; 1,0 infant | infants       | SVC_100012_3007 |
      | 1 adult; 1 infant   | infantsOnSeat | SVC_100012_3006 |

  @Sprint30 @FCPH-10360
  Scenario Outline: Record the number of infants consumed on the flight - ADAirport
    Given one of this channel ADAirport, ADCustomerService is used
    And I searched a flights for <passengerMix>
    And infants limits and consumed values are stored for the flight
    When I add the flight to the basket
    Then the number of <infantType> for the flight will be reserved
    Examples:
      | passengerMix        | infantType    |
      | 1 adult; 1,0 infant | infants       |
      | 1 adult; 1 infant   | infantsOnSeat |

  @Sprint30 @FCPH-10360
  Scenario Outline: Number of Infant/Infant own seat exceeds limit Ignore error Message
    Given one of this channel ADAirport, ADCustomerService is used
    And I searched a flights for <passengerMix>
    When I added a flight to the basket with more <infantType> than allowed with override true
    Then the number of <infantType> for the flight will be reserved
    Examples:
      | passengerMix        | infantType    |
      | 1 adult; 1,0 infant | infants       |
      | 1 adult; 1 infant   | infantsOnSeat |

  @Sprint30 @FCPH-10360
  Scenario Outline: Update the number of infants on seat consumed when flight is removed
    Given one of this channel ADAirport, ADCustomerService is used
    And I added a flight to the basket for <passengerMix>
    And infants limits and consumed values are stored for the flight
    And I want to remove a flight from the basket
    When I send the request to removeFlights service
    Then the number of <infantType> for the flight will be released
    And the flight will be removed from the basket
    Examples:
      | passengerMix        | infantType    |
      | 1 adult; 1,0 infant | infants       |
      | 1 adult; 1 infant   | infantsOnSeat |

  @Sprint30 @FCPH-10360
  Scenario Outline: Record the number of infants consumed on the flight - Digital
    Given one of this channel Digital, PublicApiMobile is used
    When I have committed a booking for <passengerMix>
    Then the number of <infantType> for the flight will be reserved
    Examples:
      | passengerMix        | infantType    |
      | 1 adult; 1,0 infant | infants       |
      | 1 adult; 1 infant   | infantsOnSeat |

  @Sprint31 @FCPH-11248
  Scenario Outline: Record the number of infants consumed on the flight - PublicApiB2B
    Given the channel PublicApiB2B is used
    When I have committed a booking for <passengerMix>
    Then the number of <infantType> for the flight will be reserved
    Examples:
      | passengerMix        | infantType    |
      | 1 adult; 1,0 infant | infants       |
      | 1 adult; 1 infant   | infantsOnSeat |
