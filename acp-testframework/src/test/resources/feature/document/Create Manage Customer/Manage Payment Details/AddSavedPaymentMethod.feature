@Sprint27
@FCPH-7418
Feature: Add a saved payment method to the customer profile

  Scenario Outline: Receive request to save different payment methods as part of commit booking
    Given I am using <channel> channel
    When I do the commit booking as <booking-type> with MultiplePaymentMethod for <payment-type> with <number> different payment <payment-details>
    Then the PaymentInfo entity has been added to the customer
    Examples: Single payment method
      | channel | booking-type      | payment-type | number | payment-details                                   |
      | Digital | STANDARD_CUSTOMER | debit        | 1      | debit-DM-5573471234567898-123-8-2018-Testing card |
    Examples: Multiple payment method
      | channel         | booking-type | payment-type | number | payment-details                                                                                                                                       |
      | PublicApiMobile | BUSINESS     | combination  | 3      | debit-DM-5573471234567898-123-8-2018-Testing card;debit-DM-5573471234567898-123-8-2018-Testing card;debit-DL-4400000000000008-737-8-2018-Testing card |

  Scenario Outline: Error if invalid customer ID
    Given I am using <channel> channel
    When I submit add payment details CREDIT_CARD request as default true for an identified customer
    Then I true get an error SVC_100055_2010
    Examples:
      | channel         |
      | PublicApiMobile |

  Scenario: Error if the customer does not have a logged in session
    Given I am using Digital channel
    When I submit add payment details DEBIT_CARD request as default true for a user not logged in
    Then I true get an error SVC_100055_2011

  Scenario: Error if the request customer does not match the logged in customer
    Given I am using Digital channel
    When I submit add payment details BANK_ACCOUNT request as default true for a not matching logged in user and customer requested
    Then I true get an error SVC_100055_2012

  Scenario Outline: Error if invalid payment method ID
    Given I am using <channel> channel
    When I submit add payment details DEBIT_CARD request as default true with invalid payment method ID
    Then I true get an error SVC_100055_2014
    Examples:
      | channel      |
      | PublicApiB2B |

  Scenario Outline: Error if the channel is not allowed to create saved payments
    Given I am using <channel> channel
    When I submit add payment details CREDIT_CARD valid request as default true
    Then I <shouldOrNot> get an error SVC_100055_2000
    Examples:
      | channel         | shouldOrNot |
      | ADAirport       | true        |
      | PublicApiMobile | false       |
      | PublicApiB2B    | false       |

  Scenario Outline: Receive request to add payment method from within the profile
    Given I am using <channel> channel
    When I submit add payment details CREDIT_CARD valid request as default true
    Then I will receive a successful response
    And the PaymentInfo entity should be stored against the customer profile for each payment method
    Examples:
      | channel |
      | Digital |

  @manual
  Scenario: Receive request to add payment method from within the profile check on audit
    When I submit add payment details CREDIT_CARD valid request as default true
    Then an audit record of the saved should be stored against the customer profile for each payment method

  @manual
  Scenario Outline: Receive request to save different payment methods as part of commit booking check on audit
    When I do the commit booking as <method> payment method
    Then the last date and time of the last payment method used should be stored
    And an audit record of the saved should be stored against the customer profile for each payment method
    Examples: Single payment method
      | method |
      | Single |
    Examples: Multiple payment method
      | method |
      | Multi  |


