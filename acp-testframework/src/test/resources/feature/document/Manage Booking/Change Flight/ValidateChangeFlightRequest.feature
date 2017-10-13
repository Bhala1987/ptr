Feature: Validate Change Flight Request

  # We do not control flights inventory
  @manual
  @TeamD
  @Sprint28 @Sprint29
  @FCPH-2678
  Scenario: Add new flight to basket, no flight inventory
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    But there is no more inventory on that flight
    When I send the changeFlight request
    Then the channel will receive an error with code SVC_100012_1001

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-2678
  Scenario: Add new flight to basket, flight inventory available, price difference
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    But the request for changeFlight contains wrong price
    When I send the changeFlight request
    Then the channel will receive a warning with code SVC_100012_3008

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-2678
  Scenario: Add new flight to basket, flight inventory available, hold items available with same price
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket with hold bag for 1 adult
    And I want to change a flight with another one
    When I send the changeFlight request
    Then the new flight is added to the basket

  # We cannot control products prices
  @manual
  @TeamD
  @Sprint28 @Sprint29
  @FCPH-2678
  Scenario: Add new flight to basket, flight inventory available, hold items available with different price
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket with hold bag for 1 adult
    And I want to change a flight with another one
    But the hold items price is different
    When I send the changeFlight request
    Then the new flight is added to the basket

  # We do not control products inventory
  @manual
  @TeamD
  @Sprint28 @Sprint29
  @FCPH-2678
  Scenario: Add new flight to basket, flight inventory available, hold items not available
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    But hold items are not available on that flight
    When I send the changeFlight request
    Then the channel will receive an error with code SVC_100288_2007

  @TeamD
  @Sprint29
  @FCPH-10342
  Scenario: Recalculate Basket total - items removed or added
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket with hold bag
    And I want to change a flight with another one
    When I send the changeFlight request
    Then the new basket calculation are right

  @TeamD
  @Sprint29
  @FCPH-452
  Scenario Outline: Change Request wrong parameters
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    But the request for changeFlight <field>
    When I send the changeFlight request
    Then the channel will receive an error with code <error>
    Examples:
      | field                                  | error           |
      | miss new flight key                    | SVC_100238_2001 |
      | miss new flight base price             | SVC_100238_2008 |
      | basketId is invalid                    | SVC_100013_1001 |
      | old flightKey is not in the basket     | SVC_100238_2004 |
      | new flightKey is already in the basket | SVC_100238_2003 |
      | passenger Ids is invalid               | SVC_100238_2009 |

  @TeamD
  @Sprint29
  @FCPH-452
  @BR:BR_01800
  Scenario: Maximum number of x infants own seat per adult on a booking (x=2)
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 2 adult; 3 infant
    And I want to change a flight with another one for 1 adult; 3 infant
    When I send the changeFlight request
    Then the channel will receive an error with code SVC_100238_2014

  @manual
  @TeamD
  @Sprint29
  @FCPH-452
  @BR:BR_00082
  Scenario: Flight which has a Cancelled status
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    But the new flight operational status is Cancelled
    When I send the changeFlight request
    Then the channel will receive an error with code SVC_100238_2011

  @manual
  @TeamD
  @Sprint29
  @FCPH-452
  @BR:BR_00072
  Scenario: Passenger being added to a flight departing on today's date within x hours
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 1 adult departing today within x hours
    And I want to add an adult to a flight
    When I send the addPassenger request
    Then the channel will receive a warning with code SVC_100238_2013

  @local
  @TeamD
  @Sprint31 @Sprint32
  @FCPH-11271
  Scenario: Change Passenger Status when Checked in
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for apis sector for 1 adult for checked-in passengers
    And I want to change a flight with another one for apis sector
    When I send the changeFlight request
    Then the new flight is added to the basket
    And the new passenger status is
      | consignment | BOOKED |
      | APIS        |        |
      | ICTS        |        |

  @TeamD
  @Sprint30
  @FCPH-10452
  Scenario: Change Passenger APIS Status when sector changes from APIS to Non APIS
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for apis sector for 1 adult
    And I want to change a flight with another one for non-apis sector
    When I send the changeFlight request
    Then the new flight is added to the basket
    And the new passenger status is
      | consignment |              |
      | APIS        | INAPPLICABLE |
      | ICTS        | UNCHECKED    |
    And APIS details should not be removed

  @TeamD
  @Sprint30
  @FCPH-10452
  Scenario: Change Passenger APIS Status when sector changes from non APIS (without entering APIS details) to APIS
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for non-apis sector for 1 adult without apis data
    And I want to change a flight with another one for apis sector
    When I send the changeFlight request
    Then the new flight is added to the basket
    And the new passenger status is
      | consignment |      |
      | APIS        | RED  |

  @TeamD
  @Sprint30
  @FCPH-10452
  Scenario: Change Passenger APIS Status when sector changes from non APIS (entered APIS details) to APIS
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for non-apis sector for 1 adult
    And I want to change a flight with another one for apis sector
    When I send the changeFlight request
    Then the new flight is added to the basket
    And the new passenger status is
      | consignment |           |
      | APIS        | GREEN     |
      | ICTS        | UNCHECKED |


