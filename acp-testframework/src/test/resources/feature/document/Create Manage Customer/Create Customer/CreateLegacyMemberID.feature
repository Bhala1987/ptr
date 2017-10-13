@Sprint32
Feature: Integrate with LegacyUserID MemberID

  @TeamE
  @FCPH-11136
  Scenario Outline: 1 - Generate a request to for a legacy user ID for the new customer
    Given I am using channel Digital
    And that the channel has initiated a registerCustomer request with <memberId>
    When the system receive the valid request
    And the request has the memberID
    Then it will generate a request to AL to getLegacyUserID
    Examples:
      | memberId                  |
      | 12345                     |

  @TeamE
  @FCPH-11136
  Scenario: 2 - Send the memberID downstream when a new customer has been created
    Given I am using channel Digital
    When the system create the customer without memberId
    Then it will populate the member ID with the received legacyUser ID
