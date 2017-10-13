Feature: Return Error message to the channel when the seating service returns error

  @Sprint28 @FCPH-8935
  Scenario Outline: Additional seat not available
    Given I am using <channel> channel
    And I want to proceed with add already allocated purchased seat <seat>
    When I sent a request to add purchased <seat> with 2 additional seat already allocated for 1 Adult on <fare> fare flight
    Then the add purchase seat service should return the error: SVC_100401_2021
    Examples:
      | channel   | fare     | seat     |
      | ADAirport | Standard | STANDARD |

  @Sprint28 @FCPH-8935
  Scenario Outline: Seat not available on API B2B
    Given I am using <channel> channel
    And I want to proceed with add already allocated purchased seat STANDARD
    When I sent a request to commit booking for 1 Adult on <fare> fare flight with a seat <seat-type> already allocated
    Then I see failure commit booking with error SVC_100500_5014
    Examples:
      | channel      | fare     | seat-type |
      | PublicApiB2B | Standard | STANDARD  |

  @Sprint28 @FCPH-8935
  Scenario Outline: Seat not available on Digital Public API mobile
    Given I am using <channel> channel
    And I want to proceed with add already allocated purchased seat UPFRONT
    When I sent a request to commit booking for 1 Adult on <fare> fare flight with a seat <seat-type> already allocated
    Then I see failure commit booking with error SVC_100500_5034
    And I see the seat has been removed
    And I see the price of the basket has been updated
    And I verify the product associated to the original bundle
    Examples:
      | channel | fare     | seat-type |
      | Digital | Standard | UPFRONT   |

  @Sprint28 @FCPH-8935 @defect:FCPH-11128
  Scenario Outline: Error update age on passenger with seat
    Given I am using <channel> channel
    When I update the age for 1 Adult as <original-type> to <modified-type> not allowed from the emergency exit seat already allocated on the passenger
    Then I see failure updating passenger with error SVC_100600_1012
    Examples:
      | channel           | original-type | modified-type |
      | ADCustomerService | adult         | child         |

  @Sprint28 @FCPH-8935
  Scenario Outline: Error change infant on lap association with seat
    Given I am using the channel <channel>    
    And I have in my basket <passenger> where first adult associated a emergency exit seat and the second adult associated the infant
    When I send change association adult to infant on lap
    Then the channel will receive an error with code SVC_100600_1012
    And I see the association has not been changed
    Examples:
      | channel   | passenger            |
      | ADAirport | 2 adult, 1 infant OL |

  @Sprint28 @FCPH-8935
  Scenario Outline: Validate a Change Purchased Seat request on AD channel
    Given I am using channel <channel>
    And I want to proceed with add already allocated purchased seat <seat-from>
    When I am requesting to change a <seat-from> seat for <passenger-mix> on <fare> flight with <seat-to> seat already allocated
    Then I see error <error> for failure changing
    Examples:
      | channel   | seat-from     | passenger-mix | fare     | seat-to       | error           |
      | ADAirport | EXTRA_LEGROOM | 1 Adult       | Standard | EXTRA_LEGROOM | SVC_100244_1017 |

  @Sprint32 @TeamC @FCPH-8438 @local
  Scenario: Seat not available at the price requested
    Given one of this channel Digital, PublicApiMobile is used
    When I commit a booking with purchased seat and transaction id 12222222-0000-0000-0000-000000000000
    But the price for the requested seat has been changed
    Then the commit booking should fail with error SVC_100022_3056
    And the basket should be update with the new price for the seat