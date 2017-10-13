Feature: Response from the Payment service around funds consumed AD - single payment

  @Sprint26
  @FCPH-8747 @FCPH-398
  Scenario Outline: Send amount to be taken to payment service and receive successful
    Given I am using the '<payment type>'
    And I am using the channel <channel>
    And that I have added a flight "<origin>" to "<destination>" to the basket with currency <currency> from <channel>
    When I do commit booking with <payment type> and <payment details>
    Then I will generate a make payment request to the payment service
    And I receive a successful response from PSP and continue with commit booking process
  @regression
    Examples:
      | origin | destination | channel           | payment type | currency | payment details                             |
      | *LO    | *PA         | PublicApiMobile   | card         | GBP      | MC-5555444433331111-737-8-2018-Testing card |
      | ALC    | LTN         | ADCustomerService | card         | GBP      | VI-4212345678901237-737-8-2018-Testing card |
    Examples:
      | origin | destination | channel           | payment type | currency | payment details                             |
      | ALC    | LTN         | Digital           | elv          | EUR      | tester-1234567890- -12345678- -Barclays     |
      | ALC    | LTN         | PublicApiB2B      | elv          | EUR      | tester-1234567890- -12345678- -Barclays     |
    Examples:
      | origin | destination | channel           | payment type | currency | payment details                             |
      | ALC    | LTN         | ADAirport         | elv          | EUR      | tester-1234567890- -12345678- -Barclays     |
    Examples:
      | origin | destination | channel           | payment type | currency | payment details                             |
      | ALC    | LTN         | ADAirport         | card         | GBP      | MC-5555444433331111-737-8-2018-Testing card |
      | ALC    | LTN         | ADCustomerService | card         | EUR      | CB-4059350000000050-123-8-2018-Testing card |
      | ALC    | LTN         | ADCustomerService | card         | GBP      | TP-100100100100103-737-8-2018-Testing card  |

  @local @FCPH-398
  Scenario Outline: Receive offline accepted staus response from payment service on AD
    Given I am using the '<payment type>'
    Given that I have added a flight "ALC" to "LTN" to the basket with currency <currency> from <channel>
    When I do commit booking with <payment type> and <payment details>
    And I will generate a make payment request when payment service is offline
    When I receive an offline payment accepted response back from the payment service
    Then I will record the payment transaction on the basket
    And I will continue with the commit booking process
    Examples:
      | channel           | payment type | currency | payment details                             |
      | ADCustomerService | card         | GBP      | AX-370000000000002-7373-8-2018-Testing card |

  @local @Sprint26 @FCPH-8747
  Scenario Outline: Receive offline accepted staus response from payment service on Digital and PublicAPI
    Given I am using the '<payment type>'
    And that I have added a flight "ALC" to "LTN" to the basket with currency <currency> from <channel>
    When I do commit booking with <payment type> and <payment details>
    And I will generate a make payment request when payment service is offline
    When I receive an offline payment accepted response back from the payment service
    Then I will record the payment transaction on the basket
    And I will continue with the commit booking process
    Examples:
      | channel         | payment type | currency | payment details                             |
      | Digital         | card         | GBP      | AX-370000000000002-7373-8-2018-Testing card |
      | PublicApiMobile | card         | GBP      | AX-370000000000002-7373-8-2018-Testing card |
      | PublicApiB2B    | elv          | EUR      | tester-1234567890- -12345678- -Barclays     |

  @FCPH-398
  Scenario Outline: Generate an Error message if the payment currency does not match the basket currency on AD
    Given I am using the '<payment type>'
    And that I have added a flight "ALC" to "LTN" to the basket with currency <currency> from <channel>
    When I perform commit booking with <payment type> and <payment details> with <incorrect currency>
    Then I got <error>
    And I will fail the commit Booking Process
    Examples:
      | channel   | payment type | currency | payment details                             | incorrect currency | error           |
      | ADAirport | card         | GBP      | VI-4212345678901237-737-8-2018-Testing card | USD                | SVC_100022_2080 |
      | ADAirport | elv          | EUR      | tester-1234567890- -12345678- -Barclays     | USD                | SVC_100022_2080 |

  @Sprint26 @FCPH-8747
  Scenario Outline: Generate an Error message if the payment currency does not match the basket currency on Digital and PublicAPI
    Given I am using the '<payment type>'
    Given that I have added a flight "ALC" to "LTN" to the basket with currency <currency> from <channel>
    When I perform commit booking with <payment type> and <payment details> with <incorrect currency>
    Then I got <error>
    And I will fail the commit Booking Process
    Examples:
      | channel         | payment type | currency | payment details                             | incorrect currency | error           |
      | Digital         | card         | GBP      | VI-4212345678901237-737-8-2018-Testing card | USD                | SVC_100022_2080 |
      | PublicApiMobile | elv          | EUR      | tester-1234567890- -12345678- -Barclays     | USD                | SVC_100022_2080 |

  @Sprint26 @FCPH-8747 @defcet:FCPH-10380
  Scenario Outline: Failed payment response from payment service for Digital and Public API mobile
    Given I am using the '<payment type>'
    And I am using <channel> channel
    And I am <condition> a staff cus00000001 and logged in as a.rossi@reply.co.uk and 1234
    And I have a flight "LTN" to "ALC" with currency <currency> fare <fare-type> and period <period>
    And I do commit booking request with <payment type> and <payment details> containing invalid <incorrect-info>
    When I receive payment rejected response
    Then I got <error>
    Examples:
      | channel         | currency | condition | fare-type | period | payment type | payment details                             | incorrect-info       | error           |
