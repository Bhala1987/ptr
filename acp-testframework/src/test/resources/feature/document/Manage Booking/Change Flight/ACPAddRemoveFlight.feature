Feature: ACP Add / Remove Flight

  @TeamD
  @Sprint30
  @FCPH-10592
  Scenario: Add new flight and passenger is linked to another flight
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket with return
    And I want to search a flight to change an existing one
    When I change the flight
    Then the passenger in the new flight is linked to the other flights

  @manual
  @Sprint32 @TeamC @FCPH-11115
  Scenario Outline: Create a new version of the booking
    Given I am using Digital channel
    When I commit booking request for amendable basket containing <Product Line Items> after delete additional flight
    Then a new version of the booking should be created
    And the previous version status should be setted to 'Amended'
    And the new version of the booking should be linked to the previous one
    And the product line items statues should be copied from the amendable basket to the booking
    Examples:
    |Product Line Items |
    |Flight             |

  @manual
  @Sprint32 @TeamC @FCPH-11115
  Scenario: Add Booking History entry
    Given I am using Digital channel
    When I commit booking request for amendable basket after delete additional flight
    Then a new version of the booking should be created
    Then I will set Date and Time,
    And Booking History Channel should be showing as Agent Desktop
    And Booking History User Id should be showing as Agent ID
    And Booking History Event Type should be set to <Action taken>
    And Booking History Description should be set to Flight Key, Passenger Last Name, Passenger First Name for each flight
    And Booking History Version should be set as Booking Version

  @Sprint32 @TeamC @FCPH-11115
  Scenario: Release the amendment lock on the booking after remove flight
    Given I am using Digital channel
    When I commit booking request for amendable basket after delete additional flight
    Then the amendment lock on the booking should be released


  @Sprint32 @TeamC @FCPH-11115
  Scenario: Return confirmation to the channel after remove flight
    Given I am using PublicApiMobile channel
    When I commit booking request for amendable basket after delete additional flight
    Then I want validate successful response after commit booking
    And I want to check status for flight in the booking is INACTIVE
    And I want to check amend entry status for flight in the booking is CHANGED