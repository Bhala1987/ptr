@FCPH-3694 @Sprint28
Feature: Return the list of refundable payment methods

  Scenario Outline: Return error message if the booking reference is incorrect
    Given that the booking reference in the request can not be identified
    When the <channel> channel initiates a getRefundPaymentMethods request
    Then I will receive an error with code 'SVC_100024_1000'
    Examples:
      | channel |
      | Digital |


  Scenario Outline: Return original payment method to the channel based on payment method already on the booking for card and elv
    Given that I have added a flight "<origin>" to "<destination>" to the basket with currency <currency> from <channel>
    And I do commit booking with <payment type> and <payment details>
    And I will generate a make payment request to the payment service
    And I receive a successful response and the booking reference
    When the <channel> channel initiates a getRefundPaymentMethods request
    Then I will return a list of payment methods only including opm
    Examples:
      | origin | destination | channel   | payment type | currency | payment details                             |
      | ALC    | LTN         | ADAirport | card         | GBP      | MC-5555444433331111-737-8-2018-Testing card |
      | ALC    | LTN         | ADAirport | elv          | EUR      | tester-1234567890- -12345678- -Barclays     |


  Scenario Outline: Return original payment method to the channel based on payment method already on the booking for credit file
    Given I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    And I send a valid commit booking request with credit fund "<fundName>" as payment type
    And I receive a booking confirmation and booking
    When the <channel> channel initiates a getRefundPaymentMethods request
    Then I will return a list of payment methods only including opm
    Examples:
      | channel   | fundName   | bookingType |
      | ADAirport | ACTIVE_GBP | BUSINESS    |


  Scenario Outline: Return cash as a alternate refund option if original payment method included cash
    Given  I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    And I send a commit booking request with cash as the payment type
    And I receive a booking confirmation and booking
    When the <channel> channel initiates a getRefundPaymentMethods request
    Then I will return a list of payment methods including cash
    And I will not return the opm
    And the list of payment methods includes all the configured methods
    Examples:
      | channel | bookingType       |
      | Digital | STANDARD_CUSTOMER |

  @regression
  Scenario Outline: Return original payment method + list of payment methods (inc. Cash) back to channel
    Given I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    And I send a commit booking request with cash and with card and "<Payment Details>" on multiple payment methods
    And I receive a booking confirmation and booking
    When the <channel> channel initiates a getRefundPaymentMethods request
    Then I will return a list of payment methods including opm
    And  I will return a list of payment methods including cash
    And the list of payment methods includes all the configured methods including opm
    Examples:
      | channel | bookingType       | Payment Details                             |
      | Digital | STANDARD_CUSTOMER | DM-5573471234567898-123-8-2018-Testing card |


