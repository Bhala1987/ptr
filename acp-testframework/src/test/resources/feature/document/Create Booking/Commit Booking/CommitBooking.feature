Feature: Commit Booking

  Verify the creation of booking and get booking
  1. Verify that booking has the flight and passenger details for given booking reference
  2. Verify the booking has customer information
  3. Verify the booking has payment transaction information

  @regression
  @FCPH-466 @FCPH-467 @FCPH-469 @FCPH-407 @FCPH-314 @FCPH-320 @FCPH-321 @FCPH-435 @FCPH-196 @FCPH-2716 @FCPH-2614
  Scenario Outline: Commit booking verification
    Given I have a basket with a valid flight with 3 adult added via <channel>
    And I select the payment method as credit card
    When I do the commit booking
    Then a booking reference <condition> returned
    And order is created from cart
    And passenger details are created with status as Booked
    And passenger has the respective bundle code
    And created date time is stored
    And customer profile is linked with the booking
#    And Payment transaction are recorded in the booking
#    And Booking history is created
    Examples:
      | channel   | condition |
      | ADAirport | is        |
      | Digital   | is        |

  @FCPH-469 @AsXml
  Scenario Outline:Corporate booking with deal information
    When I do the corporate commit booking with valid deal information for "<channel>"
    Then order is created from cart
    Then created date time is stored
    And passenger details are created with status as Booked
    And customer profile is linked with the booking
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @manual
  @FCPH-466
  Scenario Outline: Inventory is not allocated twice for Agent chanel
    Given basket contains return flight for <passengers> passengers <fareType> fare via the <channel> channel
    And the seats are allocated
    When I do the commit booking with <passengers> passengers
    Then inventory is not allocated
    Examples:
      | passengers                                   | fareType | channel           |
      | 2 Adults, 1 Child, 1 Infant OL, 1 Infant OOS | Standard | ADCustomerService |
      | 1 Adult, 1 Child, 1 Infant OL                | Standard | ADAirport         |

  @FCPH-476
  Scenario Outline: Commit booking request for a staff member
    Given I am using channel <channel>
    And I am a staff member and logged in as user <username> and <password>
    And I have valid basket for a <bookingType> booking type and <fareType> fare
    When I do the commit booking for a staff customer
    Then I will link the booking to the staff customer
    And I will return Confirmation response is generated back to the Channel
    Examples:
      | bookingType | fareType | username            | password | channel           |
      | STAFF       | Standby  | a.rossi@reply.co.uk | 1234     | Digital           |
      | STAFF       | Staff    | rachel              | 12341234 | ADCustomerService |
      | STAFF       | Standby  | rachel              | 12341234 | ADCustomerService |

  @FCPH-476
  @Sprint25
  Scenario Outline: Commit booking request for a staff member in digital
    Given I am using channel <channel>
    And I am a staff member and logged in as user <username> and <password>
    And I have valid basket for a <bookingType> booking type and <fareType> fare
    When I do the commit booking for a staff customer
    Then I will link the booking to the staff customer
    And I will return Confirmation response is generated back to the Channel
    Examples:
      | bookingType | fareType | username            | password | channel |
      | STAFF       | Staff    | a.rossi@reply.co.uk | 1234     | Digital |
