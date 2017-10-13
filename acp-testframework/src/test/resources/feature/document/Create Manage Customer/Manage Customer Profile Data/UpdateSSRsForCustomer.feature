@FCPH-8386
@Sprint25
Feature: Update SSR for a customer

  Background:
    Given am using channel ADAirport
    And I create a Customer

  Scenario Outline: update ssr request not in required format
    And I want to send a request to update SSR
    But the request contains "<invalid>"
    When I send a request to update SSR with "<channel>"
    Then I will receive an error with code '<error>'
    Examples:
      | channel | invalid            | error           |
      | Digital | invalid customerId | SVC_100012_3033 |

  Scenario Outline: Customer has already reached the max number of SSR
    And I attempt to update more than the maximum number of SSRs allowed with "<channel>"
    Then I will receive an error with code '<error>'
    Examples:
      | channel   | error           |
      | ADAirport | SVC_100012_3031 |

  Scenario Outline: Check Ts and Cs checked for update SSR
    And I send the body request where T&C acceptance is null
    When I send a request to update SSR with "<channel>"
    Then I will receive an error with code '<error>'
    Examples:
      | channel | error           |
      | Digital | SVC_100012_3032 |

  Scenario: Error returned when attempt to update empty SSR block
    When I update an empty SSR block
    Then I will receive an error with code 'SVC_100342_2001'

  Scenario: Error returned when channel attempts to update SSR that is channel inaccessible
    When I update an SSR "DEPA" that is inaccessible from channel "Digital"
    Then I will receive an error with code 'SVC_100342_2004'

  Scenario: Happy path - update single SSR to customer
    And I add an SSR "VIP,DEAF"
    Then I should see "VIP,DEAF" added
    When I update an SSR "WCHC"
    Then I should see only "WCHC" not the previous ones
