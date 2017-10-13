Feature: Provide Changes and Fees

  # We cannot wait 24h in automation
  @manual
  @TeamD
  @Sprint29
  @FCPH-9779
  Scenario: Add Change Fee for changing a flight less than or equal to x days prior to departure
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And today is x days less than or equal to the departure date of flight being removed
    And I created an amendable basket with Standard fare for 1 adult
    And I want to change a flight with another one
    When I send the changeFlight request
    Then the Change Flight Fee for Standard fare for less than x days of departure per passenger per seat will be added

  # We cannot wait 24h in automation
  @manual
  @TeamD
  @Sprint29
  @FCPH-9779
  Scenario: Add Change Fee for changing a flight greater than to x days prior to departure
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And today is x days more than the departure date of flight being removed
    And I created an amendable basket with Standard fare for 1 adult
    And I want to change a flight with another one
    When I send the changeFlight request
    Then the Change Flight Fee for Standard fare for more than x days of departure per passenger per seat will be added

  @TeamD
  @Sprint29
  @FCPH-9779
  @BR:BR_01915 @regression
  Scenario: Waive Change Fee for changing a flight within x hours of booking
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket with Standard fare for 1 adult
    And I want to change a flight with another one
    When I send the changeFlight request
    Then no change flight fee for Standard fare will be added

  # there's no availability for more than x days ahead from today, hence hardcoding +1 day for the after threshold
  @TeamD
  @Sprint29
  @FCPH-10076
  @BR:BR_01918
  Scenario: Waive Change Fee for changing the route of a Flexi bundle and the new departure date within x days before the original departure date and y days after the original departure date
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket with Flexi fare for 1 adult departing in 5 days
    And I want to search a flight to change an existing one with different sector
    And the date is within before threshold for flight change based on departure
    And I change the flight
    And no change flight fee for Flexi fare will be added
    And I want to search a flight to change an existing one with different sector departing in 6 days
    When I change the flight
    Then no change flight fee for Flexi fare will be added

  # there's no availability for all the days for all the sectors, hence hardcoding LTN-ALC
  # there's no availability for more than x days ahead from today, hence hardcoding +1 day for the after threshold
  @TeamD
  @Sprint29
  @FCPH-10076
  @BR:BR_01916
  Scenario: Waive Change Fee for changing the date and time of a Flexi bundle to a date within x days before the departure date and y days after departure date of the original flight
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket from LTN to ALC with Flexi fare departing in 5 days
    And I want to search a flight to change an existing one with same sector
    And the date is within before threshold for flight change based on departure
    And I change the flight
    And no change flight fee for Flexi fare will be added
    And I want to search a flight to change an existing one with same sector departing in 6 days
    When I change the flight
    Then no change flight fee for Flexi fare will be added

  # there's no availability for all the days for all the sectors, hence hardcoding LTN-ALC
  # there's no availability for more than x days ahead from today, hence hardcoding x = 7 and z = 60
  @TeamD
  @Sprint29
  @FCPH-10076
  Scenario: Add Change Fee when new departure date is more than x days before the departure date and more than z days from change date
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket from LTN to ALC with Flexi fare departing in 69 days
    And I want to change a flight with another one with same sector departing in 61 days
    When I send the changeFlight request
    Then the change flight fee FlightFlexiFee>60 will be added

  # there's no availability for all the days for all the sectors, hence hardcoding LTN-ALC
  # there's no availability for more than x days ahead from today, hence hardcoding x = 7 and z = 60
  @TeamD
  @Sprint29
  @FCPH-10076
  Scenario: Add Change Fee when new departure date is more than x days before the departure date and less than z days from change date
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket from LTN to ALC with Flexi fare departing in 9 days
    And I want to change a flight with another one with same sector departing in 1 day
    When I send the changeFlight request
    Then the change flight fee FlightFlexiFee<59 will be added

  # there's no availability for all the days for all the sectors, hence hardcoding LTN-ALC
  # there's no availability for more than x days ahead from today, hence hardcoding y = 21 and z = 60
  @TeamD
  @Sprint29
  @FCPH-10076
  Scenario: Add Change Fee when new departure date is more than y days after the departure date and more than z days from change date
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket from LTN to ALC with Flexi fare departing in 1 day
    And I want to change a flight with another one with same sector departing in 61 days
    When I send the changeFlight request
    Then the change flight fee FlightFlexiFee>60 will be added

  # there's no availability for all the days for all the sectors, hence hardcoding LTN-ALC
  # there's no availability for more than x days ahead from today, hence hardcoding y = 21 and z = 60
  @TeamD
  @Sprint29
  @FCPH-10076
  Scenario: Add Change Fee when new departure date is more than y days after the departure date and less than z days from change date
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket from LTN to ALC with Flexi fare departing in 1 day
    And I want to change a flight with another one with same sector departing in 22 days
    When I send the changeFlight request
    Then the change flight fee FlightFlexiFee<59 will be added

  @TeamD
  @Sprint32
  @FCPH-10454
  Scenario: Remove an amendable basket with hold bag and sport items
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket
    And I add with hold bag for 1 passenger
    And I add with sport items for 1 passenger
    When I send the request to emptyBasket service
    Then the basket is removed
    And the order version remains the same
    And hold bags are deallocated
    And sport items are deallocated

  @TeamD
  @Sprint32
  @FCPH-10454
  @local
  Scenario: Remove an amendable basket and de-allocate StandBy fares
    Given one of this channel ADAirport, ADCustomerService is used
    And I send the request to agent login service
    And I created a staff customer
    And I created an amendable basket for DCS sector with Standby fare
    And I want to change a flight with another one for DCS sector with Standby fare for 1 adult
    And I send the changeFlight request
    When I send the request to emptyBasket service
    Then the basket is removed
    And the order version remains the same
    And the flight standby stock level is released

  @manual
  @TeamD
  @Sprint32
  @FCPH-10454
  Scenario: Remove an amendable basket and de-allocate flight inventory and seating
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket
    And I added a flight to the basket
    And I add a seatProduct to the basket
    When I send the request to emptyBasket service
    Then the basket is removed
    And the order version remains the same
    And I verify the flight and seat has been deallocated properly

  @TeamD
  @Sprint32
  @FCPH-10454
  Scenario: Remove an amendable basket and update the infants on seat levels
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket
    And I added a flight to the basket for 1 adult; 1 infant
    And infants limits and consumed values are stored for the flight
    When I send the request to emptyBasket service
    Then the number of infantsOnSeat for the flight will be released
    And the order version remains the same


  @TeamD
  @Sprint32
  @FCPH-10454
  Scenario: Remove an amendable basket and update the infants levels
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket
    And I added a flight to the basket for 1 adult; 1,0 infant
    And infants limits and consumed values are stored for the flight
    When I send the request to emptyBasket service
    Then the number of infants for the flight will be released
    And the order version remains the same

