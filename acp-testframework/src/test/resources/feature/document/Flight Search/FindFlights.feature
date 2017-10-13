Feature: Find Flights for a set of search criteria

  @FCPH-320 @FCPH-321 @FCPH-435
  Scenario Outline: Maximum passengers
    Given I am using the channel <channel>
    Given the maximum number of passengers is 40
    When I search for flight with "<passenger>" exceeding maximum
    Then "SVC_100148_3005" error is returned
    Examples:
      | passenger         |
      | 40 Adult, 1 Child |
      | 1 Adult, 40 Child |
      | 39 Adult, 2 Child |

  @FCPH-320 @FCPH-321 @FCPH-435
  Scenario Outline: Error when Outbound Date is before today
    When I search for flight with "<condition>"
    Then "<errorCode>" error is returned
    Examples:
      | condition                            | errorCode       |
      | outbound date is before today        | SVC_100148_3001 |
      | inbound date is before outbound date | SVC_100148_3002 |
      | no origin airport                    | SVC_100148_2010 |
      | no destination airport               | SVC_100148_2011 |
      | wrong destination airport            | SVC_100148_2021 |
      | wrong route                          | SVC_100148_2005 |
      | wrong origin airport                 | SVC_100148_2020 |
      | outbound date is before today        | SVC_100148_3001 |
      | outbound date in a wrong format      | SVC_100148_2006 |
      | inbound date in a wrong format       | SVC_100148_2006 |

  @FCPH-320 @FCPH-321 @FCPH-435
  Scenario: Multiple errors when multiple incorrect parameters supplied for a search
    When I search for flight with "multiple incorrect search criteria"
    Then multiple error messages returned

  @regression
  @FCPH-320 @FCPH-321 @FCPH-435 @FCPH-196
  Scenario Outline: Search for direct flights for infant on own seat and also verify the flight results returned
    Given I am using the channel <channel>
    When I call the flight search with "1 Adult, 1 Infant OOS"
    Then flight is returned with "1 Adult, 1 Infant OOS"
    And the flight has a flight key
    Examples:
      | channel   |
      | Digital   |
      | ADAirport |

  @FCPH-320 @FCPH-321 @FCPH-435 @TeamA @Sprint31 @FCPH-10402
  Scenario: Validate the Maximum number of x infants on own seat per adult x = 2
    Given a valid flight exists with maximum number of infants on own seat "1 Adult, 3 Infant OOS"
    Then "SVC_100148_3007" error is returned

  @FCPH-7918
  Scenario Outline: Apply fees and takes to the adult passenger fare
    Given I send a getFlight request for <passenger> through <channel>
    When I see response includes additional seats for the requested <passenger>
    Then fees and taxes will be applied for <passenger> including credit card fee
    Examples:
      | channel           | passenger |
      | ADAirport         | 1,1 adult |
      | ADCustomerService | 1,1 child |
      | Digital           | 1,1 adult |
      | PublicApiB2B      | 1,1 child |

  @FCPH-196
  Scenario Outline: Validate that the staff customer has not logged in
    Given I am using channel <channel>
    And I am not logged in
    And I am going to send a request for staff member
    When I send the getFlight request
    Then I will return a SVC_100148_3000 error message to the channel
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |
      | PublicApiB2B      |

  @FCPH-196
  Scenario Outline: Validate that standard customer logged in and sending request for staff member
    Given I am using channel <channel>
    And I am logged in as a standard customer
    And I am going to send a request for staff member
    When I send the getFlight request
    Then I will return a SVC_100148_3000 error message to the channel
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @FCPH-196
  Scenario Outline: Passenger mix must include an adult if children included
    Given I am using channel <channel>
    And I am logged in as a staff member
    And I am going to send a request for staff member
    And request contains only a child passenger type
    When I send the getFlight request
    Then I will return a SVC_100148_3025 error message to the channel
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |

  @FCPH-196
  Scenario Outline: Restrict flight results based on number of passenger
    Given I am using channel <channel>
    And I am logged in as a staff member
    And I am going to send a request for staff member
    And request contains more than 2 passengers excluding infant on lap
    When I send the getFlight request
    Then I will not see the standby bundles in the results
    Examples:
      | channel      |
      | ADAirport    |
      | PublicApiB2B |
      | Digital      |

  @FCPH-196
  Scenario Outline: Return Bundles for staff
    Given I am using channel <channel>
    And I am logged in as a staff member
    And I am going to send a request for staff member
    When I send the getFlight request
    Then I will return bundles for the channel
    And no Credit card or Admin fees are applied
    And relevant taxes are applied
    Examples:
      | channel |
      | Digital |

  @FCPH-7653
  Scenario Outline: Identify flight when check in window is closed
    Given I am using channel <Channel>
    When a flight exists with "<CheckInWindow>" CheckInWindow closed for <Passenger>
      | origin      | LTN |
      | destination | ALC |
    Then the availableStatus should be Unavailable
    Examples:
      | CheckInWindow | Channel           | Passenger |
      | Online        | Digital           | 1,1 adult |
      | Airport       | ADAirport         | 1,1 adult |
      | Online        | ADCustomerService | 1,1 adult |

  @Sprint27
  @FCPH-9771
  @BR:BR_4005
  Scenario Outline: Flight result to the max flexible days allowed with warning message
    Given I am using channel <Channel>
    And I have <property> defined for flight search
    When I call flight search with passenger mix <pax> and flexible days outside max range
    Then I get a warning message with <warning>
    And I will get flight search result within the plus and minus range of flexible days from the travel date
    Examples:
      | Channel         | property                | pax                   | warning         |
      | PublicApiMobile | maxFlightSearchDayRange | 1 Adult, 1 Infant OOS | SVC_100148_3019 |

  @Sprint27
  @FCPH-9771
  @BR:BR_4005
  Scenario Outline: Flights result within the plus and minus range of flexible days from the travel date
    Given I am using channel <Channel>
    And I call the flight search with <pax> and flexible days <flexiDays>
    Then I will get flight search result within the plus and minus range of flexible days from the travel date
    And the search result are not outside the range of <flexiDays>
    Examples:
      | Channel         | pax                   | flexiDays |
      | Digital         | 1 Adult, 1 Infant OOS | 3         |
      | PublicApiMobile | 1 Adult               | 4         |
