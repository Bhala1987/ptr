Feature: Remove Additional Seat Request without a purchased seat

  @Sprint25 @FCPH-8736
  Scenario Outline: Request is in the format defined in the service contract
    Given I am using "1 adult, 1 child" as passenger mix with 2 additional seat for each and "<fare-type>" fare
    And I am removing a seat for passenger via "<channel>"
    But the request miss the mandatory field <field>
    When I send the remove additional fare request
    Then I will verify the error <error> has been returned
    Examples:
      | channel           | field       | fare-type | error           |
      | ADCustomerService | basketId    | Standard  | SVC_100402_2001 |
      | ADAirport         | passengerId | Standard  | SVC_100402_2003 |
      | ADAirport         | basketId    | Flexi     | SVC_100402_2001 |
      | ADCustomerService | passengerId | Flexi     | SVC_100402_2003 |

  @Sprint25 @FCPH-8736
  Scenario Outline: Request De-Allocation for additional seat BR_00961
    Given I am using "1 adult, 1 child" as passenger mix with 2 additional seat for each and "<fare-type>" fare
    And I am removing a seat for passenger via "<channel>"
    When I call the find flight
    Then I will verify the seat has been deallocated properly
    Examples:
      | channel           | fare-type |
      | ADAirport         | Standard  |
      | ADCustomerService | Flexi     |

  @Sprint25 @FCPH-8736
  Scenario Outline: Send Removal Confirmation to channel
    Given I am using "1 adult, 1 child" as passenger mix with 1 additional seat for each and "<fare-type>" fare
    And I am removing a seat for passenger via "<channel>"
    When I send the remove additional fare request
    Then I will verify a successful remove additional fare response has been returned
    And I will verify the additional seat has been removed
    And I will verify the passenger totals has been updated
    And I will verify basket totals has been updated
    Examples:
      | channel           | fare-type |
      | ADAirport         | Standard  |
      | ADCustomerService | Flexi     |

  @Sprint25 @FCPH-8736
  Scenario Outline: Error when passenger does not have an additional seat
    Given I am using "1 adult, 1 child" as passenger mix with 0 additional seat for each and "<fare-type>" fare
    And I am removing a seat for passenger via "<channel>"
    But the passenger does not have any additional seat
    When I send the remove additional fare request
    Then I will verify the error SVC_100402_2004 has been returned
    Examples:
      | channel           | fare-type |
      | ADAirport         | Standard  |
      | ADCustomerService | Flexi     |

  @Sprint25 @FCPH-8736 @defect:FCPH-10074 @FCPH-10750
  Scenario Outline: Error when request quantity does not equal the amount the passenger has in the basket
    Given I am using "1 adult, 1 child" as passenger mix with 2 additional seat for each and "<fare-type>" fare
    And I am removing a seat for passenger via "<channel>"
    But the quantity is greater than the number for the passenger
    When I send the remove additional fare request
    Then I will verify the error SVC_100402_2004 has been returned
    Examples:
      | channel           | fare-type |
      | ADAirport         | Standard  |
      | ADCustomerService | Flexi     |

  @FCPH-7560 @Sprint26 @FCPH-9718 @Sprint28 @Sprint29 @TeamC
  Scenario Outline: Generate an error for missing the mandatory field in Remove Additional Seat service request
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    And I added the seat with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat>
    But the request miss the mandatory field <field>
    When I send the remove additional fare request
    Then I will verify the error <error> has been returned
    Examples:
      | channel           | passengerMix | fareType | seat     | additionalSeat | field       | error           |
      | ADCustomerService | 1,2 adult    | Standard | STANDARD | 2              | basketId    | SVC_100402_2001 |
      | ADAirport         | 1,1 adult    | Standard | STANDARD | 1              | passengerId | SVC_100402_2003 |

  @FCPH-7560 @Sprint26 @FCPH-9718 @Sprint28 @Sprint29 @TeamC
  Scenario Outline: Request De-Allocation for additional seat
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    And I added the seat with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat>
    When I remove 1 additional seat from the basket
    Then I should receive additional seat removal successful response
    Examples:
      | channel   | passengerMix | fareType | seat    | additionalSeat |
      | ADAirport | 1,1 adult    | Standard | UPFRONT | 1              |

  @FCPH-7560 @Sprint26 @FCPH-9718 @Sprint28 @Sprint29 @TeamC
  Scenario Outline: Update basket totals and send removal confirmation to the channel
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    And I added the seat with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat>
    When I remove 1 additional seat from the basket
    Then I will verify a successful remove additional fare response has been returned
    And I will verify the additional seat has been removed
    And I will verify the passenger and basket totals has been updated
    Examples:
      | channel   | passengerMix | fareType | seat    | additionalSeat |
      | ADAirport | 1,1 adult    | Standard | UPFRONT | 1              |

  @FCPH-7560 @Sprint26 @BR:BR_01547 @FCPH-9718 @Sprint28 @Sprint29 @TeamC @regression
  Scenario Outline: Remove passenger where the passenger has an additional seat
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    And I added the seat with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat>
    When I want to remove passenger for "<numFlight>"
    Then the passenger should be removed
    Examples:
      | channel   | passengerMix | fareType | seat     | additionalSeat | numFlight |
      | ADAirport | 1,1 adult    | Standard | STANDARD | 1              | 1 Flight  |

  @FCPH-7560 @Sprint26 @BR:BR_01547 @FCPH-9718 @Sprint28 @Sprint29 @TeamC
  Scenario Outline: Receive a remove purchased seat request where the passenger also has an additional seat
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    And I added the seat with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat>
    When I send a request to remove primary seat for adult passenger
    Then both primary and additional seat should be removed
    Examples:
      | channel   | passengerMix | fareType | seat    | additionalSeat |
      | ADAirport | 1,1 adult    | Standard | UPFRONT | 1              |

  @FCPH-7560 @Sprint26 @manual @FCPH-9718 @Sprint28 @Sprint29 @TeamC
  Scenario Outline: Receive an error from AL
    Given the channel <channel> is used
    And I want to proceed with add purchased seat <seat>
    And I added the seat with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat>
    And I remove an additional seat for passenger
    And I send the remove additional fare request
    When I recieve an unsuccessful deallocate response
    Then I return an error to the channel
    Examples:
      | channel   | passengerMix | fareType | seat    | additionalSeat |
      | ADAirport | 1,1 adult    | Standard | UPFRONT | 1              |
