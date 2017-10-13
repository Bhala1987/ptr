Feature: Make a Payment as part of commit booking

  @Sprint32 @TeamC @FCPH-10067
  Scenario Outline: Send amount to be taken to payment service and receive successful on amendable basket
    Given I am using channel <channel>
    When I commit an amendable basket where flight <origin> to <destination> with <payment type> in <currency> and <card details>
    Then I receive a successful response from PSP and continue with commit booking process
    Examples:
      | origin | destination | channel           | payment type | currency | card details                                |
      | *LO    | *PA         | PublicApiMobile   | card         | GBP      | MC-5555444433331111-737-8-2018-Testing card |
      | ALC    | LTN         | ADCustomerService | card         | GBP      | VI-4212345678901237-737-8-2018-Testing card |
      | ALC    | LTN         | ADAirport         | elv          | EUR      | tester-1234567890- -12345678- -Barclays     |

  @Sprint32 @TeamC @FCPH-10067
  Scenario Outline: Generate a error message when the amount of payment methods does not equal the total balance of the basket
    Given I am using channel <channel>
    When I commit an amendable basket as <booking-type> type with MultipleInvalidPaymentMethod for <number> different <payment-type> with <payment-details>
    Then an error SVC_100022_2020 should returned to the channel
    Examples:
      | channel         | booking-type      | payment-type | number | payment-details                                                                                     |
      | Digital         | STANDARD_CUSTOMER | debit        | 2      | debit-DM-5573471234567898-123-8-2018-Testing card;debit-DL-4400000000000008-737-8-2018-Testing card |
      | ADAirport       | BUSINESS          | creditfile   | 3      | creditfile-ACTIVE_EUR;creditfile-ACTIVE_GBP;creditfile-ACTIVE_EUR                                   |
      | PublicApiMobile | BUSINESS          | combination  | 2      | debit-DM-5573471234567898-123-8-2018-Testing card;creditfile-ACTIVE_GBP                             |

  @Sprint32 @TeamC @FCPH-10067
  Scenario Outline: Successful fulfilment of all payment methods on amendable basket
    Given I am using channel <channel>
    When I commit an amendable basket as <booking-type> type with MultiplePaymentMethod for <number> different <payment-type> with <payment-details>
    Then the booking should created
    Examples:
      | channel           | booking-type      | payment-type | number | payment-details                                                                                                                                       |
      | Digital           | STANDARD_CUSTOMER | debit        | 3      | debit-DM-5573471234567898-123-8-2018-Testing card;debit-DM-5573471234567898-123-8-2018-Testing card;debit-DL-4400000000000008-737-8-2018-Testing card |
      | PublicApiMobile   | BUSINESS          | combination  | 2      | debit-DM-5573471234567898-123-8-2018-Testing card;creditfile-ACTIVE_EUR                                                                               |
      | ADCustomerService | BUSINESS          | creditfile   | 2      | creditfile-ACTIVE_GBP;creditfile-ACTIVE_GBP                                                                                                           |

  @Sprint32 @TeamC @FCPH-10067
  Scenario Outline: Failed payment response from payment service for AD on amendable basket
    Given I am using <channel> channel
    And I am <condition> a staff cus00000001 and logged in as a.rossi@reply.co.uk and 1234
    And I have a booking for flight LTN to ALC with currency <currency> fare <fare-type> and period <period>
    When I commit the amendable basket with <payment type> and <payment details> containing invalid <incorrect-info>
    Then I got <error>
    Examples:
      | channel           | currency | condition | fare-type | period  | payment type | payment details                             | incorrect-info       | error           |
      | ADCustomerService | EUR      | false     | Standard  | present | elv          | tester-1234567890- -12345678- -Barclays     | FlihtDeparting       | SVC_100022_3014 |
      | ADCustomerService | GBP      | false     | Standard  | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidCurrency      | SVC_100022_2080 |
      | ADAirport         | GBP      | false     | Flexi     | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidPaymentMethod | SVC_100022_2022 |
      | ADAirport         | EUR      | false     | Standard  | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidBankName      | SVC_100022_3035 |

  @Sprint32 @TeamC @FCPH-10067 @manual
  Scenario: Store the user ID against the payment transaction
    Given I proceed to commit a booking
    When the payment transaction entry has been created
    Then the user ID of the person requesting the commit booking should be stored against the entry
