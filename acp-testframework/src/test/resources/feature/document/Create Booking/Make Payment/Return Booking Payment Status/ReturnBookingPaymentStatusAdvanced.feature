Feature: Terminate inactive session

  @manual
  @TeamD
  @Sprint31
  @FCPH-397
  Scenario Outline: Terminate session when the configured time is reached
    Given a <Channel> is used
    And   a session has been created
    When there has been no activity on the session for <TimeOut>
    Then the session should end
    And the open baskets will be removed
    And the customer/agent should be removed from the session
    And the allocated inventory should be deallocated
  Examples:
  | Channel            | TimeOut |
  | AD                 | 20 mins |
  | Digital            | 20 mins |
  | Public API Mobile  | 20 mins |

  @manual
  @TeamD
  @Sprint31
  @FCPH-397
  Scenario Outline: Save basket to customer profile
    Given a <Channel> is used
    And  a customer is logged in
    And a basket is created
    When there has been no activity on the session for <TimeOut>
    Then the open baskets will be removed
    And the customer should be removed from the session
    And the allocated inventory should be deallocated
    And the basket should be saved in the customer profile
  Examples:
  | Channel           | TimeOut |
  | Digital           | 20 mins |
  | Public API Mobile | 20 mins |

  @manual
  @TeamD
  @Sprint31
  @FCPH-397
  Scenario: Save basket to customer profile
    Given a AD channel is used
    And  a agent is logged in
    And a customer is linked to the session
    And a basket is created
    When there has been no activity on the session for 20 mins
    Then the open baskets will be removed
    And agent and customer should be removed from the session
    And the allocated inventory should be deallocated
    And the basket should be saved in the customer profile