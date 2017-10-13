Feature: Receive request to return seat map - as part of Manage Booking

  @Sprint32 @TeamC @FCPH-3258 @local
  Scenario Outline: Validate error on passenger requesting seat is checked in
    Given I am using <channel> channel
    When I request to change the purchased seat on a passenger already checked in
    Then I <should> receive an error SVC_100400_2112 based on the channel
    Examples:
      | channel      | should |
      | Digital      | true   |
      | ADAirport    | false  |


  @Sprint32 @TeamC @FCPH-9906 @FCPH-10428
  Scenario Outline: Manage Booking - Add purchased seat in amendable basket
    Given I am using channel <channel>
    And I commit a booking with <fareType> fare and <passenger> passenger without purchased seat
    And I get the booking details
    And I create amendable basket for the booking created
    And I want to proceed with add purchased seat <seatProduct>
    When I add purchased seat <seatProduct> to the booking
    Then I should see seat number against the passenger
    And the basket totals should be updated
    And the seat entry status should be <entryStatus>
    And the seat active flag should be TRUE

    Examples:
      | channel   | fareType | passenger | seatProduct   | entryStatus |
      | Digital   | Standard | 1 adult   | EXTRA_LEGROOM | NEW         |
      | ADAirport | Flexi    | 1 adult   | UPFRONT       | CHANGED     |