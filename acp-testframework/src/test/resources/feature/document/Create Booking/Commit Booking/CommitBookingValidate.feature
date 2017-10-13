Feature: Check errors for non-valid request

  @FCPH-400
  Scenario Outline: Check all validation error response
    Given I am using channel Digital
    When I do the commit booking with parameter <parameter>
    Then an error message is returned for each <error>
    Examples:
      | parameter              | error           |
      | MissingPaymentMethods  | SVC_100022_2002 |
      | PaymentMismatched      | SVC_100022_2020 |
      | MissingPaymentMethod   | SVC_100022_2004 |
      | MissingPaymentCode     | SVC_100022_2005 |
      | MissingPaymentAmount   | SVC_100022_2006 |
      | MissingPaymentCurrency | SVC_100022_2007 |
      | InvalidPaymentMethod   | SVC_100022_2022 |
      | InvalidPaymentCode     | SVC_100022_2024 |
      | InvalidPaymentAmount   | SVC_100022_2019 |

  @FCPH-400 @FCPH-401 @FCPH-402
  Scenario Outline: Error is returned if duplicate booking found
    Given I have a valid booking via <channel>
    When I do the commit booking for the same flight and passengers
    Then an error <errorCode> is returned for duplicate booking
    Examples:
      | channel | errorCode       |
      | Digital | SVC_100022_2021 |

  @manual @FCPH-400
  Scenario: Deallocate the inventory if flight is cancelled for ADAirport
    Given I have a basket with a valid flight via ADAirport
    And the flight operational status is changed to "Cancelled"
    When I do the commit booking
    Then the inventory is deallocated once the flight is cancelled
    And an error message is returned for the cancelled flight

  @manual @FCPH-400
  Scenario: Not deallocate the inventory if flight is cancelled for Digital
    Given I have a basket with a valid flight via "Digital"
    And the flight operational status is changed to "Cancelled"
    When I do the commit booking
    Then the inventory is not deallocated once the flight is cancelled
    And an error message is returned for the cancelled flight

  @Sprint27 @FCPH-8461 @TeamC @Sprint29 @FCPH-9259
  Scenario Outline: Error when amount of payment methods does not equal basket total
    Given I am using channel <channel>
    When I do the commit booking as <booking-type> with MultipleInvalidPaymentMethod for <payment-type> with <number> different payment <payment-details>
    Then an error message is returned for each SVC_100022_2020
    Examples:
      | channel | booking-type      | payment-type | number | payment-details                                                                                     |
      | Digital | STANDARD_CUSTOMER | debit        | 2      | debit-DM-5573471234567898-123-8-2018-Testing card;debit-DL-4400000000000008-737-8-2018-Testing card |

  @Sprint27 @FCPH-8461
  Scenario Outline: Generate a error message when the amount of payment methods does not equal the total balance of the basket
    Given I am using channel <channel>
    When I do the commit booking as <booking-type> with MultipleInvalidPaymentMethod for <payment-type> with <number> different payment <payment-details>
    Then an error message is returned for each SVC_100022_2020
    Examples:
      | channel         | booking-type | payment-type | number | payment-details                                                         |
      | ADAirport       | BUSINESS     | creditfile   | 3      | creditfile-ACTIVE_EUR;creditfile-ACTIVE_GBP;creditfile-ACTIVE_EUR       |
      | PublicApiMobile | BUSINESS     | combination  | 2      | debit-DM-5573471234567898-123-8-2018-Testing card;creditfile-ACTIVE_GBP |

  @Sprint27 @FCPH-8461
  Scenario Outline: Successful fulfilment of all payment methods
    Given I am using channel <channel>
    When I do the commit booking as <booking-type> with MultiplePaymentMethod for <payment-type> with <number> different payment <payment-details>
    Then the booking has been created
#  This can be enabled once we fix the issues related to schema validation FCPH-10488
#    And I validate the json schema for created booking event
  @regression
    Examples:
      | channel | booking-type      | payment-type | number | payment-details                                                                                                                                       |
      | Digital | STANDARD_CUSTOMER | debit        | 3      | debit-DM-5573471234567898-123-8-2018-Testing card;debit-DM-5573471234567898-123-8-2018-Testing card;debit-DL-4400000000000008-737-8-2018-Testing card |
    Examples:
      | channel           | booking-type | payment-type | number | payment-details                                                         |
      | Digital           | BUSINESS     | combination  | 2      | debit-DM-5573471234567898-123-8-2018-Testing card;creditfile-ACTIVE_EUR |
      | ADCustomerService | BUSINESS     | creditfile   | 2      | creditfile-ACTIVE_EUR;creditfile-ACTIVE_GBP                             |
