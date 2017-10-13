@FCPH-3678
@Sprint29
@TeamC
Feature: Create a invalid request to generate a quote for car hire

  Background:
    Given I am using the channel Digital

  Scenario Outline: Generate error message if the basket does not contain an adult passenger or more than 6 passengers in the basket  type BR_00590
    When I add the flight to the basket with passenger <passengerMix>
    And I request for a car hire search
    Then I see an error message for car hire add basket <errorCode>

    Examples:
      | passengerMix | errorCode       |
      | 7 Adult      | SVC_100800_1003 |
      | 1 Child      | SVC_100800_1009 |

  Scenario Outline: Generate validation errors
    When I add the flight to the basket with passenger 1 Adult
    And I request for a car hire with invalid <invalidField> and value <value>
    Then I see an error message for car hire add basket <errorCode>
    Examples:
      | invalidField             | value | errorCode       |
      | driverAgeLessThan        | 17    | SVC_100800_1001 |
      | carPickUpBeforeArrival   |       | SVC_100800_1008 |
      | carDropOffAfterDeparture |       | SVC_100800_1007 |

  @manual @TeamC
  Scenario: Generate validation errors
    When I add the flight to the basket with passenger 1 Adult
    And I"ve my return flight more than and try to book car more than 28 days
    Then I see an error message for car hire add basket SVC_100800_1006
