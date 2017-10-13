Feature: Request Fulfilment from credit file
  As hybris I will receive a request to pay by credit file fund

  @Sprint25 @Sprint26 @FCPH-261
  Scenario Outline: Generate Error scenarios for credit file payment
    Given I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    When I send a valid commit booking request with credit fund "<fundName>" as payment type
    Then I receive a payment error "<errorCode>"
    Examples:
      | channel   | bookingType       | fundName                | errorCode       |
      | ADAirport | BUSINESS          | ACTIVE_ONE_BOOKTYPE_GBP | SVC_100022_2077 |
      | ADAirport | BUSINESS          | EXPIRED_GBP             | SVC_100022_2078 |
      | ADAirport | BUSINESS          | ZERO_BALANCE_GBP        | SVC_100022_2079 |
      | Digital   | STANDARD_CUSTOMER | ACTIVE_ONE_BOOKTYPE_GBP | SVC_100022_2097 |

  @Sprint25 @Sprint26 @FCPH-261
  Scenario Outline: Spot rate applied where credit file currency is not in the same as basket currency and reduce fund balance
    Given I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    When I send a valid commit booking request with credit fund "<fundName>" as payment type using currency "<currency>"
    Then I will convert the requested amount to the currency of the credit file using the spot rate and reduce the fund balance
    Examples:
      | channel   | fundName   | currency | bookingType |
      | ADAirport | ACTIVE_EUR | GBP      | BUSINESS    |

  @Sprint25 @Sprint26 @FCPH-261
  Scenario Outline: Error generated message when the amount in the payment transaction does not equal the total balance of the basket
    Given I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    When I send a valid commit booking request with credit fund "<fundName>" as payment type with payment value of 1
    Then I receive a payment error "SVC_100022_2020"
    Examples:
      | channel   | fundName   | bookingType       |
      | ADAirport | ACTIVE_GBP | BUSINESS          |
      | Digital   | ACTIVE_GBP | STANDARD_CUSTOMER |

  @TeamC @Sprint32 @FCPH-10067
  Scenario Outline: Credit Fund successfully used to pay for flight
    Given I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    When I send a valid commit booking request with credit fund "<fundName>" as payment type
    Then I receive a booking confirmation and booking
    And I commit again the booking for the amendable basket
    And I receive a booking confirmation and booking
    Examples:
      | channel           | fundName   | bookingType |
      | ADAirport         | ACTIVE_GBP | BUSINESS    |
      | ADCustomerService | ACTIVE_GBP | BUSINESS    |