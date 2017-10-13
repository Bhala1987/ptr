Feature: Consume standby Inventory for Adds and Commit

  @local
  @TeamD
  @Sprint30
  @FCPH-10721
  Scenario: Generate error if there are not enough standby fares
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created a staff customer
    And I searched a flights for DCS sector with Standby fare
    When I add a flight to the basket for 3 adult
    Then the channel will receive an error with code SVC_100012_3041

  @local
  @TeamD
  @Sprint30
  @FCPH-10721
  Scenario: Allocate inventory based on channel
    Given one of this channel ADAirport, ADCustomerService is used
    And I created a staff customer
    And I searched a flights for DCS sector with Standby fare
    When I add the flight to the basket
    Then the basket with added flight is returned
    And the flight standby stock level is reserved

  @local
  @TeamD
  @Sprint31
  @FCPH-11268
  Scenario: Generate error message if the number of standby fare for a flight has been reached
    Given one of this channel Digital, PublicApiMobile is used
    And I created a staff customer
    And I added a flight to the basket for DCS sector with Standby fare
    And I have a valid payment method
    And I updated the passenger information with customer details
    But the flight have no standby stock availability
    When I send the request to commitBooking service
    Then the channel will receive an error with code SVC_100022_3076
    And the flight will be removed from the basket

  @local
  @TeamD
  @Sprint30
  @FCPH-10721
  Scenario: Record the number of standby fare consumed on the flight
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created a staff customer
    And I added a flight to the basket for DCS sector with Standby fare
    And I have a valid payment method
    And I updated the passenger information with customer details
    When I send the request to commitBooking service
    Then the booking is completed
    And the flight standby stock level is reserved

  @local
  @TeamD
  @Sprint30
  @FCPH-10778
  Scenario:Update the number of Standby fare consumed when a flight is removed
    Given one of this channel ADAirport, ADCustomerService is used
    And I created a staff customer
    And I added a flight to the basket for DCS sector with Standby fare for 2 adult
    And I want to remove a flight from the basket
    When I send the request to removeFlights service
    Then the flight standby stock level is released
    And the flight will be removed from the basket

  @local
  @TeamD
  @Sprint30
  @FCPH-10778
  Scenario: Update the number of Standby fare consumed on the flight when a passenger is removed
    Given one of this channel ADAirport, ADCustomerService is used
    And I created a staff customer
    And I added a flight to the basket for DCS sector with Standby fare for 1 adult
    And I want to remove a passenger from the flight
    When I send the request to removePassenger service
    Then the flight standby stock level is released
    And the passenger will be removed from the basket

#  we cannot wait the session expire in test automation
  @manual
  @TeamD
  @Sprint30
  @FCPH-10778
  Scenario: Update the number of Standby fare consumed on the flight when session times out
    Given one of this channel ADAirport, ADCustomerService is used
    And I created a staff customer
    And I added a flight to the basket for DCS sector with Standby fare for 2 adult
    When the session times out
    Then the flight standby stock level is released