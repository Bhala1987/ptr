@FCPH-7721
Feature: Add SSR for a customer

  Background:
    Given am using channel ADAirport
    And I create a Customer

  Scenario Outline: UpdateSpecialServiceRequest not in required format
    And I want to send a request to add SSR
    But the request contains "<invalid>"
    When I send a request to add SSR with "<channel>"
    Then I will receive an error with code '<error>'
    Examples:
      | channel | invalid            | error           |
      | Digital | invalid customerId | SVC_100012_3033 |

  Scenario Outline: Customer has already reached the max number of SSR
    And I add the maximum number of SSRs with "<channel>"
    When I want to send a request to add SSR
    Then I will receive an error with code '<error>'
    Examples:
      | channel   | error           |
      | ADAirport | SVC_100012_3031 |

  Scenario Outline: Check Ts and Cs checked for SSR
    And I send the body request where T&C acceptance is null
    When I send a request to add SSR with "<channel>"
    Then I will receive an error with code '<error>'
    Examples:
      | channel | error           |
      | Digital | SVC_100012_3032 |

  Scenario: Error returned when duplicate SSR added to customer
    And I add an SSR
    When I add the same SSR again
    Then I will receive an error with code 'SVC_100342_3002'

  Scenario: Error returned when empty SSR block added to customer
    When I add an empty SSR block
    Then I will receive an error with code 'SVC_100342_2001'

  Scenario: Error returned when channel attempts to add SSR that is channel inaccessible
    When I add an SSR "DEPA" that is inaccessible from channel "Digital"
    Then I will receive an error with code 'SVC_100342_2004'

  Scenario: Happy path - add single SSR to customer
    When I add an SSR
    Then I will receive an SSR added confirmation message

  @regression
  Scenario: Happy path - add more than one SSR to customer
    When I add the maximum number of SSRs with "ADAirport"
    Then I will receive an SSR added confirmation message
