@FCPH-3642 @FCPH-167
Feature: Add Flight to basket

  Scenario Outline: Adding a flight from Agent and non-Agent Desktop channel and verify Successful response for a return flight
    Given I have found a valid flight via the <channel>
    When I add the flight to my basket via the <channel>
    Then the flight is added to the basket via the <channel>
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @FCPH-164 @FCPH-7038
  Scenario Outline: AddFlight response for an outbound flight and base price same when added to the basket
    Given I have found a valid flight via the <channel>
    When I add the flight to my basket via the <channel>
    Then the flight is added to the basket via the <channel>
    And the base price and associated taxes are the same in the basket
  @regression
    Examples:
      | channel |
      | Digital |
    Examples:
      | channel      |
      | ADAirport    |
      | PublicApiB2B |

  @FCPH-204
  Scenario Outline: Receive a valid request to add staff bundle to the basket
    Given the logged in customer is a staff customer  with credential "a.rossi@reply.co.uk" and "1234"
    And the booking type is Staff
    When I add the flight to the basket with passenger "<passengers>" using channel "<channel>"
    Then I should see the selected flight bundle is added per passenger
    And no Credit card, admin fees be applied
    Examples:
      | passengers       | channel |
      | 2 Adult, 1 Child | Digital |

  @FCPH-204
  Scenario Outline: Receive a valid request to add staff bundle to the basket on specific route
    Given the logged in customer is a staff customer  with credential "a.rossi@reply.co.uk" and "1234"
    And the booking type is Staff
    When I add the flight with route "<route>" to the basket with passenger "<passengers>" using channel "<channel>"
    Then I should see the selected flight bundle is added per passenger
    And Flight Tax "UK" is included in the Fare price per passenger if applicable route
    Examples:
      | route  | passengers       | channel |
      | LTNCDG | 2 Adult, 1 Child | Digital |

  @FCPH-204 @negative
  Scenario Outline: Staff not logged in
    Given the logged in customer is not a staff customer using channel "<channel>"
    When I have added a flight with "Staff" bundle to the basket
    Then I will return a error message "SVC_100148_3000" to the channel
    Examples:
      | channel |
      | Digital |

  @TeamA @Sprint29 @FCPH-10125
  Scenario: Test that a basket with a return flight contains a link between the inbound and outbounds.
    Given basket contains return flight for 1 Adult, 1 Child, 1 Infant OL passengers Standard fare via the ADAirport channel
    Then the basket flights should be linked together using the linkedFlights attribute

  @pending
  @manual
  Scenario: A hard-grabbed flight that fills the plane capacity will not show in search results
    Given a flight that is nearly full
    And the remaining seats have been allocated
    When I search for the flight
    Then it is not returned in the list of available flights

  @pending
  @manual
  Scenario: Concurrent allocation from multiple channels
    Given there is one seat unallocated on a flight
    When two Agent Desktop channels attempt to add the flight to a basket at the same time
    Then the one flight is allocated
    And the second returns an error

  @TeamA @Sprint31 @FCPH-10402
  Scenario Outline: Associate the infant on seat to the first Adult passenger on the flight BR_04010
    Given I am using channel <Channel>
    And my basket contains "<PassengerMix>"
    Then infant on own seat associated to the first adult passenger until the ratio of adult to infant on own seat is reached
    And call empty basket service
    Examples:
      | Channel           | PassengerMix                       |
      | ADAirport         | 1 Adult, 2 Infant OOS, 1 Infant 0L |
      | ADCustomerService | 2 Adult, 2 Infant OOS              |
      | ADAirport         | 3 Adult, 5 Infant OOS              |