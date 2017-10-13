@Sprint26
Feature: Standby booking confirmation email

  @FCPH-481 @manual
  Scenario: generate confirmation email as a Staff Customer
    Given I have selected any flight as Staff customer
    And I have added a passenger to that flight
    When I commit my booking
    Then confirmation email should contain Booking Reference number
    And Flight details, Fare Type Information, Bag drop times
    And Bag drop times, Gate Close time,
    And Check in, Passenger details, Payment details
    And Route specific info,Airport specific info,
    And Staff Standby Content, easyJet Deep Links, terms and conditions,

  @FCPH-481 @manual
  Scenario: Display SSR next to passenger name
    Given I have selected any flight as Staff customer
    And I have added a passenger with one SSR code to the flight
    Then confirmation email should contain passenger name
    And SSR details
    And SSR link that leads to My Bookings in the passenger detail section

  @FCPH-481 @manual
  Scenario Outline: Generate Booking confirmation in lanugage stored on the booking
    Given I have selected "<language>" as an input
    And I have selected any flight as Staff customer
    And I have added a passenger to that flight
    When I commit my booking
    Then My confirmation message should be displayed in the "<language>" selected

    Examples:
      | language |
      | English  |

  @FCPH-481 @manual
  Scenario: Send booking confirmation to the registered customer email address
    Given I have selected any flight as Staff customer
    And I have added a passenger to that flight
    When I commit my booking
    Then  confirmation message should be sent to the registered email

  @FCPH-481 @manual
  Scenario: Generate event on the booking history
    Given I have selected any flight as Staff customer
    And I have added a passenger to that flight
    When I commit my booking
    Then an entry is created in the booking history
    And entry should contain following attributes
      | attribute          | value          |
      | Document Type      | standy booking |
      | Requesting Channel | AD             |
      | Issue Date/Time    | today          |
      | Requesting User ID | 0011           |
      | Issued to          | jnn@ggm.com    |

  @FCPH-481 @manual
  Scenario: Multiple Passenger  with SSR
    Given I have selected any flight as Staff customer
    And I have added two passengers with one SSR code to the flight
    When I commit my booking
    Then confirmation email should contain passenger names
    And SSR details displayed
    And SSR link that leads to My Bookings in the passenger detail section

  @FCPH-481 @manual
  Scenario: Multiple Passenger, 1 Passenger only with SSR
    Given I have selected any flight Staff customer
    And I have added two passengers to the flight
    And One with SSR data
    And Other passenger without SSR code
    When I commit my booking
    Then confirmation email should contain passenger details
    And  one passenger with SSR data
    And  other passenger without SSR data
    And other relevant data related to flight

  @FCPH-478 @manual
  Scenario: generate confirmation email as  Customer
    Given I have selected any flight as customer
    And I have added a passenger to that flight
    When I commit my booking
    Then confirmation email should contain Booking Reference number
    And Flight details, Fare Type Information, Bag drop times
    And Bag drop times, Gate Close time,
    And Check in, Passenger details, Payment details
    And Route specific info,Airport specific info,
    And Staff Standby Content, easyJet Deep Links, terms and conditions

  @FCPH-478 @manual
  Scenario: Add APIS section per sector to the booking confirmation
    Given I have selected any flight as customer
    And I have added a passenger to that flight
    And I add APIs information to the section
    When I commit my booking
    Then confirmation email should contain static text
    And deep links

  @FCPH-478 @manual
  Scenario: Add hold item section to the booking confirmation
    Given I have selected any flight as customer
    And I have added a passenger to that flight
    And I add a hold item
    When I commit my booking
    Then confirmation email should contain information about hold item

  @FCPH-478 @manual
  Scenario: Add sports equipment section to the booking confirmation
    Given I have selected any flight as customer
    And I have added a passenger to that flight
    And I add an sport item
    When I commit my bookingo
    Then confirmation email should contain information about sport item

  @FCPH-478 @manual
  Scenario: Add flight options upsell section to the booking confirmation
    Given I have selected any flight as customer
    And I have added a passenger to that flight
    And no hold item has been added
    When I commit my booking
    Then confirmation email should contain information the upsell section
    And no hold item information

  @FCPH-478 @manual
  Scenario: Add flight options upsell section to the booking confirmation
    Given I have selected any flight as customer
    And I have added a passenger to that flight
    And no sport item has been added
    When I commit my booking
    Then confirmation email should contain information about the upsell section
    And no sport item information

  @FCPH-478 @manual
  Scenario: Additional seat reason on the booking confirmation
    Given I have selected any flight as customer
    And I have added a passenger to that flight
    And I add an additional seat
    When I commit my bookingo
    Then confirmation email should contain information about the additional seat

  @FCPH-478 @manual
  Scenario: Send booking confirmation to the registered customer email address
    Given I have selected any flight as customer
    And I have added a passenger to that flight
    When I commit my booking
    Then  confirmation message should be sent to the registered email

  @FCPH-478 @manual
  Scenario: Multiple Passenger  with SSR
    Given I have selected any flight as customer
    And I have added two passengers with one SSR code to the flight
    When I commit my booking
    Then confirmation email should contain passenger names
    And SSR details displayed
    And SSR link that leads to My Bookings in the passenger detail section

  @FCPH-478 @manual
  Scenario: Multiple Passenger, 1 Passenger only with SSR
    Given I have selected any flight customer
    And I have added two passengers to the flight
    And One with SSR data
    And Other passenger without SSR code
    When I commit my booking
    Then confirmation email should contain passenger details
    And  one passenger with SSR data
    And  other passenger without SSR data
    And other relevant data related to flight

  @TeamA @Sprint31 @FCPH-10403 @manual
  Scenario: Display promotion link with a Single Fare
  Given a basket with one single fare
  When that basket is committed
  Then the email deep link "destination" should be the destination of the flight
  And the email deep link "from date" should be the flight arrival date
  And the email deep link "to date" should be the flight arrival date + 1 day

  @TeamA @Sprint31 @FCPH-10403 @manual
  Scenario: Display promotion link with a Return Fare
  Given a basket with a return fare
  When that basket is committed
  Then the email deep link "destination" should be the destination of the first flight
  And the email deep link "from date" should be the first flights arrival date
  Then the email deep link "to date" should be the departure date of the second flight

  @TeamA @Sprint31 @FCPH-10403 @manual
  Scenario: Display promotion link with a 2x Return Fare
  Given a basket with 2 return fares
  When that basket is committed
  Then the email deep link "destination" should be the destination of the first fares first flight
  And the email deep link "from date" should be the first fares first flights' arrival date
  And the email deep link "to date" should be the departure date of the first fares second flight

  @TeamA @Sprint31 @FCPH-10403 @manual
  Scenario: Display promotion link with a standby fare
    Given a basket with a standby flight
    When that basket is committed
    Then the email deep link "destination" should be the destination of the first flight
    And the email deep link "from date" should be the first flights arrival date
    Then the email deep link "to date" should be the departure date of the second flight