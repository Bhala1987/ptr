@Sprint30 @TeamA @FCPH-9539
Feature: Commit Booking with Additional Seat

  As an agent I want to be add additional seat to my existing booking

  @FCPH-9539
  Scenario Outline: STD Bundle - 2x Pax with additional Seats and allocated seats.
    Given I am using channel <channel>
    And I am logged in as a standard customer
    And I want to proceed with add purchased seat STANDARD
    And add flight to the basket with passenger "<passengers>" with "<fareType>"
    And I provide basic passenger details
    And  I add additonal fare for "<noOfPax>" the passengers
    When I do commit booking for given basket
    Then booking request should contain standard and additional seats
    Examples:
      | passengers | channel           | fareType | noOfPax |
      | 2 Adult    | ADCustomerService | Standard | 2       |
      | 2 Adult    | ADCustomerService | Standard | 1       |


  @FCPH-9539
  Scenario Outline:  Flexi Bundle - 2x Pax with additional seats and allocated seats
    Given I am using channel <channel>
    And I am logged in as a standard customer
    And I want to proceed with add purchased seat STANDARD
    And add flight to the basket with passenger "<passengers>" with "<fareType>"
    And I provide basic passenger details
    And  I add additonal fare for "<noOfPax>" the passengers
    When I do commit booking for given basket
    Then booking request should contain standard and additional seats
    Examples:
      | channel           | fareType | passengers | noOfPax |
      | ADCustomerService | Flexi    | 2 Adult    | 2      |

  @FCPH-9539
  Scenario Outline: Error if channel not allowed to book additional seat (Public API B2B only)
    Given I am using channel <channel>
    And complete booking with "<criteria>" via "<channel>" for passengers "<passenger>"
    When add additionalfare to booking "<noOfPax>"
    Then additionalfare service returns "<errorcode>"

    Examples:
      | criteria                     | channel      | errorcode       | passenger | noOfPax |
      | Passenger with AddtionalFare | PublicApiB2B | SVC_100389_2007 | 2 Adult   | 1       |