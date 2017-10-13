Feature: Add Purchased Seats to Basket Request

  @FCPH-4083
  Scenario Outline: Add purchased seat not part of a bundle
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat STANDARD
    And a valid customer profile has been created
    And a valid request to associate staff member to member account
    And I have a flight in my basket with "<Fare without seat>" fare via channel:<Channel>
    When I make a request to add an available "STANDARD" seat product
    Then the seat product is added to the basket
    Examples:
      | Channel | Fare without seat |
      | Digital | Staff             |

  @FCPH-4083
  Scenario Outline:  Add set product with invalid price
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat STANDARD
    And my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I make a request to add an available "STANDARD" seat product with invalid price
    Then the add purchase seat service should return the error: SVC_100401_2012
    Examples:
      | Channel |
      | Digital |

  @FCPH-4083
  Scenario Outline: Add set product with invalid seat number
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat STANDARD
    And my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I make a request to add an available "STANDARD" seat product with missing seat number
    Then the add purchase seat service should return the error: SVC_100401_2008
    Examples:
      | Channel |
      | Digital |

  @FCPH-8401 @FCPH-8396 @Sprint28 @FCPH-9482
  Scenario Outline: Receive Add Seat Product Request but the request not is in the format defined
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat STANDARD
    And my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I send a request to add an available "STANDARD" seat product with "<invalid>" parameter
    Then I will receive an error with code '<error>'

    Examples:
      | Channel         | invalid             | error           |
      | Digital         | invalid basketId    | SVC_100013_1001 |
      | Digital         | invalid passengerId | SVC_100401_2010 |
      | PublicApiMobile | invalid flightKey   | SVC_100401_2011 |

  @FCPH-4083 @FCPH-8401
  Scenario Outline: Receive Add Seat Product Request for multiple passengers
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat UPFRONT
    And my basket contains "1" flights for "2" passengers with "Standard" fare added via the "<Channel>" channel
    When I make a request to add an available "UPFRONT" seat product for each passenger
    Then the seat product is added to the basket for each of the passengers
    Examples:
      | Channel         |
      | PublicApiMobile |

  @FCPH-8401 @FCPH-8396
  Scenario Outline: Add purchased seat to basket
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat STANDARD
    And my basket contains "1" flights for "1" passengers with "Standard" fare added via the "<Channel>" channel
    When I make a request to add an available "STANDARD" seat product for one passenger
    Then the seat product is added to the basket for one of the passengers
    Examples:
      | Channel   |
      | Digital   |
      | ADAirport |

  @FCPH-8401 @FCPH-8396 @Sprint28 @FCPH-9482
  Scenario Outline: Re-Price Purchased Seat, purchase seat is part of the faretype bundle BR_01480
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat <seat>
    And my basket contains "1" flights for "1" passengers with "Standard" fare added via the "<Channel>" channel
    When I make a request to add an available "<seat>" seat product for each passenger
    Then I will recalculate the price of the purchased seat

    Examples:
      | Channel         | seat     |
      | Digital         | STANDARD |
      | PublicApiMobile | UPFRONT  |

  @FCPH-8401 @FCPH-8396 @FCPH-4083 @Sprint28 @FCPH-9482
  @regression
  Scenario Outline: Recalculate Basket Totals and Return confirmation of Purchased Seat added
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat STANDARD
    And my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I make a request to add an available "STANDARD" seat product
    Then I will recalculate passenger totals, flight totals and basket totals
    And the seat product is added to the basket
    Examples:
      | Channel |
      | Digital |

  @FCPH-8401
  Scenario Outline: Recalculate Basket Totals with prise changed, seat not part of a bundle
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat EXTRA_LEGROOM
    And my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I make a request to add an available "EXTRA_LEGROOM" seat product
    Then I will recalculate passenger totals, flight totals and basket totals
    Examples:
      | Channel         |
      | PublicApiMobile |

  @regression @FCPH-4083 @FCPH-8401 @FCPH-8396
  Scenario Outline: Return confirmation of Purchased Seat added
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat STANDARD
    And my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I make a request to add an available "STANDARD" seat product
    Then the seat product is added to the basket
    Examples:
      | Channel |
      | Digital |

  @FCPH-8401 @Sprint25SpillOver
  Scenario Outline: Add any associated products to the basket
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat <seat>
    And my basket contains "1" flights for "1" passengers with "<fareType>" fare added via the "<Channel>" channel
    When I make a request to add an available "<seat>" seat product for each passenger
    Then I will also add all associated products in the bundle "<fareType>"
    Examples:
      | Channel         | seat          | fareType |
      | Digital         | STANDARD      | Flexi    |
      | PublicApiMobile | EXTRA_LEGROOM | Standard |

  @Sprint26 @FCPH-8396 @Sprint28 @FCPH-9482
  Scenario Outline: Allocate Inventory for AD requests BR_00950, BR_00960, BR_00510
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat EXTRA_LEGROOM
    And my basket contains flight with passengerMix "2 Adult" added via "<Channel>"
    When I make a request to add an available "EXTRA_LEGROOM" seat product for one passenger
    Then I will verify the seat has been allocated properly
    Examples:
      | Channel   |
      | ADAirport |

  @Sprint26 @FCPH-8396
  Scenario Outline: Do not allocate Inventory for requests from other channels
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat STANDARD
    And my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I make a request to add an available "STANDARD" seat product
    Then I will verify the seat has not been allocated
    Examples:
      | Channel |
      | Digital |

  @Sprint26 @FCPH-8396 @Sprint28 @FCPH-8935 @FCPH-9482
  Scenario Outline: Add Seat request rejected if a digital request and not for all passengers on a flight for Digital channel - BR_00510
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat EXTRA_LEGROOM
    And my basket contains flight with passengerMix "3 Adult" added via "<Channel>"
    When I make a request to add an available "EXTRA_LEGROOM" seat product for one passenger
    Then the add purchase seat service should return the error: SVC_100401_2017
    Examples:
      | Channel |
      | Digital |

  @Sprint26 @FCPH-8396
  Scenario Outline: Allocating seat for infant on lap
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat EXTRA_LEGROOM
    And my basket contains flight with passengerMix "1 Adult, 1 Infant OL" added via "<Channel>"
    When I make a request to add an available "EXTRA_LEGROOM" seat for all passenger
    Then the add purchase seat service should return the error: SVC_100401_2009
    Examples:
      | Channel |
      | Digital |

  @Sprint26 @FCPH-8396
  Scenario Outline: Allocating seat with infant has its own seat
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat EXTRA_LEGROOM
    And my basket contains flight with passengerMix "1 Adult, 1 Infant OOS" added via "<Channel>"
    When I make a request to add an available "EXTRA_LEGROOM" seat for all passenger
    Then the seat product is added for the required passenger "infant"
    Examples:
      | Channel |
      | Digital |

  @Sprint26 @FCPH-8396 @Sprint28 @FCPH-8935
  Scenario Outline: Allocate already allocated seats
    Given I am using channel <Channel>
    And I want to proceed with add already allocated purchased seat EXTRA_LEGROOM
    And my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I make a request to add an available "EXTRA_LEGROOM" seat product for one passenger with already allocated seat
    Then the add purchase seat service should return the error: SVC_100500_5034
    Examples:
      | Channel           |
      | ADCustomerService |

  @Sprint26 @FCPH-8396
  Scenario Outline: Try allocating with lower base price than provided earlier
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat STANDARD
    And my basket contains flight with passengerMix "1 Adult" added via "<Channel>"
    When I send a request to add an available "STANDARD" seat product with "<invalid>" parameter
    Then the add purchase seat service should return the error: <errorCode>
    Examples:
      | Channel         | invalid           | errorCode       |
      | PublicApiMobile | invalid basePrice | SVC_100401_2018 |

  @Sprint26 @FCPH-3421
  Scenario Outline: Allocate Seating Inventory as part of commit booking from Digital and public API mobile
    Given I am using channel <Channel>
    And I want to proceed with add purchased seat <Seat>
    And my basket contains flight with passengerMix "<Passenger Mix>" added via "<Channel>"
    And I make a request to add an available "<Seat>" seat for all passengers
    When I do commit booking with <payment type> and <payment details>
    Then commit booking should be successful
    Examples:
      | Channel | Seat          | Passenger Mix | payment type | payment details                             |
      | Digital | STANDARD      | 2 Adult       | card         | DM-5573471234567898-123-8-2018-Testing card |
      | Digital | EXTRA_LEGROOM | 1 Adult       | card         | DM-5573471234567898-123-8-2018-Testing card |
  #      | Digital         | STANDARD      | 1 Adult, 1 Infant OL | card         | DM-5573471234567898-123-8-2018-Testing card |
  #      | PublicApiMobile | STANDARD      | 1 Adult, 1 Child     | card         | DM-5573471234567898-123-8-2018-Testing card |
  #      | PublicApiMobile | UPFRONT       | 1 Adult, 1 Child     | card         | DM-5573471234567898-123-8-2018-Testing card |
  #      | PublicApiMobile | STANDARD      | 1 Adult, 1 Infant OL | card         | DM-5573471234567898-123-8-2018-Testing card |

  @Sprint28 @FCPH-9717
  Scenario Outline: Confirmation of Purchased Seat and Recalculate Basket Totals
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I added the seat with <additionalSeat> additional seat with <passengerMix> and <fareType> and <seat>
    Then I check that the seat for additional is added
    And I will recalculate basket totals with <fareType>
    Examples:
      | channel   | passengerMix | fareType | seat     | additionalSeat |
      | ADAirport | 1,2 adult    | Standard | STANDARD | 2              |
      | ADAirport | 1,1 adult    | Standard | STANDARD | 1              |

  @TeamC @Sprint29 @FCPH-10194 @pending:FCPH-10469 @ADTeam
  Scenario Outline: Seat still suitable For AD
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I added the seat <wrongSeat> and passenger detail with <passengerMix> and <fareType> and <seat>
    Then I check that SSR is added <parameter>
    Examples:
      | channel   | passengerMix | fareType | seat          | wrongSeat   | parameter     |
      | ADAirport | 1 adult      | Standard | EXTRA_LEGROOM | correctSeat | in the Basket |

  @TeamC @Sprint29 @FCPH-10194 @pending:FCPH-10469 @ADTeam
  Scenario Outline: Seat is no longer suitable For AD
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I added the seat <wrongSeat> and passenger detail with <passengerMix> and <fareType> and <seat>
    Then I will receive an error with code '<error>'
    Examples:
      | channel   | passengerMix | fareType | seat          | error           | wrongSeat |
      | ADAirport | 1 adult      | Standard | EXTRA_LEGROOM | SVC_100013_1001 | wrongSeat |

  @TeamC @Sprint29 @FCPH-10194 @manual
  Scenario Outline: Validate with Seating Service that any purchased seats are valid with new SSRs For AD
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I updated passenger detail and add Seat <wrongSeat> with <passengerMix> and <fareType> and <seat>
    Then I check that SSR is added <parameter>
    Examples:
      | channel   | passengerMix | fareType | seat          | wrongSeat   | parameter     |
      | ADAirport | 1 adult      | Standard | EXTRA_LEGROOM | correctSeat | in the Basket |

  @TeamC @Sprint29 @FCPH-10194 @manual
  Scenario Outline: Error from Seating Service that any purchased seats are invalid with new SSRs For AD
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I updated passenger detail and add Seat <wrongSeat> with <passengerMix> and <fareType> and <seat>
    Then I will receive an error with code '<error>'
    Examples:
      | channel   | passengerMix | fareType | seat          | error           | wrongSeat |
      | ADAirport | 1 adult      | Standard | EXTRA_LEGROOM | SVC_100013_1001 | wrongSeat |

  @TeamC @Sprint29 @FCPH-10194
  Scenario Outline: Error from Seating Service that any purchased seats are invalid with new SSRs For Non-AD channels
    Given the channel <channel> is used
    And I want to proceed with add purchased seat <seat>
    When I added the seat with <wrongSeat> update passenger detail and commitBooking with <passengerMix> and <fareType> and <seat>
    Then I will receive an error with code '<error>'
    Examples:
      | channel | passengerMix | fareType | seat          | wrongSeat | error           |
      | Digital | 1 adult      | Standard | EXTRA_LEGROOM | wrongSeat | SVC_100500_5016 |

  @TeamC @Sprint29 @FCPH-10194 @regression
  Scenario Outline: Validate with Seating Service that any purchased seats are valid with new SSRs For Non-AD channels
    Given the channel <channel> is used
    And travelling from LTN to ALC
    And I want to proceed with add purchased seat <seat>
    When I added the seat with <wrongSeat> update passenger detail and commitBooking with <passengerMix> and <fareType> and <seat>
    Then I check that SSR is added <parameter>
    Examples:
      | channel | passengerMix | fareType | seat     | wrongSeat   | parameter      |
      | Digital | 1 adult      | Standard | STANDARD | correctSeat | in the Booking |

  @TeamC
  @Sprint30
  @FCPH-10405
  Scenario Outline: Allow channel to request a purchased seat for a passenger's primary seat only
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I add the purchased seat only for primary seat with <passengerMix> and <fareType> and <seat>
    Then the seat product is added to the basket
    Examples:
      | channel   | passengerMix         | fareType | seat     |
      | ADAirport | 2,1 adult; 1,1 child | Standard | STANDARD |

  @TeamC
  @Sprint30
  @FCPH-10405
  Scenario Outline: Error when adding passenger's additional seat only first
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I add the purchased seat only for additional seat with <passengerMix> and <fareType> and <seat>
    Then the channel will receive an error with code SVC_100500_5036
    Examples:
      | channel           | passengerMix | fareType | seat     |
      | ADCustomerService | 1,1 adult    | Standard | STANDARD |

  @TeamC
  @Sprint30
  @FCPH-10405
  Scenario Outline: Allow channel to request a purchased seat for a passenger's additional seat only
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I add purchased seat only for additional seat with <passengerMix> and <fareType> and <seat> with <addlSeat>
    Then the additional seat product is added to the basket
    Examples:
      | channel   | passengerMix         | fareType | seat     | addlSeat |
      | ADAirport | 1,1 adult; 1,1 child | Standard | STANDARD | 1        |