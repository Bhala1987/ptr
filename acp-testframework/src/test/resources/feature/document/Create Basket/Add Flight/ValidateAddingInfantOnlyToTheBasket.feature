@FCPH-8367
Feature: Add Flight error validation so infant only can not be only traveller

  @BR:BR_00040
  Scenario Outline: Error when number of infant on own lap exceed the number of adults in a single request
    Given I am using "<channel>" to search for flights "<PassengersToSearch>"
    When I attempt to add "<PassengersToAdd>"
    Then I will return a error message "<Error>" to the channel
    Examples:
      | PassengersToSearch   | PassengersToAdd      | channel           | Error           |
      | 1 Adult, 1 Infant OL | 1 Infant OL          | Digital           | SVC_100148_3013 |
      |  3 Adult, 3 Infant OL | 2 Adult, 3 Infant OL | ADCustomerService | SVC_100148_3006 |

  @BR:BR_00040
  Scenario Outline: Error when number of infant on own lap exceed the number of adults in multiple requests
    Given I am using "<channel>" to search for flights "<PassengersToSearch>"
    When I attempt to add "<PassengersInFirstRequest>"
    And I attempt to add "<PassengersInSecondRequest>" to the same basket
    Then I will return a error message "SVC_100148_3006" to the channel
    Examples:
      | PassengersToSearch   | PassengersInFirstRequest | PassengersInSecondRequest | channel   |
      | 2 Adult, 2 Infant OL | 1 Adult                  | 2 Infant OL               | Digital   |
      | 3 Adult, 3 Infant OL | 2 Adult                  | 3 Infant OL               | ADAirport |

  @BR:BR_00040
  Scenario Outline: Number of infants on own lap equal to the number of adults
    Given I am using "<channel>" to add flight to the basket
    When I add a flight for "<passengers>"
    Then I should see passengers successfully added
    Examples:
      | passengers           | channel   |
      | 1 Adult, 1 Infant OL | Digital   |
      | 2 Adult, 2 Infant OL | ADAirport |

  @BR:BR_01800
  Scenario Outline: Error when the number of infants own seat per adult is greater than the allowed ratio in a single request
    Given I am using "<channel>" to search for flights "<PassengersToSearch>"
    When I attempt to add "<PassengersToAdd>"
    Then I will return a error message "<Error>" to the channel
    Examples:
      | PassengersToSearch    | PassengersToAdd       | channel           | Error           |
      | 1 Adult, 1 Infant OOS | 1 Infant OOS          | Digital           | SVC_100148_3013 |
      | 3 Adult, 3 Infant OOS | 2 Adult, 5 Infant OOS | ADCustomerService | SVC_100148_3007 |

  @BR:BR_01800
  Scenario Outline: Error when the number of infants own seat per adult is greater than the allowed ratio in multiple requests
    Given I am using "<channel>" to search for flights "<PassengersToSearch>"
    When I attempt to add "<PassengersInFirstRequest>"
    And I attempt to add "<PassengersInSecondRequest>" to the same basket
    Then I will return a error message "SVC_100148_3007" to the channel
    Examples:
      | PassengersToSearch    | PassengersInFirstRequest | PassengersInSecondRequest | channel   |
      | 3 Adult, 3 Infant OOS | 1 Adult                  | 3 Infant OOS              | Digital   |
      | 5 Adult, 5 Infant OOS | 2 Adult                  | 5 Infant OOS              | ADAirport |

  @BR:BR_01800
  Scenario Outline: Number of infants on own lap matches to the number of adults ratio
    Given I am using "<channel>" to add flight to the basket
    When I add a flight for "<passengers>"
    Then I should see passengers successfully added
    Examples:
      | passengers            | channel   |
      | 1 Adult, 2 Infant OOS | Digital   |
      | 2 Adult, 4 Infant OOS | ADAirport |

  @BR:BR_00031
  Scenario Outline: Error when the add flight request is for staff or standby bundle type but the passenger mix is only children or infant
    Given am using channel Digital
    And a valid customer profile has been created
    And a valid request to associate staff member to member account
    And I am using "<channel>" to search for flights "<PassengersToSearch>"
    When I add the flight to the basket as staff with "<PassengersToAdd>"
    Then I will return a error message "<Error>" to the channel
    Examples:
      | PassengersToSearch    | PassengersToAdd | channel           | Error           |
      | 1 Child               | 1 Child         | Digital           | SVC_100148_3025 |
      | 1 Adult, 1 Infant OL  | 1 Infant OL     | ADAirport         | SVC_100148_3025 |
      | 1 Adult, 1 Infant OOS | 1 Infant OOS    | ADCustomerService | SVC_100148_3025 |

  @BR:BR_00030
  Scenario: Warning message if child is travelling alone
    Given I am using "Digital" to add flight to the basket
    When I add "1 Child" to the basket as non-staff
    Then I will return a warning message "SVC_100148_3008" to the channel