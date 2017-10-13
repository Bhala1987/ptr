@Sprint27
@FCPH-9160
Feature: Update the default payment method set on a customer profile

  Scenario Outline: Error if invalid customer ID
    Given I am using <channel> channel
    And I submit add payment details DEBIT_CARD valid request as default false
    When I update payment details for an identified customer
    Then I true get an error SVC_100056_2003
    Examples:
      | channel |
      | Digital |

  Scenario: Error if the customer does not have a logged in session
    Given I am using Digital channel
    And I submit add payment details CREDIT_CARD valid request as default false
    When I update payment details for a user not logged in
    Then I true get an error SVC_100056_2004

  Scenario: Error if the request customer does not match the logged in customer
    Given I am using Digital channel
    And I submit add payment details BANK_ACCOUNT valid request as default false
    When I update payment details for a not matching logged in user and customer requested
    Then I true get an error SVC_100056_2005

  Scenario Outline: Error if invalid payment reference ID
    Given I am using <channel> channel
    And I submit add payment details BANK_ACCOUNT valid request as default false
    When I update payment details with invalid payment reference ID
    Then I true get an error SVC_100056_2002
    Examples:
      | channel         |
      | PublicApiMobile |

  Scenario Outline: Error if the AD channel try to create saved payments
    Given I am using <channel> channel
    And I submit add payment details CREDIT_CARD valid request as default false
    Then I <shouldOrNot> get an error SVC_100055_2000
    Examples:
      | channel           | shouldOrNot |
      | ADCustomerService | true        |

  Scenario Outline: Error if the channel is not allowed to update saved payments
    Given I am using <channel> channel
    And I submit add payment details CREDIT_CARD valid request as default false
    When I update payment details
    Then I <shouldOrNot> get an error SVC_100055_2000
    Examples:
      | channel      | shouldOrNot |
      | PublicApiB2B | false       |
      | Digital      | false       |

  @manual
  Scenario Outline: Error setting default to expired card
    When I update payment details <card-type> as a default with expired date as default true
    Then I true get an error SVC_100056_2007
    Examples:
      | card-type   |
      | CREDIT_CARD |
      | DEBIT_CARD  |

  @regression
  Scenario Outline: Receive request to add payment method from within the profile
    Given I am using <channel> channel
    And I submit add payment details DEBIT_CARD valid request as default false
    When I update payment details
    Then I will receive a successful response
    And payment method is now as default one
    Examples:
      | channel         |
      | PublicApiMobile |

  @manual
  Scenario: Receive request to add payment method from within the profile check on audit
    Given I submit add payment details CREDIT_CARD valid request as default false
    When I update payment details as a default
    Then an audit record of the saved should be stored against the customer profile for each payment method