Feature: Retrieve Flights and Offer Prices

  @local
  @TeamD
  @Sprint30
  @FCPH-10720
  @BR:BR_01811
  Scenario: Restrict flight results based on number of passengers
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created a staff customer
    And I want to search a flight for DCS sector for 3 adult
    When I sent the request to getFlights service
    Then the channel receive a list of flights
    And Standby flight is not returned

  @local
  @TeamD
  @Sprint30
  @FCPH-10720
  @BR:BR_00031
  Scenario Outline: Restrict flight results based on passenger type
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created a staff customer
    And I want to search a flight for DCS sector for <passengerMix>
    When I sent the request to getFlights service
    Then the channel receive a list of flights
    And Standby flight is not returned
    Examples:
      | passengerMix      |
      | 1 adult; 1 child  |
      | 1 adult; 1 infant |

  # hard-coded sector because we don't have any flight for non-DCS sectors
  @local
  @TeamD
  @Sprint30
  @FCPH-10720
  Scenario: Restrict flight results if both airports are flagged as eRes DCS Airports
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created a staff customer
    And I want to search a flight from BIA to TLS for 1 adult
    When I sent the request to getFlights service
    Then the channel receive a list of flights
    And Standby flight is not returned

  @local
  @TeamD
  @Sprint30
  @FCPH-10720
  Scenario: Check stock levels for standby inventory
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created a staff customer
    And I want to search a flight for DCS sector with Standby fare for 1 adult
    When I sent the request to getFlights service
    Then the channel receive a list of flights
    And Standby flight is returned

  @Sprint27
  @FCPH-9509
  Scenario Outline: Inclusion/Exclusion of admin fee in the price of the flight for AD channels
    Given one of this channel ADAirport, ADCustomerService is used
    And I want to search a flight
    And I have specified the exclude-admin-fee parameter as <adminFee>
    When I sent the request to getFlights service
    Then the AdminFee <not> showed in the results
    Examples:
      | adminFee | not    |
      | true     | is not |
      | false    | is not |

  @Sprint27
  @FCPH-9509
  Scenario Outline: Inclusion/Exclusion of admin fee in the price of the flight for Digital channel
    Given one of this channel Digital, PublicApiMobile is used
    And I want to search a flight
    And I have specified the exclude-admin-fee parameter as <adminFee>
    When I sent the request to getFlights service
    Then the AdminFee <not> showed in the results
    Examples:
      | adminFee | not    |
      | true     | is not |
      | false    | is     |

  @Sprint27
  @FCPH-9509
  Scenario Outline: Inclusion/Exclusion of admin fee in the price of the flight for PublicApi channels
    Given the channel PublicApiB2B is used
    And I want to search a flight
    And I have specified the exclude-admin-fee parameter as <adminFee>
    When I sent the request to getFlights service
    Then the AdminFee <not> showed in the results
    Examples:
      | adminFee | not    |
      | true     | is not |
      | false    | is     |