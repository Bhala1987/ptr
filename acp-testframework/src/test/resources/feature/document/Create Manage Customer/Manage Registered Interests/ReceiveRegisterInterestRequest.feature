@TeamC @Sprint29 @FCPH-9957
Feature: Receive request to return Register interest to the channel

  Scenario Outline: Error if the customer is not hard logged in (extension of FCPH-9980)
    Given I am using channel <channel>
    When I send an getFlightInterest with <parameter>, <login>
    Then I receive an error
    Examples:
      | channel | login    | parameter       |
      | Digital | no Login | valid parameter |

  Scenario Outline: Error if the customer ID can not be identified
    Given I am using channel <channel>
    When I send an getFlightInterest with <parameter>, <login>
    Then I will receive an error with code '<error>'
    Examples:
      | channel | login      | parameter         | error           |
      | Digital | with Login | invalid parameter | SVC_100000_2086 |

  @regression
  Scenario Outline: Get staff fares for a flight
    Given I am using channel <channel>
    When I send an getFlightInterest with <parameter>, <login>
    Then I receive the interest Flight
    Examples:
      | channel | login      | parameter       |
      | Digital | with Login | valid parameter |

  @manual
  Scenario Outline: Remove any flights which have departed for a getFlightInterest
    Given I am using channel <channel>
    When I send an getFlightInterest with <parameter>, <login>
    Then the flight from the customer's registered flight interest should permanently remove
    Examples:
      | channel | login      | parameter       |
      | Digital | with Login | valid parameter |

  @manual
  Scenario Outline: Remove any flights which have departed for getCustomerProfile
    Given I am using channel <channel>
    When I send an getFlightInterest with <parameter>, <login>
    Then the flight should be removed from the list
    And I should receive the updated list
    Examples:
      | channel | login      | parameter       |
      | Digital | with Login | valid parameter |
