Feature: Validate and Process of adding a passenger to a flight for non PublicAPI channels
  Assumes a basket has already been created from the booking and correct permissions for user and channel
  We are adding a blank passenger to an existing flight
  Service Contract ACP addPassengerToFlight

  @FCPH-9640
  @Sprint28
  Scenario Outline: Add Passenger to Flight Request - basket id does not exist
    Given the channel <channel> is used
    And I created an amendable basket for 1 adult
    When I attempt to add a passenger to the flight with an invalid basket id
    Then the channel will receive an error with code SVC_100524_2001
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @FCPH-9640
  @Sprint28
  Scenario Outline: Add Passenger to Flight Request - flight id does not exist
    Given the channel <channel> is used
    And I created an amendable basket for 1 adult
    When I attempt to add a passenger to the flight with an invalid flight id
    Then the channel will receive an error with code SVC_100273_4002
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @FCPH-9640
  @Sprint28
  Scenario Outline: Flight is already in the basket for a different bundle type BR_01013
    Given the channel <channel> is used
    And I created an amendable basket for 1 adult
    When I attempt to add a passenger to the flight with a bundle not in the basket
    Then the channel will receive an error with code SVC_100273_4007
    And the passenger is not added to the flight in the basket
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |

  @FCPH-9640
  @Sprint28
  @manual
  Scenario Outline: Flight which has a Cancelled status BR_00082
    Given the channel <channel> is used
    And I created an amendable basket for 1 adult
    And the flight to which the passenger is being added has operational status of "Cancelled"
    When I attempt to add an infant passenger to the flight in the basket with override set to false
    Then the channel will receive an error with code SVC_100273_4009
    And the passenger is not added to the flight in the basket
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |
      | Digital           |

  @FCPH-9640
  @Sprint28
  @manual
  Scenario Outline: Maximum number of x infants own seat per adult on a booking (x=2) BR_01800
    Given the channel <channel> is used
    And I created an amendable basket for 1 adult
    And the number of infants on own seat equals the per adult on booking configuration
    When I attempt to add an infant passenger to the flight in the basket with override set to false
    Then the channel will receive an error with code SVC_100273_4009
    And the passenger is not added to the flight in the basket
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |

  @FCPH-9640
  @Sprint28
  @manual
  Scenario Outline: Number of infants booked on their own seats exceeds the flight's limit - error - BR_00041
    Given the channel <channel> is used
    And I created an amendable basket for 1 adult
    And the flight in the basket is configured with the infant on seat per flight limit
    And this equals the flight's limit of infants on their own seat
    When I attempt to add an infant passenger to the flight in the basket with override set to false
    Then the passenger is not added to the flight in the basket
    And the channel will receive an error with code SVC_100273_4008
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @FCPH-9640
  @Sprint28
  @manual
  Scenario Outline: Passenger being added to a flight departing on today's date within x hours - - BR_00072
    Given the channel <channel> is used
    And I created an amendable basket for 1 adult
    And the flight to which the passenger is being added is today and departs in x hours
    When I attempt to add an infant passenger to the flight in the basket with override set to false
    Then the channel will receive an error with code SVC_100273_4009
    And the passenger is not added to the flight in the basket
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @FCPH-9640
  @Sprint28
  @manual
  Scenario Outline: Number of infants booked on their own seats exceeds the flight's limit warning - BR_00041
    Given the channel <channel> is used
    And I created an amendable basket for 1 adult
    And the flight in the basket is configured with the infant on seat per flight limit
    And this equals the flight's limit of infants on their own seat
    When I attempt to add an infant passenger to the flight in the basket with override set to true
    Then the channel will receive an error with code SVC_100273_4008
    And the passenger is added to the flight in the basket
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @Sprint29
  @TeamD
  @FCPH-3685
  @manual
  Scenario Outline: No Availability for the additional Passenger
    Given the channel <channel> is used
    And I created an amendable basket with return from LTN to ALC with <fareType> fare for <passenger>
    When I send a request to Add a passenger for single flight
    And I receive a no inventory available response
    Then the passenger is not added to the flight in the basket
    Examples:
      | channel   | passenger | fareType |
      | Digital   | 1 adult   | Standard |
      | ADAirport | 1 adult   | Standard |

  @Sprint29
  @TeamD
  @FCPH-3685
  Scenario Outline: Add Additional Passenger to Flight, inventory available - Digital and AD
    Given the channel <channel> is used
    And I created an amendable basket with return from LTN to ALC with <fareType> fare for <passenger>
    When I send a request to Add a passenger for single flight
    Then the passenger is added to single flight in the basket
    And add a <fareType> bundle for the new passenger in the basket
    And all the calculation in the basket are right after adding passenger
    Examples:
      | channel   | passenger | fareType |
      | Digital   | 1 adult   | Standard |
      | ADAirport | 1 adult   | Standard |

  @Sprint29
  @TeamD
  @FCPH-4070
  @manual
  Scenario Outline: No Availability on at least one flight
    Given the channel <channel> is used
    And I created an amendable basket with return from LTN to ALC with <fareType> fare for <passenger>
    When I send a request to Add a passenger for all flight
    And I receive a no inventory available response
    Then the passenger is not added to the flight in the basket
    Examples:
      | channel   | passenger | fareType |
      | Digital   | 1 adult   | Standard |
      | ADAirport | 1 adult   | Standard |

  @Sprint29
  @TeamD
  @FCPH-4070
  Scenario Outline: Add Additional Passenger to all flights, inventory available - Digital and AD
    Given the channel <channel> is used
    And I created an amendable basket with return from LTN to ALC with <fareType> fare for <passenger>
    When I send a request to Add a passenger for all flight
    Then the passenger is added to all flight in the basket
    And add a <fareType> bundle for the new passenger in the basket
    And all the calculation in the basket are right after adding passenger
    Examples:
      | channel   | passenger | fareType |
      | Digital   | 1 adult   | Standard |
      | ADAirport | 1 adult   | Standard |

  @FCPH-9180
  @TeamC
  @Sprint29
  Scenario Outline: Add or Remove an Infant on Seat for a booking - not Public API B2B
    Given the channel <channel> is used
    And I created an amendable basket with <fare> fare for 1 adult
    When I attempt to add an infant passenger to the flight in the basket
    Then I see recalculated basket totals
    Then the passenger is added to all flight in the basket
    Examples:
      | channel   | fare     |
      | ADAirport | Flexi    |
      | Digital   | Standard |



