Feature: Remove Purchased Seat request

  @Sprint25 @FCPH-3996
  Scenario Outline: Validate Remove Purchased Seat request
    And I want to proceed with add purchased seat EXTRA_LEGROOM
    And my basket contains flight with passengerMix "1 Adult" added via "<channel>"
    And the channel "<channel>" has initiated a request to remove a purchased seat
    But the request miss the the mandatory "<field>" defined in the service contract
    When I validate the RemoveSeatProduct request
    Then I will return an error message "<error>" to the channel
  @ADTeam
    Examples:
      | channel           | field       | error           |
      | ADCustomerService | basketId    | SVC_100013_1001 |
      | ADAirport         | flightKey   | SVC_100299_1002 |
      | ADCustomerService | passengerId | SVC_100299_1003 |
      | ADCustomerService | seats       | SVC_100299_2002 |
    Examples:
      | channel         | field       | error           |
      | Digital         | basketId    | SVC_100013_1001 |
      | PublicApiMobile | basketId    | SVC_100013_1001 |
      | PublicApiMobile | flightKey   | SVC_100299_1002 |
      | Digital         | flightKey   | SVC_100299_1002 |
      | Digital         | passengerId | SVC_100299_1003 |
      | Digital         | seats       | SVC_100299_2002 |

  @Sprint25 @FCPH-3996
  Scenario Outline: Request to remove all purchased seats for the flight in the basket if a Digital request BR_00511
    Given I am using the channel Digital
    And I want to proceed with add purchased seat <seat>
    And my basket contains flight with passengerMix "<mix>" added via "Digital"
    And the channel "Digital" has initiated a request to remove a purchased seat on passenger type "<type>"
    When I validate the RemoveSeatProduct request
    Then I will remove the purchased seat from the passenger in the basket
    And I will remove purchased seats for all other passengers on the flight in the basket
    Examples:
      | mix              | type  | seat          |
      | 2 Adult, 3 Child | child | EXTRA_LEGROOM |
      | 2 Adult, 2 Child | adult | UPFRONT       |

  @Sprint25 @FCPH-3996 @regression
  Scenario Outline: Send Removal Confirmation to channel
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat EXTRA_LEGROOM
    And my basket contains flight with passengerMix "1 Adult" added via "<channel>"
    And the channel "<channel>" has initiated a request to remove a purchased seat
    When I validate the RemoveSeatProduct request
    Then I will return a successful remove purchased seat response
  @ADTeam
    Examples:
      | channel   |
      | ADAirport |
    Examples:
      | channel |
      | Digital |

  @Sprint27 @FCPH-8639 @TeamC @Sprint29 @FCPH-10049
  Scenario Outline: Deallocate the seat number when a seat is part of a Fare type bundle
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I added the seat and removed it with <passengerMix> and <fareType> and <seat>
    Then I will remove purchased seats for all other passengers on the flight in the basket
    Examples:
      | channel | passengerMix | fareType | seat          |
      | Digital | 1 Adult      | Standard | STANDARD      |
      | Digital | 1 Adult      | Flexi    | EXTRA_LEGROOM |

  @Sprint27 @FCPH-8639 @TeamC @Sprint29 @FCPH-10049
  Scenario Outline: Update the seat price when a upgrade has been purchased
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I added the seat and removed it with <passengerMix> and <fareType> and <seat>
    Then I will recalculate basket totals with <fareType>
    @ADTeam
    Examples:
      | channel   | passengerMix | fareType | seat          |
      | ADAirport | 1 Adult      | Flexi    | EXTRA_LEGROOM |
    Examples:
      | channel | passengerMix | fareType | seat     |
      | Digital | 1 Adult      | Standard | STANDARD |

  @Sprint27 @FCPH-8639 @TeamC @Sprint29 @FCPH-10049
  Scenario Outline: Remove any associated products to the basket if purchased seat is part of a product bundle
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I added the seat and removed it with <passengerMix> and <fareType> and <seat>
    Then I check the cabin bag
    @ADTeam
    Examples:
      | channel   | passengerMix | fareType | seat          |
      | ADAirport | 1 Adult      | Flexi    | EXTRA_LEGROOM |
    Examples:
      | channel   | passengerMix | fareType | seat          |
      | Digital   | 1 Adult      | Standard | STANDARD      |