#      | Digital         | GBP      | false     | Standard  | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidExpiredDate     | SVC_100022_3019 |
      | Digital         | GBP      | false     | Flexi     | future | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidTransactionID | SVC_100022_3042 |
#      | PublicApiMobile | GBP      | false     | Standard  | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidCurrency        | SVC_100022_2080 |
#      | Digital         | GBP      | false     | Flexi     | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidSecurityCode    | SVC_100022_3022 |
      | Digital         | GBP      | false     | Standard  | future | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidAmount        | SVC_100022_2019 |
#      | Digital         | GBP      | false     | Flexi     | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidPaymentCode     | SVC_100022_2024 |
#      | PublicApiMobile | GBP      | false     | Flexi     | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidPaymentMethod   | SVC_100022_2022 |
#      | Digital         | GBP      | false     | Flexi     | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidCardType        | SVC_100022_3018 |
#      | Digital         | EUR      | false     | Standard  | present | elv          | tester-1234567890- -12345678- -Barclays     | FlihtDeparting         | SVC_100022_3014 |
#      | PublicApiMobile | EUR      | false     | Flexi     | present | elv          | tester-1234567890- -12345678- -Barclays     | FlihtDeparting         | SVC_100022_3014 |
#      | PublicApiMobile | EUR      | false     | Standard  | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidTransactionID   | SVC_100022_3042 |
      | PublicApiMobile | EUR      | false     | Flexi     | future | elv          | tester-1234567890- -12345678- -Barclays     | InvalidAccountNumber | SVC_100022_3033 |
#      | PublicApiMobile | EUR      | false     | Flexi     | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidBankCode        | SVC_100022_3034 |
#      | Digital         | EUR      | true      | Staff     | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidPaymentMethod   | SVC_100022_2022 |
#      | Digital         | EUR      | true      | Staff     | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidSecurityCode    | SVC_100022_3022 |
#      | PublicApiMobile | EUR      | false     | Standard  | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidCountryCode     | SVC_100022_3049 |
      | Digital         | EUR      | false     | Flexi     | future | elv          | tester-1234567890- -12345678- -Barclays     | InvalidCountryCode   | SVC_100022_3049 |
#      | Digital         | GBP      | false     | Standard  | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidCardIssueNumber | SVC_100022_3021 |
#      | Digital         | EUR      | false     | Flexi     | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidPlaceholderName | SVC_100022_3032 |
#      | PublicApiMobile | EUR      | false     | Standard  | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidBankName        | SVC_100022_3035 |
      | PublicApiB2B    | GBP      | false     | Standard  | future | card         | DM-5573471234567898-123-8-2018-Testing card | InvalidExpiredDate   | SVC_100022_3019 |
 #     | PublicApiB2B    | GBP      | false     | Flexi     | future | card         | DM-4212345678901237-737-8-2018-Testing card | InvalidTransactionID | SVC_100022_3042 |
