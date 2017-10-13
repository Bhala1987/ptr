Feature: Return the list of applicable payment methods

  @FCPH-322
  Scenario Outline: Validation message should not returned for the missing mandatory field
    Given I have a valid basket with passengers and associated customer created via "<channel>"
    When I call the getPaymentMethods service with missing "<parameter>" for "<channel>"
    Then the applicable payment methods are returned for "<channel>"
    Examples:
      | channel           | parameter  |
      | ADCustomerService | CustomerId |
      | PublicApiMobile   | CustomerId |
      | ADAirport         | BasketId   |
      | Digital           | BasketId   |
      | PublicApiB2B      | BasketId   |

  @TeamA @Sprint29 @FCPH-6657 @FCPH-7087 @FCPH-439 @FCPH-316 @FCPH-313
  Scenario Outline: Return the list of applicable payment methods by channel
    And I have a valid basket with passengers and associated customer created via "<channel>"
    When I call the service to retrieve payment methods for "<channel>"
    Then the applicable payment methods are returned for "<channel>"
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |
      | Digital           |
      | PublicApiMobile   |
      | PublicApiB2B      |

  @FCPH-357 @Sprint28
  Scenario Outline: Generate an error message if the customer is not able to be identified
    Given I am using channel Digital
    When retrieve saved payment methods customer not logged In "<username>"
    Then saved payment response return errorcode "<errorCode>"

    Examples:
      | errorCode       | username     |
      | SVC_100055_2010 | cus000000099 |

  @FCPH-357 @Sprint28
  Scenario Outline: Generate a errror message if the customer does not have a logged in session
    Given I am using channel Digital
    When retrieve saved payment methods with valid customer logon "<username>"
    Then saved payment response return errorcode "<errorCode>"

    Examples:
      | errorCode       | username    |
      | SVC_100055_2011 | cus00000001 |


  @FCPH-357 @Sprint28
  Scenario Outline: Generate a error message if the request customer does not match the logged in customer
    Given I am using channel Digital
    And I am logged in as a standard customer
    When retrieve saved payment methods with different customer details "<username>"
    Then saved payment response return errorcode "<errorCode>"

    Examples:
      | errorCode       | username    |
      | SVC_100055_2012 | cus00000001 |

  @FCPH-357 @Sprint28 @regression
  Scenario: Return payment methods to the channel
    Given I am using channel Digital
    And I submit add payment details CREDIT_CARD valid request as default true
    When I retrieve saved payment for the channel
    Then response contains the card details

  @FCPH-357 @Sprint28
  Scenario: Return payment methods to the channel via Customer Profile
    Given I am using channel Digital
    And I am logged in as a standard customer
    And I submit add payment details CREDIT_CARD valid request as default true
    When retrieve saved payment methods from my customer profile
    Then response contains the debitcard details

  @FCPH-357 @Sprint28
  Scenario: Return payment method and indicate that is has expired
    Given I am using channel Digital
    And I am logged in as a standard customer
    And I submit add payment details EXPIRED_CREDIT_CARD valid request as default true
    When I retrieve expired card payment for the channel
    Then response contains the nocard details

  @TeamA @Sprint29 @FCPH-6657 @manual
  Scenario: Set which channel is allowed to use vouchers as a payment method
    Given that I am on the ejPayment types in the back office
    When I set up voucher as a payment method
    Then I set which channels are allowed to use voucher as a payment method