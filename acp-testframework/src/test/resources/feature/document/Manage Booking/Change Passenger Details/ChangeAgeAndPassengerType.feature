Feature: ACP Change Age / Passenger Type

  @Sprint27
  @FCPH-2674
  Scenario Outline: Receive Change Passenger Details Request
    Given <channel> do the commit booking with "2 Adult, 1 Child, 1 Infant OOS"
    And I have initiated a change passenger age action from <From> to <To> for single flight
    When I process the request for change passenger age
    Then I will validate the request is in the expected format
    Examples:
      | channel   | From  | To       |
      | ADAirport | Adult | Adult-28 |
      | Digital   | Child | Child-11 |

  @Sprint27
  @FCPH-2674
  Scenario Outline: Change Passenger Age results in no change in passenger type
    Given <channel> do the commit booking with "2 Adult, 1 Child, 1 Infant OOS"
    And I have initiated a change passenger age action from <From> to <To> for single flight
    And I process the request for change passenger age
    When the new passenger age does not change the passenger type
    Then I will update the passenger age with the new value
    And I will receive the confirmation
    Examples:
      | channel   | From   | To       |
      | ADAirport | Adult  | Adult-28 |
      | ADAirport | Child  | Child-5  |
      | ADAirport | Infant | Infant-1 |
      | Digital   | Adult  | Adult-38 |
      | Digital   | Child  | Child-11 |

  @Sprint27
  @FCPH-2674
  Scenario Outline: Return error to channel for an invalid change in passenger type
    Given <channel> do the commit booking with "2 Adult, 1 Child, 1 Infant OOS"
    When I send an invalid change passenger request from <From> to <To>
    Then I will get an <Error> error message
    Examples:
      | channel   | From  | To       | Error           |
      | ADAirport | Adult | Adult-55 | SVC_100012_3016 |
      | Digital   | Child | Child-11 | SVC_100012_3016 |

  # It is not possible to manage the allocation of the flights, it will be covered by the e2e integration tests
  @manual
  @Sprint27 @Sprint28
  @FCPH-8457
  Scenario Outline: Change Passenger Age in Agent Desktop results in an infant on lap becoming an Adult or Child passenger, no inventory
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket for 1 adult; 1,0 infant
    And there is no more inventory on that flight
    When I change the age of an infantOnLap with <passengerType> age
    Then I will return an error message to the channel
    Examples:
      | passengerType |
      | child         |
      | adult         |

  @Sprint27 @Sprint28
  @FCPH-8457 @ADTeam @defect:FCPH-11288
  Scenario Outline: Change Passenger Age results in an infant on lap becoming an Adult or Child passenger - single flight, not checked in
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket for 1 adult; 1,0 infant
    When I change the age of an infantOnLap with <passengerType> age
    Then the passenger is changed to <passengerType>
    Examples:
      | passengerType |
      | child         |
      | adult         |

  @local
  @Sprint27 @Sprint28
  @FCPH-8457
  Scenario Outline: Change Passenger Age results in an infant on lap becoming an Adult or Child passenger - single flight, checked in
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket for 1 adult; 1,0 infant for checked-in passengers
    When I change the age of an infantOnLap with <passengerType> age
    Then the passenger is changed to <passengerType>
    Examples:
      | passengerType |
      | child         |
      | adult         |

  @Sprint27 @Sprint28
  @FCPH-8457 @ADTeam @defect:FCPH-11288
  Scenario Outline: Change Passenger Age in Agent Desktop results in an infant on lap becoming an Adult or Child passenger - multiple flights, not checked in
    Given one of this channel ADAirport, ADCustomerService is used
    And I added a flight to the basket for 1 adult; 1,0 infant
    And I created an amendable basket for 1 adult; 1,0 infant
    When I change the age of an infantOnLap with <passengerType> age
    Then the passenger is changed to <passengerType> for all the flights
    Examples:
      | passengerType |
      | child         |
      | adult         |

  @local
  @Sprint27 @Sprint28
  @FCPH-8457 @defect:FCPH-11288
  Scenario Outline: Change Passenger Age in Agent Desktop results in an infant on lap becoming an Adult or Child passenger - multiple flights, checked in
    Given one of this channel ADAirport, ADCustomerService is used
    And I added a flight to the basket for 1 adult; 1,0 infant
    And I created an amendable basket for 1 adult; 1,0 infant for checked-in passengers
    When I change the age of an infantOnLap with <passengerType> age
    Then the passenger is changed to <passengerType> for all the flights
    Examples:
      | passengerType |
      | child         |
      | adult         |

  @TeamD
  @Sprint30 @Sprint31 @Sprint32
  @FCPH-10780
  @BR:BR_01192,BR_01860,BR_01864,BR_01540,BR_00040
  Scenario Outline: Passenger Age Change results in changing a passenger to an infant on lap - single flight, not checked in - no errors
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket for 2 adult; 1 child
    When I change the age of <originalPassengerType> with infant age
    Then the passenger is changed to infant
    Examples:
      | originalPassengerType |
      | an adult              |
      | a child               |

  @local
  @TeamD
  @Sprint30 @Sprint31 @Sprint32
  @FCPH-10780
  @BR:BR_01192,BR_01860,BR_01864,BR_01540,BR_00040
  Scenario Outline: Passenger Age Change results in changing a passenger to an infant on lap - single flight, checked in - no errors
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket for 2 adult; 1 child for checked-in passengers
    When I change the age of <originalPassengerType> with infant age
    Then the passenger is changed to infant
    Examples:
      | originalPassengerType |
      | an adult              |
      | a child               |

  @TeamD
  @Sprint30 @Sprint31 @Sprint32
  @FCPH-10780
  @BR:BR_01860,BR_01864
  Scenario Outline: Change Passenger Age in Agent Desktop results in changing a passenger to an infant on lap - multiple flights - no errors
    Given one of this channel ADAirport, ADCustomerService is used
    And I added a flight to the basket for 2 adult; 1 child
    And I created an amendable basket for 2 adult; 1 child
    When I change the age of <originalPassengerType> with infant age
    Then the passenger is changed to infant for all the flights
    Examples:
      | originalPassengerType |
      | an adult              |
      | a child               |

  @TeamD
  @Sprint30 @Sprint31 @Sprint32
  @FCPH-10780
  @BR:BR_00040
  Scenario Outline: Change Passenger Age in Agent Desktop results in a passenger becoming an infant on lap and there is no Adult without an infant on lap on booking
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket for 1 adult; 1 child; 1,0 infant
    When I change the age of <originalPassengerType> with infant age
    Then the channel will receive an error with code SVC_100273_3014
    Examples:
      | originalPassengerType |
      | an adult              |
      | a child               |

  @TeamC
  @Sprint29
  @FCPH-9669
  @BR:BR_01891
  Scenario Outline: Change in passenger type not allowed for channel post booking
    Given the channel <channel> is used
    And I created an amendable basket for <passengerMix>l
    When I change the age of <originalPassengerType> with <newPassengerType> age
    Then the channel will receive an error with code SVC_100247_1001
    Examples:
      | channel         | passengerMix                 | originalPassengerType | newPassengerType |
      | Digital         | 2 adult; 2 child             | an adult              | infant           |
      | Digital         | 1 adult; 1 child; 1,0 infant | an infantOnLap        | adult            |
      | PublicApiMobile | 1 adult; 2 child             | a child               | infant           |
      | PublicApiMobile | 1 adult; 2 child; 1 infant   | an infant             | child            |

  @TeamC
  @Sprint29
  @FCPH-9669
  @BR:BR_01891 @defect:FCPH-11288
  Scenario Outline: Change in passenger type allowed for channel post booking
    Given the channel <channel> is used
    And I created an amendable basket for <passengerMix>
    When I change the age of <originalPassengerType> with <passengerType> age
    Then the passenger is changed to <passengerType>
    Examples:
      | channel           | passengerMix                 | originalPassengerType | passengerType |
      | ADAirport         | 1 adult; 1 child; 1,0 infant | an infantOnLap        | adult         |
      | ADCustomerService | 2 adult; 1 child             | an adult              | infant        |
      | Digital           | 2 adult; 1 child             | an adult              | child         |
      | ADCustomerService | 1 adult; 1 child             | a child               | adult         |
      | ADAirport         | 1 adult; 1 child; 1 infant   | an infant             | adult         |

  @Sprint29 @Sprint31 @TeamC @FCPH-7343
  Scenario Outline: Change Age results in a passenger type change and Seat is suitable for new passenger type
    Given I am using channel <channel>
    And I want to proceed with add purchased seat <seat>
    And I edit booking with passenger mix of <passengerMix> with <continuous> purchased seat of <seat>
    When I change the age of <oldPassengerType> with <newPassengerType> age having emergency exit seat false
    Then the <newPassengerType> should be set in the basket
    Examples:
      | channel           | passengerMix     | oldPassengerType | newPassengerType | seat     | continuous |
      | Digital           | 1 adult, 1 child | an adult         | child            | UPFRONT  | false      |
      | PublicApiMobile   | 2 adult, 1 child | a child          | adult            | STANDARD | false      |
      | ADAirport         | 2 adult, 2 child | an adult         | child            | UPFRONT  | false      |
      | ADCustomerService | 1 adult, 2 child | a child          | adult            | STANDARD | false      |

  @Sprint29 @Sprint31 @TeamC @FCPH-7343
  Scenario Outline: Seat is no longer suitable for new passenger type
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    And I edit booking with passenger mix of <passengerMix> with <continuous> purchased seat of <seat> having emergency exit seat true
    When I change the age of <oldPassengerType> with <newPassengerType> age having emergency exit seat true
    Then the channel will receive an error with code SVC_100600_1012
    Examples:
      | channel   | passengerMix | oldPassengerType | newPassengerType | seat          | continuous |
      | ADAirport | 2 adult      | an adult         | child            | EXTRA_LEGROOM | false      |

  @Sprint29 @Sprint31 @TeamC @FCPH-7343
  Scenario Outline: Purchased seat no longer required for passenger
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    And I edit booking with passenger mix of <passengerMix> with <continuous> purchased seat of <seat>
    When I change the age of <oldPassengerType> with <newPassengerType> age having emergency exit seat false
    Then the <newPassengerType> should be set in the basket
    And the purchased seat for <newPassengerType> should be removed in the basket
    Examples:
      | channel           | oldPassengerType | newPassengerType | passengerMix     | seat     | continuous |
      | ADAirport         | an adult         | infantOnLap      | 2 adult          | UPFRONT  | true       |
      | ADCustomerService | a child          | infantOnLap      | 1 adult, 1 child | STANDARD | true       |