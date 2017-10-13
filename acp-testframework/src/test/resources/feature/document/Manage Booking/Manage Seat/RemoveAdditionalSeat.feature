Feature: Remove Additional Seats

  @Sprint32 @TeamC @FCPH-9920
  Scenario Outline: Add cancellation fee when less than 24 hrs after booking
    Given I am using <channel> channel
    When I have amendable basket for <fareType> fare and <passenger> passenger with additional seat <additionalSeat> and <purchasedSeat> purchasedSeats
    And I remove <additionalSeat> additional seat
    Then I will receive the cancellation fees <cancellationFees>
    And I get <additionalSeat> additional seats deactivated
    And I get <purchasedSeat> purchased seats deactivated for additional seats <additionalSeat>
    And I recalculate the basket total with cancellation fees <cancellationFees> and purchased Seats <purchasedSeat>
    And I should receive successful response
    Examples:
      | channel   | passenger  | fareType | purchasedSeat | additionalSeat | cancellationFees |
      | ADAirport | 2,2 adult; | Flexi    | false         | 2              | 14.0             |
      | ADAirport | 2,1 adult; | Standard | true          | 1              | 14.0             |
      | ADAirport | 3,1 adult; | Standard | false         | 1              | 14.0             |

  @Sprint32 @TeamC @FCPH-9920 @manual
  Scenario Outline: Add cancellation fee when more than 24 hrs after booking
    Given I am using <channel> channel
    When I have amendable basket for <fareType> fare and <passenger> passenger with additional seat <additionalSeat> and <purchasedSeat> purchasedSeats
    And I remove <additionalSeat> additional seat
    Then I will receive the cancellation fees <cancellationFees>
    And I get <additionalSeat> additional seats deactivated
    And I get <purchasedSeat> purchased seats deactivated for additional seats <additionalSeat>
    And I recalculate the basket total with cancellation fees <cancellationFees> and purchased Seats <purchasedSeat>
    And I should receive successful response
    Examples:
      | channel   | passenger  | fareType | purchasedSeat | additionalSeat | cancellationFees |
      | ADAirport | 2,2 adult; | Standard | false         | 2              | fareamount       |
      | ADAirport | 2,1 adult; | Standard | true          | 1              | fareamount       |
      | ADAirport | 3,1 adult; | Standard | false         | 1              | fareamount       |

  @Sprint32 @TeamC @FCPH-11120 @manual
  Scenario Outline: Create a new version of the booking
    Given I am using ADAirport channel
    When I commit booking request for amendable basket containing <Product Line Items> after delete additional fare
    Then a new version of the booking should be created
    And the previous version status should be setted to 'Amended'
    And the new version of the booking should be linked to the previous one
    And i will copy the <Product Line Items> statues from the amendable basket to the booking
    Examples:
      | Product Line Items |
      | Flight             |
      | Seat               |

  @Sprint32 @TeamC @FCPH-11120 @manual
  Scenario: Add Booking History entry
    Given I am using ADCustomerService channel
    When I commit booking request for amendable basket after delete additional fare
    Then a new version of the booking should be created
    And Date and Time should be setted
    And Booking History Channel should be setted as Agent Desktop
    And Booking History User Id should be setted as Agent ID
    And Booking History Event Type should be setted to <Action taken>
    And Booking History Description should be setted to Flight Key, Passenger Last Name, Passenger First Name for each flight
    And Booking History Version should be setted as Booking Version

  @Sprint32 @TeamC @FCPH-11120
  Scenario Outline: Release the amendment lock on the booking after remove additional fare
    Given I am using ADAirport channel
    When I commit an amendable basket containing <numberFlight> flight and <passenger> for each after delete additional fare on <deleteFareOnFlight> flight for <deleteFareOnPassenger> passenger
    Then the amendment lock on the booking should be released
    Examples:
      | numberFlight | passenger | deleteFareOnFlight | deleteFareOnPassenger |
      | 3            | 2 adult   | first              | first/second          |
      | 3            | 2 adult   | second             | second                |

  @Sprint32 @TeamC @FCPH-11120
  Scenario: Return confirmation to the channel after remove additional fare
    Given I am using ADCustomerService channel
    When I commit an amendable basket containing 1,2 adult; after delete additional fare
    Then I want validate successful response after commit booking
    And I want to check status for additional seat in the booking is INACTIVE
    And I want to check amend entry status for additional seat in the booking is CHANGED
