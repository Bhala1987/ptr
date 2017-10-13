Feature: Update a booking add sports equipment to multiple passenger on multiple flights

  @Sprint32 @TeamC @FCPH-11118 @manual
  Scenario Outline: Create a new version of the booking after add sport equipment
    Given I am using ADAirport channel
    When I commit booking request for amendable basket containing <Product Line Items> after add sport equipment
    Then a new version of the booking should be created
    And the previous version status should be setted to 'Amended'
    And the new version of the booking should be linked to the previous one
    And i will copy the <Product Line Items> statues from the amendable basket to the booking
    Examples:
      | Product Line Items |
      | sports equipment   |

  @Sprint32 @TeamC @FCPH-11118 @manual
  Scenario: Add Booking History entry
    Given I am using ADCustomerService channel
    When I commit booking request for amendable basket after add sport equipment
    Then a new version of the booking should be created
    And Date and Time should be setted
    And Booking History Channel should be setted as Agent Desktop
    And Booking History User Id should be setted as Agent ID
    And Booking History Event Type should be setted to <Action taken>
    And Booking History Description should be setted to Flight Key, Passenger Last Name, Passenger First Name for each flight
    And Booking History Version should be setted as Booking Version

  @Sprint32 @TeamC @FCPH-11118
  Scenario: Release the amendment lock on the booking after add sport equipment
    Given I am using PublicApiMobile channel
    When I commit an amendable basket after add add sport equipment Snowboard to the passenger
    Then the amendment lock on the booking should be released

  @Sprint32 @TeamC @FCPH-11118
  Scenario Outline: Return confirmaiton to the channel after add sport equipment
    Given I am using <channel> channel
    When I commit an amendable basket containing <numberFlight> flight <fare> and <passenger> after add sport equipment <Eq> to <addSportOnPassenger> passenger on <passengerOnFlight> flight
    Then I want validate successful response after commit booking
    Examples:
      | channel         | numberFlight | fare     | passenger | Eq              | addSportOnPassenger | passengerOnFlight |
      | Digital         | 3            | Standard | 2 adult   | GolfBag         | first               | first             |
      | Digital         | 3            | Flexi    | 2 adult   | Snowboard       | first               | all               |
      | PublicApiMobile | 3            | Standard | 2 adult   | Skis            | second              | first             |
      | PublicApiMobile | 3            | Standard | 2 adult   | SportingFirearm | all                 | all               |

  @Sprint32 @TeamC @FCPH-11118
  Scenario Outline: Return the status of the boarding pass to the channel
    Given I am using Digital channel
    And I have a basket with 1 adult on 2 flight
    And the first passenger on the first flight has purchased seat and already requested boarding pass
    When I commit the amendable basket after add sport equipment GolfBag to first passenger on first flight
    Then I expect status of booking <BoardingPassStatus> for the passenger on <numOfFlight> flight
    Then I expect status of booking <BoardingPassStatus> for the passenger on <numOfFlight> flight
    Examples:
      | BoardingPassStatus | numOfFlight |
      | NEED_TO_RERETRIEVE | first       |
      | NEVER_RETRIEVED    | second      |
