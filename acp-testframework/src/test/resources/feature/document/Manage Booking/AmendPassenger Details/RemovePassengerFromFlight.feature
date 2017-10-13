@Sprint28
Feature: Process Remove Passenger from an existing flight on the booking - not Public API B2B

  @FCPH-9641
  Scenario Outline: Remove passenger reassociate infant on lap to another adult with Commit Booking
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to Remove one passenger that have an infant on lap <booking> with <passenger> and <fareType>
    Then I check that the passenger has been removed
    Examples:
      | channel | passenger           | fareType | booking      |
      | Digital | 3 adult; 2,0 infant | Standard | with Booking |

  @FCPH-9641
  Scenario Outline: Passenger being removed has an infant on lap associated BR_00040 after Commit booking
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to Remove one passenger that have an infant on lap <booking> with <passenger> and <fareType>
    Then I will receive an error with code '<error>'
    Examples:
      | channel | passenger           | fareType | error           | booking      |
      | Digital | 1 adult; 1,0 infant | Standard | SVC_100017_1005 | with Booking |

  @Sprint31 @TeamC @FCPH-10471 @FCPH-9641
  Scenario Outline: Remove passenger reassociate infant on lap to another adult no Commit Booking
    Given I am using the channel <channel>
    When I send a request to Remove one passenger that have an infant on lap <booking> with <passenger> and <fareType>
    Then I check that the passenger has been removed
    And the infant is assigned to the second adult on lap
    Examples:
      | channel   | passenger           | fareType | booking    |
      | Digital   | 3 adult; 2,0 infant | Standard | no Booking |
      | ADAirport | 3 adult; 2,0 infant | Standard | no Booking |

  @Sprint31 @TeamC @FCPH-10471 @FCPH-9641
  Scenario Outline: Passenger being removed has an infant on lap associated BR_00040 no Commit booking
    Given I am using the channel <channel>
    When I send a request to Remove one passenger that have an infant on lap <booking> with <passenger> and <fareType>
    Then I will receive an error with code '<error>'
    Examples:
      | channel   | passenger           | fareType | error           | booking    |
      | Digital   | 1 adult; 1,0 infant | Standard | SVC_100017_1005 | no Booking |
      | ADAirport | 1 adult; 1,0 infant | Standard | SVC_100017_1005 | no Booking |

  @TeamD
  @Sprint29
  @FCPH-9624 @FCPH-10508
  Scenario Outline: Remove Passenger from Flight Request - basket id does not exist
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to Remove a passenger with invalid basket id for single flight false
    Then the channel will receive an error with code SVC_100017_1001
    Examples:
      | channel   | passenger | fareType |
      | Digital   | 2 adult   | Standard |
      | ADAirport | 2 adult   | Standard |

  @TeamD
  @Sprint29
  @FCPH-9624 @FCPH-10508
  Scenario Outline: Remove Passenger from Flight Request - flight id/passenger id does not exist
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to Remove a passenger with invalid passenger id for single flight false
    Then the channel will receive an error with code SVC_100017_1002
    Examples:
      | channel   | passenger | fareType |
      | Digital   | 2 adult   | Standard |
      | ADAirport | 2 adult   | Standard |

  @TeamD
  @Sprint29
  @FCPH-9624 @FCPH-10508
  Scenario Outline: Generate Error only infants left on the flight BR_01800 BR_00040
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to Remove a passenger for single flight false
    Then the channel will receive an error with code <error>
    Examples:
      | channel   | passenger           | fareType | error |
      | Digital   | 2 adult; 2,0 infant | Standard |SVC_100017_1006 |
      | ADAirport | 1 adult; 1,0 infant | Standard |SVC_100017_1005 |
      | ADAirport | 2 adult; 4,4 infant | Standard |SVC_100017_1007 |

  @manual
  @TeamD
  @Sprint29
  @FCPH-9624 @FCPH-10508
  Scenario Outline: Generate Error if passenger removed for staff or standby bundle type but the remaining passenger mix is only children and infant BR_00031
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to Remove a passenger for single flight false
    Then the channel will receive an error with code SVC_100017_1005
    Examples:
      | channel   | passenger           | fareType |
      | Digital   | 1 adult; 1,0 infant | Staff  |
      | ADAirport | 1 adult; 1,0 infant | Staff  |

  @TeamD
  @Sprint29
  @FCPH-9624 @FCPH-10508
  Scenario Outline: Generate Error if passenger is removed and the remaining passenger mix consists of only children and infant
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to Remove a passenger for single flight false
    Then the channel will receive an error with code SVC_100017_1005
    Examples:
      | channel   | passenger                    | fareType |
      | Digital   | 1 adult; 1,0 infant; 1 child | Standard |
      | ADAirport | 1 adult; 1,0 infant; 1 child | Standard |

  @Sprint32 @TeamC @FCPH-11116 @manual
  Scenario Outline: Create a new version of the booking after remove passenger
    Given I am using <channel> channel
    When I commit booking request for amendable basket containing <passenger> after delete additional flight
    Then a new version of the booking should be created
    And the previous version status should be setted to 'Amended'
    And the new version of the booking should be linked to the previous one
    And the product line items statues should be copied from the amendable basket to the booking
    Examples:
      | channel   | passenger                    |
      | Digital   | 1 adult; 1,0 infant; 1 child |
      | ADAirport | 1 adult; 1,0 infant; 1 child |

  @Sprint32 @TeamC @FCPH-11116 @manual
  Scenario: Add Booking History entry after remove passenger
    Given I am using Digital channel
    When I commit booking request for amendable basket after delete additional flight
    Then a new version of the booking should be created
    Then I will set Date and Time,
    And Booking History Channel should be showing as Agent Desktop
    And Booking History User Id should be showing as Agent ID
    And Booking History Event Type should be set to <Action taken>
    And Booking History Description should be set to Flight Key, Passenger Last Name, Passenger First Name for each flight
    And Booking History Version should be set as Booking Version

  @Sprint32 @TeamC @FCPH-11116
  Scenario: Release the amendment lock on the booking after remove passenger
    Given I am using Digital channel
    When I commit booking request for amendable basket after delete additional passenger
    Then the amendment lock on the booking should be released

  @Sprint32 @TeamC @FCPH-11116
  Scenario: Return confirmation to the channel after remove passenger
    Given I am using Digital channel
    When I commit booking request for amendable basket after delete additional passenger
    Then I want validate successful response after commit booking
    And I want to check the passenger status for booking is INACTIVE
    And I want to check the passenger amend status for booking is CHANGED
