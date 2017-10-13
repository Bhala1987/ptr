@FCPH-8632

Feature: Receive and Process Request to Remove a Passenger from all their flights

  @FCPH-3640 @negative
  Scenario Outline: Receive Request to Remove all their Passenger with invalid Parameter
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to remove passenger for "<numFlight>"
    But the request contain "<invalid>"
    When I send a request to remove passenger
    Then I will receive an error with code '<error>'
    Examples:
      | invalid             | error           | channel           | passenger           | journey          | fareType | numFlight   |
      | invalid basketId    | SVC_100017_1001 | Digital           | 1 adult; 1,1 infant | outbound/inbound | Standard | 1 Flight    |
      | invalid passengerId | SVC_100017_1002 | ADAirport         | 1 adult; 1,1 infant | outbound/inbound | Standard | 1 Flight    |
      | invalid basketId    | SVC_100017_1001 | ADCustomerService | 1 adult; 1,1 infant | outbound/inbound | Standard | All Flights |
      | invalid passengerId | SVC_100017_1002 | PublicApiMobile   | 1 adult; 1,1 infant | outbound/inbound | Standard | All Flights |

  @FCPH-3640 @negative
  Scenario Outline: Generate Error only infants left on the flight BR_01800 BR_00040
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to remove passenger for "<numFlight>"
    When I send a request to remove passenger
    Then I will receive an error with code '<error>'
    And I will not remove the adult passenger from the flight
    Examples:
      | error           | channel           | passenger           | journey          | fareType | numFlight   |
      | SVC_100017_1005 | Digital           | 1 adult; 1,1 infant | outbound/inbound | Standard | 1 Flight    |
      | SVC_100017_1005 | ADAirport         | 1 adult; 1,1 infant | outbound/inbound | Standard | All Flights |
      | SVC_100017_1005 | ADCustomerService | 1 adult; 1,1 infant | outbound/inbound | Standard | 1 Flight    |
      | SVC_100017_1005 | PublicApiMobile   | 1 adult; 1,1 infant | outbound/inbound | Standard | All Flights |

  Scenario Outline: Auto allocate infant to next adult when first adult is removed
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to remove passenger for "<numFlight>"
    When I send a request to remove passenger
    Then the infant is autoallocated to only Adult
    Examples:
      | channel | passenger           | journey          | fareType | numFlight |
      | Digital | 2 adult; 1,0 infant | outbound/inbound | Standard | 1 Flight  |


  @FCPH-3640
  Scenario Outline: Generate Error if the add flight request is for staff or standby bundle type but the passenger mix is only children and infant BR_00031
    Given I am using the channel <channel>
    And I am logged in as a staff member
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to remove passenger for "<numFlight>"
    When I send a request to remove passenger
    Then I will receive an error with code '<error>'
    And I will not remove the adult passenger from the flight
    And I send a request to the delete customer profile
    Examples:
      | error           | channel   | passenger                    | journey          | fareType | numFlight |
      | SVC_100017_1006 | ADAirport | 1 adult; 1 child; 1,0 infant | outbound/inbound | Staff    | 1 Flight  |

  @FCPH-3640
  Scenario Outline: Generate a deallocate inventory request BR_00961
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to remove passenger for "<numFlight>"
    When I send a request to remove passenger
    Then I check that the seats are de-allocate
    Examples:
      | channel   | passenger | journey          | fareType | numFlight |
      | ADAirport | 2 adult   | outbound/inbound | Standard | 1 Flight  |

  Scenario Outline: Remove a passenger from a flight with admin fee apportioned
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to remove passenger for "<numFlight>"
    When I send a request to remove passenger
    Then I will reapportion any associated admin fee across the remaining passengers on that flight
    Examples:
      | channel | passenger                    | journey          | fareType | numFlight   |
      | Digital | 4 adult; 1 child; 1,0 infant | outbound/inbound | Standard | All Flights |

  @FCPH-3640
  Scenario Outline: Update the basket and flight total prices
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I added the hold bags and sport equipment to passeger
    And I want to remove passenger for "<numFlight>"
    When I send a request to remove passenger
    Then I recalculate the flight total
    And I remove the cabin bags, hold bags and sport equipment from the basket for the passenger
    Examples:
      | channel   | passenger        | journey          | fareType | numFlight   |
      | Digital   | 1 adult; 1 child | outbound/inbound | Standard | 1 Flight    |
      | ADAirport | 1 adult; 1 child | outbound/inbound | Standard | All Flights |

  @FCPH-3640 @regression
  Scenario Outline: Remove passenger confirmation sent to the channel
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to remove passenger for "<numFlight>"
    When I send a request to remove passenger
    Then I will receive a confirmation message
    Examples:
      | channel   | passenger                    | journey          | fareType | numFlight   |
      | ADAirport | 2 adult; 1 child; 1,0 infant | outbound/inbound | Standard | 1 Flight    |
      | Digital   | 2 adult; 1 child; 1,0 infant | outbound/inbound | Standard | All Flights |

  @FCPH-3640
  @BR:BR_00030
  Scenario Outline: Generate a warning message if child is travelling alone
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to remove passenger for "<numFlight>"
    When I send a request to remove passenger
    Then I will receive a "<warning>" message
    Examples:
      | warning         | channel   | passenger        | journey          | fareType | numFlight   |
      | SVC_100148_3008 | Digital   | 1 adult; 1 child | outbound/inbound | Standard | 1 Flight    |
      | SVC_100148_3008 | ADAirport | 1 adult; 1 child | outbound/inbound | Standard | All Flights |