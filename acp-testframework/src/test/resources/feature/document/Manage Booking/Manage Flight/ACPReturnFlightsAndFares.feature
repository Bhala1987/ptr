Feature: ACP Return Flights and Fares

  @Sprint28
  @FCPH-10093
  Scenario Outline: Receive getFlight request for change flight with different query parameters
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 2 adult; 2 child; 2,1 infant
    And I want to search a flight to change an existing one
    But the queryParam <queryParam> is <value>
    When I send the request to getFlights service
    Then the channel will receive an error with code <error>
    Examples:
      | queryParam   | value                                       | error           |
      | flightKey    | empty                                       | SVC_100148_3026 |
      | bookingRef   | empty                                       | SVC_100148_3027 |
      | flightKey    | invalid                                     | SVC_100148_3029 |
      | bookingRef   | invalid                                     | SVC_100148_3028 |
      | passengerIds | contains invalid value                      | SVC_100148_3030 |
      | passengerIds | adult does not match passenger mix          | SVC_100148_3031 |
      | passengerIds | child does not match passenger mix          | SVC_100148_3032 |
      | passengerIds | infant on seat does not match passenger mix | SVC_100148_3033 |
      | passengerIds | infant on lap does not match passenger mix  | SVC_100148_3034 |
      | passengerMix | does not match original passenger mix       | SVC_100148_3036 |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-3990 @FCPH-10554
  @BR:BR_01905,BR_01900,BR_01906BR_01907,BR_01908
  Scenario Outline: Retrieve flights where bundle is the same as the flight being changed and calculate the new flight offer price - Standard bundle, Digital channels
    Given one of this channel Digital, PublicApiMobile is used
    And I created an amendable basket with <fareType> fare for 2 adult; 2 child; 2,1 infant
    And I want to search a flight to change an existing one
    When I sent the request to getFlights service
    Then list of available flight for change is returned with <fareType> fare
    Examples:
      | fareType |
      | Standard |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-3990 @FCPH-10554 @ADTeam
  @BR:BR_01905,BR_01900,BR_01906BR_01907,BR_01908,BR_01910
  Scenario Outline: Standard can be changed to Flexi within 24 hours of booking and based on Channel
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket with <fareType> fare for 2 adult; 2 child; 2,1 infant
    And I want to search a flight to change an existing one
    When I sent the request to getFlights service
    Then list of available flight for change is returned with Standard, Flexi fares
    Examples:
      | fareType |
      | Standard |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-3990 @FCPH-10554 @ADTeam
  @BR:BR_01905,BR_01900,BR_01906BR_01907,BR_01908
  Scenario Outline: Retrieve flights where bundle is the same as the flight being changed and calculate the new flight offer price - Flexi bundles
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket with <fareType> fare for 1 adult
    And I want to search a flight to change an existing one
    When I sent the request to getFlights service
    Then list of available flight for change is returned with <fareType> fare
    Examples:
      | fareType |
      | Flexi    |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-3990 @FCPH-10554 @ADTeam
  @BR:BR_01905,BR_01900,BR_01906BR_01907,BR_01908
  Scenario Outline: Retrieve flights where bundle is the same as the flight being changed and calculate the new flight offer price - Staff bundles
    Given one of this channel ADAirport, ADCustomerService, Digital is used
    And I created an amendable basket with <fareType> fare for 1 adult
    And I want to search a flight to change an existing one
    When I sent the request to getFlights service
    Then list of available flight for change is returned with <fareType> fare
    Examples:
      | fareType |
      | Staff    |

  # We cannot wait 24 hours in automation
  @manual
  @Sprint28
  @FCPH-3990
  @BR:BR_01905,BR_01900,BR_01906BR_01907,BR_01908,BR_01910
  Scenario Outline: Retrieve flights where bundle is the same as the flight being changed and calculate the new flight offer price - Standard and Flexi bundles, AD channels
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket with <fareType> fare for 2 adult; 2 child; 2,1 infant
    And I want to search a flight to change an existing one
    When I sent the request to getFlights service
    Then list of available flight for change is returned with <fareType> fare
    Examples:
      | fareType |
      | Standard |
      | Flexi    |