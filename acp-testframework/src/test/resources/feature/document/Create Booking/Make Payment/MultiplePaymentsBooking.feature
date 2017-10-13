Feature: Commit booking with multiple payment methods

  @TeamC
  @Sprint30
  @FCPH-10037
  Scenario Outline: Generate error message if the total payment amounts does not equal the basket amount
    Given I am using <channel> channel
    When I do commit booking for an incorrect amount with passenger mix <passengerMix>, booking <bookingType> and payments with BalanceMultiplePaymentMethod, <number>, <payment-method>, <payment-details>
    Then the channel will receive an error with code SVC_100022_2020
    Examples:
      | channel      | bookingType       | passengerMix          | payment-method | number | payment-details                                                                                      |
      | PublicApiB2B | STANDARD_CUSTOMER | 2 adult               | VI;DL          | 2      | credit-VI-4012888888881881-737-8-2018-Testing card;debit-DL-4400000000000008-737-8-2018-Testing card |
      | Digital      | STANDARD_CUSTOMER | 1 adult, 1 infant OOS | VI;DM          | 2      | credit-VI-4111111111111111-737-8-2018-Testing card;debit-DM-5573471234567898-123-8-2018-Testing card |

  #Need to check the paymentFee attribute value of PaymentTransactionEntry in backoffice
  @TeamC
  @Sprint30
  @FCPH-10037
  @manual
  Scenario: - Store the payment fee a commit Booking request to the paymentFee
    Given that the channel has initiated a commitBooking request
    When the request contains a paymentFeeattribute for a payment
    Then the paymentFee attribute should be stored in PaymentTransactionEntry

  @TeamC
  @Sprint30
  @FCPH-10037
  Scenario Outline: Return the credit card fee at booking level
    Given I am using <channel> channel
    When I do commit booking for passenger mix <passengerMix>, booking <bookingType> and payments with BalanceMultiplePaymentMethod, <number>, <payment-method> and <payment-details>
    Then the booking has been created
    And the credit card fee should be same as sum of payment balance response fee amount
    Examples:
      | channel      | bookingType       | passengerMix     | payment-method | number | payment-details                                                                                                                                  |
      | ADAirport    | STANDARD_CUSTOMER | 1 adult, 1 child | VI;elv;DL      | 3      | credit-VI-4012888888881881-737-8-2018-Testing card;elv-tester-1234567890- -12345678- -Barclays;debit-DL-4400000000000008-737-8-2018-Testing card |
      | PublicApiB2B | STANDARD_CUSTOMER | 1 adult          | VI;DM          | 2      | credit-VI-4111111111111111-737-8-2018-Testing card;debit-DM-5573471234567898-123-8-2018-Testing card                                             |
