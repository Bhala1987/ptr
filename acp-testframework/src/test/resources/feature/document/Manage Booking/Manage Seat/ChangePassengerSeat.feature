@Sprint27
@FCPH-9348
Feature: Change Purchased Seat

  Scenario Outline: Able to change the seats
    Given I am using <channel> channel
    And I want to proceed with add purchased seat <seat-from>
    When I sent a request to change seat from <seat-from> to <seat-to> for 1 Adult on <fare> fare flight with price <type-price>
    Then I want the new purchased seat into the basket with the new price
    And I dont want the old purchased seat into the basket
    And I want an updated basket totals
    Examples: New purchased seat price is the same
      | channel   | seat-from | seat-to  | fare     | type-price |
      | ADAirport | STANDARD  | STANDARD | Standard | same       |
    Examples: New purchased seat price is the higher
      | channel   | seat-from | seat-to | fare  | type-price |
      | ADAirport | STANDARD  | UPFRONT | Flexi | higher     |
    Examples: New purchased seat price is the lower
      | channel           | seat-from | seat-to  | fare     | type-price |
      | ADCustomerService | UPFRONT   | STANDARD | Standard | lower      |


  @Sprint28 @FCPH-9511
  Scenario Outline: Able to change committed booking change with additional seats
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat-from>
    And I commit booking with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat-from>
    When I change booking with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat-to>
    Then I see new purchased seat and additional seat added
    And I see previous purchased seat and additional seat removed
    Examples:
      | channel   | passengerMix | fareType | seat-from | additionalSeat | seat-to |
      | ADAirport | 1,1 adult    | Standard | UPFRONT   | 1              | UPFRONT |


  @Sprint28 @FCPH-9511
  Scenario Outline: Change seat request with an additional seat with empty seats
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat-from>
    And I commit booking with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat-from>
    When I try to change booking with out providing seating information
    Then I see error for missing seat and additional seat numbers SVC_100244_1005,SVC_100244_1004
    Examples:
      | channel   | passengerMix | fareType | seat-from | additionalSeat |
      | ADAirport | 1,1 adult    | Standard | STANDARD  | 1              |


  @Sprint28 @FCPH-9511
  Scenario Outline: Change seat request with already allocated seat
    Given I am using the channel <channel>
    And I want to proceed with add already allocated purchased seat <seat-from>
    And I commit booking with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat-from>
    When I try to change booking with already allocated seat
    Then I see error for missing seat and additional seat numbers SVC_100244_1005,SVC_100244_1004
    Examples:
      | channel   | passengerMix | fareType | seat-from | additionalSeat |
      | ADAirport | 1,1 adult    | Standard | UPFRONT   | 1              |