#      | PublicApiB2B    | GBP      | false     | Standard  | future  | card         | DM-4212345678901237-737-8-2018-Testing card | InvalidCurrency        | SVC_100022_2080 |
#      | PublicApiB2B    | GBP      | false     | Flexi     | future  | card         | DM-5573471234567898-123-8-2018-Testing card | InvalidSecurityCode    | SVC_100022_3022 |
#      | PublicApiB2B    | GBP      | false     | Standard  | future  | card         | DM-4212345678901237-737-8-2018-Testing card | InvalidAmount          | SVC_100022_2019 |
#      | PublicApiB2B    | GBP      | false     | Flexi     | future  | card         | DM-4212345678901237-737-8-2018-Testing card | InvalidPaymentCode     | SVC_100022_2024 |
#      | PublicApiB2B    | GBP      | false     | Flexi     | future  | card         | DM-4212345678901237-737-8-2018-Testing card | InvalidPaymentMethod   | SVC_100022_2022 |
#      | PublicApiB2B    | GBP      | false     | Flexi     | future  | card         | DM-4212345678901237-737-8-2018-Testing card | InvalidCardType        | SVC_100022_3018 |
#      | PublicApiB2B    | EUR      | false     | Standard  | present | elv          | tester-1234567890- -12345678- -Barclays     | FlihtDeparting         | SVC_100022_3014 |
#      | PublicApiB2B    | EUR      | false     | Flexi     | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidAccountNumber   | SVC_100022_3033 |
#      | PublicApiB2B    | EUR      | false     | Flexi     | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidBankCode        | SVC_100022_3034 |
#      | PublicApiB2B    | GBP      | false     | Standard  | future  | card         | DM-5573471234567898-123-8-2018-Testing card | InvalidCardIssueNumber | SVC_100022_3021 |
#      | PublicApiB2B    | EUR      | false     | Flexi     | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidPlaceholderName | SVC_100022_3032 |
      | PublicApiB2B    | EUR      | false     | Standard  | future | elv          | tester-1234567890- -12345678- -Barclays     | InvalidBankName      | SVC_100022_3035 |

  @Sprint26 @FCPH-9219
  Scenario Outline: Failed payment response from payment service for AD
    Given I am using the '<payment type>'
    And I am using <channel> channel
    And I am <condition> a staff cus00000001 and logged in as a.rossi@reply.co.uk and 1234
    And I have a flight "LTN" to "ALC" with currency <currency> fare <fare-type> and period <period>
    And I do commit booking request with <payment type> and <payment details> containing invalid <incorrect-info>
    When I receive payment rejected response
    Then I got <error>
    Examples:
      | channel           | currency | condition | fare-type | period  | payment type | payment details                             | incorrect-info       | error           |
      | ADCustomerService | EUR      | false     | Standard  | present | elv          | tester-1234567890- -12345678- -Barclays     | FlihtDeparting       | SVC_100022_3014 |
      | ADCustomerService | GBP      | false     | Standard  | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidCurrency      | SVC_100022_2080 |
      | ADAirport         | GBP      | false     | Flexi     | future  | card         | VI-4212345678901237-737-8-2018-Testing card | InvalidPaymentMethod | SVC_100022_2022 |
      | ADAirport         | EUR      | false     | Standard  | future  | elv          | tester-1234567890- -12345678- -Barclays     | InvalidBankName      | SVC_100022_3035 |

  @TeamC @Sprint29 @FCPH-9259 @manual
  Scenario Outline: Refund other payment methods
    Given I am using channel <channel>
    When I do the commit booking STANDARD_CUSTOMER with MultipleInvalidDetailsPaymentMethod for <payment-method> with invalid value for <invalid-payment>
    Then I see the commit booking failure with SVC_100022_3021
    And I see the failure payment transaction for <refund>
    Examples:
      | channel         | payment-method                  | invalid-payment | refund     |
      | Digital         | DebitCard,CreditFile            | CreditFile      | DebitCard  |
      | ADAirport       | DebitCard,Cash                  | Cash            | DebitCard  |
      | PublicApiMobile | ELV,CreditFile                  | ELV             | CreditFile |





